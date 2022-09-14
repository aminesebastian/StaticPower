package theking530.api.energy.utilities;

import net.minecraft.world.level.Level;
import theking530.api.energy.CurrentType;
import theking530.api.energy.IStaticPowerStorage;
import theking530.api.energy.PowerStack;

public class StaticPowerEnergyUtilities {
	public enum ElectricalExplosionTrigger {
		NONE, OVER_VOLTAGE, INCOMPATIBLE_CURRENT_TYPE
	}

	public static final double FE_TO_SP_CONVERSION = 10;

	public static double convertFEtomSP(int FE) {
		return FE / FE_TO_SP_CONVERSION;
	}

	public static int convertmSPtoFE(double SV) {
		return (int) (SV * FE_TO_SP_CONVERSION);
	}

	public static double getStoredEnergyPercentScaled(IStaticPowerStorage storage, float scale) {
		return (storage.getStoredPower() / storage.getCapacity()) * scale;
	}

	public static double getCurrentFromPower(double power, double voltage) {
		return power / voltage;
	}

	public static double getVoltageFromPower(double power, double current) {
		return power / current;
	}

	public static double getPowerFromVoltageAndCurrent(double voltage, double current) {
		return voltage * current;
	}

	public static double getMaxOutputPower(IStaticPowerStorage storage) {
		return StaticPowerEnergyUtilities.getPowerFromVoltageAndCurrent(storage.getOutputVoltage(), storage.getMaximumPowerOutput());
	}

	/**
	 * Returns the multiplier that should be applied to AC voltages during the
	 * current tick;
	 * 
	 * @param level The current game world.
	 * @return
	 */
	public static double getAlternatingCurrentMultiplier(Level level) {
		return getAlternatingCurrentMultiplierAtGameTime(level.getGameTime());
	}

	/**
	 * Returns the multiplier that should be applied to AC voltages during the
	 * provided game time (usually gotten from {@link Level#getGameTime()})
	 * 
	 * @param gameTime The current game time in ticks.
	 * @return
	 */
	public static double getAlternatingCurrentMultiplierAtGameTime(long gameTime) {
		return getAlternatingCurrentMultiplierAtTick((int) (gameTime % 20));
	}

	/**
	 * Returns the multiplier that should be applied to AC voltages during the
	 * provided tick (in a range of 0-19).
	 * 
	 * @param tick The tick index within a second (0-19).
	 * @return
	 */
	public static double getAlternatingCurrentMultiplierAtTick(int tick) {
		// return Math.sin((tick / 10.0) * Math.PI); // <- this produces a sin wave,
		// realistic but not fun for gameplay.
		return tick >= 10 ? -1 : 1;
	}

	/**
	 * Determines if the provided storage can output the requested amount of power.
	 * This takes into account the storage's current output voltage and it's maximum
	 * current. If this power would exceed the output current at the storage's
	 * voltage, it cannot supply that amount of power.
	 * 
	 * @param storage
	 * @param power
	 * @return
	 */
	public static boolean canSupplyPower(IStaticPowerStorage storage, double power) {
		double requestedCurrent = getCurrentFromPower(power, storage.getOutputVoltage());
		if (requestedCurrent > storage.getMaximumPowerOutput()) {
			return false;
		}
		return power >= storage.getCapacity();
	}

	public static boolean canAcceptPower(IStaticPowerStorage storage, double power) {
		return storage.getStoredPower() + power <= storage.getCapacity();
	}

	public static double transferPower(double power, IStaticPowerStorage source, IStaticPowerStorage destination) {
		PowerStack maxDrain = source.drainPower(power, true);
		double provided = destination.addPower(maxDrain, false);
		source.drainPower(provided, false);
		return provided;
	}

	public static double transferPower(PowerStack power, IStaticPowerStorage source, IStaticPowerStorage destination) {
		double provided = destination.addPower(power, false);
		source.drainPower(provided, false);
		return provided;
	}

	public static ElectricalExplosionTrigger shouldPowerStackTriggerExplosion(PowerStack stack, IStaticPowerStorage storage) {
		// If there is no power, no need to explode.
		if (stack.getPower() == 0) {
			return ElectricalExplosionTrigger.NONE;
		}

		// If the voltage is alternating and we don't accept alternative current, that's
		// bad!
		if (stack.getCurrentType() == CurrentType.ALTERNATING && !storage.canAcceptCurrentType(stack.getCurrentType())) {
			return ElectricalExplosionTrigger.INCOMPATIBLE_CURRENT_TYPE;
		}

		// If the input voltage is over our supported range, that's an overvoltage!
		if (Math.abs(stack.getVoltage()) > storage.getInputVoltageRange().maximumVoltage().getVoltage()) {
			return ElectricalExplosionTrigger.OVER_VOLTAGE;
		}

		return ElectricalExplosionTrigger.NONE;
	}
}
