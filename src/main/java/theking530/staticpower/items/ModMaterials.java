package theking530.staticpower.items;

import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.util.EnumHelper;
import theking530.staticpower.assists.Reference;
import theking530.staticpower.utils.EnumTextFormatting;

public class ModMaterials {

	public static EquipmentMaterial STATIC;
	public static EquipmentMaterial ENERGIZED;
	public static EquipmentMaterial LUMUM;
	
	public static EquipmentMaterial COPPER;
	public static EquipmentMaterial TIN;
	public static EquipmentMaterial SILVER;
	public static EquipmentMaterial LEAD;
	public static EquipmentMaterial PLATINUM;
	public static EquipmentMaterial ALUMINIUM;
	public static EquipmentMaterial SAPPHIRE;
	public static EquipmentMaterial RUBY;
	
	public static void init() {
		STATIC = new EquipmentMaterial("Static", EnumTextFormatting.GREEN);
		ENERGIZED = new EquipmentMaterial("Energized", EnumTextFormatting.AQUA);
		LUMUM = new EquipmentMaterial("Lumum", EnumTextFormatting.YELLOW);
		
		COPPER = new EquipmentMaterial("Copper", EnumTextFormatting.GOLD);
		TIN = new EquipmentMaterial("Tin", EnumTextFormatting.GRAY);
		SILVER = new EquipmentMaterial("Silver", EnumTextFormatting.WHITE);
		LEAD = new EquipmentMaterial("Lead", EnumTextFormatting.DARK_PURPLE);
		PLATINUM = new EquipmentMaterial("Platinum", EnumTextFormatting.DARK_AQUA);
		ALUMINIUM = new EquipmentMaterial("Aluminium", EnumTextFormatting.WHITE);
		SAPPHIRE = new EquipmentMaterial("Sapphire", EnumTextFormatting.BLUE);
		RUBY = new EquipmentMaterial("Ruby", EnumTextFormatting.RED);
	
		initArmorValues();
		initToolValues();
	}
	private static void initArmorValues() {
		STATIC.initArmorMaterial(EnumHelper.addArmorMaterial("StaticMaterial", Reference.MODID + ":StaticArmor", 28, new int[] {3, 6, 6, 3}, 25, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 1.0F));
		ENERGIZED.initArmorMaterial(EnumHelper.addArmorMaterial("EnergizedMaterial", Reference.MODID + ":EnergizedArmor", 35, new int[] {5, 10, 8, 7}, 35, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 2.0F));
		LUMUM.initArmorMaterial(EnumHelper.addArmorMaterial("LumumMaterial", Reference.MODID + ":LumumArmor", 35, new int[] {8, 13, 11, 10}, 35, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 4.0F));
		
		COPPER.initArmorMaterial(EnumHelper.addArmorMaterial("CopperMaterial", Reference.MODID + ":CopperArmor", 28, new int[] {1, 2, 2, 1}, 25, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 0.5F));
		TIN.initArmorMaterial(EnumHelper.addArmorMaterial("TinMaterial", Reference.MODID + ":TinArmor", 28, new int[] {2, 2, 2, 1}, 25, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 0.0F));
		SILVER.initArmorMaterial(EnumHelper.addArmorMaterial("SilverMaterial", Reference.MODID + ":SilverArmor", 28, new int[] {1, 3, 2, 1}, 25, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 0.5F));
		LEAD.initArmorMaterial(EnumHelper.addArmorMaterial("LeadMaterial", Reference.MODID + ":LeadArmor", 28, new int[] {3, 4, 4, 3}, 25, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 1.0F));
		PLATINUM.initArmorMaterial(EnumHelper.addArmorMaterial("PlatinumMaterial", Reference.MODID + ":PlatinumArmor", 28, new int[] {3, 5, 4, 3}, 25, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 1.0F));
		ALUMINIUM.initArmorMaterial(EnumHelper.addArmorMaterial("AluminiumMaterial", Reference.MODID + ":AluminiumArmor", 28, new int[] {1, 2, 1, 1}, 35, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 0.0F));
		RUBY.initArmorMaterial(EnumHelper.addArmorMaterial("RubyMaterial", Reference.MODID + ":RubyArmor", 28, new int[] {5, 7, 6, 4}, 25, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 2.0F));
		SAPPHIRE.initArmorMaterial(EnumHelper.addArmorMaterial("SapphireMaterial", Reference.MODID + ":SapphireArmor", 28, new int[] {5, 7, 6, 4}, 35, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 2.0F));
	}
	private static void initToolValues() {
		STATIC.initToolMaterial(EnumHelper.addToolMaterial("STATIC", 2, 1500, 7.0F, 3.0F, 20));
		ENERGIZED.initToolMaterial(EnumHelper.addToolMaterial("ENERGIZED", 2, 2500, 4.0F, 4.0F, 30));
		LUMUM.initToolMaterial(EnumHelper.addToolMaterial("LUMUM", 3, 4000, 12.0F, 5.0F, 40));
		
		COPPER.initToolMaterial(EnumHelper.addToolMaterial("COPPER", 1, 500, 5.0F, 3.0F, 15));
		TIN.initToolMaterial(EnumHelper.addToolMaterial("TIN", 1, 500, 5.0F, 3.0F, 15));
		SILVER.initToolMaterial(EnumHelper.addToolMaterial("SILVER", 2, 1000, 6.0F, 3.0F, 20));
		LEAD.initToolMaterial(EnumHelper.addToolMaterial("LEAD", 2, 1500, 3.0F, 3.0F, 20));
		PLATINUM.initToolMaterial(EnumHelper.addToolMaterial("PLATINUM", 3, 2000, 4.0F, 5.0F, 30));
		ALUMINIUM.initToolMaterial(EnumHelper.addToolMaterial("ALUMINIUM", 1, 500, 3.0F, 3.0F, 15));
		RUBY.initToolMaterial(EnumHelper.addToolMaterial("RUBY", 2, 1500, 5.0F, 7.0F, 30));
		SAPPHIRE.initToolMaterial(EnumHelper.addToolMaterial("SAPPHIRE", 2, 1500, 7.0F, 5.0F, 30));
	}
	public static TextFormatting getMaterialColor(EquipmentMaterial material) {
		return EnumTextFormatting.WHITE;
	}
	public static String getMaterialAsString(EquipmentMaterial material) {
		return "Text";
	}
}
