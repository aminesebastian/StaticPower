package theking530.staticcore.crafting;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import theking530.staticcore.StaticCore;
import theking530.staticcore.init.StaticCoreRecipeTypes;
import theking530.staticcore.research.Research;
import theking530.staticcore.research.ResearchUnlock;
import theking530.staticcore.research.ResearchUnlock.ResearchUnlockType;
import theking530.staticcore.teams.ServerTeam;

public class StaticCoreRecipeManager {
	private static final HashMap<RecipeType<?>, HashMap<ResourceLocation, AbstractStaticPowerRecipe>> RECIPES = new HashMap<>();
	private static final Map<ResourceLocation, SmeltingRecipe> FURNACE_RECIPES = new HashMap<>();
	private static final Map<ResourceLocation, CraftingRecipe> CRAFTING_RECIPES = new HashMap<>();
	private static final Map<ResourceLocation, Set<ResourceLocation>> LOCKED_RECIPES = new LinkedHashMap<>();

	public static final HashMap<RecipeType<?>, HashMap<ResourceLocation, AbstractStaticPowerRecipe>> getRecipes() {
		return RECIPES;
	}

	/**
	 * Attempts to find a recipe by the provided id.
	 * 
	 * @param <T>             The type of the recipe.
	 * @param recipeType      The {@link IRecipeType} of the recipe.
	 * @param matchParameters The match parameters to used.
	 * @return Optional of the recipe if it exists, otherwise empty.
	 */
	@SuppressWarnings("unchecked")
	public static <T extends AbstractStaticPowerRecipe> Optional<T> getRecipe(RecipeType<T> recipeType,
			ResourceLocation id) {
		// If no recipes of this type exist, return empty.
		if (!RECIPES.containsKey(recipeType)) {
			return Optional.empty();
		}

		if (RECIPES.containsKey(recipeType)) {
			return Optional.ofNullable((T) RECIPES.get(recipeType).get(id));
		}

		// If we find no match, return empty.
		return Optional.empty();
	}

	@SuppressWarnings("unchecked")
	public static <T extends Recipe<?>> Optional<T> getRawRecipe(RecipeType<T> recipeType, ResourceLocation id) {
		// If no recipes of this type exist, return empty.
		if (recipeType == RecipeType.CRAFTING) {
			if (CRAFTING_RECIPES.containsKey(id)) {
				return Optional.of((T) CRAFTING_RECIPES.get(id));
			}
		} else if (recipeType == RecipeType.SMELTING) {
			if (FURNACE_RECIPES.containsKey(id)) {
				return Optional.of((T) FURNACE_RECIPES.get(id));
			}
		} else if (RECIPES.containsKey(recipeType)) {
			return Optional.ofNullable((T) RECIPES.get(recipeType).get(id));
		}

		// If we find no match, return empty.
		return Optional.empty();
	}

	public static Optional<CraftingRecipe> getCraftingRecipe(ResourceLocation id) {
		if (CRAFTING_RECIPES.containsKey(id)) {
			return Optional.of(CRAFTING_RECIPES.get(id));
		}
		return Optional.empty();
	}

	public static Map<ResourceLocation, SmeltingRecipe> getAllFurnaceRecipes() {
		return FURNACE_RECIPES;
	}

