package theking530.staticpower.energy;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.energy.IEnergyStorage;

public class StaticPowerFEStorage implements IEnergyStorage {
	protected int capacity;
	protected int currentEnergy;

	protected int maxReceive;
	protected int maxExtract;

	protected boolean canExtract;
	protected boolean canRecieve;


	public StaticPowerFEStorage(int capacity) {
		this(capacity, Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

	public StaticPowerFEStorage(int capacity, int maxInput) {
		this(capacity, maxInput, 0);
	}

	public StaticPowerFEStorage(int capacity, int maxInput, int maxExtract) {
		this.capacity = capacity;
		this.maxReceive = maxInput;
		this.maxExtract = maxExtract;

		canRecieve = true;
		canExtract = true;
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		if (!canReceive()) {
			return 0;
		}
		int maxPossibleRecieve = Math.min(maxReceive, this.maxReceive);
		int maxActualRecieve = Math.min(maxPossibleRecieve, capacity - currentEnergy);

		if (!simulate) {
			currentEnergy += maxActualRecieve;
		}
		return maxActualRecieve;
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		if (!canExtract()) {
			return 0;
		}
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

	public float getEnergyRatio() {
		return (float) currentEnergy / (float) capacity;
	}

	public void setCapacity(int newCapacity) {
		capacity = newCapacity;
		currentEnergy = Math.min(currentEnergy, capacity);
	}

	public void readFromNbt(CompoundNBT nbt) {
		if (currentEnergy > capacity) {
			currentEnergy = capacity;
		}

		currentEnergy = nbt.getInt("Energy");
		capacity = nbt.getInt("Capacity");
		maxReceive = nbt.getInt("MaxRecv");
		maxExtract = nbt.getInt("MaxExtract");
	}

	public CompoundNBT writeToNbt(CompoundNBT nbt) {
		if (currentEnergy < 0) {
			currentEnergy = 0;
		}

		nbt.putInt("Energy", currentEnergy);
		nbt.putInt("Capacity", capacity);
		nbt.putInt("MaxRecv", maxReceive);
		nbt.putInt("MaxExtract", maxExtract);
		return nbt;
	}
}
