package theking530.staticpower.handlers.crafting.recipes;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import theking530.staticpower.assists.MaterialSet;
import theking530.staticpower.assists.MaterialSets;
import theking530.staticpower.assists.RegisterHelper;
import theking530.staticpower.handlers.crafting.CraftHelpers;
import theking530.staticpower.handlers.crafting.MultiOreIngredient;
import theking530.staticpower.items.FormerMolds;
import theking530.staticpower.items.ModItems;

public class FormerRecipes {
	
	public static void registerFusionRecipes() {
		for(MaterialSet set : MaterialSets.MATERIALS) {
			RegisterHelper.registerFormerRecipes(CraftHelpers.outputItemStack(set.getPlate(), 2), CraftHelpers.ingredientFromItemstack(set.getIngot()), CraftHelpers.ingredientFromItemstack(FormerMolds.moldPlate));
			RegisterHelper.registerFormerRecipes(CraftHelpers.outputItemStack(set.getGear()), CraftHelpers.ingredientFromItemstack(set.getIngot()), CraftHelpers.ingredientFromItemstack(FormerMolds.moldGear));
			
			if(set.getBlock() != null) {
				RegisterHelper.registerFormerRecipes(CraftHelpers.outputItemStack(set.getPlate(), 18), CraftHelpers.ingredientFromBlock(set.getBlock()), CraftHelpers.ingredientFromItemstack(FormerMolds.moldPlate));
				RegisterHelper.registerFormerRecipes(CraftHelpers.outputItemStack(set.getGear(), 9), CraftHelpers.ingredientFromBlock(set.getBlock()), CraftHelpers.ingredientFromItemstack(FormerMolds.moldGear));
			}
		}
		
		//Wire From Plate
		RegisterHelper.registerFormerRecipes(new ItemStack(ModItems.CopperWire, 8), CraftHelpers.ingredientOre("plateCopper"), CraftHelpers.ingredientFromItemstack(FormerMolds.moldWire));
		RegisterHelper.registerFormerRecipes(new ItemStack(ModItems.SilverWire, 8), CraftHelpers.ingredientOre("plateSilver"), CraftHelpers.ingredientFromItemstack(FormerMolds.moldWire));
		RegisterHelper.registerFormerRecipes(new ItemStack(ModItems.GoldWire, 8), CraftHelpers.ingredientOre("plateGold"), CraftHelpers.ingredientFromItemstack(FormerMolds.moldWire));	
		
		//Wire From Ingot
		RegisterHelper.registerFormerRecipes(new ItemStack(ModItems.CopperWire, 8), CraftHelpers.ingredientOre("ingotCopper"), CraftHelpers.ingredientFromItemstack(FormerMolds.moldWire));
		RegisterHelper.registerFormerRecipes(new ItemStack(ModItems.SilverWire, 8), CraftHelpers.ingredientOre("ingotSilver"), CraftHelpers.ingredientFromItemstack(FormerMolds.moldWire));
		RegisterHelper.registerFormerRecipes(new ItemStack(ModItems.GoldWire, 8), CraftHelpers.ingredientFromItem(Items.GOLD_INGOT), CraftHelpers.ingredientFromItemstack(FormerMolds.moldWire));	
			
		//Molds
		RegisterHelper.registerFormerRecipes(CraftHelpers.outputItemStack(FormerMolds.moldBlank), new MultiOreIngredient("plateTin"), CraftHelpers.ingredientOre("plateIron"));	
		RegisterHelper.registerFormerRecipes(CraftHelpers.outputItemStack(FormerMolds.moldPlate), new MultiOreIngredient("plateTin", "plateCopper", "plateSilver", "plateLead", "plateGold"), CraftHelpers.ingredientFromItemstack(FormerMolds.moldBlank));	
		RegisterHelper.registerFormerRecipes(CraftHelpers.outputItemStack(FormerMolds.moldWire), new MultiOreIngredient("wireCopper", "wireSilver", "wireGold"), CraftHelpers.ingredientFromItemstack(FormerMolds.moldBlank));	
		RegisterHelper.registerFormerRecipes(CraftHelpers.outputItemStack(FormerMolds.moldGear), new MultiOreIngredient("gearTin", "gearCopper", "gearSilver", "gearLead", "gearGold", "gearPlatinum", "gearInertInfusion","gearRedstoneAlloy"), CraftHelpers.ingredientFromItemstack(FormerMolds.moldBlank));	
	}
}
