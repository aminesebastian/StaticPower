package theking530.staticpower.cables.digistore.crafting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;
import theking530.staticpower.cables.attachments.digistore.craftinginterface.DigistoreCraftingInterfaceAttachment;
import theking530.staticpower.cables.attachments.digistore.digistorepatternencoder.DigistorePatternEncoder.RecipeEncodingType;
import theking530.staticpower.cables.digistore.DigistoreCraftingRequest;
import theking530.staticpower.cables.digistore.DigistoreInventorySnapshot;
import theking530.staticpower.cables.digistore.DigistoreNetworkModule;
import theking530.staticpower.cables.digistore.crafting.EncodedDigistorePattern.EncodedIngredient;
import theking530.staticpower.cables.network.CableNetworkManager;

public class DigistoreNetworkCraftingManager {
	public static final int MAX_CRAFTING_STEPS = 50;
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

	/**
	 * Checks to see if the request associated with the provided ID is still being
	 * processed.
	 * 
	 * @param id
	 * @return
	 */
	public boolean isCraftingIdStillProcessint(long id) {
		for (DigistoreCraftingRequest request : craftingRequests) {
			if (request.getId() == id) {
				return true;
			}
		}
		return false;
	}

	public CraftingRequestResponse addCraftingRequest(ItemStack requestedItem, int amount, boolean simulate) {
		// Strip any autocrafting data from the stack if needed.
		ItemStack strippedItem = requestedItem.copy();
		DigistoreInventorySnapshot.stripCraftableTag(strippedItem);

		// Take a snapshot of the module's inventory.
		DigistoreInventorySnapshot snapshot = module.getSimulatedNetworkInventorySnapshot();

		// Allocate all the required steps.
		List<AutoCraftingStep> steps = new ArrayList<AutoCraftingStep>();

		// Add the final step.
		AutoCraftingStep intialStep = new AutoCraftingStep(Ingredient.fromStacks(requestedItem), 0, amount, amount);
		steps.add(intialStep);

		// Get all the required steps.
		int craftableAmount = getPatternTreeForItem(new EncodedIngredient(strippedItem, amount), amount, snapshot, steps);

		// If any are craftable, create a reuquest for that amount.
		if (craftableAmount > 0) {
			// Update the craftable amount.
			intialStep.setAmountRemainingToCraft(craftableAmount);
			intialStep.setTotalRequiredAmount(craftableAmount);

			// If we're simulating, simply return the craftable amount in the wrapper.
			// Otherwise, add the crafting reuquest.
			if (simulate) {
				return new CraftingRequestResponse(-1, craftableAmount);
			} else {
				// Add the steps in reverse to the list. Skip the steps that don't have a
				// pattern.
				Collections.reverse(steps);
				long id = CableNetworkManager.get(module.getNetwork().getWorld()).getAndIncrementCurrentCraftingId();
				DigistoreCraftingRequest request = new DigistoreCraftingRequest(id, steps);
				craftingRequests.add(request);
				return new CraftingRequestResponse(id, craftableAmount);
			}
		}
		return new CraftingRequestResponse(-1, 0);
	}

