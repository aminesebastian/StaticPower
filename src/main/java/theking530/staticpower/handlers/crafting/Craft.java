package theking530.staticpower.handlers.crafting;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.oredict.OreIngredient;

public class Craft {
	private static Ingredient block(Block block) {
		return item(Item.getItemFromBlock(block));
	}
	private static Ingredient ore(String ore) {
		return new OreIngredient(ore);
	}
	private static Ingredient item(Item item) {
		return Ingredient.fromItem(item);
	}
	private static Ingredient itemstack(ItemStack item) {
		return Ingredient.fromStacks(item);
	}
	public static Ingredient multiOre(String... items) {
		return new MultiOreIngredient(items);
	}
	public static ItemStack outputStack(ItemStack item, int amount) {
		return ItemHandlerHelper.copyStackWithSize(item, amount);
	}	
	public static ItemStack outputStack(ItemStack item) {
		return ItemHandlerHelper.copyStackWithSize(item, 1);
	}
	public static Ingredient ing(Object input) {
		if(input instanceof String) {
			return ore((String)input);
		}else if(input instanceof Item) {
			return item((Item)input);
		}else if(input instanceof Block) {
			return block((Block)input);
		}else if(input instanceof ItemStack) {
			return itemstack((ItemStack)input);
		}
		return null;
	}
	public static Ingredient ing(Object input, int metadata) {
		if(input instanceof Item) {
			return itemstack(new ItemStack((Item)input, 1, metadata));
		}else if(input instanceof Block) {
			return itemstack(new ItemStack(Item.getItemFromBlock((Block)input), 1, metadata));
		}else if(input instanceof ItemStack) {
			return itemstack(new ItemStack(((ItemStack)input).getItem(), 1, metadata));
		}
		return null;
	}
}
