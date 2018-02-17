package theking530.staticpower.handlers.crafting.recipes;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import theking530.staticpower.assists.RegisterHelper;
import theking530.staticpower.handlers.crafting.CraftHelpers;
import theking530.staticpower.handlers.crafting.MultiOreIngredient;
import theking530.staticpower.items.ModItems;

public class FormerRecipes {
	
	public static void registerFusionRecipes() {
		RegisterHelper.registerFormerRecipes(new ItemStack(ModItems.IronPlate, 2), CraftHelpers.ingredientFromItem(Items.IRON_INGOT), CraftHelpers.ingredientFromItem(ModItems.PlateMould));
		RegisterHelper.registerFormerRecipes(new ItemStack(ModItems.GoldPlate, 2), CraftHelpers.ingredientFromItem(Items.GOLD_INGOT), CraftHelpers.ingredientFromItem(ModItems.PlateMould));
		RegisterHelper.registerFormerRecipes(new ItemStack(ModItems.TinPlate, 2), CraftHelpers.ingredientOre("ingotTin"), CraftHelpers.ingredientFromItem(ModItems.PlateMould));
		RegisterHelper.registerFormerRecipes(new ItemStack(ModItems.CopperPlate, 2), CraftHelpers.ingredientOre("ingotCopper"), CraftHelpers.ingredientFromItem(ModItems.PlateMould));
		RegisterHelper.registerFormerRecipes(new ItemStack(ModItems.SilverPlate, 2), CraftHelpers.ingredientOre("ingotSilver"), CraftHelpers.ingredientFromItem(ModItems.PlateMould));
		RegisterHelper.registerFormerRecipes(new ItemStack(ModItems.LeadPlate, 2), CraftHelpers.ingredientOre("ingotLead"), CraftHelpers.ingredientFromItem(ModItems.PlateMould));
		RegisterHelper.registerFormerRecipes(new ItemStack(ModItems.StaticPlate, 2), CraftHelpers.ingredientFromItem(ModItems.StaticIngot), CraftHelpers.ingredientFromItem(ModItems.PlateMould));
		RegisterHelper.registerFormerRecipes(new ItemStack(ModItems.EnergizedPlate, 2), CraftHelpers.ingredientFromItem(ModItems.EnergizedIngot), CraftHelpers.ingredientFromItem(ModItems.PlateMould));
		RegisterHelper.registerFormerRecipes(new ItemStack(ModItems.LumumPlate, 2), CraftHelpers.ingredientFromItem(ModItems.LumumIngot), CraftHelpers.ingredientFromItem(ModItems.PlateMould));

		RegisterHelper.registerFormerRecipes(new ItemStack(ModItems.CopperWire, 8), CraftHelpers.ingredientOre("plateCopper"), CraftHelpers.ingredientFromItem(ModItems.WireMould));
		RegisterHelper.registerFormerRecipes(new ItemStack(ModItems.SilverWire, 8), CraftHelpers.ingredientOre("plateSilver"), CraftHelpers.ingredientFromItem(ModItems.WireMould));
		RegisterHelper.registerFormerRecipes(new ItemStack(ModItems.GoldWire, 8), CraftHelpers.ingredientOre("plateGold"), CraftHelpers.ingredientFromItem(ModItems.WireMould));	
		
		RegisterHelper.registerFormerRecipes(new ItemStack(ModItems.CopperWire, 8), CraftHelpers.ingredientOre("ingotCopper"), CraftHelpers.ingredientFromItem(ModItems.WireMould));
		RegisterHelper.registerFormerRecipes(new ItemStack(ModItems.SilverWire, 8), CraftHelpers.ingredientOre("ingotSilver"), CraftHelpers.ingredientFromItem(ModItems.WireMould));
		RegisterHelper.registerFormerRecipes(new ItemStack(ModItems.GoldWire, 8), CraftHelpers.ingredientFromItem(Items.GOLD_INGOT), CraftHelpers.ingredientFromItem(ModItems.WireMould));	
		
		
		
		
		
		RegisterHelper.registerFormerRecipes(new ItemStack(ModItems.PlateMould), new MultiOreIngredient("plateTin", "plateCopper", "plateSilver", "plateLead", "plateGold"), CraftHelpers.ingredientFromItem(ModItems.BlankMould));	
		RegisterHelper.registerFormerRecipes(new ItemStack(ModItems.WireMould), new MultiOreIngredient("wireCopper", "wireSilver", "wireGold"), CraftHelpers.ingredientFromItem(ModItems.BlankMould));	
		RegisterHelper.registerFormerRecipes(new ItemStack(ModItems.BlankMould), new MultiOreIngredient("plateTin", "plateCopper", "plateSilver", "plateLead", "plateGold"), new MultiOreIngredient("plateTin", "plateCopper", "plateSilver", "plateLead", "plateGold"));	
		
	}
}
