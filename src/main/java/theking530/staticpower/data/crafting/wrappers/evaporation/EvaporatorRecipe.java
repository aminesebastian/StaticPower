package theking530.staticpower.data.crafting.wrappers.evaporation;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.data.crafting.AbstractMachineRecipe;
import theking530.staticpower.data.crafting.RecipeMatchParameters;

public class EvaporatorRecipe extends AbstractMachineRecipe {
	public static final IRecipeType<EvaporatorRecipe> RECIPE_TYPE = IRecipeType.register("evaporation");

	private final FluidStack inputFluid;
	private final FluidStack outputFluid;
	private final float requiredHeat;

	public EvaporatorRecipe(ResourceLocation name, FluidStack inputFluid, FluidStack outputFluid, float requiredHeat, int processingTime) {
		super(name, 0, processingTime);
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
	public IRecipeSerializer<?> getSerializer() {
		return EvaporatorRecipeSerializer.INSTANCE;
	}

	@Override
	public IRecipeType<?> getType() {
		return RECIPE_TYPE;
	}

	public boolean isValid(RecipeMatchParameters matchParams) {
		return matchParams.hasFluids() && inputFluid.isFluidEqual(matchParams.getFluids()[0]) && matchParams.getFluids()[0].getAmount() >= inputFluid.getAmount();
	}
}
