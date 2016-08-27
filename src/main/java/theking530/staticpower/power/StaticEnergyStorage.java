package theking530.staticpower.power;

import cofh.api.energy.EnergyStorage;
import net.minecraft.nbt.NBTTagCompound;

public class StaticEnergyStorage extends EnergyStorage {

	public StaticEnergyStorage(int capacity) {
		super(capacity);
	}
	public StaticEnergyStorage readFromNBT(NBTTagCompound nbt) {
		energy = nbt.getInteger("Energy");
		capacity = nbt.getInteger("Capacity");
		if (energy > capacity) {
			energy = capacity;
		}
		return this;
	}
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		if (energy < 0) {
			energy = 0;
		}
		nbt.setInteger("Energy", energy);
		nbt.setInteger("Capacity", capacity);
		return nbt;
	}
	public float getEnergyRatio(){
		return (float)energy/(float)capacity;
	}
	public boolean hasEnoughPowerToExtract() {
		if(energy <= maxExtract) {
			return false;
		}
		return true;
	}
}
