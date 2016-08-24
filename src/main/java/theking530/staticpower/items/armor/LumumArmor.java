package theking530.staticpower.items.armor;

import theking530.staticpower.StaticPower;
import theking530.staticpower.assists.Reference;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.ISpecialArmor;

public class LumumArmor extends ItemArmor {
	public String textureName;
	
	public LumumArmor(String unlocalizedName, ArmorMaterial material, String textureName, EntityEquipmentSlot type) {
	    super(material, 0, type);
	    this.textureName = textureName;
	    this.setUnlocalizedName(unlocalizedName);
		setRegistryName(unlocalizedName);	
	    this.setCreativeTab(StaticPower.StaticPower);
	}	
}
