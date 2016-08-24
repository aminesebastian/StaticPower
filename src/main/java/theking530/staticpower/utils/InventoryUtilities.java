package theking530.staticpower.utils;

import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class InventoryUtilities {

	public static boolean canFullyInsertItemIntoSlot(IItemHandler inv, int slot, ItemStack stack) {
		if(inv.insertItem(slot, stack, true) == null) {
			return true;
		}else{
			return false;
		}
	}
	public static boolean canInsertItemIntoSlot(IItemHandler inv, int slot, ItemStack stack) {
		if(canFullyInsertItemIntoSlot(inv, slot, stack)) {
			return true;
		}else if(inv.insertItem(slot, stack, true).stackSize < stack.stackSize) {
			return true;
		}
		return false;
	}
	public static boolean canFullyInsertItemIntoInventory(IItemHandler inv, ItemStack stack) {
		for(int i=0; i<inv.getSlots(); i++) {
			if(canFullyInsertItemIntoSlot(inv, i, stack)) {
				return true;
			}
		}
		return false;
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
		for(int i=0; i<inv.getSlots(); i++) {
			if(canInsertItemIntoSlot(inv, i, stack)) {
				return inv.insertItem(i, stack, false);
			}
		}
		return stack;
	}
	public static ItemStack insertItemIntoInventory(IItemHandler inv, ItemStack stack, int start, int stop) {
		for(int i=start; i<stop+1; i++) {
			if(canInsertItemIntoSlot(inv, i, stack)) {
				return inv.insertItem(i, stack, false);
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
