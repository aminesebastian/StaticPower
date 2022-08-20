package theking530.staticpower.integration.JEI.categories.smithing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.advanced.IRecipeManagerPlugin;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import theking530.api.attributes.capability.CapabilityAttributable;
import theking530.staticpower.data.crafting.StaticPowerRecipeRegistry;
import theking530.staticpower.data.crafting.wrappers.autosmith.AutoSmithRecipe;

public class SmithingRecipeProvider implements IRecipeManagerPlugin {
	private static List<SmithingRecipeJEIWrapper> RECIPES;

	public SmithingRecipeProvider() {
	}

	public static List<SmithingRecipeJEIWrapper> getRecipes() {
		// Create the recipes array.
		RECIPES = new ArrayList<SmithingRecipeJEIWrapper>();

		// Get all the registered items and add any recipes that can be created from
		// them.
		for (Item item : ForgeRegistries.ITEMS.getValues()) {
			RECIPES.addAll(makeFromSmithingInput(new ItemStack(item)));
		}

		return RECIPES;
	}

	@Override
	public <V> List<ResourceLocation> getRecipeCategoryUids(IFocus<V> focus) {
		if (focus.getRole() == RecipeIngredientRole.OUTPUT && focus.getTypedValue().getIngredient() instanceof ItemStack) {
			// Get the focused item.
			ItemStack itemStack = (ItemStack) focus.getTypedValue().getIngredient();

			// Check to see if it is a valid smithing output.
			if (isValidSmithingOutput(itemStack)) {
				return Collections.singletonList(SmithingRecipeCategory.UID);
			}
		} else if (focus.getRole() == RecipeIngredientRole.INPUT && focus.getTypedValue().getIngredient() instanceof ItemStack) {
			// Check if the input is used in any smithing recipes or is itself smithable.
			ItemStack itemStack = (ItemStack) focus.getTypedValue().getIngredient();
			if (isValidSmithingInput(itemStack)) {
				return Collections.singletonList(SmithingRecipeCategory.UID);
			}
		}

		return Collections.emptyList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> getRecipes(IRecipeCategory<T> recipeCategory) {
		// Check the category.
		if (recipeCategory != null && !SmithingRecipeCategory.UID.equals(recipeCategory.getRecipeType().getUid())) {
			return Collections.emptyList();
		}

		// Clear the recipes array.
		RECIPES.clear();

		// Get all the registered items and add any recipes that can be created from
		// them.
		for (Item item : ForgeRegistries.ITEMS.getValues()) {
			RECIPES.addAll(makeFromSmithingInput(new ItemStack(item)));
		}

		// Return recipes.
		return (List<T>) RECIPES;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T, V> List<T> getRecipes(IRecipeCategory<T> recipeCategory, IFocus<V> focus) {
		if (!SmithingRecipeCategory.UID.equals(recipeCategory.getRecipeType().getUid())) {
			return Collections.emptyList();
		}

		if (focus.getRole() == RecipeIngredientRole.OUTPUT && focus.getTypedValue().getIngredient() instanceof ItemStack) {
			return Collections.emptyList();
		} else if (focus.getRole() == RecipeIngredientRole.INPUT && focus.getTypedValue().getIngredient() instanceof ItemStack) {
			// Get the focused item stack and item.
			ItemStack focusedItem = (ItemStack) focus.getTypedValue().getIngredient();
			return (List<T>) makeFromSmithingInput(focusedItem);
		}

		return Collections.emptyList();
	}

	private static List<SmithingRecipeJEIWrapper> makeFromSmithingInput(ItemStack input) {
		// Allocate the output.
		List<SmithingRecipeJEIWrapper> output = new ArrayList<SmithingRecipeJEIWrapper>();

		// Get all smithing reciepes.
		List<AutoSmithRecipe> recipes = StaticPowerRecipeRegistry.getRecipesOfType(AutoSmithRecipe.RECIPE_TYPE);

		// Iterate through all the recipes.
		for (AutoSmithRecipe recipe : recipes) {
			// If the recipe is not item requested OR if the recipe accepts the input.
			if (recipe.isWildcardRecipe() || recipe.getSmithTarget().test(input)) {
				ItemStack outputItem = input.copy();
				if (recipe.applyToItemStack(outputItem) || recipe.performsRepair()) {
					try {
						output.add(new SmithingRecipeJEIWrapper(recipe, input, outputItem));
					} catch (Exception e) {
						System.out.println(e);
					}

				}
			}
		}

		// Return the recipes that were processable.
		return output;
	}

	private static boolean isValidSmithingOutput(ItemStack stack) {
		if (stack.getCapability(CapabilityAttributable.ATTRIBUTABLE_CAPABILITY).isPresent()) {
			return true;
		}

		// Test for smith targets that dont have attributable marks.
		List<AutoSmithRecipe> recipes = StaticPowerRecipeRegistry.getRecipesOfType(AutoSmithRecipe.RECIPE_TYPE);
		for (AutoSmithRecipe recipe : recipes) {
			if (!recipe.isWildcardRecipe() && recipe.getSmithTarget().test(stack)) {
				return true;
			}
		}

		return false;
	}

	private static boolean isValidSmithingInput(ItemStack stack) {
		// Test to see if the item has the capability.
		if (stack.getCapability(CapabilityAttributable.ATTRIBUTABLE_CAPABILITY).isPresent()) {
			return true;
		}

		// Test for modifier materials or input materials.
		List<AutoSmithRecipe> recipes = StaticPowerRecipeRegistry.getRecipesOfType(AutoSmithRecipe.RECIPE_TYPE);
		for (AutoSmithRecipe recipe : recipes) {
			if (recipe.getModifierMaterial().test(stack)) {
				return true;
			}
			if (!recipe.isWildcardRecipe() && recipe.getSmithTarget().test(stack)) {
				return true;
			}
		}

		// If none of the tests pass, return false.
		return false;
	}
}