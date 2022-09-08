package theking530.api.energy;

import java.util.LinkedList;
import java.util.Queue;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;
import theking530.api.energy.StaticPowerEnergyDataTypes.StaticVoltageRange;
import theking530.api.energy.utilities.StaticPowerEnergyUtilities;

public class StaticPowerStorage implements IStaticPowerStorage, INBTSerializable<CompoundTag> {
	public static final int MAXIMUM_IO_CAPTURE_FRAMES = 5;

	protected StaticVoltageRange voltageRange;
	protected double maxInputCurrent;

	protected double storedPower;
	protected double capacity;
	protected double voltageOutput;
	protected double maximumCurrentOutput;
	protected boolean doesProvidePower;
	protected boolean canAcceptPower;

	protected Queue<Double> ioCaptureFrames;
	protected Queue<Double> receiveCaptureFrames;
	protected Queue<Double> extractCaptureFrames;
	protected double currentFrameEnergyReceived;
	protected double currentFrameEnergyExtracted;
	protected double averageRecieved;
	protected double averageExtracted;

	public StaticPowerStorage(double capacity, StaticVoltageRange voltageRange, double maxInputCurrent, double voltageOutput, double currentOutput) {
		this.capacity = capacity;
		this.voltageRange = voltageRange;
		this.maxInputCurrent = maxInputCurrent;
		this.voltageOutput = voltageOutput;
		this.maximumCurrentOutput = currentOutput;
		this.doesProvidePower = true;
		this.canAcceptPower = true;

		ioCaptureFrames = new LinkedList<Double>();
		receiveCaptureFrames = new LinkedList<Double>();
		extractCaptureFrames = new LinkedList<Double>();
	}

	/**
	 * Caches the current energy IO metric and starts capturing a new one. This
	 * should be called once per tick.
	 */
	public void captureEnergyMetric() {
		// IO Capture
		double tranfered = currentFrameEnergyReceived + currentFrameEnergyExtracted;

		ioCaptureFrames.add(tranfered);
		if (ioCaptureFrames.size() > MAXIMUM_IO_CAPTURE_FRAMES) {
			ioCaptureFrames.poll();
		}

		// Capture Received Amounts
		receiveCaptureFrames.add(currentFrameEnergyReceived);
		if (receiveCaptureFrames.size() > MAXIMUM_IO_CAPTURE_FRAMES) {
			receiveCaptureFrames.poll();
		}

		// Capture Extracted Amounts
		extractCaptureFrames.add(currentFrameEnergyExtracted);
		if (extractCaptureFrames.size() > MAXIMUM_IO_CAPTURE_FRAMES) {
			extractCaptureFrames.poll();
		}

		// Cache the average extracted rate.
		averageExtracted = 0;
		for (double value : extractCaptureFrames) {
			averageExtracted += value;
		}
		averageExtracted /= Math.max(1, extractCaptureFrames.size());

		// Cache the average recieved rate.
		averageRecieved = 0;
		for (double value : receiveCaptureFrames) {
			averageRecieved += value;
		}
		averageRecieved /= Math.max(1, receiveCaptureFrames.size());

		// Reset the values.
		currentFrameEnergyReceived = 0;
		currentFrameEnergyExtracted = 0;
	}

	public StaticPowerStorage setCapacity(double capacity) {
		this.capacity = capacity;
		return this;
	}

	public StaticPowerStorage setOutputVoltage(double voltageOutput) {
		this.voltageOutput = voltageOutput;
		return this;
	}

	public StaticPowerStorage setMaximumOutputCurrent(double currentOutput) {
		this.maximumCurrentOutput = currentOutput;
		return this;
	}

	public StaticPowerStorage setDoesProvidePower(boolean doesProvidePower) {
		this.doesProvidePower = doesProvidePower;
		return this;
	}

	public StaticPowerStorage setCanAcceptPower(boolean canAcceptPower) {
		this.canAcceptPower = canAcceptPower;
		return this;
	}

	public StaticPowerStorage setInputVoltageRange(StaticVoltageRange voltageRange) {
		this.voltageRange = voltageRange;
		return this;
	}

	public StaticPowerStorage setMaximumInputCurrent(double maxInputCurrent) {
		this.maxInputCurrent = maxInputCurrent;
		return this;
	}

