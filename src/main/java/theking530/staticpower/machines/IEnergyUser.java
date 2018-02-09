package theking530.staticpower.machines;

import theking530.staticpower.energy.StaticEnergyStorage;

public interface IEnergyUser {

	public StaticEnergyStorage getEnergyStorage();
	public boolean isUsingEnergy();
	public int maxEnergyUsagePerTick();
	public int getCurrentEnergyIO();
}
