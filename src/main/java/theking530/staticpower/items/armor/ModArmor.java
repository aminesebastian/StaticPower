package theking530.staticpower.items.armor;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import theking530.staticpower.assists.RegisterHelper;
import theking530.staticpower.items.ModMaterials;

public class ModArmor {

	
	public static Item StaticHelmet;
	public static Item StaticChestplate;
	public static Item StaticLeggings;
	public static Item StaticBoots;
	
	public static Item EnergizedHelmet;
	public static Item EnergizedChestplate;
	public static Item EnergizedLeggings;
	public static Item EnergizedBoots;
	
	public static Item LumumHelmet;
	public static Item LumumChestplate;
	public static Item LumumLeggings;
	public static Item LumumBoots;
	
	public static Item CopperHelmet;
	public static Item CopperChestplate;
	public static Item CopperLeggings;
	public static Item CopperBoots;
	
	public static Item TinHelmet;
	public static Item TinChestplate;
	public static Item TinLeggings;
	public static Item TinBoots;
	
	public static Item LeadHelmet;
	public static Item LeadChestplate;
	public static Item LeadLeggings;
	public static Item LeadBoots;
	
	public static Item SilverHelmet;
	public static Item SilverChestplate;
	public static Item SilverLeggings;
	public static Item SilverBoots;
	
	public static Item PlatinumHelmet;
	public static Item PlatinumChestplate;
	public static Item PlatinumLeggings;
	public static Item PlatinumBoots;
	
	public static Item AluminiumHelmet;
	public static Item AluminiumChestplate;
	public static Item AluminiumLeggings;
	public static Item AluminiumBoots;
	
	public static Item SapphireHelmet;
	public static Item SapphireChestplate;
	public static Item SapphireLeggings;
	public static Item SapphireBoots;
	
	public static Item RubyHelmet;
	public static Item RubyChestplate;
	public static Item RubyLeggings;
	public static Item RubyBoots;
	
	public static Item UndeadHelmet;
	public static Item UndeadChestplate;
	public static Item UndeadLeggings;
	public static Item UndeadBoots;
	
	public static Item SkeletonHelmet;
	public static Item SkeletonChestplate;
	public static Item SkeletonLeggings;
	public static Item SkeletonBoots;
	
	public static Item BaseShield;
	
