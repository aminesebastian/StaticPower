package theking530.staticcore.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import theking530.staticcore.StaticCore;
import theking530.staticcore.network.StaticCoreMessageHandler;

public abstract class StaticPowerGameData {
	private final ResourceLocation id;

	public StaticPowerGameData(ResourceLocation id) {
		this.id = id;
	}

	public abstract void deserialize(CompoundTag tag);

	public abstract CompoundTag serialize(CompoundTag tag);

	public void tick(Level level) {
	}

	public void clientTick() {

	}

	public ResourceLocation getId() {
		return id;
	}

	public void onFirstTimeCreated() {
		StaticCore.LOGGER.debug(String.format("Created GameData with name: %1$s.", getId()));
	}

	public void onSyncedFromServer(CompoundTag tag) {
		StaticCore.LOGGER.debug(String.format("Recieved synchronization data for GameData with name: %1$s.", getId()));
	}

	public void syncToClients() {
		StaticCoreMessageHandler.sendToAllPlayers(StaticCoreMessageHandler.MAIN_PACKET_CHANNEL,
				new StaticPowerGameDataSyncPacket(this));
	}
}
