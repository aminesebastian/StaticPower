package theking530.staticpower.tileentities.components.power;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import theking530.api.IUpgradeItem.UpgradeType;
import theking530.api.power.CapabilityStaticVolt;
import theking530.api.power.IStaticVoltHandler;
import theking530.api.power.StaticVoltAutoConverter;
import theking530.api.power.StaticVoltHandler;
import theking530.staticcore.utilities.TriFunction;
import theking530.staticpower.network.StaticPowerMessageHandler;
import theking530.staticpower.tileentities.components.AbstractTileEntityComponent;
import theking530.staticpower.tileentities.components.items.UpgradeInventoryComponent;
import theking530.staticpower.tileentities.components.items.UpgradeInventoryComponent.UpgradeItemWrapper;
import theking530.staticpower.tileentities.components.serialization.UpdateSerialize;

public class EnergyStorageComponent extends AbstractTileEntityComponent {
	public enum EnergyManipulationAction {
		PROVIDE, RECIEVE
	}

	public static final int ENERGY_SYNC_MAX_DELTA = 10;
	@UpdateSerialize
	protected final StaticVoltHandler EnergyStorage;
	@UpdateSerialize
	private float powerCapacityUpgradeMultiplier;
	@UpdateSerialize
	private float powerIOUpgradeMultiplier;
	@UpdateSerialize
	private long defaultCapacity;
	@UpdateSerialize
	private long defaultMaxInput;
	@UpdateSerialize
	private long defaultMaxOutput;
	@UpdateSerialize
	private boolean issueSyncPackets;

	private final Map<Direction, FECapabilityAccess> feAccessors;
	private final Map<Direction, SVCapabilityAccess> staticVoltAccessors;
	private final StaticVoltAutoConverter energyInterface;

	protected TriFunction<Long, Direction, EnergyManipulationAction, Boolean> filter;
	private long lastSyncEnergy;
	private UpgradeInventoryComponent upgradeInventory;

