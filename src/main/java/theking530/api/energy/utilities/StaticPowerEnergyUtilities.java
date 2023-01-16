package theking530.api.energy.utilities;

import java.util.List;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import theking530.api.energy.CurrentType;
import theking530.api.energy.IStaticPowerStorage;
import theking530.api.energy.PowerStack;
import theking530.api.energy.StaticEnergyDamangeSource;
import theking530.api.energy.StaticPowerVoltage;
import theking530.staticpower.StaticPowerConfig;

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

	public static double getCurrentFromPower(double power, StaticPowerVoltage voltage) {
		return power / voltage.getValue();
	}

	public static double getVoltageFromPower(double power, double current) {
		return power / current;
	}

	public static double getPowerFromVoltageAndCurrent(StaticPowerVoltage voltage, double current) {
		return voltage.getValue() * current;
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

	public static ElectricalExplosionTrigger shouldPowerStackTriggerExplosion(PowerStack stack, IStaticPowerStorage storage) {
		// If there is no power, no need to explode.
		if (stack.getPower() == 0) {
			return ElectricalExplosionTrigger.NONE;
		}

		// If the current is alternating and we don't accept alternating current, that's
		// bad!
		if (stack.getCurrentType() == CurrentType.ALTERNATING && !storage.canAcceptCurrentType(stack.getCurrentType())) {
			return ElectricalExplosionTrigger.INCOMPATIBLE_CURRENT_TYPE;
		}

		// If the input voltage is over our supported range, that's an overvoltage!
		if (Math.abs(stack.getVoltage().getValue()) > storage.getInputVoltageRange().maximumVoltage().getValue()) {
			return ElectricalExplosionTrigger.OVER_VOLTAGE;
		}

		return ElectricalExplosionTrigger.NONE;
	}

	public static <T extends Entity> int applyElectricalDamageInArea(Class<T> entityClass, Level level, AABB area, double current, double damageMultiplier) {
		int hitEntities = 0;
		List<T> list = level.getEntitiesOfClass(entityClass, area);
		for (T entity : list) {
			StaticPowerEnergyUtilities.applyElectricalDamage(entity, current, StaticPowerConfig.SERVER.electricalDamageMultiplier.get());
			hitEntities++;
		}
		return hitEntities;
	}

	public static <T extends Entity> boolean applyElectricalDamage(T entity, double current, double damageMultiplier) {
		// Don't try to hurt something that is invulnerable (it will cause a shakey
		// screen for players).
		if (entity.invulnerableTime <= 10) {
			if (entity.hurt(StaticEnergyDamangeSource.get(), (float) (current * damageMultiplier))) {
				entity.getLevel().playSound(null, entity.getOnPos(), SoundEvents.BEE_POLLINATE, SoundSource.HOSTILE, 2.0f, 0.4f);
				return true;
			}
		}
		return false;
	}
}
