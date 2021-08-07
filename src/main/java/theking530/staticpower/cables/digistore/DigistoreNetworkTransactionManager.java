package theking530.staticpower.cables.digistore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.minecraft.item.ItemStack;
import theking530.api.digistore.DigistoreInventory;
import theking530.api.digistore.IDigistoreInventory;
import theking530.staticpower.cables.digistore.DigistoreInventorySnapshot.DigistoreItemCraftableState;
import theking530.staticpower.tileentities.digistorenetwork.digistore.DigistoreStack;
import theking530.staticpower.utilities.ItemUtilities;

public class DigistoreNetworkTransactionManager {
	private final List<IDigistoreInventory> digistores;
	private final DigistoreNetworkModule owningModule;

	public DigistoreNetworkTransactionManager(DigistoreNetworkModule owningModule) {
		this.owningModule = owningModule;
		this.digistores = new ArrayList<IDigistoreInventory>();
	}

	public void updateDigistoreList(List<IDigistoreInventory> digistores) {
		this.digistores.clear();
		this.digistores.addAll(digistores);
	}

	public boolean containsItem(ItemStack stack) {
		for (IDigistoreInventory digistore : digistores) {
			for (int i = 0; i < digistore.getUniqueItemCapacity(); i++) {
				DigistoreStack digiStack = digistore.getDigistoreStack(i);
				if (ItemUtilities.areItemStacksStackable(stack, digiStack.getStoredItem())) {
					return true;
				}
			}
		}
		return false;
	}

	public int getItemCount(ItemStack stack) {
		int count = 0;
		for (IDigistoreInventory digistore : digistores) {
			for (int i = 0; i < digistore.getUniqueItemCapacity(); i++) {
				DigistoreStack digiStack = digistore.getDigistoreStack(i);
				if (ItemUtilities.areItemStacksStackable(stack, digiStack.getStoredItem())) {
					count += digiStack.getCount();
				}
			}
		}
		return count;
	}

	public ItemStack insertItem(ItemStack stack, boolean simulate) {
		// Skip empty items.
		if (stack.isEmpty()) {
			return stack;
		}

		// Create a copy for the output stack.
		ItemStack strippedStack = stack.copy();
		DigistoreInventorySnapshot.stripMetadataTags(strippedStack);

		// Allocate a list of all potential digistores to insert into.
		List<IDigistoreInventory> potentials = new ArrayList<IDigistoreInventory>();

		// Go through each digistore and add them to the potentials list if it can
		// accept the item. Only discard full digistores that do NOT have a void
		// upgrade.
		for (IDigistoreInventory digistore : digistores) {
			ItemStack insertSimulation = digistore.insertItem(strippedStack, true);
			boolean isFull = digistore.getTotalContainedCount() >= digistore.getItemCapacity();
			if (insertSimulation.getCount() != strippedStack.getCount() && (!isFull || (isFull && digistore.shouldVoidExcess()))) {
				potentials.add(digistore);
			}
		}

		// If we found no matches, return early.
		if (potentials.size() == 0) {
			return stack;
		}

		// Sort the digistores so that we start by filling the most full first.
		Collections.sort(potentials, new Comparator<IDigistoreInventory>() {
			public int compare(IDigistoreInventory a, IDigistoreInventory b) {
				return a.getTotalContainedCount() == 0 ? 1 : a.getRemainingStorage(true) - b.getRemainingStorage(true);
			}
		});

		/*
		 * The flow is such in order of priority (higher is first). 1) Non-Empty Mono
		 * Cards 2) Non-Empty Regular Cards. 3) Empty Regular Cards. 4) Empty Mono
		 * Cards.
		 */

		// Start filling momo-digistores that already have the item, break if we finish
		// the fill.
		for (IDigistoreInventory digistore : potentials) {
			// Skip empty digistores first time around.
			if (digistore.getTotalContainedCount() == 0) {
				continue;
			}

			// Skip multi slot digistores the first time around.
			if (digistore.getUniqueItemCapacity() > 1) {
				continue;
			}

			// Update the remaining stack.
			strippedStack = digistore.insertItem(strippedStack, simulate);
			if (strippedStack.isEmpty()) {
				break;
			}
		}

		// Start filling non-empty non-mono digistores, break if we finish the fill.
		if (!strippedStack.isEmpty()) {
			for (IDigistoreInventory digistore : potentials) {
				// Skip empty digistores first time around.
				if (digistore.getTotalContainedCount() == 0) {
					continue;
				}

				// Skip single slot digistores this time.
				if (digistore.getUniqueItemCapacity() == 1) {
					continue;
				}

				// Update the remaining stack.
				strippedStack = digistore.insertItem(strippedStack, simulate);
				if (strippedStack.isEmpty()) {
					break;
				}
			}
		}
		// Start filling empty digistores, break if we finish the fill.
		if (!strippedStack.isEmpty()) {
			for (IDigistoreInventory digistore : potentials) {
				// Skip single slot digistores this time.
				if (digistore.getUniqueItemCapacity() == 1) {
					continue;
				}

				// Update the remaining stack.
				strippedStack = digistore.insertItem(strippedStack, simulate);
				if (strippedStack.isEmpty()) {
					break;
				}
			}
		}

		// The rest get free reign.
		if (!strippedStack.isEmpty()) {
			// Start filling, this time with empty digistores allowed. Break if we finish
			// the fill.
			for (IDigistoreInventory digistore : potentials) {
				// Skip non empty digistores, we hit those already.d
				if (digistore.getTotalContainedCount() > 0) {
					continue;
				}

				strippedStack = digistore.insertItem(strippedStack, simulate);
				if (strippedStack.isEmpty()) {
					break;
				}
			}
		}

		// Return what remains.
		return strippedStack;
	}

