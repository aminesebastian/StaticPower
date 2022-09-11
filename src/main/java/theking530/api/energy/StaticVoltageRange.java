package theking530.api.energy;

import net.minecraft.nbt.CompoundTag;
import theking530.staticcore.utilities.SDMath;

public record StaticVoltageRange(double minimumVoltage, double maximumVoltage) {

	public static final StaticVoltageRange ANY_VOLTAGE = new StaticVoltageRange(Double.MIN_VALUE, Double.MAX_VALUE);
	public static final StaticVoltageRange ZERO_VOLTAGE = new StaticVoltageRange(0, 0);

	public boolean isVoltageInRange(double voltage) {
		voltage = Math.abs(voltage);
		return voltage >= minimumVoltage && voltage <= maximumVoltage;
	}

	public double clampVoltageToRange(double voltage) {
		return SDMath.clamp(voltage, minimumVoltage, maximumVoltage);
	}

	public StaticVoltageRange copy() {
		return new StaticVoltageRange(minimumVoltage, maximumVoltage);
	}

	public CompoundTag serializeNBT() {
		CompoundTag output = new CompoundTag();
		output.putDouble("min", minimumVoltage);
		output.putDouble("max", maximumVoltage);
		return output;
	}

	public static StaticVoltageRange deserializeNBT(CompoundTag nbt) {
		return new StaticVoltageRange(nbt.getDouble("min"), nbt.getDouble("max"));
	}
}