package theking530.staticpower.cables.digistore.crafting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;
import theking530.staticpower.cables.digistore.DigistoreCraftingRequest;
import theking530.staticpower.cables.digistore.DigistoreInventorySnapshot;
import theking530.staticpower.cables.digistore.DigistoreNetworkModule;
import theking530.staticpower.cables.digistore.crafting.EncodedDigistorePattern.EncodedIngredient;
import theking530.staticpower.cables.network.CableNetworkManager;

public class DigistoreNetworkCraftingManager {
	private final DigistoreNetworkModule module;
	private final List<DigistoreCraftingRequest> craftingRequests;

	public DigistoreNetworkCraftingManager(DigistoreNetworkModule module) {
		this.module = module;
		craftingRequests = new LinkedList<DigistoreCraftingRequest>();
	}

	public void processCrafting() {
		if (craftingRequests.isEmpty()) {
			return;
		}

		// Process at least one crafting request if we can.
		for (int i = 0; i < craftingRequests.size(); i++) {
			DigistoreCraftingRequest request = craftingRequests.get(i);
			// If we can process the next step, continue.
			if (canCraftRequest(request)) {
				// If this method returns true, that means we processed the LAST step for the
				// reuqest and it is now complete. We can remove it from the queue.
				if (craftNextRequestStep(request)) {
					craftingRequests.remove(i);
				}
				break;
			}
		}
	}

	public boolean addCraftingRequest(ItemStack requestedItem, int amount) {
		// Strip any autocrafting data from the stack if needed.
		ItemStack strippedItem = requestedItem.copy();
		DigistoreInventorySnapshot.stripCraftableTag(strippedItem);

		// Take a snapshot of the module's inventory.
		DigistoreInventorySnapshot snapshot = module.getSimulatedNetworkInventorySnapshot();

		// Allocate all the required steps.
		List<AutoCraftingStep> steps = new ArrayList<AutoCraftingStep>();

		// Add the final step.
		steps.add(new AutoCraftingStep(Ingredient.fromItems(requestedItem.getItem()), 0, amount, amount));

		// Get all the required steps.
		if (getPatternTreeForItem(new EncodedIngredient(strippedItem, amount), amount, snapshot, steps)) {
			// Add the steps in reverse to the list. Skip the steps that don't have a
			// pattern.
			Collections.reverse(steps);
			DigistoreCraftingRequest request = new DigistoreCraftingRequest(CableNetworkManager.get(module.getNetwork().getWorld()).getAndIncrementCurrentCraftingId(), steps);
			craftingRequests.add(request);
			System.out.println("Craftable!");
			return true;
		}
		System.out.println("Not Craftable!");
		return false;
	}

	protected boolean getPatternTreeForItem(EncodedIngredient ing, int amount, DigistoreInventorySnapshot snapshot, List<AutoCraftingStep> outSteps) {
		// Get all the patterns for this item.
		List<EncodedDigistorePattern> patterns = snapshot.getAllPatternsForIngredient(ing.getIngredient());

		// If we have no patterns for this item, return false.
		if (patterns.size() == 0) {
			patterns.clear();
			return false;
		}

		// For each of the patterns, check if we can craft it. If we can, add it to the
		// list and return.
		for (EncodedDigistorePattern pattern : patterns) {
			// Capture the steps for this pattern.
			List<AutoCraftingStep> patternSteps = new ArrayList<AutoCraftingStep>();

			// Check to see if we have the items required to craft with.
			for (EncodedIngredient requiredItem : pattern.getRequiredItems()) {
				// Attempt to extract the required amount.
				List<ItemStack> extractedItems = new ArrayList<ItemStack>();
				int simulatedExtract = snapshot.extractWithIngredient(requiredItem.getIngredient(), requiredItem.getCount() * amount, extractedItems, true);

				// Calculate the missing amount.
				int missingAmount = requiredItem.getCount() - simulatedExtract;

				// If we have a missing amount, see if we can auto craft the rest. If we DO have
				// enough, extract it from the snapshot.
				if (missingAmount > 0) {
					// Can we auto craft the missing amount? If true, extract the amount we CAN
					// extract.
					List<AutoCraftingStep> steps = new ArrayList<AutoCraftingStep>();
					steps.add(new AutoCraftingStep(requiredItem.getIngredient(), simulatedExtract, missingAmount, requiredItem.getCount()));

					if (getPatternTreeForItem(requiredItem, missingAmount, snapshot, steps)) {
						snapshot.extractWithIngredient(requiredItem.getIngredient(), requiredItem.getCount(), extractedItems, true);
						patternSteps.addAll(steps);
					} else {
						return false;
					}
				} else {
					snapshot.extractWithIngredient(requiredItem.getIngredient(), requiredItem.getCount(), extractedItems, true);
					patternSteps.add(new AutoCraftingStep(requiredItem.getIngredient(), requiredItem.getCount(), 0, requiredItem.getCount()));
				}
			}

			// If we made it this far, that means we found a valid craftable recipe. Add the
			// steps and return true.
			outSteps.get(outSteps.size() - 1).setPattern(pattern);
			outSteps.addAll(patternSteps);
			return true;
		}

		return false;
	}

