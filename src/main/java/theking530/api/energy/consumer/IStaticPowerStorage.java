package theking530.api.energy.consumer;

import theking530.api.energy.StaticPowerEnergyDataTypes.StaticVoltageRange;

public interface IStaticPowerStorage {

	public StaticVoltageRange getInputVoltageRange();

	public double getStoredPower();

	public double getCapacity();

	public double getVoltageOutput();

	/**
	 * Attempts to add power to this storage using the provided voltage. The
	 * provided current is calculated to be the provided power/voltage. We return
	 * how much power is consumed by this storage.
	 * 
	 * @param voltage
	 * @param power
	 * @param simulate
	 * @return
	 */
	public double addPower(double voltage, double power, boolean simulate);

	/**
	 * Uses the power stored within this storage. The voltage it is used that is
	 * equal to the result of {@link #getVoltageOutput()}.
	 * 
	 * @param power
	 * @param simulate
	 * @return
	 */
	public double usePower(double power, boolean simulate);

	public boolean canAcceptPower();

	public boolean doesProvidePower();
}