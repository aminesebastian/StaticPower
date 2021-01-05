package theking530.api.power;

import net.minecraft.nbt.INBT;
import net.minecraft.nbt.LongNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class CapabilityStaticVolt {
	public static final long mSV_TO_SV = 1000;
	private static final long FE_TO_SV_CONVERSION = 100;

	@CapabilityInject(IStaticVoltHandler.class)
	public static Capability<IStaticVoltHandler> STATIC_VOLT_CAPABILITY = null;

	public static void register() {
		CapabilityManager.INSTANCE.register(IStaticVoltHandler.class, new Capability.IStorage<IStaticVoltHandler>() {
			@Override
			public INBT writeNBT(Capability<IStaticVoltHandler> capability, IStaticVoltHandler instance, Direction side) {
				return LongNBT.valueOf(instance.getStoredPower());
			}

			@Override
			public void readNBT(Capability<IStaticVoltHandler> capability, IStaticVoltHandler instance, Direction side, INBT base) {
				if (!(instance instanceof StaticVoltHandler))
					throw new IllegalArgumentException("Can not deserialize to an instance that isn't the default implementation");
				((StaticVoltHandler) instance).storedPower = ((LongNBT) base).getLong();
			}
		}, () -> new StaticVoltHandler(0, 0, 0));
	}

	public static long convertFEtoSV(int FE) {
		return FE * FE_TO_SV_CONVERSION;
	}

	public static int convertSVtoFE(long SV) {
		return (int) ((SV * CapabilityStaticVolt.FE_TO_SV_CONVERSION) / mSV_TO_SV);
	}

	public static long convertmSVtoSV(long mSV) {
		return mSV / mSV_TO_SV;
	}

	public static long convertSVtomSV(long SV) {
		return SV * mSV_TO_SV;
	}
}
