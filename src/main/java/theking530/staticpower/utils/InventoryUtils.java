package theking530.staticpower.utils;

import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class InventoryUtils {

	public static boolean canInventoryAcceptItems(IInventory inv, List<ItemStack> stacks) {
		int count = 0;
		for(int i=0; i<stacks.size(); i++) {
			if(canInventoryAcceptItem(inv, stacks.get(i))) {
				count++;
			}
		}				
		return stacks.size() == count;
 	}
	public static boolean canInventoryAcceptItem(IInventory inv, ItemStack stack) {
		for(int i=0; i<inv.getSizeInventory(); i++) {
			if(canSlotAcceptItem(inv, stack, i)) {
				return true;
			}
		}
		return false;
 	}
	public static boolean canSlotAcceptItem(IInventory inv, ItemStack stack, int slot) {
		if(inv.getStackInSlot(slot) == null) {
			return true;
		}else{
			if(stack.areItemsEqual(stack, inv.getStackInSlot(slot)) && stack.areItemStackTagsEqual(stack, inv.getStackInSlot(slot))) {
				int stackSize = stack.stackSize + inv.getStackInSlot(slot).stackSize;
				if(stackSize <= stack.getMaxStackSize()) {
					return true;
				}else{
					return false;
				}
			}
		}
		return false;
	}
	public static boolean fullyInsertItem(IInventory inv, ItemStack stack) {
		if(canInventoryAcceptItem(inv, stack)) {
			for(int i=0; i<inv.getSizeInventory(); i++) {
				if(inv.getStackInSlot(i) == null) {
					inv.setInventorySlotContents(i, stack);
					return true;
				}else{
					if(stack.areItemsEqual(stack, inv.getStackInSlot(i)) && stack.areItemStackTagsEqual(stack, inv.getStackInSlot(i))) {
						int stackSize = stack.stackSize + inv.getStackInSlot(i).stackSize;
						if(stackSize <= stack.getMaxStackSize()) {
							stack.stackSize = stackSize;
							inv.setInventorySlotContents(i, stack);
							return true;
						}
					}
				}
			}
		}
		return false;
	}
}
