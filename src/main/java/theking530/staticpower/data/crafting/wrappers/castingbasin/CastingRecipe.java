package theking530.staticpower.data.crafting.wrappers.castingbasin;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.data.crafting.AbstractMachineRecipe;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.data.crafting.StaticPowerOutputItem;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeType;

public class CastingRecipe extends AbstractMachineRecipe {
	public static final String ID = "casting";
	public static final RecipeType<CastingRecipe> RECIPE_TYPE = new StaticPowerRecipeType<CastingRecipe>();

	private FluidStack inputFluid;
	private Ingredient requiredMold;
	private StaticPowerOutputItem outputItemStack;

	public CastingRecipe(ResourceLocation name, StaticPowerOutputItem output, FluidStack inputFluid, Ingredient mold, MachineRecipeProcessingSection processing) {
		super(name, processing);
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
		return outputItemStack.getItemStack();
	}

	public StaticPowerOutputItem getOutput() {
		return outputItemStack;
	}

	public Ingredient getRequiredMold() {
		return requiredMold;
	}

	@Override
	public RecipeSerializer<CastingRecipe> getSerializer() {
		return CastingRecipeSerializer.INSTANCE;
	}

	@Override
	public RecipeType<CastingRecipe> getType() {
		return RECIPE_TYPE;
	}
}
