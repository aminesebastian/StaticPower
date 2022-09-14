package theking530.api.energy;

import net.minecraft.nbt.CompoundTag;
import theking530.staticcore.utilities.SDMath;

public record StaticVoltageRange(StaticPowerVoltage minimumVoltage, StaticPowerVoltage maximumVoltage) {

	public static final StaticVoltageRange ANY_VOLTAGE = new StaticVoltageRange(StaticPowerVoltage.LOW, StaticPowerVoltage.EXTREME);
	public static final StaticVoltageRange ZERO_VOLTAGE = new StaticVoltageRange(StaticPowerVoltage.ZERO, StaticPowerVoltage.ZERO);

	public boolean isVoltageInRange(StaticPowerVoltage voltage) {
		return voltage.isGreaterThanOrEqualTo(minimumVoltage) && voltage.isLessThanOrEqualTo(maximumVoltage);
	}

	public boolean isVoltageInRange(double voltage) {
		voltage = Math.abs(voltage);
		return voltage >= minimumVoltage.getVoltage() && voltage <= maximumVoltage.getVoltage();
	}

	public double clampVoltageToRange(double voltage) {
		return SDMath.clamp(voltage, minimumVoltage.getVoltage(), maximumVoltage.getVoltage());
	}

	public StaticVoltageRange copy() {
		return new StaticVoltageRange(minimumVoltage, maximumVoltage);
	}

	public CompoundTag serializeNBT() {
		CompoundTag output = new CompoundTag();
		output.putByteArray("range", new byte[] { (byte) minimumVoltage.ordinal(), (byte) maximumVoltage.ordinal() });
		return output;
	}

	public static StaticVoltageRange deserializeNBT(CompoundTag nbt) {
		byte[] range = nbt.getByteArray("range");
		return new StaticVoltageRange(StaticPowerVoltage.values()[range[0]], StaticPowerVoltage.values()[range[1]]);
	}
}