package theking530.staticcore.blockentity.components.fluids;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import theking530.staticcore.blockentity.components.AbstractBlockEntityComponent;
import theking530.staticcore.blockentity.components.ComponentUtilities;
import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticcore.blockentity.components.items.UpgradeInventoryComponent;
import theking530.staticcore.blockentity.components.items.UpgradeInventoryComponent.UpgradeItemWrapper;
import theking530.staticcore.blockentity.components.serialization.UpdateSerialize;
import theking530.staticcore.fluid.ISidedFluidHandler;
import theking530.staticcore.fluid.SidedFluidHandlerCapabilityWrapper;
import theking530.staticcore.fluid.StaticPowerFluidTank;
import theking530.staticcore.init.StaticCoreUpgradeTypes;
import theking530.staticcore.network.StaticCoreMessageHandler;

public class FluidTankComponent extends AbstractBlockEntityComponent implements ISidedFluidHandler, IFluidTank {
	public static final int FLUID_SYNC_MAX_DELTA = 50;
	public static final int FLUID_SYNC_MAX_TICKS = 10;

	@UpdateSerialize
	protected StaticPowerFluidTank fluidStorage;
	@UpdateSerialize
	protected int lastFluidStored;
	@UpdateSerialize
	protected long lastUpdateTime;
	@UpdateSerialize
	protected boolean exposeAsCapability;
	@UpdateSerialize
	private float upgradeMultiplier;
	@UpdateSerialize
	private int defaultCapacity;
	protected IFluidTankComponentFilter fluidFilter;

	private final SidedFluidHandlerCapabilityWrapper capabilityWrapper;
	private final Set<MachineSideMode> capabilityExposedModes;

	private boolean issueSyncPackets;
	private int timeSinceLastSync;
	private float lastUpdatePartialTick;
	private FluidStack lastSyncFluidStack;
	private float visualFillLevel;

	private UpgradeInventoryComponent upgradeInventory;

	public FluidTankComponent(String name, int capacity) {
		this(name, capacity, (fluid) -> true);
	}

	public FluidTankComponent(String name, int capacity, Predicate<FluidStack> fluidStackFilter) {
		super(name);
		fluidStorage = new StaticPowerFluidTank(capacity, fluidStackFilter);
		capabilityWrapper = new SidedFluidHandlerCapabilityWrapper(this);
		capabilityExposedModes = new HashSet<>();
		issueSyncPackets = true;
		this.lastSyncFluidStack = FluidStack.EMPTY;
		upgradeMultiplier = 1.0f;
		defaultCapacity = capacity;
		lastUpdatePartialTick = 0.0f;
		exposeAsCapability = true;
	}

	@SuppressWarnings("resource")
	@Override
	public void preProcessUpdate() {
		if (!getLevel().isClientSide) {
			// Check for upgrades.
			checkUpgrades();
		}
	}

	@SuppressWarnings("resource")
	@Override
	public void postProcessUpdate() {
		if (!getLevel().isClientSide) {
			// Handle sync.
			if (issueSyncPackets) {
				// Get the current delta between the amount of power we have and the power we
				// had last tick.
				int delta = Math.abs(fluidStorage.getFluidAmount() - fluidStorage.getFluidAmount());

				// Determine if we should sync.
				boolean shouldSync = delta >= FLUID_SYNC_MAX_DELTA;
				if (!shouldSync) {
					shouldSync = !lastSyncFluidStack.isFluidEqual(fluidStorage.getFluid());
				}
				if (!shouldSync) {
					shouldSync = fluidStorage.getFluidAmount() == 0 && lastSyncFluidStack.getAmount() != 0;
				}
				if (!shouldSync) {
					shouldSync = fluidStorage.getFluidAmount() == fluidStorage.getCapacity() && lastSyncFluidStack.getAmount() != fluidStorage.getCapacity();
				}
				if (!shouldSync) {
					shouldSync = timeSinceLastSync >= FLUID_SYNC_MAX_TICKS;
				}

				// If we should sync, perform the sync.
				if (shouldSync) {
					lastSyncFluidStack = fluidStorage.getFluid().copy();
					timeSinceLastSync = 0;
					syncToClient();
				} else {
					timeSinceLastSync++;
				}
			}

			// Capture fluid metrics.
			fluidStorage.captureFluidMetrics();
		}
	}

	@Override
	public void updateBeforeRendering(float partialTicks) {
		if (visualFillLevel != fluidStorage.getFluidAmount() && lastUpdatePartialTick != partialTicks) {
			float difference = visualFillLevel - fluidStorage.getFluidAmount();
			visualFillLevel -= difference * (partialTicks / 5.0f);
			lastUpdatePartialTick = partialTicks;
		}
	}

	public FluidTankComponent setExposeAsCapability(boolean expose) {
		this.exposeAsCapability = expose;
		return this;
	}

