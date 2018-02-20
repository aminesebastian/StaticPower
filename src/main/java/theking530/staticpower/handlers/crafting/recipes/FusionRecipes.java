package theking530.staticpower.handlers.crafting.recipes;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import theking530.staticpower.assists.RegisterHelper;
import theking530.staticpower.assists.utilities.OreDictionaryUtilities;
import theking530.staticpower.blocks.ModBlocks;
import theking530.staticpower.handlers.crafting.CraftHelpers;
import theking530.staticpower.items.ModItems;

public class FusionRecipes {
	
	public static void registerFusionRecipes() {
		RegisterHelper.registerFusionRecipe(new ItemStack(Items.SADDLE), CraftHelpers.ingredientFromItem(Items.LEATHER), CraftHelpers.ingredientFromItem(Items.LEATHER), CraftHelpers.ingredientFromItem(Items.LEATHER), CraftHelpers.ingredientOre("ingotIron"), CraftHelpers.ingredientOre("ingotIron"));
		RegisterHelper.registerFusionRecipe(new ItemStack(Items.CAKE), CraftHelpers.ingredientFromItem(Items.EGG), CraftHelpers.ingredientFromItem(Items.SUGAR), CraftHelpers.ingredientFromItem(Items.WHEAT), CraftHelpers.ingredientFromItem(Items.WHEAT), CraftHelpers.ingredientFromItem(Items.MILK_BUCKET));
		RegisterHelper.registerFusionRecipe(new ItemStack(Item.getItemFromBlock(ModBlocks.ObsidianGlass), 8), CraftHelpers.ingredientOre("obsidian"), CraftHelpers.ingredientOre("obsidian"),  CraftHelpers.ingredientOre("gemQuartz"), CraftHelpers.ingredientOre("blockGlass"), CraftHelpers.ingredientOre("blockGlass"));
		
		RegisterHelper.registerFusionRecipe(new ItemStack(ModItems.RedstoneAlloyIngot, 1), CraftHelpers.ingredientOre("ingotSilver"), CraftHelpers.ingredientFromItem(Items.REDSTONE));
		
		for(int i=0; i<OreDictionary.getOres("ingotElectrum").size(); i++) {
			ItemStack stack = OreDictionaryUtilities.getOreStack("ingotElectrum", i);
			stack.setCount(2);
			RegisterHelper.registerFusionRecipe(stack, CraftHelpers.ingredientOre("ingotGold"), CraftHelpers.ingredientOre("ingotSilver"));
		}
		for(int i=0; i<OreDictionary.getOres("ingotInvar").size(); i++) {
			ItemStack stack = OreDictionaryUtilities.getOreStack("ingotInvar", i);
			stack.setCount(3);
			RegisterHelper.registerFusionRecipe(stack, CraftHelpers.ingredientOre("ingotIron"), CraftHelpers.ingredientOre("ingotIron"), CraftHelpers.ingredientOre("ingotNickel"));
		}
	}
}
