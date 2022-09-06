package theking530.api.volts;

import java.util.LinkedList;
import java.util.Queue;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;
import theking530.staticcore.utilities.SDMath;

public class StaticVoltHandler implements IStaticVoltHandler, INBTSerializable<CompoundTag> {
	public static final int MAXIMUM_IO_CAPTURE_FRAMES = 5;

	protected long storedPower;
	protected long capacity;
	protected long maxReceive;
	protected long maxDrain;

	protected boolean canRecieve;
	protected boolean canDrain;

	protected Queue<Float> ioCaptureFrames;
	protected Queue<Float> receiveCaptureFrames;
	protected Queue<Float> extractCaptureFrames;
	protected float currentFrameEnergyReceived;
	protected float currentFrameEnergyExtracted;
	protected float averageRecieved;
	protected float averageExtracted;

	public StaticVoltHandler(long capacity, long maxInput, long maxExtract) {
		this.capacity = capacity;
		this.maxReceive = maxInput;
		this.maxDrain = maxExtract;

		canRecieve = true;
		canDrain = true;
		ioCaptureFrames = new LinkedList<Float>();
		receiveCaptureFrames = new LinkedList<Float>();
		extractCaptureFrames = new LinkedList<Float>();
	}

	@Override
	public long getStoredPower() {
		return storedPower;
	}

	@Override
	public long getCapacity() {
		return capacity;
	}

	@Override
	public long drainPower(long amount, boolean simulate) {
		if (!canBeDrained()) {
			return 0;
		}
		// If there is no power, return 0.
		if (storedPower == 0) {
			return 0;
		}

		// Calculate the maximum amount we can output.
		long output = Math.min(storedPower, amount);
		output = Math.min(output, maxDrain);

		// If not simulating, perform the drain.
		if (!simulate) {
			storedPower -= output;
			currentFrameEnergyExtracted -= output;
		}

		// Return the output amount.
		return output;
	}

	@Override
	public long receivePower(long power, boolean simulate) {
		if (!canRecievePower()) {
			return 0;
		}

		long recievedAmount = Math.min(power, capacity - storedPower);
		recievedAmount = Math.min(recievedAmount, maxReceive);

		// If greater than 0 and not simulating, received the power
		if (recievedAmount > 0 && !simulate) {
			storedPower += recievedAmount;
			currentFrameEnergyReceived += recievedAmount;
		}

		// Return the received amount.
		return recievedAmount;
	}

	@Override
	public boolean canRecievePower() {
		return canRecieve;
	}

	@Override
	public boolean canBeDrained() {
		return canDrain;
	}

	public void setCanRecieve(boolean canRecieve) {
		this.canRecieve = canRecieve;
	}

	public void setCanDrain(boolean canDrain) {
		this.canDrain = canDrain;
	}

	public boolean isFull() {
		return storedPower >= capacity;
	}

	public boolean isEmpty() {
		return storedPower == 0;
	}

	public boolean canFullyAcceptPower(long power) {
		return storedPower + power <= capacity;
	}

	public void setMaxReceive(long newMaxRecieve) {
		maxReceive = newMaxRecieve;
	}

	public void setMaxExtract(long newMaxExtract) {
		maxDrain = newMaxExtract;
	}

	/**
	 * This method only exists for internal usage, general gameplay should NOT call
	 * this.
	 * 
	 * @param energy
	 */
	public long addPowerIgnoreTransferRate(long energy) {
		try {
			long remiainigCapacity = capacity - storedPower;
			long toAdd = Math.min(remiainigCapacity, Math.addExact(storedPower, energy));
			storedPower = SDMath.clamp(toAdd, 0, capacity);
			return toAdd;
		} catch (Exception e) {
			storedPower = Long.MAX_VALUE;
		}
		return 0;
	}

	public void usePowerIgnoreTransferRate(long energy) {
		storedPower = SDMath.clamp(storedPower - energy, 0, capacity);
	}

	public long getMaxDrain() {
		return maxDrain;
	}

	public long getMaxReceive() {
		return maxReceive;
	}

	/**
	 * Caches the current energy IO metric and starts capturing a new one. This
	 * should be called once per tick.
	 */
	public void captureEnergyMetric() {
		// IO Capture
		float tranfered = currentFrameEnergyReceived + currentFrameEnergyExtracted;

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
		for (float value : extractCaptureFrames) {
			averageExtracted += value;
		}
		averageExtracted /= Math.max(1, extractCaptureFrames.size());

		// Cache the average recieved rate.
		averageRecieved = 0;
		for (float value : receiveCaptureFrames) {
			averageRecieved += value;
		}
		averageRecieved /= Math.max(1, receiveCaptureFrames.size());

		// Reset the values.
		currentFrameEnergyReceived = 0;
		currentFrameEnergyExtracted = 0;
	}

	/**
	 * Gets the average IO for this storage over the last
	 * {@link #MAXIMUM_IO_CAPTURE_FRAMES} calls to {@link #captureEnergyMetric()}.
	 * 
	 * @return
	 */
	public float getEnergyIO() {
		return averageExtracted + averageRecieved;
	}

	/**
	 * Gets the average extracted energy per tick for this storage over the last
	 * {@link #MAXIMUM_IO_CAPTURE_FRAMES} calls to {@link #captureEnergyMetric()}.
	 * 
	 * @return
	 */
	public float getExtractedPerTick() {
		return averageExtracted;
	}

	/**
	 * Gets the average received energy per tick for this storage over the last
	 * {@link #MAXIMUM_IO_CAPTURE_FRAMES} calls to {@link #captureEnergyMetric()}.
	 * 
	 * @return
	 */
	public float getReceivedPerTick() {
		return averageRecieved;
	}

	public float getStoredEnergyPercentScaled(float scale) {
		return (float) storedPower / (float) capacity;
	}

	public void setCapacity(long newCapacity) {
		capacity = newCapacity;
		storedPower = Math.min(storedPower, capacity);
	}

	/**
	 * This is a helper method that returns the min between the amount of energy
	 * stored in this storage and the maximum amount that can be output per tick.
	 * For example, if our max extract is 256SV/t and we have 100SV left in this
	 * storage, this will return 100. Otherwise, if we have >250SV left in this
	 * storage, this will return 250SV.
	 * 
	 * @return The amount of energy that can be output by this storage on this tick.
	 */
	public long getCurrentMaximumPowerOutput() {
		return Math.min(getStoredPower(), getMaxDrain());
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag output = new CompoundTag();
		output.putLong("current_power", storedPower);
		output.putLong("capacity", capacity);
		output.putLong("max_receive", maxReceive);
		output.putLong("max_drain", maxDrain);
		output.putBoolean("can_recieve", canRecieve);
		output.putBoolean("can_drain", canDrain);
		output.putFloat("received", averageRecieved);
		output.putFloat("extracted", averageExtracted);
		return output;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		storedPower = nbt.getLong("current_power");
		capacity = nbt.getLong("capacity");
		maxReceive = nbt.getLong("max_receive");
		maxDrain = nbt.getLong("max_drain");
		canRecieve = nbt.getBoolean("can_recieve");
		canDrain = nbt.getBoolean("can_drain");
		averageRecieved = nbt.getFloat("received");
		averageExtracted = nbt.getFloat("extracted");
	}
}