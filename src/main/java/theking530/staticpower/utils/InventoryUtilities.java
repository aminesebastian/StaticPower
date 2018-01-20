package theking530.staticpower.utils;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class InventoryUtilities {

	public static boolean canFullyInsertItemIntoSlot(IItemHandler inv, int slot, ItemStack stack) {
		if(inv.insertItem(slot, stack, true) == ItemStack.EMPTY) {
			return true;
		}else{
			return false;
		}
	}
	public static boolean canInsertItemIntoSlot(IItemHandler inv, int slot, ItemStack stack) {
		if(canFullyInsertItemIntoSlot(inv, slot, stack)) {
			return true;
		}else if(inv.insertItem(slot, stack, true).getCount() < stack.getCount()) {
			return true;
		}
		return false;
	}
	public static boolean canFullyInsertItemIntoInventory(IItemHandler inv, ItemStack stack) {
		int leftOver = 1000;
		ItemStack output = stack;
		for(int i=0; i<inv.getSlots(); i++) {
			output = inv.insertItem(i, output, true);
			leftOver = output.getCount();
			if(leftOver <= 0) {
				break;
			}
		}
		return leftOver <= 0;
	}
	public static boolean canInsertItemIntoInventory(IItemHandler inv, ItemStack stack) {
		for(int i=0; i<inv.getSlots(); i++) {
			if(canInsertItemIntoSlot(inv, i, stack)) {
				return true;
			}
		}
		return false;
	}
	public static ItemStack insertItemIntoInventory(IItemHandler inv, ItemStack stack) {
		int leftOver = 1000;
		ItemStack output = stack;
		for(int i=0; i<inv.getSlots(); i++) {
			output = inv.insertItem(i, output, false);
			leftOver = output.getCount();
			if(leftOver <= 0) {
				break;
			}
		}
		return output;
	}
	public static ItemStack insertItemIntoInventory(IItemHandler inv, ItemStack stack, int start, int stop) {
		for(int i=start; i<stop+1; i++) {
			if(canInsertItemIntoSlot(inv, i, stack)) {
				return inv.insertItem(i, stack.copy(), false);
			}
		}
		return stack;
	}
	public static ItemStack fullyInsertItemIntoInventory(IItemHandler inv, ItemStack stack) {
		for(int i=0; i<inv.getSlots(); i++) {
			if(canFullyInsertItemIntoSlot(inv, i, stack)) {
				return inv.insertItem(i, stack, false);
			}
		}
		return stack;
	}
}
