package theking530.staticpower.energy;

import net.minecraftforge.energy.IEnergyStorage;
import net.minecraft.nbt.NBTTagCompound;

public class StaticEnergyStorage implements IEnergyStorage {

	private int capacity;
	private int currentEnergy;
	
	private int maxReceive;
	private int maxExtract;
	
	private boolean canExtract;
	private boolean canRecieve;

	public StaticEnergyStorage(int capacity) {
		this(capacity, Integer.MAX_VALUE);
	}
	public StaticEnergyStorage(int capacity, int maxInput) {
		this(capacity, maxInput, Integer.MAX_VALUE);
	}
	public StaticEnergyStorage(int capacity, int maxInput, int maxExtract) {
		this.capacity = capacity;
		this.maxReceive = maxInput;
		this.maxExtract = maxExtract;
		canRecieve = true;
		canExtract = false;
	}
	

	public StaticEnergyStorage readFromNBT(NBTTagCompound nbt) {
		currentEnergy = nbt.getInteger("Energy");
		capacity = nbt.getInteger("Capacity");
		if (currentEnergy > capacity) {
			currentEnergy = capacity;
		}
		return this;
	}
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		if (currentEnergy < 0) {
			currentEnergy = 0;
		}
		nbt.setInteger("Energy", currentEnergy);
		nbt.setInteger("Capacity", capacity);
		return nbt;
	}
	public float getEnergyRatio(){
		return (float)currentEnergy/(float)capacity;
	}
	public boolean hasEnoughPowerToExtract() {
		if(currentEnergy <= maxExtract) {
			return false;
		}
		return true;
	}
	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		int maxPossibleRecieve = Math.min(maxReceive, this.maxReceive);
		int maxActualRecieve = Math.min(maxPossibleRecieve, capacity-currentEnergy);
		
		if(!simulate) {
			currentEnergy += maxActualRecieve;
		}
		
		return maxActualRecieve;
	}
	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		int maxPossibleExtract = Math.min(maxExtract, this.maxExtract);
		int maxActualExtract = Math.min(currentEnergy, maxPossibleExtract);

		if(!simulate) {
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
	public int getMaxExtract() {
		return maxExtract;
	}
	public int getMaxReceive() {
		return maxReceive;
	}
	public void setCapacity(int newCapacity) {
		capacity = newCapacity;		
	}
}
