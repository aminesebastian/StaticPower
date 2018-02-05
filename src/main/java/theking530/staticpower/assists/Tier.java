package theking530.staticpower.assists;

public enum Tier {
	BASIC(50000, 100000, 2000), STATIC(125000,1000000,4000), ENERGIZED(625000,10000000,8000), LUMUM(2500000, 100000000,16000), CREATIVE(-1, -1, -1);
	
	private final int portableBatteryCapacity;
	private final int batteryCapacity;
	private final int fluidCanisterCapacity;
	
	private Tier(int portableBatteryCapacity, int batteryCapacity, int fluidCanisterCapacity) {
		this.portableBatteryCapacity = portableBatteryCapacity;
		this.batteryCapacity = batteryCapacity;
		this.fluidCanisterCapacity = fluidCanisterCapacity;		
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
}
