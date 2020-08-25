package theking530.staticpower.data.crafting.wrappers.condensation;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.data.crafting.AbstractMachineRecipe;
import theking530.staticpower.data.crafting.RecipeMatchParameters;

public class CondensationRecipe extends AbstractMachineRecipe {
	public static final IRecipeType<CondensationRecipe> RECIPE_TYPE = IRecipeType.register("condensation");

	private final FluidStack inputFluid;
	private final FluidStack outputFluid;
	private final float heatGeneration;

	public CondensationRecipe(ResourceLocation name, FluidStack inputFluid, FluidStack outputFluid, int processingTime, float heatGeneration) {
		super(name, processingTime, 0);
		this.inputFluid = inputFluid;
		this.outputFluid = outputFluid;
		this.heatGeneration = heatGeneration;
	}

	public FluidStack getInputFluid() {
		return inputFluid;
	}

	public FluidStack getOutputFluid() {
		return outputFluid;
	}

	public float getHeatGeneration() {
		return heatGeneration;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return CondensationRecipeSerializer.INSTANCE;
	}

	@Override
	public IRecipeType<?> getType() {
		return RECIPE_TYPE;
	}

	public boolean isValid(RecipeMatchParameters matchParams) {
		if (matchParams.hasFluids() && inputFluid.isFluidEqual(matchParams.getFluids()[0])) {
			if (matchParams.shouldVerifyFluidAmounts()) {
				return matchParams.getFluids()[0].getAmount() >= inputFluid.getAmount();
			} else {
				return true;
			}
		}
		return false;
	}
}
