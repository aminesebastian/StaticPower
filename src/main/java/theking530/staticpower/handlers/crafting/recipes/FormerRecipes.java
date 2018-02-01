package theking530.staticpower.handlers.crafting.recipes;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import theking530.staticpower.assists.RegisterHelper;
import theking530.staticpower.assists.utilities.OreDictionaryUtilities;
import theking530.staticpower.items.ModItems;

public class FormerRecipes {
	
	public static void registerFusionRecipes() {
		RegisterHelper.registerFormerRecipes(new ItemStack(ModItems.IronPlate, 2), new ItemStack(Items.IRON_INGOT), ModItems.PlateMould);
		RegisterHelper.registerFormerRecipes(new ItemStack(ModItems.GoldPlate, 2), new ItemStack(Items.GOLD_INGOT), ModItems.PlateMould);
		registerOreDictionaryRecipe(new ItemStack(ModItems.TinPlate, 2), "ingotTin", 1, ModItems.PlateMould);
		registerOreDictionaryRecipe(new ItemStack(ModItems.CopperPlate, 2), "ingotCopper", 1, ModItems.PlateMould);
		registerOreDictionaryRecipe(new ItemStack(ModItems.SilverPlate, 2), "ingotSilver", 1, ModItems.PlateMould);
		registerOreDictionaryRecipe(new ItemStack(ModItems.LeadPlate, 2), "ingotLead", 1, ModItems.PlateMould);
		RegisterHelper.registerFormerRecipes(new ItemStack(ModItems.StaticPlate, 2), new ItemStack(ModItems.StaticIngot), ModItems.PlateMould);
		RegisterHelper.registerFormerRecipes(new ItemStack(ModItems.EnergizedPlate, 2), new ItemStack(ModItems.EnergizedIngot), ModItems.PlateMould);
		RegisterHelper.registerFormerRecipes(new ItemStack(ModItems.LumumPlate, 2), new ItemStack(ModItems.LumumIngot), ModItems.PlateMould);

		registerOreDictionaryRecipe(new ItemStack(ModItems.CopperWire, 8),  "ingotCopper", 1,  ModItems.WireMould);
		registerOreDictionaryRecipe(new ItemStack(ModItems.SilverWire, 8), "ingotSilver", 1, ModItems.WireMould);
		RegisterHelper.registerFormerRecipes(new ItemStack(ModItems.GoldWire, 8), new ItemStack(Items.GOLD_INGOT), ModItems.WireMould);		
	}
	public static void registerOreDictionaryRecipe(ItemStack output, String input, int inputCount, Item mould) {
		if(OreDictionary.getOres(input).size() > 0) {
			for(int i = 0; i < OreDictionary.getOres(input).size(); i++) {
				ItemStack result = OreDictionaryUtilities.getOreDicitionaryItemStackToModify(input, i);
				result.setCount(inputCount);
				RegisterHelper.registerFormerRecipes(output, result, mould);
			}
		}
	}
}
