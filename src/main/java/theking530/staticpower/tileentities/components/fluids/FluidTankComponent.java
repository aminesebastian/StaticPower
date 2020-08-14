package theking530.staticpower.tileentities.components.fluids;

import java.util.HashSet;
import java.util.Optional;
import java.util.function.Predicate;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import theking530.staticpower.fluids.StaticPowerFluidTank;
import theking530.staticpower.network.StaticPowerMessageHandler;
import theking530.staticpower.tileentities.components.AbstractTileEntityComponent;
import theking530.staticpower.tileentities.components.ComponentUtilities;
import theking530.staticpower.tileentities.components.control.SideConfigurationComponent;
import theking530.staticpower.tileentities.utilities.MachineSideMode;

public class FluidTankComponent extends AbstractTileEntityComponent implements IFluidHandler, IFluidTank {
	public static final int FLUID_SYNC_MAX_DELTA = 1;
	protected StaticPowerFluidTank FluidStorage;

	protected int lastFluidStored;
	protected int fluidPerTick;
	protected boolean canFill;
	protected boolean canDrain;
	protected long lastUpdateTime;
	protected final HashSet<MachineSideMode> capabilityExposeModes;
	private final FluidComponentCapabilityInterface capabilityInterface;
	private FluidStack lastSyncFluidStack;
	private float visualFillLevel;

	public FluidTankComponent(String name, int capacity) {
		this(name, capacity, (fluid) -> true);
	}

	public FluidTankComponent(String name, int capacity, Predicate<FluidStack> fluidStackFilter) {
		super(name);
		FluidStorage = new StaticPowerFluidTank(capacity, fluidStackFilter);
		canFill = true;
		canDrain = true;
		capabilityInterface = new FluidComponentCapabilityInterface();
		capabilityExposeModes = new HashSet<MachineSideMode>();
		this.lastSyncFluidStack = FluidStack.EMPTY;

		// By default, ALWAYS expose this side, except on disabled or never.
		for (MachineSideMode mode : MachineSideMode.values()) {
			if (mode != MachineSideMode.Disabled && mode != MachineSideMode.Never) {
				capabilityExposeModes.add(mode);
			}
		}
	}

	@Override
	public void postProcessUpdate() {
		if (!getWorld().isRemote) {
			// Get the current delta between the amount of power we have and the power we
			// had last tick.
			int delta = Math.abs(getFluidAmount() - lastSyncFluidStack.getAmount());

			// Determine if we should sync.
			boolean shouldSync = delta > FLUID_SYNC_MAX_DELTA;
			shouldSync |= !lastSyncFluidStack.isFluidEqual(this.getFluid());
			shouldSync |= getFluidAmount() == 0 && lastSyncFluidStack.getAmount() != 0;
			shouldSync |= getFluidAmount() == getCapacity() && lastSyncFluidStack.getAmount() != getCapacity();

			// If we should sync, perform the sync.
			if (shouldSync) {
				lastSyncFluidStack = getFluid().copy();
				syncToClient();
			}

			FluidStorage.captureFluidMetrics();
		}
	}

	@Override
	public void updateBeforeRendering(float partialTicks) {
		if (visualFillLevel != getFluidAmount()) {
			float difference = visualFillLevel - getFluidAmount();
			visualFillLevel -= difference * (partialTicks / 20.0f);

		}
	}

	public float getVisualFillLevel() {
		return visualFillLevel / this.getCapacity();
	}

	/**
	 * This method syncs the current state of this fluid tank component to all
	 * clients within a 64 block radius.
	 */
	public void syncToClient() {
		if (!getWorld().isRemote) {
			PacketFluidTankComponent syncPacket = new PacketFluidTankComponent(this, getPos(), this.getComponentName());
			StaticPowerMessageHandler.sendMessageToPlayerInArea(StaticPowerMessageHandler.MAIN_PACKET_CHANNEL, getWorld(), getPos(), 64, syncPacket);
		} else {
			throw new RuntimeException("This method should only be called on the server!");
		}

	}

