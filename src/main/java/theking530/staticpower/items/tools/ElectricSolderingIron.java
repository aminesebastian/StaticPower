package theking530.staticpower.items.tools;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import theking530.staticpower.items.BasePoweredItem;
import theking530.staticpower.utils.EnumTextFormatting;

public class ElectricSolderingIron extends BasePoweredItem implements ISolderingIron{
	
	public ElectricSolderingIron(String name, int maxUses) {
		super(name, maxUses*100, maxUses);
	}
	@Override  
		public void addInformation(ItemStack itemstack, @Nullable World worldIn, List<String> list, ITooltipFlag flagIn) {
		list.add("Power Stored: " +  NumberFormat.getNumberInstance(Locale.US).format(getEnergyStored(itemstack)) +"/"
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
	public boolean useSolderingItem(ItemStack itemstack) {
		if(this.getEnergyStored(itemstack) >= 100) {
			extractEnergy(itemstack, 100, false);			
		}
		return false;
	}
	@Override
	public boolean canSolder(ItemStack itemstack) {
		return getEnergyStored(itemstack) >= 100;
	}
}
