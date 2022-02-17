package theking530.staticpower.data;

import java.io.BufferedWriter;
import java.io.IOException;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.fml.util.thread.SidedThreadGroups;
import theking530.staticpower.StaticPower;
import theking530.staticpower.network.StaticPowerMessageHandler;

public abstract class StaticPowerGameData {
	private final String name;

	public StaticPowerGameData(String name) {
		this.name = name;
	}

	public abstract void load(CompoundTag tag);

	public abstract CompoundTag serialize(CompoundTag tag);

	public void saveToDisk(BufferedWriter writer) throws IOException {
		writer.write(JsonUtilities.nbtToPrettyJson(serialize(new CompoundTag())));
	}

	public void tick() {
	}

	public String getName() {
		return name;
	}

	public void onFirstTimeCreated() {
		StaticPower.LOGGER.debug(String.format("Created GameData with name: %1$s.", getName()));
	}

	public void onSyncedFromServer() {
		StaticPower.LOGGER.debug(String.format("Recieved synchronization data for GameData with name: %1$s.", getName()));
	}

	public void syncToClients() {
		if (Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER) {
			StaticPowerMessageHandler.sendToAllPlayers(StaticPowerMessageHandler.MAIN_PACKET_CHANNEL, new StaticPowerGameDataSyncPacket(this));
		} else {
			StaticPower.LOGGER.warn(String.format("Attempted to synchronize data for GameData with name: %1$s to clients while already on the client.", getName()));
		}
	}
}
