package theking530.staticpower.data.crafting.wrappers.former;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import theking530.staticpower.data.crafting.AbstractMachineRecipe;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerIngredient;

public class FormerRecipe extends AbstractMachineRecipe {
	public static final IRecipeType<FormerRecipe> RECIPE_TYPE = IRecipeType.register("former");

	private StaticPowerIngredient inputIngredient;
	private Ingredient requiredMold;
	private ProbabilityItemStackOutput outputItemStack;

	public FormerRecipe(ResourceLocation name, int processingTime, long powerCost, ProbabilityItemStackOutput output, StaticPowerIngredient input, Ingredient mold) {
		super(name, processingTime, powerCost);
		inputIngredient = input;
		requiredMold = mold;
		outputItemStack = output;
	}

	@Override
	public boolean isValid(RecipeMatchParameters matchParams) {
		boolean matched = true;

		// Check items.
		if (matchParams.shouldVerifyItems()) {
			// Check to make sure we got two items.
			if (matchParams.getItems().length != 2) {
				return false;
			}

			matched &= matchParams.hasItems() && inputIngredient.test(matchParams.getItems()[0], matchParams.shouldVerifyItemCounts());
			matched &= matchParams.hasItems() && requiredMold.test(matchParams.getItems()[1]);
		}

		return matched;
	}

	public StaticPowerIngredient getInputIngredient() {
		return inputIngredient;
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
		return FormerRecipeSerializer.INSTANCE;
	}

	@Override
	public IRecipeType<?> getType() {
		return RECIPE_TYPE;
	}
}
