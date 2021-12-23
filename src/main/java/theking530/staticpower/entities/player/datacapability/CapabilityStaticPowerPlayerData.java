package theking530.staticpower.entities.player.datacapability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class CapabilityStaticPowerPlayerData {
	@CapabilityInject(IStaticPowerPlayerData.class)
	public static Capability<IStaticPowerPlayerData> PLAYER_CAPABILITY = null;

	public static void register() {
		CapabilityManager.INSTANCE.register(IStaticPowerPlayerData.class, new Capability.IStorage<IStaticPowerPlayerData>() {
			@Override
			public Tag writeNBT(Capability<IStaticPowerPlayerData> capability, IStaticPowerPlayerData instance, Direction side) {
				if (!(instance instanceof StaticPowerPlayerData)) {
					throw new IllegalArgumentException("Cannot serialize from an instance that isn't the default implementation");
				} else {
					return ((StaticPowerPlayerData) instance).serializeNBT();
				}
			}

			@Override
			public void readNBT(Capability<IStaticPowerPlayerData> capability, IStaticPowerPlayerData instance, Direction side, Tag base) {
				if (!(instance instanceof StaticPowerPlayerData)) {
					throw new IllegalArgumentException("Cannot deserialize to an instance that isn't the default implementation");
				} else {
					((StaticPowerPlayerData) instance).deserializeNBT((CompoundTag) base);
				}
			}
		}, () -> new StaticPowerPlayerData());
	}
}
