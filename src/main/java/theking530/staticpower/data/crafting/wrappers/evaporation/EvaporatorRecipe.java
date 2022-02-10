package theking530.staticpower.data.crafting.wrappers.evaporation;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.data.crafting.AbstractMachineRecipe;
import theking530.staticpower.data.crafting.RecipeMatchParameters;

public class EvaporatorRecipe extends AbstractMachineRecipe {
	public static final RecipeType<EvaporatorRecipe> RECIPE_TYPE = RecipeType.register("evaporation");

	private final FluidStack inputFluid;
	private final FluidStack outputFluid;
	private final float requiredHeat;

	public EvaporatorRecipe(ResourceLocation name, FluidStack inputFluid, FluidStack outputFluid, float requiredHeat, int processingTime) {
		super(name, processingTime, 0);
		this.inputFluid = inputFluid;
		this.outputFluid = outputFluid;
		this.requiredHeat = requiredHeat;
	}

	public FluidStack getInputFluid() {
		return inputFluid;
	}

	public FluidStack getOutputFluid() {
		return outputFluid;
	}

	public float getRequiredHeat() {
		return requiredHeat;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return EvaporatorRecipeSerializer.INSTANCE;
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
