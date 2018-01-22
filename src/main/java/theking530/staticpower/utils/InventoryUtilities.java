package theking530.staticpower.utils;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class InventoryUtilities {

	public static boolean canInsertItemsIntoInventory(IItemHandler inv, List<ItemStack> items) {
		IItemHandler dupInv = new ItemStackHandler(inv.getSlots());
		List<ItemStack> dupItems = new ArrayList<ItemStack>();
		
		for(int i=0; i<inv.getSlots(); i++) {
			dupInv.insertItem(i, inv.getStackInSlot(i).copy(), false);
		}
		for(int i=0; i<items.size(); i++) {
			dupItems.add(items.get(i).copy());
		}
		
		for(int i=0; i<dupInv.getSlots(); i++) {		
			for(int j=dupItems.size()-1; j>=0; j--) {
				if(dupItems.get(j) == ItemStack.EMPTY) {
					continue;
				}
				dupItems.set(j, dupInv.insertItem(i, dupItems.get(j), false));
			}
		}		
		for(int i=0; i<dupItems.size(); i++) {
			if(dupItems.get(i) != ItemStack.EMPTY) {
				return false;
			}
		}
		return true;
	}
	
	
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
		ItemStack output = stack;
		for(int i=0; i<inv.getSlots(); i++) {
			output = inv.insertItem(i, output, true);
			if(output.getCount() <= 0) {
				break;
			}
		}
		return output.getCount() <= 0;
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
		ItemStack output = stack;
		for(int i=0; i<inv.getSlots(); i++) {
			output = inv.insertItem(i, output, false);
			if(output.getCount() <= 0) {
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
