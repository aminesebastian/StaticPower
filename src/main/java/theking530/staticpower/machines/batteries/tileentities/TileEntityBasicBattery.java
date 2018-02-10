package theking530.staticpower.machines.batteries.tileentities;

import theking530.staticpower.energy.PowerDistributor;

public class TileEntityBasicBattery extends TileEntityBattery{

	public TileEntityBasicBattery() {
		initializeSlots(0, 0, 0);
		initializeBasicMachine(2, 0, 100000, 50, 0);
		MAX_INPUT = 100;
		MAX_OUTPUT = 100;
		POWER_DIS = new PowerDistributor(this, energyStorage);
	}
	@Override
	public String getName() {
		return "container.BasicBattery";
	}
}
