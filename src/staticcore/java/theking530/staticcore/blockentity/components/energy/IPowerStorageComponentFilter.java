package theking530.staticcore.blockentity.components.energy;

import net.minecraft.core.Direction;
import theking530.api.energy.PowerStack;

public interface IPowerStorageComponentFilter {
	public default boolean shouldExposePowerOnSide(Direction side) {
		return true;
	}

	public default boolean canAddPower(Direction side, PowerStack stack) {
		return true;
	}

	public default boolean canDrainPower(Direction side, double power) {
		return true;
	}
}
