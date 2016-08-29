package theking530.staticpower.handlers.crafting.wrappers;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class FermenterOutputWrapper {

	private final FluidStack FLUIDSTACK;
	private final ItemStack INPUT;
	
	public FermenterOutputWrapper(ItemStack item, FluidStack fluid) {
		FLUIDSTACK = fluid;
		INPUT = item;
	}
	public FluidStack getOutput(){
		return FLUIDSTACK;
	}
	public ItemStack getInput() {
		return INPUT;
	}
}
