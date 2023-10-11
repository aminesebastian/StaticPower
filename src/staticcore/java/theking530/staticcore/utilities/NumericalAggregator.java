package theking530.staticcore.utilities;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.CompoundTag;

public class NumericalAggregator {
	public static final float INSTANT_SNAP_DELTA_THRESHOLD = 1e4f;
	public static final float SMOOTHING_FACTOR = 1.0f / 20.0f;

	protected final float smoothingFactor;
	protected List<Double> currentSamplePeriod;
	protected double averageValue;
	protected double defaultValue;

	/**
	 * 
	 * @param initialValue
	 * @param smoothingFactor The smoothness of the averaging algorithm. Higher
	 *                        values are more responsive. Must be between (0.0,
	 *                        1.0].
	 */
	public NumericalAggregator(double initialValue, float smoothingFactor) {
		currentSamplePeriod = new ArrayList<Double>();
		averageValue = initialValue;
		defaultValue = initialValue;
		this.smoothingFactor = smoothingFactor;
	}

	public NumericalAggregator(double initialValue) {
		this(initialValue, SMOOTHING_FACTOR);
	}

	public NumericalAggregator() {
		this(0);
	}

	public void tick() {
		if (!currentSamplePeriod.isEmpty()) {
			averageValue = interpolatePowerTowards(averageValue,
					currentSamplePeriod.stream().reduce((current, previous) -> current + previous).get());
		} else {
			averageValue = defaultValue;
		}
		currentSamplePeriod.clear();
	}

	public void addSample(int sample) {
		currentSamplePeriod.add((double) sample);
	}

	public void addSample(float sample) {
		currentSamplePeriod.add((double) sample);
	}

	public void addSample(double sample) {
		currentSamplePeriod.add(sample);
	}

	public double getAverageValue() {
		return averageValue + 0.0f;
	}

	public void deserialize(CompoundTag tag) {
		averageValue = tag.getDouble("avg");
	}

	public CompoundTag serialize() {
		CompoundTag output = new CompoundTag();
		output.putDouble("avg", averageValue);
		return output;
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

		double newAverage = smoothingFactor * target + (1.0f - smoothingFactor) * currentValue;
		if (newAverage < 0.001) {
			if (target > 0) {
				newAverage = target;
			} else {
				newAverage = 0;
			}
		}

		return newAverage;
	}
}
