package theking530.staticpower.tileentities;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import theking530.staticpower.energy.StaticEnergyStorage;
import theking530.staticpower.items.upgrades.BasePowerUpgrade;
import theking530.staticpower.items.upgrades.BaseSpeedUpgrade;
import theking530.staticpower.tileentities.utilities.SideModeList.Mode;
import theking530.staticpower.tileentities.utilities.interfaces.IEnergyUser;
import theking530.staticpower.tileentities.utilities.interfaces.IProcessing;

/**
 * @author Amine
 *
 */
public class TileEntityMachine extends TileEntityBase implements IProcessing, IEnergyUser {

	public static final int DEFAULT_RF_CAPACITY = 100000;

	public int initialEnergyPerTick;
	public int initialEnergyCapacity;

	protected StaticEnergyStorage energyStorage;

	public int initialPowerUse = 100;
	public float initialProcessingEnergyMult;
	public float processingEnergyMult = initialProcessingEnergyMult;
	public int initialProcessingTime;
	public int processingTime = initialProcessingTime;

	public int moveSpeed = 4;

	public int processingTimer = 0;
	public int moveTimer = 0;

	public int batterySlot = -1;
	public int previouslyStoredEnergyAmount = 0;
	public int currentEnergyPerTick = 0;

	public boolean isUpdateQueued = true;

	public TileEntityMachine(TileEntityType<?> tileEntityType) {
		this(tileEntityType, 0);
	}

	public TileEntityMachine(TileEntityType<?> tileEntityType, int upgradeSlotCount) {
		super(tileEntityType, upgradeSlotCount);
		disableFaceInteraction();
	}

	/**
	 * @param InitialEnergyMult
	 * @param InitialPowerUse
	 * @param InitialEnergyCapacity
	 * @param InitialEntryPerTick
	 * @param InitialProcessingTime
	 */
	public void initializeBasicMachine(float InitialEnergyMult, int InitialPowerUse, int InitialEnergyCapacity, int InitialEntryPerTick, int InitialProcessingTime) {
		initialProcessingEnergyMult = InitialEnergyMult;
		initialEnergyCapacity = InitialEnergyCapacity;
		initialEnergyPerTick = InitialEntryPerTick;
		initialProcessingTime = InitialProcessingTime;
		initialPowerUse = InitialPowerUse;

		energyStorage = new StaticEnergyStorage(InitialEnergyCapacity);
		energyStorage.setMaxReceive(InitialEntryPerTick);
		energyStorage.setMaxExtract(InitialEntryPerTick);
		energyStorage.setCapacity(InitialEnergyCapacity);

		registerComponent(energyStorage);
	}

	@Override
	public void tick() {
		currentEnergyPerTick = energyStorage.getEnergyStored() - previouslyStoredEnergyAmount;
		super.tick();
		this.markDirty();
		this.markTileEntityForSynchronization();
		previouslyStoredEnergyAmount = energyStorage.getEnergyStored();
	}

	@Override
	public void upgradeTick() {
		powerUpgrade();
		processingUpgrade();
	}

	public void powerUpgrade() {
		boolean flag = false;
		int slot = 0;
		for (int i = 0; i < 3; i++) {
			if (upgradesInventoryComponent.getStackInSlot(i) != null) {
				if (upgradesInventoryComponent.getStackInSlot(i).getItem() instanceof BasePowerUpgrade) {
					flag = true;
					slot = i;
				}
			}
		}
		if (flag) {
			BasePowerUpgrade tempUpgrade = (BasePowerUpgrade) upgradesInventoryComponent.getStackInSlot(slot).getItem();
			energyStorage.setCapacity((int) (tempUpgrade.getValueMultiplied(initialEnergyCapacity, tempUpgrade.getUpgradeValueAtIndex(upgradesInventoryComponent.getStackInSlot(slot), 0))));
			energyStorage.setMaxReceive((int) (tempUpgrade.getValueMultiplied(initialEnergyPerTick, tempUpgrade.getUpgradeValueAtIndex(upgradesInventoryComponent.getStackInSlot(slot), 1))));
		} else {
			energyStorage.setCapacity(initialEnergyCapacity);
			energyStorage.setMaxReceive(initialEnergyPerTick);
		}
	}

