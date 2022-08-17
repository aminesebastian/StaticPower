package theking530.staticpower.integration.JEI.categories.covers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.IFocus;
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
import net.minecraftforge.registries.RegistryManager;
import theking530.staticpower.StaticPower;
import theking530.staticpower.cables.attachments.cover.CableCover;
import theking530.staticpower.init.ModTags;

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
	public <V> List<ResourceLocation> getRecipeCategoryUids(IFocus<V> focus) {
		if (focus.getMode() == IFocus.Mode.OUTPUT && focus.getValue() instanceof ItemStack) {
			// Looking up how a certain cover is crafted.
			ItemStack itemStack = (ItemStack) focus.getValue();
			if (itemStack.getItem() instanceof CableCover) {
				return Collections.singletonList(VanillaRecipeCategoryUid.CRAFTING);
			}
		} else if (focus.getMode() == IFocus.Mode.INPUT && focus.getValue() instanceof ItemStack) {
			// Looking up if a certain block can be used to make a cover. If this is a
			// coversaw, then just return the crafting UI.r
			ItemStack itemStack = (ItemStack) focus.getValue();
			if (itemStack.getItem() instanceof BlockItem) {
				BlockItem blockItem = (BlockItem) itemStack.getItem();
				if (CableCover.isValidForCover(blockItem.getBlock())) {
					return Collections.singletonList(VanillaRecipeCategoryUid.CRAFTING);
				}
			} else if (ModTags.tagContainsItem(ModTags.COVER_SAW, itemStack.getItem())) {
				return Collections.singletonList(VanillaRecipeCategoryUid.CRAFTING);
			}
		}

		return Collections.emptyList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T, V> List<T> getRecipes(IRecipeCategory<T> recipeCategory, IFocus<V> focus) {
		if (!VanillaRecipeCategoryUid.CRAFTING.equals(recipeCategory.getUid())) {
			return Collections.emptyList();
		}

		if (focus.getMode() == IFocus.Mode.OUTPUT && focus.getValue() instanceof ItemStack) {
			// Looking up how a certain facade is crafted
			ItemStack coverSourceItemStack = (ItemStack) focus.getValue();
			if (coverSourceItemStack.getItem() instanceof CableCover) {
				ItemStack coverBlockItem = new ItemStack(CableCover.getCoverItemBlock(coverSourceItemStack));
				return Collections.singletonList((T) make(coverBlockItem, coverSourceItemStack));
			}
		} else if (focus.getMode() == IFocus.Mode.INPUT && focus.getValue() instanceof ItemStack) {
			// Get the focused item stack and item.
			ItemStack coverSourceItemStack = (ItemStack) focus.getValue();
			Item coverSourceItem = coverSourceItemStack.getItem();

			// If the focused item is a saw, get all the recipes a saw can be used in.
			// Otherwise if the hovered item is a block ,check to see if we can make a cover
			// out of it. If so, add that recipe.
			if (ModTags.tagContainsItem(ModTags.COVER_SAW, coverSourceItem)) {
				List<T> recipes = new ArrayList<T>();
				// Get all the registered blocks.
				for (Entry<ResourceKey<Block>, Block> block : RegistryManager.ACTIVE.getRegistry(Block.class)
						.getEntries()) {
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
					ItemStack output = cableCover
							.makeCoverForBlock(((BlockItem) coverSourceItem).getBlock().defaultBlockState());
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
		ResourceLocation id = new ResourceLocation(StaticPower.MOD_ID,
				"cover/" + coverBlockItem.getItem().getRegistryName().toString().replace(':', '/'));

		// Make sure we only display a single item.
		coverBlockItem.setCount(1);

		// Popualte the ingredients.
		NonNullList<Ingredient> ingredients = NonNullList.withSize(2, Ingredient.EMPTY);
		ingredients.set(0, Ingredient.of(ModTags.COVER_SAW));
		ingredients.set(1, Ingredient.of(coverBlockItem));

		// Wrap this in a shapeless recipe.
		return new ShapelessRecipe(id, "", result, ingredients);
	}

	@Override
	public <T> List<T> getRecipes(IRecipeCategory<T> recipeCategory) {
		return Collections.emptyList();
	}
}