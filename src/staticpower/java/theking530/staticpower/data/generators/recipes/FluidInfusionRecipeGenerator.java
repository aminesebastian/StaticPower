package theking530.staticpower.data.generators.recipes;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.data.DataGenerator;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.Tags;
import theking530.staticcore.crafting.MachineRecipeProcessingSection;
import theking530.staticcore.crafting.StaticPowerIngredient;
import theking530.staticcore.crafting.StaticPowerOutputItem;
import theking530.staticcore.data.generators.helpers.SCRecipeBuilder;
import theking530.staticcore.data.generators.helpers.SCRecipeProvider;
import theking530.staticcore.fluid.FluidIngredient;
import theking530.staticpower.data.crafting.wrappers.fluidinfusion.FluidInfusionRecipe;
import theking530.staticpower.data.materials.IMaterialType;
import theking530.staticpower.data.materials.MaterialBundle;
import theking530.staticpower.data.materials.MaterialTypes;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModFluids;
import theking530.staticpower.init.ModItems;
import theking530.staticpower.init.ModMaterials;
import theking530.staticpower.init.tags.ModItemTags;

public class FluidInfusionRecipeGenerator extends SCRecipeProvider<FluidInfusionRecipe> {

	public FluidInfusionRecipeGenerator(DataGenerator dataGenerator) {
		super("fluid_infusion", dataGenerator);
	}

