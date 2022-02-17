package theking530.staticpower.teams;

import java.util.HashMap;
import java.util.HashSet;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import theking530.staticpower.data.research.Research.ResearchInstance;
import theking530.staticpower.utilities.NBTUtilities;

public class Team {
	private String name;
	private final HashSet<String> players;
	private final HashSet<ResourceLocation> completedResearch;
	private final HashMap<ResourceLocation, ResearchInstance> activeResearch;
	private ResearchInstance currentResearch;

	public Team(String name) {
		players = new HashSet<String>();
		completedResearch = new HashSet<>();
		activeResearch = new HashMap<>();
		this.name = name;
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

	public void setCurrentResearch(ResourceLocation name) {
		if (!activeResearch.containsKey(name)) {
			activeResearch.put(name, new ResearchInstance(name));
		}
		currentResearch = activeResearch.get(name);
	}

	public HashSet<ResourceLocation> getCompletedResearch() {
		return completedResearch;
	}

	public HashMap<ResourceLocation, ResearchInstance> getActiveResearch() {
		return activeResearch;
	}

	public ResearchInstance getCurrentResearch() {
		return currentResearch;
	}

	public CompoundTag serialize() {
		CompoundTag output = new CompoundTag();
		output.putString("name", name);
		output.putString("currentResearch", currentResearch.getResearchName().toString());

		output.put("players", NBTUtilities.serialize(players, (player, tag) -> {
			tag.putString("id", player);
		}));

		output.put("completedResearch", NBTUtilities.serialize(completedResearch, (research, tag) -> {
			tag.putString("name", research.toString());
		}));

		output.put("activeResearch", NBTUtilities.serialize(activeResearch.values(), (research) -> {
			return research.serialize();
		}));

		return output;
	}

	public static Team deserialize(CompoundTag tag) {
		String name = tag.getString("name");
		Team team = new Team(name);

		ListTag playersTag = tag.getList("players", Tag.TAG_COMPOUND);
		team.players.addAll(NBTUtilities.deserialize(playersTag, (playerTag) -> {
			return playerTag.getString("id");
		}));

		ListTag completedResearch = tag.getList("completedResearch", Tag.TAG_COMPOUND);
		team.completedResearch.addAll(NBTUtilities.deserialize(completedResearch, (research) -> {
			return new ResourceLocation(research.getString("name"));
		}));

		ListTag activeResearch = tag.getList("activeResearch", Tag.TAG_COMPOUND);
		NBTUtilities.deserialize(activeResearch, (research) -> {
			return ResearchInstance.deserialize(research);
		}).forEach((completed) -> {
			team.activeResearch.put(completed.getResearchName(), completed);
		});

		team.currentResearch = team.activeResearch.get(new ResourceLocation(tag.getString("currentResearch")));

		return team;
	}
}
