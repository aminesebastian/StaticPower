package theking530.staticpower.machines.batteries.tileentities;

import theking530.staticpower.energy.PowerDistributor;

public class TileEntityStaticBattery extends TileEntityBattery{

	public TileEntityStaticBattery() {
		initializeBasicMachine(2, 0, 1000000, 125, 0, 0, 0, 0, false);
		MAX_INPUT = 250;
		MAX_OUTPUT = 250;
		POWER_DIS = new PowerDistributor(this, STORAGE);
	}
	@Override
	public String getName() {
		return "container.StaticBattery";
	}
}
