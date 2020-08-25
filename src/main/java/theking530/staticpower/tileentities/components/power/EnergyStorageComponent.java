package theking530.staticpower.tileentities.components.power;

import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import theking530.common.utilities.TriFunction;
import theking530.staticpower.energy.StaticVoltAutoConverter;
import theking530.staticpower.energy.CapabilityStaticVolt;
import theking530.staticpower.energy.IStaticVoltHandler;
import theking530.staticpower.energy.StaticVoltHandler;
import theking530.staticpower.items.upgrades.IUpgradeItem.UpgradeType;
import theking530.staticpower.network.StaticPowerMessageHandler;
import theking530.staticpower.tileentities.components.AbstractTileEntityComponent;
import theking530.staticpower.tileentities.components.items.UpgradeInventoryComponent;
import theking530.staticpower.tileentities.components.items.UpgradeInventoryComponent.UpgradeItemWrapper;
import theking530.staticpower.tileentities.components.serialization.UpdateSerialize;

public class EnergyStorageComponent extends AbstractTileEntityComponent {
	public enum EnergyManipulationAction {
		PROVIDE, RECIEVE
	}

	public static final int ENERGY_SYNC_MAX_DELTA = 100;
	@UpdateSerialize
	protected final StaticVoltHandler EnergyStorage;
	@UpdateSerialize
	private float powerCapacityUpgradeMultiplier;
	@UpdateSerialize
	private float powerIOUpgradeMultiplier;
	@UpdateSerialize
	private int defaultCapacity;
	@UpdateSerialize
	private int defaultMaxInput;
	@UpdateSerialize
	private int defaultMaxOutput;

	private final FECapabilityAccess feCapabilityAccessor;
	private final SVCapabilityAccess capabilityAccecssor;
	private final StaticVoltAutoConverter energyInterface;

	protected TriFunction<Integer, Direction, EnergyManipulationAction, Boolean> filter;
	private int lastSyncEnergy;
	private UpgradeInventoryComponent upgradeInventory;

