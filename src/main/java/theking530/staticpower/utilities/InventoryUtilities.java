package theking530.staticpower.utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import theking530.api.utilities.SDMath;

public class InventoryUtilities {
	public static boolean isInventoryEmpty(IItemHandler inv) {
		for (int i = 0; i < inv.getSlots(); i++) {
			if (!inv.getStackInSlot(i).isEmpty()) {
				return false;
			}
		}
		return true;
	}

	public static boolean inventoryContainsItem(ItemStack stack, IItemHandler inv) {
		for (int i = 0; i < inv.getSlots(); i++) {
			ItemStack stackInSlot = inv.getStackInSlot(i);
			if (ItemUtilities.areItemStacksStackable(stack, stackInSlot)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets the index of the first slot containing an item. Return -1 if no slots
	 * are found containing that item.
	 * 
	 * @param stack
	 * @param inv
	 * @return
	 */
	public static int getFirstSlotContainingItem(ItemStack stack, IItemHandler inv) {
		for (int i = 0; i < inv.getSlots(); i++) {
			ItemStack stackInSlot = inv.getStackInSlot(i);
			if (ItemUtilities.areItemStacksStackable(stack, stackInSlot)) {
				return i;
			}
		}
		return -1;
	}

	public static ItemStack getRandomItemStackFromInventory(IItemHandler inv) {
		// Capture all the slots that have items in them.
		List<Integer> valueSlots = new LinkedList<Integer>();
		for (int i = 0; i < inv.getSlots(); i++) {
			if (!inv.getStackInSlot(i).isEmpty()) {
				valueSlots.add(i);
			}
		}

		// If the inventory is empty, return an empty itemstack.
		if (valueSlots.size() == 0) {
			return ItemStack.EMPTY;
		}

		// Return the stack in the slot.
		int targetSlot = valueSlots.get(SDMath.getRandomIntInRange(0, valueSlots.size() - 1));
		return inv.getStackInSlot(targetSlot);
	}

	public static boolean canFullyInsertAllItemsIntoInventory(IItemHandler inv, List<ItemStack> items) {
		// Create a new item handler.
		IItemHandler dupInv = new ItemStackHandler(inv.getSlots());

		// Populate the duplicate inventory with copies of the items from the provided
		// inventory.
		for (int i = 0; i < inv.getSlots(); i++) {
			dupInv.insertItem(i, inv.getStackInSlot(i).copy(), false);
		}

		// Create duplicates of the provided items.
		List<ItemStack> dupItems = new ArrayList<ItemStack>();
		items.forEach(item -> dupItems.add(item.copy()));

		// Attempt to insert the duplicates into the duplicate inventory. If they return
		// is ever not empty, then we couldn't fully insert something. Return false.
		for (ItemStack dupItem : dupItems) {
			ItemStack postInsert = insertItemIntoInventory(dupInv, dupItem, false);
			if (!postInsert.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	public static boolean canFullyInsertAllItemsIntoInventory(IItemHandler inv, ItemStack... items) {
		return canFullyInsertAllItemsIntoInventory(inv, Arrays.asList(items));
	}

	public static boolean canFullyInsertStackIntoSlot(IItemHandler inv, int slot, ItemStack stack) {
		return inv.insertItem(slot, stack, true).isEmpty();
	}

	public static boolean canPartiallyInsertItemIntoSlot(IItemHandler inv, int slot, ItemStack stack) {
		return inv.insertItem(slot, stack, true).getCount() != stack.getCount();
	}

	public static boolean canFullyInsertItemIntoInventory(IItemHandler inv, ItemStack stack) {
		// Preallocate the output.
		ItemStack output = stack.copy();

		// Go through every slot an attempt to insert the item. If we end up with an
		// empty stack before the end, then we have fully inserted the item.
		for (int i = 0; i < inv.getSlots(); i++) {
			output = inv.insertItem(i, output, true);
			if (output.isEmpty()) {
				return true;
			}
		}
		return output.isEmpty();
	}

	public static boolean canPartiallyInsertItemIntoInventory(IItemHandler inv, ItemStack stack) {
		for (int i = 0; i < inv.getSlots(); i++) {
			if (canPartiallyInsertItemIntoSlot(inv, i, stack)) {
				return true;
			}
		}
		return false;
	}

	public static ItemStack insertItemIntoInventory(IItemHandler inv, ItemStack stack, boolean simulate) {
		return insertItemIntoInventory(inv, stack, 0, inv.getSlots() - 1, simulate);
	}

	public static ItemStack insertItemIntoInventory(IItemHandler inv, ItemStack stack, int start, int stop, boolean simulate) {
		// Allocate a copy of the provided stack.
		ItemStack output = stack.copy();

		// Go through each slot and attempt to insert the item if an item that this can
		// stack with already exists in that slot. If we ever end up with
		// an empty item, we have fully inserted, we can return true.
		for (int i = start; i < stop + 1; i++) {
			if (ItemHandlerHelper.canItemStacksStackRelaxed(inv.getStackInSlot(i), output)) {
				output = inv.insertItem(i, output, simulate);
				if (output.isEmpty()) {
					return output;
				}
			}
		}

		// Go through each slot and attempt to insert the item. If we ever end up with
		// an empty item, we have fully inserted, we can return true.
		for (int i = start; i < stop + 1; i++) {
			output = inv.insertItem(i, output, simulate);
			if (output.isEmpty()) {
				return output;
			}
		}
		return output;
	}
}
