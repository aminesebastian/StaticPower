package theking530.staticpower.handlers.crafting.wrappers;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class FormerRecipeWrapper {

	private ItemStack inputItemStack;
	private Item requiredMold;
	private ItemStack outputItemStack;
	
	public FormerRecipeWrapper(ItemStack output, ItemStack input, Item mold) {
		inputItemStack = input;
		requiredMold = mold;
		outputItemStack = output;
	}
	
	public ItemStack getInputItemStack() {
		return inputItemStack;
	}
	public ItemStack getOutputItemStack() {
		return outputItemStack;
	}
	public Item getRequiredMold() {
		return requiredMold;
	}
	public boolean satisfiesRecipe(ItemStack input, Item mold) {
		if(ItemStack.areItemsEqual(input, getInputItemStack()) && input.getCount() >= getInputItemStack().getCount()) {
			if(mold == requiredMold) {
				return true;
			}
		}
		return false;
	}
}
