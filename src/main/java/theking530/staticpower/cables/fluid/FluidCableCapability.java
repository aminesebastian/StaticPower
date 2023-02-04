package theking530.staticpower.cables.fluid;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import theking530.staticcore.cablenetwork.Cable;
import theking530.staticcore.cablenetwork.capabilities.ServerCableCapability;
import theking530.staticcore.cablenetwork.capabilities.ServerCableCapabilityType;

public class FluidCableCapability extends ServerCableCapability {
	public static final float INTERPOLATION_RATE = 0.1f;
	public static final int MAX_PIPE_PRESSURE = 32;

	private FluidStack containedFluid;
	private float pressure;
	private float targetPressure;
	private int capacity;
	private int transferRate;
	private boolean isIndustrial;

	public FluidCableCapability(ServerCableCapabilityType<?> type, Cable owningCable) {
		super(type, owningCable);
		this.containedFluid = FluidStack.EMPTY.copy();
		this.pressure = 0;
		this.targetPressure = 0;
	}

	public void initialize(int capacity, int transferRate, boolean isIndustrial) {
		this.isIndustrial = isIndustrial;
		this.transferRate = transferRate;
		this.capacity = capacity;
	}

	public FluidStack getFluidStorage() {
		return this.containedFluid;
	}

	public int getTransferRate() {
		return transferRate;
	}

	public boolean isIndustrial() {
		return isIndustrial;
	}

	public int getCapacity() {
		return capacity;
	}

	public int getFluidAmount() {
		return containedFluid.getAmount();
	}

	public boolean isEmpty() {
		return containedFluid.isEmpty();
	}

	/**
	 * Note: DO NOT MODIFY THIS FLUID. Use the provided methods
	 * {@link #drain(int, FluidAction)} and {@link #fill(FluidStack, FluidAction)}
	 * to interact with the contained fluid.
	 * 
	 * @return
	 */
	public FluidStack getFluid() {
		return containedFluid;
	}

	public boolean isFluidValid(FluidStack fluid) {
		return containedFluid.isEmpty() ? true : fluid.isFluidEqual(containedFluid);
	}

	public float getTargetPressure() {
		return targetPressure;
	}

	public void setTargetPressure(float targetRate) {
		targetPressure = targetRate;
	}

	public float getPressure() {
		return pressure;
	}

	public int fill(FluidStack fluid, FluidAction action) {
		if (!containedFluid.isEmpty() && !fluid.isFluidEqual(containedFluid)) {
			return 0;
		}

		int maxInput = capacity - containedFluid.getAmount();
		maxInput = Math.min(maxInput, fluid.getAmount());
		if (maxInput == 0) {
			return 0;
		}

		if (action != FluidAction.SIMULATE) {
			if (containedFluid.isEmpty()) {
				containedFluid = fluid.copy();
				containedFluid.setAmount(maxInput);
			} else {
				containedFluid.grow(maxInput);
			}
		}

		return maxInput;
	}

	public FluidStack drain(int amount, FluidAction action) {
		int maxDrain = Math.min(amount, getFluid().getAmount());
		if (maxDrain <= 0) {
			return FluidStack.EMPTY;
		}

		FluidStack output = getFluid().copy();
		output.setAmount(maxDrain);
		if (action == FluidAction.EXECUTE) {
			getFluid().shrink(maxDrain);

		}
		return output;
	}

	public void updatePressure() {
		float newValue = (1.0f - INTERPOLATION_RATE) * pressure + INTERPOLATION_RATE * targetPressure;
		if (newValue < 0.1f) {
			newValue = 0;
		}
		pressure = newValue;
	}

	@Override
	public void save(CompoundTag tag) {
		containedFluid.writeToNBT(tag);
		tag.putFloat("Pressure", pressure);
		tag.putFloat("TargetPressure", targetPressure);
		tag.putInt("Capacity", capacity);
		tag.putInt("TransferRate", transferRate);
		tag.putBoolean("IsIndustrial", isIndustrial);
	}

	@Override
	public void load(CompoundTag tag) {
		containedFluid = FluidStack.loadFluidStackFromNBT(tag);
		pressure = tag.getFloat("Pressure");
		targetPressure = tag.getFloat("TargetPressure");
		capacity = tag.getInt("Capacity");
		transferRate = tag.getInt("TransferRate");
		isIndustrial = tag.getBoolean("IsIndustrial");
	}

	@Override
	public String toString() {
		return "FluidCableCapability [accumulatedFluid=" + containedFluid + ", pressure=" + pressure + ", targetPressure=" + targetPressure + ", capacity=" + capacity
				+ ", transferRate=" + transferRate + ", isIndustrial=" + isIndustrial + "]";
	}

	public static class FluidCableCapabilityType extends ServerCableCapabilityType<FluidCableCapability> {
		@Override
		public FluidCableCapability create(Cable owningCable) {
			return new FluidCableCapability(this, owningCable);
		}
	}
}
