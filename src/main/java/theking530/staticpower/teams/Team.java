package theking530.staticpower.teams;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.UUID;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
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
