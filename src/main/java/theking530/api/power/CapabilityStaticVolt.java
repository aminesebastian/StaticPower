package theking530.api.power;

import net.minecraft.nbt.INBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class CapabilityStaticVolt {
	@CapabilityInject(IStaticVoltHandler.class)
	public static Capability<IStaticVoltHandler> STATIC_VOLT_CAPABILITY = null;

	public static void register() {
		CapabilityManager.INSTANCE.register(IStaticVoltHandler.class, new Capability.IStorage<IStaticVoltHandler>() {
			@Override
			public INBT writeNBT(Capability<IStaticVoltHandler> capability, IStaticVoltHandler instance, Direction side) {
				return IntNBT.valueOf(instance.getStoredPower());
			}

			@Override
			public void readNBT(Capability<IStaticVoltHandler> capability, IStaticVoltHandler instance, Direction side, INBT base) {
				if (!(instance instanceof StaticVoltHandler))
					throw new IllegalArgumentException("Can not deserialize to an instance that isn't the default implementation");
				((StaticVoltHandler) instance).storedPower = ((IntNBT) base).getInt();
			}
		}, () -> new StaticVoltHandler(0, 0, 0));
	}
}
