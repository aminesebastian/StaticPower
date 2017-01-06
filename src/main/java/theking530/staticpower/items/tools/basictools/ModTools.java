package theking530.staticpower.items.tools.basictools;

import net.minecraft.item.Item;
import theking530.staticpower.assists.RegisterHelper;
import theking530.staticpower.items.ModMaterials;

public class ModTools {
	
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
	
	public static Item SapphirePickaxe;
	public static Item SapphireAxe;
	public static Item SapphireShovel;
	public static Item SapphireHoe;
	public static Item SapphireSword;
	
	public static Item RubyPickaxe;
	public static Item RubyAxe;
	public static Item RubyShovel;
	public static Item RubyHoe;
	public static Item RubySword;
	
	public static void init() {
		CopperPickaxe = new BasePickaxe(ModMaterials.COPPER, "sCopperPickaxe");
		RegisterHelper.registerItem(CopperPickaxe);	
		CopperAxe = new BaseAxe(ModMaterials.COPPER, "sCopperAxe");
		RegisterHelper.registerItem(CopperAxe);	
		CopperHoe = new BaseHoe(ModMaterials.COPPER, "sCopperHoe");
		RegisterHelper.registerItem(CopperHoe);	
		CopperShovel = new BaseShovel(ModMaterials.COPPER, "sCopperShovel");
		RegisterHelper.registerItem(CopperShovel);	
		CopperSword = new BaseSword(ModMaterials.COPPER, "sCopperSword");
		RegisterHelper.registerItem(CopperSword);	
		
		TinPickaxe = new BasePickaxe(ModMaterials.TIN, "sTinPickaxe");
		RegisterHelper.registerItem(TinPickaxe);	
		TinAxe = new BaseAxe(ModMaterials.TIN, "sTinAxe");
		RegisterHelper.registerItem(TinAxe);	
		TinHoe = new BaseHoe(ModMaterials.TIN, "sTinHoe");
		RegisterHelper.registerItem(TinHoe);	
		TinShovel = new BaseShovel(ModMaterials.TIN, "sTinShovel");
		RegisterHelper.registerItem(TinShovel);	
		TinSword = new BaseSword(ModMaterials.TIN, "sTinSword");
		RegisterHelper.registerItem(TinSword);	
		
		SilverPickaxe = new BasePickaxe(ModMaterials.SILVER, "sSilverPickaxe");
		RegisterHelper.registerItem(SilverPickaxe);	
		SilverAxe = new BaseAxe(ModMaterials.SILVER, "sSilverAxe");
		RegisterHelper.registerItem(SilverAxe);	
		SilverHoe = new BaseHoe(ModMaterials.SILVER, "sSilverHoe");
		RegisterHelper.registerItem(SilverHoe);	
		SilverShovel = new BaseShovel(ModMaterials.SILVER, "sSilverShovel");
		RegisterHelper.registerItem(SilverShovel);	
		SilverSword = new BaseSword(ModMaterials.SILVER, "sSilverSword");
		RegisterHelper.registerItem(SilverSword);	
		
		LeadPickaxe = new BasePickaxe(ModMaterials.LEAD, "sLeadPickaxe");
		RegisterHelper.registerItem(LeadPickaxe);	
		LeadAxe = new BaseAxe(ModMaterials.LEAD, "sLeadAxe");
		RegisterHelper.registerItem(LeadAxe);	
		LeadHoe = new BaseHoe(ModMaterials.LEAD, "sLeadHoe");
		RegisterHelper.registerItem(LeadHoe);	
		LeadShovel = new BaseShovel(ModMaterials.LEAD, "sLeadShovel");
		RegisterHelper.registerItem(LeadShovel);	
		LeadSword = new BaseSword(ModMaterials.LEAD, "sLeadSword");
		RegisterHelper.registerItem(LeadSword);	
		
		PlatinumPickaxe = new BasePickaxe(ModMaterials.PLATINUM, "sPlatinumPickaxe");
		RegisterHelper.registerItem(PlatinumPickaxe);	
		PlatinumAxe = new BaseAxe(ModMaterials.PLATINUM, "sPlatinumAxe");
		RegisterHelper.registerItem(PlatinumAxe);	
		PlatinumHoe = new BaseHoe(ModMaterials.PLATINUM, "sPlatinumHoe");
		RegisterHelper.registerItem(PlatinumHoe);	
		PlatinumShovel = new BaseShovel(ModMaterials.PLATINUM, "sPlatinumShovel");
		RegisterHelper.registerItem(PlatinumShovel);	
		PlatinumSword = new BaseSword(ModMaterials.PLATINUM, "sPlatinumSword");
		RegisterHelper.registerItem(PlatinumSword);	
		
		StaticPickaxe = new BasePickaxe(ModMaterials.STATIC, "sStaticPickaxe");
		RegisterHelper.registerItem(StaticPickaxe);	
		StaticAxe = new BaseAxe(ModMaterials.STATIC, "sStaticAxe");
		RegisterHelper.registerItem(StaticAxe);	
		StaticHoe = new BaseHoe(ModMaterials.STATIC, "sStaticHoe");
		RegisterHelper.registerItem(StaticHoe);	
		StaticShovel = new BaseShovel(ModMaterials.STATIC, "sStaticShovel");
		RegisterHelper.registerItem(StaticShovel);	
		StaticSword = new BaseSword(ModMaterials.STATIC, "sStaticSword");
		RegisterHelper.registerItem(StaticSword);	
		
		EnergizedPickaxe = new BasePickaxe(ModMaterials.ENERGIZED, "sEnergizedPickaxe");
		RegisterHelper.registerItem(EnergizedPickaxe);	
		EnergizedAxe = new BaseAxe(ModMaterials.ENERGIZED, "sEnergizedAxe");
		RegisterHelper.registerItem(EnergizedAxe);	
		EnergizedHoe = new BaseHoe(ModMaterials.ENERGIZED, "sEnergizedHoe");
		RegisterHelper.registerItem(EnergizedHoe);	
		EnergizedShovel = new BaseShovel(ModMaterials.ENERGIZED, "sEnergizedShovel");
		RegisterHelper.registerItem(EnergizedShovel);	
		EnergizedSword = new BaseSword(ModMaterials.ENERGIZED, "sEnergizedSword");
		RegisterHelper.registerItem(EnergizedSword);	
		
		LumumPickaxe = new BasePickaxe(ModMaterials.LUMUM, "sLumumPickaxe");
		RegisterHelper.registerItem(LumumPickaxe);	
		LumumAxe = new BaseAxe(ModMaterials.LUMUM, "sLumumAxe");
		RegisterHelper.registerItem(LumumAxe);	
		LumumHoe = new BaseHoe(ModMaterials.LUMUM, "sLumumHoe");
		RegisterHelper.registerItem(LumumHoe);	
		LumumShovel = new BaseShovel(ModMaterials.LUMUM, "sLumumShovel");
		RegisterHelper.registerItem(LumumShovel);	
		LumumSword = new BaseSword(ModMaterials.LUMUM, "sLumumSword");
		RegisterHelper.registerItem(LumumSword);
		
		SapphirePickaxe = new BasePickaxe(ModMaterials.SAPPHIRE, "sSapphirePickaxe");
		RegisterHelper.registerItem(SapphirePickaxe);	
		SapphireAxe = new BaseAxe(ModMaterials.SAPPHIRE, "sSapphireAxe");
		RegisterHelper.registerItem(SapphireAxe);	
		SapphireHoe = new BaseHoe(ModMaterials.SAPPHIRE, "sSapphireHoe");
		RegisterHelper.registerItem(SapphireHoe);	
		SapphireShovel = new BaseShovel(ModMaterials.SAPPHIRE, "sSapphireShovel");
		RegisterHelper.registerItem(SapphireShovel);	
		SapphireSword = new BaseSword(ModMaterials.SAPPHIRE, "sSapphireSword");
		RegisterHelper.registerItem(SapphireSword);	
		
		RubyPickaxe = new BasePickaxe(ModMaterials.RUBY, "sRubyPickaxe");
		RegisterHelper.registerItem(RubyPickaxe);	
		RubyAxe = new BaseAxe(ModMaterials.RUBY, "sRubyAxe");
		RegisterHelper.registerItem(RubyAxe);	
		RubyHoe = new BaseHoe(ModMaterials.RUBY, "sRubyHoe");
		RegisterHelper.registerItem(RubyHoe);	
		RubyShovel = new BaseShovel(ModMaterials.RUBY, "sRubyShovel");
		RegisterHelper.registerItem(RubyShovel);	
		RubySword = new BaseSword(ModMaterials.RUBY, "sRubySword");
		RegisterHelper.registerItem(RubySword);			
	}
}
