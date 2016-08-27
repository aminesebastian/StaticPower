package theking530.staticpower.machines.batteries.tileentities;

import cofh.api.energy.EnergyStorage;
import theking530.staticpower.assists.Tier;
import theking530.staticpower.power.PowerDistributor;

public class TileEntityBasicBattery extends TileEntityBattery{

	public TileEntityBasicBattery() {
		initializeBasicMachine(2, 0, 100000, 50, 0, 0, 0, 0);
		MAX_INPUT = 100;
		MAX_OUTPUT = 100;
		POWER_DIS = new PowerDistributor(this, STORAGE);
	}
	@Override
	public String getName() {
		return "container.BasicBattery";
	}
}
