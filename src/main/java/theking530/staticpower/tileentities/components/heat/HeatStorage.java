package theking530.staticpower.tileentities.components.heat;

import java.util.LinkedList;
import java.util.Queue;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public class HeatStorage implements IHeatStorage, INBTSerializable<CompoundNBT> {
	public static final int MAXIMUM_IO_CAPTURE_FRAMES = 5;
	protected float currentHeat;
	protected float maximumHeat;
	protected float maxTransferRate;

	protected boolean canHeat;
	protected boolean canCool;

	protected Queue<Float> ioCaptureFrames;
	protected Queue<Float> receiveCaptureFrames;
	protected Queue<Float> extractCaptureFrames;
	protected float currentFrameEnergyReceived;
	protected float currentFrameEnergyExtracted;
	protected float averageRecieved;
	protected float averageExtracted;

	public HeatStorage(float maximumHeat, float heatTransferRate) {
		this.maximumHeat = maximumHeat;
		this.maxTransferRate = heatTransferRate;
		canHeat = true;
		canCool = true;
		ioCaptureFrames = new LinkedList<Float>();
		receiveCaptureFrames = new LinkedList<Float>();
		extractCaptureFrames = new LinkedList<Float>();
	}

	@Override
	public float getCurrentHeat() {
		return currentHeat;
	}

	@Override
	public float getMaximumHeat() {
		return maximumHeat;
	}

	@Override
	public float getMaximumHeatTransferRate() {
		return maxTransferRate;
	}

	@Override
	public float heat(float amountToHeat, boolean simulate) {
		float clampedToTransferRate = Math.min(maxTransferRate, amountToHeat);
		float remainingHeatCapacity = maximumHeat - currentHeat;
		float actualHeatAmount = Math.min(remainingHeatCapacity, clampedToTransferRate);
		if (!simulate) {
			currentHeat += actualHeatAmount;
		}
		return actualHeatAmount;
	}

	@Override
	public float cool(float amountToCool, boolean simulate) {
		float clampedToTransferRate = Math.min(maxTransferRate, amountToCool);
		float actualCoolAmount = Math.min(currentHeat, clampedToTransferRate);
		if (!simulate) {
			currentHeat -= actualCoolAmount;
		}
		return actualCoolAmount;
	}

	public boolean isAtMaxHeat() {
		return currentHeat == maximumHeat;
	}

	public boolean isEmpty() {
		return currentHeat == 0.0f;
	}

	public boolean canFullyAbsorbHeat(float heatAmount) {
		return currentHeat + heatAmount <= maximumHeat;
	}

	/**
	 * Caches the current heat IO metric and starts capturing a new one. This should
	 * be called once per tick.
	 */
	public void captureHeatTransferMetric() {
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
	 * Gets the average heat IO for this storage over the last
	 * {@link #MAXIMUM_IO_CAPTURE_FRAMES} calls to {@link #captureEnergyMetric()}.
	 * 
	 * @return
	 */
	public float getHeatIO() {
		return averageExtracted + averageRecieved;
	}

	/**
	 * Gets the average extracted heat per tick for this storage over the last
	 * {@link #MAXIMUM_IO_CAPTURE_FRAMES} calls to {@link #captureEnergyMetric()}.
	 * 
	 * @return
	 */
	public float getCooledPerTick() {
		return averageExtracted;
	}

	/**
	 * Gets the average received heat per tick for this storage over the last
	 * {@link #MAXIMUM_IO_CAPTURE_FRAMES} calls to {@link #captureEnergyMetric()}.
	 * 
	 * @return
	 */
	public float getHeatPerTick() {
		return averageRecieved;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		if (currentHeat > maximumHeat) {
			currentHeat = maximumHeat;
		}

		currentHeat = nbt.getFloat("current_heat");
		maximumHeat = nbt.getFloat("maximum_heat");
		maxTransferRate = nbt.getFloat("maximum_transfer_rate");
		averageRecieved = nbt.getFloat("recieved");
		averageExtracted = nbt.getFloat("extracted");
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT output = new CompoundNBT();

		if (currentHeat < 0) {
			currentHeat = 0;
		}

		output.putFloat("current_heat", currentHeat);
		output.putFloat("maximum_heat", maximumHeat);
		output.putFloat("maximum_transfer_rate", maxTransferRate);
		output.putFloat("recieved", averageRecieved);
		output.putFloat("extracted", averageExtracted);
		return output;
	}
}
