package theking530.api.energy;

import java.util.function.Supplier;

import theking530.staticcore.utilities.SDMath;
import theking530.staticpower.StaticPowerConfig;

public enum StaticPowerVoltage {
	ZERO("no_voltage", () -> 0.0), LOW("low_voltage", StaticPowerConfig.SERVER.lowVoltage), MEDIUM("medium_voltage", StaticPowerConfig.SERVER.mediumVoltage),
	HIGH("high_voltage", StaticPowerConfig.SERVER.highVoltage), EXTREME("extreme_voltage", StaticPowerConfig.SERVER.extremeVoltage),
	BONKERS("bonkers_voltage", StaticPowerConfig.SERVER.bonkersVoltage);

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
		return upgrade(1);
	}

	public StaticPowerVoltage upgrade(int ratio) {
		int newOrdinal = SDMath.clamp(ordinal() + ratio, 0, StaticPowerVoltage.values().length - 1);
		return StaticPowerVoltage.values()[newOrdinal];
	}

	public StaticPowerVoltage downgrade() {
		return downgrade(1);
	}

	public StaticPowerVoltage downgrade(int ratio) {
		int newOrdinal = SDMath.clamp(ordinal() - ratio, 0, StaticPowerVoltage.values().length - 1);
		return StaticPowerVoltage.values()[newOrdinal];
	}

	public static double adjustPowerLossByVoltage(StaticPowerVoltage voltage, double powerLoss) {
		// Offset by 1 because ordinal 0 is the zero voltage.
		int adjustedOrdinal = Math.max(0, voltage.ordinal() - 1);
		double powerLossFactor = Math.pow(2, adjustedOrdinal);
		return powerLoss / powerLossFactor;
	}

	public static StaticPowerVoltage getVoltageClass(double voltage) {
		voltage = Math.abs(voltage);
		for (StaticPowerVoltage tier : StaticPowerVoltage.values()) {
			if (voltage <= tier.getVoltage()) {
				return tier;
			}
		}
		return StaticPowerVoltage.BONKERS;
	}
}
