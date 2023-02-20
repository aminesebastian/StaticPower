package theking530.staticpower.data.crafting.wrappers.former;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import theking530.staticpower.data.crafting.AbstractMachineRecipe;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.data.crafting.StaticPowerOutputItem;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeType;

public class FormerRecipe extends AbstractMachineRecipe {
	public static final String ID = "former";
	public static final RecipeType<FormerRecipe> RECIPE_TYPE = new StaticPowerRecipeType<FormerRecipe>();

	private StaticPowerIngredient inputIngredient;
	private Ingredient requiredMold;
	private StaticPowerOutputItem outputItemStack;

	public FormerRecipe(ResourceLocation name, StaticPowerOutputItem output, StaticPowerIngredient input, Ingredient mold, MachineRecipeProcessingSection processing) {
		super(name, processing);
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
		return outputItemStack.getItemStack();
	}

	public StaticPowerOutputItem getOutput() {
		return outputItemStack;
	}

	public Ingredient getRequiredMold() {
		return requiredMold;
	}

	@Override
	public RecipeSerializer<FormerRecipe> getSerializer() {
		return FormerRecipeSerializer.INSTANCE;
	}

	@Override
	public RecipeType<FormerRecipe> getType() {
		return RECIPE_TYPE;
	}
}
