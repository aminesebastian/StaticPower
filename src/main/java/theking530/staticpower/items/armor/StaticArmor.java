package theking530.staticpower.items.armor;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import theking530.staticpower.StaticPower;

public class StaticArmor extends ItemArmor {

	public String textureName;
	
	public StaticArmor(String unlocalizedName, ArmorMaterial material, String textureName, EntityEquipmentSlot type) {
	    super(material, 0, type);
	    this.textureName = textureName;
		setCreativeTab(StaticPower.StaticPower);
		setUnlocalizedName(unlocalizedName);
		setRegistryName(unlocalizedName);
		setMaxStackSize(1);
		setNoRepair();
	}
}