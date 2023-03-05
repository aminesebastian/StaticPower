package theking530.staticpower.data.generators.recipes;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.data.DataGenerator;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.StaticPowerOutputItem;
import theking530.staticpower.data.crafting.wrappers.grinder.GrinderRecipe;
import theking530.staticpower.data.generators.RecipeItem;
import theking530.staticpower.data.generators.helpers.SPRecipeBuilder;
import theking530.staticpower.data.generators.helpers.SPRecipeProvider;
import theking530.staticpower.data.materials.MaterialBundle;
import theking530.staticpower.data.materials.MaterialBundle.MaterialBundleType;
import theking530.staticpower.data.materials.MaterialTypes;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModItems;
import theking530.staticpower.init.NewModMaterials;
import theking530.staticpower.init.tags.ModItemTags;

public class GrindingRecipeGenerator extends SPRecipeProvider<GrinderRecipe> {

	public GrindingRecipeGenerator(DataGenerator dataGenerator) {
		super("grinding", dataGenerator);
	}

	@Override
	protected void buildRecipes() {
		for (MaterialBundle bundle : NewModMaterials.MATERIALS.values()) {
			if (bundle.getMaterialType() == MaterialBundleType.METAL && bundle.has(MaterialTypes.DUST)) {
				if (bundle.has(MaterialTypes.INGOT)) {
					singleItemTodust("ingots/" + bundle.getName(), RecipeItem.of(bundle.get(MaterialTypes.INGOT).getItemTag()), bundle.get(MaterialTypes.DUST).get());
				}
				if (bundle.hasOre()) {
					singleItemTodust("ores/" + bundle.getName(), RecipeItem.of(bundle.getOreItemTag()), bundle.get(MaterialTypes.DUST).get(), 2);
				}
				if (bundle.has(MaterialTypes.RAW_MATERIAL)) {
					singleItemTodust("raw_material/" + bundle.getName(), RecipeItem.of(bundle.get(MaterialTypes.RAW_MATERIAL).getItemTag()), bundle.get(MaterialTypes.DUST).get(),
							2);
				}
			}

			if (bundle.getMaterialType() == MaterialBundleType.GEM && bundle.has(MaterialTypes.DUST)) {
				if (bundle.hasOre()) {
					addRecipe("ores/" + bundle.getName(), StaticPowerIngredient.of(bundle.getOreItemTag()),
							StaticPowerOutputItem.of(bundle.get(MaterialTypes.RAW_MATERIAL).get(), 3, 1, 1, 0.25f),
							StaticPowerOutputItem.of(bundle.get(MaterialTypes.DUST).get(), 1, 0.1f));
				}
				if (bundle.has(MaterialTypes.RAW_MATERIAL)) {
					singleItemTodust("raw_material/" + bundle.getName(), RecipeItem.of(bundle.get(MaterialTypes.RAW_MATERIAL).getItemTag()), bundle.get(MaterialTypes.DUST).get());
				}
			}

			if (bundle.getMaterialType() == MaterialBundleType.DUST && bundle.has(MaterialTypes.DUST)) {
				if (bundle.hasOre()) {
					singleItemTodust("ores/" + bundle.getName(), RecipeItem.of(bundle.getOreItemTag()), bundle.get(MaterialTypes.DUST).get(), 2);
				}
			}

			if (bundle.has(MaterialTypes.STORAGE_BLOCK) && bundle.has(MaterialTypes.DUST)) {
				singleItemTodust("blocks/" + bundle.getName(), RecipeItem.of(bundle.get(MaterialTypes.STORAGE_BLOCK).getItemTag()), bundle.get(MaterialTypes.DUST).get(), 9);
			}
		}

		metalOre(NewModMaterials.IRON, StaticPowerOutputItem.of(NewModMaterials.ZINC.get(MaterialTypes.DUST).get(), 1, 0.05f));
		metalOre(NewModMaterials.GOLD, StaticPowerOutputItem.of(NewModMaterials.COPPER.get(MaterialTypes.DUST).get(), 1, 0.05f));
		metalOre(NewModMaterials.COPPER, StaticPowerOutputItem.of(NewModMaterials.GOLD.get(MaterialTypes.DUST).get(), 1, 0.05f));

		metalOre(NewModMaterials.LEAD, StaticPowerOutputItem.of(NewModMaterials.TUNGSTEN.get(MaterialTypes.DUST).get(), 1, 0.05f));
		metalOre(NewModMaterials.TIN, StaticPowerOutputItem.of(NewModMaterials.SILVER.get(MaterialTypes.DUST).get(), 1, 0.05f));

		addRecipe("ores/lapis_lazuli", StaticPowerIngredient.of(RecipeItem.of(Tags.Items.ORES_LAPIS)), true, StaticPowerOutputItem.of(Items.LAPIS_LAZULI, 5, 1, 1, 0.4f));
		addRecipe("ores/redstone", StaticPowerIngredient.of(RecipeItem.of(Tags.Items.ORES_REDSTONE)), true, StaticPowerOutputItem.of(Items.REDSTONE, 4, 1, 2, 0.4f));
		addRecipe("ores/quartz", StaticPowerIngredient.of(RecipeItem.of(Tags.Items.ORES_QUARTZ)), true, StaticPowerOutputItem.of(Items.QUARTZ, 3, 1, 1, 0.4f));

		addRecipe("ores/rusty_iron", StaticPowerIngredient.of(RecipeItem.of(ModBlocks.OreRustyIron.get())),
				StaticPowerOutputItem.of(ModItems.RustyIronScrap.get(), 4, 1, 1, 0.25f));

		singleItemTodust("misc/charcoal_dust", RecipeItem.of(ModItemTags.CHARCOAL_DUST), ModItems.DustCharcoalSmall.get(), 9);
		singleItemTodust("misc/coal_dust", RecipeItem.of(ModItemTags.COAL_DUST), ModItems.DustCoalSmall.get(), 9);

		addRecipe("misc/wheat_to_flour", StaticPowerIngredient.of(RecipeItem.of(Tags.Items.CROPS_WHEAT)), StaticPowerOutputItem.of(ModItems.WheatFlour.get(), 2));
		addRecipe("misc/potato_to_flour", StaticPowerIngredient.of(RecipeItem.of(Tags.Items.CROPS_POTATO)), StaticPowerOutputItem.of(ModItems.PotatoFlour.get(), 2));

		addRecipe("misc/wool_to_string", StaticPowerIngredient.of(RecipeItem.of(ItemTags.WOOL)), StaticPowerOutputItem.of(Items.STRING, 3, 1.0f, 1, 0.5f));
		addRecipe("misc/web_to_string", StaticPowerIngredient.of(RecipeItem.of(Items.COBWEB)), StaticPowerOutputItem.of(Items.STRING, 2));

		addRecipe("misc/stone_to_cobblestone", StaticPowerIngredient.of(RecipeItem.of(Tags.Items.STONE)), StaticPowerOutputItem.of(Items.COBBLESTONE));
		addRecipe("misc/cobblestone_to_gravel", StaticPowerIngredient.of(RecipeItem.of(Tags.Items.COBBLESTONE)), StaticPowerOutputItem.of(Items.GRAVEL));
		addRecipe("misc/gravel_to_sand", StaticPowerIngredient.of(RecipeItem.of(Tags.Items.GRAVEL)), StaticPowerOutputItem.of(Items.SAND),
				StaticPowerOutputItem.of(Items.FLINT, 1, 0.25f));
		addRecipe("misc/sand_to_silicon", StaticPowerIngredient.of(RecipeItem.of(Tags.Items.SAND)), StaticPowerOutputItem.of(ModItems.RawSilicon.get()));
		addRecipe("misc/obsidian_to_dust", StaticPowerIngredient.of(RecipeItem.of(Tags.Items.OBSIDIAN)), StaticPowerOutputItem.of(ModItems.DustObsidian.get(), 8));

		addRecipe("misc/sandstone_to_sand", StaticPowerIngredient.of(RecipeItem.of(Tags.Items.SANDSTONE)), StaticPowerOutputItem.of(Items.SAND, 4));
		addRecipe("misc/glass_to_sand", StaticPowerIngredient.of(RecipeItem.of(Tags.Items.GLASS)), StaticPowerOutputItem.of(Items.SAND));
		addRecipe("misc/glowstone_block_to_dust", StaticPowerIngredient.of(RecipeItem.of(Blocks.GLOWSTONE)), StaticPowerOutputItem.of(Items.GLOWSTONE_DUST, 3, 1, 1, 0.5f));
		addRecipe("misc/bone_to_bonemeal", StaticPowerIngredient.of(RecipeItem.of(Items.BONE)), StaticPowerOutputItem.of(Items.BONE_MEAL, 5, 1, 1, 0.25f));
		addRecipe("misc/blazerod_to_powder", StaticPowerIngredient.of(RecipeItem.of(Tags.Items.RODS_BLAZE)), StaticPowerOutputItem.of(Items.BLAZE_POWDER, 4, 1, 1, 0.5f));
	}

