package theking530.staticpower.energy;

public interface IStaticVoltHandler {
	/**
	 * Returns the amount of power currently stored in this IStaticVoltHandler
	 * 
	 * @return
	 */
	public int getStoredPower();

	/**
	 * Returns the maximum amount of power that can be stored in this
	 * IStaticVoltHandler
	 * 
	 * @return
	 */
	public int getPowerCapacity();

	/**
	 * Tries to receive the amount of power passed in and returns the total amount
	 * of power accepted.
	 * 
	 * @param amount   = Amount of power to receive
	 * @param simulate = If true, power receive is simulated
	 * @return t= The amount of power accepted by the handler.
	 */
	public int recievePower(int amount, boolean simulate);

	public int drainPower(int amount, boolean simulate);

	public boolean canRecievePower();

	public boolean canDrainPower();

	public int getMaxRecieve();

	public int getMaxDrain();

}