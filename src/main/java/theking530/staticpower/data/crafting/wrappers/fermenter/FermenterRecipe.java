package theking530.staticpower.data.crafting.wrappers.fermenter;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.data.crafting.AbstractStaticPowerRecipe;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerIngredient;

public class FermenterRecipe extends AbstractStaticPowerRecipe {
	public static final IRecipeType<FermenterRecipe> RECIPE_TYPE = IRecipeType.register("fermenter");

	private final FluidStack outputFluidStack;
	private final StaticPowerIngredient input;

	public FermenterRecipe(ResourceLocation name, StaticPowerIngredient input, FluidStack fluid) {
		super(name);
		outputFluidStack = fluid;
		this.input = input;
	}

	public FluidStack getOutputFluidStack() {
		return outputFluidStack;
	}

	public StaticPowerIngredient getInputIngredient() {
		return input;
	}

	public boolean isValid(RecipeMatchParameters matchParams) {
		boolean matched = true;

		// Check items.
		if (matchParams.shouldVerifyItems()) {
			if (matchParams.shouldVerifyItemCounts()) {
				matched &= matchParams.hasItems() && input.testWithCount(matchParams.getItems()[0]);
			} else {
				matched &= matchParams.hasItems() && input.test(matchParams.getItems()[0]);
			}
		}

		return matched;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return FermenterRecipeSerializer.INSTANCE;
	}

	@Override
	public IRecipeType<?> getType() {
		return RECIPE_TYPE;
	}
}
