package theking530.staticpower.machines;

import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import theking530.staticpower.items.upgrades.BaseTankUpgrade;

public class BaseMachineWithTank extends BaseMachine implements IFluidHandler{

	public int INITIAL_TANK_CAPACITY;
	public FluidTank TANK;

	public void initializeBaseMachineWithTank(int InitialEnergyMult, int InitialPowerUse, int InitialEnergyCapacity, int InitialEntryPerTick, int InitialProcessingTime, 
			int slotCount, int[] inputSlots, int[] outputSlots, int[] upgradeSlots, int InitialTankCapacity) {	
		initializeBasicMachine(InitialEnergyMult, InitialPowerUse, InitialEnergyCapacity, InitialEntryPerTick, InitialProcessingTime, slotCount, inputSlots, outputSlots, upgradeSlots);
		INITIAL_TANK_CAPACITY = InitialTankCapacity;
		TANK = new FluidTank(INITIAL_TANK_CAPACITY);
	}
	
	@Override
	public void upgradeHandler(){
		powerUpgrade(UPGRADE_SLOTS);	
		tankUpgrade(UPGRADE_SLOTS);
		processingUpgrade(UPGRADE_SLOTS);
	}
	public void tankUpgrade(int[] upgradeSlots) {
		boolean flag = false;
		int slot = 0;
		for(int i=0; i<3; i++) {
			if(slots[upgradeSlots[i]] != null) {
				if(slots[upgradeSlots[i]].getItem() instanceof BaseTankUpgrade) {
					flag = true;
					slot = i;
				}
			}
		}
		if(flag) {
			BaseTankUpgrade tempUpgrade = (BaseTankUpgrade) slots[upgradeSlots[slot]].getItem();
			TANK.setCapacity((int)(INITIAL_TANK_CAPACITY*tempUpgrade.CAPACITY));
		}else{
			TANK.setCapacity(INITIAL_TANK_CAPACITY);
		}
	}
	
	public NBTTagCompound onMachineBroken() {
		NBTTagCompound nbt = new NBTTagCompound();
    	return writeToNBT(nbt);
	}
	public void onMachinePlaced(NBTTagCompound nbt) {
		readFromNBT(nbt);
	}
    @Override  
	public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        TANK.readFromNBT(nbt);
    }		
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
		TANK.writeToNBT(nbt);
    	return nbt;
	}
		
	public boolean isTankEmpty() {
		return TANK.getFluidAmount() <= 0 ? true : false;
	}
	public boolean tankHasSpace() {
		return TANK.getFluidAmount() < TANK.getCapacity();
	}
	@Override
	public IFluidTankProperties[] getTankProperties() {
		return TANK.getTankProperties();
	}

	@Override
	public int fill(FluidStack resource, boolean doFill) {
		return TANK.fill(resource, doFill);
	}
	@Override
	public FluidStack drain(FluidStack resource, boolean doDrain) {
		return TANK.drain(resource, doDrain);
	}
	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		return TANK.drain(maxDrain, doDrain);
	}
	public IFluidTank getFluidTank() {
		return TANK;
	}
	public float getFluidLevelScaled(int height) {
		int capacity = TANK.getCapacity();
		int volume = TANK.getFluidAmount();
		if(capacity != 0) {
			float percentage = (float)volume/(float)capacity;
			return percentage * height;
		}else{
			return 0;
		}			
	}
}
