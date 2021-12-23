package theking530.api.power;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class CapabilityStaticVolt {
	public static final long mSV_TO_SV = 1000;
	private static final long FE_TO_SV_CONVERSION = 10;

	@CapabilityInject(IStaticVoltHandler.class)
	public static Capability<IStaticVoltHandler> STATIC_VOLT_CAPABILITY = null;

	public static void register() {
		CapabilityManager.INSTANCE.register(IStaticVoltHandler.class);
	}

	public static long convertFEtomSV(int FE) {
		return (FE * mSV_TO_SV) / FE_TO_SV_CONVERSION;
	}

	public static int convertmSVtoFE(long SV) {
		return (int) ((SV * CapabilityStaticVolt.FE_TO_SV_CONVERSION) / mSV_TO_SV);
	}

	public static long convertmSVtoSV(long mSV) {
		return mSV / mSV_TO_SV;
	}

	public static long convertSVtomSV(long SV) {
		return SV * mSV_TO_SV;
	}
}
