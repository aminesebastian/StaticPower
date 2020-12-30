package theking530.staticpower.tileentities.components.heat;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import theking530.api.IUpgradeItem.UpgradeType;
import theking530.api.heat.CapabilityHeatable;
import theking530.api.heat.HeatStorage;
import theking530.api.heat.IHeatStorage;
import theking530.staticcore.utilities.TriFunction;
import theking530.staticpower.network.StaticPowerMessageHandler;
import theking530.staticpower.tileentities.components.AbstractTileEntityComponent;
import theking530.staticpower.tileentities.components.items.UpgradeInventoryComponent;
import theking530.staticpower.tileentities.components.items.UpgradeInventoryComponent.UpgradeItemWrapper;
import theking530.staticpower.tileentities.components.serialization.UpdateSerialize;

public class HeatStorageComponent extends AbstractTileEntityComponent {
	public enum HeatManipulationAction {
		COOL, HEAT
	}

	public enum HeatDissipationTiming {
		PRE_PROCESS, POST_PROCESS
	}

	public static final float HEAT_SYNC_MAX_DELTA = 10;

	@UpdateSerialize
	protected final HeatStorage heatStorage;
	@UpdateSerialize
	private double defaultCapacity;
	@UpdateSerialize
	private double defaultConductivity;
	@UpdateSerialize
	private double heatCapacityUpgradeMultiplier;
	@UpdateSerialize
	private double heatConductivityMultiplier;
	@UpdateSerialize
	private boolean issueSyncPackets;

	protected final HeatDissipationTiming dissipationTiming;
	protected TriFunction<Double, Direction, HeatManipulationAction, Boolean> filter;

	private UpgradeInventoryComponent upgradeInventory;
	private final Map<Direction, HeatComponentCapabilityAccess> accessors;
	private double lastSyncHeat;

	public HeatStorageComponent(String name, double maxHeat, double maxTransferRate) {
		this(name, maxHeat, maxTransferRate, HeatDissipationTiming.POST_PROCESS);
	}

	public HeatStorageComponent(String name, double maxHeat, double maxConductivity, HeatDissipationTiming timing) {
		super(name);
		defaultCapacity = maxHeat;
		defaultConductivity = maxConductivity;
		issueSyncPackets = false;
		heatStorage = new HeatStorage(maxHeat, maxConductivity);

		// Create the accessors.
		accessors = new HashMap<Direction, HeatComponentCapabilityAccess>();
		for (Direction dir : Direction.values()) {
			accessors.put(dir, new HeatComponentCapabilityAccess(dir));
		}

		lastSyncHeat = 0.0f;
		dissipationTiming = timing;
	}

	@Override
	public void preProcessUpdate() {
		// Do nothing on the client.
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
				double delta = Math.abs(heatStorage.getCurrentHeat() - lastSyncHeat);

				// Determine if we should sync.
				boolean shouldSync = delta > HEAT_SYNC_MAX_DELTA;
				shouldSync |= heatStorage.getCurrentHeat() == 0 && lastSyncHeat != 0;
				shouldSync |= heatStorage.getCurrentHeat() == heatStorage.getMaximumHeat() && lastSyncHeat != heatStorage.getMaximumHeat();

				// If we should sync, perform the sync.
				if (shouldSync) {
					lastSyncHeat = heatStorage.getCurrentHeat();
					syncToClient();
				}
			}

			// Capture heat transfer metrics.
			heatStorage.captureHeatTransferMetric();

