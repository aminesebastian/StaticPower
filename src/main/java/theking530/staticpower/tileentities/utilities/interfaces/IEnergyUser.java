package theking530.staticpower.tileentities.utilities.interfaces;

import theking530.staticpower.tileentities.components.EnergyStorageComponent;

public interface IEnergyUser {

	public EnergyStorageComponent getEnergyStorage();

	public boolean isUsingEnergy();

	public int maxEnergyUsagePerTick();

	public int getCurrentEnergyIO();
}