	public void processingUpgrade() {
		boolean flag = false;
		int slot = 0;
		for (int i = 0; i < 3; i++) {
			if (upgradesInventoryComponent.getStackInSlot(i) != null) {
				if (upgradesInventoryComponent.getStackInSlot(i).getItem() instanceof BaseSpeedUpgrade) {
					flag = true;
					slot = i;
				}
			}
		}
		if (flag) {
			BaseSpeedUpgrade tempUpgrade = (BaseSpeedUpgrade) upgradesInventoryComponent.getStackInSlot(slot).getItem();
			processingTime = Math.max(1, (int) (initialProcessingTime / (1 + (tempUpgrade.getUpgradeValueAtIndex(upgradesInventoryComponent.getStackInSlot(slot), 0)))));
			processingEnergyMult = (int) (tempUpgrade.getValueMultiplied(initialProcessingEnergyMult, tempUpgrade.getUpgradeValueAtIndex(upgradesInventoryComponent.getStackInSlot(slot), 1)));
		} else {
			processingEnergyMult = initialProcessingEnergyMult;
			processingTime = initialProcessingTime;
		}
	}

	public void setBatterySlot(int slot) {
		batterySlot = slot;
	}

	public void useBattery() {
//		if (slotsInput.getStackInSlot(batterySlot) != ItemStack.EMPTY && slotsInput.getStackInSlot(batterySlot).getItem() instanceof IEnergyContainerItem
//				&& energyStorage.getEnergyStored() < energyStorage.getMaxEnergyStored()) {
//			IEnergyContainerItem batteryItem = (IEnergyContainerItem) slotsInput.getStackInSlot(batterySlot).getItem();
//			if (batteryItem.getEnergyStored(slotsInput.getStackInSlot(batterySlot)) > 0) {
//				int maxExtract = batteryItem.extractEnergy(slotsInput.getStackInSlot(batterySlot), energyStorage.getMaxReceive(), false);
//				int recieved = energyStorage.receiveEnergy(maxExtract, false);
//				batteryItem.extractEnergy(slotsInput.getStackInSlot(batterySlot), recieved, true);
//			}
//		}
	}

	public CompoundNBT serializeUpdateNbt(CompoundNBT nbt) {
		super.serializeUpdateNbt(nbt);
		energyStorage.writeToNBT(nbt);
		nbt.putInt("P_TIMER", processingTimer);
		return nbt;
	}

	public void deserializeUpdateNbt(CompoundNBT nbt) {
		super.deserializeUpdateNbt(nbt);
		energyStorage.readFromNBT(nbt);
		processingTimer = nbt.getInt("P_TIMER");
	}

	/* PROCESSING */
	public int getProcessingEnergy() {
		return (int) (initialPowerUse * processingEnergyMult);
	}

	@Override
	public boolean hasValidRecipe() {
		return false;
	}

	@Override
	public boolean canProcess() {
		return hasValidRecipe();
	}

	@Override
	public boolean isProcessing() {
		return processingTimer > 0 && redstoneControlComponent.passesRedstoneCheck();
	}

	@Override
	public int getProcessingTime() {
		return processingTime;
	}

	@Override
	public int getCurrentProgress() {
		return processingTimer;
	}

	@Override
	public float getProcessingPercentage() {
		return (float) processingTimer / (float) processingTime;
	}

	public boolean isMoving() {
		return moveTimer > 0;
	}

	public int getProgressScaled(int i) {
		return (processingTimer * i) / Math.max(processingTime, 1);
	}

	/* ENERGY */
	public float getEnergyPercent() {
		float amount = energyStorage.getEnergyStored();
		float capacity = energyStorage.getMaxEnergyStored();
		float volume = (amount / capacity);
		return volume;
	}

	public boolean hasPower() {
		return energyStorage.getEnergyStored() > 0 ? true : false;
	}

	public int useEnergy(int energyCost) {
		return 0; // extractEnergy(null, energyCost, false);
	}

	/* ENERGY AND PROCESSING */
	@Override
	public boolean isUsingEnergy() {
		return isProcessing();
	}

	@Override
	public int maxEnergyUsagePerTick() {
		return getProcessingEnergy() / Math.max(1, getProcessingTime());
	}

	@Override
	public StaticEnergyStorage getEnergyStorage() {
		return energyStorage;
	}

	@Override
	public int getCurrentEnergyIO() {
		return currentEnergyPerTick;
	}

	/* CAPABILITIES */
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == CapabilityEnergy.ENERGY) {
			if (energyStorage != null) {
				if (side != null && this.getSideConfiguration(side) == Mode.Disabled) {
					return null;
				}
				return LazyOptional.of(() -> {
					return energyStorage;
				}).cast();
			}
		}
		return super.getCapability(cap, side);
	}
}
