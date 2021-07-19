package theking530.staticpower.entities.player.datacapability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class CapabilityStaticPowerPlayerData {
	@CapabilityInject(IStaticPowerPlayerData.class)
	public static Capability<IStaticPowerPlayerData> PLAYER_CAPABILITY = null;

	public static void register() {
		CapabilityManager.INSTANCE.register(IStaticPowerPlayerData.class, new Capability.IStorage<IStaticPowerPlayerData>() {
			@Override
			public INBT writeNBT(Capability<IStaticPowerPlayerData> capability, IStaticPowerPlayerData instance, Direction side) {
				if (!(instance instanceof StaticPowerPlayerData)) {
					throw new IllegalArgumentException("Cannot serialize from an instance that isn't the default implementation");
				} else {
					return ((StaticPowerPlayerData) instance).serializeNBT();
				}
			}

			@Override
			public void readNBT(Capability<IStaticPowerPlayerData> capability, IStaticPowerPlayerData instance, Direction side, INBT base) {
				if (!(instance instanceof StaticPowerPlayerData)) {
					throw new IllegalArgumentException("Cannot deserialize to an instance that isn't the default implementation");
				} else {
					((StaticPowerPlayerData) instance).deserializeNBT((CompoundNBT) base);
				}
			}
		}, () -> new StaticPowerPlayerData());
	}
}
