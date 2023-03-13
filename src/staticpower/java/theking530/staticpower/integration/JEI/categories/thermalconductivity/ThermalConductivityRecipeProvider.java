package theking530.staticpower.integration.JEI.categories.thermalconductivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.advanced.IRecipeManagerPlugin;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.wrappers.thermalconductivity.ThermalConductivityRecipe;
import theking530.staticpower.init.ModRecipeTypes;
import theking530.staticpower.utilities.ItemUtilities;

public class ThermalConductivityRecipeProvider implements IRecipeManagerPlugin {
	private static List<ThermalConductivityJEIRecipeWrapper> RECIPES;

	public ThermalConductivityRecipeProvider() {
	}

	public static List<ThermalConductivityJEIRecipeWrapper> getRecipes() {
		// Create the recipes array.
		RECIPES = new ArrayList<ThermalConductivityJEIRecipeWrapper>();

		// Get all thermal conductivity recipes.
		@SuppressWarnings("resource")
		List<ThermalConductivityRecipe> originalRecipes = Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.THERMAL_CONDUCTIVITY_RECIPE_TYPE.get());

		// Iterate through all the recipes.
		for (ThermalConductivityRecipe recipe : originalRecipes) {
			try {
				// Skip air recipe.
				if (recipe.isAirRecipe()) {
					continue;
				}

				// Create the recipe (may be dropped).
				ThermalConductivityJEIRecipeWrapper jeiRecipe;

				// If this is a fire input recipe, mark it.
				if (recipe.getBlockTags().size() > 0 && recipe.getBlockTags().get(0).toString().equals("minecraft:fire")) {
					jeiRecipe = new ThermalConductivityJEIRecipeWrapper(recipe, true);
				} else {
					try {
						jeiRecipe = new ThermalConductivityJEIRecipeWrapper(recipe);
					} catch (Throwable e) {
						StaticPower.LOGGER.warn(e);
						continue;
					}

				}

				// Add blocks.
				if (recipe.getBlockTags().size() > 0) {
					RECIPES.add(jeiRecipe);

					// Add all the potential inputs.
					for (Entry<ResourceKey<Block>, Block> block : ForgeRegistries.BLOCKS.getEntries()) {
						RecipeMatchParameters matchParams = new RecipeMatchParameters(block.getValue().defaultBlockState());
						if (recipe.isValid(matchParams)) {
							jeiRecipe.addInput(new ItemStack(block.getValue()));
						}
					}
					// Finalize the recipe.
					jeiRecipe.finalize();
				} else if (recipe.getFluidTags().size() > 0) {
					RECIPES.add(jeiRecipe);
					// Add all the potential inputs.
					for (Entry<ResourceKey<Fluid>, Fluid> fluid : ForgeRegistries.FLUIDS.getEntries()) {
						RecipeMatchParameters matchParams = new RecipeMatchParameters(new FluidStack(fluid.getValue(), 1));
						if (recipe.isValid(matchParams)) {
							jeiRecipe.setFluidStack(new FluidStack(fluid.getValue(), 1000));
						}
					}

					// Finalize the recipe.
					jeiRecipe.finalize();
				}
			} catch (Exception e) {
				StaticPower.LOGGER.warn(e);
			}
		}
		return RECIPES;
	}

	@Override
	public <V> List<RecipeType<?>> getRecipeTypes(IFocus<V> focus) {
		if (focus.getTypedValue().getIngredient() instanceof ItemStack) {
			ItemStack itemStack = (ItemStack) focus.getTypedValue().getIngredient();
			if (focus.getRole() == RecipeIngredientRole.OUTPUT) {
				if (isValidOverheatingOutput(itemStack)) {
					return Collections.singletonList(ThermalConductivityRecipeCategory.TYPE);
				}
			} else if (focus.getRole() == RecipeIngredientRole.INPUT) {
				if (hasThermalConductivity(itemStack)) {
					return Collections.singletonList(ThermalConductivityRecipeCategory.TYPE);
				}
			}
		} else if (focus.getTypedValue().getIngredient() instanceof FluidStack) {
			if (focus.getRole() == RecipeIngredientRole.INPUT) {
				if (hasThermalConductivity((FluidStack) focus.getTypedValue().getIngredient())) {
					return Collections.singletonList(ThermalConductivityRecipeCategory.TYPE);
				}
			}
		}
		return Collections.emptyList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> getRecipes(IRecipeCategory<T> recipeCategory) {
		// Check the category.
		if (recipeCategory != null && !ThermalConductivityRecipeCategory.TYPE.equals(recipeCategory.getRecipeType())) {
			return Collections.emptyList();
		}

		// Return the recipes.
		return (List<T>) getRecipes();
	}

	@Override
	public <T, V> List<T> getRecipes(IRecipeCategory<T> recipeCategory, IFocus<V> focus) {
		return Collections.emptyList();
	}

	private static boolean hasThermalConductivity(ItemStack stack) {
		// Check to see if this itemstack represents a block.
		if (stack.getItem() instanceof BlockItem) {
			// Get block item.
			BlockItem blockItem = (BlockItem) stack.getItem();

			// Create the match params.
			ItemStack input = new ItemStack(blockItem);

			// Get the recipes.
			for (ThermalConductivityJEIRecipeWrapper recipe : RECIPES) {
				if (recipe.getInput().test(input)) {
					return true;
				}
			}
		}

		return false;
	}

	private static boolean hasThermalConductivity(FluidStack stack) {
		// Get the recipes.
		for (ThermalConductivityJEIRecipeWrapper recipe : RECIPES) {
			if (recipe.getFluidInput().isFluidEqual(stack)) {
				return true;
			}
		}
		return false;
	}

	private static boolean isValidOverheatingOutput(ItemStack stack) {
		// Iterate through all the recipes and add the applicable ones.
		for (ThermalConductivityJEIRecipeWrapper recipe : RECIPES) {
			if (ItemUtilities.areItemStacksStackable(recipe.getOutputBlock(), stack) || ItemUtilities.areItemStacksStackable(recipe.getOutputItem().getItemStack(), stack)) {
				return true;
			}
		}
		return false;
	}
}