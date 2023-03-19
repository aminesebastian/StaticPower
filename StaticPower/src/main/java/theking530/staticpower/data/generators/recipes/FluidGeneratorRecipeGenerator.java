package theking530.staticpower.data.generators.recipes;

import net.minecraft.data.DataGenerator;
import theking530.staticcore.crafting.MachineRecipeProcessingSection;
import theking530.staticcore.fluid.FluidIngredient;
import theking530.staticpower.data.crafting.wrappers.fluidgenerator.FluidGeneratorRecipe;
import theking530.staticpower.data.generators.helpers.SPRecipeBuilder;
import theking530.staticpower.data.generators.helpers.SPRecipeProvider;
import theking530.staticpower.init.ModFluids;

public class FluidGeneratorRecipeGenerator extends SPRecipeProvider<FluidGeneratorRecipe> {

	public FluidGeneratorRecipeGenerator(DataGenerator dataGenerator) {
		super("fluid_generation", dataGenerator);
	}

	@Override
	protected void buildRecipes() {
		addRecipe("sap_tree", FluidIngredient.of(ModFluids.TreeSap.getTag(), 1), 4);
		addRecipe("sap_infernal_tree", FluidIngredient.of(ModFluids.InfernalTreeSap.getTag(), 1), 18);

		addRecipe("fluid_static", FluidIngredient.of(ModFluids.StaticFluid.getTag(), 1), 8);
		addRecipe("fluid_energized", FluidIngredient.of(ModFluids.EnergizedFluid.getTag(), 1), 16);
		addRecipe("fluid_lumum", FluidIngredient.of(ModFluids.LumumFluid.getTag(), 1), 32);

		addRecipe("mash", FluidIngredient.of(ModFluids.Mash.getTag(), 1), 2);
		addRecipe("ethanol", FluidIngredient.of(ModFluids.Ethanol.getTag(), 1), 12);

		addRecipe("oil_seed", FluidIngredient.of(ModFluids.SeedOil.getTag(), 1), 7);
		addRecipe("oil_tree", FluidIngredient.of(ModFluids.TreeOil.getTag(), 1), 4);
		addRecipe("oil_crude", FluidIngredient.of(ModFluids.CrudeOil.getTag(), 1), 12);
		addRecipe("oil_light", FluidIngredient.of(ModFluids.LightOil.getTag(), 1), 48);
		addRecipe("oil_heavy", FluidIngredient.of(ModFluids.HeavyOil.getTag(), 1), 36);

		addRecipe("fuel", FluidIngredient.of(ModFluids.Fuel.getTag(), 1), 60);
	}

	protected void addRecipe(String nameOverride, FluidIngredient fluid, int powerGeneration) {
		addRecipe(nameOverride, fluid, powerGeneration, 1);
	}

	protected void addRecipe(String nameOverride, FluidIngredient fluid, int powerGeneration, int processingTime) {
		FluidGeneratorRecipe recipe = new FluidGeneratorRecipe(null, fluid, MachineRecipeProcessingSection.hardcoded(processingTime, powerGeneration, 0, 0));
		addRecipe(nameOverride, SPRecipeBuilder.create(recipe));
	}
}
