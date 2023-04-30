package theking530.api.energy;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.nbt.CompoundTag;
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

	protected boolean canAcceptExternalPower;
	protected boolean canOutputExternalPower;

	protected StaticPowerEnergyTracker ticker;
	/**
	 * If true, the input/output limits for this storage are cummulative per tick.
	 * Meaning if the power output limit is 100 W/t, then trying to drain 20W 6
	 * times in a single tick will result in 5 x 20W drains and then a 0W drain.
	 * 
	 * If false, the limit will be per drain. Meaning if the power output limit is
	 * 100 W/t, then trying to drain 200W will result in a 100W drain. Doing so
	 * multiple times in a single tick will still result in 100W drains.
	 * 
	 * Note: If this is true, then
	 * {@link #StaticPowerEnergyTracker.tick(net.minecraft.world.level.Level)} must
	 * be called per tick.
	 */
	protected boolean arePowerLimitsCumulative;

	public StaticPowerStorage(double capacity, StaticVoltageRange inputVoltageRange, double maxInputPower,
			CurrentType[] acceptableCurrentTypes, StaticPowerVoltage outputVoltage, double maxOutputPower,
			CurrentType outputCurrentType, boolean canAcceptExternalPower, boolean canOutputExternalPower,
			boolean arePowerLimitsCumulative) {
		this();
		this.capacity = capacity;
		this.inputVoltageRange = inputVoltageRange;
		this.maxInputPower = maxInputPower;
		this.outputVoltage = outputVoltage;
		this.maxOutputPower = maxOutputPower;
		this.outputCurrentType = outputCurrentType;
		this.acceptableCurrentTypes.addAll(Arrays.asList(acceptableCurrentTypes));
		this.canAcceptExternalPower = canAcceptExternalPower;
		this.canOutputExternalPower = canOutputExternalPower;
		this.arePowerLimitsCumulative = arePowerLimitsCumulative;
	}

	protected StaticPowerStorage() {
		acceptableCurrentTypes = new HashSet<>();
		ticker = new StaticPowerEnergyTracker();
	}

	public StaticPowerStorage setCapacity(double capacity) {
		this.capacity = capacity;

		// Can't have more power stored than we can actually store.
		if (storedPower > capacity) {
			storedPower = capacity;
		}
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

	public StaticPowerStorage setCanAcceptExternalPower(boolean canAcceptExternalPower) {
		this.canAcceptExternalPower = canAcceptExternalPower;
		return this;
	}

	public StaticPowerStorage setCanOutputExternalPower(boolean canOutputExternalPower) {
		this.canOutputExternalPower = canOutputExternalPower;
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
	public StaticPowerVoltage getOutputVoltage() {
		return outputVoltage;
	}

	@Override
	public double getMaximumPowerOutput() {
		return maxOutputPower;
	}

	@Override
	public boolean canAcceptExternalPower() {
		return canAcceptExternalPower;
	}

	@Override
	public boolean canOutputExternalPower() {
		return canOutputExternalPower;
	}

	@Override
	public double addPower(PowerStack stack, boolean simulate) {
		// If we can't accept the input type, do nothing,
		if (!canAcceptCurrentType(stack.getCurrentType()) || stack.getPower() < 0) {
			if (!simulate) {
				ticker.powerAdded(stack.copyWithPower(0));
			}
			return 0;
		}

		double maxInputRate = getMaximumPowerInput();
		if (arePowerLimitsCumulative) {
			maxInputRate -= getEnergyTracker().getCurrentFrameAdded();
		}

		if (maxInputRate <= 0) {
			return 0;
		}

		double maxPowerDelta = Math.min(getMaximumPowerInput(), capacity - storedPower);
		double actualPowerDelta = Math.min(stack.getPower(), maxPowerDelta);
		actualPowerDelta = Math.min(actualPowerDelta, maxInputRate);

		if (!simulate) {
			ticker.powerAdded(stack.copyWithPower(actualPowerDelta));
			storedPower += actualPowerDelta;
		}

		return actualPowerDelta;
	}

	@Override
	public PowerStack drainPower(double power, boolean simulate) {
		if (power > 0) {
			double maxPowerDrain = getMaximumPowerOutput();
			if (arePowerLimitsCumulative) {
				maxPowerDrain -= getEnergyTracker().getCurrentFrameDrained();
			}

			double maxUsedPower = Math.min(power, maxPowerDrain);
			maxUsedPower = Math.min(maxUsedPower, getStoredPower());
			if (maxUsedPower <= 0) {
				return PowerStack.EMPTY;
			}

			if (!simulate) {
				storedPower -= maxUsedPower;
				ticker.powerDrained(maxUsedPower);
			}
			return new PowerStack(maxUsedPower, getOutputVoltage(), getOutputCurrentType());

		}
		return PowerStack.EMPTY;
	}

	public double getAveragePowerUsedPerTick() {
		return ticker.getAveragePowerDrainedPerTick();
	}

	public double getAveragePowerAddedPerTick() {
		return ticker.getAveragePowerAddedPerTick();
	}

	public StaticPowerVoltage getLastRecievedVoltage() {
		return ticker.getAverageVoltage();
	}

	public double getLastRecievedCurrent() {
		return ticker.getAverageCurrent();
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
			output.putByte("acceptedInputCount" + index, (byte) type.ordinal());
			index++;
		}
		output.put("ticker", ticker.serializeNBT());

		output.putBoolean("canAcceptExternalPower", canAcceptExternalPower);
		output.putBoolean("canOutputExternalPower", canOutputExternalPower);
		output.putBoolean("arePowerLimitsCumulative", arePowerLimitsCumulative);

		return output;
	}

	@Override
	public StaticPowerEnergyTracker getEnergyTracker() {
		return ticker;
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

		canAcceptExternalPower = nbt.getBoolean("canAcceptExternalPower");
		canOutputExternalPower = nbt.getBoolean("canOutputExternalPower");
		arePowerLimitsCumulative = nbt.getBoolean("arePowerLimitsCumulative");
	}

	public static StaticPowerStorage fromTag(CompoundTag nbt) {
		StaticPowerStorage output = new StaticPowerStorage();
		output.deserializeNBT(nbt);
		return output;
	}
}