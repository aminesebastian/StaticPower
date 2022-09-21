package theking530.api.energy;

import java.util.function.Supplier;

import theking530.staticpower.StaticPowerConfig;

public enum StaticPowerVoltage {
	ZERO("no_voltage", () -> 0.0), LOW("low_voltage", StaticPowerConfig.SERVER.lowVoltage), MEDIUM("medium_voltage", StaticPowerConfig.SERVER.mediumVoltage),
	HIGH("high_voltage", StaticPowerConfig.SERVER.highVoltage),
	EXTREME("extreme_voltage", StaticPowerConfig.SERVER.extremeVoltage), BONKERS("bonkers_voltage", StaticPowerConfig.SERVER.bonkersVoltage);

	private static final double POWER_LOSS_ADJUSTMENT_PER_VOLTAGE = 1.0 / (StaticPowerVoltage.values().length - 1);
	private String unlocalizedName;
	private String shortName;
	private Supplier<Double> voltageSupplier;

	private StaticPowerVoltage(String unlocalizedName, Supplier<Double> voltageSupplier) {
		this.unlocalizedName = "gui.staticpower.voltage.full_name." + unlocalizedName;
		this.shortName = "gui.staticpower.voltage.short_name." + unlocalizedName;
		this.voltageSupplier = voltageSupplier;
	}

	public String getShortName() {
		return shortName;
	}

	public String getUnlocalizedName() {
		return unlocalizedName;
	}

	public double getVoltage() {
		return voltageSupplier.get();
	}

	public boolean isLessThan(StaticPowerVoltage otherVoltage) {
		return ordinal() < otherVoltage.ordinal();
	}

	public boolean isLessThanOrEqualTo(StaticPowerVoltage otherVoltage) {
		return ordinal() <= otherVoltage.ordinal();
	}

	public boolean isGreaterThan(StaticPowerVoltage otherVoltage) {
		return ordinal() > otherVoltage.ordinal();
	}

	public boolean isGreaterThanOrEqualTo(StaticPowerVoltage otherVoltage) {
		return ordinal() >= otherVoltage.ordinal();
	}

	public StaticPowerVoltage upgrade() {
		if (ordinal() == StaticPowerVoltage.values().length - 1) {
			return this;
		} else {
			return StaticPowerVoltage.values()[ordinal() + 1];
		}
	}

	public StaticPowerVoltage downgrade() {
		if (this.ordinal() == 0) {
			return this;
		} else {
			return StaticPowerVoltage.values()[ordinal() - 1];
		}
	}

	public static double adjustPowerLossByVoltage(StaticPowerVoltage voltage, double powerLoss) {
		// We do voltage.ordinal() - 1 to adjust for the "ZERO" option.
		if (voltage == StaticPowerVoltage.ZERO) {
			return powerLoss;
		}

		double adjustment = POWER_LOSS_ADJUSTMENT_PER_VOLTAGE * (StaticPowerVoltage.values().length - voltage.ordinal() - 1);
		return powerLoss * adjustment;
	}

	public static StaticPowerVoltage getVoltageClass(double voltage) {
		voltage = Math.abs(voltage);
		for (StaticPowerVoltage tier : StaticPowerVoltage.values()) {
			if (voltage <= tier.getVoltage()) {
				return tier;
			}
		}
		return StaticPowerVoltage.EXTREME;
	}
}