	protected boolean canCraftRequest(DigistoreCraftingRequest request) {
		// Check to see if we have the items required to craft with.
		DigistoreInventorySnapshot snapshot = module.getNonSimulatedNetworkInventorySnapshot();

		// Get the next step.
		AutoCraftingStep step = request.peekTopStep();
		if (step.isCraftingStep()) {
			for (EncodedIngredient requiredItem : request.peekTopStep().getPattern().getRequiredItems()) {
				int simulatedExtract = snapshot.extractWithIngredient(requiredItem.getIngredient(), requiredItem.getCount(), true);
				if (simulatedExtract != requiredItem.getCount()) {
					return false;
				}
			}

			// Check to see if we have space to insert the crafted item.
			ItemStack remaining = module.insertItem(request.peekTopStep().getPattern().getOutputs()[0].copy(), true);
			return remaining.isEmpty();
		} else {
			int simulatedExtract = snapshot.extractWithIngredient(step.getRequiredItem(), step.getOwnedAmount(), true);

			// If we don't have enough for a non-crafting step, see if we can resolve it by
			// putting in a request to craft those missing items.
			if (simulatedExtract != step.getTotalAmount()) {
				// Only do this ONCE per step though.
				if (!step.isAttemptingResolve()) {
					// See which of the ingredients we can craft.
					for (ItemStack potentialResolver : step.getRequiredItem().getMatchingStacks()) {
						// Add the request.
						if (addCraftingRequest(potentialResolver, step.getTotalAmount() - simulatedExtract)) {
							// Mark this step as having been attempted to resolve.
							step.setResolving();
							break;
						}
					}
				}
				return false;
			} else {
				return true;
			}
		}
	}

	protected boolean craftNextRequestStep(DigistoreCraftingRequest request) {
		// Take a snapshot of the module's inventory.
		DigistoreInventorySnapshot snapshot = module.getNonSimulatedNetworkInventorySnapshot();

		// Get the next step.
		AutoCraftingStep step = request.peekTopStep();
		if (step.isCraftingStep()) {
			for (EncodedIngredient requiredItem : request.peekTopStep().getPattern().getRequiredItems()) {
				snapshot.extractWithIngredient(requiredItem.getIngredient(), requiredItem.getCount(), false);
			}
			module.insertItem(request.peekTopStep().getPattern().getOutputs()[0].copy(), false);
		}
		request.completeCurrentStep();
		return request.isDone();
	}

	public void mergeWithOtherManager(DigistoreNetworkCraftingManager otherCraftingManager) {
		craftingRequests.addAll(otherCraftingManager.craftingRequests);
		otherCraftingManager.craftingRequests.clear();
	}

	public void readFromNbt(CompoundNBT tag) {
		// Get the request NBT list and add the parcels.
		ListNBT requestNBTList = tag.getList("requests", Constants.NBT.TAG_COMPOUND);
		craftingRequests.clear();
		requestNBTList.forEach(requestTag -> {
			CompoundNBT requestNbtTag = (CompoundNBT) requestTag;
			craftingRequests.add(DigistoreCraftingRequest.read(requestNbtTag));
		});
	}

	public CompoundNBT writeToNbt(CompoundNBT tag) {
		// Serialize the requests to the list.
		ListNBT requestNBTList = new ListNBT();
		craftingRequests.forEach(request -> {
			requestNBTList.add(request.serializeToNBT());
		});
		tag.put("requests", requestNBTList);

		return tag;
	}

}
