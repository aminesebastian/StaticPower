package theking530.staticpower.crafting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class CraftingIngredient {
	public enum CraftingIngredientType {
		BASE_ITEM, EXACT_MATCH, TAG_MATCH
	}

	private final CraftingIngredientType recipeType;
	private final Item ingredientItem;
	private final ItemStack ingredientItemStack;
	private final ResourceLocation ingredientTag;

	public CraftingIngredient(Item ingredientItem) {
		this.ingredientItem = ingredientItem;
		recipeType = CraftingIngredientType.BASE_ITEM;
		ingredientTag = null;
		ingredientItemStack = null;
	}

	public CraftingIngredient(ItemStack ingredientItemStack) {
		this.ingredientItemStack = ingredientItemStack;
		recipeType = CraftingIngredientType.EXACT_MATCH;
		ingredientTag = null;
		ingredientItem = null;
	}

	public CraftingIngredientType getType() {
		return recipeType;
	}

	public boolean doesItemStackMatch(ItemStack stackInput) {
		// IF the itemstack is null or empty, return false.
		if (stackInput == null || stackInput.isEmpty()) {
			return false;
		}

		// Peform a check based on the reicpe type.
		switch (recipeType) {
		case BASE_ITEM:
			return stackInput.getItem() == ingredientItem;
		case EXACT_MATCH:
			return stackInput.getItem() == ingredientItemStack.getItem() && stackInput.getDamage() == ingredientItemStack.getDamage();
		case TAG_MATCH:
			for (ResourceLocation tag : stackInput.getItem().getTags()) {
				return tag == ingredientTag;
			}
		}
		return false;
	}

	
}
