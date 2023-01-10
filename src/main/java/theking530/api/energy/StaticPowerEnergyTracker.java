package theking530.api.energy;

import java.util.LinkedList;
import java.util.List;
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

	protected Queue<Double> voltageCaptureFrames;
	protected Queue<Double> currentCaptureFrames;

	protected List<PowerStack> currentTickRecievedPower;

	protected double currentFrameEnergyReceived;
	protected double currentFrameEnergyExtracted;
	protected double averageRecieved;
	protected double averageExtracted;

	protected double averageVoltage;
	protected double averageCurrent;
	protected CurrentType lastCurrentType;

	public StaticPowerEnergyTracker() {
		this(MAXIMUM_IO_CAPTURE_FRAMES);
	}

	public StaticPowerEnergyTracker(int maxIoCaptureFrames) {
		this.maxIoCaptureFrames = maxIoCaptureFrames;
		ioCaptureFrames = new LinkedList<>();
		receiveCaptureFrames = new LinkedList<>();
		extractCaptureFrames = new LinkedList<>();
		voltageCaptureFrames = new LinkedList<>();
		currentCaptureFrames = new LinkedList<>();
		currentTickRecievedPower = new LinkedList<>();
		lastCurrentType = CurrentType.DIRECT;
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

		double currentTickAverageVoltage = 0;
		double currentTickAverageCurrent = 0;
		for (PowerStack stack : currentTickRecievedPower) {
			currentTickAverageVoltage += stack.getVoltage().getValue();
			currentTickAverageCurrent += stack.getCurrent();
		}
		currentTickAverageVoltage /= Math.max(1, currentTickRecievedPower.size());
		currentTickAverageCurrent /= Math.max(1, currentTickRecievedPower.size());

		// Capture Voltage
		voltageCaptureFrames.add(currentTickAverageVoltage);
		if (voltageCaptureFrames.size() > maxIoCaptureFrames) {
			voltageCaptureFrames.poll();
		}

		// Capture Current
		currentCaptureFrames.add(currentTickAverageCurrent);
		if (currentCaptureFrames.size() > maxIoCaptureFrames) {
			currentCaptureFrames.poll();
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

		// Cache the average voltage.
		averageVoltage = 0;
		for (double value : voltageCaptureFrames) {
			averageVoltage += value;
		}
		averageVoltage /= Math.max(1, voltageCaptureFrames.size());

		// Cache the average current.
		averageCurrent = 0;
		for (double value : currentCaptureFrames) {
			averageCurrent += value;
		}
		averageCurrent /= Math.max(1, currentCaptureFrames.size());

		// Reset the values.
		currentFrameEnergyReceived = 0;
		currentFrameEnergyExtracted = 0;
		lastCurrentType = CurrentType.DIRECT;
		currentTickRecievedPower.clear();
	}

	public void powerAdded(PowerStack stack) {
		currentFrameEnergyReceived += stack.getPower();
		currentTickRecievedPower.add(stack.copy());
		if (stack.getCurrentType() == CurrentType.ALTERNATING) {
			lastCurrentType = CurrentType.ALTERNATING;
		}
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

	public StaticPowerVoltage getLastRecievedVoltage() {
		return StaticPowerVoltage.getVoltageClass(averageVoltage);
	}

	public double getLastRecievedCurrent() {
		return averageCurrent;
	}

	public CurrentType getLastRecievedCurrentType() {
		return lastCurrentType;
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag output = new CompoundTag();
		output.putDouble("avgRe", averageRecieved);
		output.putDouble("avgEx", averageExtracted);

		output.putDouble("avgV", averageVoltage);
		output.putDouble("avgC", averageCurrent);
		output.putByte("lastCT", (byte) lastCurrentType.ordinal());

		return output;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		averageRecieved = nbt.getDouble("avgRe");
		averageExtracted = nbt.getDouble("avgEx");

		averageVoltage = nbt.getDouble("avgV");
		averageCurrent = nbt.getDouble("avgC");
		lastCurrentType = CurrentType.values()[nbt.getByte("lastCT")];
	}
}
