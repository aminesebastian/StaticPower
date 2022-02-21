package theking530.staticpower.teams;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.server.ServerLifecycleHooks;
import theking530.staticpower.StaticPower;
import theking530.staticpower.network.NetworkMessage;
import theking530.staticpower.network.StaticPowerMessageHandler;
import theking530.staticpower.teams.research.ResearchManager;
import theking530.staticpower.utilities.NBTUtilities;

public class Team {
	private String name;
	private UUID id;
	private final HashSet<String> players;
	private final ResearchManager researchContainer;
	private boolean dirty;

	public Team(String name) {
		players = new LinkedHashSet<String>();
		researchContainer = new ResearchManager(this);
		this.name = name;
		this.id = UUID.randomUUID();
	}

	public String getName() {
		return name;
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
		return researchContainer;
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
				StaticPowerMessageHandler.sendMessageToPlayer(StaticPowerMessageHandler.MAIN_PACKET_CHANNEL, player, message);
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
			StaticPower.LOGGER.error("An attempt was made to execute logic for all members on this team from the client. This is a NOOP.");
		}
	}

	public void markDirty(boolean dirty) {
		this.dirty = dirty;
	}

	public boolean isDirty() {
		return dirty;
	}

	public UUID getId() {
		return id;
	}

	public CompoundTag serialize() {
		CompoundTag output = new CompoundTag();
		output.putString("name", name);
		output.putString("id", id.toString());
		output.put("research", researchContainer.serialize());
		output.put("players", NBTUtilities.serialize(players, (player, tag) -> {
			tag.putString("id", player);
		}));

		return output;
	}

	public static Team deserialize(CompoundTag tag) {
		String name = tag.getString("name");
		Team team = new Team(name);
		UUID id = UUID.fromString(tag.getString("id"));
		team.id = id;

		ListTag playersTag = tag.getList("players", Tag.TAG_COMPOUND);
		team.players.addAll(NBTUtilities.deserialize(playersTag, (playerTag) -> {
			return playerTag.getString("id");
		}));

		team.researchContainer.deserialize(tag.getCompound("research"), team);
		return team;
	}

	@Override
	public String toString() {
		return "Team [name=" + name + ", id=" + id + ", players=" + players + ", researchContainer=" + researchContainer + ", dirty=" + dirty + "]";
	}
}
