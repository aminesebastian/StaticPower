package theking530.staticpower.handlers.crafting.recipes;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import theking530.staticpower.assists.RegisterHelper;
import theking530.staticpower.blocks.ModBlocks;
import theking530.staticpower.items.ModItems;

public class FusionRecipes {
	
	public static void registerFusionRecipes() {
		RegisterHelper.registerFusionRecipe(new ItemStack(Items.SADDLE), new ItemStack(Items.LEATHER), new ItemStack(Items.LEATHER), new ItemStack(Items.LEATHER), new ItemStack(Items.IRON_INGOT), new ItemStack(Items.IRON_INGOT));
		RegisterHelper.registerFusionRecipe(new ItemStack(Items.CAKE), new ItemStack(Items.EGG), new ItemStack(Items.SUGAR), new ItemStack(Items.WHEAT), new ItemStack(Items.WHEAT), new ItemStack(Items.MILK_BUCKET));
		RegisterHelper.registerFusionRecipe(new ItemStack(Item.getItemFromBlock(ModBlocks.ObsidianGlass), 8), new ItemStack(Item.getItemFromBlock(Blocks.OBSIDIAN)), new ItemStack(Item.getItemFromBlock(Blocks.OBSIDIAN)), new ItemStack(Items.QUARTZ),new ItemStack(Item.getItemFromBlock(Blocks.GLASS)) , new ItemStack(Item.getItemFromBlock(Blocks.GLASS)));
		
		for(int i=0; i<OreDictionary.getOres("ingotSilver").size(); i++) {
			RegisterHelper.registerFusionRecipe(new ItemStack(ModItems.RedstoneAlloyIngot, 1), OreDictionary.getOres("ingotSilver").get(i), new ItemStack(Items.REDSTONE));
		}
		
		for(int i=0; i<OreDictionary.getOres("ingotSilver").size(); i++) {
			oreDictionaryOutputRecipe("ingotElectrum", 2, new ItemStack(Items.GOLD_INGOT), OreDictionary.getOres("ingotSilver").get(i));
		}
		for(int i=0; i<OreDictionary.getOres("ingotNickel").size(); i++) {
			oreDictionaryOutputRecipe("ingotInvar", 3, new ItemStack(Items.IRON_INGOT), new ItemStack(Items.IRON_INGOT), OreDictionary.getOres("ingotNickel").get(i));
		}
	}
	
	public static void oreDictionaryOutputRecipe(String outputString, int outputAmount, ItemStack...inputs) {
		if(OreDictionary.getOres(outputString).size() > 0) {
			ItemStack output = OreDictionary.getOres(outputString).get(0).copy();
			output.setCount(outputAmount);
			RegisterHelper.registerFusionRecipe(output, inputs);
		}
	}
}
