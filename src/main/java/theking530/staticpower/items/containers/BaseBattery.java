package theking530.staticpower.items.containers;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import cofh.api.energy.ItemEnergyContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import theking530.staticpower.StaticPower;
import theking530.staticpower.assists.Tier;
import theking530.staticpower.items.BasePoweredItem;
import theking530.staticpower.utils.EnumTextFormatting;
import theking530.staticpower.utils.GUIUtilities;

public class BaseBattery extends BasePoweredItem { //implements IItemColor {

	public BaseBattery(String name, int capacity, int powerDivisor) {
		super(name, capacity, powerDivisor);
		setMaxStackSize(1);
	}
	@Override  
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean par4) {
		list.add("Power Stored: " +  NumberFormat.getNumberInstance(Locale.US).format(getEnergyStored(itemstack)) +"RF/"
				+  NumberFormat.getNumberInstance(Locale.US).format(capacity) + "RF");
	}
	/**
	@Override
	public int getColorFromItemstack(ItemStack stack, int tintIndex) {
		BaseBattery tempBat = (BaseBattery) stack.getItem();
		if(tintIndex == 1) {
			int color = (int) (255*((float)tempBat.getEnergyStored(stack)/(float)tempBat.capacity));
			return GUIUtilities.getColor(color, color, color);
		}
		return -1;
	}
	**/
}
