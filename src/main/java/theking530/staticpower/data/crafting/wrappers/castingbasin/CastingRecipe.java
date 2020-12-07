package theking530.staticpower.data.crafting.wrappers.castingbasin;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.data.crafting.AbstractMachineRecipe;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.RecipeMatchParameters;

public class CastingRecipe extends AbstractMachineRecipe {
	public static final IRecipeType<CastingRecipe> RECIPE_TYPE = IRecipeType.register("casting");

	private FluidStack inputFluid;
	private Ingredient requiredMold;
	private ProbabilityItemStackOutput outputItemStack;

	public CastingRecipe(ResourceLocation name, int processingTime, int powerCost, ProbabilityItemStackOutput output, FluidStack inputFluid, Ingredient mold) {
		super(name, processingTime, powerCost);
		this.inputFluid = inputFluid;
		requiredMold = mold;
		outputItemStack = output;
	}

	@Override
	public boolean isValid(RecipeMatchParameters matchParams) {
		boolean matched = true;

		// Check items.
		if (matchParams.shouldVerifyItems() && matchParams.hasItems()) {
			matched &= requiredMold.test(matchParams.getItems()[0]);
		}

		// Check fluids.
		if (matchParams.shouldVerifyFluids() && matchParams.hasFluids()) {
			matched &= matchParams.getFluids()[0].equals(inputFluid);
			if (matched && matchParams.shouldVerifyFluidAmounts()) {
				matched &= matchParams.getFluids()[0].getAmount() >= inputFluid.getAmount();
			}
		}

		return matched;

	}

	public FluidStack getInputFluid() {
		return inputFluid;
	}

	public ItemStack getRawRecipeOutput() {
		return outputItemStack.getItem();
	}

	public ProbabilityItemStackOutput getOutput() {
		return outputItemStack;
	}

	public Ingredient getRequiredMold() {
		return requiredMold;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return CastingRecipeSerializer.INSTANCE;
	}

	@Override
	public IRecipeType<?> getType() {
		return RECIPE_TYPE;
	}
}
