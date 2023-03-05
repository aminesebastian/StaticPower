package theking530.staticpower.data.generators.recipes;

import net.minecraft.data.DataGenerator;
import net.minecraft.tags.FluidTags;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.fluid.FluidIngredient;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.wrappers.refinery.RefineryRecipe;
import theking530.staticpower.data.generators.helpers.SPRecipeBuilder;
import theking530.staticpower.data.generators.helpers.SPRecipeProvider;
import theking530.staticpower.init.ModFluids;
import theking530.staticpower.init.ModItems;

public class RefineryRecipeGenerator extends SPRecipeProvider<RefineryRecipe> {

	public RefineryRecipeGenerator(DataGenerator dataGenerator) {
		super("refining", dataGenerator);
	}

	@Override
	protected void buildRecipes() {
		addRecipe("fuel", FluidIngredient.of(100, ModFluids.CrudeOil.getTag()), FluidIngredient.EMPTY, FluidStack.EMPTY, FluidStack.EMPTY,
				new FluidStack(ModFluids.Fuel.getSource().get(), 45));

		addRecipe("crude_cracking", FluidIngredient.of(100, ModFluids.CrudeOil.getTag()), FluidIngredient.of(50, FluidTags.WATER),
				new FluidStack(ModFluids.HeavyOil.getSource().get(), 25), new FluidStack(ModFluids.LightOil.getSource().get(), 45),
				new FluidStack(ModFluids.Fuel.getSource().get(), 55));
		addRecipe("light_cracking", FluidIngredient.of(30, ModFluids.LightOil.getTag()), FluidIngredient.of(30, FluidTags.WATER), FluidStack.EMPTY,
				new FluidStack(ModFluids.Fuel.getSource().get(), 20), FluidStack.EMPTY);
		addRecipe("heavy_cracking", FluidIngredient.of(40, ModFluids.HeavyOil.getTag()), FluidIngredient.of(30, FluidTags.WATER), FluidStack.EMPTY,
				new FluidStack(ModFluids.Fuel.getSource().get(), 30), FluidStack.EMPTY);

		addRecipe("coal_liquifaction", FluidIngredient.of(25, ModFluids.HeavyOil.getTag()), FluidIngredient.of(50, ModFluids.Steam.getTag()),
				StaticPowerIngredient.of(ModItems.DustCoalSmall.get()), new FluidStack(ModFluids.HeavyOil.getSource().get(), 90),
				new FluidStack(ModFluids.LightOil.getSource().get(), 20), new FluidStack(ModFluids.Fuel.getSource().get(), 10));

	}

	protected void addRecipe(String nameOverride, FluidIngredient inputFluid1, FluidIngredient inputFluid2, FluidStack output1, FluidStack output2, FluidStack output3) {
		addRecipe(nameOverride, inputFluid1, inputFluid2, StaticPowerIngredient.EMPTY, output1, output2, output3);
	}

	protected void addRecipe(String nameOverride, FluidIngredient inputFluid1, FluidIngredient inputFluid2, StaticPowerIngredient catalyst, FluidStack output1, FluidStack output2,
			FluidStack output3) {
		addRecipe(nameOverride, inputFluid1, inputFluid2, catalyst, output1, output2, output3,
				MachineRecipeProcessingSection.hardcoded(RefineryRecipe.DEFAULT_PROCESSING_TIME, RefineryRecipe.DEFAULT_POWER_COST, 0, 0));
	}

	protected void addRecipe(String nameOverride, FluidIngredient inputFluid1, FluidIngredient inputFluid2, FluidStack output1, FluidStack output2, FluidStack output3,
			MachineRecipeProcessingSection processing) {
		addRecipe(nameOverride, inputFluid1, inputFluid2, StaticPowerIngredient.EMPTY, output1, output2, output3, processing);
	}

	protected void addRecipe(String nameOverride, FluidIngredient inputFluid1, FluidIngredient inputFluid2, StaticPowerIngredient catalyst, FluidStack output1, FluidStack output2,
			FluidStack output3, MachineRecipeProcessingSection processing) {
		RefineryRecipe recipe = new RefineryRecipe(null, inputFluid1, inputFluid2, catalyst, output1, output2, output3, processing);
		addRecipe(nameOverride, SPRecipeBuilder.create(recipe));
	}
}
