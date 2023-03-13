package theking530.api.energy;

import javax.annotation.Nullable;

public interface IStaticPowerStorage {

	public StaticVoltageRange getInputVoltageRange();

	public double getMaximumPowerInput();

	public boolean canAcceptCurrentType(CurrentType type);

	public StaticPowerVoltage getOutputVoltage();

	public double getMaximumPowerOutput();

	public CurrentType getOutputCurrentType();

	public double getStoredPower();

	public double getCapacity();

	/**
	 * This should return whether or not this power storage can accept power
	 * EXTERNLLY. For example, this should return false on generators like solar
	 * panels, but true on batteries and machines.
	 * 
	 * Mods should still use {@link #addPower(PowerStack, boolean)} to determine if
	 * a power storage can actually accept power. This is purely for cosmetic
	 * purposes. For example, this is used to determine whether or not to draw an
	 * input voltage tooltip in WAILIA/TOP.
	 * 
	 * @return
	 */
	public boolean canAcceptExternalPower();

	/**
	 * This should return whether or not this power storage can provider power
	 * EXTERNLLY. For example, this should return false on machines but true on
	 * solar panels or batteries.
	 * 
	 * Mods should still use {@link #drainPower(double, boolean)} to determine if a
	 * power storage can actually supply power. This is purely for cosmetic
	 * purposes. For example, this is used to determine whether or not to draw an
	 * output voltage tooltip in WAILIA/TOP.
	 * 
	 * @return
	 */
	public boolean canOutputExternalPower();

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

	/**
	 * Return an option energy tracker that will be used when rendering the
	 * electrical overlay into the world.
	 * 
	 * @return
	 */
	@Nullable
	public default IStaticPowerEnergyTracker getEnergyTracker() {
		return null;
	}
}