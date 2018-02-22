package theking530.staticpower.handlers.crafting.recipes;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import theking530.staticpower.assists.RegisterHelper;
import theking530.staticpower.handlers.crafting.CraftHelpers;
import theking530.staticpower.handlers.crafting.MultiOreIngredient;
import theking530.staticpower.items.SPItemMaterial;
import theking530.staticpower.items.ModItems;

public class FormerRecipes {
	
	public static void registerFusionRecipes() {
		RegisterHelper.registerFormerRecipes(CraftHelpers.outputItemStack(SPItemMaterial.plateIron, 2), CraftHelpers.ingredientFromItem(Items.IRON_INGOT), CraftHelpers.ingredientFromItem(ModItems.PlateMould));
		RegisterHelper.registerFormerRecipes(CraftHelpers.outputItemStack(SPItemMaterial.plateGold, 2), CraftHelpers.ingredientFromItem(Items.GOLD_INGOT), CraftHelpers.ingredientFromItem(ModItems.PlateMould));
		RegisterHelper.registerFormerRecipes(CraftHelpers.outputItemStack(SPItemMaterial.plateTin, 2), CraftHelpers.ingredientOre("ingotTin"), CraftHelpers.ingredientFromItem(ModItems.PlateMould));
		RegisterHelper.registerFormerRecipes(CraftHelpers.outputItemStack(SPItemMaterial.plateCopper, 2), CraftHelpers.ingredientOre("ingotCopper"), CraftHelpers.ingredientFromItem(ModItems.PlateMould));
		RegisterHelper.registerFormerRecipes(CraftHelpers.outputItemStack(SPItemMaterial.plateSilver, 2), CraftHelpers.ingredientOre("ingotSilver"), CraftHelpers.ingredientFromItem(ModItems.PlateMould));
		RegisterHelper.registerFormerRecipes(CraftHelpers.outputItemStack(SPItemMaterial.plateLead, 2), CraftHelpers.ingredientOre("ingotLead"), CraftHelpers.ingredientFromItem(ModItems.PlateMould));
		RegisterHelper.registerFormerRecipes(CraftHelpers.outputItemStack(SPItemMaterial.plateStatic, 2), CraftHelpers.ingredientFromItemstack(SPItemMaterial.ingotStatic), CraftHelpers.ingredientFromItem(ModItems.PlateMould));
		RegisterHelper.registerFormerRecipes(CraftHelpers.outputItemStack(SPItemMaterial.plateEnergized, 2), CraftHelpers.ingredientFromItemstack(SPItemMaterial.ingotEnergized), CraftHelpers.ingredientFromItem(ModItems.PlateMould));
		RegisterHelper.registerFormerRecipes(CraftHelpers.outputItemStack(SPItemMaterial.plateLumum, 2), CraftHelpers.ingredientFromItemstack(SPItemMaterial.ingotLumum), CraftHelpers.ingredientFromItem(ModItems.PlateMould));

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
