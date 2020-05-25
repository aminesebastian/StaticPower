package theking530.staticpower.items;

import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.IItemTier;
import net.minecraft.util.text.TextFormatting;

public class EquipmentMaterial {

	private String name;
	private TextFormatting color;
	private IArmorMaterial armorMaterial;
	private IItemTier toolMaterial;
	private EquipmentMaterial parentMaterial;

	public EquipmentMaterial(String name, TextFormatting color, EquipmentMaterial parentMaterial, boolean validArmor, boolean validTool) {
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
	public void initArmorMaterial(IArmorMaterial material){
		armorMaterial = material;
	}
	public void initToolMaterial(IItemTier material) {
		toolMaterial = material;
	}
	
	public IArmorMaterial getArmorMaterial() {
		return armorMaterial;
	}
	public boolean isValidArmorMaterial() {
		return getArmorMaterial() != null;
	}
	
	public IItemTier getToolMaterial() {
		return toolMaterial;
	}
	public boolean isValidToolMaterial() {
		return getToolMaterial() != null;
	}

	public String toColorString() {
		return color + name;
	}
}
