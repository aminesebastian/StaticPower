package theking530.staticpower.data.crafting.wrappers.fluidinfusion;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.data.crafting.AbstractMachineRecipe;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.RecipeMatchParameters;

public class FluidInfusionRecipe extends AbstractMachineRecipe {
	public static final IRecipeType<FluidInfusionRecipe> RECIPE_TYPE = IRecipeType.register("fluid_infusion");

	private final Ingredient input;
	private final ProbabilityItemStackOutput output;
	private final FluidStack inputFluid;

	public FluidInfusionRecipe(ResourceLocation name, Ingredient input, ProbabilityItemStackOutput output, FluidStack inputFluid, int processingTime, int powerCost) {
		super(name, processingTime, powerCost);
		this.input = input;
		this.output = output;
		this.inputFluid = inputFluid;
	}

	public Ingredient getInput() {
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
		return input.test(matchParams.getItems()[0]) && inputFluid.isFluidEqual(matchParams.getFluids()[0]) && matchParams.getFluids()[0].getAmount() >= inputFluid.getAmount();
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return FluidInfusionRecipeSerializer.INSTANCE;
	}

	@Override
	public IRecipeType<?> getType() {
		return RECIPE_TYPE;
	}
}
