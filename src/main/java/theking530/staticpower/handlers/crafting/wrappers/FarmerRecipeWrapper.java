package theking530.staticpower.handlers.crafting.wrappers;

import net.minecraftforge.fluids.FluidStack;

public class FarmerRecipeWrapper {

	private FluidStack inputFluid;
	private float outputMultiplier;
	
	public FarmerRecipeWrapper(FluidStack input, float mult) {
		inputFluid = input;
		outputMultiplier = mult;
	}
	
	public FluidStack getInputFluid() {
		return inputFluid;
	}
	public float getOutputMultiplier() {
		return outputMultiplier;
	}
	public boolean isSatisfied(FluidStack fluid) {
		return fluid != null && fluid.isFluidEqual(inputFluid);
	}
}
