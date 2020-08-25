package theking530.staticpower.energy;

import java.util.LinkedList;
import java.util.Queue;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;
import theking530.common.utilities.SDMath;

public class StaticVoltHandler implements IStaticVoltHandler, INBTSerializable<CompoundNBT> {
	public static final int MAXIMUM_IO_CAPTURE_FRAMES = 5;

	protected int storedPower;
	protected int capacity;
	protected int maxReceive;
	protected int maxDrain;

	protected boolean canRecieve;
	protected boolean canDrain;

	protected Queue<Float> ioCaptureFrames;
	protected Queue<Float> receiveCaptureFrames;
	protected Queue<Float> extractCaptureFrames;
	protected float currentFrameEnergyReceived;
	protected float currentFrameEnergyExtracted;
	protected float averageRecieved;
	protected float averageExtracted;

	public StaticVoltHandler(int capacity, int maxInput, int maxExtract) {
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
	public int getStoredPower() {
		return storedPower;
	}

	@Override
	public int getCapacity() {
		return capacity;
	}

	@Override
	public int drainPower(int amount, boolean simulate) {
		if (!canDrainPower()) {
			return 0;
		}
		// If there is no power, return 0.
		if (storedPower == 0) {
			return 0;
		}

		// Calculate the maximum amount we can output.
		int output = Math.min(storedPower, amount);
		output = Math.min(output, maxDrain);

		// If not simulating, perform the drain.
		if (!simulate) {
			storedPower -= output;
		}

		// Return the output amount.
		return output;
	}

	@Override
	public int receivePower(int power, boolean simulate) {
		if (!canRecievePower()) {
			return 0;
		}

		int recievedAmount = Math.min(power, capacity - storedPower);
		recievedAmount = Math.min(recievedAmount, maxReceive);

		// If greater than 0 and not simulating, received the power
		if (recievedAmount > 0 && !simulate) {
			storedPower += recievedAmount;
		}

		// Return the received amount.
		return recievedAmount;
	}

	@Override
	public boolean canRecievePower() {
		return canRecieve;
	}

	@Override
	public boolean canDrainPower() {
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

	public boolean canFullyAcceptPower(int power) {
		return storedPower + power <= capacity;
	}

	public void setMaxReceive(int newMaxRecieve) {
		maxReceive = newMaxRecieve;
	}

	public void setMaxExtract(int newMaxExtract) {
		maxDrain = newMaxExtract;
	}

	public void addPowerIgnoreTransferRate(int energy) {
		storedPower = SDMath.clamp(storedPower + energy, 0, capacity);
	}

	public void usePowerIgnoreTransferRate(int energy) {
		storedPower = SDMath.clamp(storedPower - energy, 0, capacity);
	}

	public int getMaxDrain() {
		return maxDrain;
	}

	public int getMaxReceive() {
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
	public float getRecievedPerTick() {
		return averageRecieved;
	}

	public float getStoredEnergyPercentScaled(float scale) {
		return (float) storedPower / (float) capacity;
	}

	public void setCapacity(int newCapacity) {
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
	public int getCurrentMaximumPowerOutput() {
		return Math.min(getStoredPower(), getMaxDrain());
	}

	/**
	 * This is a helper method that returns the min between the amount of energy
	 * space remaining in this storage and the maximum amount that can be received
	 * per tick. For example, if our max receive is 256SV/t and we have 100FE left
	 * to store in this storage, this will return 100. Otherwise, if we have >250SV
	 * left to store in this storage, this will return 250SV.
	 * 
	 * @return The amount of energy that can be input into this storage on this
	 *         tick.
	 */
	public int getCurrentMaximumPowerInput() {
		return Math.min(getCapacity() - getStoredPower(), getMaxReceive());
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT output = new CompoundNBT();
		output.putInt("current_power", storedPower);
		output.putInt("capacity", capacity);
		output.putBoolean("can_recieve", canRecieve);
		output.putBoolean("can_drain", canDrain);
		output.putFloat("received", averageRecieved);
		output.putFloat("extracted", averageExtracted);
		return output;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		storedPower = nbt.getInt("current_power");
		capacity = nbt.getInt("capacity");
		canRecieve = nbt.getBoolean("can_recieve");
		canDrain = nbt.getBoolean("can_drain");
		averageRecieved = nbt.getFloat("received");
		averageExtracted = nbt.getFloat("extracted");
	}
}