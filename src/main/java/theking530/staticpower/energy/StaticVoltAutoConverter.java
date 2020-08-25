package theking530.staticpower.energy;

import net.minecraftforge.energy.IEnergyStorage;

public class StaticVoltAutoConverter implements IEnergyStorage, IStaticVoltHandler {
	protected final IStaticVoltHandler staticVoltHandler;

	public StaticVoltAutoConverter(IStaticVoltHandler staticVoltHandler) {
		this.staticVoltHandler = staticVoltHandler;
	}

	/******************************
	 * IStaticVoltHandler Interface
	 ******************************/
	@Override
	public int getStoredPower() {
		return staticVoltHandler.getStoredPower();
	}

	@Override
	public int getCapacity() {
		return staticVoltHandler.getCapacity();
	}

	@Override
	public int receivePower(int power, boolean simulate) {
		return staticVoltHandler.receivePower(power, simulate);
	}

	@Override
	public int drainPower(int power, boolean simulate) {
		return staticVoltHandler.drainPower(power, simulate);
	}

	@Override
	public boolean canRecievePower() {
		return staticVoltHandler.canRecievePower();
	}

	@Override
	public boolean canDrainPower() {
		return staticVoltHandler.canDrainPower();
	}

	/**************************
	 * IEnergyStorage Interface
	 **************************/
	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		return receivePower(maxReceive / IStaticVoltHandler.FE_TO_SV_CONVERSION, simulate) * IStaticVoltHandler.FE_TO_SV_CONVERSION;
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		return drainPower(maxExtract / IStaticVoltHandler.FE_TO_SV_CONVERSION, simulate) * IStaticVoltHandler.FE_TO_SV_CONVERSION;
	}

	@Override
	public int getEnergyStored() {
		return getStoredPower() * IStaticVoltHandler.FE_TO_SV_CONVERSION;
	}

	@Override
	public int getMaxEnergyStored() {
		return getCapacity() * IStaticVoltHandler.FE_TO_SV_CONVERSION;
	}

	@Override
	public boolean canExtract() {
		return canDrainPower();
	}

	@Override
	public boolean canReceive() {
		return canRecievePower();
	}

}
