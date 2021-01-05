package theking530.api.power;

/**
 * Interface for defining a static volt handler.
 * 
 * @author amine
 *
 */
public interface IStaticVoltHandler {
	/**
	 * Returns the amount of power currently stored in this IStaticVoltHandler
	 * 
	 * @return
	 */
	public long getStoredPower();

	/**
	 * Returns the maximum amount of power that can be stored in this
	 * IStaticVoltHandler (in mSV).
	 * 
	 * @return The capacity of this static volt handler in mSV.
	 */
	public long getCapacity();

	/**
	 * Returns the maximum amount of power that can be received at once by this
	 * static volt handler (in mSV).
	 * 
	 * @return
	 */
	public long getMaxReceive();

	/**
	 * Returns the maximum amount of power that can be drained at once from this
	 * static volt handler (in mSV).
	 * 
	 * @return
	 */
	public long getMaxDrain();

	/**
	 * Attempts to add the provided amount of power to his static volt handler.
	 * Returns the amount of power that was actually added. If simulate is true,
	 * then the addition will only be simulated.
	 * 
	 * @param power
	 * @param simulate
	 * @return
	 */
	public long receivePower(long power, boolean simulate);

	/**
	 * Attempts to drain the provided amount of power from his static volt handler.
	 * Returns the amount of power that was actually drained. If simulate is true,
	 * then the drain will only be simulated.
	 * 
	 * @param power
	 * @param simulate
	 * @return
	 */
	public long drainPower(long power, boolean simulate);

	/**
	 * Indicates whether or not this static volt handler can receive power.This is
	 * useful to indicate to any other mods that need to access this handler whether
	 * it can ever have added to it externally.
	 * 
	 * Example: On a power generator, this should be set to false because external power
	 * usually is not added to a generator.
	 * 
	 * @return
	 */
	public default boolean canRecievePower() {
		return true;
	}

	/**
	 * Indicates whether or not this static volt handler can have power drained from
	 * it. This is useful to indicate to any other mods that need to access this
	 * handler whether it can ever have power drained from it.
	 * 
	 * Example: On most machines, this should be set to false as you usually can't
	 * drain power from a machine using a cable. Another way to think of it is, when
	 * this is true, it indicates that this static volt handler belongs to a power
	 * PROVIDER like a battery.
	 * 
	 * @return
	 */
	public default boolean canBeDrained() {
		return false;
	}

}