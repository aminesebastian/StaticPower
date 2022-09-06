package theking530.api.volts;

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
	public long getStoredPower() {
		return staticVoltHandler.getStoredPower();
	}

	@Override
	public long getCapacity() {
		return staticVoltHandler.getCapacity();
	}

	@Override
	public long receivePower(long power, boolean simulate) {
		return staticVoltHandler.receivePower(power, simulate);
	}

	@Override
	public long drainPower(long power, boolean simulate) {
		return staticVoltHandler.drainPower(power, simulate);
	}

	@Override
	public boolean canRecievePower() {
		return staticVoltHandler.canRecievePower();
	}

	@Override
	public boolean canBeDrained() {
		return staticVoltHandler.canBeDrained();
	}

	@Override
	public long getMaxReceive() {
		return staticVoltHandler.getMaxReceive();
	}

	@Override
	public long getMaxDrain() {
		return staticVoltHandler.getMaxDrain();
	}

	/**************************
	 * IEnergyStorage Interface
	 **************************/
	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		long recievedPower = receivePower(CapabilityStaticVolt.convertFEtomSV(maxReceive), simulate);
		return CapabilityStaticVolt.convertmSVtoFE(recievedPower);
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		long drainedPower = drainPower(CapabilityStaticVolt.convertFEtomSV(maxExtract), simulate);
		return CapabilityStaticVolt.convertmSVtoFE(drainedPower);
	}

	@Override
	public int getEnergyStored() {
		return CapabilityStaticVolt.convertmSVtoFE(getStoredPower());
	}

	@Override
	public int getMaxEnergyStored() {
		return CapabilityStaticVolt.convertmSVtoFE(getCapacity());
	}

	@Override
	public boolean canExtract() {
		return canBeDrained();
	}

	@Override
	public boolean canReceive() {
		return canRecievePower();
	}
}
