package theking530.staticpower.data.crafting;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.registries.ForgeRegistries;
import theking530.staticcore.fluid.FluidIngredient;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.container.FakeCraftingInventory;
import theking530.staticpower.data.crafting.wrappers.bottler.BottleRecipe;
import theking530.staticpower.data.crafting.wrappers.castingbasin.CastingRecipe;
import theking530.staticpower.data.crafting.wrappers.former.FormerRecipe;
import theking530.staticpower.data.crafting.wrappers.packager.PackagerRecipe;
import theking530.staticpower.data.crafting.wrappers.solidfuel.SolidFuelRecipe;
import theking530.staticpower.data.research.Research;
import theking530.staticpower.data.research.ResearchUnlock;
import theking530.staticpower.data.research.ResearchUnlock.ResearchUnlockType;
import theking530.staticpower.init.ModRecipeTypes;
import theking530.staticpower.teams.Team;

public class StaticPowerRecipeRegistry {
	public static final Logger LOGGER = LogManager.getLogger(StaticPowerRecipeRegistry.class);

	@SuppressWarnings("rawtypes")
	private static final HashMap<RecipeType, LinkedList<AbstractStaticPowerRecipe>> RECIPES = new HashMap<>();
	private static final Map<ResourceLocation, SmeltingRecipe> FURNACE_RECIPES = new HashMap<>();
	private static final Map<ResourceLocation, CraftingRecipe> CRAFTING_RECIPES = new HashMap<>();
	private static final Map<ResourceLocation, Set<ResourceLocation>> LOCKED_RECIPES = new LinkedHashMap<>();

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
	public static <T extends AbstractStaticPowerRecipe> Optional<T> getRecipe(RecipeType<T> recipeType, RecipeMatchParameters matchParameters) {
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
	 * Attempts to find a recipe by the provided id.
	 * 
	 * @param <T>             The type of the recipe.
	 * @param recipeType      The {@link IRecipeType} of the recipe.
	 * @param matchParameters The match parameters to used.
	 * @return Optional of the recipe if it exists, otherwise empty.
	 */
	@SuppressWarnings("unchecked")
	public static <T extends AbstractStaticPowerRecipe> Optional<T> getRecipe(RecipeType<T> recipeType, ResourceLocation id) {
		// If no recipes of this type exist, return empty.
		if (!RECIPES.containsKey(recipeType)) {
			return Optional.empty();
		}

		// Iterate through the recipe linked list and return the first instance that
		// matches.
		for (AbstractStaticPowerRecipe recipe : RECIPES.get(recipeType)) {
			if (recipe.getId().equals(id)) {
				return Optional.of((T) recipe);
			}
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
			// Iterate through the recipe linked list and return the first instance that
			// matches.
			for (AbstractStaticPowerRecipe recipe : RECIPES.get(recipeType)) {
				if (recipe.getId().equals(id)) {
					return Optional.of((T) recipe);
				}
			}
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
	 * Checks to see if the provided itemstack is a valid former mold used in any
	 * recipe.
	 * 
	 * @param stack
	 * @return
	 */
	public static boolean isValidFormerMold(ItemStack stack) {
		for (AbstractStaticPowerRecipe recipe : RECIPES.get(FormerRecipe.RECIPE_TYPE)) {
			FormerRecipe formerRecipe = (FormerRecipe) recipe;
			if (formerRecipe.getRequiredMold().test(stack)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks to see if the provided itemstack is a valid casting mold used in any
	 * recipe.
	 * 
	 * @param stack
	 * @return
	 */
	public static boolean isValidCastingMold(ItemStack stack) {
		for (AbstractStaticPowerRecipe recipe : RECIPES.get(ModRecipeTypes.CASTING_RECIPE_TYPE.get())) {
			CastingRecipe castingRecipe = (CastingRecipe) recipe;
			if (castingRecipe.getRequiredMold().test(stack)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This event is raised when the resources are loaded/reloaded.
	 */
	public static void onResourcesReloaded(RecipeManager manager) {
		// Capture if this is the first time we are caching.
		boolean firstTime = RECIPES.size() == 0;

		// Log that caching has started.
		LOGGER.info(String.format("%1$s Static Power recipes.", (firstTime ? "Caching" : "Re-caching")));

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
		handleCraftingResearchRecipeReplacement(manager, newRecipes.getOrDefault(RecipeType.CRAFTING, new HashMap<ResourceLocation, Recipe<?>>()));

		// Cache additional recipes.
		// cacheDynamicBottlerRecipes(manager, getOrPutDefault(newRecipes,
		// BottleRecipe.RECIPE_TYPE));
		// cacheDynamicSolidGeneratorRecipes(manager, getOrPutDefault(newRecipes,
		// SolidFuelRecipe.RECIPE_TYPE));

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
		LOGGER.info(String.format("Succesfully %1$s %2$d Static Power recipes.", (firstTime ? "cached" : "re-cached"),
				RECIPES.size() + FURNACE_RECIPES.size() + CRAFTING_RECIPES.size()));
	}

	protected static Map<ResourceLocation, Recipe<?>> getOrPutDefault(Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> recipes, RecipeType<?> type) {
		if (!recipes.containsKey(type)) {
			recipes.put(type, new HashMap<ResourceLocation, Recipe<?>>());
		}
		return recipes.get(type);
	}

	public static List<ResourceLocation> getMissingResearchForRecipe(ResourceLocation recipeId, Team team) {
		// If the recipe is locked, capture all the research that still needs to be
		// completed.
		if (LOCKED_RECIPES.containsKey(recipeId)) {
			return LOCKED_RECIPES.get(recipeId).stream().filter((resourceLocation) -> !team.getResearchManager().hasCompletedResearch(resourceLocation)).toList();
		}

		return Collections.emptyList();
	}

	public static Optional<PackagerRecipe> getPackagerRecipe(RecipeManager recipeManager, ItemStack stack, int size) {
		if (stack.isEmpty()) {
			return Optional.empty();
		}

		if (size == 2) {
			FakeCraftingInventory sizeTwoInv = new FakeCraftingInventory(2, 2);
			for (int i = 0; i < 4; i++) {
				sizeTwoInv.setItem(i, stack);
			}

			Optional<CraftingRecipe> twoRecipe = recipeManager.getRecipeFor(RecipeType.CRAFTING, sizeTwoInv, null);
			if (twoRecipe.isPresent()) {
				CraftingRecipe recipe = twoRecipe.get();
				ResourceLocation id = new ResourceLocation(recipe.getId().getNamespace(), recipe.getId().getPath() + "_packager_2_dynamic");
				return Optional.of(new PackagerRecipe(id, 2, new StaticPowerIngredient(stack.copy(), 4), StaticPowerOutputItem.of(recipe.getResultItem()),
						MachineRecipeProcessingSection.hardcoded(StaticPowerConfig.SERVER.packagerProcessingTime, StaticPowerConfig.SERVER.packagerPowerUsage, () -> 0, () -> 0)));
			}
		} else if (size == 3) {
			FakeCraftingInventory sizeThreeInv = new FakeCraftingInventory(3, 3);
			for (int i = 0; i < 9; i++) {
				sizeThreeInv.setItem(i, stack);
			}

			Optional<CraftingRecipe> threeRecipe = recipeManager.getRecipeFor(RecipeType.CRAFTING, sizeThreeInv, null);
			if (threeRecipe.isPresent()) {
				CraftingRecipe recipe = threeRecipe.get();
				ResourceLocation id = new ResourceLocation(recipe.getId().getNamespace(), recipe.getId().getPath() + "_packager_3_dynamic");
				return Optional.of(new PackagerRecipe(id, 3, new StaticPowerIngredient(stack.copy(), 9), StaticPowerOutputItem.of(recipe.getResultItem()),
						MachineRecipeProcessingSection.hardcoded(StaticPowerConfig.SERVER.packagerProcessingTime, StaticPowerConfig.SERVER.packagerPowerUsage, () -> 0, () -> 0)));
			}
		}

		return Optional.empty();
	}

	/**
	 * Adds a recipe to the recipes list.
	 * 
	 * @param recipe
	 */
	private static void cacheRecipe(AbstractStaticPowerRecipe recipe) {
		if (!RECIPES.containsKey(recipe.getType())) {
			RECIPES.put(recipe.getType(), new LinkedList<AbstractStaticPowerRecipe>());
		}
		RECIPES.get(recipe.getType()).add(recipe);
	}

	// TODO: Review this
	private static void cacheDynamicSolidGeneratorRecipes(RecipeManager manager, Map<ResourceLocation, Recipe<?>> newRecipes) {
		for (Item item : ForgeRegistries.ITEMS) {
			// Create an instance to use.
			ItemStack instance = new ItemStack(item);
			// If this is a burnable, cache it.
			if (ForgeHooks.getBurnTime(instance, null) > 0) {
				ResourceLocation itemRegistryName = ForgeRegistries.ITEMS.getKey(item);
				ResourceLocation recipe = new ResourceLocation(itemRegistryName.getNamespace(), itemRegistryName.getPath() + "_solid_fuel_dynamic");
				SolidFuelRecipe solidFuelRecipe = new SolidFuelRecipe(recipe, instance.copy());
				newRecipes.put(solidFuelRecipe.getId(), solidFuelRecipe);
			}
		}
		// Log the completion.
		LOGGER.info("Succesfully cached dynamic solid generator recipes!");
	}

	// TODO: Review this
	private static void cacheDynamicBottlerRecipes(RecipeManager manager, Map<ResourceLocation, Recipe<?>> newRecipes) {
		// Capture bottler recipes.
		for (Fluid fluid : ForgeRegistries.FLUIDS) {
			// If it has no bucket, skip it.
			if (fluid.getBucket() == null) {
				continue;
			}

			// Skip the flowing fluids.
			if (!fluid.isSource(fluid.defaultFluidState())) {
				continue;
			}

			// Capture dynamic recipes.
			for (Item item : ForgeRegistries.ITEMS) {

				// Create an instance to use.
				ItemStack container = new ItemStack(item);

				// Get the fluid container handler. If this itemstack doesn't have a fluid
				// container, skip it.
				IFluidHandler containerHandler = FluidUtil.getFluidHandler(container).orElse(null);
				if (containerHandler == null) {
					continue;
				}

				int bucketVolume = 1000;

				// If we can't fill the container with the fluid, skip this container.
				int filled = containerHandler.fill(new FluidStack(fluid, bucketVolume), FluidAction.EXECUTE);
				if (filled != bucketVolume) {
					continue;
				}

				// Create the recipe.
				ResourceLocation itemRegistryName = ForgeRegistries.ITEMS.getKey(item);
				ResourceLocation fluidRegistryName = ForgeRegistries.FLUIDS.getKey(fluid);
				FluidActionResult result = FluidUtil.tryFillContainer(container, containerHandler, bucketVolume, null, true);
				FluidStack fluidStack = new FluidStack(fluid, bucketVolume);
				ResourceLocation recipe = new ResourceLocation(itemRegistryName.getNamespace(),
						itemRegistryName.getPath() + "_" + fluidRegistryName.getPath() + "_bottler_dynamic");
				BottleRecipe bucketRecipe = new BottleRecipe(recipe, StaticPowerIngredient.of(new ItemStack(item)), StaticPowerOutputItem.of(result.result),
						FluidIngredient.of(1000, fluidStack));
				newRecipes.put(bucketRecipe.getId(), bucketRecipe);
			}
		}
		// Log the completion.
		LOGGER.info("Succesfully cached dynamic bottler recipes!");
	}

	private static void handleCraftingResearchRecipeReplacement(RecipeManager manager, Map<ResourceLocation, Recipe<?>> newRecipes) {
		// Clear the previously cached lockable recipes.
		LOCKED_RECIPES.clear();

		// Capture all the lockable recipes mapped to the set of required research for
		// them.
		for (Research research : manager.getAllRecipesFor(ModRecipeTypes.RESEARCH_RECIPE_TYPE.get())) {
			for (ResearchUnlock unlock : research.getUnlocks()) {
				if (unlock.getType() == ResearchUnlockType.CRAFTING || unlock.getType() == ResearchUnlockType.MACHINE_RECIPE) {
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
