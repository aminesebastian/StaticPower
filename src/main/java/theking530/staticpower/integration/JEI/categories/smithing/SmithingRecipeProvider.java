package theking530.staticpower.integration.JEI.categories.smithing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.advanced.IRecipeManagerPlugin;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import theking530.api.attributes.capability.CapabilityAttributable;
import theking530.staticpower.data.crafting.StaticPowerRecipeRegistry;
import theking530.staticpower.data.crafting.wrappers.autosmith.AutoSmithRecipe;

/**
 * Huge thanks to Applied Energistics 2 and the JEI Wiki!
 * 
 * @author Amine Sebastian
 *
 */
public class SmithingRecipeProvider implements IRecipeManagerPlugin {
	private List<AutoSmithRecipeJEIWrapper> recipes;

	public SmithingRecipeProvider() {
	}

	@Override
	public <V> List<ResourceLocation> getRecipeCategoryUids(IFocus<V> focus) {
		if (focus.getMode() == IFocus.Mode.OUTPUT && focus.getValue() instanceof ItemStack) {
			// Get the focused item.
			ItemStack itemStack = (ItemStack) focus.getValue();

			// Check to see if it is a valid smithing output.
			if (isValidSmithingOutput(itemStack)) {
				return Collections.singletonList(SmithingRecipeCategory.AUTO_SMITHING_UID);
			}
		} else if (focus.getMode() == IFocus.Mode.INPUT && focus.getValue() instanceof ItemStack) {
			// Check if the input is used in any smithing recipes or is itself smithable.
			ItemStack itemStack = (ItemStack) focus.getValue();
			if (isValidSmithingInput(itemStack)) {
				return Collections.singletonList(SmithingRecipeCategory.AUTO_SMITHING_UID);
			}
		}

		return Collections.emptyList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> getRecipes(IRecipeCategory<T> recipeCategory) {
		if (recipes == null) {
			// Create the recipes array.
			recipes = new ArrayList<AutoSmithRecipeJEIWrapper>();

			// Get all the registered items and add any recipes that can be created from
			// them.
			for (Entry<RegistryKey<Item>, Item> item : GameRegistry.findRegistry(Item.class).getEntries()) {
				recipes.addAll(makeFromSmithingInput(new ItemStack(item.getValue())));
			}
		}
		return (List<T>) recipes;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T, V> List<T> getRecipes(IRecipeCategory<T> recipeCategory, IFocus<V> focus) {
		if (!SmithingRecipeCategory.AUTO_SMITHING_UID.equals(recipeCategory.getUid())) {
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

	private List<AutoSmithRecipeJEIWrapper> makeFromSmithingInput(ItemStack input) {
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

	public boolean isValidSmithingOutput(ItemStack stack) {
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

	public boolean isValidSmithingInput(ItemStack stack) {
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

	public static class AutoSmithRecipeJEIWrapper implements IRecipe<IInventory> {
		public static final IRecipeType<AutoSmithRecipeJEIWrapper> RECIPE_TYPE = IRecipeType.register("auto_smith_jei");
		public final ResourceLocation id;
		public final AutoSmithRecipe recipe;
		public final ItemStack input;
		public final ItemStack output;

		public AutoSmithRecipeJEIWrapper(AutoSmithRecipe recipe, ItemStack input, ItemStack output) {
			super();
			this.recipe = recipe;
			this.input = input;
			this.output = output;
			this.id = new ResourceLocation(recipe.getId().getNamespace(), recipe.getId().getPath() + output.getItem().getRegistryName().getPath().replace(":", "/"));
		}

		public AutoSmithRecipe getRecipe() {
			return recipe;
		}

		@Override
		public boolean matches(IInventory inv, World worldIn) {
			return false;
		}

		@Override
		public ItemStack getCraftingResult(IInventory inv) {
			return output;
		}

		@Override
		public boolean canFit(int width, int height) {
			return false;
		}

		@Override
		public ItemStack getRecipeOutput() {
			return output;
		}

		public ItemStack getInputItem() {
			return input;
		}

		public Ingredient getInput() {
			return Ingredient.fromStacks(input);
		}

		@Override
		public ResourceLocation getId() {
			return id;
		}

		@Override
		public IRecipeSerializer<?> getSerializer() {
			return null;
		}

		@Override
		public IRecipeType<?> getType() {
			return RECIPE_TYPE;
		}
	}
}