package theking530.api.energy;

import net.minecraft.nbt.CompoundTag;
import theking530.staticcore.utilities.SDMath;

public class StaticPowerEnergyDataTypes {
	public static record StaticVoltageRange(double minimumVoltage, double maximumVoltage) {
		public static final StaticVoltageRange ANY_VOLTAGE = new StaticVoltageRange(Double.MIN_VALUE, Double.MAX_VALUE);
		public static final StaticVoltageRange ZERO_VOLTAGE = new StaticVoltageRange(0, 0);

		public boolean isVoltageInRange(double voltage) {
			return voltage >= minimumVoltage && voltage <= maximumVoltage;
		}

		public double clampVoltageToRange(double voltage) {
			return SDMath.clamp(voltage, minimumVoltage, maximumVoltage);
		}

		public CompoundTag serializeNBT() {
			CompoundTag output = new CompoundTag();
			output.putDouble("minimumVoltage", minimumVoltage);
			output.putDouble("maximumVoltage", maximumVoltage);
			return output;
		}

		public static StaticVoltageRange deserializeNBT(CompoundTag nbt) {
			return new StaticVoltageRange(nbt.getDouble("minimumVoltage"), nbt.getDouble("maximumVoltage"));
		}
	}

	public static record StaticCurrentRange(double minimumCurrent, double maximumCurrent) {
		public boolean isCurrentInRange(double current) {
			return current >= minimumCurrent && current <= maximumCurrent;
		}

		public double clampCurrentToRange(double current) {
			return SDMath.clamp(current, minimumCurrent, maximumCurrent);
		}

		public CompoundTag serializeNBT() {
			CompoundTag output = new CompoundTag();
			output.putDouble("minimumCurrent", minimumCurrent);
			output.putDouble("maximumCurrent", maximumCurrent);
			return output;
		}

		public static StaticCurrentRange deserializeNBT(CompoundTag nbt) {
			return new StaticCurrentRange(nbt.getDouble("minimumCurrent"), nbt.getDouble("maximumCurrent"));
		}
	}

	public static record StaticPowerValue(double voltage, double current) {

	}
}