	protected int getPatternTreeForItem(EncodedIngredient ing, int amount, DigistoreInventorySnapshot snapshot, List<AutoCraftingStep> outSteps) {
		// Just in case someone asks for fewer than 0 items, return false.
		if (amount < 0) {
			return 0;
		}

		// If we surpassed the max number of steps, return false.
		if (outSteps.size() > MAX_CRAFTING_STEPS) {
			return 0;
		}

		// Get all the patterns for this item.
		List<EncodedDigistorePattern> patterns = snapshot.getAllPatternsForIngredient(ing.getIngredient());

		// If we have no patterns for this item, return false.
		if (patterns.size() == 0) {
			patterns.clear();
			return 0;
		}

		// For each of the patterns, check if we can craft it.
		for (EncodedDigistorePattern pattern : patterns) {
			// Create a snapshot for this pattern's testing. We need a new snapshot for each
			// pattern so they don't mess with eachother.
			DigistoreInventorySnapshot patternSnapshot = new DigistoreInventorySnapshot(snapshot);

			// Create a container for the steps for this pattern.
			List<AutoCraftingStep> patternSteps = new ArrayList<AutoCraftingStep>();

			// Allocate a flag to track if this pattern is useable.
			boolean failed = false;

			// Keep track of the max output.
			int maxOutput = amount;

			// Check to see if we have the items required to craft with.
			for (EncodedIngredient requiredItem : pattern.getRequiredItems()) {
				// Calculate the amount of steps required.
				double crafingStepsRequired = (double) maxOutput / (double) pattern.getOutput().getCount();

				// Calculate the required amount.
				int requiredAmount = (int) (requiredItem.getCount() * Math.ceil(crafingStepsRequired));

				// Actually perform the extract and capture the extract amount.
				int extracted = patternSnapshot.extractWithIngredient(requiredItem.getIngredient(), requiredAmount, false);

				// Calculate the missing amount between the required and extracted.
				int missingAmount = requiredAmount - extracted;

				// If we have a missing amount, see if we can auto craft the rest. If we do not
				// have a missing amount, add this as a non-crafting step.
				if (missingAmount > 0) {
					// Allocate a list of steps for the item we're testing.
					List<AutoCraftingStep> steps = new ArrayList<AutoCraftingStep>();

					// Create a step for the item we have to craft.
					AutoCraftingStep currentStep = new AutoCraftingStep(requiredItem.getIngredient(), extracted, missingAmount, requiredAmount);

					// Add the current step here first, but we may later modify the values in this
					// step.
					steps.add(currentStep);

					// Can we auto craft the missing amount? If true, add a step for that. If not,
					// then this is a failed path. Break so that we can try another pattern.
					int craftableAmount = getPatternTreeForItem(requiredItem, missingAmount, patternSnapshot, steps);

					// If we can craft at least 1 of the missing item, lets investigate further. If
					// not, this is failed.
					if (craftableAmount > 0) {
						// If we can craft more than enough, we're good. Just add the steps to the steps
						// for this pattern.
						if (craftableAmount >= missingAmount) {
							patternSteps.addAll(steps);
						} else {
							maxOutput = Math.min(maxOutput, (craftableAmount + extracted) / requiredItem.getCount());
							currentStep.setAmountRemainingToCraft(craftableAmount);
							currentStep.setTotalRequiredAmount(craftableAmount + extracted);
							failed = true;
							break;
						}
					} else {
						if (extracted > requiredItem.getCount()) {
							maxOutput = Math.min(maxOutput, extracted / requiredItem.getCount());
							currentStep.setAmountRemainingToCraft(0);
							currentStep.setTotalRequiredAmount(extracted);
						} else {
							failed = true;
							break;
						}
					}
				} else {
					// If we have all the items, mark this as a crafting step.
					patternSteps.add(new AutoCraftingStep(requiredItem.getIngredient(), requiredAmount, 0, requiredAmount));
				}
			}

			// If we made it this far and have not failed, that means we found a valid
			// craftable recipe. Add the
			// steps and return true.
			if (!failed) {
				outSteps.get(outSteps.size() - 1).setCraftingPattern(pattern);
				outSteps.addAll(patternSteps);
				return maxOutput;
			}
		}
		return 0;
	}