	/**
	 * This event is raised when the resources are loaded/reloaded.
	 */
	public static void onResourcesReloaded(RecipeManager manager) {
		// Capture if this is the first time we are caching.
		boolean firstTime = RECIPES.size() == 0;

		// Log that caching has started.
		StaticCore.LOGGER.info(String.format("%1$s recipes.", (firstTime ? "Caching" : "Re-caching")));

		// Clear the recipe lists.
		RECIPES.clear();
		FURNACE_RECIPES.clear();
		CRAFTING_RECIPES.clear();

		// Get all the recipes and populate them into a map.
		Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> newRecipes = new HashMap<>();
		for (Recipe<?> recipe : manager.getRecipes()) {
			if (!newRecipes.containsKey(recipe.getType())) {
				newRecipes.put(recipe.getType(), new HashMap<>());
			}
			newRecipes.get(recipe.getType()).put(recipe.getId(), recipe);
		}

		// Handle research replacements.
		handleCraftingResearchRecipeReplacement(manager,
				newRecipes.getOrDefault(RecipeType.CRAFTING, new HashMap<ResourceLocation, Recipe<?>>()));
		// Replace the recipes.
		// TODO: This feels so dirty, wish I could just add recipes.
		List<Recipe<?>> collectedNewRecipes = newRecipes.values().stream().flatMap((x) -> x.values().stream()).toList();
		manager.replaceRecipes(collectedNewRecipes);

		// Iterate through all the recipes and cache the Static Power ones.
		for (Recipe<?> recipe : manager.getRecipes()) {
			if (recipe instanceof AbstractStaticPowerRecipe) {
				cacheRecipe((AbstractStaticPowerRecipe) recipe);
			} else if (recipe.getType() == RecipeType.SMELTING) {
				// Cache smelting recipes.
				FURNACE_RECIPES.put(recipe.getId(), (SmeltingRecipe) recipe);
			} else if (recipe.getType() == RecipeType.CRAFTING) {
				// Cache crafting recipes.
				CRAFTING_RECIPES.put(recipe.getId(), (CraftingRecipe) recipe);
			}
		}
		// Log the completion.
		StaticCore.LOGGER
				.info(String.format("Succesfully %1$s %2$d Static Power recipes.", (firstTime ? "cached" : "re-cached"),
						RECIPES.size() + FURNACE_RECIPES.size() + CRAFTING_RECIPES.size()));
	}

	protected static Map<ResourceLocation, Recipe<?>> getOrPutDefault(
			Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> recipes, RecipeType<?> type) {
		if (!recipes.containsKey(type)) {
			recipes.put(type, new HashMap<ResourceLocation, Recipe<?>>());
		}
		return recipes.get(type);
	}

	public static List<ResourceLocation> getMissingResearchForRecipe(ResourceLocation recipeId, ServerTeam team) {
		// If the recipe is locked, capture all the research that still needs to be
		// completed.
		if (LOCKED_RECIPES.containsKey(recipeId)) {
			return LOCKED_RECIPES.get(recipeId).stream()
					.filter((resourceLocation) -> !team.getResearchManager().hasCompletedResearch(resourceLocation))
					.toList();
		}

		return Collections.emptyList();
	}

	/**
	 * Adds a recipe to the recipes list.
	 * 
	 * @param recipe
	 */
	private static void cacheRecipe(AbstractStaticPowerRecipe recipe) {
		if (!RECIPES.containsKey(recipe.getType())) {
			RECIPES.put(recipe.getType(), new HashMap<ResourceLocation, AbstractStaticPowerRecipe>());
		}
		RECIPES.get(recipe.getType()).put(recipe.getId(), recipe);
	}

	private static void handleCraftingResearchRecipeReplacement(RecipeManager manager,
			Map<ResourceLocation, Recipe<?>> newRecipes) {
		// Clear the previously cached lockable recipes.
		LOCKED_RECIPES.clear();

		// Capture all the lockable recipes mapped to the set of required research for
		// them.
		for (Research research : manager.getAllRecipesFor(StaticCoreRecipeTypes.RESEARCH_RECIPE_TYPE.get())) {
			for (ResearchUnlock unlock : research.getUnlocks()) {
				if (unlock.getType() == ResearchUnlockType.CRAFTING
						|| unlock.getType() == ResearchUnlockType.MACHINE_RECIPE) {
					if (!LOCKED_RECIPES.containsKey(unlock.getTarget())) {
						LOCKED_RECIPES.put(unlock.getTarget(), new HashSet<ResourceLocation>());
					}
					LOCKED_RECIPES.get(unlock.getTarget()).add(research.getId());
				}
			}
		}

		// Replace any recipes that need to be replaced.
		for (Recipe<?> recipe : manager.getAllRecipesFor(RecipeType.CRAFTING)) {
			if (LOCKED_RECIPES.containsKey(recipe.getId())) {
				if (recipe instanceof ShapedRecipe) {
					ShapedRecipe craftingRecipe = (ShapedRecipe) recipe;
					newRecipes.put(recipe.getId(), craftingRecipe);
				} else if (recipe instanceof ShapelessRecipe) {
					ShapelessRecipe craftingRecipe = (ShapelessRecipe) recipe;
					newRecipes.put(recipe.getId(), craftingRecipe);
				}
			}
		}
	}
}
