package theking530.staticpower.integration.JEI.categories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.advanced.IRecipeManagerPlugin;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import theking530.staticcore.cablenetwork.attachment.CableCover;
import theking530.staticpower.StaticPower;
import theking530.staticpower.init.tags.ModItemTags;

/**
 * Huge thanks to Applied Energistics 2 and the JEI Wiki!
 * 
 * @author Amine Sebastian
 *
 */
public class CoverRecipeCategory implements IRecipeManagerPlugin {

	private final CableCover cableCover;

	public CoverRecipeCategory(CableCover cableCover) {
		this.cableCover = cableCover;
	}

	@Override
	public <V> List<RecipeType<?>> getRecipeTypes(IFocus<V> focus) {
		return List.of(RecipeTypes.CRAFTING);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T, V> List<T> getRecipes(IRecipeCategory<T> recipeCategory, IFocus<V> focus) {
		if (!RecipeTypes.CRAFTING.equals(recipeCategory)) {
			return Collections.emptyList();
		}

		if (focus.getRole() == RecipeIngredientRole.OUTPUT && focus.getTypedValue().getIngredient() instanceof ItemStack) {
			// Looking up how a certain facade is crafted
			ItemStack coverSourceItemStack = (ItemStack) focus.getTypedValue().getIngredient();
			if (coverSourceItemStack.getItem() instanceof CableCover) {
				ItemStack coverBlockItem = new ItemStack(CableCover.getCoverItemBlock(coverSourceItemStack));
				return Collections.singletonList((T) make(coverBlockItem, coverSourceItemStack));
			}
		} else if (focus.getRole() == RecipeIngredientRole.INPUT && focus.getTypedValue().getIngredient() instanceof ItemStack) {
			// Get the focused item stack and item.
			ItemStack coverSourceItemStack = (ItemStack) focus.getTypedValue().getIngredient();
			Item coverSourceItem = coverSourceItemStack.getItem();

			// If the focused item is a saw, get all the recipes a saw can be used in.
			// Otherwise if the hovered item is a block ,check to see if we can make a cover
			// out of it. If so, add that recipe.
			if (ModItemTags.matches(ModItemTags.COVER_SAW, coverSourceItem)) {
				List<T> recipes = new ArrayList<T>();
				// Get all the registered blocks.
				for (Entry<ResourceKey<Block>, Block> block : ForgeRegistries.BLOCKS.getEntries()) {
					// If this is a valid cover block, create the recipe.
					if (CableCover.isValidForCover(block.getValue())) {
						ItemStack output = cableCover.makeCoverForBlock(block.getValue().defaultBlockState());
						output.setCount(8);
						recipes.add((T) make(new ItemStack(block.getValue()), output));
					}
				}
				return recipes;
			} else {
				// If there is no saw, or if the target item is not for a block, return empty.
				if (!(coverSourceItem instanceof BlockItem)) {
					return Collections.emptyList();
				}

				// If we can make a cover for this block, return that cover. Otherwise, return
				// an empty itemstack.
				if (CableCover.isValidForCover(((BlockItem) coverSourceItem).getBlock())) {
					ItemStack output = cableCover.makeCoverForBlock(((BlockItem) coverSourceItem).getBlock().defaultBlockState());
					output.setCount(8);
					return Collections.singletonList((T) make(coverSourceItemStack, output));
				} else {
					return Collections.emptyList();
				}
			}
		}

		return Collections.emptyList();
	}

	private ShapelessRecipe make(ItemStack coverBlockItem, ItemStack result) {
		// This id should only be used within JEI and not really matter
		ResourceLocation id = new ResourceLocation(StaticPower.MOD_ID, "cover/" + ForgeRegistries.ITEMS.getKey(coverBlockItem.getItem()).toString().replace(':', '/'));

		// Make sure we only display a single item.
		coverBlockItem.setCount(1);

		// Popualte the ingredients.
		NonNullList<Ingredient> ingredients = NonNullList.withSize(2, Ingredient.EMPTY);
		ingredients.set(0, Ingredient.of(ModItemTags.COVER_SAW));
		ingredients.set(1, Ingredient.of(coverBlockItem));

		// Wrap this in a shapeless recipe.
		return new ShapelessRecipe(id, "", result, ingredients);
	}

	@Override
	public <T> List<T> getRecipes(IRecipeCategory<T> recipeCategory) {
		return Collections.emptyList();
	}
}