package theking530.staticpower.data.crafting.wrappers;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;

public class FluidInfuserOutputWrapper {
	private final Ingredient inputIngredient;
	private final ItemStack outputItem;
	private final FluidStack requiredFluid;
	
	public FluidInfuserOutputWrapper(Ingredient input, ItemStack output, FluidStack fluid) {
		inputIngredient = input;
		outputItem = output;
		requiredFluid = fluid;
	}
	public Ingredient getInputItemStack() {
		return inputIngredient;
	}
	public ItemStack getOutputItemStack() {
		return outputItem;
	}
	public FluidStack getRequiredFluidStack() {
		return requiredFluid;
	}
}

