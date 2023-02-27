package theking530.staticpower.data.generators.recipes;

import net.minecraft.data.DataGenerator;
import net.minecraft.tags.FluidTags;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.fluid.FluidIngredient;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.StaticPowerOutputItem;
import theking530.staticpower.data.crafting.wrappers.cauldron.CauldronRecipe;
import theking530.staticpower.data.generators.helpers.SPRecipeBuilder;
import theking530.staticpower.data.generators.helpers.SPRecipeProvider;
import theking530.staticpower.init.ModFluids;
import theking530.staticpower.init.ModItems;

public class CasterRecipeGenerator extends SPRecipeProvider<CauldronRecipe> {

	public CasterRecipeGenerator(DataGenerator dataGenerator) {
		super("cauldron", dataGenerator);
	}

	@Override
	protected void buildRecipes() {
		addRecipe("rubber_wood_bark", create(StaticPowerIngredient.of(ModItems.RubberWoodBark.get()), StaticPowerOutputItem.of(ModItems.LatexChunk.get()),
				FluidIngredient.of(1000, FluidTags.WATER), true, 100));

		addRecipe("fruit_static", create(StaticPowerIngredient.of(ModItems.StaticFruit.get()), StaticPowerOutputItem.of(ModItems.DepletedFruit.get()), FluidIngredient.EMPTY,
				new FluidStack(ModFluids.StaticFluid.getSource().get(), 100), false, 200));
		addRecipe("fruit_energized", create(StaticPowerIngredient.of(ModItems.EnergizedFruit.get()), StaticPowerOutputItem.of(ModItems.DepletedFruit.get()), FluidIngredient.EMPTY,
				new FluidStack(ModFluids.EnergizedFluid.getSource().get(), 100), false, 200));
		addRecipe("fruit_lumum", create(StaticPowerIngredient.of(ModItems.LumumFruit.get()), StaticPowerOutputItem.of(ModItems.DepletedFruit.get()), FluidIngredient.EMPTY,
				new FluidStack(ModFluids.LumumFluid.getSource().get(), 100), false, 200));

	}

	protected SPRecipeBuilder<CauldronRecipe> create(StaticPowerIngredient input, StaticPowerOutputItem output, FluidIngredient fluid, boolean drainAfterCraft,
			int timeInCauldron) {
		return create(input, output, fluid, FluidStack.EMPTY, drainAfterCraft, timeInCauldron);
	}

	protected SPRecipeBuilder<CauldronRecipe> create(StaticPowerIngredient input, StaticPowerOutputItem output, FluidIngredient fluid, FluidStack outputFluid,
			boolean drainAfterCraft, int timeInCauldron) {
		return SPRecipeBuilder.create(new CauldronRecipe(null, input, output, fluid, outputFluid, drainAfterCraft, timeInCauldron));
	}
}
