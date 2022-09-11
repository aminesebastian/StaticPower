package theking530.api.energy;

import java.util.LinkedList;
import java.util.Queue;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;
import theking530.staticpower.StaticPower;

public class StaticPowerEnergyTracker implements INBTSerializable<CompoundTag> {
	public static final int MAXIMUM_IO_CAPTURE_FRAMES = 5;
	private long lastTickTime;

	protected int maxIoCaptureFrames;
	protected Queue<Double> ioCaptureFrames;
	protected Queue<Double> receiveCaptureFrames;
	protected Queue<Double> extractCaptureFrames;

	protected PowerStack currentRecievedPower;
	protected PowerStack lastRecievedPower;

	protected double currentFrameEnergyReceived;
	protected double currentFrameEnergyExtracted;
	protected double averageRecieved;
	protected double averageExtracted;

	public StaticPowerEnergyTracker() {
		this(MAXIMUM_IO_CAPTURE_FRAMES);
	}

	public StaticPowerEnergyTracker(int maxIoCaptureFrames) {
		this.maxIoCaptureFrames = maxIoCaptureFrames;
		ioCaptureFrames = new LinkedList<>();
		receiveCaptureFrames = new LinkedList<>();
		extractCaptureFrames = new LinkedList<>();
		currentRecievedPower = PowerStack.EMPTY;
		lastRecievedPower = PowerStack.EMPTY;
	}

	public void tick(Level level) {
		if (lastTickTime == level.getGameTime()) {
			StaticPower.LOGGER.error("StaticPowerStorageTicker#tick should only be called once per tick!");
			return;
		}
		lastTickTime = level.getGameTime();

		captureEnergyMetric();
	}

	/**
	 * Caches the current energy IO metric and starts capturing a new one. This
	 * should be called once per tick.
	 */
	protected void captureEnergyMetric() {
		// IO Capture
		double transfered = currentFrameEnergyReceived + currentFrameEnergyExtracted;

		ioCaptureFrames.add(transfered);
		if (ioCaptureFrames.size() > maxIoCaptureFrames) {
			ioCaptureFrames.poll();
		}

		// Capture Received Amounts
		receiveCaptureFrames.add(currentFrameEnergyReceived);
		if (receiveCaptureFrames.size() > maxIoCaptureFrames) {
			receiveCaptureFrames.poll();
		}

		// Capture Extracted Amounts
		extractCaptureFrames.add(currentFrameEnergyExtracted);
		if (extractCaptureFrames.size() > maxIoCaptureFrames) {
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

		// Induce one tick of lag on this metric to make sure we get a correct value in
		// the gui.
		lastRecievedPower = currentRecievedPower;

		// Reset the values.
		currentFrameEnergyReceived = 0;
		currentFrameEnergyExtracted = 0;
		currentRecievedPower = PowerStack.EMPTY;
	}

	public void powerAdded(PowerStack stack) {
		if(stack.getVoltage() == 0) {
			System.out.println(stack.getVoltage());
		}

		currentFrameEnergyReceived += stack.getPower();
		currentRecievedPower = stack.copy();
	}

	public void powerDrained(double power) {
		currentFrameEnergyExtracted -= power;
	}

	public double getAveragePowerUsedPerTick() {
		return averageExtracted;
	}

	public double getAveragePowerAddedPerTick() {
		return averageRecieved;
	}

	public double getLastRecievedVoltage() {
		return lastRecievedPower.getVoltage();
	}

	public double getLastRecievedCurrent() {
		return lastRecievedPower.getCurrent();
	}

	public CurrentType getLastRecievedCurrentType() {
		return lastRecievedPower.getCurrentType();
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag output = new CompoundTag();
		output.putDouble("avgRecieved", averageRecieved);
		output.putDouble("avgExtracted", averageExtracted);
		output.put("lastPower", lastRecievedPower.serialize());

		return output;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		averageRecieved = nbt.getDouble("avgRecieved");
		averageExtracted = nbt.getDouble("avgExtracted");
		lastRecievedPower = PowerStack.deserialize(nbt.getCompound("lastPower"));
	}
}
