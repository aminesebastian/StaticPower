package theking530.staticpower.integration.JEI.categories.smithing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.advanced.IRecipeManagerPlugin;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryManager;
import theking530.api.attributes.capability.CapabilityAttributable;
import theking530.staticpower.data.crafting.StaticPowerRecipeRegistry;
import theking530.staticpower.data.crafting.wrappers.autosmith.AutoSmithRecipe;

public class SmithingRecipeProvider implements IRecipeManagerPlugin {
	private static List<AutoSmithRecipeJEIWrapper> RECIPES;

	public SmithingRecipeProvider() {
	}

	public static List<AutoSmithRecipeJEIWrapper> getRecipes() {
		// Create the recipes array.
		RECIPES = new ArrayList<AutoSmithRecipeJEIWrapper>();

		// Get all the registered items and add any recipes that can be created from
		// them.
		for (Entry<ResourceKey<Item>, Item> item : RegistryManager.ACTIVE.getRegistry(Item.class).getEntries()) {
			RECIPES.addAll(makeFromSmithingInput(new ItemStack(item.getValue())));
		}

		return RECIPES;
	}

	@Override
	public <V> List<ResourceLocation> getRecipeCategoryUids(IFocus<V> focus) {
		if (focus.getMode() == IFocus.Mode.OUTPUT && focus.getValue() instanceof ItemStack) {
			// Get the focused item.
			ItemStack itemStack = (ItemStack) focus.getValue();

			// Check to see if it is a valid smithing output.
			if (isValidSmithingOutput(itemStack)) {
				return Collections.singletonList(SmithingRecipeCategory.UID);
			}
		} else if (focus.getMode() == IFocus.Mode.INPUT && focus.getValue() instanceof ItemStack) {
			// Check if the input is used in any smithing recipes or is itself smithable.
			ItemStack itemStack = (ItemStack) focus.getValue();
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
		if (recipeCategory != null && !SmithingRecipeCategory.UID.equals(recipeCategory.getUid())) {
			return Collections.emptyList();
		}

		// Clear the recipes array.
		RECIPES.clear();

		// Get all the registered items and add any recipes that can be created from
		// them.
		for (Entry<ResourceKey<Item>, Item> item : RegistryManager.ACTIVE.getRegistry(Item.class).getEntries()) {
			RECIPES.addAll(makeFromSmithingInput(new ItemStack(item.getValue())));
		}

		// Return recipes.
		return (List<T>) RECIPES;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T, V> List<T> getRecipes(IRecipeCategory<T> recipeCategory, IFocus<V> focus) {
		if (!SmithingRecipeCategory.UID.equals(recipeCategory.getUid())) {
			return Collections.emptyList();
		}

		if (focus.getMode() == IFocus.Mode.OUTPUT && focus.getValue() instanceof ItemStack) {
			return Collections.emptyList();
		} else if (focus.getMode() == IFocus.Mode.INPUT && focus.getValue() instanceof ItemStack) {
			// Get the focused item stack and item.
			ItemStack focusedItem = (ItemStack) focus.getValue();
			return (List<T>) makeFromSmithingInput(focusedItem);
		}

		return Collections.emptyList();
	}

	private static List<AutoSmithRecipeJEIWrapper> makeFromSmithingInput(ItemStack input) {
		// Allocate the output.
		List<AutoSmithRecipeJEIWrapper> output = new ArrayList<AutoSmithRecipeJEIWrapper>();

		// Get all smithing reciepes.
		List<AutoSmithRecipe> recipes = StaticPowerRecipeRegistry.getRecipesOfType(AutoSmithRecipe.RECIPE_TYPE);

		// Iterate through all the recipes.
		for (AutoSmithRecipe recipe : recipes) {
			// If the recipe is not item requested OR if the recipe accepts the input.
			if (recipe.isWildcardRecipe() || recipe.getSmithTarget().test(input)) {
				ItemStack outputItem = input.copy();
				if (recipe.applyToItemStack(outputItem) || recipe.performsRepair()) {
					output.add(new AutoSmithRecipeJEIWrapper(recipe, input, outputItem));
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

	public static class AutoSmithRecipeJEIWrapper implements Recipe<Container> {
		public static final RecipeType<AutoSmithRecipeJEIWrapper> RECIPE_TYPE = RecipeType.register("auto_smith_jei");
		public final ResourceLocation id;
		public final AutoSmithRecipe recipe;
		public final Ingredient inputIng;
		final ItemStack input;
		public final ItemStack output;

		public AutoSmithRecipeJEIWrapper(AutoSmithRecipe recipe, ItemStack input, ItemStack output) {
			super();
			this.recipe = recipe;
			this.input = input;
			this.inputIng = Ingredient.of(input);
			this.output = output;
			this.id = new ResourceLocation(recipe.getId().getNamespace(), recipe.getId().getPath() + output.getItem().getRegistryName().getPath().replace(":", "/"));
		}

		public AutoSmithRecipe getRecipe() {
			return recipe;
		}

		@Override
		public boolean matches(Container inv, Level worldIn) {
			return false;
		}

		@Override
		public ItemStack assemble(Container inv) {
			return output;
		}

		@Override
		public boolean canCraftInDimensions(int width, int height) {
			return false;
		}

		@Override
		public ItemStack getResultItem() {
			return output;
		}

		public ItemStack getInputItem() {
			return input;
		}

		public Ingredient getInput() {
			return inputIng;
		}

		@Override
		public ResourceLocation getId() {
			return id;
		}

		@Override
		public RecipeSerializer<?> getSerializer() {
			return null;
		}

		@Override
		public RecipeType<?> getType() {
			return RECIPE_TYPE;
		}
	}
}