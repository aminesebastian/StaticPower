package theking530.staticpower.tileentities.components;

import java.util.Optional;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import theking530.staticpower.tileentities.utilities.MachineSideMode;

public class FluidTankComponent extends AbstractTileEntityComponent implements IFluidHandler, IFluidTank {

	protected FluidTank FluidStorage;

	protected int lastFluidStored;
	protected int fluidPerTick;
	protected boolean canFill;
	protected boolean canDrain;
	protected long lastUpdateTime;
	protected MachineSideMode operationMode;
	private final FluidComponentCapabilityInterface capabilityInterface;

	public FluidTankComponent(String name, int capacity, MachineSideMode operationMode) {
		super(name);
		this.operationMode = operationMode;
		FluidStorage = new FluidTank(capacity);
		canFill = true;
		canDrain = true;
		capabilityInterface = new FluidComponentCapabilityInterface();
	}

	public boolean getCanFill() {
		return canFill;
	}

	public void setCanFill(boolean canFill) {
		this.canFill = canFill;
	}

	public boolean getCanDrain() {
		return canDrain;
	}

	public void setCanDrain(boolean canDrain) {
		this.canDrain = canDrain;
	}

	public int getFluidRate() {
		return fluidPerTick;
	}

	@Override
	public void deserializeUpdateNbt(CompoundNBT nbt, boolean fromUpdate) {
		super.deserializeUpdateNbt(nbt, fromUpdate);
		FluidStorage.readFromNBT(nbt);
		fluidPerTick = nbt.getInt("PerTick");
	}

	@Override
	public CompoundNBT serializeUpdateNbt(CompoundNBT nbt, boolean fromUpdate) {
		super.serializeUpdateNbt(nbt, fromUpdate);
		FluidStorage.writeToNBT(nbt);

		long ticksSinceLastUpdate = Math.max(getTileEntity().getWorld().getGameTime() - lastUpdateTime, 1);
		int energyUsedPerTickSinceLastPacket = ((int) ((FluidStorage.getFluidAmount() - lastFluidStored) / ticksSinceLastUpdate));
		nbt.putInt("PerTick", energyUsedPerTickSinceLastPacket);
		lastUpdateTime = getTileEntity().getWorld().getGameTime();
		lastFluidStored = FluidStorage.getFluidAmount();
		return nbt;
	}

	@Override
	public <T> LazyOptional<T> provideCapability(Capability<T> cap, Direction side) {
		if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			// Check if the owner is side configurable. If it is, check to make sure it's
			// not disabled, if not, return the inventory.
			Optional<SideConfigurationComponent> sideConfig = ComponentUtilities.getComponent(SideConfigurationComponent.class, getTileEntity());
			if (side == null || !sideConfig.isPresent() || sideConfig.get().getWorldSpaceDirectionConfiguration(side) == operationMode) {
				return LazyOptional.of(() -> capabilityInterface).cast();
			}
		}
		return LazyOptional.empty();
	}

	@Override
	public FluidStack getFluid() {
		return FluidStorage.getFluid();
	}

	@Override
	public int getFluidAmount() {
		return FluidStorage.getFluidAmount();
	}

	@Override
	public int getCapacity() {
		return FluidStorage.getCapacity();
	}

	@Override
	public boolean isFluidValid(FluidStack stack) {
		return FluidStorage.isFluidValid(stack);
	}

	@Override
	public int getTanks() {
		return FluidStorage.getTanks();
	}

	@Override
	public FluidStack getFluidInTank(int tank) {
		return FluidStorage.getFluidInTank(tank);
	}

	@Override
	public int getTankCapacity(int tank) {
		return FluidStorage.getTankCapacity(tank);
	}

	@Override
	public boolean isFluidValid(int tank, FluidStack stack) {
		return FluidStorage.isFluidValid(tank, stack);
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
		return FluidStorage.fill(resource, action);
	}

	@Override
	public FluidStack drain(FluidStack resource, FluidAction action) {
		return FluidStorage.drain(resource, action);
	}

	@Override
	public FluidStack drain(int maxDrain, FluidAction action) {
		return FluidStorage.drain(maxDrain, action);
	}

	public class FluidComponentCapabilityInterface implements IFluidHandler {

		@Override
		public int getTanks() {
			return FluidTankComponent.this.getTanks();
		}

		@Override
		public FluidStack getFluidInTank(int tank) {
			return FluidTankComponent.this.getFluidInTank(tank);
		}

		@Override
		public int getTankCapacity(int tank) {
			return FluidTankComponent.this.getTankCapacity(tank);
		}

		@Override
		public boolean isFluidValid(int tank, FluidStack stack) {
			return FluidTankComponent.this.isFluidValid(stack);
		}

		@Override
		public int fill(FluidStack resource, FluidAction action) {
			if (!FluidTankComponent.this.getCanFill()) {
				return 0;
			}
			return FluidTankComponent.this.fill(resource, action);
		}

		@Override
		public FluidStack drain(FluidStack resource, FluidAction action) {
			if (!FluidTankComponent.this.getCanDrain()) {
				return FluidStack.EMPTY;
			}
			return FluidTankComponent.this.drain(resource, action);
		}

		@Override
		public FluidStack drain(int maxDrain, FluidAction action) {
			if (!FluidTankComponent.this.getCanDrain()) {
				return FluidStack.EMPTY;
			}
			return FluidTankComponent.this.drain(maxDrain, action);
		}
	}
}
