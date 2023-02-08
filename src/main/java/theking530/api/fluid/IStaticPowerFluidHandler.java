package theking530.api.fluid;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public interface IStaticPowerFluidHandler extends IFluidHandler {
	public int fill(FluidStack resource, float pressure, FluidAction action);

	public float getHeadPressure();
}
