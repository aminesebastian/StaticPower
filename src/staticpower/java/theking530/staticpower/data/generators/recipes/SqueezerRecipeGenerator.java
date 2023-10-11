package theking530.staticpower.data.generators.recipes;

import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.crafting.MachineRecipeProcessingSection;
import theking530.staticcore.crafting.StaticPowerIngredient;
import theking530.staticcore.crafting.StaticPowerOutputItem;
import theking530.staticcore.data.generators.helpers.SCRecipeBuilder;
import theking530.staticcore.data.generators.helpers.SCRecipeProvider;
import theking530.staticcore.utilities.MinecraftColor;
import theking530.staticpower.data.crafting.wrappers.squeezer.SqueezerRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModFluids;
import theking530.staticpower.init.ModItems;
import theking530.staticpower.init.tags.ModItemTags;

public class SqueezerRecipeGenerator extends SCRecipeProvider<SqueezerRecipe> {

	public SqueezerRecipeGenerator(DataGenerator dataGenerator) {
		super("squeezing", dataGenerator);
	}

	@Override
	protected void buildRecipes() {
		addRecipe("apple", StaticPowerIngredient.of(Items.APPLE),
				new FluidStack(ModFluids.AppleJuice.getSource().get(), 250));
		addRecipe("beetroot", StaticPowerIngredient.of(Tags.Items.CROPS_BEETROOT),
				StaticPowerOutputItem.of(Items.BEETROOT_SEEDS),
				new FluidStack(ModFluids.BeetJuice.getSource().get(), 125));

		addRecipe("pumpkin", StaticPowerIngredient.of(Items.PUMPKIN),
				StaticPowerOutputItem.of(Items.PUMPKIN_SEEDS, 3, 1, 2, 0.5f),
				new FluidStack(ModFluids.PumpkinJuice.getSource().get(), 1000));

		addRecipe("melon_slice", StaticPowerIngredient.of(Items.MELON_SLICE),
				StaticPowerOutputItem.of(Items.MELON_SEEDS),
				new FluidStack(ModFluids.WatermelonJuice.getSource().get(), 250),
				SqueezerRecipe.DEFAULT_PROCESSING_TIME / 4);
		addRecipe("melon", StaticPowerIngredient.of(Items.MELON),
				StaticPowerOutputItem.of(Items.MELON_SEEDS, 3, 1, 2, 0.5f),
				new FluidStack(ModFluids.WatermelonJuice.getSource().get(), 1000));

		addRecipe("carrot", StaticPowerIngredient.of(Tags.Items.CROPS_CARROT),
				new FluidStack(ModFluids.CarrotJuice.getSource().get(), 100));
		addRecipe("sweet_berry", StaticPowerIngredient.of(Items.SWEET_BERRIES),
				new FluidStack(ModFluids.BerryJuice.getSource().get(), 25), SqueezerRecipe.DEFAULT_PROCESSING_TIME / 5);

		addRecipe("static_fruit", StaticPowerIngredient.of(ModItems.StaticFruit.get()),
				StaticPowerOutputItem.of(ModItems.DepletedFruit.get(), SqueezerRecipe.DEFAULT_PROCESSING_TIME / 2),
				new FluidStack(ModFluids.StaticFluid.getSource().get(), 100));
		addRecipe("energized_fruit", StaticPowerIngredient.of(ModItems.EnergizedFruit.get()),
				StaticPowerOutputItem.of(ModItems.DepletedFruit.get(), SqueezerRecipe.DEFAULT_PROCESSING_TIME / 2),
				new FluidStack(ModFluids.EnergizedFluid.getSource().get(), 100));
		addRecipe("lumum_fruit", StaticPowerIngredient.of(ModItems.LumumFruit.get()),
				StaticPowerOutputItem.of(ModItems.DepletedFruit.get(), SqueezerRecipe.DEFAULT_PROCESSING_TIME / 2),
				new FluidStack(ModFluids.LumumFluid.getSource().get(), 100));
		addRecipe("depleted_fruit", StaticPowerIngredient.of(ModItems.DepletedFruit.get()),
				new FluidStack(ModFluids.Fertilizer.getSource().get(), 50));

		addRecipe("bonemeal", StaticPowerIngredient.of(Items.BONE_MEAL),
				new FluidStack(ModFluids.Fertilizer.getSource().get(), 100));
		addRecipe("sawdust", StaticPowerIngredient.of(ModItemTags.WOOD_DUST),
				new FluidStack(ModFluids.TreeOil.getSource().get(), 1), SqueezerRecipe.DEFAULT_PROCESSING_TIME / 10);

		addRecipe("honey_block", StaticPowerIngredient.of(Items.HONEY_BLOCK),
				new FluidStack(ModFluids.Honey.getSource().get(), 1000));
		addRecipe("honeycomb_block", StaticPowerIngredient.of(Items.HONEYCOMB_BLOCK),
				new FluidStack(ModFluids.Honey.getSource().get(), 320));
		addRecipe("honeycomb", StaticPowerIngredient.of(Items.HONEYCOMB),
				new FluidStack(ModFluids.Honey.getSource().get(), 80), SqueezerRecipe.DEFAULT_PROCESSING_TIME / 4);

		addRecipe("wheat_seed", StaticPowerIngredient.of(Tags.Items.SEEDS_WHEAT),
				new FluidStack(ModFluids.SeedOil.getSource().get(), 5), SqueezerRecipe.DEFAULT_PROCESSING_TIME / 4);
		addRecipe("beetroot_seed", StaticPowerIngredient.of(Tags.Items.SEEDS_BEETROOT),
				new FluidStack(ModFluids.SeedOil.getSource().get(), 10), SqueezerRecipe.DEFAULT_PROCESSING_TIME / 4);
		addRecipe("melon_seed", StaticPowerIngredient.of(Tags.Items.SEEDS_MELON),
				new FluidStack(ModFluids.SeedOil.getSource().get(), 20), SqueezerRecipe.DEFAULT_PROCESSING_TIME / 4);
		addRecipe("pumpkin_seed", StaticPowerIngredient.of(Tags.Items.SEEDS_PUMPKIN),
				new FluidStack(ModFluids.SeedOil.getSource().get(), 20), SqueezerRecipe.DEFAULT_PROCESSING_TIME / 4);

		addRecipe("rubber_leaves", StaticPowerIngredient.of(ModBlocks.RubberTreeLeaves.get()),
				new FluidStack(ModFluids.Latex.getSource().get(), 20), SqueezerRecipe.DEFAULT_PROCESSING_TIME / 2);
		addRecipe("rubber_sapling", StaticPowerIngredient.of(ModBlocks.RubberTreeSapling.get()),
				new FluidStack(ModFluids.Latex.getSource().get(), 100), SqueezerRecipe.DEFAULT_PROCESSING_TIME / 2);
		addRecipe("rubber_bark", StaticPowerIngredient.of(ModItems.RubberWoodBark.get()),
				new FluidStack(ModFluids.Latex.getSource().get(), 100), SqueezerRecipe.DEFAULT_PROCESSING_TIME / 2);
		addRecipe("rubber_log", StaticPowerIngredient.of(ModBlocks.RubberTreeLog.get()),
				StaticPowerOutputItem.of(ModBlocks.RubberTreeStrippedLog.get()),
				new FluidStack(ModFluids.Latex.getSource().get(), 200));
		addRecipe("rubber_wood", StaticPowerIngredient.of(ModBlocks.RubberTreeWood.get()),
				StaticPowerOutputItem.of(ModBlocks.RubberTreeStrippedWood.get()),
				new FluidStack(ModFluids.Latex.getSource().get(), 200));

		for (MinecraftColor color : MinecraftColor.values()) {
			addRecipe("dyes/dye_" + color.getName(), StaticPowerIngredient.of(color.getDyeTag()),
					new FluidStack(ModFluids.DYES.get(color).getSource().get(), 100));
		}
	}

