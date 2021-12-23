package theking530.staticpower.data.crafting.wrappers.castingbasin;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.data.crafting.AbstractMachineRecipe;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.RecipeMatchParameters;

public class CastingRecipe extends AbstractMachineRecipe {
	public static final RecipeType<CastingRecipe> RECIPE_TYPE = RecipeType.register("casting");

	private FluidStack inputFluid;
	private Ingredient requiredMold;
	private ProbabilityItemStackOutput outputItemStack;

	public CastingRecipe(ResourceLocation name, int processingTime, long powerCost, ProbabilityItemStackOutput output, FluidStack inputFluid, Ingredient mold) {
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
	public RecipeSerializer<?> getSerializer() {
		return CastingRecipeSerializer.INSTANCE;
	}

	@Override
	public RecipeType<?> getType() {
		return RECIPE_TYPE;
	}
}
