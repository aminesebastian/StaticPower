package theking530.staticpower.data.crafting.wrappers.condensation;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.data.crafting.AbstractMachineRecipe;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.data.crafting.RecipeMatchParameters;

public class CondensationRecipe extends AbstractMachineRecipe {
	public static final RecipeType<CondensationRecipe> RECIPE_TYPE = RecipeType.register("condensation");

	private final FluidStack inputFluid;
	private final FluidStack outputFluid;
	private final float heatGeneration;

	public CondensationRecipe(ResourceLocation name, FluidStack inputFluid, FluidStack outputFluid, float heatGeneration, MachineRecipeProcessingSection processing) {
		super(name, processing);
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
	public RecipeSerializer<?> getSerializer() {
		return CondensationRecipeSerializer.INSTANCE;
	}

	@Override
	public RecipeType<?> getType() {
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
