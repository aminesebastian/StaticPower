package theking530.staticpower.data.generators.recipes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import theking530.staticcore.crafting.StaticPowerIngredient;
import theking530.staticcore.data.generators.helpers.SCRecipeBuilder;
import theking530.staticcore.data.generators.helpers.SCRecipeProvider;
import theking530.staticcore.init.StaticCoreItems;
import theking530.staticcore.research.Research;
import theking530.staticcore.research.ResearchIcon;
import theking530.staticcore.research.ResearchUnlock;
import theking530.staticcore.utilities.SDColor;
import theking530.staticcore.utilities.math.Vector2D;
import theking530.staticpower.StaticPower;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModItems;

public class ResearchRecipeGenerator extends SCRecipeProvider<Research> {

	public ResearchRecipeGenerator(DataGenerator dataGenerator) {
		super("research", dataGenerator);
	}

	@Override
	protected void buildRecipes() {
		addRecipe("primary_tier", ResearchBuilder.of("basic_research", SDColor.RED)
				.setIcon(StaticCoreItems.ResearchTier1.get()).requirement((StaticCoreItems.ResearchTier1.get()), 10));

		addRecipe("primary_tier/static_seeds",
				ResearchBuilder.of("static_seeds", SDColor.RED).setIcon(ModItems.StaticSeeds.get())
						.spprerequisite("primary_tier").requirement((StaticCoreItems.ResearchTier1.get()), 20));

		addRecipe("primary_tier/tools/hammer",
				ResearchBuilder.of("basic_hamering", SDColor.RED).setIcon(ModItems.IronMetalHammer.get())
						.spprerequisite("primary_tier").requirement((StaticCoreItems.ResearchTier1.get()), 10)
						.unlocks(ResearchUnlock.craftingRecipe("iron", "crafting/shaped/tools/hammer/iron"))
						.unlocks(ResearchUnlock.craftingRecipe("copper", "crafting/shaped/tools/hammer/copper"))
						.unlocks(ResearchUnlock.craftingRecipe("zinc", "crafting/shaped/tools/hammer/zinc")));

		addRecipe("primary_tier/basic_electronics",
				ResearchBuilder.of("basic_electronics", SDColor.RED).setIcon(ModItems.Plug.get())
						.spprerequisite("primary_tier/tools/hammer")
						.requirement((StaticCoreItems.ResearchTier1.get()), 30)
						.unlocks(ResearchUnlock.craftingRecipe("crafting/shaped/io_port"))
						.unlocks(ResearchUnlock.craftingRecipe("crafting/shaped/plug"))
						.unlocks(ResearchUnlock.craftingRecipe("crafting/shaped/motor")));

		addRecipe("primary_tier/tools/wrench", ResearchBuilder.of("wrench", SDColor.RED).setIcon(ModItems.Wrench.get())
				.spprerequisite("primary_tier/tools/hammer").requirement((StaticCoreItems.ResearchTier1.get()), 10)
				.unlocks(ResearchUnlock.craftingRecipe("crafting/shaped/tools/wrench")));

		addRecipe("primary_tier/tools/soldering_iron",
				ResearchBuilder.of("soldering_iron", SDColor.RED).setIcon(ModItems.SolderingIron.get())
						.spprerequisite("primary_tier/tools/hammer")
						.requirement((StaticCoreItems.ResearchTier1.get()), 10)
						.unlocks(ResearchUnlock.craftingRecipe("crafting/shaped/tools/soldering_iron")));

		addRecipe("primary_tier/tools/wire_cutters",
				ResearchBuilder.of("basic_wire_cutting", SDColor.RED).setIcon(ModItems.IronWireCutters.get())
						.spprerequisite("primary_tier").requirement((StaticCoreItems.ResearchTier1.get()), 10)
						.unlocks(ResearchUnlock.craftingRecipe("iron", "crafting/shaped/tools/wire_cutter/iron"))
						.unlocks(ResearchUnlock.craftingRecipe("zinc", "crafting/shaped/tools/wire_cutter/zinc")));

		addRecipe("primary_tier/rubber_wood_stripping",
				ResearchBuilder.of("rubber_wood_stripping", SDColor.RED).setIcon(ModItems.RubberWoodBark.get())
						.spprerequisite("primary_tier").requirement((StaticCoreItems.ResearchTier1.get()), 10));

		addRecipe("primary_tier/rusty_caldron",
				ResearchBuilder.of("rusty_cauldron", SDColor.RED).setIcon(ModBlocks.RustyCauldron.get())
						.spprerequisite("primary_tier/rubber_wood_stripping")
						.requirement((StaticCoreItems.ResearchTier1.get()), 30)
						.unlocks(ResearchUnlock.craftingRecipe("iron", "crafting/shaped/machines/rusty_cauldron")));
	}

	protected void addRecipe(String nameOverride, String title, String description, SDColor color,
			Vector2D visualOffset, int sortOrder, ResearchIcon icon, StaticPowerIngredient... requirements) {
		addRecipe(nameOverride, title, description, color, visualOffset, sortOrder, Collections.emptyList(), icon,
				new String[] {}, requirements);
	}

