package theking530.staticpower.data.generators.recipes;

import net.minecraft.data.DataGenerator;
import theking530.staticcore.fluid.FluidIngredient;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
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
		addRecipe("sap_tree", FluidIngredient.of(1, ModFluids.TreeSap.getTag()), 4);
		addRecipe("sap_infernal_tree", FluidIngredient.of(1, ModFluids.InfernalTreeSap.getTag()), 18);

		addRecipe("fluid_static", FluidIngredient.of(1, ModFluids.StaticFluid.getTag()), 8);
		addRecipe("fluid_energized", FluidIngredient.of(1, ModFluids.EnergizedFluid.getTag()), 16);
		addRecipe("fluid_lumum", FluidIngredient.of(1, ModFluids.LumumFluid.getTag()), 32);

		addRecipe("mash", FluidIngredient.of(1, ModFluids.Mash.getTag()), 2);
		addRecipe("ethanol", FluidIngredient.of(1, ModFluids.Ethanol.getTag()), 12);

		addRecipe("oil_seed", FluidIngredient.of(1, ModFluids.SeedOil.getTag()), 7);
		addRecipe("oil_tree", FluidIngredient.of(1, ModFluids.TreeOil.getTag()), 4);
		addRecipe("oil_crude", FluidIngredient.of(1, ModFluids.CrudeOil.getTag()), 12);
		addRecipe("oil_light", FluidIngredient.of(1, ModFluids.LightOil.getTag()), 48);
		addRecipe("oil_heavy", FluidIngredient.of(1, ModFluids.HeavyOil.getTag()), 36);

		addRecipe("fuel", FluidIngredient.of(1, ModFluids.Fuel.getTag()), 60);
	}

	protected void addRecipe(String nameOverride, FluidIngredient fluid, int powerGeneration) {
		addRecipe(nameOverride, fluid, powerGeneration, 1);
	}

	protected void addRecipe(String nameOverride, FluidIngredient fluid, int powerGeneration, int processingTime) {
		FluidGeneratorRecipe recipe = new FluidGeneratorRecipe(null, fluid, MachineRecipeProcessingSection.hardcoded(processingTime, powerGeneration, 0, 0));
		addRecipe(nameOverride, SPRecipeBuilder.create(recipe));
	}
}
