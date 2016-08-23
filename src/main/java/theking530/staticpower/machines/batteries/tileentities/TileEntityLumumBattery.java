package theking530.staticpower.machines.batteries.tileentities;

import theking530.staticpower.assists.Tier;
import theking530.staticpower.power.PowerDistributor;

public class TileEntityLumumBattery extends TileEntityBattery{

	private int INPUT = 2500;
	private int OUTPUT = 2500;
	
	public TileEntityLumumBattery() {
		TIER = Tier.LUMUM;
		initializeBasicMachine(2, 10000000, 5000, 0, 0, new int[]{}, new int[]{}, new int[]{});
		MAX_INPUT = 5000;
		MAX_OUTPUT = 5000;
		CUSTOM_NAME = "LumumBattery";
		POWER_DIS = new PowerDistributor(this, STORAGE);
	}
	@Override
	public String getName() {
		return "Lumum Battery";
	}
}