	public ItemStack extractItem(ItemStack stack, int count, boolean simulate) {
		// Strip the craftable tag from the item to extract if needed.
		if (DigistoreInventorySnapshot.getCraftableStateOfItem(stack) == DigistoreItemCraftableState.ONLY_CRAFTABLE) {
			return ItemStack.EMPTY;
		}

		ItemStack extractItem = stack.copy();
		DigistoreInventorySnapshot.stripMetadataTags(extractItem);

		// Allocate a list of all potential digistores to insert into.
		List<IDigistoreInventory> potentials = new ArrayList<IDigistoreInventory>();

		// Go through each digistore and add them to the potentials list if it can
		// accept the item.
		for (IDigistoreInventory digistore : digistores) {
			for (int i = 0; i < digistore.getUniqueItemCapacity(); i++) {
				if (!digistore.getDigistoreStack(i).isEmpty()) {
					if (ItemUtilities.areItemStacksStackable(extractItem, digistore.getDigistoreStack(i).getStoredItem())) {
						potentials.add(digistore);
					}
				}
			}

		}

		// If we found no matches, return early.
		if (potentials.size() == 0) {
			return ItemStack.EMPTY;
		}

		// Sort the digistores so that we start by extracting from the emptiest first.
		Collections.sort(potentials, new Comparator<IDigistoreInventory>() {
			public int compare(IDigistoreInventory a, IDigistoreInventory b) {
				return a.getRemainingStorage(false) - b.getRemainingStorage(false);
			}
		});

		// Create a handle to the output.
		ItemStack output = ItemStack.EMPTY;

		// Start extracting, break if we finish the extract.
		for (IDigistoreInventory digistore : potentials) {
			// Extract up to the amount we need.
			ItemStack extracted = digistore.extractItem(extractItem, count - output.getCount(), simulate);

			// If this is our first iteration, set the output stack. Otherwise, just grow
			// it.
			if (output.isEmpty()) {
				output = extracted;
			} else {
				output.grow(extracted.getCount());
			}

			// If we have gathered enough, break;
			if (output.getCount() >= count) {
				break;
			}
		}

		// Return what remains.
		return output;
	}

	/**
	 * This can be used to determine if an item can be inserted into a digistore
	 * network given the already existing items, items on route, and a specific item
	 * we care about. This is mostly useful for predicting inserts for the item
	 * cable network.
	 * 
	 * @param itemsOnRoute Items that are already on route to this digistore
	 *                     network.
	 * @param itemToInsert The item we want to add now that is NOT part of the
	 *                     itemsOnRoute.
	 * @return The remaining amount of the itemToInsert after simulating an insert.
	 */
	public ItemStack simulatePredictedInsert(List<ItemStack> itemsOnRoute, ItemStack itemToInsert) {

		// Make an array of the duplicate digistore inventories.
		List<IDigistoreInventory> duplicates = new ArrayList<IDigistoreInventory>();

		// Loop through all the existing digistore inventories and duplicate them.
		for (IDigistoreInventory original : digistores) {
			// Create a duplicate.
			DigistoreInventory dup = new DigistoreInventory(original.getUniqueItemCapacity(), original.getItemCapacity());

			// Insert duplicate items into the duplicate inventories.
			for (int i = 0; i < original.getUniqueItemCapacity(); i++) {
				DigistoreStack originalStack = original.getDigistoreStack(i);
				ItemStack stackToInsert = new ItemStack(originalStack.getStoredItem().getItem(), originalStack.getCount());
				dup.insertItem(stackToInsert, false);
			}

			// Add the duplicate inventory to the duplicates list.
			duplicates.add(dup);
		}

		// Create a duplicate digistore network transaction manager.
		DigistoreNetworkTransactionManager transcationManager = new DigistoreNetworkTransactionManager(owningModule);
		transcationManager.updateDigistoreList(duplicates);

		// Now insert the already traveling items into the duplicates. IF any of these
		// fail, immediately return.
		for (ItemStack alreadyTravelingItem : itemsOnRoute) {
			ItemStack remaining = transcationManager.insertItem(alreadyTravelingItem, false);
			if (!remaining.isEmpty()) {
				return itemToInsert;
			}
		}

		return transcationManager.insertItem(itemToInsert, false);
	}
}
