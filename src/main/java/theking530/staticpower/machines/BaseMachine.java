package theking530.staticpower.machines;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyContainerItem;
import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import theking530.staticpower.items.upgrades.BasePowerUpgrade;
import theking530.staticpower.items.upgrades.BaseSpeedUpgrade;
import theking530.staticpower.power.StaticEnergyStorage;
import theking530.staticpower.tileentity.BaseTileEntity;

/**
 * @author Amine
 *
 */
public class BaseMachine extends BaseTileEntity implements IEnergyHandler, IEnergyReceiver, IEnergyProvider{
	
	public int INITIAL_ENERGY_PER_TICK;
	public int INITIAL_ENERGY_CAPACITY;
	public StaticEnergyStorage STORAGE;
	
	public int INITIAL_POWER_USE = 100;
	public int INITIAL_PROCESSING_ENERGY_MULT;
	public int PROCESSING_ENERGY_MULT = INITIAL_PROCESSING_ENERGY_MULT;
	public int INITIAL_PROCESSING_TIME;
	public int PROCESSING_TIME = INITIAL_PROCESSING_TIME;
	
	public int MAX_POWER_THRESHOLD;
	public int MIN_POWER_THRESHOLD;
	
	public int MOVE_SPEED = 4;
	
	public int PROCESSING_TIMER = 0;
	public int MOVE_TIMER = 0;
	
	public int UPDATE_TIMER = 10;
	public int UPDATE_TIME = 0;
	
	public int BATTERY_SLOT = -1;
	
	public BaseMachine() {
	}
	
	/**
	 * @param InitialEnergyMult
	 * @param InitialPowerUse
	 * @param InitialEnergyCapacity
	 * @param InitialEntryPerTick
	 * @param InitialProcessingTime
	 * @param slotCount
	 * @param outputSlots
	 * @param inputSlots
	 */
	public void initializeBasicMachine(int InitialEnergyMult, int InitialPowerUse, int InitialEnergyCapacity, int InitialEntryPerTick, int InitialProcessingTime, int internalSlotCount, int inputSlots, int outputSlots) {	
		initializeBasicTileEntity(internalSlotCount, inputSlots, outputSlots);
		INITIAL_PROCESSING_ENERGY_MULT = InitialEnergyMult;
		INITIAL_ENERGY_CAPACITY = InitialEnergyCapacity;
		INITIAL_ENERGY_PER_TICK = InitialEntryPerTick;
		INITIAL_PROCESSING_TIME = InitialProcessingTime;
		INITIAL_POWER_USE = InitialPowerUse;
		
		STORAGE = new StaticEnergyStorage(InitialEnergyCapacity);
		STORAGE.setMaxExtract(INITIAL_ENERGY_PER_TICK);
		STORAGE.setMaxReceive(INITIAL_ENERGY_PER_TICK);
		STORAGE.setMaxTransfer(INITIAL_ENERGY_PER_TICK);
		STORAGE.setCapacity(InitialEnergyCapacity);		
	}
	public void setBatterySlot(int slot) {
		BATTERY_SLOT = slot;
	}
	public float getEnergyPercent() {
		float amount = STORAGE.getEnergyStored();
		float capacity = STORAGE.getMaxEnergyStored();
		float volume = (amount/capacity);		
			return volume;	
	}	
	public boolean hasPower(){
		return STORAGE.getEnergyStored() > 0 ? true : false;
	}
	public int useEnergy(int energyCost) {
		int tempCost = 0;
		if(STORAGE.getEnergyStored() - energyCost >= 0) {
			tempCost = energyCost;
			STORAGE.setEnergyStored(STORAGE.getEnergyStored() - energyCost);	
		}else{
			tempCost = STORAGE.getEnergyStored();
			STORAGE.setEnergyStored(0);
		}
		return tempCost;
	}
	
