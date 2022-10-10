package theking530.staticpower.integration.JEI.categories.packager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.advanced.IRecipeManagerPlugin;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.registries.ForgeRegistries;
import theking530.staticpower.data.crafting.StaticPowerRecipeRegistry;
import theking530.staticpower.data.crafting.wrappers.packager.PackagerRecipe;
import theking530.staticpower.integration.JEI.categories.PackagerRecipeCategory;
import theking530.staticpower.utilities.ItemUtilities;

public class PackagerRecipeProvider implements IRecipeManagerPlugin {
	private static final List<PackagerRecipe> RECIPES = new ArrayList<>();

	public PackagerRecipeProvider() {
	}

	public static List<PackagerRecipe> getRecipes() {
		RECIPES.clear();
		RECIPES.addAll(getRecipeManager().getAllRecipesFor(PackagerRecipe.RECIPE_TYPE));

		// Iterate through all items.
		for (Item item : ForgeRegistries.ITEMS) {
			Optional<PackagerRecipe> twoRecipe = StaticPowerRecipeRegistry.getPackagerRecipe(getRecipeManager(), new ItemStack(item), 2);
			if (twoRecipe.isPresent()) {
				RECIPES.add(twoRecipe.get());
			}

			Optional<PackagerRecipe> threeRecipe = StaticPowerRecipeRegistry.getPackagerRecipe(getRecipeManager(), new ItemStack(item), 2);
			if (threeRecipe.isPresent()) {
				RECIPES.add(threeRecipe.get());
			}
		}
		return RECIPES;
	}

	@Override
	public <V> List<mezz.jei.api.recipe.RecipeType<?>> getRecipeTypes(IFocus<V> focus) {
		if (focus.getTypedValue().getIngredient() instanceof ItemStack) {
			// Get the focused item.
			ItemStack itemStack = (ItemStack) focus.getTypedValue().getIngredient();
			if (focus.getRole() == RecipeIngredientRole.OUTPUT) {
				if (isValidPackagerOutput(itemStack)) {
					return Collections.singletonList(PackagerRecipeCategory.TYPE);
				}
			} else if (focus.getRole() == RecipeIngredientRole.INPUT) {
				if (isValidPackagerInput(itemStack)) {
					return Collections.singletonList(PackagerRecipeCategory.TYPE);
				}
			}
		}
		return Collections.emptyList();
	}

	private static boolean isValidPackagerInput(ItemStack stack) {
		for (PackagerRecipe recipe : RECIPES) {
			if (recipe.getInputIngredient().test(stack)) {
				return true;
			}
		}
		return false;
	}

	private static boolean isValidPackagerOutput(ItemStack stack) {
		for (PackagerRecipe recipe : RECIPES) {
			if (ItemUtilities.areItemStacksStackable(recipe.getOutput().getItem(), stack)) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> getRecipes(IRecipeCategory<T> recipeCategory) {
		// Check the category.
		if (recipeCategory != null && !PackagerRecipeCategory.TYPE.equals(recipeCategory.getRecipeType())) {
			return Collections.emptyList();
		}

		// Return the recipes.
		return (List<T>) getRecipes();
	}

	@Override
	public <T, V> List<T> getRecipes(IRecipeCategory<T> recipeCategory, IFocus<V> focus) {
		return Collections.emptyList();
	}

	@SuppressWarnings("resource")
	private static RecipeManager getRecipeManager() {
		return Minecraft.getInstance().level.getRecipeManager();
	}
}