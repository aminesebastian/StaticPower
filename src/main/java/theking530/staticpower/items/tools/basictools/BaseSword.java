package theking530.staticpower.items.tools.basictools;

import java.util.Collection;
import java.util.List;

import org.lwjgl.input.Keyboard;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import theking530.staticpower.StaticPower;
import theking530.staticpower.utils.EnumTextFormatting;


public class BaseSword extends ItemSword {

	public String NAME = "";
	public ToolMaterial MATERIAL;
	
	public BaseSword(ToolMaterial material, String unlocalizedName) {
		super(material);
		NAME= unlocalizedName;
		setUnlocalizedName(unlocalizedName);
		setRegistryName(unlocalizedName);
		setCreativeTab(StaticPower.StaticPower);
		MATERIAL = material;
	}
	
	@Override  
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean par4) {
    	if(showHiddenTooltips()) {
    		Collection<AttributeModifier> temp = getAttributeModifiers(EntityEquipmentSlot.MAINHAND, itemstack).get(SharedMonsterAttributes.ATTACK_SPEED.getAttributeUnlocalizedName());
    		
    		//String tempSpeed = "Attack Speed: " +  temp.toArray()[0];
    		String tempDamage = "Damage: " + (Math.round(getDamageVsEntity()) + 4); 
    		String tempDurability = "Durability: " + (MATERIAL.getMaxUses()-itemstack.getMetadata()) + "/" + MATERIAL.getMaxUses();
    		
    		
    		//list.add(tempSpeed);
    		list.add(tempDurability);
    		list.add(tempDamage);
    	}else{
    		list.add(EnumTextFormatting.ITALIC + "Hold Shift");
	    }
	}
	public boolean showHiddenTooltips() {
		return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
	}
}