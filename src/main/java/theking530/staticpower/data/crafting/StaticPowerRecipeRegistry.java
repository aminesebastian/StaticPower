package theking530.staticpower.data.crafting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.fml.common.registry.GameRegistry;
import theking530.staticpower.container.FakeCraftingInventory;
import theking530.staticpower.data.crafting.wrappers.bottler.BottleRecipe;
import theking530.staticpower.data.crafting.wrappers.castingbasin.CastingRecipe;
import theking530.staticpower.data.crafting.wrappers.former.FormerRecipe;
import theking530.staticpower.data.crafting.wrappers.packager.PackagerRecipe;
import theking530.staticpower.data.crafting.wrappers.solidfuel.SolidFuelRecipe;
import theking530.staticpower.tileentities.powered.packager.TileEntityPackager;

public class StaticPowerRecipeRegistry {
	public static final Logger LOGGER = LogManager.getLogger(StaticPowerRecipeRegistry.class);

	@SuppressWarnings("rawtypes")
	public static final HashMap<IRecipeType, LinkedList<AbstractStaticPowerRecipe>> RECIPES = new HashMap<IRecipeType, LinkedList<AbstractStaticPowerRecipe>>();
	public static final LinkedList<FurnaceRecipe> FURNACE_RECIPES = new LinkedList<FurnaceRecipe>();
	public static final Map<ResourceLocation, ICraftingRecipe> CRAFTING_RECIPES = new HashMap<ResourceLocation, ICraftingRecipe>();

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

		// Iterate through all the recipes and cache the Static Power ones.
		Collection<IRecipe<?>> recipes = manager.getRecipes();
		for (IRecipe<?> recipe : recipes) {
			if (recipe instanceof AbstractStaticPowerRecipe) {
				addRecipe((AbstractStaticPowerRecipe) recipe);
			} else if (recipe.getType() == IRecipeType.SMELTING) {
				// Cache smelting recipes.
				FURNACE_RECIPES.add((FurnaceRecipe) recipe);
			} else if (recipe.getType() == IRecipeType.CRAFTING) {
				// Cache crafting recipes.
				CRAFTING_RECIPES.put(recipe.getId(), (ICraftingRecipe) recipe);
			}
		}

		// Cache all packager recipes.
		cachePackagerRecipes(manager);

		// Cache all dynamic bottler recipes.
		cacheDynamicBottlerRecipes(manager);

		// Log the completion.
		LOGGER.info(String.format("Succesfully %1$s %2$d Static Power recipes.", (firstTime ? "cached" : "re-cached"), RECIPES.size() + FURNACE_RECIPES.size() + CRAFTING_RECIPES.size()));
	}

	private static void cacheDynamicBottlerRecipes(RecipeManager manager) {
		// Capture dynamic recipes.
		for (Item item : GameRegistry.findRegistry(Item.class)) {
			// Create an instance to use.
			ItemStack instance = new ItemStack(item);

			// Skip empty instances.
			if (instance.isEmpty()) {
				continue;
			}

			// If this is a burnable, cache it.
			if (ForgeHooks.getBurnTime(instance) > 0) {
				ResourceLocation recipe = new ResourceLocation(item.getRegistryName().getNamespace(), item.getRegistryName().getPath() + "_solid_fuel_dynamic");
				SolidFuelRecipe solidFuelRecipe = new SolidFuelRecipe(recipe, instance.copy());
				addRecipe(solidFuelRecipe);
			}

			// Capture bottler recipes.
			for (Fluid fluid : GameRegistry.findRegistry(Fluid.class)) {
				// If it has no bucket, skip it.
				if (fluid.getFilledBucket() == null) {
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
				LOGGER.info(String.format("Registering a dynamic bottler recipe for item: %1$s and fluid: %2$s.", emptyContainer.getDisplayName().getString(),
						fluid.getAttributes().getDisplayName(fluidStack).getString()));
			}
		}
	}

	private static void cachePackagerRecipes(RecipeManager manager) {
		// Iterate through all items.
		for (Item item : GameRegistry.findRegistry(Item.class)) {
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
					sizeTwoInv.setInventorySlotContents(i, instance);
				}
				sizeThreeInv.setInventorySlotContents(i, instance);
			}

			// Check for recipes.
			// Get the outputs for a 2x2 and 3x3 recipe.
			Optional<ICraftingRecipe> twoRecipe = manager.getRecipe(IRecipeType.CRAFTING, sizeTwoInv, null);
			Optional<ICraftingRecipe> threeRecipe = manager.getRecipe(IRecipeType.CRAFTING, sizeThreeInv, null);

			// Create and add the 2x2 recipe.
			if (twoRecipe.isPresent()) {
				ICraftingRecipe recipe = twoRecipe.get();
				ResourceLocation id = new ResourceLocation(recipe.getId().getNamespace(), recipe.getId().getPath() + "_packager_2_dynamic");
				PackagerRecipe packRecipe = new PackagerRecipe(id, TileEntityPackager.DEFAULT_PROCESSING_TIME, TileEntityPackager.DEFAULT_PROCESSING_COST, 2,
						new StaticPowerIngredient(instance, 4), new ProbabilityItemStackOutput(recipe.getRecipeOutput()));
				addRecipe(packRecipe);
			}

			// Create and add the 3x3 recipe.
			if (threeRecipe.isPresent()) {
				ICraftingRecipe recipe = threeRecipe.get();
				ResourceLocation id = new ResourceLocation(recipe.getId().getNamespace(), recipe.getId().getPath() + "_packager_3_dynamic");
				PackagerRecipe packRecipe = new PackagerRecipe(id, TileEntityPackager.DEFAULT_PROCESSING_TIME, TileEntityPackager.DEFAULT_PROCESSING_COST, 3,
						new StaticPowerIngredient(instance, 9), new ProbabilityItemStackOutput(recipe.getRecipeOutput()));
				addRecipe(packRecipe);
			}
		}
	}
}