	public static void init() {		
		
		BaseShield = new BaseShield("BaseShield", BaseArmor.ArmorType.MEDIUM, ModMaterials.COPPER);
		RegisterHelper.registerItem(BaseShield);	
			
		SkeletonHelmet = new SkeletonArmor("SkeletonHelmet", BaseArmor.ArmorType.LIGHT, ModMaterials.SKELETON, EntityEquipmentSlot.HEAD);
		RegisterHelper.registerItem(SkeletonHelmet);	
		SkeletonChestplate = new SkeletonArmor("SkeletonChestplate", BaseArmor.ArmorType.LIGHT, ModMaterials.SKELETON, EntityEquipmentSlot.CHEST);
		RegisterHelper.registerItem(SkeletonChestplate);		
		SkeletonLeggings = new SkeletonArmor("SkeletonLeggings", BaseArmor.ArmorType.LIGHT, ModMaterials.SKELETON, EntityEquipmentSlot.LEGS);
		RegisterHelper.registerItem(SkeletonLeggings);		
		SkeletonBoots = new SkeletonArmor("SkeletonBoots", BaseArmor.ArmorType.LIGHT, ModMaterials.SKELETON, EntityEquipmentSlot.FEET);
		RegisterHelper.registerItem(SkeletonBoots);
		
		
		UndeadHelmet = new UndeadArmor("UndeadHelmet", BaseArmor.ArmorType.LIGHT, ModMaterials.UNDEAD, EntityEquipmentSlot.HEAD);
		RegisterHelper.registerItem(UndeadHelmet);	
		UndeadChestplate = new UndeadArmor("UndeadChestplate", BaseArmor.ArmorType.LIGHT, ModMaterials.UNDEAD, EntityEquipmentSlot.CHEST);
		RegisterHelper.registerItem(UndeadChestplate);		
		UndeadLeggings = new UndeadArmor("UndeadLeggings", BaseArmor.ArmorType.LIGHT, ModMaterials.UNDEAD, EntityEquipmentSlot.LEGS);
		RegisterHelper.registerItem(UndeadLeggings);		
		UndeadBoots = new UndeadArmor("UndeadBoots", BaseArmor.ArmorType.LIGHT, ModMaterials.UNDEAD, EntityEquipmentSlot.FEET);
		RegisterHelper.registerItem(UndeadBoots);
		
		StaticHelmet = new BaseArmor("StaticHelmet", BaseArmor.ArmorType.MEDIUM, ModMaterials.STATIC, EntityEquipmentSlot.HEAD);
		RegisterHelper.registerItem(StaticHelmet);	
		StaticChestplate = new BaseArmor("StaticChestplate", BaseArmor.ArmorType.MEDIUM, ModMaterials.STATIC, EntityEquipmentSlot.CHEST);
		RegisterHelper.registerItem(StaticChestplate);		
		StaticLeggings = new BaseArmor("StaticLeggings", BaseArmor.ArmorType.MEDIUM, ModMaterials.STATIC, EntityEquipmentSlot.LEGS);
		RegisterHelper.registerItem(StaticLeggings);		
		StaticBoots = new BaseArmor("StaticBoots", BaseArmor.ArmorType.MEDIUM, ModMaterials.STATIC, EntityEquipmentSlot.FEET);
		RegisterHelper.registerItem(StaticBoots);
		
		EnergizedHelmet = new BaseArmor("EnergizedHelmet", BaseArmor.ArmorType.MEDIUM, ModMaterials.ENERGIZED, EntityEquipmentSlot.HEAD);
		RegisterHelper.registerItem(EnergizedHelmet);		
		EnergizedChestplate = new BaseArmor("EnergizedChestplate", BaseArmor.ArmorType.MEDIUM, ModMaterials.ENERGIZED, EntityEquipmentSlot.CHEST);
		RegisterHelper.registerItem(EnergizedChestplate);		
		EnergizedLeggings = new BaseArmor("EnergizedLeggings", BaseArmor.ArmorType.MEDIUM, ModMaterials.ENERGIZED, EntityEquipmentSlot.LEGS);
		RegisterHelper.registerItem(EnergizedLeggings);		
		EnergizedBoots = new BaseArmor("EnergizedBoots", BaseArmor.ArmorType.MEDIUM, ModMaterials.ENERGIZED,EntityEquipmentSlot.FEET);
		RegisterHelper.registerItem(EnergizedBoots);
		
		LumumHelmet = new BaseArmor("LumumHelmet", BaseArmor.ArmorType.HEAVY, ModMaterials.LUMUM, EntityEquipmentSlot.HEAD);
		RegisterHelper.registerItem(LumumHelmet);		
		LumumChestplate = new BaseArmor("LumumChestplate", BaseArmor.ArmorType.HEAVY, ModMaterials.LUMUM, EntityEquipmentSlot.CHEST);
		RegisterHelper.registerItem(LumumChestplate);		
		LumumLeggings = new BaseArmor("LumumLeggings", BaseArmor.ArmorType.HEAVY, ModMaterials.LUMUM,EntityEquipmentSlot.LEGS);
		RegisterHelper.registerItem(LumumLeggings);		
		LumumBoots = new BaseArmor("LumumBoots", BaseArmor.ArmorType.HEAVY, ModMaterials.LUMUM, EntityEquipmentSlot.FEET);
		RegisterHelper.registerItem(LumumBoots);
		
		CopperHelmet = new CopperArmor("sCopperHelmet", BaseArmor.ArmorType.MEDIUM, ModMaterials.COPPER, EntityEquipmentSlot.HEAD);
		RegisterHelper.registerItem(CopperHelmet);		
		CopperChestplate = new CopperArmor("sCopperChestplate", BaseArmor.ArmorType.MEDIUM, ModMaterials.COPPER, EntityEquipmentSlot.CHEST);
		RegisterHelper.registerItem(CopperChestplate);		
		CopperLeggings = new CopperArmor("sCopperLeggings", BaseArmor.ArmorType.MEDIUM, ModMaterials.COPPER, EntityEquipmentSlot.LEGS);
		RegisterHelper.registerItem(CopperLeggings);		
		CopperBoots = new CopperArmor("sCopperBoots", BaseArmor.ArmorType.MEDIUM, ModMaterials.COPPER, EntityEquipmentSlot.FEET);
		RegisterHelper.registerItem(CopperBoots);
		
		TinHelmet = new TinArmor("sTinHelmet", BaseArmor.ArmorType.LIGHT, ModMaterials.TIN,  EntityEquipmentSlot.HEAD);
		RegisterHelper.registerItem(TinHelmet);		
		TinChestplate = new TinArmor("sTinChestplate", BaseArmor.ArmorType.LIGHT, ModMaterials.TIN, EntityEquipmentSlot.CHEST);
		RegisterHelper.registerItem(TinChestplate);		
		TinLeggings = new TinArmor("sTinLeggings", BaseArmor.ArmorType.LIGHT, ModMaterials.TIN, EntityEquipmentSlot.LEGS);
		RegisterHelper.registerItem(TinLeggings);		
		TinBoots = new TinArmor("sTinBoots", BaseArmor.ArmorType.LIGHT, ModMaterials.TIN, EntityEquipmentSlot.FEET);
		RegisterHelper.registerItem(TinBoots);
		
		LeadHelmet = new LeadArmor("sLeadHelmet", BaseArmor.ArmorType.HEAVY, ModMaterials.LEAD, EntityEquipmentSlot.HEAD);
		RegisterHelper.registerItem(LeadHelmet);		
		LeadChestplate = new LeadArmor("sLeadChestplate", BaseArmor.ArmorType.HEAVY, ModMaterials.LEAD, EntityEquipmentSlot.CHEST);
		RegisterHelper.registerItem(LeadChestplate);		
		LeadLeggings = new LeadArmor("sLeadLeggings", BaseArmor.ArmorType.HEAVY, ModMaterials.LEAD, EntityEquipmentSlot.LEGS);
		RegisterHelper.registerItem(LeadLeggings);		
		LeadBoots = new LeadArmor("sLeadBoots", BaseArmor.ArmorType.HEAVY, ModMaterials.LEAD, EntityEquipmentSlot.FEET);
		RegisterHelper.registerItem(LeadBoots);
		
		SilverHelmet = new SilverArmor("sSilverHelmet", BaseArmor.ArmorType.MEDIUM, ModMaterials.SILVER, EntityEquipmentSlot.HEAD);
		RegisterHelper.registerItem(SilverHelmet);		
		SilverChestplate = new SilverArmor("sSilverChestplate", BaseArmor.ArmorType.MEDIUM, ModMaterials.SILVER, EntityEquipmentSlot.CHEST);
		RegisterHelper.registerItem(SilverChestplate);		
		SilverLeggings = new SilverArmor("sSilverLeggings", BaseArmor.ArmorType.MEDIUM, ModMaterials.SILVER, EntityEquipmentSlot.LEGS);
		RegisterHelper.registerItem(SilverLeggings);		
		SilverBoots = new SilverArmor("sSilverBoots", BaseArmor.ArmorType.MEDIUM, ModMaterials.SILVER,  EntityEquipmentSlot.FEET);
		RegisterHelper.registerItem(SilverBoots);
		
		PlatinumHelmet = new PlatinumArmor("sPlatinumHelmet", BaseArmor.ArmorType.HEAVY, ModMaterials.PLATINUM, EntityEquipmentSlot.HEAD);
		RegisterHelper.registerItem(PlatinumHelmet);		
		PlatinumChestplate = new PlatinumArmor("sPlatinumChestplate", BaseArmor.ArmorType.HEAVY, ModMaterials.PLATINUM, EntityEquipmentSlot.CHEST);
		RegisterHelper.registerItem(PlatinumChestplate);		
		PlatinumLeggings = new PlatinumArmor("sPlatinumLeggings", BaseArmor.ArmorType.HEAVY, ModMaterials.PLATINUM,  EntityEquipmentSlot.LEGS);
		RegisterHelper.registerItem(PlatinumLeggings);		
		PlatinumBoots = new PlatinumArmor("sPlatinumBoots", BaseArmor.ArmorType.HEAVY, ModMaterials.PLATINUM, EntityEquipmentSlot.FEET);
		RegisterHelper.registerItem(PlatinumBoots);
		
		AluminiumHelmet = new AluminiumArmor("sAluminiumHelmet", BaseArmor.ArmorType.LIGHT, ModMaterials.ALUMINIUM, EntityEquipmentSlot.HEAD);
		RegisterHelper.registerItem(AluminiumHelmet);		
		AluminiumChestplate = new AluminiumArmor("sAluminiumChestplate", BaseArmor.ArmorType.LIGHT, ModMaterials.ALUMINIUM, EntityEquipmentSlot.CHEST);
		RegisterHelper.registerItem(AluminiumChestplate);		
		AluminiumLeggings = new AluminiumArmor("sAluminiumLeggings", BaseArmor.ArmorType.LIGHT, ModMaterials.ALUMINIUM, EntityEquipmentSlot.LEGS);
		RegisterHelper.registerItem(AluminiumLeggings);		
		AluminiumBoots = new AluminiumArmor("sAluminiumBoots", BaseArmor.ArmorType.LIGHT, ModMaterials.ALUMINIUM, EntityEquipmentSlot.FEET);
		RegisterHelper.registerItem(AluminiumBoots);
		
		SapphireHelmet = new SapphireArmor("sSapphireHelmet", BaseArmor.ArmorType.MEDIUM, ModMaterials.SAPPHIRE, EntityEquipmentSlot.HEAD);
		RegisterHelper.registerItem(SapphireHelmet);		
		SapphireChestplate = new SapphireArmor("sSapphireChestplate", BaseArmor.ArmorType.MEDIUM, ModMaterials.SAPPHIRE,  EntityEquipmentSlot.CHEST);
		RegisterHelper.registerItem(SapphireChestplate);		
		SapphireLeggings = new SapphireArmor("sSapphireLeggings", BaseArmor.ArmorType.MEDIUM, ModMaterials.SAPPHIRE, EntityEquipmentSlot.LEGS);
		RegisterHelper.registerItem(SapphireLeggings);		
		SapphireBoots = new SapphireArmor("sSapphireBoots", BaseArmor.ArmorType.MEDIUM, ModMaterials.SAPPHIRE, EntityEquipmentSlot.FEET);
		RegisterHelper.registerItem(SapphireBoots);
		
		RubyHelmet = new RubyArmor("sRubyHelmet", BaseArmor.ArmorType.MEDIUM, ModMaterials.RUBY, EntityEquipmentSlot.HEAD);
		RegisterHelper.registerItem(RubyHelmet);		
		RubyChestplate = new RubyArmor("sRubyChestplate", BaseArmor.ArmorType.MEDIUM, ModMaterials.RUBY, EntityEquipmentSlot.CHEST);
		RegisterHelper.registerItem(RubyChestplate);		
		RubyLeggings = new RubyArmor("sRubyLeggings", BaseArmor.ArmorType.MEDIUM, ModMaterials.RUBY, EntityEquipmentSlot.LEGS);
		RegisterHelper.registerItem(RubyLeggings);		
		RubyBoots = new RubyArmor("sRubyBoots", BaseArmor.ArmorType.MEDIUM, ModMaterials.RUBY, EntityEquipmentSlot.FEET);
		RegisterHelper.registerItem(RubyBoots);
		
	}
}