	public EnergyStorageComponent(String name, int capacity) {
		this(name, capacity, Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

	public EnergyStorageComponent(String name, int capacity, int maxInput) {
		this(name, capacity, maxInput, 0);
	}

	public EnergyStorageComponent(String name, int capacity, int maxInput, int maxExtract) {
		super(name);
		EnergyStorage = new StaticVoltHandler(capacity, maxInput, maxExtract);
		capabilityAccecssor = new SVCapabilityAccess();
		feCapabilityAccessor = new FECapabilityAccess();
		defaultCapacity = capacity;
		powerCapacityUpgradeMultiplier = 1.0f;
		powerIOUpgradeMultiplier = 1.0f;
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
			// Get the current delta between the amount of power we have and the power we
			// had last tick.
			int delta = Math.abs(EnergyStorage.getStoredPower() - lastSyncEnergy);

			// Determine if we should sync.
			boolean shouldSync = delta > ENERGY_SYNC_MAX_DELTA;
			shouldSync |= EnergyStorage.getStoredPower() == 0 && lastSyncEnergy != 0;
			shouldSync |= EnergyStorage.getStoredPower() == EnergyStorage.getCapacity() && lastSyncEnergy != EnergyStorage.getCapacity();

			// If we should sync, perform the sync.
			if (shouldSync) {
				lastSyncEnergy = EnergyStorage.getStoredPower();
				syncToClient();
			}
			EnergyStorage.captureEnergyMetric();
		}
	}

	public EnergyStorageComponent setMaxInput(int maxInput) {
		defaultMaxInput = maxInput;
		return this;
	}

	public EnergyStorageComponent setMaxOutput(int maxOutput) {
		defaultMaxOutput = maxOutput;
		return this;
	}

	public int getDefaultMaxInput() {
		return defaultMaxInput;
	}

	public int getDefaultMaxOutput() {
		return defaultMaxOutput;
	}

	public int getDefaultCapacity() {
		return defaultCapacity;
	}

	public EnergyStorageComponent setDefaultCapacity(int defaultCapacity) {
		this.defaultCapacity = defaultCapacity;
		return this;
	}

	public EnergyStorageComponent setUpgradeInventory(UpgradeInventoryComponent inventory) {
		upgradeInventory = inventory;
		return this;
	}

	public FECapabilityAccess getFECapabilityAccessor() {
		return feCapabilityAccessor;
	}

	public SVCapabilityAccess getSVCapabilityAccessor() {
		return capabilityAccecssor;
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
			powerCapacityUpgradeMultiplier = 1.0f + (upgrade.getTier().getPowerUpgrade() * upgrade.getUpgradeWeight());
			powerIOUpgradeMultiplier = 1.0f + (upgrade.getTier().getPowerIoUpgrade() * upgrade.getUpgradeWeight());
		}

		// Set the new values.
		getStorage().setCapacity((int) (defaultCapacity * powerCapacityUpgradeMultiplier));
		getStorage().setMaxExtract((int) (defaultMaxOutput * powerIOUpgradeMultiplier));
		getStorage().setMaxReceive((int) (defaultMaxInput * powerIOUpgradeMultiplier));
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
	public boolean hasEnoughPower(int power) {
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
	public boolean useBulkPower(int power) {
		if (hasEnoughPower(power)) {
			int maxExtract = getStorage().getMaxDrain();
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
	public boolean canAcceptPower(int power) {
		return EnergyStorage.getStoredPower() + power <= EnergyStorage.getCapacity();
	}

	/**
	 * If this storage can fully receive the amount of power passed, it will receive
	 * that amount and return true. Otherwise, it will do nothing and return false.
	 * 
	 * @param power The amount of power to receive.
	 * @return True if the provided amount of power was received, false otherwise.
	 */
	public boolean addPower(int power) {
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
	public void setCapabiltiyFilter(TriFunction<Integer, Direction, EnergyManipulationAction, Boolean> filter) {
		this.filter = filter;
	}

	@Override
	public <T> LazyOptional<T> provideCapability(Capability<T> cap, Direction side) {
		if (isEnabled()) {
			if (cap == CapabilityStaticVolt.STATIC_VOLT_CAPABILITY) {
				capabilityAccecssor.currentSide = side;
				return LazyOptional.of(() -> capabilityAccecssor).cast();
			} else if (cap == CapabilityEnergy.ENERGY) {
				feCapabilityAccessor.currentSide = side;
				return LazyOptional.of(() -> feCapabilityAccessor).cast();
			}
		}

		return LazyOptional.empty();
	}

	public class FECapabilityAccess implements IEnergyStorage {
		protected Direction currentSide;

		@Override
		public int receiveEnergy(int maxReceive, boolean simulate) {
			if (!EnergyStorageComponent.this.isEnabled()) {
				return 0;
			}
			if (EnergyStorageComponent.this.filter != null
					&& !EnergyStorageComponent.this.filter.apply(maxReceive / IStaticVoltHandler.FE_TO_SV_CONVERSION, currentSide, EnergyManipulationAction.RECIEVE)) {
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
					&& !EnergyStorageComponent.this.filter.apply(maxExtract / IStaticVoltHandler.FE_TO_SV_CONVERSION, currentSide, EnergyManipulationAction.PROVIDE)) {
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
		protected Direction currentSide;

		@Override
		public int getStoredPower() {
			if (!EnergyStorageComponent.this.isEnabled()) {
				return 0;
			}
			return energyInterface.getStoredPower();
		}

		@Override
		public int getCapacity() {
			if (!EnergyStorageComponent.this.isEnabled()) {
				return 0;
			}
			return energyInterface.getCapacity();
		}

		@Override
		public int receivePower(int power, boolean simulate) {
			if (!EnergyStorageComponent.this.isEnabled()) {
				return 0;
			}
			if (EnergyStorageComponent.this.filter != null && !EnergyStorageComponent.this.filter.apply(power, currentSide, EnergyManipulationAction.PROVIDE)) {
				return 0;
			}
			return energyInterface.drainPower(power, simulate);
		}

		@Override
		public int drainPower(int power, boolean simulate) {
			if (!EnergyStorageComponent.this.isEnabled()) {
				return 0;
			}
			if (EnergyStorageComponent.this.filter != null && !EnergyStorageComponent.this.filter.apply(power, currentSide, EnergyManipulationAction.PROVIDE)) {
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
		public boolean canDrainPower() {
			if (!EnergyStorageComponent.this.isEnabled()) {
				return false;
			}

			return energyInterface.canDrainPower();
		}
	}
}
