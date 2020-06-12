package theking530.staticpower.crafting.wrappers;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Optional;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import theking530.staticpower.StaticPower;

public class StaticPowerRecipeRegistry {

	@SuppressWarnings("rawtypes")
	public static final HashMap<IRecipeType, LinkedList<AbstractStaticPowerRecipe>> RECIPES = new HashMap<IRecipeType, LinkedList<AbstractStaticPowerRecipe>>();

	/**
	 * Attempts to find a recipe of the given type that matches the provided
	 * parameters.
	 * 
	 * @param <T>             The type of the recipe.
	 * @param recipeType      The {@link IRecipeType} of the recipe.
	 * @param matchParameters The match parameters to used.
	 * @return Optional of the recipe if it exists, otherwise empty.
	 */
	@SuppressWarnings("unchecked")
	public static <T extends AbstractStaticPowerRecipe> Optional<T> getRecipe(IRecipeType<T> recipeType, RecipeMatchParameters matchParameters) {
		// If no recipes of this type exist, return empty.
		if (!RECIPES.containsKey(recipeType)) {
			return Optional.empty();
		}

		// Iterate through the recipe linked list and return the first instance that
		// matches.
		for (AbstractStaticPowerRecipe recipe : RECIPES.get(recipeType)) {
			if (recipe.isValid(matchParameters)) {
				return Optional.of((T) recipe);
			}
		}

		// If we find no match, return empty.
		return Optional.empty();
	}

	/**
	 * Adds a recipe to the recipes list.
	 * 
	 * @param recipe
	 */
	private static void addRecipe(AbstractStaticPowerRecipe recipe) {
		if (!RECIPES.containsKey(recipe.getType())) {
			RECIPES.put(recipe.getType(), new LinkedList<AbstractStaticPowerRecipe>());
		}
		RECIPES.get(recipe.getType()).add(recipe);
	}

	/**
	 * This event is raised when the resources are loaded/reloaded.
	 */
	public static void onResourcesReloaded(RecipesUpdatedEvent event) {
		// Capture if this is the first time we are caching.
		boolean firstTime = RECIPES.size() == 0;

		// Log that caching has started.
		StaticPower.LOGGER.info(String.format("%1$s Static Power recipes.", (firstTime ? "caching" : "re-caching")));

		// Clear the recipes list.
		RECIPES.clear();

		// Keep track of how many recipes are cached.
		int recipeCount = 0;

		// Iterate through all the recipes and cache the Static Power ones.
		Collection<IRecipe<?>> recipes = event.getRecipeManager().getRecipes();
		for (IRecipe<?> recipe : recipes) {
			if (recipe instanceof AbstractStaticPowerRecipe) {
				addRecipe((AbstractStaticPowerRecipe) recipe);
				recipeCount++;
			}
		}

		// Log the completion.
		StaticPower.LOGGER.info(String.format("Succesfully %1$s %2$d Static Power recipes.", (firstTime ? "cached" : "re-cached"), recipeCount));
	}

}
