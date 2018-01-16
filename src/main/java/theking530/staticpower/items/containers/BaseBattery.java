package theking530.staticpower.items.containers;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import theking530.staticpower.items.BasePoweredItem;

public class BaseBattery extends BasePoweredItem { 

	public BaseBattery(String name, int capacity, int powerDivisor) {
		super(name, capacity, powerDivisor);
		setMaxStackSize(1);
	}
    @SideOnly(Side.CLIENT)
    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {

    	ItemStack emptyBattery = new ItemStack(this); 
    	emptyBattery.setItemDamage(getMaxEnergyStored(emptyBattery)/DAMAGE_DIVISOR);
    	items.add(emptyBattery);
        
        ItemStack filledBattery = new ItemStack(this);     
        this.receiveEnergy(filledBattery, this.getMaxEnergyStored(filledBattery), false);
        items.add(filledBattery);
    }
	
	@Override  
		public void addInformation(ItemStack itemstack, @Nullable World worldIn, List<String> list, ITooltipFlag flagIn) {
		list.add("Power Stored: " +  NumberFormat.getNumberInstance(Locale.US).format(getEnergyStored(itemstack)) +"/"
				+  NumberFormat.getNumberInstance(Locale.US).format(capacity) + "RF");
	}
}