	@Override
	protected void buildRecipes() {
		addRecipe("clay", StaticPowerIngredient.of(Tags.Items.GRAVEL), FluidIngredient.of(FluidTags.WATER, 1000), StaticPowerOutputItem.of(Items.CLAY));

		addRecipe("tools/static_wrench", StaticPowerIngredient.of(ModItems.Wrench.get()), FluidIngredient.of(ModFluids.StaticFluid.getTag(), 1000),
				StaticPowerOutputItem.of(ModItems.StaticWrench.get()));
		addRecipe("tools/energized_wrench", StaticPowerIngredient.of(ModItems.Wrench.get()), FluidIngredient.of(ModFluids.EnergizedFluid.getTag(), 1000),
				StaticPowerOutputItem.of(ModItems.EnergizedWrench.get()));
		addRecipe("tools/lumum_wrench", StaticPowerIngredient.of(ModItems.Wrench.get()), FluidIngredient.of(ModFluids.LumumFluid.getTag(), 1000),
				StaticPowerOutputItem.of(ModItems.LumumWrench.get()));

		addRecipe("wood/static_log", StaticPowerIngredient.of(ItemTags.LOGS), FluidIngredient.of(ModFluids.StaticFluid.getTag(), 500),
				StaticPowerOutputItem.of(ModBlocks.StaticLog.get()));
		addRecipe("wood/energized_log", StaticPowerIngredient.of(ItemTags.LOGS), FluidIngredient.of(ModFluids.EnergizedFluid.getTag(), 500),
				StaticPowerOutputItem.of(ModBlocks.EnergizedLog.get()));
		addRecipe("wood/lumum_log", StaticPowerIngredient.of(ItemTags.LOGS), FluidIngredient.of(ModFluids.LumumFluid.getTag(), 500),
				StaticPowerOutputItem.of(ModBlocks.LumumLog.get()));

		addRecipe("grass/static_grass", StaticPowerIngredient.of(Items.GRASS_BLOCK), FluidIngredient.of(ModFluids.StaticFluid.getTag(), 1000),
				StaticPowerOutputItem.of(ModBlocks.StaticGrass.get()));
		addRecipe("grass/energized_grass", StaticPowerIngredient.of(Items.GRASS_BLOCK), FluidIngredient.of(ModFluids.EnergizedFluid.getTag(), 1000),
				StaticPowerOutputItem.of(ModBlocks.EnergizedGrass.get()));

		List<Tuple<MaterialBundle, Tuple<MaterialBundle, TagKey<Fluid>>>> materialPairs = new ArrayList<>();
		materialPairs.add(new Tuple<>(ModMaterials.COPPER, new Tuple<>(ModMaterials.STATIC_METAL, ModFluids.StaticFluid.getTag())));
		materialPairs.add(new Tuple<>(ModMaterials.GOLD, new Tuple<>(ModMaterials.ENERGIZED_METAL, ModFluids.EnergizedFluid.getTag())));
		materialPairs.add(new Tuple<>(ModMaterials.PLATINUM, new Tuple<>(ModMaterials.LUMUM_METAL, ModFluids.LumumFluid.getTag())));

		for (Tuple<MaterialBundle, Tuple<MaterialBundle, TagKey<Fluid>>> tuple : materialPairs) {
			addMetalInfusion(MaterialTypes.NUGGET, tuple.getA(), tuple.getB().getA(), tuple.getB().getB(), 16);
			addMetalInfusion(MaterialTypes.DUST, tuple.getA(), tuple.getB().getA(), tuple.getB().getB(), 144);
			addMetalInfusion(MaterialTypes.INGOT, tuple.getA(), tuple.getB().getA(), tuple.getB().getB(), 144);
			addMetalInfusion(MaterialTypes.RAW_MATERIAL, tuple.getA(), tuple.getB().getA(), tuple.getB().getB(), 1296);
			addMetalInfusion(MaterialTypes.RAW_STOARGE_BLOCK, tuple.getA(), tuple.getB().getA(), tuple.getB().getB(), 1296);
			addMetalInfusion(MaterialTypes.STORAGE_BLOCK, tuple.getA(), tuple.getB().getA(), tuple.getB().getB(), 1296);
		}

		addRecipe("silicon/static", StaticPowerIngredient.of(ModItemTags.SILICON), FluidIngredient.of(ModFluids.StaticFluid.getTag(), 100),
				StaticPowerOutputItem.of(ModItems.StaticDopedSilicon.get()));
		addRecipe("silicon/energized", StaticPowerIngredient.of(ModItemTags.SILICON), FluidIngredient.of(ModFluids.EnergizedFluid.getTag(), 100),
				StaticPowerOutputItem.of(ModItems.EnergizedDopedSilicon.get()));
		addRecipe("silicon/lumum", StaticPowerIngredient.of(ModItemTags.SILICON), FluidIngredient.of(ModFluids.LumumFluid.getTag(), 100),
				StaticPowerOutputItem.of(ModItems.LumumDopedSilicon.get()));

		addRecipe("concrete/white", StaticPowerIngredient.of(Items.WHITE_CONCRETE_POWDER), FluidIngredient.of(FluidTags.WATER, 1000),
				StaticPowerOutputItem.of(Blocks.WHITE_CONCRETE));
		addRecipe("concrete/orange", StaticPowerIngredient.of(Items.ORANGE_CONCRETE_POWDER), FluidIngredient.of(FluidTags.WATER, 1000),
				StaticPowerOutputItem.of(Blocks.ORANGE_CONCRETE));
		addRecipe("concrete/magenta", StaticPowerIngredient.of(Items.MAGENTA_CONCRETE_POWDER), FluidIngredient.of(FluidTags.WATER, 1000),
				StaticPowerOutputItem.of(Blocks.MAGENTA_CONCRETE));
		addRecipe("concrete/light_blue", StaticPowerIngredient.of(Items.LIGHT_BLUE_CONCRETE_POWDER), FluidIngredient.of(FluidTags.WATER, 1000),
				StaticPowerOutputItem.of(Blocks.LIGHT_BLUE_CONCRETE));
		addRecipe("concrete/yellow", StaticPowerIngredient.of(Items.YELLOW_CONCRETE_POWDER), FluidIngredient.of(FluidTags.WATER, 1000),
				StaticPowerOutputItem.of(Blocks.YELLOW_CONCRETE));
		addRecipe("concrete/lime", StaticPowerIngredient.of(Items.LIME_CONCRETE_POWDER), FluidIngredient.of(FluidTags.WATER, 1000), StaticPowerOutputItem.of(Blocks.LIME_CONCRETE));
		addRecipe("concrete/pink", StaticPowerIngredient.of(Items.PINK_CONCRETE_POWDER), FluidIngredient.of(FluidTags.WATER, 1000), StaticPowerOutputItem.of(Blocks.PINK_CONCRETE));
		addRecipe("concrete/gray", StaticPowerIngredient.of(Items.GRAY_CONCRETE_POWDER), FluidIngredient.of(FluidTags.WATER, 1000), StaticPowerOutputItem.of(Blocks.GRAY_CONCRETE));
		addRecipe("concrete/light_gray", StaticPowerIngredient.of(Items.LIGHT_GRAY_CONCRETE_POWDER), FluidIngredient.of(FluidTags.WATER, 1000),
				StaticPowerOutputItem.of(Blocks.LIGHT_GRAY_CONCRETE));
		addRecipe("concrete/cyan", StaticPowerIngredient.of(Items.CYAN_CONCRETE_POWDER), FluidIngredient.of(FluidTags.WATER, 1000), StaticPowerOutputItem.of(Blocks.CYAN_CONCRETE));
		addRecipe("concrete/purple", StaticPowerIngredient.of(Items.PURPLE_CONCRETE_POWDER), FluidIngredient.of(FluidTags.WATER, 1000),
				StaticPowerOutputItem.of(Blocks.PURPLE_CONCRETE));
		addRecipe("concrete/blue", StaticPowerIngredient.of(Items.BLUE_CONCRETE_POWDER), FluidIngredient.of(FluidTags.WATER, 1000), StaticPowerOutputItem.of(Blocks.BLUE_CONCRETE));
		addRecipe("concrete/brown", StaticPowerIngredient.of(Items.BROWN_CONCRETE_POWDER), FluidIngredient.of(FluidTags.WATER, 1000),
				StaticPowerOutputItem.of(Blocks.BROWN_CONCRETE));
		addRecipe("concrete/green", StaticPowerIngredient.of(Items.GREEN_CONCRETE_POWDER), FluidIngredient.of(FluidTags.WATER, 1000),
				StaticPowerOutputItem.of(Blocks.GREEN_CONCRETE));
		addRecipe("concrete/red", StaticPowerIngredient.of(Items.RED_CONCRETE_POWDER), FluidIngredient.of(FluidTags.WATER, 1000), StaticPowerOutputItem.of(Blocks.RED_CONCRETE));
		addRecipe("concrete/black", StaticPowerIngredient.of(Items.BLACK_CONCRETE_POWDER), FluidIngredient.of(FluidTags.WATER, 1000),
				StaticPowerOutputItem.of(Blocks.BLACK_CONCRETE));

		addRecipe("misc/exposed_copper", StaticPowerIngredient.of(Items.COPPER_BLOCK), FluidIngredient.of(FluidTags.WATER, 100), StaticPowerOutputItem.of(Items.EXPOSED_COPPER));
		addRecipe("misc/weathered_copper", StaticPowerIngredient.of(Items.WEATHERED_COPPER), FluidIngredient.of(FluidTags.WATER, 100),
				StaticPowerOutputItem.of(Items.WEATHERED_COPPER));
		addRecipe("misc/oxidizeds_copper", StaticPowerIngredient.of(Items.EXPOSED_COPPER), FluidIngredient.of(FluidTags.WATER, 100),
				StaticPowerOutputItem.of(Items.OXIDIZED_COPPER));

		addRecipe("misc/exposed_cut_copper", StaticPowerIngredient.of(Items.CUT_COPPER), FluidIngredient.of(FluidTags.WATER, 100),
				StaticPowerOutputItem.of(Items.EXPOSED_CUT_COPPER));
		addRecipe("misc/weathered_cut_copper", StaticPowerIngredient.of(Items.WEATHERED_CUT_COPPER), FluidIngredient.of(FluidTags.WATER, 100),
				StaticPowerOutputItem.of(Items.WEATHERED_CUT_COPPER));
		addRecipe("misc/oxidizeds_cut_copper", StaticPowerIngredient.of(Items.EXPOSED_CUT_COPPER), FluidIngredient.of(FluidTags.WATER, 100),
				StaticPowerOutputItem.of(Items.OXIDIZED_CUT_COPPER));

		addRecipe("misc/exposed_cut_copper_stairs", StaticPowerIngredient.of(Items.CUT_COPPER_STAIRS), FluidIngredient.of(FluidTags.WATER, 100),
				StaticPowerOutputItem.of(Items.EXPOSED_CUT_COPPER_STAIRS));
		addRecipe("misc/weathered_cut_copper_stairs", StaticPowerIngredient.of(Items.WEATHERED_CUT_COPPER_STAIRS), FluidIngredient.of(FluidTags.WATER, 100),
				StaticPowerOutputItem.of(Items.WEATHERED_CUT_COPPER_STAIRS));
		addRecipe("misc/oxidizeds_cut_copper_stairs", StaticPowerIngredient.of(Items.EXPOSED_CUT_COPPER_STAIRS), FluidIngredient.of(FluidTags.WATER, 100),
				StaticPowerOutputItem.of(Items.OXIDIZED_CUT_COPPER_STAIRS));
	}

