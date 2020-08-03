package theking530.staticpower.data.crafting.wrappers.squeezer;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.data.crafting.AbstractMachineRecipe;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerIngredient;

public class SqueezerRecipe extends AbstractMachineRecipe {
	public static final IRecipeType<SqueezerRecipe> RECIPE_TYPE = IRecipeType.register("squeezer");

	private final StaticPowerIngredient input;
	private final ProbabilityItemStackOutput output;
	private final FluidStack outputFluid;

	public SqueezerRecipe(ResourceLocation name, StaticPowerIngredient input, ProbabilityItemStackOutput output, FluidStack outputFluid, int processingTime, int powerCost) {
		super(name, processingTime, powerCost);
		this.input = input;
		this.output = output;
		this.outputFluid = outputFluid;
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

	public FluidStack getOutputFluid() {
		return outputFluid;
	}

	public boolean hasOutputFluid() {
		return !outputFluid.isEmpty();
	}

	@Override
	public boolean isValid(RecipeMatchParameters matchParams) {
		return input.test(matchParams.getItems()[0]);
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return SqueezerRecipeSerializer.INSTANCE;
	}

	@Override
	public IRecipeType<?> getType() {
		return RECIPE_TYPE;
	}
}
