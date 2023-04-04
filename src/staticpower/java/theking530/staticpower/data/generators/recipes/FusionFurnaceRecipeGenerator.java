package theking530.staticpower.data.generators.recipes;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;
import theking530.staticcore.crafting.MachineRecipeProcessingSection;
import theking530.staticcore.crafting.StaticPowerIngredient;
import theking530.staticcore.crafting.StaticPowerOutputItem;
import theking530.staticcore.data.generators.helpers.SCRecipeBuilder;
import theking530.staticcore.data.generators.helpers.SCRecipeProvider;
import theking530.staticpower.data.crafting.wrappers.alloyfurnace.AlloyFurnaceRecipe;
import theking530.staticpower.data.crafting.wrappers.fusionfurnace.FusionFurnaceRecipe;
import theking530.staticpower.data.materials.MaterialTypes;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModMaterials;
import theking530.staticpower.init.tags.ModItemTags;

public class FusionFurnaceRecipeGenerator extends SCRecipeProvider<FusionFurnaceRecipe> {

	public FusionFurnaceRecipeGenerator(DataGenerator dataGenerator) {
		super("fusion_furnace", dataGenerator);
	}

	@Override
	protected void buildRecipes() {
		addRecipe("metal/brass_nugget", StaticPowerOutputItem.of(ModMaterials.BRASS.get(MaterialTypes.NUGGET).get()), AlloyFurnaceRecipe.DEFAULT_PROCESSING_TIME / 9,
				StaticPowerIngredient.of(ModMaterials.COPPER.get(MaterialTypes.NUGGET).getItemTag()),
				StaticPowerIngredient.of(ModMaterials.ZINC.get(MaterialTypes.NUGGET).getItemTag()));
		addRecipe("metal/brass_raw_material", StaticPowerOutputItem.of(ModMaterials.BRASS.get(MaterialTypes.RAW_MATERIAL).get()),
				StaticPowerIngredient.of(Tags.Items.RAW_MATERIALS_COPPER), StaticPowerIngredient.of(ModMaterials.ZINC.get(MaterialTypes.RAW_MATERIAL).getItemTag()));
		addRecipe("metal/brass_ingot", StaticPowerOutputItem.of(ModMaterials.BRASS.get(MaterialTypes.INGOT).get()), StaticPowerIngredient.of(Tags.Items.INGOTS_COPPER),
				StaticPowerIngredient.of(ModMaterials.ZINC.get(MaterialTypes.INGOT).getItemTag()));
		addRecipe("metal/brass_block", StaticPowerOutputItem.of(ModMaterials.BRASS.get(MaterialTypes.STORAGE_BLOCK).get()), AlloyFurnaceRecipe.DEFAULT_PROCESSING_TIME * 9,
				StaticPowerIngredient.of(Tags.Items.STORAGE_BLOCKS_COPPER), StaticPowerIngredient.of(ModMaterials.ZINC.get(MaterialTypes.STORAGE_BLOCK).getItemTag()));

		addRecipe("metal/bronze_nugget", StaticPowerOutputItem.of(ModMaterials.BRONZE.get(MaterialTypes.NUGGET).get()), AlloyFurnaceRecipe.DEFAULT_PROCESSING_TIME / 9,
				StaticPowerIngredient.of(ModMaterials.COPPER.get(MaterialTypes.NUGGET).getItemTag(), 3),
				StaticPowerIngredient.of(ModMaterials.ZINC.get(MaterialTypes.NUGGET).getItemTag()));
		addRecipe("metal/bronze_raw_material", StaticPowerOutputItem.of(ModMaterials.BRONZE.get(MaterialTypes.RAW_MATERIAL).get()),
				StaticPowerIngredient.of(Tags.Items.RAW_MATERIALS_COPPER, 3), StaticPowerIngredient.of(ModMaterials.ZINC.get(MaterialTypes.RAW_MATERIAL).getItemTag()));
		addRecipe("metal/bronze_ingot", StaticPowerOutputItem.of(ModMaterials.BRONZE.get(MaterialTypes.INGOT).get()), StaticPowerIngredient.of(Tags.Items.INGOTS_COPPER, 3),
				StaticPowerIngredient.of(ModMaterials.ZINC.get(MaterialTypes.INGOT).getItemTag()));
		addRecipe("metal/bronze_block", StaticPowerOutputItem.of(ModMaterials.BRONZE.get(MaterialTypes.STORAGE_BLOCK).get()), AlloyFurnaceRecipe.DEFAULT_PROCESSING_TIME * 9,
				StaticPowerIngredient.of(Tags.Items.STORAGE_BLOCKS_COPPER, 3), StaticPowerIngredient.of(ModMaterials.ZINC.get(MaterialTypes.STORAGE_BLOCK).getItemTag()));

		addRecipe("metal/redstone_alloy_raw_material", StaticPowerOutputItem.of(ModMaterials.REDSTONE_ALLOY.get(MaterialTypes.RAW_MATERIAL).get()),
				StaticPowerIngredient.of(Tags.Items.DUSTS_REDSTONE), StaticPowerIngredient.of(ModMaterials.SILVER.get(MaterialTypes.RAW_MATERIAL).getItemTag()));
		addRecipe("metal/redstone_alloy_ingot", StaticPowerOutputItem.of(ModMaterials.REDSTONE_ALLOY.get(MaterialTypes.INGOT).get()),
				StaticPowerIngredient.of(Tags.Items.DUSTS_REDSTONE), StaticPowerIngredient.of(ModMaterials.SILVER.get(MaterialTypes.INGOT).getItemTag()));
		addRecipe("metal/redstone_alloy_block", StaticPowerOutputItem.of(ModMaterials.REDSTONE_ALLOY.get(MaterialTypes.STORAGE_BLOCK).get()),
				AlloyFurnaceRecipe.DEFAULT_PROCESSING_TIME * 9, StaticPowerIngredient.of(Tags.Items.STORAGE_BLOCKS_REDSTONE),
				StaticPowerIngredient.of(ModMaterials.SILVER.get(MaterialTypes.STORAGE_BLOCK).getItemTag()));

		addRecipe("metal/inert_infusion_nugget", StaticPowerOutputItem.of(ModMaterials.INERT_INFUSION.get(MaterialTypes.NUGGET).get()), AlloyFurnaceRecipe.DEFAULT_PROCESSING_TIME / 9,
				StaticPowerIngredient.of(ModMaterials.COPPER.get(MaterialTypes.NUGGET).getItemTag()), StaticPowerIngredient.of(Tags.Items.NUGGETS_GOLD),
				StaticPowerIngredient.of(ModMaterials.PLATINUM.get(MaterialTypes.NUGGET).getItemTag()));
		addRecipe("metal/inert_infusion_raw_material", StaticPowerOutputItem.of(ModMaterials.INERT_INFUSION.get(MaterialTypes.RAW_MATERIAL).get()),
				StaticPowerIngredient.of(Tags.Items.RAW_MATERIALS_COPPER), StaticPowerIngredient.of(Tags.Items.RAW_MATERIALS_GOLD),
				StaticPowerIngredient.of(ModMaterials.PLATINUM.get(MaterialTypes.RAW_MATERIAL).getItemTag()));
		addRecipe("metal/inert_infusion_ingot", StaticPowerOutputItem.of(ModMaterials.INERT_INFUSION.get(MaterialTypes.INGOT).get()),
				StaticPowerIngredient.of(Tags.Items.INGOTS_COPPER), StaticPowerIngredient.of(Tags.Items.INGOTS_GOLD),
				StaticPowerIngredient.of(ModMaterials.PLATINUM.get(MaterialTypes.INGOT).getItemTag()));
		addRecipe("metal/inert_infusion_block", StaticPowerOutputItem.of(ModMaterials.INERT_INFUSION.get(MaterialTypes.STORAGE_BLOCK).get()),
				AlloyFurnaceRecipe.DEFAULT_PROCESSING_TIME * 9, StaticPowerIngredient.of(Tags.Items.STORAGE_BLOCKS_COPPER),
				StaticPowerIngredient.of(Tags.Items.STORAGE_BLOCKS_GOLD), StaticPowerIngredient.of(ModMaterials.PLATINUM.get(MaterialTypes.STORAGE_BLOCK).getItemTag()));

		addRecipe("gems/diamond", StaticPowerOutputItem.of(Items.DIAMOND), StaticPowerIngredient.of(ModMaterials.DIAMOND.get(MaterialTypes.DUST).getItemTag()),
				StaticPowerIngredient.of(ModMaterials.CHARCOAL.get(MaterialTypes.DUST).getItemTag(), ModMaterials.COAL.get(MaterialTypes.DUST).getItemTag()));
		addRecipe("gems/emerald", StaticPowerOutputItem.of(Items.EMERALD), StaticPowerIngredient.of(ModMaterials.EMERALD.get(MaterialTypes.DUST).getItemTag()),
				StaticPowerIngredient.of(ModMaterials.CHARCOAL.get(MaterialTypes.DUST).getItemTag(), ModMaterials.COAL.get(MaterialTypes.DUST).getItemTag()));
		addRecipe("gems/ruby", StaticPowerOutputItem.of(ModMaterials.RUBY.get(MaterialTypes.RAW_MATERIAL).get()),
				StaticPowerIngredient.of(ModMaterials.RUBY.get(MaterialTypes.DUST).getItemTag()),
				StaticPowerIngredient.of(ModMaterials.CHARCOAL.get(MaterialTypes.DUST).getItemTag(), ModMaterials.COAL.get(MaterialTypes.DUST).getItemTag()));
		addRecipe("gems/sapphire", StaticPowerOutputItem.of(ModMaterials.SAPPHIRE.get(MaterialTypes.RAW_MATERIAL).get()),
				StaticPowerIngredient.of(ModMaterials.SAPPHIRE.get(MaterialTypes.DUST).getItemTag()),
				StaticPowerIngredient.of(ModMaterials.CHARCOAL.get(MaterialTypes.DUST).getItemTag(), ModMaterials.COAL.get(MaterialTypes.DUST).getItemTag()));

		addRecipe("obsidian_glass", StaticPowerOutputItem.of(ModBlocks.ObsidianGlass.get(), 4), StaticPowerIngredient.of(Tags.Items.GLASS),
				StaticPowerIngredient.of(ModMaterials.LEAD.get(MaterialTypes.DUST).getItemTag()), StaticPowerIngredient.of(ModItemTags.OBSIDIAN_DUST, 8));
	}

	protected void addRecipe(String nameOverride, StaticPowerOutputItem output, StaticPowerIngredient... inputs) {
		addRecipe(nameOverride, output, null, inputs);
	}

	protected void addRecipe(String nameOverride, StaticPowerOutputItem output, int processingTime, StaticPowerIngredient... inputs) {
		addRecipe(nameOverride, output, MachineRecipeProcessingSection.hardcoded(processingTime, FusionFurnaceRecipe.DEFAULT_POWER_COST, 0, 0), inputs);
	}

	protected void addRecipe(String nameOverride, StaticPowerOutputItem output, MachineRecipeProcessingSection processing, StaticPowerIngredient... inputs) {
		List<StaticPowerIngredient> inputList = new LinkedList<StaticPowerIngredient>();
		for (StaticPowerIngredient item : inputs) {
			inputList.add(item);
		}
		FusionFurnaceRecipe recipe = new FusionFurnaceRecipe(null, inputList, output, processing);
		addRecipe(nameOverride, SCRecipeBuilder.create(recipe));
	}
}
