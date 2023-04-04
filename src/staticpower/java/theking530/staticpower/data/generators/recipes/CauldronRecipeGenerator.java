package theking530.staticpower.data.generators.recipes;

import net.minecraft.data.DataGenerator;
import net.minecraft.tags.FluidTags;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.crafting.StaticPowerIngredient;
import theking530.staticcore.crafting.StaticPowerOutputItem;
import theking530.staticcore.data.generators.helpers.SCRecipeBuilder;
import theking530.staticcore.data.generators.helpers.SCRecipeProvider;
import theking530.staticcore.fluid.FluidIngredient;
import theking530.staticpower.data.crafting.wrappers.cauldron.CauldronRecipe;
import theking530.staticpower.init.ModFluids;
import theking530.staticpower.init.ModItems;

public class CauldronRecipeGenerator extends SCRecipeProvider<CauldronRecipe> {

	public CauldronRecipeGenerator(DataGenerator dataGenerator) {
		super("cauldron", dataGenerator);
	}

	@Override
	protected void buildRecipes() {
		addRecipe("rubber_wood_bark", create(StaticPowerIngredient.of(ModItems.RubberWoodBark.get()), StaticPowerOutputItem.of(ModItems.LatexChunk.get()),
				FluidIngredient.of(FluidTags.WATER, 1000), true, 100));

		addRecipe("fruit_static", create(StaticPowerIngredient.of(ModItems.StaticFruit.get()), StaticPowerOutputItem.of(ModItems.DepletedFruit.get()), FluidIngredient.EMPTY,
				new FluidStack(ModFluids.StaticFluid.getSource().get(), 100), false, 200));
		addRecipe("fruit_energized", create(StaticPowerIngredient.of(ModItems.EnergizedFruit.get()), StaticPowerOutputItem.of(ModItems.DepletedFruit.get()), FluidIngredient.EMPTY,
				new FluidStack(ModFluids.EnergizedFluid.getSource().get(), 100), false, 200));
		addRecipe("fruit_lumum", create(StaticPowerIngredient.of(ModItems.LumumFruit.get()), StaticPowerOutputItem.of(ModItems.DepletedFruit.get()), FluidIngredient.EMPTY,
				new FluidStack(ModFluids.LumumFluid.getSource().get(), 100), false, 200));

	}

	protected SCRecipeBuilder<CauldronRecipe> create(StaticPowerIngredient input, StaticPowerOutputItem output, FluidIngredient fluid, boolean drainAfterCraft,
			int timeInCauldron) {
		return create(input, output, fluid, FluidStack.EMPTY, drainAfterCraft, timeInCauldron);
	}

	protected SCRecipeBuilder<CauldronRecipe> create(StaticPowerIngredient input, StaticPowerOutputItem output, FluidIngredient fluid, FluidStack outputFluid,
			boolean drainAfterCraft, int timeInCauldron) {
		return SCRecipeBuilder.create(new CauldronRecipe(null, input, output, fluid, outputFluid, drainAfterCraft, timeInCauldron));
	}
}
