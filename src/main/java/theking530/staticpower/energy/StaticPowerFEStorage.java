package theking530.staticpower.energy;

import java.util.LinkedList;
import java.util.Queue;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.energy.IEnergyStorage;

public class StaticPowerFEStorage implements IEnergyStorage {
	public static final int MAXIMUM_IO_CAPTURE_FRAMES = 5;
	protected int capacity;
	protected int currentEnergy;

	protected int maxReceive;
	protected int maxExtract;

	protected boolean canExtract;
	protected boolean canRecieve;

	protected Queue<Integer> ioCaptureFrames;
	protected Queue<Integer> receiveCaptureFrames;
	protected Queue<Integer> extractCaptureFrames;
	protected int currentFrameEnergyReceived;
	protected int currentFrameEnergyExtracted;
	protected int averageRecieved;
	protected int averageExtracted;

	public StaticPowerFEStorage(int capacity) {
		this(capacity, Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

	public StaticPowerFEStorage(int capacity, int maxInput) {
		this(capacity, maxInput, 0);
	}

	public StaticPowerFEStorage(int capacity, int maxInput, int maxExtract) {
		this.capacity = capacity;
		this.maxReceive = maxInput;
		this.maxExtract = maxExtract;

		canRecieve = true;
		canExtract = true;
		ioCaptureFrames = new LinkedList<Integer>();
		receiveCaptureFrames = new LinkedList<Integer>();
		extractCaptureFrames = new LinkedList<Integer>();
	}

	/**
	 * Caches the current energy IO metric and starts capturing a new one. This
	 * should be called once per tick.
	 */
	public void captureEnergyMetric() {
		// IO Capture
		int tranfered = currentFrameEnergyReceived + currentFrameEnergyExtracted;
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
		for (int value : extractCaptureFrames) {
			averageExtracted += value;
		}
		averageExtracted /= Math.max(1, extractCaptureFrames.size());

		// Cache the average recieved rate.
		averageRecieved = 0;
		for (int value : receiveCaptureFrames) {
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
	public int getEnergyIO() {
		return averageExtracted + averageRecieved;
	}

	/**
	 * Gets the average extracted energy per tick for this storage over the last
	 * {@link #MAXIMUM_IO_CAPTURE_FRAMES} calls to {@link #captureEnergyMetric()}.
	 * 
	 * @return
	 */
	public int getExtractedPerTick() {
		return averageExtracted;
	}

	/**
	 * Gets the average received energy per tick for this storage over the last
	 * {@link #MAXIMUM_IO_CAPTURE_FRAMES} calls to {@link #captureEnergyMetric()}.
	 * 
	 * @return
	 */
	public int getRecievedPerTick() {
		return averageRecieved;
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		if (!canReceive()) {
			return 0;
		}
		int maxPossibleRecieve = Math.min(maxReceive, this.maxReceive);
		int maxActualRecieve = Math.min(maxPossibleRecieve, capacity - currentEnergy);

		if (!simulate) {
			currentEnergy += maxActualRecieve;
			currentFrameEnergyReceived += maxActualRecieve;
		}
		return maxActualRecieve;
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		if (!canExtract()) {
			return 0;
		}
		int maxPossibleExtract = Math.min(maxExtract, this.maxExtract);
		int maxActualExtract = Math.min(currentEnergy, maxPossibleExtract);

		if (!simulate) {
			currentEnergy -= maxActualExtract;
			currentFrameEnergyExtracted -= maxActualExtract;
		}

		return maxActualExtract;
	}

	@Override
	public int getEnergyStored() {
		return currentEnergy;
	}

	@Override
	public int getMaxEnergyStored() {
		return capacity;
	}

	@Override
	public boolean canExtract() {
		return canExtract;
	}

	@Override
	public boolean canReceive() {
		return canRecieve;
	}

	public void setCanRecieve(boolean newCanRecieve) {
		canRecieve = newCanRecieve;
	}

	public void setCanExtract(boolean newCanExtract) {
		canExtract = newCanExtract;
	}

	public void setMaxReceive(int newMaxRecieve) {
		maxReceive = newMaxRecieve;
	}

	public void setMaxExtract(int newMaxExtract) {
		maxExtract = newMaxExtract;
	}

	/**
	 * This is a helper method that returns the min between the amount of energy
	 * stored in this storage and the maximum amount that can be output per tick.
	 * For example, if our max extract is 256FE/t and we have 100FE left in this
	 * storage, this will return 100. Otherwise, if we have >250FE left in this
	 * storage, this will return 250FE.
	 * 
	 * @return The amount of energy that can be output by this storage on this tick.
	 */
	public int getCurrentMaximumPowerOutput() {
		return Math.min(getEnergyStored(), getMaxExtract());
	}

	/**
	 * This is a helper method that returns the min between the amount of energy
	 * space remaining in this storage and the maximum amount that can be received
	 * per tick. For example, if our max receive is 256FE/t and we have 100FE left
	 * to store in this storage, this will return 100. Otherwise, if we have >250FE
	 * left to store in this storage, this will return 250FE.
	 * 
	 * @return The amount of energy that can be input into this storage on this
	 *         tick.
	 */
	public int getCurrentMaximumPowerInput() {
		return Math.min(getMaxEnergyStored() - getEnergyStored(), getMaxReceive());
	}

	public int getMaxExtract() {
		return maxExtract;
	}

	public int getMaxReceive() {
		return maxReceive;
	}

	public float getStoredEnergyPercentScaled(float scale) {
		return (float) currentEnergy / (float) capacity;
	}

	public void setCapacity(int newCapacity) {
		capacity = newCapacity;
		currentEnergy = Math.min(currentEnergy, capacity);
	}

	public void readFromNbt(CompoundNBT nbt) {
		if (currentEnergy > capacity) {
			currentEnergy = capacity;
		}

		currentEnergy = nbt.getInt("Energy");
		capacity = nbt.getInt("Capacity");
		maxReceive = nbt.getInt("MaxRecv");
		maxExtract = nbt.getInt("MaxExtract");
		averageRecieved = nbt.getInt("Received");
		averageExtracted = nbt.getInt("Extracted");
	}

	public CompoundNBT writeToNbt(CompoundNBT nbt) {
		if (currentEnergy < 0) {
			currentEnergy = 0;
		}

		nbt.putInt("Energy", currentEnergy);
		nbt.putInt("Capacity", capacity);
		nbt.putInt("MaxRecv", maxReceive);
		nbt.putInt("MaxExtract", maxExtract);
		nbt.putInt("Received", averageRecieved);
		nbt.putInt("Extracted", averageExtracted);
		return nbt;
	}
}
