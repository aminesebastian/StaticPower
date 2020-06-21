package theking530.staticpower.crafting.wrappers.lumbermill;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.crafting.wrappers.AbstractStaticPowerRecipe;
import theking530.staticpower.crafting.wrappers.ProbabilityItemStackOutput;
import theking530.staticpower.crafting.wrappers.RecipeMatchParameters;

public class LumberMillRecipe extends AbstractStaticPowerRecipe {
	public static final IRecipeType<LumberMillRecipe> RECIPE_TYPE = IRecipeType.register("lumber_mill");

	private final Ingredient input;
	private final ProbabilityItemStackOutput primaryOutput;
	private final ProbabilityItemStackOutput secondaryOutput;
	private final FluidStack outputFluid;
	private final int processingTime;
	private final int powerCost;

	public LumberMillRecipe(ResourceLocation name, Ingredient input, ProbabilityItemStackOutput primaryOutput, ProbabilityItemStackOutput secondaryOutput, FluidStack outputFluid, int processingTime,
			int powerCost) {
		super(name);
		this.input = input;
		this.primaryOutput = primaryOutput;
		this.secondaryOutput = secondaryOutput;
		this.outputFluid = outputFluid;
		this.processingTime = processingTime;
		this.powerCost = powerCost;
	}

	public int getProcessingTime() {
		return processingTime;
	}

	public int getPowerCost() {
		return powerCost;
	}

	public Ingredient getInput() {
		return input;
	}

	public ProbabilityItemStackOutput getPrimaryOutput() {
		return primaryOutput;
	}

	public ProbabilityItemStackOutput getSecondaryOutput() {
		return secondaryOutput;
	}

	public FluidStack getOutputFluid() {
		return outputFluid;
	}

	public boolean hasSecondaryOutput() {
		return !secondaryOutput.isEmpty();
	}

	public boolean hasOutputFluid() {
		return !outputFluid.isEmpty();
	}

	@Override
	public boolean isValid(RecipeMatchParameters matchParams) {
		return input.test(matchParams.getItems()[0]) && matchParams.getStoredEnergy() >= powerCost;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return LumberMillRecipeSerializer.INSTANCE;
	}

	@Override
	public IRecipeType<?> getType() {
		return RECIPE_TYPE;
	}
}
