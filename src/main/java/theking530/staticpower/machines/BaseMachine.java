package theking530.staticpower.machines;

import cofh.api.energy.EnergyStorage;
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

/**
 * @author Amine
 *
 */
public class BaseMachine extends BaseTileEntity implements IEnergyHandler, IEnergyReceiver, IEnergyProvider{
	
	public int INITIAL_ENERGY_PER_TICK;
	public int INITIAL_ENERGY_CAPACITY;
	public StaticEnergyStorage STORAGE;
	
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
	
	public int[] OUTPUT_SLOTS = {1};
	public int[] UPGRADE_SLOTS;

	public BaseMachine() {
	}
	
	/**
	 * @param InitialEnergyMult
	 * @param InitialEnergyCapacity
	 * @param InitialEntryPerTick
	 * @param InitialProcessingTime
	 * @param slotCount
	 * @param outputSlots
	 * @param inputSlots
	 */
	public void initializeBasicMachine(int InitialEnergyMult, int InitialEnergyCapacity, int InitialEntryPerTick, int InitialProcessingTime, int slotCount, int[] inputSlots, int[] outputSlots, int[] upgradeSlots) {	
		initializeBasicTileEntity(slotCount, inputSlots, outputSlots);
		INITIAL_PROCESSING_ENERGY_MULT = InitialEnergyMult;
		INITIAL_ENERGY_CAPACITY = InitialEnergyCapacity;
		INITIAL_ENERGY_PER_TICK = InitialEntryPerTick;
		INITIAL_PROCESSING_TIME = InitialProcessingTime;
		
		STORAGE = new StaticEnergyStorage(InitialEnergyCapacity);
		STORAGE.setMaxExtract(INITIAL_ENERGY_PER_TICK);
		STORAGE.setMaxReceive(INITIAL_ENERGY_PER_TICK);
		STORAGE.setMaxTransfer(INITIAL_ENERGY_PER_TICK);
		STORAGE.setCapacity(InitialEnergyCapacity);		
		UPGRADE_SLOTS = upgradeSlots;
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
			outputFunction(OUTPUT_SLOTS);
			inputFunction(INPUT_SLOTS);
		}
		if(REDSTONE_MODE == 1) {
			if(redstoneSignal == 0) {
				process();	
				outputFunction(OUTPUT_SLOTS);
				inputFunction(INPUT_SLOTS);
			}
		}
		if(REDSTONE_MODE == 2) {
			if(redstoneSignal > 0) {
				process();	
				outputFunction(OUTPUT_SLOTS);
				inputFunction(INPUT_SLOTS);
			}
		}
		if(UPDATE_TIMER < UPDATE_TIME) {
			UPDATE_TIMER++;
		}else{
			markForUpdate();
			markDirty();
			UPDATE_TIMER = 0;
		}
	}				
	public void process(){
		
	}
	public void upgradeHandler(){
		powerUpgrade(UPGRADE_SLOTS);		
		processingUpgrade(UPGRADE_SLOTS);
	}
	public void powerUpgrade(int[] upgradeSlots) {
		boolean flag = false;
		int slot = 0;
		for(int i=0; i<3; i++) {
			if(slots[upgradeSlots[i]] != null) {
				if(slots[upgradeSlots[i]].getItem() instanceof BasePowerUpgrade) {
					flag = true;
					slot = i;
				}
			}
		}
		if(flag) {
			BasePowerUpgrade tempUpgrade = (BasePowerUpgrade) slots[upgradeSlots[slot]].getItem();
			STORAGE.setCapacity((int)(INITIAL_ENERGY_CAPACITY*tempUpgrade.CAPACITY));
			STORAGE.setMaxExtract((int)(INITIAL_ENERGY_PER_TICK*tempUpgrade.TICK_UPGRADE));
			STORAGE.setMaxReceive((int)(INITIAL_ENERGY_PER_TICK*tempUpgrade.TICK_UPGRADE));
			STORAGE.setMaxTransfer((int)(INITIAL_ENERGY_PER_TICK*tempUpgrade.TICK_UPGRADE));
		}else{
			STORAGE.setCapacity(INITIAL_ENERGY_CAPACITY);
			STORAGE.setMaxExtract(INITIAL_ENERGY_PER_TICK);
			STORAGE.setMaxReceive(INITIAL_ENERGY_PER_TICK);
			STORAGE.setMaxTransfer(INITIAL_ENERGY_PER_TICK);
		}
	}
	public void processingUpgrade(int[] upgradeSlots) {
		boolean flag = false;
		int slot = 0;
		for(int i=0; i<3; i++) {
			if(slots[upgradeSlots[i]] != null) {
				if(slots[upgradeSlots[i]].getItem() instanceof BaseSpeedUpgrade) {
					flag = true;
					slot = i;
				}
			}
		}
		if(flag) {
			BaseSpeedUpgrade tempUpgrade = (BaseSpeedUpgrade) slots[upgradeSlots[slot]].getItem();
			PROCESSING_TIME = (int) (INITIAL_PROCESSING_TIME/tempUpgrade.SPEED);
			PROCESSING_ENERGY_MULT = (int) (INITIAL_PROCESSING_ENERGY_MULT*tempUpgrade.POWER_MULT);
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
        
        if(slots != null) {
            NBTTagList list = nbt.getTagList("Items", 10);
    		slots = new ItemStack[getSizeInventory()];
            for (int i =0; i < list.tagCount(); i++) {
    			NBTTagCompound nbt1 = (NBTTagCompound)list.getCompoundTagAt(i);
    			byte b0 = nbt1.getByte("Slot");
    			
    			if (b0 >= 0 && b0 < slots.length) {
    				slots[b0] = ItemStack.loadItemStackFromNBT(nbt1);
    			}
    		}	
        }
    }		
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        STORAGE.writeToNBT(nbt);	
    	if(slots != null) {
        	NBTTagList list = new NBTTagList();
    		for (int i = 0; i < slots.length; i++) {
    			if (slots[i] != null) {
    				NBTTagCompound nbt1 = new NBTTagCompound();
    				nbt1.setByte("Slot", (byte)i);
    				slots[i].writeToNBT(nbt1);
    				list.appendTag(nbt1);
    			}
    			
    		}
    		nbt.setTag("Items", list);
    	}	
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
}
