package theking530.staticpower.data.generators.recipes;

import net.minecraft.data.DataGenerator;
import net.minecraft.tags.FluidTags;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.crafting.MachineRecipeProcessingSection;
import theking530.staticcore.crafting.StaticPowerIngredient;
import theking530.staticcore.data.generators.helpers.SCRecipeBuilder;
import theking530.staticcore.data.generators.helpers.SCRecipeProvider;
import theking530.staticcore.fluid.FluidIngredient;
import theking530.staticpower.data.crafting.wrappers.refinery.RefineryRecipe;
import theking530.staticpower.init.ModFluids;
import theking530.staticpower.init.ModItems;

public class RefineryRecipeGenerator extends SCRecipeProvider<RefineryRecipe> {

	public RefineryRecipeGenerator(DataGenerator dataGenerator) {
		super("refining", dataGenerator);
	}

	@Override
	protected void buildRecipes() {
		addRecipe("fuel", FluidIngredient.of(ModFluids.CrudeOil.getTag(), 100), FluidIngredient.EMPTY, FluidStack.EMPTY,
				FluidStack.EMPTY, new FluidStack(ModFluids.Fuel.getSource().get(), 45), 350, 1000);

		addRecipe("crude_cracking", FluidIngredient.of(ModFluids.CrudeOil.getTag(), 100),
				FluidIngredient.of(FluidTags.WATER, 50), new FluidStack(ModFluids.HeavyOil.getSource().get(), 25),
				new FluidStack(ModFluids.LightOil.getSource().get(), 45),
				new FluidStack(ModFluids.Fuel.getSource().get(), 55), 600, 1000);
		addRecipe("light_cracking", FluidIngredient.of(ModFluids.LightOil.getTag(), 30),
				FluidIngredient.of(FluidTags.WATER, 30), FluidStack.EMPTY,
				new FluidStack(ModFluids.Fuel.getSource().get(), 20), FluidStack.EMPTY, 350, 1000);
		addRecipe("heavy_cracking", FluidIngredient.of(ModFluids.HeavyOil.getTag(), 40),
				FluidIngredient.of(FluidTags.WATER, 30), FluidStack.EMPTY,
				new FluidStack(ModFluids.Fuel.getSource().get(), 30), FluidStack.EMPTY, 250, 1000);

		addRecipe("coal_liquifaction", FluidIngredient.of(ModFluids.HeavyOil.getTag(), 25),
				FluidIngredient.of(ModFluids.Steam.getTag(), 50),
				StaticPowerIngredient.of(ModItems.DustCoalSmall.get()),
				new FluidStack(ModFluids.HeavyOil.getSource().get(), 90),
				new FluidStack(ModFluids.LightOil.getSource().get(), 20),
				new FluidStack(ModFluids.Fuel.getSource().get(), 10), 700, 2000);

	}

	protected void addRecipe(String nameOverride, FluidIngredient inputFluid1, FluidIngredient inputFluid2,
			FluidStack output1, FluidStack output2, FluidStack output3, float minimumTemperature, float heatUse) {
		addRecipe(nameOverride, inputFluid1, inputFluid2, StaticPowerIngredient.EMPTY, output1, output2, output3,
				minimumTemperature, heatUse);
	}

	protected void addRecipe(String nameOverride, FluidIngredient inputFluid1, FluidIngredient inputFluid2,
			StaticPowerIngredient catalyst, FluidStack output1, FluidStack output2, FluidStack output3,
			float minimumTemperature, float heatUse) {
		addRecipe(nameOverride, inputFluid1, inputFluid2, catalyst, output1, output2, output3,
				MachineRecipeProcessingSection.hardcoded(RefineryRecipe.DEFAULT_PROCESSING_TIME,
						RefineryRecipe.DEFAULT_POWER_COST, minimumTemperature, heatUse));
	}

	protected void addRecipe(String nameOverride, FluidIngredient inputFluid1, FluidIngredient inputFluid2,
			FluidStack output1, FluidStack output2, FluidStack output3, MachineRecipeProcessingSection processing) {
		addRecipe(nameOverride, inputFluid1, inputFluid2, StaticPowerIngredient.EMPTY, output1, output2, output3,
				processing);
	}

	protected void addRecipe(String nameOverride, FluidIngredient inputFluid1, FluidIngredient inputFluid2,
			StaticPowerIngredient catalyst, FluidStack output1, FluidStack output2, FluidStack output3,
			MachineRecipeProcessingSection processing) {
		RefineryRecipe recipe = new RefineryRecipe(null, inputFluid1, inputFluid2, catalyst, output1, output2, output3,
				processing);
		addRecipe(nameOverride, SCRecipeBuilder.create(recipe));
	}
}
