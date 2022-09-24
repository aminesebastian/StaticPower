package theking530.api.energy;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;

public class StaticPowerStorage implements IStaticPowerStorage, INBTSerializable<CompoundTag> {
	protected double capacity;
	protected double storedPower;

	protected StaticVoltageRange inputVoltageRange;
	protected double maxInputPower;
	protected Set<CurrentType> acceptableCurrentTypes;

	protected StaticPowerVoltage outputVoltage;
	protected double maxOutputPower;
	protected CurrentType outputCurrentType;

	protected StaticPowerEnergyTracker ticker;

	public StaticPowerStorage(double capacity, StaticVoltageRange inputVoltageRange, double maxInputPower, CurrentType[] acceptableCurrentTypes, StaticPowerVoltage outputVoltage,
			double maxOutputPower, CurrentType outputCurrentType) {
		this();
		this.capacity = capacity;
		this.inputVoltageRange = inputVoltageRange;
		this.maxInputPower = maxInputPower;
		this.outputVoltage = outputVoltage;
		this.maxOutputPower = maxOutputPower;
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

	public StaticPowerStorage setOutputVoltage(StaticPowerVoltage voltageOutput) {
		this.outputVoltage = voltageOutput;
		return this;
	}

	public StaticPowerStorage setMaximumOutputPower(double maxOutputPower) {
		this.maxOutputPower = maxOutputPower;
		return this;
	}

	public StaticPowerStorage setInputVoltageRange(StaticVoltageRange voltageRange) {
		this.inputVoltageRange = voltageRange;
		return this;
	}

	public StaticPowerStorage setMaximumInputPower(double maxInputPower) {
		this.maxInputPower = maxInputPower;
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
		if (power > this.getMaximumPowerInput()) {
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
	public double getMaximumPowerInput() {
		return maxInputPower;
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
		return outputVoltage.getVoltage();
	}

	@Override
	public double getMaximumPowerOutput() {
		return maxOutputPower;
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
		double absPower = Math.abs(stack.getPower());

		double maxPowerDelta = Math.min(getMaximumPowerInput(), capacity - storedPower);
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
			double maxPowerDrain = getMaximumPowerOutput();
			double maxUsedPower = Math.min(power, maxPowerDrain);
			maxUsedPower = Math.min(maxUsedPower, getStoredPower());

			if (!simulate) {
				storedPower -= maxUsedPower;
				ticker.powerDrained(maxUsedPower);
			}
			return new PowerStack(maxUsedPower, getOutputVoltage(), getOutputCurrentType());

		}
		return PowerStack.EMPTY;
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
		output.putDouble("maxInputPower", maxInputPower);

		output.putByte("voltageOutput", (byte) outputVoltage.ordinal());
		output.putDouble("maxOutputPower", maxOutputPower);

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
		maxInputPower = nbt.getDouble("maxInputPower");

		outputVoltage = StaticPowerVoltage.values()[nbt.getByte("voltageOutput")];
		maxOutputPower = nbt.getDouble("maxOutputPower");

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