	public void addMetalInfusion(IMaterialType<?> type, MaterialBundle inputMaterial, MaterialBundle outputMaterial, TagKey<Fluid> fluid, int amount) {
		String name = "metal/" + outputMaterial.getName() + "_" + type.getName();
		addRecipe(name, StaticPowerIngredient.of(inputMaterial.get(type).getItemTag()), FluidIngredient.of(fluid, amount),
				StaticPowerOutputItem.of((ItemLike) outputMaterial.get(type).get()));
		addRecipe(name + "_from_inert", StaticPowerIngredient.of(ModMaterials.INERT_INFUSION.get(type).getItemTag()), FluidIngredient.of(fluid, amount),
				StaticPowerOutputItem.of((ItemLike) outputMaterial.get(type).get()));
	}

	protected void addRecipe(String nameOverride, StaticPowerIngredient input, FluidIngredient inputFluid, StaticPowerOutputItem output) {
		addRecipe(nameOverride, input, inputFluid, output, null);
	}

	protected void addRecipe(String nameOverride, StaticPowerIngredient input, FluidIngredient inputFluid, StaticPowerOutputItem output, int processingTime) {
		addRecipe(nameOverride, input, inputFluid, output, MachineRecipeProcessingSection.hardcoded(processingTime, FluidInfusionRecipe.DEFAULT_POWER_COST, 0, 0));
	}

	protected void addRecipe(String nameOverride, StaticPowerIngredient input, FluidIngredient inputFluid, StaticPowerOutputItem output,
			MachineRecipeProcessingSection processing) {
		addRecipe(nameOverride, SCRecipeBuilder.create(new FluidInfusionRecipe(null, input, inputFluid, output, processing)));
	}
}
