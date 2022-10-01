package theking530.staticpower.cables.fluid;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import theking530.staticcore.cablenetwork.ServerCable;
import theking530.staticcore.cablenetwork.capabilities.ServerCableCapability;
import theking530.staticcore.cablenetwork.capabilities.ServerCableCapabilityType;
import theking530.staticpower.fluid.StaticPowerFluidTank;

public class FluidCableCapability extends ServerCableCapability {
	private StaticPowerFluidTank fluidStorage;
	private float pressure;
	private int transferRate;
	private boolean isIndustrial;

	public FluidCableCapability(ServerCableCapabilityType type, ServerCable owningCable) {
		super(type, owningCable);
		this.fluidStorage = new StaticPowerFluidTank(0);
	}

	public void initialize(int capacity, int transferRate, boolean isIndustrial) {
		this.isIndustrial = isIndustrial;
		this.transferRate = transferRate;
		this.fluidStorage.setCapacity(capacity);
	}

	public StaticPowerFluidTank getFluidStorage() {
		return this.fluidStorage;
	}

	public float getPressure() {
		return pressure;
	}

	public void setPressure(float pressure) {
		this.pressure = Math.max(0, pressure);
	}

	public int getTransferRate() {
		return transferRate;
	}

	public boolean isIndustrial() {
		return isIndustrial;
	}

	public int fill(FluidStack fluid, float pressure, FluidAction action) {
		if (fluid.isEmpty()) {
			return 0;
		}

		FluidStack limitedStack = fluid.copy();
		limitedStack.setAmount(Math.min(getTransferRate(), fluid.getAmount()));
		int filled = fluidStorage.fill(limitedStack, action);
		if (action == FluidAction.EXECUTE) {
			setPressure(Math.max(getPressure(), pressure));
		}
		return filled;
	}

	public FluidStack drain(int amount, FluidAction action) {
		return fluidStorage.drain(amount, action);
	}

	@Override
	public void save(CompoundTag tag) {
		tag.put("f", this.fluidStorage.serializeNBT());
		tag.putFloat("p", pressure);
		tag.putInt("t", transferRate);
		tag.putBoolean("i", isIndustrial);
	}

	@Override
	public void load(CompoundTag tag) {
		fluidStorage.deserializeNBT(tag.getCompound("f"));
		pressure = tag.getFloat("p");
		transferRate = tag.getInt("t");
		isIndustrial = tag.getBoolean("i");
	}

	@Override
	public String toString() {
		return "FluidCableCapability [fluidStorage=" + fluidStorage + ", pressure=" + pressure + ", transferRate=" + transferRate + ", isIndustrial=" + isIndustrial + "]";
	}

	public static class FluidCableCapabilityType extends ServerCableCapabilityType {
		@Override
		public FluidCableCapability create(ServerCable owningCable) {
			return new FluidCableCapability(this, owningCable);
		}
	}
}
