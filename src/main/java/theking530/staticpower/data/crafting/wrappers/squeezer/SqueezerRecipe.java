package theking530.staticpower.data.crafting.wrappers.squeezer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.data.crafting.AbstractMachineRecipe;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerIngredient;

public class SqueezerRecipe extends AbstractMachineRecipe {
	public static final RecipeType<SqueezerRecipe> RECIPE_TYPE = RecipeType.register("squeezer");

	private final StaticPowerIngredient input;
	private final ProbabilityItemStackOutput output;
	private final FluidStack outputFluid;

	public SqueezerRecipe(ResourceLocation name, StaticPowerIngredient input, ProbabilityItemStackOutput output, FluidStack outputFluid, int processingTime, long powerCost) {
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
		boolean matched = true;

		// Check items.
		if (matchParams.shouldVerifyItems()) {
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
		return SqueezerRecipeSerializer.INSTANCE;
	}

	@Override
	public RecipeType<?> getType() {
		return RECIPE_TYPE;
	}
}
