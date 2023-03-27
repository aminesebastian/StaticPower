package theking530.staticcore.blockentity.components.fluids;

import net.minecraft.core.Direction;
import net.minecraftforge.fluids.FluidStack;

public interface IFluidTankComponentFilter {
	public default boolean shouldExposeTankOnSide(Direction side) {
		return true;
	}

	public default boolean canFill(Direction side, FluidStack fluid) {
		return true;
	}

	public default boolean canDrain(Direction side, FluidStack resource) {
		return true;
	}

	public default boolean canDrain(Direction side, int amount) {
		return true;
	}
}
