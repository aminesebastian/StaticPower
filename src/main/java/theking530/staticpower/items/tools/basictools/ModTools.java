package theking530.staticpower.items.tools.basictools;

import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.EnumHelper;
import theking530.staticpower.assists.RegisterHelper;
import theking530.staticpower.items.ModItems;

public class ModTools {

	public static ToolMaterial COPPER = EnumHelper.addToolMaterial("COPPER", 1, 225, 5.0F, 3.0F, 15);
	public static ToolMaterial TIN = EnumHelper.addToolMaterial("TIN", 1, 225, 5.0F, 3.0F, 15);
	public static ToolMaterial SILVER = EnumHelper.addToolMaterial("SILVER", 2, 500, 8.0F, 4.0F, 20);
	public static ToolMaterial LEAD = EnumHelper.addToolMaterial("LEAD", 2, 2000, 3.0F, 4.0F, 20);
	public static ToolMaterial PLATINUM = EnumHelper.addToolMaterial("PLATINUM", 2, 2500, 5.0F, 5.0F, 30);
	public static ToolMaterial STATIC = EnumHelper.addToolMaterial("STATIC", 2, 500, 7.0F, 3.0F, 20);
	public static ToolMaterial ENERGIZED = EnumHelper.addToolMaterial("ENERGIZED", 2, 2000, 5.0F, 4.0F, 30);
	public static ToolMaterial LUMUM = EnumHelper.addToolMaterial("LUMUM", 3, 4000, 12.0F, 5.0F, 40);
	
	
	public static Item CopperPickaxe;
	public static Item CopperAxe;
	public static Item CopperShovel;
	public static Item CopperHoe;
	public static Item CopperSword;
	
	public static Item SilverPickaxe;
	public static Item SilverAxe;
	public static Item SilverShovel;
	public static Item SilverHoe;
	public static Item SilverSword;
	
	public static Item TinPickaxe;
	public static Item TinAxe;
	public static Item TinShovel;
	public static Item TinHoe;
	public static Item TinSword;
	
	public static Item LeadPickaxe;
	public static Item LeadAxe;
	public static Item LeadShovel;
	public static Item LeadHoe;
	public static Item LeadSword;
	
	public static Item PlatinumPickaxe;
	public static Item PlatinumAxe;
	public static Item PlatinumShovel;
	public static Item PlatinumHoe;
	public static Item PlatinumSword;
	
	
	public static Item StaticPickaxe;
	public static Item StaticAxe;
	public static Item StaticShovel;
	public static Item StaticHoe;
	public static Item StaticSword;
	
	public static Item EnergizedPickaxe;
	public static Item EnergizedAxe;
	public static Item EnergizedShovel;
	public static Item EnergizedHoe;
	public static Item EnergizedSword;
	
	public static Item LumumPickaxe;
	public static Item LumumAxe;
	public static Item LumumShovel;
	public static Item LumumHoe;
	public static Item LumumSword;
	
