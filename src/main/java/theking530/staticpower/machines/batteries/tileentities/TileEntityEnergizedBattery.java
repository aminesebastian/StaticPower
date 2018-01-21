package theking530.staticpower.machines.batteries.tileentities;

import theking530.staticpower.energy.PowerDistributor;

public class TileEntityEnergizedBattery extends TileEntityBattery{

	public TileEntityEnergizedBattery() {
		initializeBasicMachine(2, 0, 10000000, 500, 0, 0, 0, 0,  false);
		MAX_INPUT = 1000;
		MAX_OUTPUT = 1000;
		POWER_DIS = new PowerDistributor(this, STORAGE);
	}
	@Override
	public String getName() {
		return "container.EnergizedBattery";
	}
}
