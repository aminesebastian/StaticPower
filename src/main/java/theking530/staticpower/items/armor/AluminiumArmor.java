package theking530.staticpower.items.armor;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.potion.PotionEffect;
import theking530.staticpower.items.EquipmentMaterial;
import theking530.staticpower.utils.EnumTextFormatting;

public class AluminiumArmor extends BaseArmor {

	public AluminiumArmor(String name, ArmorType type, EquipmentMaterial materialIn, EntityEquipmentSlot equipmentSlotIn) {
		super(name, type, materialIn, equipmentSlotIn);
	}
	
	@Override
	public List getSetInfo() {
		List tempList = new ArrayList();
		tempList.add("Weak: " + EnumTextFormatting.RED + "-4 Attack");
		tempList.add("Quick: " + EnumTextFormatting.DARK_AQUA + "+20% Speed");
		return tempList;
	}
	@Override
	public String getTagline() {
		return "Run Like the Wind";
	}
	@Override
	public PotionEffect[] getEffects() {
		return new PotionEffect[]{new PotionEffect(MobEffects.SPEED, 2, 0), new PotionEffect(MobEffects.WEAKNESS, 4, 0)};
	}
}
