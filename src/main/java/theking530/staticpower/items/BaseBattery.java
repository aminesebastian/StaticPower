package theking530.staticpower.items;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import cofh.api.energy.ItemEnergyContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import theking530.staticpower.StaticPower;
import theking530.staticpower.assists.Tier;
import theking530.staticpower.utils.EnumTextFormatting;

public class BaseBattery extends BasePoweredItem {

	public BaseBattery(String name, int capacity, int powerDivisor) {
		super(name, capacity, powerDivisor);
	}
	@Override  
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean par4) {
		list.add("Power Stored: " +  NumberFormat.getNumberInstance(Locale.US).format(getEnergyStored(itemstack)) +"RF/"
				+  NumberFormat.getNumberInstance(Locale.US).format(capacity) + "RF");
	}
}
