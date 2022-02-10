package theking530.staticpower.tileentities.components.fluids;

import java.util.HashSet;
import java.util.Optional;
import java.util.function.Predicate;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import theking530.api.IUpgradeItem.UpgradeType;
import theking530.staticpower.fluid.StaticPowerFluidTank;
import theking530.staticpower.network.StaticPowerMessageHandler;
import theking530.staticpower.tileentities.components.AbstractTileEntityComponent;
import theking530.staticpower.tileentities.components.ComponentUtilities;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticpower.tileentities.components.items.UpgradeInventoryComponent;
import theking530.staticpower.tileentities.components.items.UpgradeInventoryComponent.UpgradeItemWrapper;
import theking530.staticpower.tileentities.components.serialization.UpdateSerialize;

public class FluidTankComponent extends AbstractTileEntityComponent implements IFluidHandler, IFluidTank {
	public static final int FLUID_SYNC_MAX_DELTA = 5;

	@UpdateSerialize
	protected StaticPowerFluidTank fluidStorage;
	@UpdateSerialize
	protected int lastFluidStored;
	@UpdateSerialize
	protected long lastUpdateTime;
	@UpdateSerialize
	protected boolean canFill;
	@UpdateSerialize
	protected boolean canDrain;
	@UpdateSerialize
	protected boolean exposeAsCapability;
	@UpdateSerialize
	private float upgradeMultiplier;
	@UpdateSerialize
	private int defaultCapacity;
	@UpdateSerialize
	private boolean issueSyncPackets;

	private float lastUpdatePartialTick;

	protected final HashSet<MachineSideMode> capabilityExposeModes;
	private final FluidComponentCapabilityInterface capabilityInterface;
	private FluidStack lastSyncFluidStack;
	private float visualFillLevel;
	private UpgradeInventoryComponent upgradeInventory;

	public FluidTankComponent(String name, int capacity) {
		this(name, capacity, (fluid) -> true);
	}

	public FluidTankComponent(String name, int capacity, Predicate<FluidStack> fluidStackFilter) {
		super(name);
		fluidStorage = new StaticPowerFluidTank(capacity, fluidStackFilter);
		canFill = true;
		canDrain = true;
		issueSyncPackets = false;
		capabilityInterface = new FluidComponentCapabilityInterface();
		capabilityExposeModes = new HashSet<MachineSideMode>();
		this.lastSyncFluidStack = FluidStack.EMPTY;
		upgradeMultiplier = 1.0f;
		defaultCapacity = capacity;
		// By default, ALWAYS expose this side, except on disabled or never.
		for (MachineSideMode mode : MachineSideMode.values()) {
			if (mode != MachineSideMode.Disabled && mode != MachineSideMode.Never) {
				capabilityExposeModes.add(mode);
			}
		}
		lastUpdatePartialTick = 0.0f;
		exposeAsCapability = true;
	}

	@Override
	public void preProcessUpdate() {
		if (!getWorld().isClientSide) {
			// Check for upgrades.
			checkUpgrades();
		}
	}

	@Override
	public void postProcessUpdate() {
		if (!getWorld().isClientSide) {
			// Handle sync.
			if (issueSyncPackets) {
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
			}

			// Capture fluid metrics.
			fluidStorage.captureFluidMetrics();
		}
	}

	@Override
	public void updateBeforeRendering(float partialTicks) {
		if (visualFillLevel != getFluidAmount() && lastUpdatePartialTick != partialTicks) {
			float difference = visualFillLevel - getFluidAmount();
			visualFillLevel -= difference * (partialTicks / 10.0f);
			lastUpdatePartialTick = partialTicks;
		}
	}

	public FluidTankComponent setExposeAsCapability(boolean expose) {
		this.exposeAsCapability = expose;
		return this;
	}

	public FluidTankComponent setUpgradeInventory(UpgradeInventoryComponent inventory) {
		upgradeInventory = inventory;
		return this;
	}

	/**
	 * If set to true, packets will be sent to keep the values between the client
	 * and server in sync within a small threshold. This should only be set to true
	 * if the values from this component are required when rendering the block. GUI
	 * values are automatically synchronized.
	 * 
	 * @param enabled
	 * @return
	 */
	public FluidTankComponent setAutoSyncPacketsEnabled(boolean enabled) {
		issueSyncPackets = enabled;
		return this;
	}

