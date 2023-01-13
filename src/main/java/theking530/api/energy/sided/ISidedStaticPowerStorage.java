package theking530.api.energy.sided;

import net.minecraft.core.Direction;
import theking530.api.energy.CurrentType;
import theking530.api.energy.IStaticPowerEnergyTracker;
import theking530.api.energy.IStaticPowerStorage;
import theking530.api.energy.PowerStack;
import theking530.api.energy.StaticPowerVoltage;
import theking530.api.energy.StaticVoltageRange;

public interface ISidedStaticPowerStorage extends IStaticPowerStorage {

	public default StaticVoltageRange getInputVoltageRange(Direction side) {
		return getInputVoltageRange();
	}

	public default double getMaximumCurrentInput(Direction side) {
		return getMaximumPowerInput();
	}

	public default boolean canAcceptCurrentType(Direction side, CurrentType type) {
		return canAcceptCurrentType(type);
	}

	public default StaticPowerVoltage getOutputVoltage(Direction side) {
		return getOutputVoltage();
	}

	public default double getMaximumCurrentOutput(Direction side) {
		return getMaximumPowerOutput();
	}

	public default CurrentType getOutputCurrentType(Direction side) {
		return getOutputCurrentType();
	}

	public default double getStoredPower(Direction side) {
		return getStoredPower();
	}

	public default double getCapacity(Direction side) {
		return getCapacity();
	}

	public default boolean canAcceptExternalPower(Direction side) {
		return canAcceptExternalPower();
	}

	public default boolean canOutputExternalPower(Direction side) {
		return canOutputExternalPower();
	}

	public default double addPower(Direction side, PowerStack power, boolean simulate) {
		return addPower(power, simulate);
	}

	public default PowerStack drainPower(Direction side, double power, boolean simulate) {
		return drainPower(power, simulate);
	}

	public default IStaticPowerEnergyTracker getEnergyTracker(Direction side) {
		return getEnergyTracker();
	}
}