	protected boolean canCraftRequest(DigistoreCraftingRequest request) {
		// Check to see if we have the items required to craft with.
		DigistoreInventorySnapshot snapshot = module.getSimulatedNetworkInventorySnapshot();

		// Get the next step.
		AutoCraftingStep step = request.peekTopStep();

		// If this is a crafting step, make sure we can craft it. If it is not, make
		// sure we still have the required items. If we do not, see if we can resolve
		// this by using ore dictionary or other crafting patterns. We only do this ONCE
		// though.
		if (step.isCraftingStep()) {
			// If this was a machine processing step and the machine items were already
			// supplied, we can skip any further checks.
			if (step.areMachineCraftingItemsAlreadySupplied()) {
				return true;
			}

			// Check to make sure we have all the required items.
			for (EncodedIngredient requiredItem : request.peekTopStep().getCraftingPattern().getRequiredItems()) {
				int simulatedExtract = snapshot.extractWithIngredient(requiredItem.getIngredient(), requiredItem.getCount(), false);
				if (simulatedExtract != requiredItem.getCount()) {
					return false;
				}
			}

			// If this is a machine recipe type, make sure the crafting interface can take
			// the recipe. If there is no crafting interface for this recipe, return false.
			if (step.getCraftingPattern().getRecipeType() == RecipeEncodingType.MACHINE) {
				if (getCraftingInterfaceForIngredient(step.getIngredientToCraft()) == null) {
					return false;
				}
			}

			// Check to see if we have space to insert the crafted item.
			ItemStack remaining = module.insertItem(request.peekTopStep().getCraftingPattern().getOutput().copy(), true);
			return remaining.isEmpty();
		} else {
			// See if we have enough.
			int simulatedExtract = snapshot.extractWithIngredient(step.getIngredientToCraft(), step.getStoredAmount(), true);

			// If we don't have enough for a non-crafting step, see if we can resolve it by
			// putting in a request to craft those missing items.
			if (simulatedExtract != step.getTotalRequiredAmount()) {
				// Only do this ONCE per step though.
				if (!step.isAttemptingResolve()) {
					// See which of the ingredients we can craft.
					for (ItemStack potentialResolver : step.getIngredientToCraft().getMatchingStacks()) {
						// Add the request.
						if (addCraftingRequest(potentialResolver, step.getTotalRequiredAmount() - simulatedExtract, false).getCraftableAmount() > 0) {
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
		// If this is a crafting step, attempt to craft it.
		if (step.isCraftingStep()) {
			// If this is a crafting table step, try to craft the item.
			if (step.getCraftingPattern().getRecipeType() == RecipeEncodingType.CRAFTING_TABLE) {
				// Perform the craft.
				for (EncodedIngredient requiredItem : step.getCraftingPattern().getRequiredItems()) {
					snapshot.extractWithIngredient(requiredItem.getIngredient(), requiredItem.getCount(), false);
				}

				// Insert the crafted item.
				module.insertItem(request.peekTopStep().getCraftingPattern().getOutput().copy(), false);

				// Mark the step as completed.
				step.markCraftingIterationCompleted();

				// If the crafting is completed, mark this step as completed.
				if (step.isCraftingCompleted()) {
					request.completeCurrentStep();
				}
			} else {
				// If we have already supplied the items to the machine, see if we now have
				// enough to continue.
				if (step.areMachineCraftingItemsAlreadySupplied()) {
					// Check if we have enough now.
					int simulatedExtract = snapshot.extractWithIngredient(step.getIngredientToCraft(), step.getTotalRequiredAmount(), true);

					// If we have enough, mark this request as completed.
					if (simulatedExtract == step.getTotalRequiredAmount()) {
						request.completeCurrentStep();
					}
				} else {
					// Get the crafting interface to output the items into.
					CraftingInterfaceWrapper craftingInterface = getCraftingInterfaceForIngredient(step.getIngredientToCraft());

					// If the crafting interface is null, return early.
					if (craftingInterface == null) {
						return false;
					}

					// Capture all the items we need to supply.
					List<ItemStack> itemsToSupply = new ArrayList<ItemStack>();
					for (EncodedIngredient requiredItem : step.getCraftingPattern().getRequiredItems()) {
						snapshot.extractWithIngredient(requiredItem.getIngredient(), requiredItem.getCount() * step.getAmountRemainingToCraft(), itemsToSupply, false);
					}

					// If we were able to supply the items, mark the step as having been supplied.
					if (DigistoreCraftingInterfaceAttachment.addIngredientsToInterface(craftingInterface.getAttachment(), itemsToSupply)) {
						step.markMachineCraftingItemsAlreadySupplied();
					}
				}
			}
		} else {
			// If this is a non crafting step, just mark it as completed.
			request.completeCurrentStep();
		}

		// If the request is done, return true.
		return request.isDone();
	}

	@Nullable
	protected CraftingInterfaceWrapper getCraftingInterfaceForIngredient(Ingredient ingredient) {
		// Iterate through all the crafting interfaces.
		for (CraftingInterfaceWrapper wrapper : module.getCraftingInterfaces()) {
			// If the interface is busy, do nothing.
			if (DigistoreCraftingInterfaceAttachment.isCraftingInterfaceBusy(wrapper.getAttachment())) {
				continue;
			}

			// Check to see if this interface can craft this output item. If so, return it.
			for (EncodedDigistorePattern pattern : DigistoreCraftingInterfaceAttachment.getAllPaternsInInterface(wrapper.getAttachment())) {
				if (ingredient.test(pattern.getOutput())) {
					return wrapper;
				}
			}
		}

		// If we made it this far, return null as we did not find a wrapper.
		return null;
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
