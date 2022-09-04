package theking530.api.power;

public class StaticVoltUtilities {
	public static long getCurrentMaximumPowerOutput(IStaticVoltHandler storage) {
		return Math.min(storage.getStoredPower(), storage.getMaxDrain());
	}

	public static long getCurrentMaximumPowerInput(IStaticVoltHandler storage) {
		return Math.min(storage.getStoredPower(), storage.getMaxDrain());
	}

	public static float getStoredEnergyPercentScaled(IStaticVoltHandler storage, float scale) {
		return ((float) storage.getStoredPower() / storage.getCapacity()) * scale;
	}
}
