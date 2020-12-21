package theking530.staticpower.cables.digistore.crafting;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;
import theking530.staticpower.cables.attachments.digistore.craftinginterface.DigistoreCraftingInterfaceAttachment;
import theking530.staticpower.cables.attachments.digistore.patternencoder.DigistorePatternEncoder.RecipeEncodingType;
import theking530.staticpower.cables.digistore.DigistoreInventorySnapshot;
import theking530.staticpower.cables.digistore.DigistoreNetworkModule;
import theking530.staticpower.cables.digistore.crafting.EncodedDigistorePattern.EncodedIngredient;
import theking530.staticpower.cables.digistore.crafting.recipes.CraftingStepsBundle;
import theking530.staticpower.cables.digistore.crafting.recipes.CraftingStepsBundle.CraftingStepsBundleContainer;
import theking530.staticpower.cables.digistore.crafting.recipes.CraftingStepsGenerator;
import theking530.staticpower.cables.digistore.crafting.recipes.RecipeTreeGenerator;
import theking530.staticpower.cables.digistore.crafting.recipes.RecipeTreeGenerator.AutoCraftingTree;
import theking530.staticpower.cables.network.CableNetworkManager;

public class DigistoreNetworkCraftingManager {
	public static final int MAX_CRAFT_QUERY_DEPTH = 128;
	public static final Logger LOGGER = LogManager.getLogger(DigistoreNetworkCraftingManager.class);
	private final DigistoreNetworkModule module;
	private final LinkedHashMap<Long, CraftingRequestResponse> craftingRequests;

	public DigistoreNetworkCraftingManager(DigistoreNetworkModule module) {
		this.module = module;
		craftingRequests = new LinkedHashMap<Long, CraftingRequestResponse>();
	}