	public FluidTankComponent setFluidTankFilter(IFluidTankComponentFilter filter) {
		this.fluidFilter = filter;
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

	public FluidTankComponent setCapabilityExposedModes(MachineSideMode... modes) {
		capabilityExposedModes.clear();
		for (MachineSideMode mode : modes) {
			capabilityExposedModes.add(mode);
		}
		return this;
	}

	protected void checkUpgrades() {
		// Do nothing if there is no upgrade inventory.
		if (upgradeInventory == null) {
			return;
		}
		// Get the upgrade.
		UpgradeItemWrapper<Double> upgrade = upgradeInventory.getMaxTierItemForUpgradeType(StaticCoreUpgradeTypes.TANK_CAPACITY.get());

		// If it is not valid, set the values back to the defaults. Otherwise, set the
		// new processing speeds.
		if (upgrade.isEmpty()) {
			upgradeMultiplier = 1.0f;
		} else {
			upgradeMultiplier = (float) (1.0f + (upgrade.getUpgradeValue() * upgrade.getUpgradeWeight()));
		}

		// Set the capacity.
		getStorage().setCapacity((int) (defaultCapacity * upgradeMultiplier));

		// Handle the void upgrade.
		fluidStorage.setVoidExcess(upgradeInventory.hasUpgradeOfType(StaticCoreUpgradeTypes.VOID.get()));
	}

	public float getVisualFillLevel() {
		return visualFillLevel / fluidStorage.getCapacity();
	}

	public void setVisualFillLevel(float level) {
		this.visualFillLevel = level;
	}

	/**
	 * This method syncs the current state of this fluid tank component to all
	 * clients within a 64 block radius.
	 */
	@SuppressWarnings("resource")
	public void syncToClient() {
		if (!getLevel().isClientSide) {
			PacketFluidTankComponent syncPacket = new PacketFluidTankComponent(this, getPos(), this.getComponentName());
			StaticCoreMessageHandler.sendMessageToPlayerInArea(StaticCoreMessageHandler.MAIN_PACKET_CHANNEL, getLevel(), getPos(), 64, syncPacket);
		} else {
			throw new RuntimeException("This method should only be called on the server!");
		}

	}

	public StaticPowerFluidTank getStorage() {
		return fluidStorage;
	}

	public boolean isEmpty() {
		return fluidStorage.getFluidAmount() == 0;
	}

	public boolean isFull() {
		return fluidStorage.getFluidAmount() == fluidStorage.getCapacity();
	}

	@Override
	public <T> LazyOptional<T> provideCapability(Capability<T> cap, Direction side) {
		if (!isEnabled()) {
			return LazyOptional.empty();
		}

		if (cap != ForgeCapabilities.FLUID_HANDLER) {
			return LazyOptional.empty();
		}

		// Still expose even if exposeAsCapability is false if the side is null. This is
		// used for JADE and other overlays.
		if (side == null) {
			return manuallyGetCapability(cap, side);
		}

		if (!exposeAsCapability) {
			return LazyOptional.empty();
		}

		Optional<SideConfigurationComponent> sideConfig = ComponentUtilities.getComponent(SideConfigurationComponent.class, getBlockEntity());
		if (!sideConfig.isPresent()) {
			return manuallyGetCapability(cap, side);
		}

		if (!sideConfig.get().getWorldSpaceDirectionConfiguration(side).isDisabledMode()) {
			if (capabilityExposedModes.contains(sideConfig.get().getWorldSpaceDirectionConfiguration(side))) {
				return manuallyGetCapability(cap, side);
			}
		}

		return LazyOptional.empty();
	}

	public <T> LazyOptional<T> manuallyGetCapability(Capability<T> cap, Direction side) {
		if (cap == ForgeCapabilities.FLUID_HANDLER) {
			return LazyOptional.of(() -> capabilityWrapper.get(side)).cast();
		}
		return LazyOptional.empty();
	}

	@Override
	public int getTanks(Direction direction) {
		return getTanks();
	}

	@Override
	public FluidStack getFluidInTank(Direction direction, int tank) {
		return getFluidInTank(tank);
	}

	@Override
	public int getTankCapacity(Direction direction, int tank) {
		return getTankCapacity(tank);
	}

	@Override
	public boolean isFluidValid(Direction direction, int tank, FluidStack stack) {
		return isFluidValid(tank, stack);
	}

	@Override
	public int fill(Direction direction, FluidStack resource, FluidAction action) {
		if (fluidFilter != null) {
			if (!fluidFilter.canFill(direction, resource)) {
				return 0;
			}
		}

		Optional<SideConfigurationComponent> sideConfig = ComponentUtilities.getComponent(SideConfigurationComponent.class, getBlockEntity());
		if (sideConfig.isPresent()) {
			if (!sideConfig.get().getWorldSpaceDirectionConfiguration(direction).isInputMode()) {
				return 0;
			}
		}
		return fill(resource, action);
	}

	@Override
	public FluidStack drain(Direction direction, FluidStack resource, FluidAction action) {
		if (fluidFilter != null) {
			if (!fluidFilter.canDrain(direction, resource)) {
				return FluidStack.EMPTY;
			}
		}

		Optional<SideConfigurationComponent> sideConfig = ComponentUtilities.getComponent(SideConfigurationComponent.class, getBlockEntity());
		if (sideConfig.isPresent()) {
			if (!sideConfig.get().getWorldSpaceDirectionConfiguration(direction).isOutputMode()) {
				return FluidStack.EMPTY;
			}
		}
		return drain(resource, action);
	}

	@Override
	public FluidStack drain(Direction direction, int maxDrain, FluidAction action) {
		if (fluidFilter != null) {
			if (!fluidFilter.canDrain(direction, maxDrain)) {
				return FluidStack.EMPTY;
			}
		}

		Optional<SideConfigurationComponent> sideConfig = ComponentUtilities.getComponent(SideConfigurationComponent.class, getBlockEntity());
		if (sideConfig.isPresent()) {
			if (!sideConfig.get().getWorldSpaceDirectionConfiguration(direction).isOutputMode()) {
				return FluidStack.EMPTY;
			}
		}
		return drain(maxDrain, action);
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
		return fluidStorage.isFluidValid(stack);
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
		return fluidStorage.fill(resource, action);
	}

	@Override
	public FluidStack drain(FluidStack resource, FluidAction action) {
		return fluidStorage.drain(resource, action);
	}

	@Override
	public FluidStack drain(int maxDrain, FluidAction action) {
		return fluidStorage.drain(maxDrain, action);
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
}
