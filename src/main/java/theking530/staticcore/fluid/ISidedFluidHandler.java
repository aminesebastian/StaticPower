package theking530.staticcore.fluid;

import net.minecraft.core.Direction;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public interface ISidedFluidHandler extends IFluidHandler {

	public default int getTanks(Direction direction) {
		return getTanks();
	}

	public default FluidStack getFluidInTank(Direction direction, int tank) {
		return getFluidInTank(tank);
	}

	public default int getTankCapacity(Direction direction, int tank) {
		return getTankCapacity(tank);
	}

	public default boolean isFluidValid(Direction direction, int tank, FluidStack stack) {
		return isFluidValid(tank, stack);
	}

	public default int fill(Direction direction, FluidStack resource, FluidAction action) {
		return fill(resource, action);
	}

	public default FluidStack drain(Direction direction, FluidStack resource, FluidAction action) {
		return drain(resource, action);
	}

	public default FluidStack drain(Direction direction, int maxDrain, FluidAction action) {
		return drain(maxDrain, action);
	}
}