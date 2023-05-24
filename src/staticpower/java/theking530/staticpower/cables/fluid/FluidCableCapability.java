package theking530.staticpower.cables.fluid;

import org.jetbrains.annotations.NotNull;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import theking530.staticcore.cablenetwork.Cable;
import theking530.staticcore.cablenetwork.capabilities.ServerCableCapability;
import theking530.staticcore.cablenetwork.capabilities.ServerCableCapabilityType;
import theking530.staticpower.cables.fluid.BlockEntityFluidCable.FluidPipeType;

public class FluidCableCapability extends ServerCableCapability implements IFluidTank {
	public static final float INTERPOLATION_RATE = 0.5f;

	private FluidStack containedFluid;
	private float headPressure;
	private float targetPressure;
	private int capacity;
	private int transferRate;
	private PipePressureProperties pressureProperties;
	private FluidPipeType type;

	protected FluidCableCapability(ServerCableCapabilityType<?> type, Cable owningCable) {
		super(type, owningCable);
		this.containedFluid = FluidStack.EMPTY.copy();
		this.headPressure = 0;
		this.targetPressure = 0;
		this.pressureProperties = new PipePressureProperties(0, 0, 0);
	}

	public void initialize(int capacity, int transferRate, PipePressureProperties pressureProperties, FluidPipeType type) {
		this.type = type;
		this.transferRate = transferRate;
		this.capacity = capacity;
		this.pressureProperties = pressureProperties;
	}

	public PipePressureProperties getPressureProperties() {
		return pressureProperties;
	}

	public FluidStack getFluidStorage() {
		return this.containedFluid;
	}

	public int getTransferRate() {
		return transferRate;
	}

	public FluidPipeType getPipeType() {
		return type;
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

	public void setTargetHeadPressure(float targetRate) {
		targetPressure = targetRate;
	}

	public float getTargetHeadPressure() {
		return targetPressure;
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
		float newValue = (1.0f - 0.5f) * headPressure + 0.5f * targetPressure;
		if (newValue < 0.1f) {
			newValue = 0;
		}
		headPressure = newValue;
	}

	@Override
	public void save(CompoundTag tag) {
		containedFluid.writeToNBT(tag);
		tag.putInt("Capacity", capacity);
		tag.putInt("TransferRate", transferRate);
		tag.putByte("Type", (byte) type.ordinal());
		tag.put("PressureProperties", pressureProperties.serialize());
	}

	@Override
	public void load(CompoundTag tag) {
		containedFluid = FluidStack.loadFluidStackFromNBT(tag);
		capacity = tag.getInt("Capacity");
		transferRate = tag.getInt("TransferRate");
		type = FluidPipeType.values()[tag.getByte("Type")];
		pressureProperties.deserialize(tag.getCompound("PressureProperties"));
	}

	@Override
	public String toString() {
		return "FluidCableCapability [accumulatedFluid=" + containedFluid + ", headPressure=" + headPressure + ", targetPressure=" + targetPressure + ", capacity=" + capacity
				+ ", transferRate=" + transferRate + ", type=" + type + "]";
	}

	public static class FluidCableCapabilityType extends ServerCableCapabilityType<FluidCableCapability> {
		@Override
		public FluidCableCapability create(Cable owningCable) {
			return new FluidCableCapability(this, owningCable);
		}
	}

}
