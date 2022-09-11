package theking530.api.energy;

public interface IStaticPowerStorage {

	public StaticVoltageRange getInputVoltageRange();

	public double getMaximumCurrentInput();

	public boolean canAcceptCurrentType(CurrentType type);

	public double getOutputVoltage();

	public double getMaximumCurrentOutput();

	public CurrentType getOutputCurrentType();

	public double getStoredPower();

	public double getCapacity();

	/**
	 * Attempts to add power to this storage using the provided voltage. The
	 * provided current is calculated to be the provided power/voltage. We return
	 * how much power is consumed by this storage.
	 * 
	 * @param power
	 * @return
	 */
	public double addPower(PowerStack power, boolean simulate);

	/**
	 * Uses the power stored within this storage. The voltage it is used that is
	 * equal to the result of {@link #getOutputVoltage()}.
	 * 
	 * @param power
	 * @param simulate
	 * @return
	 */
	public PowerStack drainPower(double power, boolean simulate);
}