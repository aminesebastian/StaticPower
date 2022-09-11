package theking530.api.energy;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;
import theking530.api.energy.utilities.StaticPowerEnergyUtilities;

public class StaticPowerStorage implements IStaticPowerStorage, INBTSerializable<CompoundTag> {
	protected double capacity;
	protected double storedPower;

	protected StaticVoltageRange inputVoltageRange;
	protected double maxInputCurrent;
	protected Set<CurrentType> acceptableCurrentTypes;

	protected double outputVoltage;
	protected double maxOutputCurrent;
	protected CurrentType outputCurrentType;

	protected StaticPowerEnergyTracker ticker;

	public StaticPowerStorage(double capacity, StaticVoltageRange inputVoltageRange, double maxInputCurrent, CurrentType[] acceptableCurrentTypes, double outputVoltage,
			double maxOutputCurrent, CurrentType outputCurrentType) {
		this();
		this.capacity = capacity;
		this.inputVoltageRange = inputVoltageRange;
		this.maxInputCurrent = maxInputCurrent;
		this.outputVoltage = outputVoltage;
		this.maxOutputCurrent = maxOutputCurrent;
		this.outputCurrentType = outputCurrentType;
		this.acceptableCurrentTypes.addAll(Arrays.asList(acceptableCurrentTypes));
	}

	protected StaticPowerStorage() {
		acceptableCurrentTypes = new HashSet<>();
		ticker = new StaticPowerEnergyTracker();
	}

	/**
	 * Caches the current energy IO metric and starts capturing a new one. This
	 * should be called once per tick.
	 */
	public void tick(Level level) {
		ticker.tick(level);
	}

	public StaticPowerStorage setCapacity(double capacity) {
		this.capacity = capacity;
		return this;
	}

	public StaticPowerStorage setOutputVoltage(double voltageOutput) {
		this.outputVoltage = voltageOutput;
		return this;
	}

	public StaticPowerStorage setMaximumOutputCurrent(double currentOutput) {
		this.maxOutputCurrent = currentOutput;
		return this;
	}

	public StaticPowerStorage setInputVoltageRange(StaticVoltageRange voltageRange) {
		this.inputVoltageRange = voltageRange;
		return this;
	}

	public StaticPowerStorage setMaximumInputCurrent(double maxInputCurrent) {
		this.maxInputCurrent = maxInputCurrent;
		return this;
	}

	public StaticPowerStorage setInputCurrentTypes(CurrentType... currentTypes) {
		this.acceptableCurrentTypes.clear();
		for (CurrentType type : currentTypes) {
			acceptableCurrentTypes.add(type);
		}
		return this;
	}

	public StaticPowerStorage setOutputCurrentType(CurrentType type) {
		this.outputCurrentType = type;
		return this;
	}

	/**
	 * Calculates the maximum amount of power that can be supplied by this storage.
	 * This is constrained by the voltage output and maximum current output.
	 * 
	 * @return
	 */
	public double getMaxOutputPower() {
		return StaticPowerEnergyUtilities.getPowerFromVoltageAndCurrent(getOutputVoltage(), getMaximumCurrentOutput());
	}

	/**
	 * Determines whether or not the requested amount of power can be drawn from
	 * this storage. This factors the output voltage and maximum output current.
	 * 
	 * @param power
	 * @return
	 */
	public boolean canSupplyPower(double power) {
		return drainPower(power, true).getPower() == power;
	}

	/**
	 * Determines whether or not this storage can accept the provided amount of
	 * power. This factors the the maximum input voltage and maximum input current.
	 * 
	 * @param stack
	 * @return
	 */
	public boolean canFullyAcceptPower(double power) {
		if (power > this.getOutputVoltage() * this.getMaximumCurrentOutput()) {
			return false;
		}
		return storedPower + power <= capacity;
	}

	@Override
	public boolean canAcceptCurrentType(CurrentType type) {
		return acceptableCurrentTypes.contains(type);
	}

	@Override
	public CurrentType getOutputCurrentType() {
		return outputCurrentType;
	}

	@Override
	public StaticVoltageRange getInputVoltageRange() {
		return inputVoltageRange;
	}

