package theking530.staticpower.data.generators.recipes;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.data.DataGenerator;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import theking530.staticpower.data.MaterialBundle;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.StaticPowerOutputItem;
import theking530.staticpower.data.crafting.wrappers.grinder.GrinderRecipe;
import theking530.staticpower.data.generators.RecipeItem;
import theking530.staticpower.data.generators.helpers.SPRecipeBuilder;
import theking530.staticpower.data.generators.helpers.SPRecipeProvider;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModItems;
import theking530.staticpower.init.ModMaterials;
import theking530.staticpower.init.tags.ModItemTags;

public class GrindingRecipeGenerator extends SPRecipeProvider<GrinderRecipe> {

	public GrindingRecipeGenerator(DataGenerator dataGenerator) {
		super("grinding", dataGenerator);
	}

	@Override
	protected void buildRecipes() {
		metalOre(ModMaterials.IRON, StaticPowerOutputItem.of(ModMaterials.ZINC.getDust().get(), 1, 0.05f));
		metalOre(ModMaterials.GOLD, StaticPowerOutputItem.of(ModMaterials.COPPER.getDust().get(), 1, 0.05f));
		metalOre(ModMaterials.COPPER, StaticPowerOutputItem.of(ModMaterials.GOLD.getDust().get(), 1, 0.05f));

		metalOre(ModMaterials.LEAD, StaticPowerOutputItem.of(ModMaterials.TUNGSTEN.getDust().get(), 1, 0.05f));
		metalOre(ModMaterials.TIN, StaticPowerOutputItem.of(ModMaterials.SILVER.getDust().get(), 1, 0.05f));

		metalOre(ModMaterials.URANIUM);
		metalOre(ModMaterials.PLATINUM);
		metalOre(ModMaterials.ZINC);
		metalOre(ModMaterials.ALUMINUM);
		metalOre(ModMaterials.MAGNESIUM);
		metalOre(ModMaterials.TUNGSTEN);
		metalOre(ModMaterials.SILVER);

		gemOre(ModMaterials.RUBY, ModMaterials.RUBY.getRawMaterial().get());
		gemOre(ModMaterials.SAPPHIRE, ModMaterials.SAPPHIRE.getRawMaterial().get());
		gemOre(ModMaterials.DIAMOND, Items.DIAMOND);
		gemOre(ModMaterials.EMERALD, Items.EMERALD);

		addRecipe("ores/lapis_lazuli", StaticPowerIngredient.of(RecipeItem.of(Tags.Items.ORES_LAPIS)), StaticPowerOutputItem.of(Items.LAPIS_LAZULI, 5, 1, 1, 0.4f));
		addRecipe("ores/redstone", StaticPowerIngredient.of(RecipeItem.of(Tags.Items.ORES_REDSTONE)), StaticPowerOutputItem.of(Items.REDSTONE, 4, 1, 2, 0.4f));
		addRecipe("ores/coal", StaticPowerIngredient.of(RecipeItem.of(Tags.Items.ORES_COAL)), StaticPowerOutputItem.of(Items.COAL, 2, 1, 1, 0.25f));
		addRecipe("ores/quartz", StaticPowerIngredient.of(RecipeItem.of(Tags.Items.ORES_QUARTZ)), StaticPowerOutputItem.of(Items.QUARTZ, 3, 1, 1, 0.4f));
		addRecipe("ores/rusty_iron", StaticPowerIngredient.of(RecipeItem.of(ModBlocks.OreRustyIron.get())),
				StaticPowerOutputItem.of(ModItems.RustyIronScrap.get(), 4, 1, 1, 0.25f));

		for (MaterialBundle material : ModMaterials.MATERIALS.values()) {
			if (material.shouldGenerateIngot() && material.shouldGenerateDust()) {
				singleItemTodust("ingots/" + material.getName(), RecipeItem.of(material.getIngotTag()), material.getDust().get());
			}
		}
		singleItemTodust("gems/" + ModMaterials.RUBY.getName(), RecipeItem.of(ModMaterials.RUBY.getRawMaterialTag()), ModMaterials.RUBY.getDust().get());
		singleItemTodust("gems/" + ModMaterials.SAPPHIRE.getName(), RecipeItem.of(ModMaterials.SAPPHIRE.getRawMaterialTag()), ModMaterials.SAPPHIRE.getDust().get());
		singleItemTodust("gems/" + ModMaterials.DIAMOND.getName(), RecipeItem.of(Tags.Items.GEMS_DIAMOND), ModMaterials.DIAMOND.getDust().get());
		singleItemTodust("gems/" + ModMaterials.EMERALD.getName(), RecipeItem.of(Tags.Items.GEMS_EMERALD), ModMaterials.EMERALD.getDust().get());

		for (MaterialBundle material : ModMaterials.MATERIALS.values()) {
			if (material.shouldGenerateStorageBlock() && material.shouldGenerateDust()) {
				singleItemTodust("blocks/" + material.getName(), RecipeItem.of(material.getStorageBlockItemTag()), 9, material.getDust().get());
			}
		}
		singleItemTodust("blocks/" + ModMaterials.RUBY.getName(), RecipeItem.of(ModMaterials.RUBY.getRawStorageBlockItemTag()), 9, ModMaterials.RUBY.getDust().get());
		singleItemTodust("blocks/" + ModMaterials.SAPPHIRE.getName(), RecipeItem.of(ModMaterials.SAPPHIRE.getRawStorageBlockItemTag()), 9, ModMaterials.SAPPHIRE.getDust().get());
		singleItemTodust("blocks/" + ModMaterials.DIAMOND.getName(), RecipeItem.of(Tags.Items.STORAGE_BLOCKS_DIAMOND), 9, ModMaterials.DIAMOND.getDust().get());
		singleItemTodust("blocks/" + ModMaterials.EMERALD.getName(), RecipeItem.of(Tags.Items.STORAGE_BLOCKS_EMERALD), 9, ModMaterials.EMERALD.getDust().get());

		singleItemTodust("misc/charcoal", RecipeItem.of(Items.CHARCOAL), ModItems.DustCharcoal.get());
		singleItemTodust("misc/charcoal_dust", RecipeItem.of(ModItemTags.CHARCOAL_DUST), 9, ModItems.DustCharcoalSmall.get());

		singleItemTodust("misc/coal", RecipeItem.of(Items.COAL), ModItems.DustCoal.get());
		singleItemTodust("misc/coal_dust", RecipeItem.of(ModItemTags.COAL_DUST), 9, ModItems.DustCoalSmall.get());

		addRecipe("misc/wheat_to_flour", StaticPowerIngredient.of(RecipeItem.of(Tags.Items.CROPS_WHEAT)), StaticPowerOutputItem.of(ModItems.WheatFlour.get(), 2));
		addRecipe("misc/potato_to_flour", StaticPowerIngredient.of(RecipeItem.of(Tags.Items.CROPS_POTATO)), StaticPowerOutputItem.of(ModItems.PotatoFlour.get(), 2));

		addRecipe("misc/wool_to_string", StaticPowerIngredient.of(RecipeItem.of(ItemTags.WOOL)), StaticPowerOutputItem.of(Items.STRING, 3, 1.0f, 1, 0.5f));
		addRecipe("misc/web_to_string", StaticPowerIngredient.of(RecipeItem.of(Items.COBWEB)), StaticPowerOutputItem.of(Items.STRING, 2));

		addRecipe("misc/stone_to_cobblestone", StaticPowerIngredient.of(RecipeItem.of(Tags.Items.STONE)), StaticPowerOutputItem.of(Items.COBBLESTONE));
		addRecipe("misc/cobblestone_to_gravel", StaticPowerIngredient.of(RecipeItem.of(Tags.Items.COBBLESTONE)), StaticPowerOutputItem.of(Items.GRAVEL));
		addRecipe("misc/gravel_to_sand", StaticPowerIngredient.of(RecipeItem.of(Tags.Items.GRAVEL)), StaticPowerOutputItem.of(Items.SAND),
				StaticPowerOutputItem.of(Items.FLINT, 1, 0.25f));
		addRecipe("misc/sand_to_silicon", StaticPowerIngredient.of(RecipeItem.of(Tags.Items.SAND)), StaticPowerOutputItem.of(ModItems.Silicon.get()));
		addRecipe("misc/obsidian_to_dust", StaticPowerIngredient.of(RecipeItem.of(Tags.Items.OBSIDIAN)), StaticPowerOutputItem.of(ModItems.DustObsidian.get(), 8));

		addRecipe("misc/sandstone_to_sand", StaticPowerIngredient.of(RecipeItem.of(Tags.Items.SANDSTONE)), StaticPowerOutputItem.of(Items.SAND, 4));
		addRecipe("misc/glass_to_sand", StaticPowerIngredient.of(RecipeItem.of(Tags.Items.GLASS)), StaticPowerOutputItem.of(Items.SAND));
		addRecipe("misc/glowstone_block_to_dust", StaticPowerIngredient.of(RecipeItem.of(Blocks.GLOWSTONE)), StaticPowerOutputItem.of(Items.GLOWSTONE_DUST, 3, 1, 1, 0.5f));
		addRecipe("misc/bone_to_bonemeal", StaticPowerIngredient.of(RecipeItem.of(Items.BONE)), StaticPowerOutputItem.of(Items.BONE_MEAL, 5, 1, 1, 0.25f));
		addRecipe("misc/blazerod_to_powder", StaticPowerIngredient.of(RecipeItem.of(Tags.Items.RODS_BLAZE)), StaticPowerOutputItem.of(Items.BLAZE_POWDER, 4, 1, 1, 0.5f));
	}

