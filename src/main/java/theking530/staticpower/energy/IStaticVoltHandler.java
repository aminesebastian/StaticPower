package theking530.staticpower.energy;

public interface IStaticVoltHandler {
	/**
	 * Returns the amount of power currently stored in this IStaticVoltHandler
	 * 
	 * @return
	 */
	public int getStoredCharge();

	/**
	 * Returns the maximum amount of power that can be stored in this
	 * IStaticVoltHandler
	 * 
	 * @return
	 */
	public int getMaximumCharge();

	public int recievePower(int voltage, float current, boolean simulate);

	public int drainPower(int charge, boolean simulate);

	public boolean canRecievePower();

	public boolean canDrainPower();

	public int getMaximumInputVoltage();

	public float getMaximumInputCurrent();
}