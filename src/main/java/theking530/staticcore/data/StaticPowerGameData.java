package theking530.staticcore.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import theking530.staticpower.StaticPower;
import theking530.staticpower.network.StaticPowerMessageHandler;

public abstract class StaticPowerGameData {
	private final ResourceLocation id;

	public StaticPowerGameData(ResourceLocation id) {
		this.id = id;
	}

	public abstract void loadFromDisk(CompoundTag tag);

	public abstract void deserialize(CompoundTag tag);

	public abstract CompoundTag serialize(CompoundTag tag);

	public void tick(Level level) {
	}

	public ResourceLocation getId() {
		return id;
	}

	public void onFirstTimeCreated() {
		StaticPower.LOGGER.debug(String.format("Created GameData with name: %1$s.", getId()));
	}

	public void onSyncedFromServer(CompoundTag tag) {
		StaticPower.LOGGER.debug(String.format("Recieved synchronization data for GameData with name: %1$s.", getId()));
	}

	public void syncToClients() {
		StaticPowerMessageHandler.sendToAllPlayers(StaticPowerMessageHandler.MAIN_PACKET_CHANNEL, new StaticPowerGameDataSyncPacket(this));
	}
}
