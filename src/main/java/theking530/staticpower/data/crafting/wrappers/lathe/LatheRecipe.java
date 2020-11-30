package theking530.staticpower.data.crafting.wrappers.lathe;

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

public class LatheRecipe extends AbstractMachineRecipe {
	public static final IRecipeType<LatheRecipe> RECIPE_TYPE = IRecipeType.register("lathe");

	private final StaticPowerIngredient[] inputs;
	private final ProbabilityItemStackOutput primaryOutput;
	private final ProbabilityItemStackOutput secondaryOutput;
	private final FluidStack outputFluid;

	public LatheRecipe(ResourceLocation name, StaticPowerIngredient[] inputs, ProbabilityItemStackOutput primaryOutput, ProbabilityItemStackOutput secondaryOutput, FluidStack outputFluid,
			int processingTime, int powerCost) {
		super(name, processingTime, powerCost);

		// Limit recipes to the max size.
		if (inputs.length != 4) {
			this.inputs = new StaticPowerIngredient[4];
			System.arraycopy(inputs, 0, this.inputs, 0, inputs.length);
		} else {
			this.inputs = inputs;
		}

		// Fill the null slots with empty.
		for (int i = 0; i < this.inputs.length; i++) {
			if (this.inputs[i] == null) {
				this.inputs[i] = StaticPowerIngredient.EMPTY;
			}
		}

		this.primaryOutput = primaryOutput;
		this.secondaryOutput = secondaryOutput;
		this.outputFluid = outputFluid;
	}

	public StaticPowerIngredient[] getInputs() {
		return inputs;
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
		// Check items.
		if (matchParams.shouldVerifyItems()) {
			if (matchParams.getItems().length != this.inputs.length) {
				return false;
			}
			for (int i = 0; i < inputs.length; i++) {
				if (!inputs[i].test(matchParams.getItems()[i], matchParams.shouldVerifyItemCounts())) {
					return false;
				}
			}
		}

		return true;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return LatheRecipeSerializer.INSTANCE;
	}

	@Override
	public IRecipeType<?> getType() {
		return RECIPE_TYPE;
	}
}
