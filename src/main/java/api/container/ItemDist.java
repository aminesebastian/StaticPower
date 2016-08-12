package api.container;

import net.minecraft.item.ItemStack;

public class ItemDist {

	public void distributeItem(ItemStack input, ItemStack output1, ItemStack output2, ItemStack output3, ItemStack output4, ItemStack output5) {
		
	}
	public int distributedSize(ItemStack itemStack1, ItemStack itemStack2) {
		if(itemStack1 != null && itemStack2 != null) {
			if(itemStack1.isItemEqual(itemStack2)) {
				int i = itemStack1.stackSize;
				int j = itemStack2.stackSize;
				return i+j;
			}
		}
		return 0;
	}
	public static boolean areSlotsAvailable(ItemStack stack, ItemStack slot, int invStackLimit) {
		if (slot == null) {
			return true;
		}	
		if(slot.isItemEqual(stack) && (!stack.getHasSubtypes() || stack.getItemDamage() == slot.getItemDamage()) && ItemStack.areItemStackTagsEqual(stack, slot)) {
			int i = slot.stackSize + stack.stackSize;
			if(i > stack.stackSize || i > invStackLimit) {
				return false;
			}else{
				return true;
			}
		}
		return false;
	}
}
