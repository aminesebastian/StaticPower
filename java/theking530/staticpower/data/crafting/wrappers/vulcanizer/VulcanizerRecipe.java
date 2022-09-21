package theking530.staticpower.data.crafting.wrappers.vulcanizer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.data.crafting.AbstractMachineRecipe;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeType;

public class VulcanizerRecipe extends AbstractMachineRecipe {
	public static final String ID = "vulcanizer";
	public static final RecipeType<VulcanizerRecipe> RECIPE_TYPE = new StaticPowerRecipeType<VulcanizerRecipe>();

	private final FluidStack inputFluid;
	private final ProbabilityItemStackOutput output;

	public VulcanizerRecipe(ResourceLocation name, FluidStack inputFluid, ProbabilityItemStackOutput output, MachineRecipeProcessingSection processing) {
		super(name, processing);
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
	public RecipeSerializer<?> getSerializer() {
		return VulcanizerRecipeSerializer.INSTANCE;
	}

	@Override
	public RecipeType<?> getType() {
		return RECIPE_TYPE;
	}
}