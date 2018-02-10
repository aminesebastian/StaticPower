package theking530.staticpower.machines.batteries.tileentities;

import theking530.staticpower.energy.PowerDistributor;

public class TileEntityEnergizedBattery extends TileEntityBattery{

	public TileEntityEnergizedBattery() {
		initializeSlots(0, 0, 0);
		initializeBasicMachine(2, 0, 10000000, 500, 0);
		MAX_INPUT = 1000;
		MAX_OUTPUT = 1000;
		POWER_DIS = new PowerDistributor(this, energyStorage);
	}
	@Override
	public String getName() {
		return "container.EnergizedBattery";
	}
}
