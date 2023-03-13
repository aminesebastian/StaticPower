package theking530.staticpower.data.generators.recipes;

import net.minecraft.data.DataGenerator;
import net.minecraft.tags.FluidTags;
import theking530.staticcore.fluid.FluidIngredient;
import theking530.staticpower.data.crafting.wrappers.fertilization.FertalizerRecipe;
import theking530.staticpower.data.generators.helpers.SPRecipeBuilder;
import theking530.staticpower.data.generators.helpers.SPRecipeProvider;
import theking530.staticpower.init.ModFluids;

public class FertilizerRecipeGenerator extends SPRecipeProvider<FertalizerRecipe> {

	public FertilizerRecipeGenerator(DataGenerator dataGenerator) {
		super("fertilizer", dataGenerator);
	}

	@Override
	protected void buildRecipes() {
		addRecipe("water", FluidIngredient.of(FluidTags.WATER, 1), 0.01f);
		addRecipe("mash", FluidIngredient.of(ModFluids.Mash.getTag(), 1), 0.05f);
		addRecipe("liquid_fertilizer", FluidIngredient.of(ModFluids.Fertilizer.getTag(), 1), 0.1f);

		addRecipe("fluid_static", FluidIngredient.of(ModFluids.StaticFluid.getTag(), 1), 0.15f);
		addRecipe("fluid_energized", FluidIngredient.of(ModFluids.EnergizedFluid.getTag(), 1), 0.2f);
		addRecipe("fluid_lumum", FluidIngredient.of(ModFluids.LumumFluid.getTag(), 1), 0.3f);
	}

	protected void addRecipe(String nameOverride, FluidIngredient inputFluid, float fertalizationAmount) {
		FertalizerRecipe recipe = new FertalizerRecipe(null, inputFluid, fertalizationAmount);
		addRecipe(nameOverride, SPRecipeBuilder.create(recipe));
	}
}
