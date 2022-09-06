package theking530.api.energy;

public class StaticPowerEnergyUtilities {
	public static final long mSV_TO_SV = 1000;
	public static final long mSA_TO_SA = 1000;
	public static final long mSP_TO_SP = mSV_TO_SV * mSA_TO_SA;

	public static long convertmSVtoSV(long mSV) {
		return mSV / mSV_TO_SV;
	}

	public static long convertSVtomSV(long SV) {
		return SV * mSV_TO_SV;
	}

	public static long convertmSAtoSA(long mSA) {
		return mSA / mSA_TO_SA;
	}

	public static long convertSAtomSA(long SA) {
		return SA * mSA_TO_SA;
	}

	public static long convertmSPtoSP(long mSP) {
		return mSP / mSP_TO_SP;
	}

	public static long convertSWtomSW(long SP) {
		return SP * mSP_TO_SP;
	}
}
