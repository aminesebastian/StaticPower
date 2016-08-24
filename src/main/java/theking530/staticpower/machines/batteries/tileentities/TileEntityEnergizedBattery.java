package theking530.staticpower.machines.batteries.tileentities;

import theking530.staticpower.assists.Tier;
import theking530.staticpower.power.PowerDistributor;

public class TileEntityEnergizedBattery extends TileEntityBattery{

	private int INPUT = 500;
	private int OUTPUT = 500;
	
	public TileEntityEnergizedBattery() {
		TIER = Tier.ENERGIZED;
		initializeBasicMachine(2, 0, 2500000, 1000, 0, 0, new int[]{}, new int[]{}, new int[]{});
		MAX_INPUT = 1000;
		MAX_OUTPUT = 1000;
		CUSTOM_NAME = "EnergizedBattery";
		POWER_DIS = new PowerDistributor(this, STORAGE);
	}
	@Override
	public String getName() {
		return "Energized Battery";
	}
}
