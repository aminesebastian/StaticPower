package theking530.staticpower.energy;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
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
				return instance.serializeNBT();
			}

			@Override
			public void readNBT(Capability<IStaticVoltHandler> capability, IStaticVoltHandler instance, Direction side, INBT base) {
				instance.deserializeNBT((CompoundNBT) base);
			}
		}, () -> new StaticVoltHandler(0, 0));
	}
}
