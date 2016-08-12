package theking530.staticpower.handlers.crafting.wrappers;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class FluidInfuserOutputWrapper {
	protected final ItemStack outputItem;
	protected final FluidStack requiredFluid;
	
	public FluidInfuserOutputWrapper(ItemStack output, FluidStack fluid) {
		outputItem = output;
		requiredFluid = fluid;
	}
	
	public ItemStack getOutputItem() {
		return outputItem;
	}

	public FluidStack getRequiredFluid() {
		return requiredFluid;
	}
}

