package theking530.staticpower.handlers.crafting;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.oredict.OreIngredient;

public class Craft {
	public static Ingredient block(Block block) {
		return item(Item.getItemFromBlock(block));
	}
	public static Ingredient ore(String ore) {
		return new OreIngredient(ore);
	}
	public static Ingredient item(Item item) {
		return Ingredient.fromItem(item);
	}
	public static Ingredient itemstack(ItemStack item) {
		return Ingredient.fromStacks(item);
	}
	public static ItemStack outputItemStack(ItemStack item, int amount) {
		return ItemHandlerHelper.copyStackWithSize(item, amount);
	}	
	public static ItemStack outputItemStack(ItemStack item) {
		return ItemHandlerHelper.copyStackWithSize(item, 1);
	}
}
