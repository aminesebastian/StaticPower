package theking530.staticpower.data.crafting.wrappers;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;

public class EsotericEnchanterRecipeWrapper {

	private final Ingredient inputIngredient1;
	private final Ingredient inputIngredient2;
	private final Ingredient inputIngredient3;
	private final FluidStack inputFluidStack;
	
	private final ItemStack outputItem;
	
	public EsotericEnchanterRecipeWrapper(ItemStack outputItem, Ingredient input1, Ingredient input2, FluidStack inputFluid) {
		this(outputItem, input1, input2, null, inputFluid);
	}
	public EsotericEnchanterRecipeWrapper(ItemStack outputItem, Ingredient input1, Ingredient input2, Ingredient input3, FluidStack inputFluid) {
		inputIngredient1 = input1;
		inputIngredient2 = input2;
		inputIngredient3 = input3;
		inputFluidStack = inputFluid;
		
		this.outputItem = outputItem;
	}

	public Ingredient getIngredient1() {
		return inputIngredient1;
	}
	public Ingredient getIngredient2() {
		return inputIngredient2;
	}
	public Ingredient getIngredient3() {
		return inputIngredient3;
	}
	public ItemStack getOutputItemStack() {
		return outputItem;
	}
	public FluidStack getInputFluidStack() {
		return inputFluidStack;
	}
}