	protected void singleItemTodust(String nameOverride, RecipeItem input, ItemLike result) {
		singleItemTodust(nameOverride, input, result, 1);
	}

	protected void singleItemTodust(String nameOverride, RecipeItem input, ItemLike result, int count) {
		addRecipe(nameOverride, StaticPowerIngredient.of(input), StaticPowerOutputItem.of(result, count));
	}

	protected void metalOre(MaterialBundle bundle, StaticPowerOutputItem... extraOutputs) {
		StaticPowerOutputItem[] dustResult = new StaticPowerOutputItem[] { StaticPowerOutputItem.of(bundle.get(MaterialTypes.DUST).get(), 2) };
		StaticPowerOutputItem[] result = Arrays.copyOf(dustResult, dustResult.length + extraOutputs.length);
		System.arraycopy(extraOutputs, 0, result, dustResult.length, extraOutputs.length);

		addRecipe("ores/" + bundle.getName(), StaticPowerIngredient.of(bundle.getOreItemTag()), true, result);
		addRecipe("raw_materials/" + bundle.getName(), StaticPowerIngredient.of(bundle.get(MaterialTypes.RAW_MATERIAL).getItemTag()), true, result);
	}

	protected void addRecipe(String nameOverride, StaticPowerIngredient input, StaticPowerOutputItem... outputs) {
		addRecipe(nameOverride, input, false, outputs);
	}

	protected void addRecipe(String nameOverride, StaticPowerIngredient input, boolean replace, StaticPowerOutputItem... outputs) {
		List<StaticPowerOutputItem> outputsList = new LinkedList<StaticPowerOutputItem>();
		for (StaticPowerOutputItem item : outputs) {
			outputsList.add(item);
		}
		GrinderRecipe recipe = new GrinderRecipe(null, input, null, outputsList);
		addRecipe(nameOverride, SPRecipeBuilder.create(recipe), replace);
	}
}
