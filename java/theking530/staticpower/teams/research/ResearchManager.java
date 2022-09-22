package theking530.staticpower.teams.research;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.StaticPowerRecipeRegistry;
import theking530.staticpower.data.research.Research;
import theking530.staticpower.data.research.ResearchLevels;
import theking530.staticpower.init.ModResearch;
import theking530.staticpower.teams.Team;
import theking530.staticpower.utilities.NBTUtilities;

public class ResearchManager {
	private final Team team;
	private final List<ResourceLocation> completedResearch;
	private final HashMap<ResourceLocation, ResearchInstance> activeResearch;
	private ResearchInstance selectedResearch;

	public ResearchManager(Team team) {
		this.team = team;
		completedResearch = new ArrayList<>();
		activeResearch = new LinkedHashMap<>();
	}

	public void setSelectedResearch(ResourceLocation name) {
		if (completedResearch.contains(name)) {
			return;
		}

		if (!activeResearch.containsKey(name)) {
			activeResearch.put(name, new ResearchInstance(name, this));
		}
		selectedResearch = activeResearch.get(name);
		team.markDirty(true);
	}

	public void clearSelectedResearch() {
		selectedResearch = null;
		team.markDirty(true);
	}

	public List<ResourceLocation> getCompletedResearch() {
		return completedResearch;
	}

	public boolean hasCompletedResearch(ResourceLocation research) {
		return completedResearch.contains(research);
	}

	public boolean isSelectedResearch(ResourceLocation research) {
		if (selectedResearch == null) {
			return false;
		}
		return selectedResearch.getTrackedResearch().getId().equals(research);
	}

	public ResearchInstance getSelectedResearch() {
		return selectedResearch;
	}

	public boolean hasSelectedResearch() {
		return selectedResearch != null;
	}

	public void addProgressToSelectedResearch(int requirementIndex, int amount) {
		if (hasSelectedResearch() && !getSelectedResearch().isCompleted()) {
			team.markDirty(true);
			selectedResearch.requirementFullfillment[requirementIndex] = selectedResearch.requirementFullfillment[requirementIndex] + amount;
			if (selectedResearch.isCompleted()) {
				markResearchAsCompleted(selectedResearch.getTrackedResearch().getId());
			}
		}
	}

	public HashMap<ResourceLocation, ResearchInstance> getAllActiveResearch() {
		return activeResearch;
	}

	public ResearchInstance getResearchProgress(ResourceLocation research) {
		return activeResearch.get(research);
	}

	public boolean isResearching(ResourceLocation research) {
		return activeResearch.containsKey(research) && activeResearch.get(research).getFullfillmentPercentage() > 0;
	}

	public Research getLastCompletedResearch() {
		if (completedResearch.size() > 0) {
			return StaticPowerRecipeRegistry.getRecipe(Research.RECIPE_TYPE, completedResearch.get(completedResearch.size() - 1)).orElse(null);
		}
		return null;
	}

	public boolean isResearchAvailable(ResourceLocation id) {
		if (hasCompletedResearch(id)) {
			return false;
		}

		Research research = StaticPowerRecipeRegistry.getRecipe(Research.RECIPE_TYPE, id).orElse(null);
		if (research != null) {
			for (ResourceLocation pre : research.getPrerequisites()) {
				if (!completedResearch.contains(pre)) {
					return false;
				}
			}
		}

		return true;
	}

	public void unlockAllResearch(Level level) {
		completedResearch.clear();
		for (Research research : ResearchLevels.getAllResearch(level).values()) {
			completedResearch.add(research.getId());
		}
		selectedResearch = null;
	}

	public void lockAllResearch() {
		completedResearch.clear();
		activeResearch.clear();
		setSelectedResearch(ModResearch.BASIC_RESEARCH);
	}

	public Team getTeam() {
		return team;
	}

	protected void markResearchAsCompleted(ResourceLocation research) {
		// If we already completed this, wtf you doin.
		if (completedResearch.contains(research)) {
			StaticPower.LOGGER.warn(String.format("Team: %1$s attempted to complete already completed research: %2$s.", team, research.toString()));
			return;
		}

		// Add the completed research and remove it from the active research tree.
		completedResearch.add(research);
		if (activeResearch.containsKey(research)) {
			activeResearch.remove(research);
		}

		// Clear the selected research.
		selectedResearch = null;

		// Play a happy sound.
		team.playLocalSoundForAllPlayers(SoundEvents.FIREWORK_ROCKET_LAUNCH, 1.0f, 1.0f);
		team.playLocalSoundForAllPlayers(SoundEvents.ENCHANTMENT_TABLE_USE, 1.0f, 1.0f);
		team.playLocalSoundForAllPlayers(SoundEvents.EXPERIENCE_ORB_PICKUP, 0.5f, 1.0f);
		team.markDirty(true);
		StaticPower.LOGGER.info(String.format("Team: %1$s completed research: %2$s!", team, research.toString()));
	}

