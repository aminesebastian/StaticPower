package theking530.staticpower.utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class InventoryUtilities {

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
