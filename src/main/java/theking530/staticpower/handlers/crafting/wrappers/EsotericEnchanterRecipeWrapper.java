package theking530.staticpower.handlers.crafting.wrappers;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;

public class EsotericEnchanterRecipeWrapper {

	private final Ingredient inputIngredient1;
	private final Ingredient inputIngredient2;
	private final FluidStack inputFluidStack;
	
	private final ItemStack outputItem;
	
	public EsotericEnchanterRecipeWrapper(ItemStack outputItem, Ingredient input1, FluidStack inputFluid) {
		this(outputItem, input1, null, inputFluid);
	}
	public EsotericEnchanterRecipeWrapper(ItemStack outputItem, Ingredient input1, Ingredient input2, FluidStack inputFluid) {
		inputIngredient1 = input1;
		inputIngredient2 = input2;
		inputFluidStack = inputFluid;
		
		this.outputItem = outputItem;
	}

	public Ingredient getIngredient1() {
		return inputIngredient1;
	}
	public Ingredient getIngredient2() {
		return inputIngredient2;
	}
	public FluidStack getInputFluidStack() {
		return inputFluidStack;
	}
	public ItemStack getOutputItemStack() {
		return outputItem;
	}
	
	public boolean isSatisfied(ItemStack input1, ItemStack input2, FluidStack inputFluid) {
		if(inputIngredient1 != null && !input1.isEmpty()) {
			if(inputIngredient1.apply(input1)) {
					if(inputIngredient2 != null && !input2.isEmpty()) {
						if(!inputIngredient2.apply(input2)) {
							return false;
						}
						if(inputFluid.isFluidEqual(inputFluidStack) && inputFluid.amount >= inputFluidStack.amount) {
							return true;
						}
					}
				}
			}
		return false;
	}
}
