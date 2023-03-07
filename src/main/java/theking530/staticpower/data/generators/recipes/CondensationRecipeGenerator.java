package theking530.staticpower.data.generators.recipes;

import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.fluid.FluidIngredient;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.data.crafting.wrappers.condensation.CondensationRecipe;
import theking530.staticpower.data.generators.helpers.SPRecipeBuilder;
import theking530.staticpower.data.generators.helpers.SPRecipeProvider;
import theking530.staticpower.init.ModFluids;

public class CondensationRecipeGenerator extends SPRecipeProvider<CondensationRecipe> {

	public CondensationRecipeGenerator(DataGenerator dataGenerator) {
		super("condensation", dataGenerator);
	}

	@Override
	protected void buildRecipes() {
		addRecipe("ethanol", create(FluidIngredient.of(ModFluids.EvaporatedMash.getTag(), 10), new FluidStack(ModFluids.Ethanol.getSource().get(), 1), 20, 100));
		addRecipe("water_from_steam", create(FluidIngredient.of(ModFluids.Steam.getTag(), 10), new FluidStack(Fluids.WATER, 1), 10, 100));

	}

	protected SPRecipeBuilder<CondensationRecipe> create(FluidIngredient inputFluid, FluidStack outputFluid, int heatOutput, int minimumHeat) {
		return SPRecipeBuilder.create(new CondensationRecipe(null, inputFluid, outputFluid,
				MachineRecipeProcessingSection.hardcoded(CondensationRecipe.DEFAULT_PROCESSING_TIME, 0, minimumHeat, heatOutput)));
	}
}
