package theking530.staticpower.data.crafting.wrappers.vulcanizer;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.data.crafting.AbstractMachineRecipe;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.RecipeMatchParameters;

public class VulcanizerRecipe extends AbstractMachineRecipe {
	public static final IRecipeType<VulcanizerRecipe> RECIPE_TYPE = IRecipeType.register("vulcanizer");

	private final FluidStack inputFluid;
	private final ProbabilityItemStackOutput output;

	public VulcanizerRecipe(ResourceLocation name, int processingTime, int powerCost, FluidStack inputFluid, ProbabilityItemStackOutput output) {
		super(name, processingTime, powerCost);
		this.output = output;
		this.inputFluid = inputFluid;
	}

	public ProbabilityItemStackOutput getOutput() {
		return output;
	}

	public FluidStack getInputFluid() {
		return inputFluid;
	}

	public ItemStack getRawOutputItem() {
		return output.getItem();
	}

	@Override
	public boolean isValid(RecipeMatchParameters matchParams) {
		boolean matched = true;

		// Check fluid.
		if (matchParams.shouldVerifyFluids()) {
			matched &= matchParams.hasFluids();
			matched &= inputFluid.isFluidEqual(matchParams.getFluids()[0]);
			if (matchParams.shouldVerifyFluidAmounts()) {
				matched &= matchParams.getFluids()[0].getAmount() >= inputFluid.getAmount();
			}
		}

		return matched;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return VulcanizerRecipeSerializer.INSTANCE;
	}

	@Override
	public IRecipeType<?> getType() {
		return RECIPE_TYPE;
	}
}