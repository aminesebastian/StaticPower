package theking530.staticpower.handlers.crafting.wrappers;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class SqueezerOutputWrapper {
	
	protected final ItemStack outputItem;
	protected final FluidStack outputFluid;
	
	public SqueezerOutputWrapper(ItemStack output, FluidStack fluid) {
		outputItem = output;
		outputFluid = fluid;
	}
	
	public ItemStack getOutputItem() {
		return outputItem;
	}

	public FluidStack getOutputFluid() {
		return outputFluid;
	}
}
