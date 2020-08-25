package theking530.staticpower.energy;

public interface IStaticVoltHandler {
	public static final int FE_TO_SV_CONVERSION = 10;

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
	public int getCapacity();

	public int receivePower(int power, boolean simulate);

	public int drainPower(int power, boolean simulate);

	public boolean canRecievePower();

	public boolean canDrainPower();
}