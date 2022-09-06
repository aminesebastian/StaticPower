package theking530.staticpower.tileentities.components.energy;

import net.minecraft.core.Direction;
import theking530.api.energy.StaticPowerEnergyDataTypes.StaticVoltageRange;
import theking530.api.energy.consumer.IStaticPowerStorage;

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
	public final double addPower(double voltage, double power, boolean simulate) {
		return addPower(fromSide, voltage, power, simulate);
	}

	@Override
	public final double usePower(double power, boolean simulate) {
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

	public double getStoredPower(Direction side) {
		return owningStorage.getStoredPower();
	}

	public double getCapacity(Direction side) {
		return owningStorage.getCapacity();
	}

	public double getVoltageOutput(Direction side) {
		return owningStorage.getVoltageOutput();
	}

	public double usePower(Direction side, double power, boolean simulate) {
		return owningStorage.usePower(power, simulate);
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
