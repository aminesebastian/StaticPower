package theking530.staticpower.handlers.crafting.recipes;

import net.minecraft.init.Items;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import theking530.staticpower.items.ModItems;
import theking530.staticpower.items.tools.basictools.ModTools;

public class ToolRecipes {
	
	@SuppressWarnings("all")
	public static void registerToolRecipes() {
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.CopperAxe), new Object[]{"MM ","MS "," S ",
				'M', "ingotCopper", 'S', Items.STICK}));	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.CopperAxe), new Object[]{" MM"," SM"," S ",
				'M', "ingotCopper", 'S', Items.STICK}));	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.CopperHoe), new Object[]{"MM "," S "," S ",
				'M', "ingotCopper", 'S', Items.STICK}));	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.CopperHoe), new Object[]{" MM"," S "," S ",
				'M', "ingotCopper", 'S', Items.STICK}));	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.CopperPickaxe), new Object[]{"MMM"," S "," S ",
				'M', "ingotCopper", 'S', Items.STICK}));	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.CopperShovel), new Object[]{"M  ","S  ","S  ",
				'M', "ingotCopper", 'S', Items.STICK}));	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.CopperShovel), new Object[]{" M "," S "," S ",
				'M', "ingotCopper", 'S', Items.STICK}));	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.CopperShovel), new Object[]{"  M","  S","  S",
				'M', "ingotCopper", 'S', Items.STICK}));	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.CopperSword), new Object[]{"M  ","M  ","S  ",
				'M', "ingotCopper", 'S', Items.STICK}));	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.CopperSword), new Object[]{" M "," M "," S ",
				'M', "ingotCopper", 'S', Items.STICK}));	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.CopperSword), new Object[]{"  M","  M","  S",
				'M', "ingotCopper", 'S', Items.STICK}));	
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.TinAxe), new Object[]{"MM ","MS "," S ",
				'M', "ingotTin", 'S', Items.STICK}));	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.TinAxe), new Object[]{" MM"," SM"," S ",
				'M', "ingotTin", 'S', Items.STICK}));	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.TinHoe), new Object[]{"MM "," S "," S ",
				'M', "ingotTin", 'S', Items.STICK}));	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.TinHoe), new Object[]{" MM"," S "," S ",
				'M', "ingotTin", 'S', Items.STICK}));	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.TinPickaxe), new Object[]{"MMM"," S "," S ",
				'M', "ingotTin", 'S', Items.STICK}));	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.TinShovel), new Object[]{"M  ","S  ","S  ",
				'M', "ingotTin", 'S', Items.STICK}));	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.TinShovel), new Object[]{" M "," S "," S ",
				'M', "ingotTin", 'S', Items.STICK}));	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.TinShovel), new Object[]{"  M","  S","  S",
				'M', "ingotTin", 'S', Items.STICK}));	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.TinSword), new Object[]{"M  ","M  ","S  ",
				'M', "ingotTin", 'S', Items.STICK}));	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.TinSword), new Object[]{" M "," M "," S ",
				'M', "ingotTin", 'S', Items.STICK}));	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.TinSword), new Object[]{"  M","  M","  S",
				'M', "ingotTin", 'S', Items.STICK}));	
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.SilverAxe), new Object[]{"MM ","MS "," S ",
				'M', "ingotSilver", 'S', Items.STICK}));	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.SilverAxe), new Object[]{" MM"," SM"," S ",
				'M', "ingotSilver", 'S', Items.STICK}));	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.SilverHoe), new Object[]{"MM "," S "," S ",
				'M', "ingotSilver", 'S', Items.STICK}));	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.SilverHoe), new Object[]{" MM"," S "," S ",
				'M', "ingotSilver", 'S', Items.STICK}));	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.SilverPickaxe), new Object[]{"MMM"," S "," S ",
				'M', "ingotSilver", 'S', Items.STICK}));	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.SilverShovel), new Object[]{"M  ","S  ","S  ",
				'M', "ingotSilver", 'S', Items.STICK}));	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.SilverShovel), new Object[]{" M "," S "," S ",
				'M', "ingotSilver", 'S', Items.STICK}));	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.SilverShovel), new Object[]{"  M","  S","  S",
				'M', "ingotSilver", 'S', Items.STICK}));	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.SilverSword), new Object[]{"M  ","M  ","S  ",
				'M', "ingotSilver", 'S', Items.STICK}));	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.SilverSword), new Object[]{" M "," M "," S ",
				'M', "ingotSilver", 'S', Items.STICK}));	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.SilverSword), new Object[]{"  M","  M","  S",
				'M', "ingotSilver", 'S', Items.STICK}));	
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.LeadAxe), new Object[]{"MM ","MS "," S ",
				'M', "ingotLead", 'S', Items.STICK}));	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.LeadAxe), new Object[]{" MM"," SM"," S ",
				'M', "ingotLead", 'S', Items.STICK}));	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.LeadHoe), new Object[]{"MM "," S "," S ",
				'M', "ingotLead", 'S', Items.STICK}));	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.LeadHoe), new Object[]{" MM"," S "," S ",
				'M', "ingotLead", 'S', Items.STICK}));	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.LeadPickaxe), new Object[]{"MMM"," S "," S ",
				'M', "ingotLead", 'S', Items.STICK}));	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.LeadShovel), new Object[]{"M  ","S  ","S  ",
				'M', "ingotLead", 'S', Items.STICK}));	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.LeadShovel), new Object[]{" M "," S "," S ",
				'M', "ingotLead", 'S', Items.STICK}));	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.LeadShovel), new Object[]{"  M","  S","  S",
				'M', "ingotLead", 'S', Items.STICK}));	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.LeadSword), new Object[]{"M  ","M  ","S  ",
				'M', "ingotLead", 'S', Items.STICK}));	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.LeadSword), new Object[]{" M "," M "," S ",
				'M', "ingotLead", 'S', Items.STICK}));	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.LeadSword), new Object[]{"  M","  M","  S",
				'M', "ingotLead", 'S', Items.STICK}));	
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.PlatinumAxe), new Object[]{"MM ","MS "," S ",
				'M', "ingotPlatinum", 'S', Items.STICK}));	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.PlatinumAxe), new Object[]{" MM"," SM"," S ",
				'M', "ingotPlatinum", 'S', Items.STICK}));	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.PlatinumHoe), new Object[]{"MM "," S "," S ",
				'M', "ingotPlatinum", 'S', Items.STICK}));	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.PlatinumHoe), new Object[]{" MM"," S "," S ",
				'M', "ingotPlatinum", 'S', Items.STICK}));	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.PlatinumPickaxe), new Object[]{"MMM"," S "," S ",
				'M', "ingotPlatinum", 'S', Items.STICK}));	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.PlatinumShovel), new Object[]{"M  ","S  ","S  ",
				'M', "ingotPlatinum", 'S', Items.STICK}));	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.PlatinumShovel), new Object[]{" M "," S "," S ",
				'M', "ingotPlatinum", 'S', Items.STICK}));	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.PlatinumShovel), new Object[]{"  M","  S","  S",
				'M', "ingotPlatinum", 'S', Items.STICK}));	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.PlatinumSword), new Object[]{"M  ","M  ","S  ",
				'M', "ingotPlatinum", 'S', Items.STICK}));	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.PlatinumSword), new Object[]{" M "," M "," S ",
				'M', "ingotPlatinum", 'S', Items.STICK}));	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.PlatinumSword), new Object[]{"  M","  M","  S",
				'M', "ingotPlatinum", 'S', Items.STICK}));	
		
		GameRegistry.addRecipe(new ItemStack(ModTools.StaticAxe), new Object[]{"MM ","MS "," S ",
				'M', ModItems.StaticIngot, 'S', Items.STICK});	
		GameRegistry.addRecipe(new ItemStack(ModTools.StaticAxe), new Object[]{" MM"," SM"," S ",
				'M', ModItems.StaticIngot, 'S', Items.STICK});	
		GameRegistry.addRecipe(new ItemStack(ModTools.StaticHoe), new Object[]{"MM "," S "," S ",
				'M', ModItems.StaticIngot, 'S', Items.STICK});	
		GameRegistry.addRecipe(new ItemStack(ModTools.StaticHoe), new Object[]{" MM"," S "," S ",
				'M', ModItems.StaticIngot, 'S', Items.STICK});	
		GameRegistry.addRecipe(new ItemStack(ModTools.StaticPickaxe), new Object[]{"MMM"," S "," S ",
				'M', ModItems.StaticIngot, 'S', Items.STICK});	
		GameRegistry.addRecipe(new ItemStack(ModTools.StaticShovel), new Object[]{"M  ","S  ","S  ",
				'M', ModItems.StaticIngot, 'S', Items.STICK});	
		GameRegistry.addRecipe(new ItemStack(ModTools.StaticShovel), new Object[]{" M "," S "," S ",
				'M', ModItems.StaticIngot, 'S', Items.STICK});	
		GameRegistry.addRecipe(new ItemStack(ModTools.StaticShovel), new Object[]{"  M","  S","  S",
				'M', ModItems.StaticIngot, 'S', Items.STICK});	
		GameRegistry.addRecipe(new ItemStack(ModTools.StaticSword), new Object[]{"M  ","M  ","S  ",
				'M', ModItems.StaticIngot, 'S', Items.STICK});	
		GameRegistry.addRecipe(new ItemStack(ModTools.StaticSword), new Object[]{" M "," M "," S ",
				'M', ModItems.StaticIngot, 'S', Items.STICK});	
		GameRegistry.addRecipe(new ItemStack(ModTools.StaticSword), new Object[]{"  M","  M","  S",
				'M', ModItems.StaticIngot, 'S', Items.STICK});	
		
		GameRegistry.addRecipe(new ItemStack(ModTools.EnergizedAxe), new Object[]{"MM ","MS "," S ",
				'M', ModItems.EnergizedIngot, 'S', Items.STICK});	
		GameRegistry.addRecipe(new ItemStack(ModTools.EnergizedAxe), new Object[]{" MM"," SM"," S ",
				'M', ModItems.EnergizedIngot, 'S', Items.STICK});	
		GameRegistry.addRecipe(new ItemStack(ModTools.EnergizedHoe), new Object[]{"MM "," S "," S ",
				'M', ModItems.EnergizedIngot, 'S', Items.STICK});	
		GameRegistry.addRecipe(new ItemStack(ModTools.EnergizedHoe), new Object[]{" MM"," S "," S ",
				'M', ModItems.EnergizedIngot, 'S', Items.STICK});	
		GameRegistry.addRecipe(new ItemStack(ModTools.EnergizedPickaxe), new Object[]{"MMM"," S "," S ",
				'M', ModItems.EnergizedIngot, 'S', Items.STICK});	
		GameRegistry.addRecipe(new ItemStack(ModTools.EnergizedShovel), new Object[]{"M  ","S  ","S  ",
				'M', ModItems.EnergizedIngot, 'S', Items.STICK});	
		GameRegistry.addRecipe(new ItemStack(ModTools.EnergizedShovel), new Object[]{" M "," S "," S ",
				'M', ModItems.EnergizedIngot, 'S', Items.STICK});	
		GameRegistry.addRecipe(new ItemStack(ModTools.EnergizedShovel), new Object[]{"  M","  S","  S",
				'M', ModItems.EnergizedIngot, 'S', Items.STICK});	
		GameRegistry.addRecipe(new ItemStack(ModTools.EnergizedSword), new Object[]{"M  ","M  ","S  ",
				'M', ModItems.EnergizedIngot, 'S', Items.STICK});	
		GameRegistry.addRecipe(new ItemStack(ModTools.EnergizedSword), new Object[]{" M "," M "," S ",
				'M', ModItems.EnergizedIngot, 'S', Items.STICK});	
		GameRegistry.addRecipe(new ItemStack(ModTools.EnergizedSword), new Object[]{"  M","  M","  S",
				'M', ModItems.EnergizedIngot, 'S', Items.STICK});
		
		GameRegistry.addRecipe(new ItemStack(ModTools.LumumAxe), new Object[]{"MM ","MS "," S ",
				'M', ModItems.LumumIngot, 'S', Items.STICK});	
		GameRegistry.addRecipe(new ItemStack(ModTools.LumumAxe), new Object[]{" MM"," SM"," S ",
				'M', ModItems.LumumIngot, 'S', Items.STICK});	
		GameRegistry.addRecipe(new ItemStack(ModTools.LumumHoe), new Object[]{"MM "," S "," S ",
				'M', ModItems.LumumIngot, 'S', Items.STICK});	
		GameRegistry.addRecipe(new ItemStack(ModTools.LumumHoe), new Object[]{" MM"," S "," S ",
				'M', ModItems.LumumIngot, 'S', Items.STICK});	
		GameRegistry.addRecipe(new ItemStack(ModTools.LumumPickaxe), new Object[]{"MMM"," S "," S ",
				'M', ModItems.LumumIngot, 'S', Items.STICK});	
		GameRegistry.addRecipe(new ItemStack(ModTools.LumumShovel), new Object[]{"M  ","S  ","S  ",
				'M', ModItems.LumumIngot, 'S', Items.STICK});	
		GameRegistry.addRecipe(new ItemStack(ModTools.LumumShovel), new Object[]{" M "," S "," S ",
				'M', ModItems.LumumIngot, 'S', Items.STICK});	
		GameRegistry.addRecipe(new ItemStack(ModTools.LumumShovel), new Object[]{"  M","  S","  S",
				'M', ModItems.LumumIngot, 'S', Items.STICK});	
		GameRegistry.addRecipe(new ItemStack(ModTools.LumumSword), new Object[]{"M  ","M  ","S  ",
				'M', ModItems.LumumIngot, 'S', Items.STICK});	
		GameRegistry.addRecipe(new ItemStack(ModTools.LumumSword), new Object[]{" M "," M "," S ",
				'M', ModItems.LumumIngot, 'S', Items.STICK});	
		GameRegistry.addRecipe(new ItemStack(ModTools.LumumSword), new Object[]{"  M","  M","  S",
				'M', ModItems.LumumIngot, 'S', Items.STICK});	
	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.SapphireAxe), new Object[]{"MM ","MS "," S ",
				'M', "gemSapphire", 'S', Items.STICK}));	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.SapphireAxe), new Object[]{" MM"," SM"," S ",
				'M', "gemSapphire", 'S', Items.STICK}));	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.SapphireHoe), new Object[]{"MM "," S "," S ",
				'M', "gemSapphire", 'S', Items.STICK}));	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.SapphireHoe), new Object[]{" MM"," S "," S ",
				'M', "gemSapphire", 'S', Items.STICK}));	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.SapphirePickaxe), new Object[]{"MMM"," S "," S ",
				'M', "gemSapphire", 'S', Items.STICK}));	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.SapphireShovel), new Object[]{"M  ","S  ","S  ",
				'M', "gemSapphire", 'S', Items.STICK}));	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.SapphireShovel), new Object[]{" M "," S "," S ",
				'M', "gemSapphire", 'S', Items.STICK}));	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.SapphireShovel), new Object[]{"  M","  S","  S",
				'M', "gemSapphire", 'S', Items.STICK}));	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.SapphireSword), new Object[]{"M  ","M  ","S  ",
				'M', "gemSapphire", 'S', Items.STICK}));	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.SapphireSword), new Object[]{" M "," M "," S ",
				'M', "gemSapphire", 'S', Items.STICK}));	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.SapphireSword), new Object[]{"  M","  M","  S",
				'M', "gemSapphire", 'S', Items.STICK}));	
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.RubyAxe), new Object[]{"MM ","MS "," S ",
				'M', "gemRuby", 'S', Items.STICK}));	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.RubyAxe), new Object[]{" MM"," SM"," S ",
				'M', "gemRuby", 'S', Items.STICK}));	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.RubyHoe), new Object[]{"MM "," S "," S ",
				'M', "gemRuby", 'S', Items.STICK}));	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.RubyHoe), new Object[]{" MM"," S "," S ",
				'M', "gemRuby", 'S', Items.STICK}));	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.RubyPickaxe), new Object[]{"MMM"," S "," S ",
				'M', "gemRuby", 'S', Items.STICK}));	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.RubyShovel), new Object[]{"M  ","S  ","S  ",
				'M', "gemRuby", 'S', Items.STICK}));	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.RubyShovel), new Object[]{" M "," S "," S ",
				'M', "gemRuby", 'S', Items.STICK}));	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.RubyShovel), new Object[]{"  M","  S","  S",
				'M', "gemRuby", 'S', Items.STICK}));	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.RubySword), new Object[]{"M  ","M  ","S  ",
				'M', "gemRuby", 'S', Items.STICK}));	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.RubySword), new Object[]{" M "," M "," S ",
				'M', "gemRuby", 'S', Items.STICK}));	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModTools.RubySword), new Object[]{"  M","  M","  S",
				'M', "gemRuby", 'S', Items.STICK}));	
	}
}


