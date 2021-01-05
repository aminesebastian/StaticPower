package theking530.staticpower.data.crafting.wrappers.lumbermill;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.data.crafting.AbstractMachineRecipe;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerIngredient;

public class LumberMillRecipe extends AbstractMachineRecipe {
	public static final IRecipeType<LumberMillRecipe> RECIPE_TYPE = IRecipeType.register("lumber_mill");

	private final StaticPowerIngredient input;
	private final ProbabilityItemStackOutput primaryOutput;
	private final ProbabilityItemStackOutput secondaryOutput;
	private final FluidStack outputFluid;

	public LumberMillRecipe(ResourceLocation name, StaticPowerIngredient input, ProbabilityItemStackOutput primaryOutput, ProbabilityItemStackOutput secondaryOutput, FluidStack outputFluid,
			int processingTime, long powerCost) {
		super(name, processingTime, powerCost);
		this.input = input;
		this.primaryOutput = primaryOutput;
		this.secondaryOutput = secondaryOutput;
		this.outputFluid = outputFluid;
	}

	public StaticPowerIngredient getInput() {
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
	public IRecipeSerializer<?> getSerializer() {
		return LumberMillRecipeSerializer.INSTANCE;
	}

	@Override
	public IRecipeType<?> getType() {
		return RECIPE_TYPE;
	}
}
