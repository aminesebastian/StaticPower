package theking530.staticpower.fluid;

import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class StaticPowerFluidTank extends FluidTank implements INBTSerializable<CompoundTag> {
	public static final StaticPowerFluidTank EMPTY = new StaticPowerFluidTank(0);
	public static final int MAXIMUM_IO_CAPTURE_FRAMES = 20;
	protected Queue<Float> ioCaptureFrames;
	protected Queue<Float> filledCaptureFrames;
	protected Queue<Float> drainedCaptureFrames;
	protected float currentFrameFilled;
	protected float currentFrameDrained;
	protected float averageFilled;
	protected float averageDrained;
	protected boolean voidExcess;

	public StaticPowerFluidTank(int capacity) {
		this(capacity, fluid -> true);
	}

	public StaticPowerFluidTank(int capacity, Predicate<FluidStack> validator) {
		super(capacity, validator);
		ioCaptureFrames = new LinkedList<Float>();
		filledCaptureFrames = new LinkedList<Float>();
		drainedCaptureFrames = new LinkedList<Float>();
		voidExcess = false;
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
		if (resource.isEmpty() || !isFluidValid(resource)) {
			return 0;
		}

		if (action.simulate()) {
			if (voidExcess) {
				return resource.getAmount();
			} else if (fluid.isEmpty()) {
				return Math.min(capacity, resource.getAmount());
			} else if (!fluid.isFluidEqual(resource)) {
				return 0;
			}
			return Math.min(capacity - fluid.getAmount(), resource.getAmount());
		}

		if (fluid.isEmpty()) {
			fluid = new FluidStack(resource, Math.min(capacity, resource.getAmount()));
			onContentsChanged();
			currentFrameFilled += fluid.getAmount();
			return voidExcess ? resource.getAmount() : fluid.getAmount();
		} else if (!fluid.isFluidEqual(resource)) {
			return 0;
		}
		int filled = capacity - fluid.getAmount();

		if (resource.getAmount() < filled) {
			fluid.grow(resource.getAmount());
			filled = resource.getAmount();
		} else {
			fluid.setAmount(capacity);
		}
		if (filled > 0) {
			currentFrameFilled += filled;
			onContentsChanged();
		}
		return voidExcess ? resource.getAmount() : filled;
	}

	@Override
	public FluidTank setCapacity(int capacity) {
		this.capacity = capacity;
		if (fluid.getAmount() > capacity) {
			fluid.setAmount(capacity);
		}
		return this;
	}

	@Nonnull
	@Override
	public FluidStack drain(FluidStack resource, FluidAction action) {
		if (resource.isEmpty() || !resource.isFluidEqual(fluid)) {
			return FluidStack.EMPTY;
		}
		return drain(resource.getAmount(), action);
	}

	@Nonnull
	@Override
	public FluidStack drain(int maxDrain, FluidAction action) {
		int drained = maxDrain;
		if (fluid.getAmount() < drained) {
			drained = fluid.getAmount();
		}
		FluidStack stack = new FluidStack(fluid, drained);
		if (action.execute() && drained > 0) {
			fluid.shrink(drained);
			currentFrameDrained -= drained;
			onContentsChanged();
		}
		return stack;
	}

	/**
	 * Caches the current heat IO metric and starts capturing a new one. This should
	 * be called once per tick.
	 */
	public void captureFluidMetrics() {
		// IO Capture
		float tranfered = currentFrameFilled + currentFrameDrained;
		ioCaptureFrames.add(tranfered);
		if (ioCaptureFrames.size() > MAXIMUM_IO_CAPTURE_FRAMES) {
			ioCaptureFrames.poll();
		}

		// Capture Received Amounts
		filledCaptureFrames.add(currentFrameFilled);
		if (filledCaptureFrames.size() > MAXIMUM_IO_CAPTURE_FRAMES) {
			filledCaptureFrames.poll();
		}

		// Capture Extracted Amounts
		drainedCaptureFrames.add(currentFrameDrained);
		if (drainedCaptureFrames.size() > MAXIMUM_IO_CAPTURE_FRAMES) {
			drainedCaptureFrames.poll();
		}

		// Cache the average extracted rate.
		averageDrained = 0;
		for (float value : drainedCaptureFrames) {
			averageDrained += value;
		}
		averageDrained /= Math.max(1, drainedCaptureFrames.size());

		// Cache the average recieved rate.
		averageFilled = 0;
		for (float value : filledCaptureFrames) {
			averageFilled += value;
		}
		averageFilled /= Math.max(1, filledCaptureFrames.size());

		// Reset the values.
		currentFrameFilled = 0;
		currentFrameDrained = 0;
	}

	/**
	 * Gets the average fluid IO for this storage over the last
	 * {@link #MAXIMUM_IO_CAPTURE_FRAMES} calls to {@link #captureFluidMetrics()}.
	 * 
	 * @return
	 */
	public float getFluidIO() {
		return averageFilled + averageDrained;
	}

	/**
	 * Gets the average extracted fluid per tick for this storage over the last
	 * {@link #MAXIMUM_IO_CAPTURE_FRAMES} calls to {@link #captureFluidMetrics()}.
	 * 
	 * @return
	 */
	public float getFilledPerTick() {
		return averageFilled;
	}

	/**
	 * Gets the average received fluid per tick for this storage over the last
	 * {@link #MAXIMUM_IO_CAPTURE_FRAMES} calls to {@link #captureFluidMetrics()}.
	 * 
	 * @return
	 */
	public float getDrainedPerTick() {
		return averageDrained;
	}

	public void setVoidExcess(boolean voidExcess) {
		this.voidExcess = voidExcess;
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag nbt = new CompoundTag();
		nbt.putFloat("recieved", averageFilled);
		nbt.putFloat("extracted", averageDrained);
		nbt.putInt("capacity", capacity);

		CompoundTag tankNbt = new CompoundTag();
		this.writeToNBT(tankNbt);
		nbt.put("tank", tankNbt);
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		averageFilled = nbt.getFloat("recieved");
		averageDrained = nbt.getFloat("extracted");
		capacity = nbt.getInt("capacity");
		readFromNBT(nbt.getCompound("tank"));
	}

	@Override
	public String toString() {
		return "StaticPowerFluidTank [fluid=" + fluid + ", capacity=" + capacity + ", averageFilled=" + averageFilled + ", averageDrained=" + averageDrained + "]";
	}
}
