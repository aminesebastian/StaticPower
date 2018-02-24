package theking530.staticpower.handlers.crafting.recipes;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import theking530.staticpower.assists.MaterialSet;
import theking530.staticpower.assists.MaterialSets;
import theking530.staticpower.assists.RegisterHelper;
import theking530.staticpower.handlers.crafting.Craft;
import theking530.staticpower.handlers.crafting.MultiOreIngredient;
import theking530.staticpower.items.FormerMolds;
import theking530.staticpower.items.ModItems;

public class FormerRecipes {
	
	public static void registerFusionRecipes() {
		for(MaterialSet set : MaterialSets.MATERIALS) {
			RegisterHelper.registerFormerRecipe(Craft.outputItemStack(set.getPlate(), 2), Craft.itemstack(set.getIngot()), Craft.itemstack(FormerMolds.moldPlate));
			RegisterHelper.registerFormerRecipe(Craft.outputItemStack(set.getGear()), Craft.itemstack(set.getIngot()), Craft.itemstack(FormerMolds.moldGear));
			
			if(set.getBlock() != null) {
				RegisterHelper.registerFormerRecipe(Craft.outputItemStack(set.getPlate(), 18), Craft.block(set.getBlock()), Craft.itemstack(FormerMolds.moldPlate));
				RegisterHelper.registerFormerRecipe(Craft.outputItemStack(set.getGear(), 9), Craft.block(set.getBlock()), Craft.itemstack(FormerMolds.moldGear));
			}
		}
		
		//Wire From Plate
		RegisterHelper.registerFormerRecipe(new ItemStack(ModItems.CopperWire, 8), Craft.ore("plateCopper"), Craft.itemstack(FormerMolds.moldWire));
		RegisterHelper.registerFormerRecipe(new ItemStack(ModItems.SilverWire, 8), Craft.ore("plateSilver"), Craft.itemstack(FormerMolds.moldWire));
		RegisterHelper.registerFormerRecipe(new ItemStack(ModItems.GoldWire, 8), Craft.ore("plateGold"), Craft.itemstack(FormerMolds.moldWire));	
		
		//Wire From Ingot
		RegisterHelper.registerFormerRecipe(new ItemStack(ModItems.CopperWire, 8), Craft.ore("ingotCopper"), Craft.itemstack(FormerMolds.moldWire));
		RegisterHelper.registerFormerRecipe(new ItemStack(ModItems.SilverWire, 8), Craft.ore("ingotSilver"), Craft.itemstack(FormerMolds.moldWire));
		RegisterHelper.registerFormerRecipe(new ItemStack(ModItems.GoldWire, 8), Craft.item(Items.GOLD_INGOT), Craft.itemstack(FormerMolds.moldWire));	
			
		//Molds
		RegisterHelper.registerFormerRecipe(Craft.outputItemStack(FormerMolds.moldBlank), new MultiOreIngredient("plateTin"), Craft.ore("plateIron"));	
		RegisterHelper.registerFormerRecipe(Craft.outputItemStack(FormerMolds.moldPlate), new MultiOreIngredient("plateTin", "plateCopper", "plateSilver", "plateLead", "plateGold"), Craft.itemstack(FormerMolds.moldBlank));	
		RegisterHelper.registerFormerRecipe(Craft.outputItemStack(FormerMolds.moldWire), new MultiOreIngredient("wireCopper", "wireSilver", "wireGold"), Craft.itemstack(FormerMolds.moldBlank));	
		RegisterHelper.registerFormerRecipe(Craft.outputItemStack(FormerMolds.moldGear), new MultiOreIngredient("gearTin", "gearCopper", "gearSilver", "gearLead", "gearGold", "gearPlatinum", "gearInertInfusion","gearRedstoneAlloy"), Craft.itemstack(FormerMolds.moldBlank));	
	}
}
