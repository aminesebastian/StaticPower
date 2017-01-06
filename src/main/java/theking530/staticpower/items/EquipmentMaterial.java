package theking530.staticpower.items;

import java.util.List;

import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.TextFormatting;
import theking530.staticpower.utils.EnumTextFormatting;

public class EquipmentMaterial {

	private String NAME;
	private TextFormatting COLOR;
	private ArmorMaterial ARMOR_MATERIAL;
	private ToolMaterial TOOL_MATERIAL;
	
	public EquipmentMaterial(String name, TextFormatting color) {
		NAME = name;
		COLOR = color;
	}
	
	public void initArmorMaterial(ArmorMaterial material){
		ARMOR_MATERIAL = material;
	}
	public void initToolMaterial(ToolMaterial material) {
		TOOL_MATERIAL = material;
	}
	public ArmorMaterial getArmorMaterial() {
		return ARMOR_MATERIAL;
	}
	public ToolMaterial getToolMaterial() {
		return TOOL_MATERIAL;
	}
	public String toColorString() {
		return COLOR + NAME;
	}

}
