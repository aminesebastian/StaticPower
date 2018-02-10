package theking530.staticpower.machines.batteries.tileentities;

import theking530.staticpower.energy.PowerDistributor;

public class TileEntityLumumBattery extends TileEntityBattery{

	public TileEntityLumumBattery() {
		initializeSlots(0,0,0);
		initializeBasicMachine(2, 0, 100000000, 5000, 0);
		MAX_INPUT = 10000;
		MAX_OUTPUT = 10000;
		POWER_DIS = new PowerDistributor(this, energyStorage);
	}
	@Override
	public String getName() {
		return "container.LumumBattery";
	}
}
