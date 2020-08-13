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

	public CondensationRecipe(ResourceLocation name, FluidStack inputFluid, FluidStack outputFluid, int processingCost, int processingTime) {
		super(name, processingCost, processingTime);
		this.inputFluid = inputFluid;
		this.outputFluid = outputFluid;
	}

	public FluidStack getInputFluid() {
		return inputFluid;
	}

	public FluidStack getOutputFluid() {
		return outputFluid;
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
		return matchParams.hasFluids() && inputFluid.isFluidEqual(matchParams.getFluids()[0]) && matchParams.getFluids()[0].getAmount() >= inputFluid.getAmount();
	}
}