	protected void addRecipe(String nameOverride, StaticPowerIngredient input, FluidStack outputFluid) {
		addRecipe(nameOverride, input, StaticPowerOutputItem.EMPTY, outputFluid, null);
	}

	protected void addRecipe(String nameOverride, StaticPowerIngredient input, FluidStack outputFluid,
			int processingTime) {
		addRecipe(nameOverride, input, StaticPowerOutputItem.EMPTY, outputFluid,
				MachineRecipeProcessingSection.hardcoded(processingTime, SqueezerRecipe.DEFAULT_POWER_COST, 0, 0));
	}

	protected void addRecipe(String nameOverride, StaticPowerIngredient input, StaticPowerOutputItem output,
			FluidStack outputFluid) {
		addRecipe(nameOverride, input, output, outputFluid, null);
	}

	protected void addRecipe(String nameOverride, StaticPowerIngredient input, StaticPowerOutputItem output,
			FluidStack outputFluid, int processingTime) {
		addRecipe(nameOverride, input, output, outputFluid,
				MachineRecipeProcessingSection.hardcoded(processingTime, SqueezerRecipe.DEFAULT_POWER_COST, 0, 0));
	}

	protected void addRecipe(String nameOverride, StaticPowerIngredient input, StaticPowerOutputItem output,
			FluidStack outputFluid, MachineRecipeProcessingSection processing) {
		addRecipe(nameOverride,
				SCRecipeBuilder.create(new SqueezerRecipe(null, input, output, outputFluid, processing)));
	}
}
