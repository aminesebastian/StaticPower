package theking530.api.energy.utilities;

import theking530.api.energy.IStaticPowerStorage;

public class StaticPowerEnergyUtilities {
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

	public static double getCurrentFromPower(double power, double voltage) {
		return power / voltage;
	}

	public static double getVoltageFromPower(double power, double current) {
		return power / current;
	}

	public static double getPowerFromVoltageAndCurrent(double voltage, double current) {
		return voltage * current;
	}

	public static double getMaxOutputPower(IStaticPowerStorage storage) {
		return StaticPowerEnergyUtilities.getPowerFromVoltageAndCurrent(storage.getVoltageOutput(), storage.getMaximumCurrentOutput());
	}

	public static boolean canSupplyPower(IStaticPowerStorage storage, double power) {
		double requestedCurrent = getCurrentFromPower(power, storage.getVoltageOutput());
		if (requestedCurrent > storage.getMaximumCurrentOutput()) {
			return false;
		}
		return power >= storage.getCapacity();
	}

	public static boolean canAcceptPower(IStaticPowerStorage storage, double power) {
		return storage.getStoredPower() + power <= storage.getCapacity();
	}
}
