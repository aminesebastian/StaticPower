package theking530.staticpower.data.generators.recipes;

import net.minecraft.data.DataGenerator;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.crafting.MachineRecipeProcessingSection;
import theking530.staticcore.crafting.StaticPowerIngredient;
import theking530.staticcore.crafting.StaticPowerOutputItem;
import theking530.staticcore.data.generators.helpers.SCRecipeBuilder;
import theking530.staticcore.data.generators.helpers.SCRecipeProvider;
import theking530.staticpower.data.crafting.wrappers.lumbermill.LumberMillRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModFluids;
import theking530.staticpower.init.ModItems;
import theking530.staticpower.init.tags.ModItemTags;

public class LumberMillRecipeGenerator extends SCRecipeProvider<LumberMillRecipe> {

	public LumberMillRecipeGenerator(DataGenerator dataGenerator) {
		super("lumber_mill", dataGenerator);
	}

	@Override
	protected void buildRecipes() {
		addRecipe("bowl", StaticPowerIngredient.of(Items.BOWL), StaticPowerOutputItem.of(ModItems.DustWood.get(), 3), new FluidStack(ModFluids.TreeOil.getSource().get(), 30));
		addRecipe("planks", StaticPowerIngredient.of(ItemTags.PLANKS), StaticPowerOutputItem.of(ModItems.DustWood.get(), 6),
				new FluidStack(ModFluids.TreeOil.getSource().get(), 60));

		addLogRecipe("acacia/logs", StaticPowerIngredient.of(ItemTags.ACACIA_LOGS), Blocks.ACACIA_PLANKS);
		addLogRecipe("acacia/fence", StaticPowerIngredient.of(Blocks.ACACIA_FENCE), Blocks.ACACIA_PLANKS);
		addFenceGateRecipe("acacia/fence_gate", StaticPowerIngredient.of(Blocks.ACACIA_FENCE_GATE), Blocks.ACACIA_PLANKS);

		addLogRecipe("birch/logs", StaticPowerIngredient.of(ItemTags.BIRCH_LOGS), Blocks.BIRCH_PLANKS);
		addLogRecipe("birch/fence", StaticPowerIngredient.of(Blocks.BIRCH_FENCE), Blocks.BIRCH_PLANKS);
		addFenceGateRecipe("birch/fence_gate", StaticPowerIngredient.of(Blocks.BIRCH_FENCE_GATE), Blocks.BIRCH_PLANKS);

		addLogRecipe("dark_oak/logs", StaticPowerIngredient.of(ItemTags.DARK_OAK_LOGS), Blocks.DARK_OAK_PLANKS);
		addLogRecipe("dark_oak/fence", StaticPowerIngredient.of(Blocks.DARK_OAK_FENCE), Blocks.DARK_OAK_PLANKS);
		addFenceGateRecipe("dark_oak/fence_gate", StaticPowerIngredient.of(Blocks.DARK_OAK_FENCE_GATE), Blocks.DARK_OAK_PLANKS);

		addLogRecipe("jungle/logs", StaticPowerIngredient.of(ItemTags.JUNGLE_LOGS), Blocks.JUNGLE_PLANKS);
		addLogRecipe("jungle/fence", StaticPowerIngredient.of(Blocks.JUNGLE_FENCE), Blocks.JUNGLE_PLANKS);
		addFenceGateRecipe("jungle/fence_gate", StaticPowerIngredient.of(Blocks.JUNGLE_FENCE_GATE), Blocks.JUNGLE_PLANKS);

		addLogRecipe("magrove/logs", StaticPowerIngredient.of(ItemTags.MANGROVE_LOGS), Blocks.MANGROVE_PLANKS);
		addLogRecipe("magrove/fence", StaticPowerIngredient.of(Blocks.MANGROVE_FENCE), Blocks.MANGROVE_PLANKS);
		addFenceGateRecipe("magrove/fence_gate", StaticPowerIngredient.of(Blocks.MANGROVE_FENCE_GATE), Blocks.MANGROVE_PLANKS);

		addLogRecipe("oak/logs", StaticPowerIngredient.of(ItemTags.OAK_LOGS), Blocks.OAK_PLANKS);
		addLogRecipe("oak/fence", StaticPowerIngredient.of(Blocks.OAK_FENCE), Blocks.OAK_PLANKS);
		addFenceGateRecipe("oak/fence_gate", StaticPowerIngredient.of(Blocks.OAK_FENCE_GATE), Blocks.OAK_PLANKS);

		addLogRecipe("spruce/logs", StaticPowerIngredient.of(ItemTags.SPRUCE_LOGS), Blocks.SPRUCE_PLANKS);
		addLogRecipe("spruce/fence", StaticPowerIngredient.of(Blocks.SPRUCE_FENCE), Blocks.SPRUCE_PLANKS);
		addFenceGateRecipe("spruce/fence_gate", StaticPowerIngredient.of(Blocks.SPRUCE_FENCE_GATE), Blocks.SPRUCE_PLANKS);

		addLogRecipe("rubber/logs", StaticPowerIngredient.of(ModItemTags.RUBBER_WOOD_LOGS), ModBlocks.RubberTreePlanks.get());
		addLogRecipe("static/logs", StaticPowerIngredient.of(ModBlocks.StaticLog.get()), ModBlocks.StaticPlanks.get());
		addLogRecipe("energized/logs", StaticPowerIngredient.of(ModBlocks.EnergizedLog.get()), ModBlocks.EnergizedPlanks.get());
		addLogRecipe("lumum/logs", StaticPowerIngredient.of(ModBlocks.LumumLog.get()), ModBlocks.LumumPlanks.get());

		addLogRecipe("crimson/logs", StaticPowerIngredient.of(ItemTags.CRIMSON_STEMS), Blocks.CRIMSON_PLANKS, ModFluids.InfernalTreeSap.getSource().get());
	}

