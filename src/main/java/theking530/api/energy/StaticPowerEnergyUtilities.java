package theking530.api.energy;

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
}
