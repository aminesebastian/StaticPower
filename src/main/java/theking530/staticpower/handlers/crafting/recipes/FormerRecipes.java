package theking530.staticpower.handlers.crafting.recipes;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import theking530.staticpower.assists.RegisterHelper;
import theking530.staticpower.items.ModItems;

public class FormerRecipes {
	
	public static void registerFusionRecipes() {
		RegisterHelper.registerFormerRecipes(new ItemStack(ModItems.IronPlate, 2), new ItemStack(Items.IRON_INGOT), ModItems.PlateMould);
		RegisterHelper.registerFormerRecipes(new ItemStack(ModItems.GoldPlate, 2), new ItemStack(Items.GOLD_INGOT), ModItems.PlateMould);
		RegisterHelper.registerFormerRecipes(new ItemStack(ModItems.TinPlate, 2), new ItemStack(ModItems.TinIngot), ModItems.PlateMould);
		RegisterHelper.registerFormerRecipes(new ItemStack(ModItems.CopperPlate, 2), new ItemStack(ModItems.CopperIngot), ModItems.PlateMould);
		RegisterHelper.registerFormerRecipes(new ItemStack(ModItems.SilverPlate, 2), new ItemStack(ModItems.SilverIngot), ModItems.PlateMould);
		RegisterHelper.registerFormerRecipes(new ItemStack(ModItems.LeadPlate, 2), new ItemStack(ModItems.LeadIngot), ModItems.PlateMould);
		RegisterHelper.registerFormerRecipes(new ItemStack(ModItems.StaticPlate, 2), new ItemStack(ModItems.StaticIngot), ModItems.PlateMould);
		RegisterHelper.registerFormerRecipes(new ItemStack(ModItems.EnergizedPlate, 2), new ItemStack(ModItems.EnergizedIngot), ModItems.PlateMould);
		RegisterHelper.registerFormerRecipes(new ItemStack(ModItems.LumumPlate, 2), new ItemStack(ModItems.LumumIngot), ModItems.PlateMould);

		RegisterHelper.registerFormerRecipes(new ItemStack(ModItems.CopperWire, 16), new ItemStack(ModItems.CopperIngot), ModItems.WireMould);
		RegisterHelper.registerFormerRecipes(new ItemStack(ModItems.SilverWire, 16), new ItemStack(ModItems.SilverIngot), ModItems.WireMould);
		RegisterHelper.registerFormerRecipes(new ItemStack(ModItems.GoldWire, 16), new ItemStack(Items.GOLD_INGOT), ModItems.WireMould);
		
		
	}
}
