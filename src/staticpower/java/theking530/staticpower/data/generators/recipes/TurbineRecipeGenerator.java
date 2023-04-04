package theking530.staticpower.data.generators.recipes;

import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.data.generators.helpers.SCRecipeBuilder;
import theking530.staticcore.data.generators.helpers.SCRecipeProvider;
import theking530.staticcore.fluid.FluidIngredient;
import theking530.staticpower.data.crafting.wrappers.turbine.TurbineRecipe;
import theking530.staticpower.init.ModFluids;

public class TurbineRecipeGenerator extends SCRecipeProvider<TurbineRecipe> {

	public TurbineRecipeGenerator(DataGenerator dataGenerator) {
		super("turbine", dataGenerator);
	}

	@Override
	protected void buildRecipes() {
		addRecipe("steam", FluidIngredient.of(ModFluids.Steam.getTag(), 20), new FluidStack(Fluids.WATER, 1), 10);
	}

	protected void addRecipe(String nameOverride, FluidIngredient input, FluidStack output, int generationAmount) {
		addRecipe(nameOverride, SCRecipeBuilder.create(new TurbineRecipe(null, input, output, generationAmount)));
	}
}
