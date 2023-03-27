package theking530.api.energy;

import theking530.staticcore.utilities.math.SDMath;

public enum StaticPowerVoltage {
	ZERO("no_voltage", 0.0), LOW("low_voltage", 10.0), MEDIUM("medium_voltage", 100.0), HIGH("high_voltage", 1000.0),
	EXTREME("extreme_voltage", 10000.0), BONKERS("bonkers_voltage", 100000.0);

	private String unlocalizedName;
	private String shortName;
	private Double voltage;

	private StaticPowerVoltage(String unlocalizedName, Double voltage) {
		this.unlocalizedName = "gui.staticcore.voltage.full_name." + unlocalizedName;
		this.shortName = "gui.staticcore.voltage.short_name." + unlocalizedName;
		this.voltage = voltage;
	}

	public String getShortName() {
		return shortName;
	}

	public String getUnlocalizedName() {
		return unlocalizedName;
	}

	public double getValue() {
		return voltage;
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
		int newOrdinal = SDMath.clamp(ordinal() - ratio, 1, StaticPowerVoltage.values().length - 1);
		return StaticPowerVoltage.values()[newOrdinal];
	}

	public static StaticPowerVoltage getVoltageClass(double voltage) {
		voltage = Math.abs(voltage);
		for (StaticPowerVoltage tier : StaticPowerVoltage.values()) {
			if (voltage <= tier.getValue()) {
				return tier;
			}
		}
		return StaticPowerVoltage.BONKERS;
	}
}
