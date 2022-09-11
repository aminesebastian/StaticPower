package theking530.api.energy;

public enum StaticPowerVoltage {
	VERY_LOW(5, "very_low"), LOW(12, "low"), NORMAL(120, "normal"), MEDIUM(240, "medium"), HIGH(480, "high"), VERY_HIGH(1000, "very_high"), EXTREME(10000, "extreme");

	private String unlocalizedName;
	private double voltage;

	private StaticPowerVoltage(double voltage, String unlocalizedName) {
		this.unlocalizedName = "gui.staticpower.mode." + unlocalizedName;
		this.voltage = voltage;
	}

	public String getUnlocalizedName() {
		return unlocalizedName;
	}

	public double getVoltage() {
		return voltage;
	}
}