	protected void singleItemTodust(String nameOverride, RecipeItem input, ItemLike result) {
		singleItemTodust(nameOverride, input, 1, result);
	}

	protected void singleItemTodust(String nameOverride, RecipeItem input, int count, ItemLike result) {
		addRecipe("dusts/" + nameOverride, StaticPowerIngredient.of(input), StaticPowerOutputItem.of(result, count));
	}

	protected void gemOre(MaterialBundle bundle, Item output) {
		addRecipe("ores/" + bundle.getName(), StaticPowerIngredient.of(bundle.getOreItemTag()), StaticPowerOutputItem.of(output, 3, 1, 1, 0.25f),
				StaticPowerOutputItem.of(bundle.getDust().get(), 1, 0.1f));
	}

	protected void metalOre(MaterialBundle bundle, StaticPowerOutputItem... extraOutputs) {
		StaticPowerOutputItem[] dustResult = new StaticPowerOutputItem[] { StaticPowerOutputItem.of(bundle.getDust().get(), 2) };
		StaticPowerOutputItem[] result = Arrays.copyOf(dustResult, dustResult.length + extraOutputs.length);
		System.arraycopy(extraOutputs, 0, result, dustResult.length, extraOutputs.length);

		addRecipe("ores/" + bundle.getName(), StaticPowerIngredient.of(bundle.getOreItemTag()), result);
		addRecipe("raw_materials/" + bundle.getName(), StaticPowerIngredient.of(bundle.getRawMaterialTag()), result);
	}

	protected void addRecipe(String nameOverride, StaticPowerIngredient input, StaticPowerOutputItem... outputs) {
		List<StaticPowerOutputItem> outputsList = new LinkedList<StaticPowerOutputItem>();
		for (StaticPowerOutputItem item : outputs) {
			outputsList.add(item);
		}
		GrinderRecipe recipe = new GrinderRecipe(null, input, null, outputsList);
		addRecipe(nameOverride, SPRecipeBuilder.create(recipe));
	}
}