	protected void checkUpgrades() {
		// Do nothing if there is no upgrade inventory.
		if (upgradeInventory == null) {
			return;
		}
		// Get the upgrade.
		UpgradeItemWrapper upgrade = upgradeInventory.getMaxTierItemForUpgradeType(UpgradeType.TANK);

		// If it is not valid, set the values back to the defaults. Otherwise, set the
		// new processing speeds.
		if (upgrade.isEmpty()) {
			upgradeMultiplier = 1.0f;
		} else {
			upgradeMultiplier = (float) (1.0f + (upgrade.getTier().tankCapacityUpgrade.get() * upgrade.getUpgradeWeight()));
		}

		// Set the capacity.
		getStorage().setCapacity((int) (defaultCapacity * upgradeMultiplier));
	}

	public float getVisualFillLevel() {
		return visualFillLevel / this.getCapacity();
	}

	public void setVisualFillLevel(float level) {
		this.visualFillLevel = level;
	}

	/**
	 * This method syncs the current state of this fluid tank component to all
	 * clients within a 64 block radius.
	 */
	public void syncToClient() {
		if (!getWorld().isClientSide) {
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
		return fluidStorage;
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

	public boolean isEmpty() {
		return getFluidAmount() == 0;
	}

	public boolean isFull() {
		return getFluidAmount() == getCapacity();
	}

	@Override
	public <T> LazyOptional<T> provideCapability(Capability<T> cap, Direction side) {
		if (isEnabled() && exposeAsCapability && cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
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
		return fluidStorage.getFluid();
	}

	@Override
	public int getFluidAmount() {
		return fluidStorage.getFluidAmount();
	}

	@Override
	public int getCapacity() {
		return fluidStorage.getCapacity();
	}

	@Override
	public boolean isFluidValid(FluidStack stack) {
		return fluidStorage.isFluidValid(stack);
	}

	@Override
	public int getTanks() {
		return fluidStorage.getTanks();
	}

	@Override
	public FluidStack getFluidInTank(int tank) {
		return fluidStorage.getFluidInTank(tank);
	}

	@Override
	public int getTankCapacity(int tank) {
		return fluidStorage.getTankCapacity(tank);
	}

	@Override
	public boolean isFluidValid(int tank, FluidStack stack) {
		return fluidStorage.isFluidValid(tank, stack);
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
		// Perform the fill, and then sync the tile entity if this was a real fill.
		int result = fluidStorage.fill(resource, action);

		return result;
	}

	@Override
	public FluidStack drain(FluidStack resource, FluidAction action) {
		// Perform the drain, and then sync the tile entity if this was a real drain.
		FluidStack result = fluidStorage.drain(resource, action);

		return result;
	}

	@Override
	public FluidStack drain(int maxDrain, FluidAction action) {
		// Perform the drain, and then sync the tile entity if this was a real drain.
		FluidStack result = fluidStorage.drain(maxDrain, action);

		return result;
	}

	public class FluidComponentCapabilityInterface implements IFluidHandler {
		private Direction direction;

		public void updateDirection(Direction direction) {
			this.direction = direction;
		}

		@Override
		public int getTanks() {
			if (!FluidTankComponent.this.isEnabled()) {
				return 0;
			}
			return FluidTankComponent.this.getTanks();
		}

		@Override
		public FluidStack getFluidInTank(int tank) {
			if (!FluidTankComponent.this.isEnabled()) {
				return FluidStack.EMPTY;
			}
			return FluidTankComponent.this.getFluidInTank(tank);
		}

		@Override
		public int getTankCapacity(int tank) {
			if (!FluidTankComponent.this.isEnabled()) {
				return 0;
			}
			return FluidTankComponent.this.getTankCapacity(tank);
		}

		@Override
		public boolean isFluidValid(int tank, FluidStack stack) {
			if (!FluidTankComponent.this.isEnabled()) {
				return false;
			}
			return FluidTankComponent.this.isFluidValid(stack);
		}

		@Override
		public int fill(FluidStack resource, FluidAction action) {
			if (!FluidTankComponent.this.isEnabled()) {
				return 0;
			}

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
			if (!FluidTankComponent.this.isEnabled()) {
				return FluidStack.EMPTY;
			}

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
			if (!FluidTankComponent.this.isEnabled()) {
				return FluidStack.EMPTY;
			}

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
