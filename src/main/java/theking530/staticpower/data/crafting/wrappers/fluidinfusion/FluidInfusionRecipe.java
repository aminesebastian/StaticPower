package theking530.staticpower.data.crafting.wrappers.fluidinfusion;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.data.crafting.AbstractMachineRecipe;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerIngredient;

public class FluidInfusionRecipe extends AbstractMachineRecipe {
	public static final RecipeType<FluidInfusionRecipe> RECIPE_TYPE = RecipeType.register("fluid_infusion");

	private final StaticPowerIngredient input;
	private final ProbabilityItemStackOutput output;
	private final FluidStack inputFluid;

	public FluidInfusionRecipe(ResourceLocation name, StaticPowerIngredient input, ProbabilityItemStackOutput output, FluidStack inputFluid, int processingTime, long powerCost) {
		super(name, processingTime, powerCost);
		this.input = input;
		this.output = output;
		this.inputFluid = inputFluid;
	}

	public StaticPowerIngredient getInput() {
		return input;
	}

	public ProbabilityItemStackOutput getOutput() {
		return output;
	}

	public boolean hasItemOutput() {
		return !output.isEmpty();
	}

	public FluidStack getRequiredFluid() {
		return inputFluid;
	}

	@Override
	public boolean isValid(RecipeMatchParameters matchParams) {
		boolean matched = true;

		// Check fluids.
		if (matchParams.shouldVerifyFluids()) {
			matched &= matchParams.hasFluids() && matchParams.getFluids()[0].equals(inputFluid);
			if (matched && matchParams.shouldVerifyFluidAmounts()) {
				matched &= matchParams.hasFluids() && matchParams.getFluids()[0].getAmount() >= inputFluid.getAmount();
			}
		}

		// Check items.
		if (matched && matchParams.shouldVerifyItems()) {
			if (matchParams.shouldVerifyItemCounts()) {
				matched &= matchParams.hasItems() && input.testWithCount(matchParams.getItems()[0]);
			} else {
				matched &= matchParams.hasItems() && input.test(matchParams.getItems()[0]);
			}
		}

		return matched;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return FluidInfusionRecipeSerializer.INSTANCE;
	}

	@Override
	public RecipeType<?> getType() {
		return RECIPE_TYPE;
	}
}