	public EnergyStorageComponent(String name, long capacity) {
		this(name, capacity, Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

	public EnergyStorageComponent(String name, long capacity, long maxInput) {
		this(name, capacity, maxInput, 0);
	}

	public EnergyStorageComponent(String name, long capacity, long maxInput, long maxExtract) {
		super(name);
		EnergyStorage = new StaticVoltHandler(capacity, maxInput, maxExtract);

		// Create the accessors.
		feAccessors = new HashMap<Direction, FECapabilityAccess>();
		staticVoltAccessors = new HashMap<Direction, SVCapabilityAccess>();
		for (Direction dir : Direction.values()) {
			feAccessors.put(dir, new FECapabilityAccess(dir));
			staticVoltAccessors.put(dir, new SVCapabilityAccess(dir));
		}

		defaultCapacity = capacity;
		powerCapacityUpgradeMultiplier = 1.0f;
		powerIOUpgradeMultiplier = 1.0f;
		issueSyncPackets = false;
		defaultMaxInput = maxInput;
		defaultMaxOutput = maxExtract;
		// Create the interface.
		energyInterface = new StaticVoltAutoConverter(EnergyStorage);
	}

	@Override
	public void preProcessUpdate() {
		if (!getWorld().isRemote) {
			// Check for upgrades.
			checkUpgrades();
		}
	}

	@Override
	public void postProcessUpdate() {
		if (!getWorld().isRemote) {
			// Handle sync.
			if (issueSyncPackets) {
				// Get the current delta between the amount of power we have and the power we
				// had last tick.
				long delta = Math.abs(EnergyStorage.getStoredPower() - lastSyncEnergy);

				// Determine if we should sync.
				boolean shouldSync = delta > ENERGY_SYNC_MAX_DELTA;
				shouldSync |= EnergyStorage.getStoredPower() == 0 && lastSyncEnergy != 0;
				shouldSync |= EnergyStorage.getStoredPower() == EnergyStorage.getCapacity() && lastSyncEnergy != EnergyStorage.getCapacity();

				// If we should sync, perform the sync.
				if (shouldSync) {
					lastSyncEnergy = EnergyStorage.getStoredPower();
					syncToClient();
				}
			}

			EnergyStorage.captureEnergyMetric();
		}
	}

	public EnergyStorageComponent setMaxInput(long maxInput) {
		defaultMaxInput = maxInput;
		if (upgradeInventory == null) {
			EnergyStorage.setMaxReceive(defaultMaxInput);
		}
		return this;
	}

	public EnergyStorageComponent setMaxOutput(long maxOutput) {
		defaultMaxOutput = maxOutput;
		if (upgradeInventory == null) {
			EnergyStorage.setMaxExtract(defaultMaxOutput);
		}
		return this;
	}

	public long getDefaultMaxInput() {
		return defaultMaxInput;
	}

	public long getDefaultMaxOutput() {
		return defaultMaxOutput;
	}

	public long getDefaultCapacity() {
		return defaultCapacity;
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
	public EnergyStorageComponent setAutoSyncPacketsEnabled(boolean enabled) {
		issueSyncPackets = enabled;
		return this;
	}

	public EnergyStorageComponent setDefaultCapacity(int defaultCapacity) {
		this.defaultCapacity = defaultCapacity;
		return this;
	}

	public EnergyStorageComponent setUpgradeInventory(UpgradeInventoryComponent inventory) {
		upgradeInventory = inventory;
		return this;
	}

	protected void checkUpgrades() {
		// Do nothing if there is no upgrade inventory.
		if (upgradeInventory == null) {
			return;
		}
		// Get the upgrade.
		UpgradeItemWrapper upgrade = upgradeInventory.getMaxTierItemForUpgradeType(UpgradeType.POWER);

		// If it is not valid, set the values back to the defaults. Otherwise, set the
		// new processing speeds.
		if (upgrade.isEmpty()) {
			powerCapacityUpgradeMultiplier = 1.0f;
			powerIOUpgradeMultiplier = 1.0f;
		} else {
			powerCapacityUpgradeMultiplier = (float) (1.0f + (upgrade.getTier().powerUpgrade.get() * upgrade.getUpgradeWeight()));
			powerIOUpgradeMultiplier = (float) (1.0f + (upgrade.getTier().powerIOUpgrade.get() * upgrade.getUpgradeWeight()));
		}

		// Set the new values.
		getStorage().setCapacity((int) (defaultCapacity * powerCapacityUpgradeMultiplier));
		getStorage().setMaxExtract(Math.min(getStorage().getCapacity(), (long) (defaultMaxOutput * powerIOUpgradeMultiplier)));
		getStorage().setMaxReceive(Math.min(getStorage().getCapacity(), (long) (defaultMaxInput * powerIOUpgradeMultiplier)));
	}

	/**
	 * Gets the raw energy storage object.
	 * 
	 * @return
	 */
	public StaticVoltHandler getStorage() {
		return EnergyStorage;
	}

	/**
	 * Returns true if this energy component has >= the amount of power that was
	 * passed.
	 * 
	 * @param power
	 * @return
	 */
	public boolean hasEnoughPower(long power) {
		return EnergyStorage.getStoredPower() >= power;
	}

	/**
	 * If this storage component contains at least the provided amount of power, it
	 * will drain that amount and return true. Otherwise, it will do nothing and
	 * return false. This ignores the extract rate.
	 * 
	 * @param power The amount of power to drain.
	 * @return True if the provided amount of power was drained, false otherwise.
	 */
	public boolean useBulkPower(long power) {
		if (hasEnoughPower(power)) {
			long maxExtract = getStorage().getMaxDrain();
			getStorage().setMaxExtract(Integer.MAX_VALUE);
			getStorage().drainPower(power, false);
			getStorage().setMaxExtract(maxExtract);
			return true;
		}
		return false;
	}

	/**
	 * Returns true if this energy component can fully accept the amount passed.
	 * 
	 * @param power The amount of power test this component for.
	 * @return
	 */
	public boolean canAcceptPower(long power) {
		return EnergyStorage.getStoredPower() + power <= EnergyStorage.getCapacity();
	}

	/**
	 * If this storage can fully receive the amount of power passed, it will receive
	 * that amount and return true. Otherwise, it will do nothing and return false.
	 * 
	 * @param power The amount of power to receive.
	 * @return True if the provided amount of power was received, false otherwise.
	 */
	public boolean addPower(long power) {
		if (canAcceptPower(power)) {
			getStorage().receivePower(power, false);
			return true;
		}
		return false;
	}

	/**
	 * Returns true if this component has >0 SV.
	 * 
	 * @return
	 */
	public boolean hasPower() {
		return EnergyStorage.getStoredPower() > 0;
	}

	/**
	 * This method syncs the current state of this energy storage component to all
	 * clients within a 64 block radius.
	 */
	public void syncToClient() {
		if (!getWorld().isRemote) {
			PacketEnergyStorageComponent syncPacket = new PacketEnergyStorageComponent(this, getPos(), this.getComponentName());
			StaticPowerMessageHandler.sendMessageToPlayerInArea(StaticPowerMessageHandler.MAIN_PACKET_CHANNEL, getWorld(), getPos(), 64, syncPacket);
		} else {
			throw new RuntimeException("This method should only be called on the server!");
		}

	}

	/**
	 * Sets the filter used to restrict access to this component through
	 * capabilities. Use this to prevent certain actions from the capability access
	 * (ie. make it so external accessor cannot extract power). No need to check
	 * side configurations (ex. if a side is disabled) here as the energy component
	 * automatically picks those up.
	 * 
	 * @param filter
	 */
	public void setCapabiltiyFilter(TriFunction<Long, Direction, EnergyManipulationAction, Boolean> filter) {
		this.filter = filter;
	}

	@Override
	public <T> LazyOptional<T> provideCapability(Capability<T> cap, Direction side) {
		if (isEnabled()) {
			if (cap == CapabilityStaticVolt.STATIC_VOLT_CAPABILITY) {
				if (side != null) {
					return LazyOptional.of(() -> staticVoltAccessors.get(side)).cast();
				} else {
					return LazyOptional.of(() -> energyInterface).cast();
				}
			} else if (cap == CapabilityEnergy.ENERGY) {
				if (side != null) {
					return LazyOptional.of(() -> feAccessors.get(side)).cast();
				} else {
					return LazyOptional.of(() -> energyInterface).cast();
				}
			}
		}

		return LazyOptional.empty();
	}

	public class FECapabilityAccess implements IEnergyStorage {
		private final Direction side;

		public FECapabilityAccess(Direction side) {
			this.side = side;
		}

		@Override
		public int receiveEnergy(int maxReceive, boolean simulate) {
			if (!EnergyStorageComponent.this.isEnabled()) {
				return 0;
			}
			if (EnergyStorageComponent.this.filter != null
					&& !EnergyStorageComponent.this.filter.apply(CapabilityStaticVolt.convertFEtoSV(maxReceive), side, EnergyManipulationAction.RECIEVE)) {
				return 0;
			}
			return energyInterface.receiveEnergy(maxReceive, simulate);
		}

		@Override
		public int extractEnergy(int maxExtract, boolean simulate) {
			if (!EnergyStorageComponent.this.isEnabled()) {
				return 0;
			}
			if (EnergyStorageComponent.this.filter != null
					&& !EnergyStorageComponent.this.filter.apply(CapabilityStaticVolt.convertFEtoSV(maxExtract), side, EnergyManipulationAction.PROVIDE)) {
				return 0;
			}
			return energyInterface.extractEnergy(maxExtract, simulate);
		}

		@Override
		public int getEnergyStored() {
			if (!EnergyStorageComponent.this.isEnabled()) {
				return 0;
			}
			return energyInterface.getEnergyStored();
		}

		@Override
		public int getMaxEnergyStored() {
			if (!EnergyStorageComponent.this.isEnabled()) {
				return 0;
			}
			return energyInterface.getMaxEnergyStored();
		}

		@Override
		public boolean canExtract() {
			if (!EnergyStorageComponent.this.isEnabled()) {
				return false;
			}
			return energyInterface.canExtract();
		}

		@Override
		public boolean canReceive() {
			if (!EnergyStorageComponent.this.isEnabled()) {
				return false;
			}
			return energyInterface.canReceive();
		}

	}

	public class SVCapabilityAccess implements IStaticVoltHandler {
		private final Direction side;

		public SVCapabilityAccess(Direction side) {
			this.side = side;
		}

		@Override
		public long getStoredPower() {
			if (!EnergyStorageComponent.this.isEnabled()) {
				return 0;
			}
			return energyInterface.getStoredPower();
		}

		@Override
		public long getCapacity() {
			if (!EnergyStorageComponent.this.isEnabled()) {
				return 0;
			}
			return energyInterface.getCapacity();
		}

		@Override
		public long receivePower(long power, boolean simulate) {
			if (!EnergyStorageComponent.this.isEnabled()) {
				return 0;
			}
			if (EnergyStorageComponent.this.filter != null && !EnergyStorageComponent.this.filter.apply(power, side, EnergyManipulationAction.RECIEVE)) {
				return 0;
			}
			return energyInterface.receivePower(power, simulate);
		}

		@Override
		public long drainPower(long power, boolean simulate) {
			if (!EnergyStorageComponent.this.isEnabled()) {
				return 0;
			}
			if (EnergyStorageComponent.this.filter != null && !EnergyStorageComponent.this.filter.apply(power, side, EnergyManipulationAction.PROVIDE)) {
				return 0;
			}
			return energyInterface.drainPower(power, simulate);
		}

		@Override
		public boolean canRecievePower() {
			if (!EnergyStorageComponent.this.isEnabled()) {
				return false;
			}
			return energyInterface.canRecievePower();
		}

		@Override
		public boolean canBeDrained() {
			if (!EnergyStorageComponent.this.isEnabled()) {
				return false;
			}

			return energyInterface.canBeDrained();
		}

		@Override
		public long getMaxReceive() {
			if (!EnergyStorageComponent.this.isEnabled()) {
				return 0;
			}
			return energyInterface.getMaxReceive();
		}

		@Override
		public long getMaxDrain() {
			if (!EnergyStorageComponent.this.isEnabled()) {
				return 0;
			}
			return energyInterface.getMaxDrain();
		}
	}
}
