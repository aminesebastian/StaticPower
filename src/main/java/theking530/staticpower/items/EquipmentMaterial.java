package theking530.staticpower.items;

import java.util.List;

import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.TextFormatting;
import theking530.staticpower.items.armor.BaseArmor;
import theking530.staticpower.items.armor.BaseArmor.ArmorType;
import theking530.staticpower.utils.EnumTextFormatting;

public class EquipmentMaterial {

	private String NAME;
	private TextFormatting COLOR;
	private ArmorMaterial ARMOR_MATERIAL;
	private ToolMaterial TOOL_MATERIAL;
	private EquipmentMaterial PARENT_MATERIAL;
	
	private boolean IS_VALID_TOOL;
	private boolean IS_VALID_ARMOR;
	
	public EquipmentMaterial(String name, TextFormatting color, EquipmentMaterial parentMaterial, boolean validArmor, boolean validTool) {
		NAME = name;
		COLOR = color;
		PARENT_MATERIAL = parentMaterial;
		IS_VALID_ARMOR = validArmor;
		IS_VALID_TOOL = validTool;
	}
	
	public EquipmentMaterial getParentMaterial() {
		if(PARENT_MATERIAL != null) {
			return getParentMaterialWorker(PARENT_MATERIAL);
		}else{
			return this;
		}
	}
	private EquipmentMaterial getParentMaterialWorker(EquipmentMaterial parentMaterial) {
		if(parentMaterial.PARENT_MATERIAL != null) {
			return getParentMaterialWorker(parentMaterial);
		}else{
			return parentMaterial;
		}
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
	public boolean isValidArmorMaterial() {
		return getArmorMaterial() != null;
	}
	
	public ToolMaterial getToolMaterial() {
		return TOOL_MATERIAL;
	}
	public boolean isValidToolMaterial() {
		return getToolMaterial() != null;
	}

	public String toColorString() {
		return COLOR + NAME;
	}
}
