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
		RegisterHelper.registerFusionRecipe(new ItemStack(Items.SADDLE), Craft.ing(Items.LEATHER), Craft.ing(Items.LEATHER), Craft.ing(Items.LEATHER), Craft.ing("ingotIron"), Craft.ing("ingotIron"));
		RegisterHelper.registerFusionRecipe(new ItemStack(Items.CAKE), Craft.ing(Items.EGG), Craft.ing(Items.SUGAR), Craft.ing(Items.WHEAT), Craft.ing(Items.WHEAT), Craft.ing(Items.MILK_BUCKET));
		RegisterHelper.registerFusionRecipe(new ItemStack(Item.getItemFromBlock(ModBlocks.ObsidianGlass), 8), Craft.ing("dustObsidian"), Craft.ing("dustObsidian"),  Craft.ing("gemQuartz"), Craft.ing("blockGlass"), Craft.ing("blockGlass"));
		
		RegisterHelper.registerFusionRecipe(Craft.outputStack(ItemMaterials.ingotRedstoneAlloy, 1), Craft.ing("ingotSilver"), Craft.ing(Items.REDSTONE));
		
		for(int i=0; i<OreDictionary.getOres("ingotElectrum").size(); i++) {
			ItemStack stack = OreDictionaryUtilities.getOreStack("ingotElectrum", i);
			stack.setCount(2);
			RegisterHelper.registerFusionRecipe(stack, Craft.ing("ingotGold"), Craft.ing("ingotSilver"));
		}
		for(int i=0; i<OreDictionary.getOres("ingotInvar").size(); i++) {
			ItemStack stack = OreDictionaryUtilities.getOreStack("ingotInvar", i);
			stack.setCount(3);
			RegisterHelper.registerFusionRecipe(stack, Craft.ing("ingotIron"), Craft.ing("ingotIron"), Craft.ing("ingotNickel"));
		}
		for(int i=0; i<OreDictionary.getOres("ingotConstantan").size(); i++) {
			ItemStack stack = OreDictionaryUtilities.getOreStack("ingotConstantan", i);
			stack.setCount(2);
			RegisterHelper.registerFusionRecipe(stack, Craft.ing("ingotCopper"), Craft.ing("ingotNickel"));
		}
	}
}
