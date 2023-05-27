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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.ForgeRegistries;
import theking530.staticcore.container.FakeCraftingInventory;
import theking530.staticcore.crafting.MachineRecipeProcessingSection;
import theking530.staticcore.crafting.StaticPowerIngredient;
import theking530.staticcore.crafting.StaticPowerOutputItem;
import theking530.staticcore.utilities.item.ItemUtilities;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.data.crafting.wrappers.packager.PackagerRecipe;
import theking530.staticpower.integration.JEI.categories.PackagerRecipeCategory;

public class PackagerRecipeProvider implements IRecipeManagerPlugin {
	private static final List<PackagerRecipe> RECIPES = new ArrayList<>();

	public PackagerRecipeProvider() {
	}

	public static List<PackagerRecipe> getRecipes() {
		RECIPES.clear();
		RECIPES.addAll(getRecipeManager().getAllRecipesFor(PackagerRecipe.RECIPE_TYPE));

		// Iterate through all items.
		for (Item item : ForgeRegistries.ITEMS) {
			Optional<PackagerRecipe> twoRecipe = getPackagerRecipe(getRecipeManager(), new ItemStack(item), 2);
			if (twoRecipe.isPresent()) {
				RECIPES.add(twoRecipe.get());
			}

			Optional<PackagerRecipe> threeRecipe = getPackagerRecipe(getRecipeManager(), new ItemStack(item), 2);
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
			if (ItemUtilities.areItemStacksStackable(recipe.getOutput().getItemStack(), stack)) {
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

	private static Optional<PackagerRecipe> getPackagerRecipe(RecipeManager recipeManager, ItemStack stack, int size) {
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
				ResourceLocation id = new ResourceLocation(recipe.getId().getNamespace(),
						recipe.getId().getPath() + "_packager_2_dynamic");
				return Optional.of(new PackagerRecipe(id, 2, StaticPowerIngredient.of(stack.copy(), 4),
						StaticPowerOutputItem.of(recipe.getResultItem()),
						MachineRecipeProcessingSection.hardcoded(StaticPowerConfig.SERVER.packagerProcessingTime,
								StaticPowerConfig.SERVER.packagerPowerUsage, () -> 0.0f, () -> 0.0f)));
			}
		} else if (size == 3) {
			FakeCraftingInventory sizeThreeInv = new FakeCraftingInventory(3, 3);
			for (int i = 0; i < 9; i++) {
				sizeThreeInv.setItem(i, stack);
			}

			Optional<CraftingRecipe> threeRecipe = recipeManager.getRecipeFor(RecipeType.CRAFTING, sizeThreeInv, null);
			if (threeRecipe.isPresent()) {
				CraftingRecipe recipe = threeRecipe.get();
				ResourceLocation id = new ResourceLocation(recipe.getId().getNamespace(),
						recipe.getId().getPath() + "_packager_3_dynamic");
				return Optional.of(new PackagerRecipe(id, 3, StaticPowerIngredient.of(stack.copy(), 9),
						StaticPowerOutputItem.of(recipe.getResultItem()),
						MachineRecipeProcessingSection.hardcoded(StaticPowerConfig.SERVER.packagerProcessingTime,
								StaticPowerConfig.SERVER.packagerPowerUsage, () -> 0.0f, () -> 0.0f)));
			}
		}

		return Optional.empty();
	}
}