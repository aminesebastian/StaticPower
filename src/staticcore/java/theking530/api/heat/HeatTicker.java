package theking530.api.heat;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;
import theking530.api.heat.IHeatStorage.HeatTransferAction;
import theking530.staticcore.StaticCore;

public class HeatTicker implements INBTSerializable<CompoundTag> {
	public static final float INSTANT_SNAP_DELTA_THRESHOLD = 1e4f;
	public static final float SMOOTHING_FACTOR = 1.0f / 20.0f;

	protected final float smoothingFactor;
	protected final IHeatStorage owningStorage;

	private long lastTickTime;

	protected List<Float> currentFrameHeated;
	protected float averageHeated;

	protected List<Float> currentFrameCooled;
	protected float averageCooled;

	public HeatTicker(IHeatStorage owningStorage) {
		this(owningStorage, SMOOTHING_FACTOR);
	}

	/**
	 * 
	 * @param smoothingFactor The smoothness of the averaging algorithm. Higher
	 *                        values are more responsive. Must be between (0.0,
	 *                        1.0].
	 */
	public HeatTicker(IHeatStorage owningStorage, float smoothingFactor) {
		this.owningStorage = owningStorage;
		this.smoothingFactor = smoothingFactor;
		currentFrameHeated = new LinkedList<>();
		averageHeated = 0;

		currentFrameCooled = new LinkedList<>();
		averageCooled = 0;
	}

	public void tick(Level level, BlockPos pos) {
		if (lastTickTime == level.getGameTime()) {
			StaticCore.LOGGER.error("StaticPowerStorageTicker#tick should only be called once per tick!");
			return;
		}
		lastTickTime = level.getGameTime();

		captureMetrics();

		HeatUtilities.transferHeat(owningStorage, level, pos, HeatTransferAction.EXECUTE);
	}

	/**
	 * Caches the current energy IO metric and starts capturing a new one. This
	 * should be called once per tick.
	 */
	protected void captureMetrics() {
		if (!currentFrameHeated.isEmpty()) {
			averageHeated = interpolatePowerTowards(averageHeated,
					currentFrameHeated.stream().reduce((current, previous) -> current + previous).get());
		} else {
			averageHeated = 0.0f;
		}

		if (!currentFrameCooled.isEmpty()) {
			averageCooled = interpolatePowerTowards(averageCooled,
					currentFrameCooled.stream().reduce((current, previous) -> current + previous).get());
		} else {
			averageCooled = 0.0f;
		}

		// Reset the current frame values.
		currentFrameHeated.clear();
		currentFrameCooled.clear();
	}

	protected float interpolatePowerTowards(float currentValue, float target) {
		// If the difference is sufficiently large, just snap to the new target.
		float delta = Math.abs(currentValue - target);
		if (delta > INSTANT_SNAP_DELTA_THRESHOLD) {
			return target;
		}

		if (delta < 1) {
			return target;
		}

		float newAverage = smoothingFactor * target + (1.0f - smoothingFactor) * currentValue;
		if (newAverage < 0.001) {
			if (target > 0) {
				newAverage = target;
			} else {
				newAverage = 0;
			}
		}
		return newAverage;
	}

	public void heated(float heat) {
		currentFrameHeated.add(heat);
	}

	public void cooled(float power) {
		currentFrameCooled.add(power);
	}

	public double getCurrentFrameAdded() {
		return currentFrameHeated.stream().mapToDouble((stack) -> stack).sum();
	}

	public double getCurrentFrameDrained() {
		return currentFrameCooled.stream().mapToDouble((power) -> power).sum();
	}

	public double getAverageCooledPerTick() {
		// We add 0.0f here to avoid a -0 value getting returned and rendered in the UI.
		return -averageCooled + 0.0f;
	}

	public double getAverageHeatedPerTick() {
		return averageHeated;
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag output = new CompoundTag();
		output.putFloat("r", averageHeated);
		output.putFloat("e", averageCooled);

		return output;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		averageHeated = nbt.getFloat("r");
		averageCooled = nbt.getFloat("e");
	}
}