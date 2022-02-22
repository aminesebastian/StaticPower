package theking530.staticpower.data.research;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import theking530.staticcore.utilities.Color;
import theking530.staticpower.data.crafting.AbstractStaticPowerRecipe;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.items.ResearchItem;

public class Research extends AbstractStaticPowerRecipe {
	public static final RecipeType<Research> RECIPE_TYPE = RecipeType.register("research");
	private final String title;
	private final String description;
	private final List<ResearchUnlock> unlocks;
	private final ResearchIcon icon;
	private final List<ResourceLocation> prerequisites;
	private final List<StaticPowerIngredient> requirements;
	private final List<ItemStack> rewards;
	private final List<ResourceLocation> advancements;
	private final boolean hiddenUntilAvailable;
	private final Color color;

	public Research(ResourceLocation name, String title, String description, List<ResourceLocation> prerequisites, List<StaticPowerIngredient> requirements, List<ItemStack> rewards,
			List<ResearchUnlock> unlocks, List<ResourceLocation> advancements, ResearchIcon icon, boolean hiddenUntilAvailable, Color color) {
		super(name);
		this.title = title;
		this.description = description;
		this.prerequisites = prerequisites;
		this.requirements = requirements;
		this.rewards = rewards;
		this.unlocks = unlocks;
		this.icon = icon;
		this.advancements = advancements;
		this.hiddenUntilAvailable = hiddenUntilAvailable;
		if (color == null) {
			this.color = calculateColor();
		} else {
			this.color = color;
		}
	}

	public List<ResourceLocation> getAdvancements() {
		return advancements;
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

	public List<ResearchUnlock> getUnlocks() {
		return unlocks;
	}

	public ResearchIcon getIcon() {
		return icon;
	}

	public boolean isHiddenUntilAvailable() {
		return hiddenUntilAvailable;
	}

	public Color getColor() {
		return color;
	}

	@Override
	public RecipeType<?> getType() {
		return RECIPE_TYPE;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return ResearchSerializer.INSTANCE;
	}

	@Override
	public String toString() {
		return getId().toString();
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

	private Color calculateColor() {
		int maxTier = 0;
		Color color = Color.WHITE;
		for (StaticPowerIngredient input : requirements) {
			if (input.getIngredient().getItems()[0].getItem() instanceof ResearchItem) {
				ResearchItem item = (ResearchItem) input.getIngredient().getItems()[0].getItem();
				if (item.getResearchTier() > maxTier) {
					maxTier = item.getResearchTier();
					color = item.getColor();
				}
			}
		}

		return color;
	}

}
