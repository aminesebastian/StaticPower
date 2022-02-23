package theking530.staticpower.data.crafting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.registries.RegistryManager;
import theking530.staticpower.StaticPower;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.container.FakeCraftingInventory;
import theking530.staticpower.data.crafting.researchwrappers.ShapedResearchWrapper;
import theking530.staticpower.data.crafting.researchwrappers.ShapelessResearchWrapper;
import theking530.staticpower.data.crafting.wrappers.bottler.BottleRecipe;
import theking530.staticpower.data.crafting.wrappers.castingbasin.CastingRecipe;
import theking530.staticpower.data.crafting.wrappers.former.FormerRecipe;
import theking530.staticpower.data.crafting.wrappers.packager.PackagerRecipe;
import theking530.staticpower.data.crafting.wrappers.solidfuel.SolidFuelRecipe;
import theking530.staticpower.data.research.Research;
import theking530.staticpower.data.research.ResearchUnlock;
import theking530.staticpower.data.research.ResearchUnlock.ResearchUnlockType;

public class StaticPowerRecipeRegistry {
	public static final Logger LOGGER = LogManager.getLogger(StaticPowerRecipeRegistry.class);

	@SuppressWarnings("rawtypes")
	public static final HashMap<RecipeType, LinkedList<AbstractStaticPowerRecipe>> RECIPES = new HashMap<>();
	public static final Map<ResourceLocation, SmeltingRecipe> FURNACE_RECIPES = new HashMap<>();
	public static final Map<ResourceLocation, CraftingRecipe> CRAFTING_RECIPES = new HashMap<>();
	public static final Map<ResourceLocation, Set<ResourceLocation>> LOCKED_RECIPES = new LinkedHashMap<>();

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