	public void processCrafting() {
		if (craftingRequests.isEmpty()) {
			return;
		}

		// craftingRequests.clear();

		// Allocate a list of completed crafts to then remove.
		List<Long> completedCrafts = new LinkedList<Long>();

		// Process at least one crafting request if we can.
		for (CraftingRequestResponse request : craftingRequests.values()) {
			// If we can process the next step, continue.
			if (canCraftRequest(request)) {
				// If this method returns true, that means we processed the LAST step for the
				// reuqest and it is now complete. We can mark it for removal.
				if (craftNextRequestStep(request)) {
					completedCrafts.add(request.getId());
				}
				break;
			}
		}

		// Remove the completed crafts.
		for (Long id : completedCrafts) {
			craftingRequests.remove(id);
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
		return craftingRequests.containsKey(id);
	}

	public CraftingStepsBundleContainer getAllCraftingLists(ItemStack requestedItem, int amount) {
		// Strip any autocrafting data from the stack if needed. Set the count to 1 to
		// so we have an example stack of exactly one.
		ItemStack strippedItem = requestedItem.copy();
		DigistoreInventorySnapshot.stripMetadataTags(strippedItem);
		strippedItem.setCount(1);

		// Take a snapshot of the module's inventory.
		DigistoreInventorySnapshot snapshot = module.getSimulatedNetworkInventorySnapshot();

		// Generate all the crafting trees.
		RecipeTreeGenerator test = new RecipeTreeGenerator();
		List<AutoCraftingTree> trees = test.generateTree(strippedItem, amount, snapshot);

		// Allocate the output.
		List<CraftingStepsBundle> output = new ArrayList<CraftingStepsBundle>();

		// Generate crafting steps for all the trees.
		for (AutoCraftingTree tree : trees) {
			CraftingStepsGenerator generator = new CraftingStepsGenerator();
			List<AutoCraftingStep> steps = generator.generateSteps(tree, new DigistoreInventorySnapshot(snapshot));
			CraftingStepsBundle bundle = new CraftingStepsBundle(steps);
			output.add(bundle);
		}

		// Return the list of bundles.
		return new CraftingStepsBundleContainer(output);
	}

	public @Nullable CraftingRequestResponse addAutomationCraftingRequest(ItemStack requestedItem, int amount) {
		// Get all the possible ways to craft this item.
		CraftingStepsBundleContainer bundleContainer = getAllCraftingLists(requestedItem, amount);

		// If there are none, return null.
		if (bundleContainer.isEmpty()) {
			return null;
		}

		// Go through all of the bundles. Return the first craftable one.
		for (CraftingStepsBundle bundle : bundleContainer.getBundles()) {
			if (bundle.hasCraftableOutput()) {
				return addCraftingRequest(bundle);
			}
		}

		// Return nothing if there is not way to craft it.
		return null;
	}

	public @Nullable CraftingRequestResponse addCraftingRequest(CraftingStepsBundle bundle) {
		if (!bundle.hasCraftableOutput()) {
			return null;
		}
		long id = CableNetworkManager.get(module.getNetwork().getWorld()).getAndIncrementCurrentCraftingId();
		CraftingRequestResponse response = new CraftingRequestResponse(id, bundle.getCraftableAmount(), bundle.getOutput(), bundle);
		craftingRequests.put(response.getId(), response);
		return response;
	}

	public CraftingRequestResponse cancelCraftingRequest(long craftingRequestId) {
		return craftingRequests.remove(craftingRequestId);
	}

	protected boolean canCraftRequest(CraftingRequestResponse request) {
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
			int simulatedExtract = snapshot.extractWithIngredient(step.getIngredientToCraft(), step.getTotalRequiredAmount(), true);

			// Update the current step.
			request.peekTopStep().setStoredAmount(simulatedExtract);
			request.peekTopStep().setAmountRemainingToCraft(step.getTotalRequiredAmount() - simulatedExtract);

			// If we don't have enough for a non-crafting step, see if we can resolve it by
			// putting in a request to craft those missing items.
			if (simulatedExtract != step.getTotalRequiredAmount()) {
				// Only do this ONCE per step though.
				if (!step.isAttemptingResolve()) {
					// See which of the ingredients we can craft.
					for (ItemStack potentialResolver : step.getIngredientToCraft().getMatchingStacks()) {
						// Add the request.
						if (addAutomationCraftingRequest(potentialResolver, step.getTotalRequiredAmount() - simulatedExtract).getCraftableAmount() > 0) {
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

	protected boolean craftNextRequestStep(CraftingRequestResponse request) {
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

					// Update the current step.
					request.peekTopStep().setStoredAmount(simulatedExtract);
					request.peekTopStep().setAmountRemainingToCraft(step.getTotalRequiredAmount() - simulatedExtract);

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

					// Capture all the items we need to supply (simulated).
					List<ItemStack> itemsToSupply = new ArrayList<ItemStack>();
					for (EncodedIngredient requiredItem : step.getCraftingPattern().getRequiredItems()) {
						// Calculate the amount of output items required.
						double outputsRequired = requiredItem.getCount() * step.getAmountRemainingToCraft();
						// Calculate the required inputs.
						int inputsRequried = (int) Math.ceil(outputsRequired / step.getCraftingPattern().getOutput().getCount());
						// Extract the required inputs.
						snapshot.extractWithIngredient(requiredItem.getIngredient(), inputsRequried, itemsToSupply, true);
					}

					// If we were able to supply the items, mark the step as having been supplied
					// and actually extract the required items.
					if (DigistoreCraftingInterfaceAttachment.addIngredientsToInterface(craftingInterface.getAttachment(), itemsToSupply)) {
						// Actually extract the items.
						for (EncodedIngredient requiredItem : step.getCraftingPattern().getRequiredItems()) {
							// Calculate the amount of output items required.
							double outputsRequired = requiredItem.getCount() * step.getAmountRemainingToCraft();
							// Calculate the required inputs.
							int inputsRequried = (int) Math.ceil(outputsRequired / step.getCraftingPattern().getOutput().getCount());
							// Extract the required inputs.
							snapshot.extractWithIngredient(requiredItem.getIngredient(), inputsRequried, false);
						}
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
		otherCraftingManager.craftingRequests.putAll(craftingRequests);
	}

	public ListNBT serializeCraftingQueue() {
		// Serialize the requests to the list.
		ListNBT requestNBTList = new ListNBT();
		craftingRequests.values().forEach(request -> {
			requestNBTList.add(request.serialze());
		});
		return requestNBTList;
	}

	public static List<CraftingRequestResponse> deserializeCraftingQueue(ListNBT queueNbt) {
		List<CraftingRequestResponse> output = new ArrayList<CraftingRequestResponse>();
		queueNbt.forEach(requestTag -> {
			CompoundNBT requestNbtTag = (CompoundNBT) requestTag;
			output.add(CraftingRequestResponse.read(requestNbtTag));
		});
		return output;
	}

	public void readFromNbt(CompoundNBT tag) {
		// Get the request NBT list and add the parcels.
		ListNBT requestNBTList = tag.getList("requests", Constants.NBT.TAG_COMPOUND);
		requestNBTList.forEach(requestTag -> {
			CompoundNBT requestNbtTag = (CompoundNBT) requestTag;
			CraftingRequestResponse request = CraftingRequestResponse.read(requestNbtTag);
			craftingRequests.put(request.getId(), request);
		});
	}

	public CompoundNBT writeToNbt(CompoundNBT tag) {
		// Serialize the requests to the list.
		tag.put("requests", serializeCraftingQueue());

		return tag;
	}

}