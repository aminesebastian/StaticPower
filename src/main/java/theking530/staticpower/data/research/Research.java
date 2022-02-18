package theking530.staticpower.data.research;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.crafting.AbstractStaticPowerRecipe;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.StaticPowerRecipeRegistry;
import theking530.staticpower.teams.Team;

public class Research extends AbstractStaticPowerRecipe {
	public static final RecipeType<Research> RECIPE_TYPE = RecipeType.register("research");
	private final String title;
	private final String description;
	private final ItemStack itemIcon;
	private final ResourceLocation textureIcon;
	private final List<ResourceLocation> prerequisites;
	private final List<StaticPowerIngredient> requirements;
	private final List<ItemStack> rewards;
	private final List<ResourceLocation> advancements;

	public Research(ResourceLocation name, String title, String description, List<ResourceLocation> prerequisites, List<StaticPowerIngredient> requirements, List<ItemStack> rewards,
			List<ResourceLocation> advancements, ItemStack itemIcon, ResourceLocation textureIcon) {
		super(name);
		this.title = title;
		this.description = description;
		this.prerequisites = prerequisites;
		this.requirements = requirements;
		this.rewards = rewards;
		this.itemIcon = itemIcon;
		this.textureIcon = textureIcon;
		this.advancements = advancements;
	}

	public List<ResourceLocation> getAdvancements() {
		return advancements;
	}

	public boolean hasItemStackIcon() {
		return itemIcon != null;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public List<ResourceLocation> getPrerequisites() {
		return prerequisites;
	}

	public List<StaticPowerIngredient> getRequirements() {
		return requirements;
	}

	public List<ItemStack> getRewards() {
		return rewards;
	}

	public ItemStack getItemIcon() {
		return itemIcon;
	}

	public ResourceLocation getTextureIcon() {
		return textureIcon;
	}

	@Override
	public RecipeType<?> getType() {
		return RECIPE_TYPE;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return ResearchSerializer.INSTANCE;
	}

	public boolean isValid(RecipeMatchParameters matchParams) {
		// Check items if requested.
		if (matchParams.shouldVerifyItems()) {
			// Check if the input counts catch.
			if (matchParams.getItems().length < requirements.size()) {
				return false;
			}

			// Copy the inputs.
			List<ItemStack> inputCopies = new ArrayList<ItemStack>();
			for (ItemStack input : matchParams.getItems()) {
				inputCopies.add(input.copy());
			}

			// Check each item, if any fails, return false.
			int matches = 0;
			for (StaticPowerIngredient ing : requirements) {
				for (int i = 0; i < inputCopies.size(); i++) {
					// Check the match.
					boolean itemMatched = false;
					if (matchParams.shouldVerifyItemCounts()) {
						itemMatched = ing.testWithCount(inputCopies.get(i));
					} else {
						itemMatched = ing.test(inputCopies.get(i));
					}

					// Check if there was a match.
					if (itemMatched) {
						inputCopies.set(i, ItemStack.EMPTY);
						matches++;
						break;
					}
				}
			}

			// Return false if we did not have the correct amount of matches.
			if (matches != requirements.size()) {
				return false;
			}
		}

		return true;
	}

	public static class ResearchInstance {
		private final ResourceLocation researchName;
		private final List<Integer> requirementFullfillment;
		private final Research research;
		private final Team team;

		public ResearchInstance(ResourceLocation researchName, Team team) {
			this.team = team;
			this.researchName = researchName;
			this.requirementFullfillment = new LinkedList<Integer>();
			research = StaticPowerRecipeRegistry.getRecipe(Research.RECIPE_TYPE, researchName).orElse(null);

			// Throw a fatal error if somehow we ended up with an invalid research name.
			if (research == null) {
				StaticPower.LOGGER.fatal(String.format("Invalid research with name: %1$s provided.", researchName.toString()));
			} else {
				for (int i = 0; i < research.getRequirements().size(); i++) {
					requirementFullfillment.add(0);
				}
			}
		}

		public ResourceLocation getResearchName() {
			return researchName;
		}

		public Research getTrackedResearch() {
			return research;
		}

		public int getRequirementFullfillment(int index) {
			return requirementFullfillment.get(index);
		}

		public void addRequirementFullfillment(int index, int amount) {
			requirementFullfillment.set(index, requirementFullfillment.get(index) + amount);
			if (isCompleted()) {
				//team.addCompletedResearch(researchName);
			}
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

		public boolean isCompleted() {
			return getFullfillmentPercentage() >= 1.0f;
		}

		public static ResearchInstance deserialize(CompoundTag tag, Team team) {
			String researchName = tag.getString("researchName");
			ResearchInstance instance = new ResearchInstance(new ResourceLocation(researchName), team);

			int[] fullfillment = tag.getIntArray("requirementFullfillment");
			for (int i = 0; i < fullfillment.length; i++) {
				instance.requirementFullfillment.set(i, fullfillment[i]);
			}
			System.out.println();
			return instance;
		}

		public CompoundTag serialize() {
			CompoundTag output = new CompoundTag();
			output.putString("researchName", researchName.toString());
			output.putIntArray("requirementFullfillment", requirementFullfillment);
			return output;
		}
	}
}