			// Cool off the heat storage.
			heatStorage.transferWithSurroundings(getWorld(), getPos());
		}
	}

	public HeatStorageComponent setMaxHeat(double heat) {
		this.defaultCapacity = heat;
		return this;
	}

	public HeatStorageComponent setMaxConductivity(double heat) {
		this.defaultConductivity = heat;
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
	public HeatStorageComponent setAutoSyncPacketsEnabled(boolean enabled) {
		issueSyncPackets = enabled;
		return this;
	}

	/**
	 * Gets the raw heat storage object. This should 99% of the time NOT modified.
	 * 
	 * @return
	 */
	public HeatStorage getStorage() {
		return heatStorage;
	}

	/**
	 * This method syncs the current state of this energy storage component to all
	 * clients within a 64 block radius.
	 */
	public void syncToClient() {
		if (!getWorld().isRemote) {
			PacketHeatStorageComponent syncPacket = new PacketHeatStorageComponent(this, getPos(), this.getComponentName());
			StaticPowerMessageHandler.sendMessageToPlayerInArea(StaticPowerMessageHandler.MAIN_PACKET_CHANNEL, getWorld(), getPos(), 64, syncPacket);
		} else {
			throw new RuntimeException("This method should only be called on the server!");
		}

	}

	/**
	 * Sets the filter used to restrict access to this component through
	 * capabilities. Use this to prevent certain actions from the capability access
	 * (ie. make it so external accessor cannot extract heat). No need to check side
	 * configurations (ex. if a side is disabled) here as the heat component
	 * automatically picks those up.
	 * 
	 * @param filter
	 * @return
	 */
	public HeatStorageComponent setCapabiltiyFilter(TriFunction<Double, Direction, HeatManipulationAction, Boolean> filter) {
		this.filter = filter;
		return this;
	}

	@Override
	public <T> LazyOptional<T> provideCapability(Capability<T> cap, Direction side) {
		if (isEnabled()) {
			if (cap == CapabilityHeatable.HEAT_STORAGE_CAPABILITY) {
				if (side != null) {
					return LazyOptional.of(() -> accessors.get(side)).cast();
				} else {
					return LazyOptional.of(() -> heatStorage).cast();
				}
			}
		}

		return LazyOptional.empty();
	}

	public HeatStorageComponent setUpgradeInventory(UpgradeInventoryComponent inventory) {
		upgradeInventory = inventory;
		return this;
	}

	protected void checkUpgrades() {
		// Do nothing if there is no upgrade inventory.
		if (upgradeInventory == null) {
			return;
		}
		// First, see if there is a heat upgrade.
		UpgradeItemWrapper heatUpgrade = upgradeInventory.getMaxTierItemForUpgradeType(UpgradeType.HEAT);

		// If there is, apply the combo heat upgrade, otherwise check if there is just a
		// heat capacity upgrade, and then apply that.
		if (!heatUpgrade.isEmpty()) {
			heatCapacityUpgradeMultiplier = (1.0f + (heatUpgrade.getTier().heatCapacityUpgrade.get() * heatUpgrade.getUpgradeWeight()));
			heatConductivityMultiplier = (1.0f + (heatUpgrade.getTier().heatConductivityUpgrade.get() * heatUpgrade.getUpgradeWeight()));
		} else {
			// Set the heat conductivity back to 1.
			heatConductivityMultiplier = 1.0f;

			// check for a regular heat capacity upgrade.
			UpgradeItemWrapper heatCapacityUpgrade = upgradeInventory.getMaxTierItemForUpgradeType(UpgradeType.HEAT_CAPACITY);
			if (heatCapacityUpgrade.isEmpty()) {
				heatCapacityUpgradeMultiplier = 1.0f;
			} else {
				heatCapacityUpgradeMultiplier = (1.0f + (heatCapacityUpgrade.getTier().heatCapacityUpgrade.get() * heatCapacityUpgrade.getUpgradeWeight()));
			}
		}

		// Set the new values.
		getStorage().setMaximumHeat(defaultCapacity * heatCapacityUpgradeMultiplier);
		getStorage().setConductivity(defaultConductivity * heatConductivityMultiplier);
	}

	private class HeatComponentCapabilityAccess implements IHeatStorage {
		protected final Direction side;

		public HeatComponentCapabilityAccess(Direction side) {
			this.side = side;
		}

		@Override
		public double heat(double amountToHeat, boolean simulate) {
			if (HeatStorageComponent.this.filter != null && !HeatStorageComponent.this.filter.apply(amountToHeat, side, HeatManipulationAction.HEAT)) {
				return 0.0f;
			}
			return HeatStorageComponent.this.getStorage().heat(amountToHeat, simulate);
		}

		@Override
		public double cool(double amountToCool, boolean simulate) {
			if (HeatStorageComponent.this.filter != null && !HeatStorageComponent.this.filter.apply(amountToCool, side, HeatManipulationAction.COOL)) {
				return 0.0f;
			}
			return HeatStorageComponent.this.getStorage().cool(amountToCool, simulate);
		}

		@Override
		public double getCurrentHeat() {
			return HeatStorageComponent.this.getStorage().getCurrentHeat();
		}

		@Override
		public double getMaximumHeat() {
			return HeatStorageComponent.this.getStorage().getMaximumHeat();
		}

		@Override
		public double getConductivity() {
			return HeatStorageComponent.this.getStorage().getConductivity();
		}
	}
}
