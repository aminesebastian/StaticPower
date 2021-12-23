package theking530.staticpower.entities.player.datacapability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class StaticPowerPlayerCapabilityProvider implements ICapabilitySerializable<CompoundTag> {
	private final StaticPowerPlayerData playerData;

	public StaticPowerPlayerCapabilityProvider() {
		playerData = new StaticPowerPlayerData();
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == CapabilityStaticPowerPlayerData.PLAYER_CAPABILITY) {
			return LazyOptional.of(() -> playerData).cast();
		}
		return LazyOptional.empty();
	}

	@Override
	public CompoundTag serializeNBT() {
		return playerData.serializeNBT();
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		playerData.deserializeNBT(nbt);
	}
}
