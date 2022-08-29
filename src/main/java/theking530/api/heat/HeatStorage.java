package theking530.api.heat;

import java.util.LinkedList;
import java.util.Queue;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public class HeatStorage implements IHeatStorage, INBTSerializable<CompoundTag>, Cloneable {
	public static final int MAXIMUM_IO_CAPTURE_FRAMES = 20;
	protected int currentHeat;
	protected int maximumHeat;
	protected float conductivity;

	protected boolean canHeat;
	protected boolean canCool;

	protected Queue<Float> ioCaptureFrames;
	protected Queue<Float> receiveCaptureFrames;
	protected Queue<Float> extractCaptureFrames;
	protected float currentFrameEnergyReceived;
	protected float currentFrameEnergyExtracted;
	protected float averageRecieved;
	protected float averageExtracted;

	public HeatStorage(int maximumHeat, float conductivity) {
		this.maximumHeat = maximumHeat;
		this.conductivity = conductivity;
		canHeat = true;
		canCool = true;
		ioCaptureFrames = new LinkedList<Float>();
		receiveCaptureFrames = new LinkedList<Float>();
		extractCaptureFrames = new LinkedList<Float>();
	}

	@Override
	public int getCurrentHeat() {
		return currentHeat;
	}

	@Override
	public int getOverheatThreshold() {
		return maximumHeat;
	}

	public void setMaximumHeat(int newMax) {
		maximumHeat = newMax;
		currentHeat = Math.min(maximumHeat, currentHeat);
	}

	@Override
	public float getConductivity() {
		return conductivity;
	}

	public void setConductivity(float conductivity) {
		this.conductivity = conductivity;
	}

	@Override
	public int heat(int amountToHeat, boolean simulate) {
		if (!canHeat) {
			return 0;
		}
		int remainingHeatCapacity = maximumHeat - currentHeat;
		int actualHeatAmount = Math.min(remainingHeatCapacity, amountToHeat);
		if (!simulate) {
			currentHeat += actualHeatAmount;
			currentFrameEnergyReceived += actualHeatAmount;
		}

		return actualHeatAmount;
	}

	@Override
	public int cool(int amountToCool, boolean simulate) {
		if (!canCool) {
			return 0;
		}
		int actualCoolAmount = amountToCool;
		if (!simulate) {
			currentHeat -= actualCoolAmount;
			currentFrameEnergyExtracted -= actualCoolAmount;
		}
		return actualCoolAmount;
	}

	public boolean isAtMaxHeat() {
		return currentHeat == maximumHeat;
	}

	public boolean isEmpty() {
		return currentHeat == 0.0f;
	}

	public boolean canFullyAbsorbHeat(double heatAmount) {
		return currentHeat + heatAmount <= maximumHeat;
	}

	public boolean isCanHeat() {
		return canHeat;
	}

	public void setCanHeat(boolean canHeat) {
		this.canHeat = canHeat;
	}

	public boolean isCanCool() {
		return canCool;
	}

	public void setCanCool(boolean canCool) {
		this.canCool = canCool;
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
	 * {@link #MAXIMUM_IO_CAPTURE_FRAMES} calls to
	 * {@link #captureHeatTransferMetric()}.
	 * 
	 * @return
	 */
	public float getHeatIO() {
		return averageExtracted + averageRecieved;
	}

	/**
	 * Gets the average extracted heat per tick for this storage over the last
	 * {@link #MAXIMUM_IO_CAPTURE_FRAMES} calls to
	 * {@link #captureHeatTransferMetric()}.
	 * 
	 * @return
	 */
	public float getCooledPerTick() {
		return averageExtracted;
	}

	/**
	 * Gets the average received heat per tick for this storage over the last
	 * {@link #MAXIMUM_IO_CAPTURE_FRAMES} calls to
	 * {@link #captureHeatTransferMetric()}.
	 * 
	 * @return
	 */
	public float getHeatPerTick() {
		return averageRecieved;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		if (currentHeat > maximumHeat) {
			currentHeat = maximumHeat;
		}

		currentHeat = nbt.getInt("current_heat");
		maximumHeat = nbt.getInt("maximum_heat");
		conductivity = nbt.getFloat("maximum_transfer_rate");
		averageRecieved = nbt.getFloat("recieved");
		averageExtracted = nbt.getFloat("extracted");
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag output = new CompoundTag();

		output.putDouble("current_heat", currentHeat);
		output.putDouble("maximum_heat", maximumHeat);
		output.putDouble("maximum_transfer_rate", conductivity);
		output.putFloat("recieved", averageRecieved);
		output.putFloat("extracted", averageExtracted);
		return output;
	}

	@Override
	public HeatStorage clone() {
		HeatStorage output = new HeatStorage(this.getOverheatThreshold(), this.getConductivity());
		output.deserializeNBT(serializeNBT());
		return output;
	}
}
