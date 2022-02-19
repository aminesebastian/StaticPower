package theking530.staticpower.teams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import theking530.staticpower.data.crafting.StaticPowerRecipeRegistry;
import theking530.staticpower.data.research.Research;
import theking530.staticpower.data.research.Research.ResearchInstance;
import theking530.staticpower.utilities.NBTUtilities;

public class Team {
	private String name;
	private UUID id;
	private final HashSet<String> players;
	private final List<ResourceLocation> completedResearch;
	private final HashMap<ResourceLocation, ResearchInstance> activeResearch;
	private ResearchInstance currentResearch;

	public Team(String name) {
		players = new LinkedHashSet<String>();
		completedResearch = new ArrayList<>();
		activeResearch = new LinkedHashMap<>();
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

	public void setCurrentResearch(ResourceLocation name) {
		if (!activeResearch.containsKey(name)) {
			activeResearch.put(name, new ResearchInstance(name, this));
		}
		currentResearch = activeResearch.get(name);
	}

	public List<ResourceLocation> getCompletedResearch() {
		return completedResearch;
	}

	public void addCompletedResearch(ResourceLocation research) {
		this.completedResearch.add(research);
		if (activeResearch.containsKey(research)) {
			activeResearch.remove(research);
		}
	}

	public HashMap<ResourceLocation, ResearchInstance> getActiveResearch() {
		return activeResearch;
	}

	public ResearchInstance getResearchProgress(ResourceLocation research) {
		return activeResearch.get(research);
	}

	public ResearchInstance getCurrentResearch() {
		return currentResearch;
	}

	public boolean isResearching() {
		return currentResearch != null;
	}

	public boolean isResearching(ResourceLocation research) {
		return activeResearch.containsKey(research);
	}

	public Research getLastCompletedResearch() {
		return StaticPowerRecipeRegistry.getRecipe(Research.RECIPE_TYPE, completedResearch.get(completedResearch.size() - 1)).orElse(null);
	}

	public UUID getId() {
		return id;
	}

	public CompoundTag serialize() {
		CompoundTag output = new CompoundTag();
		output.putString("name", name);
		output.putString("id", id.toString());

		if (currentResearch != null) {
			output.putString("currentResearch", currentResearch.getResearchName().toString());
		}

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
		UUID id = UUID.fromString(tag.getString("id"));
		team.id = id;

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
			return ResearchInstance.deserialize(research, team);
		}).forEach((active) -> {
			team.activeResearch.put(active.getResearchName(), active);
		});

		if (tag.contains("currentResearch")) {
			ResourceLocation current = new ResourceLocation(tag.getString("currentResearch"));
			if (team.activeResearch.containsKey(current)) {
				team.currentResearch = team.activeResearch.get(new ResourceLocation(tag.getString("currentResearch")));
			}
		}

		return team;
	}
}