	/**
	 * Calculates the maximum amount of power that can be supplied by this storage.
	 * This is constrained by the voltage output and maximum current output.
	 * 
	 * @return
	 */
	public double getMaxOutputPower() {
		return StaticPowerEnergyUtilities.getPowerFromVoltageAndCurrent(getVoltageOutput(), getMaximumCurrentOutput());
	}

	/**
	 * Determines whether or not the requested amount of power can be drawn from
	 * this storage. This factors the output voltage and maximum output current.
	 * 
	 * @param power
	 * @return
	 */
	public boolean canSupplyPower(double power) {
		return drainPower(power, true) == power;
	}

	/**
	 * Determines whether or not this storage can accept the provided amount of
	 * power. This factors the the maximum input voltage and maximum input current.
	 * 
	 * @param power
	 * @return
	 */
	public boolean canAcceptPower(double power) {
		return addPower(this.getInputVoltageRange().maximumVoltage(), power, true) == power;
	}

	@Override
	public StaticVoltageRange getInputVoltageRange() {
		return voltageRange;
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
	public double getVoltageOutput() {
		return voltageOutput;
	}

	@Override
	public double getMaximumCurrentOutput() {
		return maximumCurrentOutput;
	}

	@Override
	public double addPower(double voltage, double power, boolean simulate) {
		if (power <= 0) {
			return 0;
		}

		double maxAcceptablePower = Math.min(voltage * getMaximumCurrentInput(), capacity - storedPower);
		double acceptedPower = Math.min(power, maxAcceptablePower);

		if (acceptedPower > 0 && !simulate) {
			currentFrameEnergyReceived += acceptedPower;
			storedPower += acceptedPower;
		}

		return acceptedPower;
	}

	@Override
	public double drainPower(double power, boolean simulate) {
		if (power <= 0) {
			return 0;
		}

		double maxPowerDrain = getVoltageOutput() * getMaximumCurrentOutput();
		double maxUsedPower = Math.min(power, maxPowerDrain);
		maxUsedPower = Math.min(maxUsedPower, storedPower);

		if (!simulate) {
			storedPower -= maxUsedPower;
			currentFrameEnergyExtracted -= maxUsedPower;
		}
		return maxUsedPower;
	}

	@Override
	public boolean canAcceptPower() {
		return canAcceptPower;
	}

	@Override
	public boolean doesProvidePower() {
		return doesProvidePower;
	}

//	public double addPowerIgnoringVoltageLimitations(double power) {
//		double maxAdd = Math.min(power, capacity - storedPower);
//		this.storedPower = SDMath.clamp(storedPower + power, 0, capacity);
//		return maxAdd;
//	}
//
//	public double usePowerIgnoringVoltageLimitations(double power) {
//		double maxUse = Math.min(power, storedPower);
//		this.storedPower = SDMath.clamp(storedPower - maxUse, 0, capacity);
//		return maxUse;
//	}

	public double getAveragePowerUsedPerTick() {
		return averageExtracted;
	}

	public double getAveragePowerAddedPerTick() {
		return averageRecieved;
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag output = new CompoundTag();
		output.put("voltageRange", voltageRange.serializeNBT());
		output.putDouble("maxInputCurrent", maxInputCurrent);
		output.putDouble("storedPower", storedPower);
		output.putDouble("capacity", capacity);
		output.putDouble("voltageOutput", voltageOutput);
		output.putDouble("currentOutput", maximumCurrentOutput);

		output.putDouble("averageRecieved", averageRecieved);
		output.putDouble("averageExtracted", averageExtracted);
		return output;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		voltageRange = StaticVoltageRange.deserializeNBT(nbt.getCompound("voltageRange"));
		maxInputCurrent = nbt.getDouble("maxInputCurrent");
		storedPower = nbt.getDouble("storedPower");
		capacity = nbt.getDouble("capacity");
		voltageOutput = nbt.getDouble("voltageOutput");
		maximumCurrentOutput = nbt.getDouble("currentOutput");

		averageRecieved = nbt.getDouble("averageRecieved");
		averageExtracted = nbt.getDouble("averageExtracted");
	}

	public static StaticPowerStorage fromTag(CompoundTag nbt) {
		StaticPowerStorage output = new StaticPowerStorage(0, StaticVoltageRange.ZERO_VOLTAGE, 0, 0, 0);
		output.deserializeNBT(nbt);
		return output;
	}
}