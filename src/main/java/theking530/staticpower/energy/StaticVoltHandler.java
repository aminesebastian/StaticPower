package theking530.staticpower.energy;

import net.minecraft.nbt.CompoundNBT;

public class StaticVoltHandler implements IStaticVoltHandler {
	int storedPower;
	int capacity;
	int minimumVoltage;
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
		// If there is no power, return 0.
		if (storedPower == 0) {
			return 0;
		}

		// Calculate the maximum amount we can output.
		int output = Math.min(storedPower, amount);

		// If not simulating, perform the drain.
		if (!simulate) {
			storedPower -= output;
		}

		// Return the output amount.
		return output;
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
	public int getMinimumInputVoltage() {
		return minimumVoltage;
	}

	@Override
	public int getMaximumInputVoltage() {
		return maximumVoltage;
	}

	@Override
	public int recievePower(int voltage, int current, boolean simulate) {
		// If the provided voltage is outside the operating range, do nothing.
		if (voltage < minimumVoltage || voltage > maximumVoltage) {
			return 0;
		}

		// Calculate the recieved amount.
		int recievedAmount = StaticVoltUtilities.getMilliWatts(voltage, current);
		recievedAmount = Math.min(recievedAmount, capacity - storedPower);

		// If greater than 0 and not simulating, recieve the power
		if (recievedAmount > 0 && !simulate) {
			storedPower += recievedAmount;
		}

		// Return the recieved amount.
		return recievedAmount;
	}

	public boolean isFull() {
		return storedPower >= capacity;
	}

	public boolean isEmpty() {
		return storedPower == 0;
	}

	public boolean canFullyAcceptPower(int milliVolts, int milliAmps) {
		return storedPower + StaticVoltUtilities.getMilliWatts(milliVolts, milliAmps) <= capacity;
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT output = new CompoundNBT();
		output.putInt("current_power", storedPower);
		output.putInt("capacity", capacity);
		output.putInt("minimum_voltage", minimumVoltage);
		output.putInt("maximum_voltage", maximumVoltage);
		return output;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		storedPower = nbt.getInt("current_power");
		capacity = nbt.getInt("capacity");
		minimumVoltage = nbt.getInt("minimum_voltage");
		maximumVoltage = nbt.getInt("maximum_voltage");
	}
}