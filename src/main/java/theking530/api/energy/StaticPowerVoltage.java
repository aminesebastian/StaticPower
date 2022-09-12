package theking530.api.energy;

import java.util.function.Supplier;

import theking530.staticpower.StaticPowerConfig;

public enum StaticPowerVoltage {
	LOW("low_voltage", StaticPowerConfig.SERVER.lowVoltage), MEDIUM("medium_voltage", StaticPowerConfig.SERVER.mediumVoltage),
	HIGH("high_voltage", StaticPowerConfig.SERVER.highVoltage), VERY_HIGH("very_high_voltage", StaticPowerConfig.SERVER.veryHighVoltage),
	EXTREME("extreme_voltage", StaticPowerConfig.SERVER.extremeVoltage);

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
