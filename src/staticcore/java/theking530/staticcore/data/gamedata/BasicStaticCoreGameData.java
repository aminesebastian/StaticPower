package theking530.staticcore.data.gamedata;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import theking530.staticcore.StaticCore;
import theking530.staticcore.network.StaticCoreMessageHandler;

public abstract class BasicStaticCoreGameData implements IStaticCoreGameData {
	private final ResourceLocation id;
	private final boolean isClientSide;

	public BasicStaticCoreGameData(ResourceLocation id, boolean isClientSide) {
		this.id = id;
		this.isClientSide = isClientSide;
	}

	@Override
	public void tick(Level level) {
	}

	@Override
	public boolean isClientSide() {
		return isClientSide;
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	@Override
	public void onFirstTimeCreated() {
		StaticCore.LOGGER.debug(String.format("Created GameData with name: %1$s.", getId()));
	}

	@Override
	public void onSyncedFromServer(CompoundTag tag) {
		if (!isClientSide()) {
			throw new RuntimeException("#onSyncedFromServer should only be called on the client!");
		}

		StaticCore.LOGGER.debug(String.format("Recieved synchronization data for GameData with name: %1$s.", getId()));
	}

	@Override
	public void syncToClients() {
		if (isClientSide()) {
			throw new RuntimeException("#syncToClient should only be called from the server side!");
		}
		StaticCoreMessageHandler.sendToAllPlayers(StaticCoreMessageHandler.MAIN_PACKET_CHANNEL,
				new StaticPowerGameDataSyncPacket(this));
	}
}
