package theking530.staticpower.tileentities.components.energy;

import net.minecraft.core.Direction;
import theking530.api.energy.IStaticPowerStorage;
import theking530.api.energy.StaticPowerEnergyDataTypes.StaticVoltageRange;

public class SidedEnergyProxy implements IStaticPowerStorage {
	private final Direction fromSide;
	private final IStaticPowerStorage owningStorage;

	public SidedEnergyProxy(Direction fromSide, IStaticPowerStorage owningStorage) {
		this.fromSide = fromSide;
		this.owningStorage = owningStorage;
	}

	@Override
	public final StaticVoltageRange getInputVoltageRange() {
		return getInputVoltageRange(fromSide);
	}

	@Override
	public double getMaximumCurrentInput() {
		return getMaximumCurrentInput(fromSide);
	}

	@Override
	public final double getStoredPower() {
		return getStoredPower(fromSide);
	}

	@Override
	public final double getCapacity() {
		return getCapacity(fromSide);
	}

	@Override
	public final double getVoltageOutput() {
		return getVoltageOutput(fromSide);
	}

	@Override
	public final double getMaximumCurrentOutput() {
		return getMaximumCurrentOutput(fromSide);
	}

	@Override
	public final double addPower(double voltage, double power, boolean simulate) {
		return addPower(fromSide, voltage, power, simulate);
	}

	@Override
	public final double drainPower(double power, boolean simulate) {
		return usePower(fromSide, power, simulate);
	}

	@Override
	public boolean canAcceptPower() {
		return canAcceptPower(fromSide);
	}

	@Override
	public boolean doesProvidePower() {
		return canAcceptPower(fromSide);
	}

	/**
	 * Sided Proxy Overrides
	 */

	public StaticVoltageRange getInputVoltageRange(Direction side) {
		return owningStorage.getInputVoltageRange();
	}

	public double getMaximumCurrentInput(Direction side) {
		return owningStorage.getMaximumCurrentInput();
	}

	public double getStoredPower(Direction side) {
		return owningStorage.getStoredPower();
	}

	public double getCapacity(Direction side) {
		return owningStorage.getCapacity();
	}

	public double getVoltageOutput(Direction side) {
		return owningStorage.getVoltageOutput();
	}

	public double getMaximumCurrentOutput(Direction side) {
		return owningStorage.getMaximumCurrentOutput();
	}

	public double usePower(Direction side, double power, boolean simulate) {
		return owningStorage.drainPower(power, simulate);
	}

	public double addPower(Direction side, double power, double current, boolean simulate) {
		return owningStorage.addPower(power, current, simulate);
	}

	public boolean canAcceptPower(Direction side) {
		return owningStorage.canAcceptPower();
	}

	public boolean doesProvidePower(Direction side) {
		return owningStorage.doesProvidePower();
	}
}
