package theking530.staticpower.data.generators.recipes;

import net.minecraft.data.DataGenerator;
import theking530.staticcore.fluid.FluidIngredient;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.data.crafting.StaticPowerOutputItem;
import theking530.staticpower.data.crafting.wrappers.vulcanizer.VulcanizerRecipe;
import theking530.staticpower.data.generators.helpers.SPRecipeBuilder;
import theking530.staticpower.data.generators.helpers.SPRecipeProvider;
import theking530.staticpower.init.ModFluids;
import theking530.staticpower.init.ModItems;

public class VulcanizerRecipeGenerator extends SPRecipeProvider<VulcanizerRecipe> {

	public VulcanizerRecipeGenerator(DataGenerator dataGenerator) {
		super("vulcanization", dataGenerator);
	}

	@Override
	protected void buildRecipes() {
		addRecipe("rubber_bar", FluidIngredient.of(ModFluids.Latex.getTag(), 100), StaticPowerOutputItem.of(ModItems.RubberBar.get()));
	}

	protected void addRecipe(String nameOverride, FluidIngredient inputFluid, StaticPowerOutputItem output) {
		addRecipe(nameOverride, inputFluid, output, null);
	}

	protected void addRecipe(String nameOverride, FluidIngredient inputFluid, StaticPowerOutputItem output, MachineRecipeProcessingSection processing) {
		addRecipe(nameOverride, SPRecipeBuilder.create(new VulcanizerRecipe(null, inputFluid, output, processing)));
	}
}
