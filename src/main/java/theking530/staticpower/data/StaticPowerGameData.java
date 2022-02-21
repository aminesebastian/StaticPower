package theking530.staticpower.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.util.thread.SidedThreadGroups;
import theking530.staticpower.StaticPower;
import theking530.staticpower.network.StaticPowerMessageHandler;

public abstract class StaticPowerGameData {
	private final ResourceLocation id;

	public StaticPowerGameData(ResourceLocation id) {
		this.id = id;
	}

	public abstract void load(CompoundTag tag);

	public abstract CompoundTag serialize(CompoundTag tag);

	public void tick() {
	}

	public ResourceLocation getId() {
		return id;
	}

	public void onFirstTimeCreated() {
		StaticPower.LOGGER.debug(String.format("Created GameData with name: %1$s.", getId()));
	}

	public void onSyncedFromServer() {
		StaticPower.LOGGER.debug(String.format("Recieved synchronization data for GameData with name: %1$s.", getId()));
	}

	public void syncToClients() {
		if (Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER) {
			StaticPowerMessageHandler.sendToAllPlayers(StaticPowerMessageHandler.MAIN_PACKET_CHANNEL, new StaticPowerGameDataSyncPacket(this));
		} else {
			StaticPower.LOGGER.warn(String.format("Attempted to synchronize data for GameData with name: %1$s to clients while already on the client.", getId()));
		}
	}
}
