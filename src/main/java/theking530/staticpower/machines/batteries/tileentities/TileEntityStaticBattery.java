package theking530.staticpower.machines.batteries.tileentities;

import cofh.api.energy.EnergyStorage;
import theking530.staticpower.assists.Tier;
import theking530.staticpower.power.PowerDistributor;

public class TileEntityStaticBattery extends TileEntityBattery{
	
	private int INPUT = 100;
	private int OUTPUT = 100;
	
	public TileEntityStaticBattery() {
		TIER = Tier.STATIC;
		initializeBasicMachine(2, 0, 500000, 250, 0, 0, new int[]{}, new int[]{}, new int[]{});
		MAX_INPUT = 250;
		MAX_OUTPUT = 250;
		CUSTOM_NAME = "StaticBattery";
		POWER_DIS = new PowerDistributor(this, STORAGE);
	}
	@Override
	public String getName() {
		return "Static Battery";
	}
}
