package theking530.staticpower.handlers.crafting;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.oredict.OreIngredient;

public class CraftHelpers {
	public static Ingredient ingredientFromBlock(Block block) {
		return ingredientFromItem(Item.getItemFromBlock(block));
	}
	public static Ingredient ingredientOre(String ore) {
		return new OreIngredient(ore);
	}
	public static Ingredient ingredientFromItem(Item item) {
		return Ingredient.fromItem(item);
	}
	public static Ingredient ingredientFromItemstack(ItemStack item) {
		return Ingredient.fromStacks(item);
	}
	public static ItemStack outputItemStack(ItemStack item, int amount) {
		return ItemHandlerHelper.copyStackWithSize(item, amount);
	}	
	public static ItemStack outputItemStack(ItemStack item) {
		return ItemHandlerHelper.copyStackWithSize(item, 1);
	}
}
