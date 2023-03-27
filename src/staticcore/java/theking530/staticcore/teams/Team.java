package theking530.staticcore.teams;

import java.sql.Connection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.server.ServerLifecycleHooks;
import theking530.staticcore.StaticCore;
import theking530.staticcore.data.StaticPowerGameDataManager;
import theking530.staticcore.network.NetworkMessage;
import theking530.staticcore.network.StaticCoreMessageHandler;
import theking530.staticcore.productivity.ProductionManager;
import theking530.staticcore.research.gui.ResearchManager;
import theking530.staticcore.utilities.NBTUtilities;

public class Team {
	private final Connection db;
	private final HashSet<String> players;
	private final ResearchManager researchManager;
	private final ProductionManager productionManager;
	private final boolean isClientSide;
	private String id;
	private String name;
	private boolean dirty;

	public Team(String name, String id, boolean isClientSide) {
		this.id = id.replace("-", "");
		if (!isClientSide) {
			this.db = StaticPowerGameDataManager.getDatabaseConnection(new ResourceLocation(StaticCore.MOD_ID, id));
		} else {
			this.db = null;
		}

		this.isClientSide = isClientSide;
		players = new LinkedHashSet<String>();
		researchManager = new ResearchManager(this, isClientSide);
		productionManager = new ProductionManager(this, isClientSide);
		this.name = name;
	}

	public void tick(Level level) {
		level.getProfiler().push("ProductionManager.Tick");
		productionManager.tick(level.getGameTime());
		level.getProfiler().pop();
	}

	public String getName() {
		return name;
	}

	public boolean isClientSide() {
		return isClientSide;
	}

	public void setName(String name) {
		this.name = name;
	}

	public HashSet<String> getPlayers() {
		return players;
	}

	public boolean hasPlayer(Player player) {
		return players.contains(player.getStringUUID());
	}

	public void addPlayer(Player player) {
		this.players.add(player.getStringUUID());
	}

	public boolean removePlayer(Player player) {
		return this.players.remove(player.getStringUUID());
	}

	public ResearchManager getResearchManager() {
		return researchManager;
	}

	public ProductionManager getProductionManager() {
		return productionManager;
	}

	public Connection getDatabaseConnection() {
		return db;
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
				StaticCoreMessageHandler.sendMessageToPlayer(StaticCoreMessageHandler.MAIN_PACKET_CHANNEL, player, message);
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
				ServerPlayer player = (ServerPlayer) ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(UUID.fromString(uuid));
				callback.accept(player);
			}
		} else {
			StaticCore.LOGGER.error("An attempt was made to execute logic for all members on this team from the client. This is a NOOP.");
		}
	}

	public void markDirty(boolean dirty) {
		this.dirty = dirty;
	}

	public boolean isDirty() {
		return dirty;
	}

	public String getId() {
		return id;
	}

	public static Team fromTag(CompoundTag tag, boolean isClientSide) {
		Team output = new Team(tag.getString("name"), tag.getString("id"), isClientSide);
		output.deserialize(tag);
		return output;
	}

	public void deserialize(CompoundTag tag) {
		name = tag.getString("name");
		id = tag.getString("id");

		ListTag playersTag = tag.getList("players", Tag.TAG_COMPOUND);
		players.addAll(NBTUtilities.deserialize(playersTag, (playerTag) -> {
			return ((CompoundTag) playerTag).getString("id");
		}));

		researchManager.deserialize(tag.getCompound("research"), this);
	}

	public CompoundTag serialize() {
		CompoundTag output = new CompoundTag();
		output.putString("name", name);
		output.putString("id", id.toString());
		output.put("research", researchManager.serialize());
		output.put("players", NBTUtilities.serialize(players, (player, tag) -> {
			tag.putString("id", player);
		}));

		return output;
	}

	@Override
	public String toString() {
		return "Team [id=" + id + ", name=" + name + ", players=" + players + ", researchManager=" + researchManager + ", productionManager=" + productionManager + "]";
	}
}
