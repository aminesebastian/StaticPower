package theking530.staticpower.data.crafting.wrappers.turbine;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.data.crafting.AbstractStaticPowerRecipe;
import theking530.staticpower.data.crafting.RecipeMatchParameters;

public class TurbineRecipe extends AbstractStaticPowerRecipe {
	public static final IRecipeType<TurbineRecipe> RECIPE_TYPE = IRecipeType.register("turbine");

	private final FluidStack input;
	private final FluidStack output;
	private final int generationAmount;

	public TurbineRecipe(ResourceLocation name, FluidStack input, FluidStack output, int generationAmount) {
		super(name);
		this.input = input;
		this.output = output;
		this.generationAmount = generationAmount;
	}

	public FluidStack getInput() {
		return input;
	}

	public FluidStack getOutput() {
		return output;
	}

	public boolean hasOutput() {
		return !output.isEmpty();
	}

	public int getGenerationAmount() {
		return generationAmount;
	}

	@Override
	public boolean isValid(RecipeMatchParameters matchParams) {
		if (matchParams.shouldVerifyFluids() && matchParams.hasFluids()) {
			if (matchParams.getFluids()[0].isFluidEqual(input)) {
				if (matchParams.shouldVerifyFluidAmounts()) {
					return matchParams.getFluids()[0].getAmount() >= input.getAmount();
				} else {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return TurbineRecipeSerializer.INSTANCE;
	}

	@Override
	public IRecipeType<?> getType() {
		return RECIPE_TYPE;
	}
}
