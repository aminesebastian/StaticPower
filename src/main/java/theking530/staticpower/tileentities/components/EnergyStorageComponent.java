package theking530.staticpower.tileentities.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class EnergyStorageComponent extends AbstractTileEntityComponent implements IEnergyStorage {

	protected int capacity;
	protected int currentEnergy;

	protected int maxReceive;
	protected int maxExtract;

	protected int lastEnergyStored;
	protected int energyPerTick;
	protected List<Integer> powerPerTickList;
	protected int powerPerTickSmoothingFactor;
	protected long lastUpdateTime;

	protected boolean canExtract;
	protected boolean canRecieve;

	public EnergyStorageComponent(String name, int capacity) {
		this(name, capacity, Integer.MAX_VALUE);
	}

	public EnergyStorageComponent(String name, int capacity, int maxInput) {
		this(name, capacity, maxInput, Integer.MAX_VALUE);
	}

	public EnergyStorageComponent(String name, int capacity, int maxInput, int maxExtract) {
		super(name);
		this.capacity = capacity;
		this.maxReceive = maxInput;
		this.maxExtract = maxExtract;
		canRecieve = true;
		canExtract = false;
		powerPerTickSmoothingFactor = 2;
		powerPerTickList = new ArrayList<Integer>();
	}

	@Override
	public void deserializeUpdateNbt(CompoundNBT nbt) {
		if (currentEnergy > capacity) {
			currentEnergy = capacity;
		}

		energyPerTick = nbt.getInt("PerTick");
		currentEnergy = nbt.getInt("Energy");
		capacity = nbt.getInt("Capacity");
		maxReceive = nbt.getInt("MaxRecv");
		maxExtract = nbt.getInt("MaxExtract");
	}

	@Override
	public CompoundNBT serializeUpdateNbt(CompoundNBT nbt) {
		if (currentEnergy < 0) {
			currentEnergy = 0;
		}

		long ticksSinceLastUpdate = Math.max(getTileEntity().getWorld().getGameTime() - lastUpdateTime, 1);
		int energyUsedPerTickSinceLastPacket = (int) ((currentEnergy - lastEnergyStored) / ticksSinceLastUpdate);

		nbt.putInt("Energy", currentEnergy);
		nbt.putInt("Capacity", capacity);
		nbt.putInt("MaxRecv", maxReceive);
		nbt.putInt("MaxExtract", maxExtract);
		nbt.putInt("PerTick", energyUsedPerTickSinceLastPacket);
		lastUpdateTime = getTileEntity().getWorld().getGameTime();
		lastEnergyStored = currentEnergy;
		return nbt;
	}

	public float getEnergyRatio() {
		return (float) currentEnergy / (float) capacity;
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		int maxPossibleRecieve = Math.min(maxReceive, this.maxReceive);
		int maxActualRecieve = Math.min(maxPossibleRecieve, capacity - currentEnergy);

		if (!simulate) {
			currentEnergy += maxActualRecieve;
		}
		return maxActualRecieve;
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		int maxPossibleExtract = Math.min(maxExtract, this.maxExtract);
		int maxActualExtract = Math.min(currentEnergy, maxPossibleExtract);

		if (!simulate) {
			currentEnergy -= maxActualExtract;
		}

		return maxActualExtract;
	}

	@Override
	public int getEnergyStored() {
		return currentEnergy;
	}

	@Override
	public int getMaxEnergyStored() {
		return capacity;
	}

	@Override
	public boolean canExtract() {
		return canExtract;
	}

	@Override
	public boolean canReceive() {
		return canRecieve;
	}

	public void setCanRecieve(boolean newCanRecieve) {
		canRecieve = newCanRecieve;
	}

	public void setCanExtract(boolean newCanExtract) {
		canExtract = newCanExtract;
	}

	public void setMaxReceive(int newMaxRecieve) {
		maxReceive = newMaxRecieve;
	}

	public void setMaxExtract(int newMaxExtract) {
		maxExtract = newMaxExtract;
	}

	/**
	 * This is a helper method that returns the min between the amount of energy
	 * stored in this storage and the maximum amount that can be output per tick.
	 * For example, if our max extract is 256FE/t and we have 100FE left in this
	 * storage, this will return 100. Otherwise, if we have >250FE left in this
	 * storage, this will return 250FE.
	 * 
	 * @return The amount of energy that can be output by this storage on this tick.
	 */
	public int getCurrentMaximumPowerOutput() {
		return Math.min(getEnergyStored(), getMaxExtract());
	}

	/**
	 * This is a helper method that returns the min between the amount of energy
	 * space remaining in this storage and the maximum amount that can be received
	 * per tick. For example, if our max receive is 256FE/t and we have 100FE left
	 * to store in this storage, this will return 100. Otherwise, if we have >250FE
	 * left to store in this storage, this will return 250FE.
	 * 
	 * @return The amount of energy that can be input into this storage on this
	 *         tick.
	 */
	public int getCurrentMaximumPowerInput() {
		return Math.min(getMaxEnergyStored() - getEnergyStored(), getMaxReceive());
	}

	public int getMaxExtract() {
		return maxExtract;
	}

	public int getMaxReceive() {
		return maxReceive;
	}

	public void setCapacity(int newCapacity) {
		capacity = newCapacity;
		currentEnergy = Math.min(currentEnergy, capacity);
	}

	public int getEnergyIO() {
		return energyPerTick;
	}

	@Override
	public void preProcessUpdate() {
		if (powerPerTickList.size() > powerPerTickSmoothingFactor) {
			powerPerTickList.remove(0);
		}
	}

	@Override
	public void postProcessUpdate() {

	}

	@Override
	public <T> LazyOptional<T> provideCapability(Capability<T> cap, Direction side) {
		if (cap == CapabilityEnergy.ENERGY) {
			Optional<SideConfigurationComponent> sideConfig = ComponentUtilities.getComponent(SideConfigurationComponent.class, getTileEntity());
			if (side == null || !sideConfig.isPresent() || !sideConfig.get().getWorldSpaceDirectionConfiguration(side).isDisabledMode()) {
				return LazyOptional.of(() -> {
					return this;
				}).cast();
			}
		}
		return LazyOptional.empty();
	}
}