	public CompoundTag serialize() {
		CompoundTag output = new CompoundTag();

		if (selectedResearch != null) {
			output.putString("selectedResearch", selectedResearch.getId().toString());
		}

		output.put("completedResearch", NBTUtilities.serialize(completedResearch, (research, tag) -> {
			tag.putString("name", research.toString());
		}));

		output.put("activeResearch", NBTUtilities.serialize(activeResearch.values(), (research) -> {
			return research.serialize();
		}));

		return output;
	}

	public void deserialize(CompoundTag tag, Team team) {
		ListTag completedResearchList = tag.getList("completedResearch", Tag.TAG_COMPOUND);
		completedResearch.clear();
		completedResearch.addAll(NBTUtilities.deserialize(completedResearchList, (research) -> {
			return new ResourceLocation(((CompoundTag) research).getString("name"));
		}));

		activeResearch.clear();
		ListTag activeResearchList = tag.getList("activeResearch", Tag.TAG_COMPOUND);
		NBTUtilities.deserialize(activeResearchList, (research) -> {
			return ResearchInstance.deserialize(((CompoundTag) research), this);
		}).forEach((active) -> {
			activeResearch.put(active.getId(), active);
		});

		selectedResearch = null;
		if (tag.contains("selectedResearch")) {
			ResourceLocation current = new ResourceLocation(tag.getString("selectedResearch"));
			if (activeResearch.containsKey(current)) {
				selectedResearch = activeResearch.get(new ResourceLocation(tag.getString("selectedResearch")));
			}
		}
	}

	public static class ResearchInstance {
		public enum ResearchStatus {
			LOCKED, UNLOCKED, IN_PROGRESS_INACTIVE, IN_PROGRESS_ACTIVE, COMPLETED
		}

		private final int[] requirementFullfillment;
		private final Research research;
		private final ResearchManager manager;

		public ResearchInstance(ResourceLocation researchName, ResearchManager manager) {
			this.manager = manager;
			research = StaticPowerRecipeRegistry.getRecipe(Research.RECIPE_TYPE, researchName).orElse(null);

			// Throw a fatal error if somehow we ended up with an invalid research name.
			if (research == null) {
				requirementFullfillment = new int[0];
				StaticPower.LOGGER.fatal(String.format("Invalid research with name: %1$s provided.", researchName.toString()));
			} else {
				requirementFullfillment = new int[research.getRequirements().size()];
				for (int i = 0; i < research.getRequirements().size(); i++) {
					requirementFullfillment[i] = 0;
				}
			}
		}

		public ResourceLocation getId() {
			return research.getId();
		}

		public Research getTrackedResearch() {
			return research;
		}

		public int getRequirementFullfillment(int index) {
			return requirementFullfillment[index];
		}

		public ResearchManager getResearchManager() {
			return manager;
		}

		public float getFullfillmentPercentage() {
			int fullfillmentCount = 0;
			int totalRequirements = 0;

			for (int fullfillment : requirementFullfillment) {
				fullfillmentCount += fullfillment;
			}

			for (StaticPowerIngredient req : getTrackedResearch().getRequirements()) {
				totalRequirements += req.getCount();
			}

			return (float) fullfillmentCount / totalRequirements;
		}

		/**
		 * Checks if the provided item can satisfy part of this research and returns the
		 * index of the requirement.
		 * 
		 * @param stack
		 * @return
		 */
		public int getRequirementIndexFullfilledByItem(ItemStack stack) {
			for (int i = 0; i < getTrackedResearch().getRequirements().size(); i++) {
				StaticPowerIngredient ing = getTrackedResearch().getRequirements().get(i);
				if (ing.test(stack)) {
					int fullfilled = getRequirementFullfillment(i);
					if (fullfilled < ing.getCount()) {
						return i;
					}
				}
			}
			return -1;
		}

		public boolean isCompleted() {
			return getFullfillmentPercentage() >= 1.0f;
		}

		public static ResearchInstance deserialize(CompoundTag tag, ResearchManager manager) {
			String researchId = tag.getString("researchId");
			ResearchInstance instance = new ResearchInstance(new ResourceLocation(researchId), manager);

			int[] fullfillment = tag.getIntArray("requirementFullfillment");
			for (int i = 0; i < instance.getTrackedResearch().getRequirements().size(); i++) {
				if (i < fullfillment.length) {
					instance.requirementFullfillment[i] = fullfillment[i];
				}
			}
			return instance;
		}

		public CompoundTag serialize() {
			CompoundTag output = new CompoundTag();
			output.putString("researchId", getId().toString());
			output.putIntArray("requirementFullfillment", requirementFullfillment);
			return output;
		}
	}
}
