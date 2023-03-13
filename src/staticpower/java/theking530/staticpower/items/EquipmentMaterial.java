package theking530.staticpower.items;

import net.minecraft.ChatFormatting;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Tier;

public class EquipmentMaterial {

	private String name;
	private ChatFormatting color;
	private ArmorMaterial armorMaterial;
	private Tier toolMaterial;
	private EquipmentMaterial parentMaterial;

	public EquipmentMaterial(String name, ChatFormatting color, EquipmentMaterial parentMaterial, boolean validArmor, boolean validTool) {
		this.name = name;
		this.color = color;
		this.parentMaterial = parentMaterial;
	}
	
	public EquipmentMaterial getParentMaterial() {
		if(parentMaterial != null) {
			return getParentMaterialWorker(parentMaterial);
		}else{
			return this;
		}
	}
	private EquipmentMaterial getParentMaterialWorker(EquipmentMaterial parentMaterial) {
		if(parentMaterial.parentMaterial != null) {
			return getParentMaterialWorker(parentMaterial);
		}else{
			return parentMaterial;
		}
	}
	public void initArmorMaterial(ArmorMaterial material){
		armorMaterial = material;
	}
	public void initToolMaterial(Tier material) {
		toolMaterial = material;
	}
	
	public ArmorMaterial getArmorMaterial() {
		return armorMaterial;
	}
	public boolean isValidArmorMaterial() {
		return getArmorMaterial() != null;
	}
	
	public Tier getToolMaterial() {
		return toolMaterial;
	}
	public boolean isValidToolMaterial() {
		return getToolMaterial() != null;
	}

	public String toColorString() {
		return color + name;
	}
}
