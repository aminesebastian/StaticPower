package theking530.api.energy.consumer;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;
import theking530.api.energy.StaticPowerEnergyDataTypes.StaticVoltageRange;
import theking530.staticpower.tileentities.components.energy.PowerStorageComponent;

public class StaticPowerStorage implements IStaticPowerStorage, INBTSerializable<CompoundTag> {
	protected StaticVoltageRange voltageRange;
	protected double storedPower;
	protected double capacity;
	protected double voltageOutput;

	public StaticPowerStorage(StaticVoltageRange voltageRange, double capacity, double voltageOutput) {
		this.voltageRange = voltageRange;
		this.capacity = capacity;
		this.voltageOutput = voltageOutput;
	}

	public StaticPowerStorage setCapacity(double capacity) {
		this.capacity = capacity;
		return this;
	}

	public StaticPowerStorage setOutputVoltage(double voltageOutput) {
		this.voltageOutput = voltageOutput;
		return this;
	}

	@Override
	public StaticVoltageRange getInputVoltageRange() {
		return voltageRange;
	}

	@Override
	public double getStoredPower() {
		return storedPower;
	}

	@Override
	public double getCapacity() {
		return capacity;
	}

	@Override
	public double getVoltageOutput() {
		return voltageOutput;
	}

	@Override
	public double addPower(double voltage, double power, boolean simulate) {
		if (power < 0) {
			return 0;
		}

		double maxAcceptablePower = capacity - storedPower;

		double acceptedPower = Math.min(power, maxAcceptablePower);
		if (!simulate) {
			storedPower += acceptedPower;
		}

		return acceptedPower;
	}

	@Override
	public double usePower(double power, boolean simulate) {
		double maxUsedPower = Math.min(power, storedPower);
		if (!simulate) {
			storedPower -= maxUsedPower;
		}
		return maxUsedPower;
	}

	@Override
	public boolean canAcceptPower() {
		return true;
	}

	@Override
	public boolean doesProvidePower() {
		return true;
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag output = new CompoundTag();
		output.put("voltageRange", voltageRange.serializeNBT());
		output.putDouble("storedPower", storedPower);
		output.putDouble("capacity", capacity);
		output.putDouble("voltageOutput", voltageOutput);
		return output;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		voltageRange = StaticVoltageRange.deserializeNBT(nbt.getCompound("voltageRange"));
		storedPower = nbt.getDouble("storedPower");
		capacity = nbt.getDouble("capacity");
		voltageOutput = nbt.getDouble("voltageOutput");
	}
}