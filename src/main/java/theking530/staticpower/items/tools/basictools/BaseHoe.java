package theking530.staticpower.items.tools.basictools;

import java.util.List;

import org.lwjgl.input.Keyboard;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import theking530.staticpower.StaticPower;
import theking530.staticpower.utils.EnumTextFormatting;


public class BaseHoe extends ItemHoe {

	public String NAME = "";
	public ToolMaterial MATERIAL;
	
	public BaseHoe(ToolMaterial material, String unlocalizedName) {
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
    		String tempLevel = "Mining Level: " +  MATERIAL.getHarvestLevel();
    		String tempSpeed = "Speed: " +  MATERIAL.getEfficiencyOnProperMaterial();
    		String tempDamage = "Damage: " + 1; 
    		String tempDurability = "Durability: " + (MATERIAL.getMaxUses()-itemstack.getMetadata()) + "/" + MATERIAL.getMaxUses();
    		
    		list.add(tempLevel);
    		list.add(tempSpeed);
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