	/**
	 * Sets modes that will expose this tank as a capability. This is useful in the
	 * case of the TankTileEntity, where it can be connected to in Regular, Input,
	 * or Output modes.
	 * 
	 * @param mode
	 * @return
	 */
	public FluidTankComponent setCapabilityExposedModes(MachineSideMode... modes) {
		capabilityExposeModes.clear();
		for (MachineSideMode mode : modes) {
			capabilityExposeModes.add(mode);
		}
		return this;
	}

	public boolean getCanFill() {
		return canFill;
	}

	public StaticPowerFluidTank getStorage() {
		return FluidStorage;
	}

	/**
	 * Sets this tank's ability to fill. Only affects the tank interface exposed
	 * through the capability. You can still insert through direct reference.
	 * 
	 * @param canFill
	 */
	public FluidTankComponent setCanFill(boolean canFill) {
		this.canFill = canFill;
		return this;
	}

	public boolean getCanDrain() {
		return canDrain;
	}

	/**
	 * Sets this tank's ability to drain. Only affects the tank interface exposed
	 * through the capability. You can drain insert through direct reference.
	 * 
	 * @param canDrain
	 */
	public FluidTankComponent setCanDrain(boolean canDrain) {
		this.canDrain = canDrain;
		return this;
	}

	public int getFluidRate() {
		return fluidPerTick;
	}

	public boolean isEmpty() {
		return getFluidAmount() == 0;
	}

	public boolean isFull() {
		return getFluidAmount() == getCapacity();
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
			if (side == null || !sideConfig.isPresent() || capabilityExposeModes.contains(sideConfig.get().getWorldSpaceDirectionConfiguration(side))) {
				capabilityInterface.updateDirection(side);
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
		// Perform the fill, and then sync the tile entity if this was a real fill.
		int result = FluidStorage.fill(resource, action);

		return result;
	}

	@Override
	public FluidStack drain(FluidStack resource, FluidAction action) {
		// Perform the drain, and then sync the tile entity if this was a real drain.
		FluidStack result = FluidStorage.drain(resource, action);

		return result;
	}

	@Override
	public FluidStack drain(int maxDrain, FluidAction action) {
		// Perform the drain, and then sync the tile entity if this was a real drain.
		FluidStack result = FluidStorage.drain(maxDrain, action);

		return result;
	}

	public class FluidComponentCapabilityInterface implements IFluidHandler {
		private Direction direction;

		public void updateDirection(Direction direction) {
			this.direction = direction;
		}

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

			Optional<SideConfigurationComponent> sideConfig = ComponentUtilities.getComponent(SideConfigurationComponent.class, getTileEntity());
			if (sideConfig.isPresent() && sideConfig.get().getWorldSpaceDirectionConfiguration(direction).isOutputMode()) {
				return 0;
			}
			return FluidTankComponent.this.fill(resource, action);
		}

		@Override
		public FluidStack drain(FluidStack resource, FluidAction action) {
			if (!FluidTankComponent.this.getCanDrain()) {
				return FluidStack.EMPTY;
			}

			Optional<SideConfigurationComponent> sideConfig = ComponentUtilities.getComponent(SideConfigurationComponent.class, getTileEntity());
			if (sideConfig.isPresent() && sideConfig.get().getWorldSpaceDirectionConfiguration(direction).isInputMode()) {
				return FluidStack.EMPTY;
			}

			return FluidTankComponent.this.drain(resource, action);
		}

		@Override
		public FluidStack drain(int maxDrain, FluidAction action) {
			if (!FluidTankComponent.this.getCanDrain()) {
				return FluidStack.EMPTY;
			}

			Optional<SideConfigurationComponent> sideConfig = ComponentUtilities.getComponent(SideConfigurationComponent.class, getTileEntity());
			if (sideConfig.isPresent() && sideConfig.get().getWorldSpaceDirectionConfiguration(direction).isInputMode()) {
				return FluidStack.EMPTY;
			}

			return FluidTankComponent.this.drain(maxDrain, action);
		}
	}
}
