package theking530.staticpower.handlers.crafting.wrappers;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class FluidInfuserOutputWrapper {
	private final ItemStack inputItem;
	private final ItemStack outputItem;
	private final FluidStack requiredFluid;
	
	public FluidInfuserOutputWrapper(ItemStack input, ItemStack output, FluidStack fluid) {
		inputItem = input;
		outputItem = output;
		requiredFluid = fluid;
	}
	public ItemStack getInputItemStack() {
		return inputItem;
	}
	public ItemStack getOutputItemStack() {
		return outputItem;
	}
	public FluidStack getRequiredFluidStack() {
		return requiredFluid;
	}
}

