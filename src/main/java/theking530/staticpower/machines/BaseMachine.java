package theking530.staticpower.machines;

import javax.annotation.Nullable;

import cofh.redstoneflux.api.IEnergyContainerItem;
import cofh.redstoneflux.api.IEnergyHandler;
import cofh.redstoneflux.api.IEnergyProvider;
import cofh.redstoneflux.api.IEnergyReceiver;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import theking530.staticpower.energy.StaticEnergyStorage;
import theking530.staticpower.items.upgrades.BasePowerUpgrade;
import theking530.staticpower.items.upgrades.BaseSpeedUpgrade;
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
	
	public int BATTERY_SLOT = -1;
	public int PREV_STORAGE = 0;
	public int CURRENT_RF_TICK = 0;
	
	public boolean REQUIRES_UPDATE = true;
	

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
	 * @param disableFaceInteraction
	 */
	public void initializeBasicMachine(int InitialEnergyMult, int InitialPowerUse, int InitialEnergyCapacity, int InitialEntryPerTick, int InitialProcessingTime, int internalSlotCount, int inputSlots, int outputSlots, boolean disableFaceInteraction) {	
		initializeBasicTileEntity(internalSlotCount, inputSlots, outputSlots, disableFaceInteraction);
		INITIAL_PROCESSING_ENERGY_MULT = InitialEnergyMult;
		INITIAL_ENERGY_CAPACITY = InitialEnergyCapacity;
		INITIAL_ENERGY_PER_TICK = InitialEntryPerTick;
		INITIAL_PROCESSING_TIME = InitialProcessingTime;
		INITIAL_POWER_USE = InitialPowerUse;
		
		STORAGE = new StaticEnergyStorage(InitialEnergyCapacity);
		STORAGE.setMaxReceive(InitialEntryPerTick);
		STORAGE.setMaxExtract(InitialEntryPerTick);
		STORAGE.setCapacity(InitialEnergyCapacity);		
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
		initializeBasicMachine(InitialEnergyMult, InitialPowerUse, InitialEnergyCapacity, InitialEntryPerTick, InitialProcessingTime, internalSlotCount, inputSlots, outputSlots, true);	
	}
	@Override
	public void update(){
		upgradeHandler();
		CURRENT_RF_TICK = STORAGE.getEnergyStored() - PREV_STORAGE;
		if(evauluateRedstoneSettings()) {
			process();
			outputFunction();
			inputFunction();	
		}
		if(UPDATE_TIMER < UPDATE_TIME) {
			UPDATE_TIMER++;
		}else{
			if(REQUIRES_UPDATE) {
				updateBlock();
			}
			markDirty();
			UPDATE_TIMER = 0;
		}
		PREV_STORAGE = STORAGE.getEnergyStored();
	}	
	public boolean evauluateRedstoneSettings() {
		int redstoneSignal = getWorld().getStrongPower(pos);
		if(REDSTONE_MODE == 0) {
			return true;
		}
		if(REDSTONE_MODE == 1) {
			if(redstoneSignal <= 0) {
				return true;	
			}
		}
		if(REDSTONE_MODE == 2) {
			if(redstoneSignal > 0) {
				return true;	
			}
		}
		return false;
	}
	@Override
	public void onPlaced(){
		PREV_STORAGE = STORAGE.getEnergyStored();
		updateBlock();
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
			STORAGE.setMaxReceive((int)(tempUpgrade.getValueMultiplied(INITIAL_ENERGY_PER_TICK, tempUpgrade.getMultiplier(SLOTS_UPGRADES.getStackInSlot(slot), 1))));
		}else{
			STORAGE.setCapacity(INITIAL_ENERGY_CAPACITY);
			STORAGE.setMaxReceive(INITIAL_ENERGY_PER_TICK);
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

	public void setBatterySlot(int slot) {
		BATTERY_SLOT = slot;
	}
	public void useBattery() {
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
	
	@Override
	public void readFromSyncNBT(NBTTagCompound nbt) {
		super.readFromSyncNBT(nbt);
		STORAGE.readFromNBT(nbt);
		PROCESSING_TIMER = nbt.getInteger("P_TIMER");
		MOVE_TIMER = nbt.getInteger("M_TIMER");
	}
	@Override
	public NBTTagCompound writeToSyncNBT(NBTTagCompound nbt) {
		super.writeToSyncNBT(nbt);
		STORAGE.writeToNBT(nbt);
		nbt.setInteger("P_TIMER", PROCESSING_TIMER);
		nbt.setInteger("M_TIMER", MOVE_TIMER);
		return nbt;
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
	
	@Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
	}
    @Nullable
    public SPacketUpdateTileEntity getUpdatePacket(){
    	NBTTagCompound tag = new NBTTagCompound();
    	writeToNBT(tag);
    	return new SPacketUpdateTileEntity(pos, getBlockMetadata(), tag);
    }
 
	public void onMachinePlaced(NBTTagCompound nbt) {
		super.onMachinePlaced(nbt);
        STORAGE.readFromNBT(nbt);
	}
	
	public ItemStack[] craftingResults(ItemStack[] items) {
		return null;
	}
	
	//PROCESSING
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
	public boolean isProcessing() {
		return PROCESSING_TIMER > 0;
	}
	public boolean isMoving() {
		return MOVE_TIMER > 0;
	}
	public int getProgressScaled(int i) {
		return (PROCESSING_TIMER * i) / PROCESSING_TIME;
	}
	public int getProcessingCost(){
		return (INITIAL_POWER_USE*PROCESSING_ENERGY_MULT);
	}
	
	//ENERGY
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
		return extractEnergy(null, energyCost, false);
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
		if(!getWorld().isRemote) {
			updateBlock();
		}
		return STORAGE.extractEnergy(maxExtract, simulate);
	}
	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		if(!getWorld().isRemote) {
			updateBlock();
		}
		return STORAGE.receiveEnergy(maxReceive, simulate);
	}
	/* CAPABILITIES */
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing from) {
		return capability == CapabilityEnergy.ENERGY || super.hasCapability(capability, from);
	}
	@Override
	public <T> T getCapability(Capability<T> capability, final EnumFacing from) {
		if (capability == CapabilityEnergy.ENERGY) {
			return CapabilityEnergy.ENERGY.cast(new net.minecraftforge.energy.IEnergyStorage() {

				@Override
				public int receiveEnergy(int maxReceive, boolean simulate) {

					return BaseMachine.this.receiveEnergy(from, maxReceive, simulate);
				}

				@Override
				public int extractEnergy(int maxExtract, boolean simulate) {

					return BaseMachine.this.extractEnergy(from, maxExtract, simulate);
				}

				@Override
				public int getEnergyStored() {

					return BaseMachine.this.getEnergyStored(from);
				}

				@Override
				public int getMaxEnergyStored() {

					return BaseMachine.this.getMaxEnergyStored(from);
				}

				@Override
				public boolean canExtract() {

					return false;
				}

				@Override
				public boolean canReceive() {

					return true;
				}
			});
		}
		return super.getCapability(capability, from);
	}	
}
