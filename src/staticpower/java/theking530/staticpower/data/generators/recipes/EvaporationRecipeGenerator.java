package theking530.staticpower.data.generators.recipes;

import net.minecraft.data.DataGenerator;
import net.minecraft.tags.FluidTags;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.crafting.MachineRecipeProcessingSection;
import theking530.staticcore.data.generators.helpers.SCRecipeBuilder;
import theking530.staticcore.data.generators.helpers.SCRecipeProvider;
import theking530.staticcore.fluid.FluidIngredient;
import theking530.staticpower.data.crafting.wrappers.evaporation.EvaporatorRecipe;
import theking530.staticpower.init.ModFluids;

public class EvaporationRecipeGenerator extends SCRecipeProvider<EvaporatorRecipe> {

	public EvaporationRecipeGenerator(DataGenerator dataGenerator) {
		super("evaporation", dataGenerator);
	}

	@Override
	protected void buildRecipes() {
		addRecipe("evaporated_mash", create(FluidIngredient.of(ModFluids.Mash.getTag(), 1), new FluidStack(ModFluids.EvaporatedMash.getSource().get(), 1), 10, 100));
		addRecipe("steam", create(FluidIngredient.of(FluidTags.WATER, 1), new FluidStack(ModFluids.Steam.getSource().get(), 10), 20, 100));

	}

	protected SCRecipeBuilder<EvaporatorRecipe> create(FluidIngredient inputFluid, FluidStack outputFluid, int heatUse, int minimumHeat) {
		return SCRecipeBuilder.create(
				new EvaporatorRecipe(null, inputFluid, outputFluid, MachineRecipeProcessingSection.hardcoded(EvaporatorRecipe.DEFAULT_PROCESSING_TIME, 0, minimumHeat, heatUse)));
	}
}
