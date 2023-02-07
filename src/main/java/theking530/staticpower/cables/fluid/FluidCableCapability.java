package theking530.staticpower.cables.fluid;

import org.jetbrains.annotations.NotNull;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import theking530.staticcore.cablenetwork.Cable;
import theking530.staticcore.cablenetwork.capabilities.ServerCableCapability;
import theking530.staticcore.cablenetwork.capabilities.ServerCableCapabilityType;

public class FluidCableCapability extends ServerCableCapability implements IFluidTank {
	public static final float INTERPOLATION_RATE = 0.1f;
	public static final int MAX_PIPE_PRESSURE = 32;

	private FluidStack containedFluid;
	private float headPressure;
	private float targetPressure;
	private int capacity;
	private int transferRate;
	private boolean isIndustrial;

	public FluidCableCapability(ServerCableCapabilityType<?> type, Cable owningCable) {
		super(type, owningCable);
		this.containedFluid = FluidStack.EMPTY.copy();
		this.headPressure = 0;
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

	@Override
	public int getCapacity() {
		return capacity;
	}

	@Override
	public int getFluidAmount() {
		return containedFluid.getAmount();
	}

	public boolean isEmpty() {
		return containedFluid.isEmpty();
	}

	@Override
	public boolean isFluidValid(FluidStack fluid) {
		return containedFluid.isEmpty() ? true : fluid.isFluidEqual(containedFluid);
	}

	public void setHeadPressure(float targetRate) {
		targetPressure = targetRate;
	}

	public float getHeadPressure() {
		return headPressure;
	}

	public boolean isFluidEqual(FluidStack other) {
		return containedFluid.isFluidEqual(other);
	}

	@Override
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

	@Override
	public FluidStack drain(int amount, FluidAction action) {
		int maxDrain = Math.min(amount, containedFluid.getAmount());
		if (maxDrain <= 0) {
			return FluidStack.EMPTY;
		}

		FluidStack output = containedFluid.copy();
		output.setAmount(maxDrain);
		if (action == FluidAction.EXECUTE) {
			containedFluid.shrink(maxDrain);

		}
		return output;
	}

	@Override
	public @NotNull FluidStack getFluid() {
		return containedFluid;
	}

	@Override
	public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
		if (!resource.isFluidEqual(containedFluid)) {
			return FluidStack.EMPTY;
		}
		return drain(resource.getAmount(), action);
	}

	public void updateHeadPressure() {
		float newValue = (1.0f - INTERPOLATION_RATE) * headPressure + INTERPOLATION_RATE * targetPressure;
		if (newValue < 0.1f) {
			newValue = 0;
		}
		headPressure = newValue;
	}

	@Override
	public void save(CompoundTag tag) {
		containedFluid.writeToNBT(tag);
		tag.putFloat("HeadPressure", headPressure);
		tag.putFloat("TargetPressure", targetPressure);
		tag.putInt("Capacity", capacity);
		tag.putInt("TransferRate", transferRate);
		tag.putBoolean("IsIndustrial", isIndustrial);
	}

	@Override
	public void load(CompoundTag tag) {
		containedFluid = FluidStack.loadFluidStackFromNBT(tag);
		headPressure = tag.getFloat("HeadPressure");
		targetPressure = tag.getFloat("TargetPressure");
		capacity = tag.getInt("Capacity");
		transferRate = tag.getInt("TransferRate");
		isIndustrial = tag.getBoolean("IsIndustrial");
	}

	@Override
	public String toString() {
		return "FluidCableCapability [accumulatedFluid=" + containedFluid + ", headPressure=" + headPressure + ", targetPressure=" + targetPressure + ", capacity=" + capacity
				+ ", transferRate=" + transferRate + ", isIndustrial=" + isIndustrial + "]";
	}

	public static class FluidCableCapabilityType extends ServerCableCapabilityType<FluidCableCapability> {
		@Override
		public FluidCableCapability create(Cable owningCable) {
			return new FluidCableCapability(this, owningCable);
		}
	}

}