	public static void init() {
		CopperPickaxe = new BasePickaxe(COPPER, "sCopperPickaxe");
		RegisterHelper.registerItem(CopperPickaxe);	
		CopperAxe = new BaseAxe(COPPER, "sCopperAxe");
		RegisterHelper.registerItem(CopperAxe);	
		CopperHoe = new BaseHoe(COPPER, "sCopperHoe");
		RegisterHelper.registerItem(CopperHoe);	
		CopperShovel = new BaseShovel(COPPER, "sCopperShovel");
		RegisterHelper.registerItem(CopperShovel);	
		CopperSword = new BaseSword(COPPER, "sCopperSword");
		RegisterHelper.registerItem(CopperSword);	
		
		TinPickaxe = new BasePickaxe(TIN, "sTinPickaxe");
		RegisterHelper.registerItem(TinPickaxe);	
		TinAxe = new BaseAxe(TIN, "sTinAxe");
		RegisterHelper.registerItem(TinAxe);	
		TinHoe = new BaseHoe(TIN, "sTinHoe");
		RegisterHelper.registerItem(TinHoe);	
		TinShovel = new BaseShovel(TIN, "sTinShovel");
		RegisterHelper.registerItem(TinShovel);	
		TinSword = new BaseSword(TIN, "sTinSword");
		RegisterHelper.registerItem(TinSword);	
		
		SilverPickaxe = new BasePickaxe(SILVER, "sSilverPickaxe");
		RegisterHelper.registerItem(SilverPickaxe);	
		SilverAxe = new BaseAxe(SILVER, "sSilverAxe");
		RegisterHelper.registerItem(SilverAxe);	
		SilverHoe = new BaseHoe(SILVER, "sSilverHoe");
		RegisterHelper.registerItem(SilverHoe);	
		SilverShovel = new BaseShovel(SILVER, "sSilverShovel");
		RegisterHelper.registerItem(SilverShovel);	
		SilverSword = new BaseSword(SILVER, "sSilverSword");
		RegisterHelper.registerItem(SilverSword);	
		
		LeadPickaxe = new BasePickaxe(LEAD, "sLeadPickaxe");
		RegisterHelper.registerItem(LeadPickaxe);	
		LeadAxe = new BaseAxe(LEAD, "sLeadAxe");
		RegisterHelper.registerItem(LeadAxe);	
		LeadHoe = new BaseHoe(LEAD, "sLeadHoe");
		RegisterHelper.registerItem(LeadHoe);	
		LeadShovel = new BaseShovel(LEAD, "sLeadShovel");
		RegisterHelper.registerItem(LeadShovel);	
		LeadSword = new BaseSword(LEAD, "sLeadSword");
		RegisterHelper.registerItem(LeadSword);	
		
		PlatinumPickaxe = new BasePickaxe(PLATINUM, "sPlatinumPickaxe");
		RegisterHelper.registerItem(PlatinumPickaxe);	
		PlatinumAxe = new BaseAxe(PLATINUM, "sPlatinumAxe");
		RegisterHelper.registerItem(PlatinumAxe);	
		PlatinumHoe = new BaseHoe(PLATINUM, "sPlatinumHoe");
		RegisterHelper.registerItem(PlatinumHoe);	
		PlatinumShovel = new BaseShovel(PLATINUM, "sPlatinumShovel");
		RegisterHelper.registerItem(PlatinumShovel);	
		PlatinumSword = new BaseSword(PLATINUM, "sPlatinumSword");
		RegisterHelper.registerItem(PlatinumSword);	
		
		StaticPickaxe = new BasePickaxe(STATIC, "sStaticPickaxe");
		RegisterHelper.registerItem(StaticPickaxe);	
		StaticAxe = new BaseAxe(STATIC, "sStaticAxe");
		RegisterHelper.registerItem(StaticAxe);	
		StaticHoe = new BaseHoe(STATIC, "sStaticHoe");
		RegisterHelper.registerItem(StaticHoe);	
		StaticShovel = new BaseShovel(STATIC, "sStaticShovel");
		RegisterHelper.registerItem(StaticShovel);	
		StaticSword = new BaseSword(STATIC, "sStaticSword");
		RegisterHelper.registerItem(StaticSword);	
		
		EnergizedPickaxe = new BasePickaxe(ENERGIZED, "sEnergizedPickaxe");
		RegisterHelper.registerItem(EnergizedPickaxe);	
		EnergizedAxe = new BaseAxe(ENERGIZED, "sEnergizedAxe");
		RegisterHelper.registerItem(EnergizedAxe);	
		EnergizedHoe = new BaseHoe(ENERGIZED, "sEnergizedHoe");
		RegisterHelper.registerItem(EnergizedHoe);	
		EnergizedShovel = new BaseShovel(ENERGIZED, "sEnergizedShovel");
		RegisterHelper.registerItem(EnergizedShovel);	
		EnergizedSword = new BaseSword(ENERGIZED, "sEnergizedSword");
		RegisterHelper.registerItem(EnergizedSword);	
		
		LumumPickaxe = new BasePickaxe(LUMUM, "sLumumPickaxe");
		RegisterHelper.registerItem(LumumPickaxe);	
		LumumAxe = new BaseAxe(LUMUM, "sLumumAxe");
		RegisterHelper.registerItem(LumumAxe);	
		LumumHoe = new BaseHoe(LUMUM, "sLumumHoe");
		RegisterHelper.registerItem(LumumHoe);	
		LumumShovel = new BaseShovel(LUMUM, "sLumumShovel");
		RegisterHelper.registerItem(LumumShovel);	
		LumumSword = new BaseSword(LUMUM, "sLumumSword");
		RegisterHelper.registerItem(LumumSword);	
		
	}
}
