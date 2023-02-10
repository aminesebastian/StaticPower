package theking530.api.fluid;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public interface IStaticPowerFluidHandler extends IFluidHandler {
	public static final int MAX_PRESSURE = 32;
	public static final int PASSIVE_FLOW_PRESSURE = 16;

	public int fill(FluidStack resource, float pressure, FluidAction action);

	public float getHeadPressure();
}
