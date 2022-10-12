package theking530.staticpower.data.research;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import theking530.staticcore.utilities.SDColor;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.crafting.AbstractStaticPowerRecipe;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeType;
import theking530.staticpower.data.research.ResearchUnlock.ResearchUnlockType;
import theking530.staticpower.init.ModItems;
import theking530.staticpower.items.ResearchItem;

public class Research extends AbstractStaticPowerRecipe {
	public static final String ID = "research";
	public static final RecipeType<Research> RECIPE_TYPE = new StaticPowerRecipeType<Research>();

	private final String title;
	private final String description;
	private final Vector2D visualOffset;
	private final int sortOrder;
	private final List<ResearchUnlock> unlocks;
	private final ResearchIcon icon;
	private final List<ResourceLocation> prerequisites;
	private final List<StaticPowerIngredient> requirements;
	private final List<ItemStack> rewards;
	private final List<ResourceLocation> advancements;
	private final boolean hiddenUntilAvailable;
	private SDColor color;

	public Research(ResourceLocation name, String title, String description, Vector2D visualOffset, int sortOrder, List<ResourceLocation> prerequisites,
			List<StaticPowerIngredient> requirements, List<ItemStack> rewards, List<ResearchUnlock> unlocks, List<ResourceLocation> advancements, ResearchIcon icon,
			boolean hiddenUntilAvailable, SDColor color) {
		super(name);
		this.title = title;
		this.description = description;
		this.visualOffset = visualOffset;
		this.prerequisites = prerequisites;
		this.requirements = requirements;
		this.rewards = rewards;
		this.unlocks = unlocks;
		this.icon = icon;
		this.advancements = advancements;
		this.hiddenUntilAvailable = hiddenUntilAvailable;
		this.sortOrder = sortOrder;
		this.color = color;
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

	public Vector2D getVisualOffset() {
		return visualOffset;
	}

	public int getSortOrder() {
		return sortOrder;
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

	public SDColor getColor() {
		if (color == null) {
			// We have to lazy load like this to not break tags.
			color = calculateColor();
		}
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

	private SDColor calculateColor() {
		int maxTier = 0;
		SDColor color = SDColor.WHITE;
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

	public static class ResearchBuilder {
		private final ResourceLocation name;
		private final String title;
		private String description;
		private Vector2D visualOffset;
		private int sortOrder;
		private ResearchIcon icon;
		private boolean hiddenUntilAvailable;
		private SDColor color;

		private final List<ResourceLocation> prerequisites;
		private final List<StaticPowerIngredient> requirements;
		private final List<ResourceLocation> advancements;
		private final List<ItemStack> rewards;
		private final List<ResearchUnlock> unlocks;

		public ResearchBuilder(ResourceLocation name, String title) {
			this.name = name;
			this.title = title;
			this.sortOrder = 0;
			this.prerequisites = new ArrayList<>();
			this.requirements = new ArrayList<>();
			this.rewards = new ArrayList<>();
			this.advancements = new ArrayList<>();
			this.unlocks = new ArrayList<>();
		}

		public ResourceLocation getName() {
			return name;
		}

		public static ResearchBuilder Create(ResourceLocation name, String title) {
			ResearchBuilder output = new ResearchBuilder(name, title);
			return output;
		}

		public static ResearchBuilder Create(String name, String title) {
			ResearchBuilder output = new ResearchBuilder(new ResourceLocation(StaticPower.MOD_ID, name), title);
			return output;
		}

		public ResearchBuilder description(String description) {
			this.description = description;
			return this;
		}

		public ResearchBuilder visualOffset(float x, float y) {
			this.visualOffset = new Vector2D(x, y);
			return this;
		}

		public ResearchBuilder icon(ResourceLocation texture) {
			this.icon = ResearchIcon.fromTexture(texture);
			return this;
		}

		public ResearchBuilder icon(Item item) {
			this.icon = ResearchIcon.fromItem(new ItemStack(item));
			return this;
		}

		public ResearchBuilder icon(ItemStack stack) {
			this.icon = ResearchIcon.fromItem(stack);
			return this;
		}

		public ResearchBuilder color(SDColor color) {
			this.color = color;
			return this;
		}

		public ResearchBuilder hiddenUntilAvailable() {
			this.hiddenUntilAvailable = true;
			return this;
		}

		public ResearchBuilder preReqs(ResourceLocation... prereqs) {
			for (ResourceLocation req : prereqs) {
				this.prerequisites.add(req);
			}
			return this;
		}

		public ResearchBuilder advancements(ResourceLocation... advancements) {
			for (ResourceLocation advancement : advancements) {
				this.advancements.add(advancement);
			}
			return this;
		}

		public ResearchBuilder sortOrder(int sortOrder) {
			this.sortOrder = sortOrder;
			return this;
		}

		public ResearchBuilder tier1(int count) {
			requirements.add(new StaticPowerIngredient(new ItemStack(ModItems.ResearchTier1.get()), count));
			return this;
		}

		public ResearchBuilder tier2(int count) {
			requirements.add(new StaticPowerIngredient(new ItemStack(ModItems.ResearchTier2.get()), count));
			return this;
		}

		public ResearchBuilder tier3(int count) {
			requirements.add(new StaticPowerIngredient(new ItemStack(ModItems.ResearchTier3.get()), count));
			return this;
		}

		public ResearchBuilder tier4(int count) {
			requirements.add(new StaticPowerIngredient(new ItemStack(ModItems.ResearchTier4.get()), count));
			return this;
		}

		public ResearchBuilder tier5(int count) {
			requirements.add(new StaticPowerIngredient(new ItemStack(ModItems.ResearchTier5.get()), count));
			return this;
		}

		public ResearchBuilder tier6(int count) {
			requirements.add(new StaticPowerIngredient(new ItemStack(ModItems.ResearchTier6.get()), count));
			return this;
		}

		public ResearchBuilder tier7(int count) {
			requirements.add(new StaticPowerIngredient(new ItemStack(ModItems.ResearchTier7.get()), count));
			return this;
		}

		public ResearchBuilder craftingUnlock(String uniqueKey, ResourceLocation recipe, String description) {
			unlocks.add(new ResearchUnlock(uniqueKey, ResearchUnlockType.CRAFTING, recipe, null, description, ItemStack.EMPTY, false));
			return this;
		}

		public ResearchBuilder hiddenCraftingUnlock(String uniqueKey, ResourceLocation recipe, String description) {
			unlocks.add(new ResearchUnlock(uniqueKey, ResearchUnlockType.CRAFTING, recipe, null, description, ItemStack.EMPTY, true));
			return this;
		}

		public Research build() {
			return new Research(name, title, description, visualOffset, sortOrder, advancements, requirements, rewards, unlocks, advancements, icon, hiddenUntilAvailable, color);
		}
	}
}
