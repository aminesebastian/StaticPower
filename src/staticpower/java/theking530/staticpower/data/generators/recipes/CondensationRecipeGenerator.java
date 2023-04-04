package theking530.staticpower.data.generators.recipes;

import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.crafting.MachineRecipeProcessingSection;
import theking530.staticcore.data.generators.helpers.SCRecipeBuilder;
import theking530.staticcore.data.generators.helpers.SCRecipeProvider;
import theking530.staticcore.fluid.FluidIngredient;
import theking530.staticpower.data.crafting.wrappers.condensation.CondensationRecipe;
import theking530.staticpower.init.ModFluids;

public class CondensationRecipeGenerator extends SCRecipeProvider<CondensationRecipe> {

	public CondensationRecipeGenerator(DataGenerator dataGenerator) {
		super("condensation", dataGenerator);
	}

	@Override
	protected void buildRecipes() {
		addRecipe("ethanol", create(FluidIngredient.of(ModFluids.EvaporatedMash.getTag(), 10), new FluidStack(ModFluids.Ethanol.getSource().get(), 1), 20, 100));
		addRecipe("water_from_steam", create(FluidIngredient.of(ModFluids.Steam.getTag(), 10), new FluidStack(Fluids.WATER, 1), 10, 100));

	}

	protected SCRecipeBuilder<CondensationRecipe> create(FluidIngredient inputFluid, FluidStack outputFluid, int heatOutput, int minimumHeat) {
		return SCRecipeBuilder.create(new CondensationRecipe(null, inputFluid, outputFluid,
				MachineRecipeProcessingSection.hardcoded(CondensationRecipe.DEFAULT_PROCESSING_TIME, 0, minimumHeat, heatOutput)));
	}
}