	protected void addRecipe(String nameOverride, String title, String description, SDColor color,
			Vector2D visualOffset, int sortOrder, ResearchIcon icon, String[] prerequisites,
			StaticPowerIngredient... requirements) {
		addRecipe(nameOverride, title, description, color, visualOffset, sortOrder, Collections.emptyList(), icon,
				prerequisites, requirements);
	}

	protected void addRecipe(String nameOverride, String title, String description, SDColor color,
			Vector2D visualOffset, int sortOrder, List<ResearchUnlock> unlocks, ResearchIcon icon,
			String[] prerequisites, StaticPowerIngredient... requirements) {
		addRecipe(nameOverride, title, description, color, visualOffset, sortOrder, unlocks, icon, prerequisites,
				Arrays.asList(requirements), Collections.emptyList(), Collections.emptyList(), false);
	}

	protected void addRecipe(String nameOverride, String title, String description, SDColor color,
			Vector2D visualOffset, int sortOrder, List<ResearchUnlock> unlocks, ResearchIcon icon,
			String[] prerequisites, List<StaticPowerIngredient> requirements, List<ItemStack> rewards,
			List<ResourceLocation> advancements, boolean hiddenUntilAvailable) {
		List<ResourceLocation> adjustedPreReqs = new ArrayList<>();
		for (String preReq : prerequisites) {
			adjustedPreReqs.add(new ResourceLocation(StaticPower.MOD_ID, "research/" + preReq));
		}
		addRecipe(nameOverride, SCRecipeBuilder.create(new Research(null, title, description, visualOffset, sortOrder,
				unlocks, icon, adjustedPreReqs, requirements, rewards, advancements, hiddenUntilAvailable, color)));
	}

	protected void addRecipe(String nameOverride, ResearchBuilder researchBuilder) {
		addRecipe(nameOverride, SCRecipeBuilder.create(researchBuilder.build()));
	}

	protected static class ResearchBuilder {
		private final String title;
		private final String description;
		private Vector2D visualOffset;
		private int sortOrder;
		private List<ResearchUnlock> unlocks;
		private ResearchIcon icon;
		private List<ResourceLocation> prerequisites;
		private List<StaticPowerIngredient> requirements;
		private List<ItemStack> rewards;
		private List<ResourceLocation> advancements;
		private boolean hiddenUntilAvailable;
		private SDColor color;

		private ResearchBuilder(String title, String description, SDColor color) {
			this.title = title;
			this.description = description;
			this.color = color;
			visualOffset = Vector2D.ZERO;
			sortOrder = 0;
			unlocks = new ArrayList<>();
			icon = ResearchIcon.EMPTY;
			prerequisites = new ArrayList<>();
			requirements = new ArrayList<>();
			rewards = new ArrayList<>();
			advancements = new ArrayList<>();
			hiddenUntilAvailable = false;
		}

		public static ResearchBuilder of(String title, SDColor color) {
			return new ResearchBuilder("research." + title + ".title", "research." + title + ".description", color);
		}

		public static ResearchBuilder of(String title, String description, SDColor color) {
			return new ResearchBuilder("research." + title + ".title", "research." + description + ".description",
					color);
		}

		public Research build() {
			return new Research(null, title, description, visualOffset, sortOrder, unlocks, icon, prerequisites,
					requirements, rewards, advancements, hiddenUntilAvailable, color);
		}

		public ResearchBuilder setVisualOffset(float x, float y) {
			this.visualOffset = new Vector2D(x, y);
			return this;
		}

		public ResearchBuilder setSortOrder(int sortOrder) {
			this.sortOrder = sortOrder;
			return this;
		}

		public ResearchBuilder unlocks(ResearchUnlock unlock) {
			this.unlocks.add(unlock);
			return this;
		}

		public ResearchBuilder setIcon(ResearchIcon icon) {
			this.icon = icon;
			return this;
		}

		public ResearchBuilder setIcon(Item icon) {
			this.icon = ResearchIcon.fromItem(icon);
			return this;
		}

		public ResearchBuilder setIcon(Block icon) {
			this.icon = ResearchIcon.fromItem(icon.asItem());
			return this;
		}

		public ResearchBuilder setIcon(ItemStack icon) {
			this.icon = ResearchIcon.fromItem(icon);
			return this;
		}

		public ResearchBuilder spprerequisite(String prerequisite) {
			this.prerequisites.add(new ResourceLocation(StaticPower.MOD_ID, "research/" + prerequisite));
			return this;
		}

		public ResearchBuilder prerequisite(ResourceLocation prerequisite) {
			this.prerequisites.add(prerequisite);
			return this;
		}

		public ResearchBuilder requirement(StaticPowerIngredient requirement) {
			this.requirements.add(requirement);
			return this;
		}

		public ResearchBuilder requirement(Item item, int count) {
			this.requirements.add(StaticPowerIngredient.of(item, count));
			return this;
		}

		public ResearchBuilder reward(ItemStack reward) {
			this.rewards.add(reward);
			return this;
		}

		public ResearchBuilder advancement(ResourceLocation advancement) {
			this.advancements.add(advancement);
			return this;
		}

		public ResearchBuilder setHiddenUntilAvailable(boolean hiddenUntilAvailable) {
			this.hiddenUntilAvailable = hiddenUntilAvailable;
			return this;
		}

		public ResearchBuilder setColor(SDColor color) {
			this.color = color;
			return this;
		}
	}
}
