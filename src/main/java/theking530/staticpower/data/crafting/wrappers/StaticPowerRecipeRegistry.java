package theking530.staticpower.data.crafting.wrappers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.fml.common.registry.GameRegistry;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.crafting.wrappers.bottler.BottleRecipe;
import theking530.staticpower.data.crafting.wrappers.former.FormerRecipe;
import theking530.staticpower.data.crafting.wrappers.solidfuel.SolidFuelRecipe;

public class StaticPowerRecipeRegistry {

	@SuppressWarnings("rawtypes")
	public static final HashMap<IRecipeType, LinkedList<AbstractStaticPowerRecipe>> RECIPES = new HashMap<IRecipeType, LinkedList<AbstractStaticPowerRecipe>>();
	public static final LinkedList<FurnaceRecipe> FURNACE_RECIPES = new LinkedList<FurnaceRecipe>();

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
	 * Gets all the recipes of the provided type.
	 * 
	 * @param <T>        The class of the recipe.
	 * @param recipeType The type of the recipe.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T extends AbstractStaticPowerRecipe> List<T> getRecipesOfType(IRecipeType<T> recipeType) {
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

		// Clear the recipe lists.
		RECIPES.clear();
		FURNACE_RECIPES.clear();

		// Keep track of how many recipes are cached.
		int recipeCount = 0;

		// Iterate through all the recipes and cache the Static Power ones.
		Collection<IRecipe<?>> recipes = event.getRecipeManager().getRecipes();
		for (IRecipe<?> recipe : recipes) {
			if (recipe instanceof AbstractStaticPowerRecipe) {
				addRecipe((AbstractStaticPowerRecipe) recipe);
				recipeCount++;
			} else if (recipe.getType() == IRecipeType.SMELTING) {
				// Cache smelting recipes.
				FURNACE_RECIPES.add((FurnaceRecipe) recipe);
			}
		}

		// Capture dynamic recipes.
		for (Item item : GameRegistry.findRegistry(Item.class)) {
			// Create an instance to use.
			ItemStack instance = new ItemStack(item);

			// If this is a burnable, cache it.
			if (ForgeHooks.getBurnTime(instance) > 0) {
				ResourceLocation recipe = new ResourceLocation(item.getRegistryName().getNamespace(), item.getRegistryName().getPath() + "_solid_fuel_dynamic");
				SolidFuelRecipe solidFuelRecipe = new SolidFuelRecipe(recipe, instance.copy());
				addRecipe(solidFuelRecipe);
			}

			// Capture bottler recipes.
			for (Fluid fluid : GameRegistry.findRegistry(Fluid.class)) {
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

				if (!result.isSuccess()) {
					continue;
				}

				// Create the recipe.
				ItemStack emptyContainer = container.copy();
				ResourceLocation recipe = new ResourceLocation(fluid.getRegistryName().getNamespace(), fluid.getRegistryName().getPath() + "_bottler_dynamic");
				BottleRecipe bucketRecipe = new BottleRecipe(recipe, result.getResult(), emptyContainer, new FluidStack(fluid, FluidAttributes.BUCKET_VOLUME));

				// Add the recipe if is not a duplicate, otherwise, skip it.
				if (!StaticPowerRecipeRegistry.getRecipesOfType(BottleRecipe.RECIPE_TYPE).contains(bucketRecipe)) {
					recipeCount++;
					addRecipe(bucketRecipe);
				} else {
					StaticPower.LOGGER.info(String.format("Attempted to register a dynamic bottler recipe for item: %1$s. The .json definition may not be required unless it is an override.", emptyContainer.getDisplayName().getFormattedText()));
				}
			}
		}
		
		// Log the completion.
		StaticPower.LOGGER.info(String.format("Succesfully %1$s %2$d Static Power recipes.", (firstTime ? "cached" : "re-cached"), recipeCount));
	}
}