	@Override
	public double getMaximumCurrentInput() {
		return maxInputCurrent;
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
	public double getOutputVoltage() {
		return outputVoltage;
	}

	@Override
	public double getMaximumCurrentOutput() {
		return maxOutputCurrent;
	}

	@Override
	public double addPower(PowerStack stack, boolean simulate) {
		// If we can't accept the input type, do nothing,
		if (!canAcceptCurrentType(stack.getCurrentType()) || stack.getVoltage() < 0 || stack.getPower() < 0) {
			if (!simulate) {
				ticker.powerAdded(new PowerStack(0, stack.getVoltage(), stack.getCurrentType()));
			}
			return 0;
		}

		double actualPowerDelta = 0;
		double absVoltage = Math.abs(stack.getVoltage());
		double absPower = Math.abs(stack.getPower());

		double maxPowerDelta = Math.min(absVoltage * getMaximumCurrentInput(), capacity - storedPower);
		actualPowerDelta = Math.min(absPower, maxPowerDelta);

		if (!simulate) {
			ticker.powerAdded(new PowerStack(actualPowerDelta, stack.getVoltage(), stack.getCurrentType()));
			storedPower += actualPowerDelta;
		}

		return actualPowerDelta;
	}

	@Override
	public PowerStack drainPower(double power, boolean simulate) {
		if (power > 0) {
			double maxPowerDrain = getOutputVoltage() * getMaximumCurrentOutput();
			double maxUsedPower = Math.min(power, maxPowerDrain);
			maxUsedPower = Math.min(maxUsedPower, getStoredPower());

			if (!simulate) {
				storedPower -= maxUsedPower;
				ticker.powerDrained(maxUsedPower);
			}
			return new PowerStack(maxUsedPower, getOutputVoltage(), getOutputCurrentType());
		} else {
			power = -power;
			double maxPowerAdd = getInputVoltageRange().maximumVoltage() * getMaximumCurrentInput();
			double maxAddedPower = Math.min(power, maxPowerAdd);
			maxAddedPower = Math.min(maxAddedPower, getCapacity() - getStoredPower());

			if (!simulate) {
				storedPower += maxAddedPower;
				ticker.powerDrained(-maxAddedPower);
			}
			return new PowerStack(maxAddedPower, getOutputVoltage(), getOutputCurrentType());
		}
	}

	public double getAveragePowerUsedPerTick() {
		return ticker.getAveragePowerUsedPerTick();
	}

	public double getAveragePowerAddedPerTick() {
		return ticker.getAveragePowerAddedPerTick();
	}

	public double getLastRecievedVoltage() {
		return ticker.getLastRecievedVoltage();
	}

	public double getLastRecievedCurrent() {
		return ticker.getLastRecievedCurrent();
	}

	public CurrentType getLastRecievedCurrentType() {
		return ticker.getLastRecievedCurrentType();
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag output = new CompoundTag();
		output.putDouble("capacity", capacity);
		output.putDouble("storedPower", storedPower);

		output.put("inputVoltageRange", inputVoltageRange.serializeNBT());
		output.putDouble("maxInputCurrent", maxInputCurrent);

		output.putDouble("voltageOutput", outputVoltage);
		output.putDouble("currentOutput", maxOutputCurrent);

		output.putByte("outputType", (byte) outputCurrentType.ordinal());

		output.putByte("acceptedInputCount", (byte) acceptableCurrentTypes.size());
		byte index = 0;
		for (CurrentType type : acceptableCurrentTypes) {
			output.putByte("input" + index, (byte) type.ordinal());
			index++;
		}
		output.put("ticker", ticker.serializeNBT());
		return output;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		capacity = nbt.getDouble("capacity");
		storedPower = nbt.getDouble("storedPower");

		inputVoltageRange = StaticVoltageRange.deserializeNBT(nbt.getCompound("inputVoltageRange"));
		maxInputCurrent = nbt.getDouble("maxInputCurrent");

		outputVoltage = nbt.getDouble("voltageOutput");
		maxOutputCurrent = nbt.getDouble("currentOutput");

		outputCurrentType = CurrentType.values()[nbt.getByte("outputType")];

		acceptableCurrentTypes.clear();
		int acceptedInputCount = nbt.getByte("acceptedInputCount");
		for (int i = 0; i < acceptedInputCount; i++) {
			acceptableCurrentTypes.add(CurrentType.values()[nbt.getByte("acceptedInputCount" + i)]);
		}

		ticker.deserializeNBT(nbt.getCompound("ticker"));
	}

	public static StaticPowerStorage fromTag(CompoundTag nbt) {
		StaticPowerStorage output = new StaticPowerStorage();
		output.deserializeNBT(nbt);
		return output;
	}
}