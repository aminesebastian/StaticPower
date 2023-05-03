package theking530.staticpower.integration.JEI.categories.thermalconductivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.advanced.IRecipeManagerPlugin;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.crafting.thermal.ThermalConductivityRecipe;
import theking530.staticcore.init.StaticCoreRecipeTypes;
import theking530.staticcore.utilities.item.ItemUtilities;
import theking530.staticpower.StaticPower;

public class ThermalConductivityRecipeProvider implements IRecipeManagerPlugin {
	private static final List<ThermalConductivityJEIRecipeWrapper> RECIPES = new ArrayList<>();

	public ThermalConductivityRecipeProvider() {
	}

	public static List<ThermalConductivityJEIRecipeWrapper> getRecipes() {
		RECIPES.clear();

		@SuppressWarnings("resource")
		List<ThermalConductivityRecipe> originalRecipes = Minecraft.getInstance().level.getRecipeManager()
				.getAllRecipesFor(StaticCoreRecipeTypes.THERMAL_CONDUCTIVITY_RECIPE_TYPE.get());

		// Iterate through all the recipes.
		for (ThermalConductivityRecipe recipe : originalRecipes) {
			try {
				if (recipe.isAirRecipe()) {
					continue;
				}

				boolean isFireInput = !recipe.getBlocks().isEmpty() && recipe.getBlocks().test(Blocks.FIRE);
				ThermalConductivityJEIRecipeWrapper jeiRecipe = new ThermalConductivityJEIRecipeWrapper(recipe,
						isFireInput);

				RECIPES.add(jeiRecipe);
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
				if (isValidItemOrBlockOutput(itemStack)) {
					return Collections.singletonList(ThermalConductivityRecipeCategory.TYPE);
				}
			} else if (focus.getRole() == RecipeIngredientRole.INPUT) {
				if (isValidBlockInput(itemStack)) {
					return Collections.singletonList(ThermalConductivityRecipeCategory.TYPE);
				}
			}
		} else if (focus.getTypedValue().getIngredient() instanceof FluidStack) {
			FluidStack fluidStack = (FluidStack) focus.getTypedValue().getIngredient();
			if (focus.getRole() == RecipeIngredientRole.INPUT) {
				if (isValidFluidInput(fluidStack)) {
					return Collections.singletonList(ThermalConductivityRecipeCategory.TYPE);
				}
			} else if (focus.getRole() == RecipeIngredientRole.OUTPUT) {
				if (isValidFluidOutput(fluidStack)) {
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

	private static boolean isValidBlockInput(ItemStack stack) {
		if (stack.getItem() instanceof BlockItem) {
			BlockItem blockItem = (BlockItem) stack.getItem();
			for (ThermalConductivityJEIRecipeWrapper recipe : RECIPES) {
				if (recipe.getBlocks().test(blockItem.getBlock())) {
					return true;
				}
			}
		}

		return false;
	}

	private static boolean isValidFluidInput(FluidStack stack) {
		for (ThermalConductivityJEIRecipeWrapper recipe : RECIPES) {
			if (recipe.getFluidInput().test(stack)) {
				return true;
			}
		}
		return false;
	}

	private static boolean isValidItemOrBlockOutput(ItemStack stack) {
		for (ThermalConductivityJEIRecipeWrapper recipe : RECIPES) {
			if (recipe.getRecipe().hasOverheatingBehaviour()) {
				if (ItemUtilities.areItemStacksStackable(recipe.getConcretizedOverheat().block(), stack)
						|| ItemUtilities.areItemStacksStackable(recipe.getConcretizedOverheat().item().getItemStack(),
								stack)) {
					return true;
				}
			}
			if (recipe.getRecipe().hasFreezeBehaviour()) {
				if (ItemUtilities.areItemStacksStackable(recipe.getConcretizedFreeze().block(), stack) || ItemUtilities
						.areItemStacksStackable(recipe.getConcretizedFreeze().item().getItemStack(), stack)) {
					return true;
				}
			}
		}
		return false;
	}

	private static boolean isValidFluidOutput(FluidStack stack) {
		for (ThermalConductivityJEIRecipeWrapper recipe : RECIPES) {
			if (recipe.getRecipe().hasOverheatingBehaviour()) {
				if (recipe.getConcretizedOverheat().fluid().isFluidEqual(stack)) {
					return true;
				}
			}
			if (recipe.getRecipe().hasFreezeBehaviour()) {
				if (recipe.getConcretizedFreeze().fluid().isFluidEqual(stack)) {
					return true;
				}
			}
		}
		return false;
	}
}