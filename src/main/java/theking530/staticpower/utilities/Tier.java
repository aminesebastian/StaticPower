package theking530.staticpower.utilities;

public enum Tier {
	BASIC("basic", 50000, 100000, 2000, 512), 
	CREATIVE("creative", 0, 0, 0, -1), 
	IRON("iron", 0, 0, 0, 1024), 
	GOLD("gold", 0, 0, 0, 4096), 
	TIN("tin", 0, 0, 0, -1), 
	COPPER("copper", 0, 0, 0, -1),
	SILVER("silver", 0, 0, 0, -1), 
	LEAD("lead", 0, 0, 0, -64), 
	NICKEL("nickel", 0, 0, 0, -1), 
	ALUMINIUM("aluminium", 0, 0, 0, -1), 
	DIAMOND("diamond", 0, 0, 0, -1), 
	EMERALD("emerald", 0, 0, 0, -1),
	OBSIDIAN("obsidian", 0, 0, 0, 1), // Change this to make the digistore max size to 1 item. IE the digistore
										// capacity minus 1.
	SAPPHIRE("sapphire", 0, 0, 0, -1), 
	RUBY("ruby", 0, 0, 0, -1), 
	STATIC("static", 125000, 1000000, 4000, 2048), 
	ENERGIZED("energized", 625000, 10000000, 8000, 8192),
	LUMUM("lumum", 2500000, 100000000, 16000, 16384);

	private final String name;
	private final int portableBatteryCapacity;
	private final int batteryCapacity;
	private final int fluidCanisterCapacity;
	private final int digistoreItemCapacityUpgrade;

	private Tier(String name, int portableBatteryCapacity, int batteryCapacity, int fluidCanisterCapacity, int digistoreItemCapacityUpgrade) {
		this.portableBatteryCapacity = portableBatteryCapacity;
		this.batteryCapacity = batteryCapacity;
		this.fluidCanisterCapacity = fluidCanisterCapacity;
		this.digistoreItemCapacityUpgrade = digistoreItemCapacityUpgrade;
		this.name = name;
	}

	public int getPortableBatteryCapacity() {
		return portableBatteryCapacity;
	}

	public int getBatteryCapacity() {
		return batteryCapacity;
	}

	public int getFluidCanisterCapacity() {
		return fluidCanisterCapacity;
	}

	public int getDigistoreItemCapacityAmount() {
		return digistoreItemCapacityUpgrade;
	}

	public String getName() {
		return "tier." + name;
	}

	@Override
	public String toString() {
		return name.toLowerCase();
	}
}
