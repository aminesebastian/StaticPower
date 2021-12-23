package theking530.staticpower.data.crafting.wrappers.former;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.resources.ResourceLocation;
import theking530.staticpower.data.crafting.AbstractMachineRecipe;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerIngredient;

public class FormerRecipe extends AbstractMachineRecipe {
	public static final RecipeType<FormerRecipe> RECIPE_TYPE = RecipeType.register("former");

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
	public RecipeSerializer<?> getSerializer() {
		return FormerRecipeSerializer.INSTANCE;
	}

	@Override
	public RecipeType<?> getType() {
		return RECIPE_TYPE;
	}
}
