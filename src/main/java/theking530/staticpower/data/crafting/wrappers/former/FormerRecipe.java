package theking530.staticpower.data.crafting.wrappers.former;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import theking530.staticpower.data.crafting.AbstractMachineRecipe;
import theking530.staticpower.data.crafting.RecipeMatchParameters;

public class FormerRecipe extends AbstractMachineRecipe {
	public static final IRecipeType<FormerRecipe> RECIPE_TYPE = IRecipeType.register("former");

	private Ingredient inputIngredient;
	private Ingredient requiredMold;
	private ItemStack outputItemStack;

	public FormerRecipe(ResourceLocation name, int processingTime, int powerCost, ItemStack output, Ingredient input, Ingredient mold) {
		super(name, processingTime, powerCost);
		inputIngredient = input;
		requiredMold = mold;
		outputItemStack = output;
	}

	@Override
	public boolean isValid(RecipeMatchParameters matchParams) {
		if (matchParams.getItems().length != 2) {
			return false;
		}
		return inputIngredient.test(matchParams.getItems()[0]) && requiredMold.test(matchParams.getItems()[1]);
	}

	public Ingredient getInputIngredient() {
		return inputIngredient;
	}

	@Override
	public ItemStack getCraftingResult(IInventory inv) {
		return outputItemStack;
	}

	@Override
	public ItemStack getRecipeOutput() {
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
