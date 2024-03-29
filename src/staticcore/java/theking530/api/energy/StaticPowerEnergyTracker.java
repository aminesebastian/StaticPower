package theking530.api.energy;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import theking530.staticcore.StaticCore;

public class StaticPowerEnergyTracker implements IStaticPowerEnergyTracker {
	public static final double INSTANT_SNAP_DELTA_THRESHOLD = 1e4;
	public static final double SMOOTHING_FACTOR = 1.0 / 5.0;
	protected final double smoothingFactor;

	private long lastTickTime;

	protected List<PowerStack> currentFrameRecieved;
	protected double averageRecievedPower;

	protected List<Double> currentFrameExtracted;
	protected double averageDrainedPower;

	protected StaticPowerVoltage averageVoltage;
	protected double averageCurrent;

	protected CurrentType currentFrameCurrentType;
	protected CurrentType lastCurrentType;

	public StaticPowerEnergyTracker() {
		this(SMOOTHING_FACTOR);
	}

	/**
	 * 
	 * @param smoothingFactor The smoothness of the averaging algorithm. Higher
	 *                        values are more responsive. Must be between (0.0,
	 *                        1.0].
	 */
	public StaticPowerEnergyTracker(double smoothingFactor) {
		this.smoothingFactor = smoothingFactor;
		currentFrameRecieved = new LinkedList<>();
		averageRecievedPower = 0;

		currentFrameExtracted = new LinkedList<>();
		averageDrainedPower = 0;

		averageVoltage = StaticPowerVoltage.ZERO;
		averageCurrent = 0;

		currentFrameCurrentType = CurrentType.DIRECT;
		lastCurrentType = CurrentType.DIRECT;
	}

	@Override
	public void tick(Level level) {
		if (lastTickTime == level.getGameTime()) {
			StaticCore.LOGGER.error("StaticPowerStorageTicker#tick should only be called once per tick!");
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
		averageRecievedPower = interpolatePowerTowards(averageRecievedPower,
				currentFrameRecieved.stream().mapToDouble((stack) -> stack.getPower()).sum());
		averageDrainedPower = interpolatePowerTowards(averageDrainedPower,
				currentFrameExtracted.stream().mapToDouble((power) -> power).sum());

		// Capture the average received voltages and current.
		if (currentFrameRecieved.size() > 0) {
			double totalRecievedVoltage = 0;
			for (PowerStack stack : currentFrameRecieved) {
				totalRecievedVoltage += stack.getVoltage().getValue();
			}
			averageVoltage = StaticPowerVoltage.getVoltageClass(totalRecievedVoltage / currentFrameRecieved.size());
			averageCurrent = averageRecievedPower / averageVoltage.getValue();
		} else {
			averageVoltage = StaticPowerVoltage.ZERO;
			averageCurrent = 0;
		}

		lastCurrentType = currentFrameCurrentType;

		// Reset the current frame values.
		currentFrameRecieved.clear();
		currentFrameExtracted.clear();
		currentFrameCurrentType = CurrentType.DIRECT;
	}

	protected double interpolatePowerTowards(double currentValue, double target) {
		// If the difference is sufficiently large, just snap to the new target.
		double delta = Math.abs(currentValue - target);
		if (delta > INSTANT_SNAP_DELTA_THRESHOLD) {
			return target;
		}

		if (delta < 1) {
			return target;
		}

		double newAverage = smoothingFactor * target + (1.0 - smoothingFactor) * currentValue;
		if (newAverage < 0.001) {
			if (target > 0) {
				newAverage = target;
			} else {
				newAverage = 0;
			}
		}
		return newAverage;
	}

	@Override
	public void powerAdded(PowerStack stack) {
		currentFrameRecieved.add(stack);
		if (stack.getCurrentType() == CurrentType.ALTERNATING) {
			currentFrameCurrentType = CurrentType.ALTERNATING;
		}
	}

	@Override
	public void powerDrained(double power) {
		currentFrameExtracted.add(power);
	}

	public double getCurrentFrameAdded() {
		return currentFrameRecieved.stream().mapToDouble((stack) -> stack.getPower()).sum();
	}

	public double getCurrentFrameDrained() {
		return currentFrameExtracted.stream().mapToDouble((power) -> power).sum();
	}

	@Override
	public double getAveragePowerDrainedPerTick() {
		return -averageDrainedPower;
	}

	@Override
	public double getAveragePowerAddedPerTick() {
		return averageRecievedPower;
	}

	@Override
	public StaticPowerVoltage getAverageVoltage() {
		return averageVoltage;
	}

	@Override
	public double getAverageCurrent() {
		return averageCurrent;
	}

	@Override
	public CurrentType getLastRecievedCurrentType() {
		return lastCurrentType;
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag output = new CompoundTag();
		output.putDouble("r", averageRecievedPower);
		output.putDouble("e", averageDrainedPower);

		output.putByte("v", (byte) averageVoltage.ordinal());
		output.putDouble("c", averageCurrent);
		output.putByte("t", (byte) lastCurrentType.ordinal());

		return output;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		averageRecievedPower = nbt.getDouble("r");
		averageDrainedPower = nbt.getDouble("e");

		averageVoltage = StaticPowerVoltage.values()[nbt.getByte("v")];
		averageCurrent = nbt.getDouble("c");
		lastCurrentType = CurrentType.values()[nbt.getByte("t")];
	}
}
