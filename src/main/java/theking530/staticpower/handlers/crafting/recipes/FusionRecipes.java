package theking530.staticpower.handlers.crafting.recipes;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import theking530.staticpower.assists.RegisterHelper;
import theking530.staticpower.assists.utilities.OreDictionaryUtilities;
import theking530.staticpower.blocks.ModBlocks;
import theking530.staticpower.handlers.crafting.Craft;
import theking530.staticpower.items.ItemMaterials;

public class FusionRecipes {
	
	public static void registerFusionRecipes() {
		RegisterHelper.registerFusionRecipe(new ItemStack(Items.SADDLE), Craft.item(Items.LEATHER), Craft.item(Items.LEATHER), Craft.item(Items.LEATHER), Craft.ore("ingotIron"), Craft.ore("ingotIron"));
		RegisterHelper.registerFusionRecipe(new ItemStack(Items.CAKE), Craft.item(Items.EGG), Craft.item(Items.SUGAR), Craft.item(Items.WHEAT), Craft.item(Items.WHEAT), Craft.item(Items.MILK_BUCKET));
		RegisterHelper.registerFusionRecipe(new ItemStack(Item.getItemFromBlock(ModBlocks.ObsidianGlass), 8), Craft.ore("dustObisidan"), Craft.ore("dustObisidan"),  Craft.ore("gemQuartz"), Craft.ore("blockGlass"), Craft.ore("blockGlass"));
		
		RegisterHelper.registerFusionRecipe(Craft.outputItemStack(ItemMaterials.ingotRedstoneAlloy, 1), Craft.ore("ingotSilver"), Craft.item(Items.REDSTONE));
		
		for(int i=0; i<OreDictionary.getOres("ingotElectrum").size(); i++) {
			ItemStack stack = OreDictionaryUtilities.getOreStack("ingotElectrum", i);
			stack.setCount(2);
			RegisterHelper.registerFusionRecipe(stack, Craft.ore("ingotGold"), Craft.ore("ingotSilver"));
		}
		for(int i=0; i<OreDictionary.getOres("ingotInvar").size(); i++) {
			ItemStack stack = OreDictionaryUtilities.getOreStack("ingotInvar", i);
			stack.setCount(3);
			RegisterHelper.registerFusionRecipe(stack, Craft.ore("ingotIron"), Craft.ore("ingotIron"), Craft.ore("ingotNickel"));
		}
	}
}
