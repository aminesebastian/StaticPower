package theking530.staticpower.energy;

public class StaticVoltHandler implements IStaticVoltHandler {
	int storedPower;
	int capacity;
	int maximumVoltage;

	boolean canRecieve;
	boolean canDrain;

	public StaticVoltHandler(int capacity, int maximumVoltage) {
		this.capacity = capacity;
		this.maximumVoltage = maximumVoltage;
	}

	@Override
	public int getStoredCharge() {
		return storedPower;
	}

	@Override
	public int getMaximumCharge() {
		return capacity;
	}

	@Override
	public int drainPower(int amount, boolean simulate) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean canRecievePower() {
		return canRecieve;
	}

	@Override
	public boolean canDrainPower() {
		return canDrain;
	}

	@Override
	public int getMaximumInputVoltage() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int recievePower(int voltage, float current, boolean simulate) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getMaximumInputCurrent() {
		// TODO Auto-generated method stub
		return 0;
	}

}