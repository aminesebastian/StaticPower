package theking530.staticpower.cables.fluid;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import theking530.staticcore.cablenetwork.Cable;
import theking530.staticcore.cablenetwork.capabilities.ServerCableCapability;
import theking530.staticcore.cablenetwork.capabilities.ServerCableCapabilityType;
import theking530.staticcore.utilities.SDMath;
import theking530.staticpower.fluid.StaticPowerFluidTank;

public class FluidCableCapability extends ServerCableCapability {
	public static float CABLE_FRICTION = 1.0f;

	private StaticPowerFluidTank fluidStorage;
	private int transferRate;
	private boolean isIndustrial;
	private final Map<Direction, Float> sidedFlowMap;

	public FluidCableCapability(ServerCableCapabilityType type, Cable owningCable) {
		super(type, owningCable);
		this.fluidStorage = new StaticPowerFluidTank(0);
		sidedFlowMap = new HashMap<>();
		for (Direction dir : Direction.values()) {
			sidedFlowMap.put(dir, 0.0f);
		}
	}

	public void initialize(int capacity, int transferRate, boolean isIndustrial) {
		this.isIndustrial = isIndustrial;
		this.transferRate = transferRate;
		this.fluidStorage.setCapacity(capacity);
	}

	public StaticPowerFluidTank getFluidStorage() {
		return this.fluidStorage;
	}

	public int getPressure() {
		return (int) (16 * (float) getFluidStorage().getFluidAmount() / getFluidStorage().getCapacity());
	}

	public int getTransferRate() {
		return transferRate;
	}

	public boolean isIndustrial() {
		return isIndustrial;
	}

	public int fill(Direction fromDir, float flowRate, FluidStack fluid, FluidAction action) {
		if (fluid.isEmpty()) {
			return 0;
		}

		FluidStack limitedStack = fluid.copy();
		limitedStack.setAmount(Math.min(fluid.getAmount(), getTransferRate()));

		if (action == FluidAction.EXECUTE) {
			setFlowRateOfDirection(fromDir, flowRate);
		}

		return fluidStorage.fill(fluid, action);
	}

	public FluidStack drain(int amount, FluidAction action) {
		return fluidStorage.drain(amount, action);
	}

	public void setFlowRateOfDirection(Direction direction, float flowRate) {
		float newRate = SDMath.clamp(flowRate, 0, 32);
		if (Math.abs(newRate) <= 0.1f) {
			newRate = 0;
		}
		sidedFlowMap.put(direction, newRate);
	}

	public float getFlowRateOfDirection(Direction direction) {
		return sidedFlowMap.get(direction);
	}

	public Map<Direction, Float> getFlowMap() {
		return sidedFlowMap;
	}

	@Override
	public void save(CompoundTag tag) {
		tag.put("f", this.fluidStorage.serializeNBT());
		tag.putInt("t", transferRate);
		tag.putBoolean("i", isIndustrial);
	}

	@Override
	public void load(CompoundTag tag) {
		fluidStorage.deserializeNBT(tag.getCompound("f"));
		transferRate = tag.getInt("t");
		isIndustrial = tag.getBoolean("i");
	}

	@Override
	public String toString() {
		return "FluidCableCapability [fluidStorage=" + fluidStorage + ", pressure=" + getPressure() + ", transferRate=" + transferRate + ", isIndustrial=" + isIndustrial + "]";
	}

	public static class FluidCableCapabilityType extends ServerCableCapabilityType {
		@Override
		public FluidCableCapability create(Cable owningCable) {
			return new FluidCableCapability(this, owningCable);
		}
	}
}
