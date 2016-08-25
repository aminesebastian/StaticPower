package theking530.staticpower.items.armor;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import cofh.api.energy.IEnergyContainerItem;
import cofh.api.energy.ItemEnergyContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import theking530.staticpower.StaticPower;

public class BasePoweredArmor extends ItemArmor implements IEnergyContainerItem {

	protected int capacity;
	protected int maxReceive;
	protected int maxExtract;
	public int DAMAGE_DIVISOR;
	
	public BasePoweredArmor(String name, ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn, int capacity) {
		super(materialIn, renderIndexIn, equipmentSlotIn);
		this.capacity = capacity;
		this.maxReceive = capacity;
		this.maxExtract = capacity;
		DAMAGE_DIVISOR = capacity/100;
		setCreativeTab(StaticPower.StaticPower);
		setUnlocalizedName(name);
		setRegistryName(name);
		setMaxStackSize(1);
		setMaxDamage(capacity/DAMAGE_DIVISOR);
		setNoRepair();
	}
	public void setEnergy(ItemStack container, int energy) {
		if (!container.hasTagCompound()) {
			container.setTagCompound(new NBTTagCompound());
		}
		container.getTagCompound().setInteger("Energy", energy);
		updateDamage(container);
	}
	/* IEnergyContainerItem */
	@Override
	public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {

		if (!container.hasTagCompound()) {
			container.setTagCompound(new NBTTagCompound());
		}
		int energy = container.getTagCompound().getInteger("Energy");
		int energyReceived = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));

		if (!simulate) {
			energy += energyReceived;
			container.getTagCompound().setInteger("Energy", energy);
		}
		updateDamage(container);
		return energyReceived;
	}

	@Override
	public int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {
		if (container.getTagCompound() == null || !container.getTagCompound().hasKey("Energy")) {
			return 0;
		}
		int energy = container.getTagCompound().getInteger("Energy");
		int energyExtracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));

		if (!simulate) {
			energy -= energyExtracted;
			container.getTagCompound().setInteger("Energy", energy);
		}
		updateDamage(container);
		return energyExtracted;
	}

	@Override
	public int getEnergyStored(ItemStack container) {
		if (container.getTagCompound() == null || !container.getTagCompound().hasKey("Energy")) {
			return 0;
		}
		return container.getTagCompound().getInteger("Energy");
	}

	@Override
	public int getMaxEnergyStored(ItemStack container) {
		return capacity;
	}
	public void updateDamage(ItemStack stack) {
		setDamage(stack, (capacity - getEnergyStored(stack))/DAMAGE_DIVISOR);
	}
	@Override
    public void setDamage(ItemStack stack, int damage){
		setEnergy(stack, capacity*(damage/stack.getMaxDamage()));
		if(damage >= stack.getMaxDamage()) {
			stack.setItemDamage(stack.getMaxDamage() - 1);
		}else{
	        stack.setItemDamage(damage);		
		}

        if (stack.getItemDamage() < 0){
            stack.setItemDamage(0);
        }
    }
	@Override  
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean par4) {
		list.add("Power Stored: " +  NumberFormat.getNumberInstance(Locale.US).format(getEnergyStored(itemstack)) +"RF/"
				+  NumberFormat.getNumberInstance(Locale.US).format(capacity) + "RF");
	}
}
