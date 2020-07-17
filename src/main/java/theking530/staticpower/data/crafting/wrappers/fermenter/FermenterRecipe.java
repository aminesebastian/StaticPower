package theking530.staticpower.data.crafting.wrappers.fermenter;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.data.crafting.wrappers.AbstractStaticPowerRecipe;
import theking530.staticpower.data.crafting.wrappers.RecipeMatchParameters;

public class FermenterRecipe extends AbstractStaticPowerRecipe {
	public static final IRecipeType<FermenterRecipe> RECIPE_TYPE = IRecipeType.register("fermenter");

	private final FluidStack outputFluidStack;
	private final Ingredient inputIngredient;

	public FermenterRecipe(ResourceLocation name, Ingredient input, FluidStack fluid) {
		super(name);
		outputFluidStack = fluid;
		inputIngredient = input;
	}

	public FluidStack getOutputFluidStack() {
		return outputFluidStack;
	}

	public Ingredient getInputIngredient() {
		return inputIngredient;
	}

	public boolean isValid(RecipeMatchParameters matchParams) {
		return inputIngredient.test(matchParams.getItems()[0]);
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