	@Override
	public void update(){
		upgradeHandler();
		int redstoneSignal = worldObj.getStrongPower(pos);
		if(UPDATE_TIMER < UPDATE_TIME) {
			UPDATE_TIMER++;
		}else{
			//worldObj.mark
			UPDATE_TIMER = 0;
		}
		if(REDSTONE_MODE == 0) {
			process();
			outputFunction();
			inputFunction();
		}
		if(REDSTONE_MODE == 1) {
			if(redstoneSignal == 0) {
				process();	
				outputFunction();
				inputFunction();
			}
		}
		if(REDSTONE_MODE == 2) {
			if(redstoneSignal > 0) {
				process();	
				outputFunction();
				inputFunction();
			}
		}
		if(UPDATE_TIMER < UPDATE_TIME) {
			UPDATE_TIMER++;
		}else{
			markForUpdate();
			markDirty();
			UPDATE_TIMER = 0;
		}
		if(BATTERY_SLOT != -1) {
			if(SLOTS_INPUT.getStackInSlot(BATTERY_SLOT) != null && SLOTS_INPUT.getStackInSlot(BATTERY_SLOT).getItem() instanceof IEnergyContainerItem && STORAGE.getEnergyStored() < STORAGE.getMaxEnergyStored()) {
				IEnergyContainerItem batteryItem = (IEnergyContainerItem) SLOTS_INPUT.getStackInSlot(BATTERY_SLOT).getItem();
				if(batteryItem.getEnergyStored(SLOTS_INPUT.getStackInSlot(BATTERY_SLOT)) > 0) {
					if(STORAGE.getMaxEnergyStored() - STORAGE.getEnergyStored() < STORAGE.getMaxReceive()) {
						STORAGE.receiveEnergy(batteryItem.extractEnergy(SLOTS_INPUT.getStackInSlot(BATTERY_SLOT), STORAGE.getMaxEnergyStored() - STORAGE.getEnergyStored(), false), false);
					}else{
						STORAGE.receiveEnergy(batteryItem.extractEnergy(SLOTS_INPUT.getStackInSlot(BATTERY_SLOT), STORAGE.getMaxReceive(), false), false);		
					}
				}
			}
		}
	}				
	public void process(){
		
	}
	public void upgradeHandler(){
		powerUpgrade();		
		processingUpgrade();
	}
	public void powerUpgrade() {
		boolean flag = false;
		int slot = 0;
		for(int i=0; i<3; i++) {
			if(SLOTS_UPGRADES.getStackInSlot(i) != null) {
				if(SLOTS_UPGRADES.getStackInSlot(i).getItem() instanceof BasePowerUpgrade) {
					flag = true;
					slot = i;
				}
			}
		}
		if(flag) {
			BasePowerUpgrade tempUpgrade = (BasePowerUpgrade) SLOTS_UPGRADES.getStackInSlot(slot).getItem();
			STORAGE.setCapacity((int)(tempUpgrade.getValueMultiplied(INITIAL_ENERGY_CAPACITY, tempUpgrade.getMultiplier(SLOTS_UPGRADES.getStackInSlot(slot), 0))));
			STORAGE.setMaxExtract((int)(tempUpgrade.getValueMultiplied(INITIAL_ENERGY_PER_TICK, tempUpgrade.getMultiplier(SLOTS_UPGRADES.getStackInSlot(slot), 1))));
			STORAGE.setMaxReceive((int)(tempUpgrade.getValueMultiplied(INITIAL_ENERGY_PER_TICK, tempUpgrade.getMultiplier(SLOTS_UPGRADES.getStackInSlot(slot), 1))));
			STORAGE.setMaxTransfer((int)(tempUpgrade.getValueMultiplied(INITIAL_ENERGY_PER_TICK, tempUpgrade.getMultiplier(SLOTS_UPGRADES.getStackInSlot(slot), 1))));
		}else{
			STORAGE.setCapacity(INITIAL_ENERGY_CAPACITY);
			STORAGE.setMaxExtract(INITIAL_ENERGY_PER_TICK);
			STORAGE.setMaxReceive(INITIAL_ENERGY_PER_TICK);
			STORAGE.setMaxTransfer(INITIAL_ENERGY_PER_TICK);
		}
	}
	public void processingUpgrade() {
		boolean flag = false;
		int slot = 0;
		for(int i=0; i<3; i++) {
			if(SLOTS_UPGRADES.getStackInSlot(i) != null) {
				if(SLOTS_UPGRADES.getStackInSlot(i).getItem() instanceof BaseSpeedUpgrade) {
					flag = true;
					slot = i;
				}
			}
		}
		if(flag) {
			BaseSpeedUpgrade tempUpgrade = (BaseSpeedUpgrade) SLOTS_UPGRADES.getStackInSlot(slot).getItem();
			PROCESSING_TIME = (int) (INITIAL_PROCESSING_TIME/(1+(tempUpgrade.getMultiplier(SLOTS_UPGRADES.getStackInSlot(slot), 0))));
			PROCESSING_ENERGY_MULT = (int)(tempUpgrade.getValueMultiplied(INITIAL_PROCESSING_ENERGY_MULT, tempUpgrade.getMultiplier(SLOTS_UPGRADES.getStackInSlot(slot), 1)));
		}else{
			PROCESSING_ENERGY_MULT = INITIAL_PROCESSING_ENERGY_MULT;
			PROCESSING_TIME = INITIAL_PROCESSING_TIME;
		}
	}
	public ItemStack getResult(ItemStack itemstack) {
		return null;	
	}
	public boolean hasResult(ItemStack itemstack) {
		return false;	
	}	
	public boolean canProcess(ItemStack itemstack) {
		return false;
	}
	public int getProcessingEnergy(ItemStack itemstack) {
		return 0;
	}
	
    @Override  
	public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        STORAGE.readFromNBT(nbt);
        PROCESSING_TIMER = nbt.getInteger("P_TIMER");
    }		
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        STORAGE.writeToNBT(nbt);
        nbt.setInteger("P_TIMER", PROCESSING_TIMER);
        return nbt;
	}
	
	public ItemStack[] craftingResults(ItemStack[] items) {
		return null;
	}
	public boolean isProcessing() {
		return PROCESSING_TIMER > 0;
	}
	public boolean isMoving() {
		return MOVE_TIMER > 0;
	}
	public int getProgressScaled(int i) {
		if(this.PROCESSING_TIMER != 0) {
			return (PROCESSING_TIMER * i) / PROCESSING_TIME;
		}
		return 0;
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		return true;
	}
	@Override
	public int getEnergyStored(EnumFacing from) {
		return STORAGE.getEnergyStored();
	}
	@Override
	public int getMaxEnergyStored(EnumFacing from) {
		return STORAGE.getMaxEnergyStored();
	}
	@Override
	public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
		return STORAGE.extractEnergy(maxExtract, simulate);
	}
	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		return STORAGE.receiveEnergy(maxReceive, simulate);
	}
	public int getProcessingCost(){
		return (INITIAL_POWER_USE*PROCESSING_ENERGY_MULT);
	}

}
