package theking530.staticpower.handlers.crafting.recipes;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import theking530.staticpower.assists.RegisterHelper;
import theking530.staticpower.blocks.ModBlocks;
import theking530.staticpower.handlers.crafting.Craft;
import theking530.staticpower.items.ItemMaterials;

public class LumberMillRecipes {
	
	public static void registerLumberMillRecipes() {
		RegisterHelper.registerLumberMillRecipe(Craft.ing(Blocks.LOG, 0), new ItemStack(Blocks.PLANKS, 6, 0), ItemMaterials.dustWood);
		RegisterHelper.registerLumberMillRecipe(Craft.ing(Blocks.LOG, 1), new ItemStack(Blocks.PLANKS, 6, 1), ItemMaterials.dustWood);
		RegisterHelper.registerLumberMillRecipe(Craft.ing(Blocks.LOG, 2), new ItemStack(Blocks.PLANKS, 6, 2), ItemMaterials.dustWood);
		RegisterHelper.registerLumberMillRecipe(Craft.ing(Blocks.LOG, 3), new ItemStack(Blocks.PLANKS, 6, 3), ItemMaterials.dustWood);
		RegisterHelper.registerLumberMillRecipe(Craft.ing(Blocks.LOG2, 0), new ItemStack(Blocks.PLANKS, 6, 4), ItemMaterials.dustWood);
		RegisterHelper.registerLumberMillRecipe(Craft.ing(Blocks.LOG2, 1), new ItemStack(Blocks.PLANKS, 6, 5), ItemMaterials.dustWood);
		
		RegisterHelper.registerLumberMillRecipe(Craft.ing(ModBlocks.StaticWood), new ItemStack(Item.getItemFromBlock(ModBlocks.StaticPlanks), 6), ItemMaterials.dustWood);
		RegisterHelper.registerLumberMillRecipe(Craft.ing(ModBlocks.EnergizedWood), new ItemStack(Item.getItemFromBlock(ModBlocks.EnergizedPlanks), 6), ItemMaterials.dustWood);
		RegisterHelper.registerLumberMillRecipe(Craft.ing(ModBlocks.LumumWood), new ItemStack(Item.getItemFromBlock(ModBlocks.LumumPlanks), 6), ItemMaterials.dustWood);
	}
}
