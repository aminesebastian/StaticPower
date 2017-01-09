package theking530.staticpower.items.containers;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import cofh.api.energy.IEnergyContainerItem;
import cofh.api.energy.ItemEnergyContainer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import theking530.staticpower.StaticPower;
import theking530.staticpower.assists.Tier;
import theking530.staticpower.items.BasePoweredItem;
import theking530.staticpower.utils.EnumTextFormatting;
import theking530.staticpower.utils.GUIUtilities;

public class BaseBattery extends BasePoweredItem { 

	public BaseBattery(String name, int capacity, int powerDivisor) {
		super(name, capacity, powerDivisor);
		setMaxStackSize(1);
	}
    @SideOnly(Side.CLIENT)
    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {

    	ItemStack emptyBattery = new ItemStack(this); 
    	emptyBattery.setItemDamage(getMaxEnergyStored(emptyBattery)/DAMAGE_DIVISOR);
        subItems.add(emptyBattery);
        
        ItemStack filledBattery = new ItemStack(this);     
        this.receiveEnergy(filledBattery, this.getMaxEnergyStored(filledBattery), false);
        subItems.add(filledBattery);
    }
	
	@Override  
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean par4) {
		list.add("Power Stored: " +  NumberFormat.getNumberInstance(Locale.US).format(getEnergyStored(itemstack)) +"/"
				+  NumberFormat.getNumberInstance(Locale.US).format(capacity) + "RF");
	}
}
