package theking530.staticpower.crafting.wrappers.lumbermill;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.crafting.wrappers.AbstractMachineRecipe;
import theking530.staticpower.crafting.wrappers.ProbabilityItemStackOutput;
import theking530.staticpower.crafting.wrappers.RecipeMatchParameters;

public class LumberMillRecipe extends AbstractMachineRecipe {
	public static final IRecipeType<LumberMillRecipe> RECIPE_TYPE = IRecipeType.register("lumber_mill");

	private final Ingredient input;
	private final ProbabilityItemStackOutput primaryOutput;
	private final ProbabilityItemStackOutput secondaryOutput;
	private final FluidStack outputFluid;

	public LumberMillRecipe(ResourceLocation name, Ingredient input, ProbabilityItemStackOutput primaryOutput, ProbabilityItemStackOutput secondaryOutput, FluidStack outputFluid, int processingTime, int powerCost) {
		super(name, processingTime, powerCost);
		this.input = input;
		this.primaryOutput = primaryOutput;
		this.secondaryOutput = secondaryOutput;
		this.outputFluid = outputFluid;
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

	public List<ItemStack> getRawOutputItems() {
		List<ItemStack> output = new LinkedList<ItemStack>();
		output.add(getPrimaryOutput().getItem());
		if (hasSecondaryOutput()) {
			output.add(getSecondaryOutput().getItem());
		}
		return output;
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
		return input.test(matchParams.getItems()[0]);
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
