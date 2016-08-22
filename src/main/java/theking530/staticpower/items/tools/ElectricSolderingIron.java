package theking530.staticpower.items.tools;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import org.lwjgl.input.Keyboard;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import theking530.staticpower.StaticPower;
import theking530.staticpower.items.BasePoweredItem;
import theking530.staticpower.utils.EnumTextFormatting;

public class ElectricSolderingIron extends BasePoweredItem implements ISolderingIron{
	
	public ElectricSolderingIron(String name, int maxUses) {
		super(name, maxUses*100, maxUses);
	}
	@Override  
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean par4) {
		list.add("Power Stored: " +  NumberFormat.getNumberInstance(Locale.US).format(getEnergyStored(itemstack)) +"RF/"
				+  NumberFormat.getNumberInstance(Locale.US).format(capacity) + "RF");
    	if(showHiddenTooltips()) {
    		list.add("Power per Operation: 100RF");
    	}else{
    		list.add(EnumTextFormatting.ITALIC + "Hold Shift");
	    }
	}
	public boolean showHiddenTooltips() {
		return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
	}
	@Override
	public void useSolderingItem(ItemStack itemstack) {
		extractEnergy(itemstack, 100, false);
	}
	@Override
	public boolean canSolder(ItemStack itemstack) {
		return getEnergyStored(itemstack) >= 100;
	}
}