	protected void addLogRecipe(String name, StaticPowerIngredient log, ItemLike output) {
		addLogRecipe(name, log, output, ModFluids.TreeOil.getSource().get());

	}

	protected void addLogRecipe(String name, StaticPowerIngredient log, ItemLike output, Fluid fluid) {
		addRecipe(name, log, StaticPowerOutputItem.of(output, 6), StaticPowerOutputItem.of(ModItems.DustWood.get(), 6), new FluidStack(fluid, 60));
	}

	protected void addFenceRecipe(String name, StaticPowerIngredient fence, ItemLike planks) {
		addRecipe(name, fence, StaticPowerOutputItem.of(planks, 4), StaticPowerOutputItem.of(ModItems.DustWood.get(), 4), new FluidStack(ModFluids.TreeOil.getSource().get(), 40));
	}

	protected void addFenceGateRecipe(String name, StaticPowerIngredient fence, ItemLike planks) {
		addRecipe(name, fence, StaticPowerOutputItem.of(planks, 2), StaticPowerOutputItem.of(ModItems.DustWood.get(), 2), new FluidStack(ModFluids.TreeOil.getSource().get(), 20));
	}

	protected void addRecipe(String nameOverride, StaticPowerIngredient input, StaticPowerOutputItem primaryOutput) {
		addRecipe(nameOverride, input, primaryOutput, StaticPowerOutputItem.EMPTY, FluidStack.EMPTY, null);
	}

	protected void addRecipe(String nameOverride, StaticPowerIngredient input, StaticPowerOutputItem primaryOutput, StaticPowerOutputItem secondaryOutput) {
		addRecipe(nameOverride, input, primaryOutput, secondaryOutput, FluidStack.EMPTY, null);
	}

	protected void addRecipe(String nameOverride, StaticPowerIngredient input, StaticPowerOutputItem primaryOutput, FluidStack outputFluid) {
		addRecipe(nameOverride, input, primaryOutput, StaticPowerOutputItem.EMPTY, outputFluid, null);
	}

	protected void addRecipe(String nameOverride, StaticPowerIngredient input, StaticPowerOutputItem primaryOutput, StaticPowerOutputItem secondaryOutput, FluidStack outputFluid) {
		addRecipe(nameOverride, input, primaryOutput, secondaryOutput, outputFluid, null);
	}

	protected void addRecipe(String nameOverride, StaticPowerIngredient input, StaticPowerOutputItem primaryOutput, StaticPowerOutputItem secondaryOutput, FluidStack outputFluid,
			MachineRecipeProcessingSection processing) {
		addRecipe(nameOverride, SCRecipeBuilder.create(new LumberMillRecipe(null, input, primaryOutput, secondaryOutput, outputFluid, processing)));
	}
}
