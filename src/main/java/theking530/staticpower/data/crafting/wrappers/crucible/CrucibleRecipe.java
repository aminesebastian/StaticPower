package theking530.staticpower.data.crafting.wrappers.crucible;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.data.crafting.AbstractMachineRecipe;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerIngredient;

public class CrucibleRecipe extends AbstractMachineRecipe {
	public static final IRecipeType<CrucibleRecipe> RECIPE_TYPE = IRecipeType.register("crucible");

	private final StaticPowerIngredient input;
	private final ProbabilityItemStackOutput output;
	private final FluidStack outputFluid;
	private final int minimumTemperature;

	public CrucibleRecipe(ResourceLocation name, StaticPowerIngredient input, ProbabilityItemStackOutput output, FluidStack outputFluid, int minimumTemperature, int processingTime,
			long powerCost) {
		super(name, processingTime, powerCost);
		this.input = input;
		this.output = output;
		this.outputFluid = outputFluid;
		this.minimumTemperature = minimumTemperature;
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

	public int getMinimumTemperature() {
		return minimumTemperature;
	}

	@Override
	public boolean isValid(RecipeMatchParameters matchParams) {
		boolean matched = true;

		// Check items.
		if (matchParams.shouldVerifyItems()) {
			matched &= matchParams.hasItems() && input.test(matchParams.getItems()[0], matchParams.shouldVerifyItemCounts());
		}

		return matched;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return CrucibleRecipeSerializer.INSTANCE;
	}

	@Override
	public IRecipeType<?> getType() {
		return RECIPE_TYPE;
	}
}
