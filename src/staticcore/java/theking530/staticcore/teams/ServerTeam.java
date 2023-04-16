package theking530.staticcore.teams;

import java.sql.Connection;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.server.ServerLifecycleHooks;
import theking530.staticcore.StaticCore;
import theking530.staticcore.data.StaticCoreGameDataManager;
import theking530.staticcore.network.NetworkMessage;
import theking530.staticcore.network.StaticCoreMessageHandler;
import theking530.staticcore.productivity.ServerProductionManager;
import theking530.staticcore.research.gui.ResearchManager;

public class ServerTeam extends AbstractTeam {
	private final Connection db;
	private final ResearchManager researchManager;
	private final ServerProductionManager productionManager;

	public ServerTeam(String name, String id) {
		super(name, id);
		db = StaticCoreGameDataManager.get()
				.getDatabaseConnection(new ResourceLocation(StaticCore.MOD_ID, "team_" + id));
		researchManager = new ResearchManager(this, false);
		productionManager = new ServerProductionManager(this);
	}

	@Override
	public ResearchManager getResearchManager() {
		return researchManager;
	}

	@Override
	public ServerProductionManager getProductionManager() {
		return productionManager;
	}

	public Connection getDatabaseConnection() {
		return db;
	}

	@Override
	public boolean isClientSide() {
		return false;
	}

	/**
	 * Sends the provided packet to all players on the team. Note: This should ONLY
	 * be called on the server. Client side calls will result in a noop.
	 * 
	 * @param message
	 */
	public void sendPacketToAllPlayers(NetworkMessage message) {
		sendPacketToAllPlayers(message, null);
	}

	/**
	 * Sends the provided packet to all players on the team. Note: This should ONLY
	 * be called on the server. Client side calls will result in a noop. The filter
	 * parameter can be used to limit which players the packet is sent to.
	 * 
	 * @param message
	 * @param filter
	 */
	public void sendPacketToAllPlayers(NetworkMessage message, Function<ServerPlayer, Boolean> filter) {
		forEachPlayer((player) -> {
			if (filter == null || filter.apply(player)) {
				StaticCoreMessageHandler.sendMessageToPlayer(StaticCoreMessageHandler.MAIN_PACKET_CHANNEL, player,
						message);
			}
		});
	}

	/**
	 * Plays a sound for all players on the team.
	 * 
	 * @param sound
	 * @param volume
	 * @param pitch
	 */
	public void playLocalSoundForAllPlayers(SoundEvent sound, float volume, float pitch) {
		forEachPlayer((player) -> {
			player.getLevel().playSound(null, player.getOnPos(), sound, SoundSource.MASTER, volume, pitch);
		});
	}

	/**
	 * Executes the provided callback for each ServerPlayer. Calling this on the
	 * client results in a no-op.
	 * 
	 * @param callback
	 */
	protected void forEachPlayer(Consumer<ServerPlayer> callback) {
		if (ServerLifecycleHooks.getCurrentServer() != null) {
			for (String uuid : getPlayers()) {
				ServerPlayer player = (ServerPlayer) ServerLifecycleHooks.getCurrentServer().getPlayerList()
						.getPlayer(UUID.fromString(uuid));
				callback.accept(player);
			}
		} else {
			StaticCore.LOGGER.error(
					"An attempt was made to execute logic for all members on this team from the client. This is a NOOP.");
		}
	}

	public static ServerTeam fromTag(CompoundTag tag) {
		ServerTeam output = new ServerTeam(tag.getString("name"), tag.getString("id"));
		output.deserializeNBT(tag);
		return output;
	}
}