	/**
	 * Gets all the recipes of the provided type.
	 * 
	 * @param <T>        The class of the recipe.
	 * @param recipeType The type of the recipe.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T extends AbstractStaticPowerRecipe> List<T> getRecipesOfType(RecipeType<T> recipeType) {
		List<T> recipes = new ArrayList<>();

		// Return the empty list if there are no recipes of the provided type. This is
		// important to handle an edge case were a modpack completly removes all recipes
		// for a machine for whatever reason.
		if (!RECIPES.containsKey(recipeType)) {
			return recipes;
		}

		for (AbstractStaticPowerRecipe abstractRecipe : RECIPES.get(recipeType)) {
			T formerRecipe = (T) abstractRecipe;
			recipes.add(formerRecipe);
		}
		return recipes;
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
		for (AbstractStaticPowerRecipe recipe : RECIPES.get(CastingRecipe.RECIPE_TYPE)) {
			CastingRecipe castingRecipe = (CastingRecipe) recipe;
			if (castingRecipe.getRequiredMold().test(stack)) {
				return true;
			}
		}
		return false;
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
	public static void onResourcesReloaded(RecipeManager manager) {
		// Capture if this is the first time we are caching.
		boolean firstTime = RECIPES.size() == 0;

		// Log that caching has started.
		LOGGER.info(String.format("%1$s Static Power recipes.", (firstTime ? "Caching" : "Re-caching")));

		// Clear the recipe lists.
		RECIPES.clear();
		FURNACE_RECIPES.clear();
		CRAFTING_RECIPES.clear();

		// Handle research replacements.
		handleResearchRecipeReplacement(manager);

		// Iterate through all the recipes and cache the Static Power ones.
		Collection<Recipe<?>> recipes = manager.getRecipes();
		for (Recipe<?> recipe : recipes) {
			if (recipe instanceof AbstractStaticPowerRecipe) {
				addRecipe((AbstractStaticPowerRecipe) recipe);
			} else if (recipe.getType() == RecipeType.SMELTING) {
				// Cache smelting recipes.
				FURNACE_RECIPES.put(recipe.getId(), (SmeltingRecipe) recipe);
			} else if (recipe.getType() == RecipeType.CRAFTING) {
				// Cache crafting recipes.
				CRAFTING_RECIPES.put(recipe.getId(), (CraftingRecipe) recipe);
			}
		}

		// Cache additional recipes.
		cacheDynamicBottlerRecipes(manager, null);
		cachePackagerRecipes(manager, null);

		// Log the completion.
		LOGGER.info(String.format("Succesfully %1$s %2$d Static Power recipes.", (firstTime ? "cached" : "re-cached"), RECIPES.size() + FURNACE_RECIPES.size() + CRAFTING_RECIPES.size()));
	}

	private static void cacheDynamicBottlerRecipes(RecipeManager manager, @Nullable Level world) {
		// Capture dynamic recipes.
		for (Item item : RegistryManager.ACTIVE.getRegistry(Item.class)) {
			// Create an instance to use.
			ItemStack instance = new ItemStack(item);

			// Skip empty instances.
			if (instance.isEmpty()) {
				continue;
			}

			// If this is a burnable, cache it.
			if (ForgeHooks.getBurnTime(instance, null) > 0) {
				ResourceLocation recipe = new ResourceLocation(item.getRegistryName().getNamespace(), item.getRegistryName().getPath() + "_solid_fuel_dynamic");
				SolidFuelRecipe solidFuelRecipe = new SolidFuelRecipe(recipe, instance.copy());
				addRecipe(solidFuelRecipe);
			}

			// Capture bottler recipes.
			for (Fluid fluid : RegistryManager.ACTIVE.getRegistry(Fluid.class)) {
				// If it has no bucket, skip it.
				if (fluid.getBucket() == null) {
					continue;
				}

				// Skip the flowing fluids.
				if (fluid.defaultFluidState().getAmount() != 8) {
					continue;
				}

				// Get the fluid container handler. If this itemstack doesn't have a fluid
				// container, skip it.
				IFluidHandler containerHandler = FluidUtil.getFluidHandler(instance).orElse(null);
				if (containerHandler == null) {
					continue;
				}

				// Create a copy of the itemstack to use.
				ItemStack container = instance.copy();

				// If we can't fill the container with the fluid, skip this container.
				FluidTank simulatedTank = new FluidTank(FluidAttributes.BUCKET_VOLUME);
				simulatedTank.fill(new FluidStack(fluid, FluidAttributes.BUCKET_VOLUME), FluidAction.EXECUTE);
				FluidActionResult result = FluidUtil.tryFillContainer(container, simulatedTank, FluidAttributes.BUCKET_VOLUME, null, true);
				ItemStack emptyContainer = container.copy();

				if (!result.isSuccess() || result.getResult().isEmpty() || emptyContainer.isEmpty()) {
					continue;
				}

				// Create the recipe.
				FluidStack fluidStack = new FluidStack(fluid, FluidAttributes.BUCKET_VOLUME);
				ResourceLocation recipe = new ResourceLocation(fluid.getRegistryName().getNamespace(), fluid.getRegistryName().getPath() + "_bottler_dynamic");
				BottleRecipe bucketRecipe = new BottleRecipe(recipe, result.getResult(), emptyContainer, fluidStack);

				// Add the recipe if is not a duplicate, otherwise, skip it.
				addRecipe(bucketRecipe);
				LOGGER.debug(String.format("Registering a dynamic bottler recipe for item: %1$s and fluid: %2$s.", emptyContainer.getHoverName().getString(),
						fluid.getAttributes().getDisplayName(fluidStack).getString()));
			}
		}
		// Log the completion.
		LOGGER.info("Succesfully cached dynamic bottler recipes!");
	}

	private static void cachePackagerRecipes(RecipeManager manager, @Nullable Level world) {
		// Iterate through all items.
		for (Item item : RegistryManager.ACTIVE.getRegistry(Item.class)) {
			// Create an item stack instance.
			ItemStack instance = new ItemStack(item);

			// Skip any items that result in an empty item stack.
			if (instance.isEmpty()) {
				continue;
			}

			// Create 2x2 and 3x3 inventories.
			FakeCraftingInventory sizeTwoInv = new FakeCraftingInventory(2, 2);
			FakeCraftingInventory sizeThreeInv = new FakeCraftingInventory(3, 3);

			// Populate the inventories.
			for (int i = 0; i < 9; i++) {
				if (i < 4) {
					sizeTwoInv.setItem(i, instance);
				}
				sizeThreeInv.setItem(i, instance);
			}

			// Check for recipes.
			// Get the outputs for a 2x2 and 3x3 recipe.
			try {
				Optional<CraftingRecipe> twoRecipe = manager.getRecipeFor(RecipeType.CRAFTING, sizeTwoInv, world);

				// Create and add the 2x2 recipe.
				if (twoRecipe.isPresent()) {
					CraftingRecipe recipe = twoRecipe.get();
					ResourceLocation id = new ResourceLocation(recipe.getId().getNamespace(), recipe.getId().getPath() + "_packager_2_dynamic");
					PackagerRecipe packRecipe = new PackagerRecipe(id, 2, new StaticPowerIngredient(instance.copy(), 4), new ProbabilityItemStackOutput(recipe.getResultItem()),
							MachineRecipeProcessingSection.hardcoded(StaticPowerConfig.SERVER.packagerProcessingTime.get(), StaticPowerConfig.SERVER.packagerPowerUsage.get()));
					addRecipe(packRecipe);
				}
			} catch (Exception e) {
				StaticPower.LOGGER.error(
						"An error occured when attempting to cache a 2x2 packager recipe! Recipes that require a reference to the world are not currently supported. Check the debug log for more details.");
				StaticPower.LOGGER.debug("An error occured when attempting to cache a 2x2 packager recipe! Recipes that require a reference to the world are not currently supported.", e);
			}

			try {
				Optional<CraftingRecipe> threeRecipe = manager.getRecipeFor(RecipeType.CRAFTING, sizeThreeInv, world);

				// Create and add the 3x3 recipe.
				if (threeRecipe.isPresent()) {
					CraftingRecipe recipe = threeRecipe.get();
					ResourceLocation id = new ResourceLocation(recipe.getId().getNamespace(), recipe.getId().getPath() + "_packager_3_dynamic");
					PackagerRecipe packRecipe = new PackagerRecipe(id, 3, new StaticPowerIngredient(instance.copy(), 9), new ProbabilityItemStackOutput(recipe.getResultItem()),
							MachineRecipeProcessingSection.hardcoded(StaticPowerConfig.SERVER.packagerProcessingTime.get(), StaticPowerConfig.SERVER.packagerPowerUsage.get()));
					addRecipe(packRecipe);
				}
			} catch (Exception e) {
				StaticPower.LOGGER.error(
						"An error occured when attempting to cache a 3x3 packager recipe! Recipes that require a reference to the world are not currently supported. Check the debug log for more details.");
				StaticPower.LOGGER.debug("An error occured when attempting to cache a 3x3 packager recipe! Recipes that require a reference to the world are not currently supported.", e);
			}
		}
		// Log the completion.
		LOGGER.info("Succesfully cached packager recipes!");
	}

	private static void handleResearchRecipeReplacement(RecipeManager manager) {
		// Clear the previously cached lockable recipes.
		LOCKED_RECIPES.clear();

		// Get all recipes.
		List<Recipe<?>> recipes = new ArrayList<>(manager.getRecipes());

		// Capture all the lockable recipes mapped to the set of required research for
		// them.
		for (Research research : manager.getAllRecipesFor(Research.RECIPE_TYPE)) {
			for (ResearchUnlock unlock : research.getUnlocks()) {
				if (unlock.getType() == ResearchUnlockType.CRAFTING) {
					if (!LOCKED_RECIPES.containsKey(unlock.getTarget())) {
						LOCKED_RECIPES.put(unlock.getTarget(), new HashSet<ResourceLocation>());
					}
					LOCKED_RECIPES.get(unlock.getTarget()).add(research.getId());
				}
			}
		}

		// Replace any recipes that need to be replaced.
		for (int i = recipes.size() - 1; i >= 0; i--) {
			Recipe<?> recipe = recipes.get(i);
			if (LOCKED_RECIPES.containsKey(recipe.getId())) {
				if (recipe instanceof ShapedRecipe) {
					ShapedRecipe craftingRecipe = (ShapedRecipe) recipe;
					recipes.set(i, new ShapedResearchWrapper(LOCKED_RECIPES.get(recipe.getId()), craftingRecipe));
				} else if (recipe instanceof ShapelessRecipe) {
					ShapelessRecipe craftingRecipe = (ShapelessRecipe) recipe;
					recipes.set(i, new ShapelessResearchWrapper(LOCKED_RECIPES.get(recipe.getId()), craftingRecipe));
				}
			}
		}

		// Replace the recipes.
		// TODO: This feels so dirty, wish I could replace by ID one by one.
		manager.replaceRecipes(recipes);
	}
}
