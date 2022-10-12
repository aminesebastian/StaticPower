package theking530.staticpower.data.crafting.wrappers.turbine;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.data.crafting.AbstractStaticPowerRecipe;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeType;

public class TurbineRecipe extends AbstractStaticPowerRecipe {
	public static final String ID = "turbine";
	public static final RecipeType<TurbineRecipe> RECIPE_TYPE = new StaticPowerRecipeType<TurbineRecipe>();

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
	public RecipeSerializer<TurbineRecipe> getSerializer() {
		return TurbineRecipeSerializer.INSTANCE;
	}

	@Override
	public RecipeType<TurbineRecipe> getType() {
		return RECIPE_TYPE;
	}
}
