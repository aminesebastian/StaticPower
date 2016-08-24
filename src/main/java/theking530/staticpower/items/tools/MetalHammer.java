package theking530.staticpower.items.tools;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import theking530.staticpower.items.ItemBase;
import theking530.staticpower.utils.EnumTextFormatting;

public class MetalHammer extends ItemBase {

	public MetalHammer() {
		super("MetalHammer");
		setMaxDamage(35);
		setMaxStackSize(1);
	}
	@Override
	public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
		if(repair.getItem() == Items.IRON_INGOT) {
			return true;
		}
		return false;
	}
	@Override
	public ItemStack getContainerItem(ItemStack itemStack) {
	    itemStack.setItemDamage(itemStack.getItemDamage() + 1);
	    return itemStack;     	
	}
	@Override
	public boolean hasContainerItem(){
		return true;
	}
	@Override  
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean par4) {
	    	if(showHiddenTooltips()) {
	    		list.add("Max Uses: " + getMaxDamage());
	    		list.add("Uses Remaining: " + (getMaxDamage() - getDamage(itemstack)));
	    	}else{
	    		list.add(EnumTextFormatting.ITALIC + "Hold Shift");
	    }
	}
}
