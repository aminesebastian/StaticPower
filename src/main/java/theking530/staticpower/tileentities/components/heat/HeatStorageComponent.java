package theking530.staticpower.tileentities.components.heat;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import theking530.api.IUpgradeItem.UpgradeType;
import theking530.api.heat.CapabilityHeatable;
import theking530.api.heat.HeatStorage;
import theking530.api.heat.HeatStorageUtilities;
import theking530.api.heat.IHeatStorage;
import theking530.api.heat.IHeatStorage.HeatTransferAction;
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

	public static final float HEAT_SYNC_MAX_DELTA = 10;

	@UpdateSerialize
	protected final HeatStorage heatStorage;
	@UpdateSerialize
	private int defaultCapacity;
	@UpdateSerialize
	private float defaultConductivity;
	@UpdateSerialize
	private float heatCapacityUpgradeMultiplier;
	@UpdateSerialize
	private float heatConductivityMultiplier;
	@UpdateSerialize
	private boolean issueSyncPackets;
	@UpdateSerialize
	private boolean exposeAsCapability;
	@UpdateSerialize
	private boolean enableAutomaticHeatTransfer;
	@UpdateSerialize
	private int meltdownRecoveryTime;
	@UpdateSerialize
	private int remainingMeltdownTicks;
	@UpdateSerialize
	private boolean hasMeltdownBehavior;
	@UpdateSerialize
	private boolean enteredMeltdownState;

	protected TriFunction<Integer, Direction, HeatManipulationAction, Boolean> filter;

	private UpgradeInventoryComponent upgradeInventory;
	private final Map<Direction, HeatComponentCapabilityAccess> accessors;
	private double lastSyncHeat;

	public HeatStorageComponent(String name, int maxHeat, float conductivity) {
		this(name, IHeatStorage.MINIMUM_TEMPERATURE, maxHeat, maxHeat, conductivity);
	}

	public HeatStorageComponent(String name, int overheatThreshold, int maxHeat, float conductivity) {
		this(name, IHeatStorage.MINIMUM_TEMPERATURE, overheatThreshold, maxHeat, conductivity);
	}

	public HeatStorageComponent(String name, int minHeat, int overheatThreshold, int maxHeat, float conductivity) {
		super(name);
		defaultCapacity = maxHeat;
		defaultConductivity = conductivity;
		issueSyncPackets = false;
		heatStorage = new HeatStorage(minHeat, overheatThreshold, maxHeat, conductivity);
		exposeAsCapability = true;
		enableAutomaticHeatTransfer = true;

		// Create the accessors.
		accessors = new HashMap<Direction, HeatComponentCapabilityAccess>();
		for (Direction dir : Direction.values()) {
			accessors.put(dir, new HeatComponentCapabilityAccess(dir));
		}
		lastSyncHeat = 0.0f;
	}

	@Override
	public void preProcessUpdate() {
		// Do nothing on the client.
		if (!getLevel().isClientSide) {
			// Check for upgrades.
			checkUpgrades();
		}
	}

	@Override
	public void postProcessUpdate() {
		if (!getLevel().isClientSide) {
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

			// Put us into meltdown mode if this ticker wants that.
			if (hasMeltdownBehavior && heatStorage.getCurrentHeat() >= heatStorage.getOverheatThreshold()) {
				// Only do the following ONCE after we started the meltdown.
				if (!enteredMeltdownState) {
					enteredMeltdownState = true;
					getLevel().playSound(null, getPos(), SoundEvents.SHULKER_BULLET_HIT, SoundSource.BLOCKS, 1.0f, 0.75f);
					getLevel().playSound(null, getPos(), SoundEvents.NOTE_BLOCK_BASEDRUM, SoundSource.BLOCKS, 0.5f, 1.5f);
				}

				// But allow the time to keep getting reset until we dip below the overheat
				// threshold.
				remainingMeltdownTicks = meltdownRecoveryTime;
			}

			// Tick down if we're in a meltdown.
			if (isRecoveringFromMeltdown()) {
				remainingMeltdownTicks--;
				if (remainingMeltdownTicks <= 0) {
					enteredMeltdownState = false;
					remainingMeltdownTicks = 0;
				}
			}

			if (this.getEnableAutomaticHeatTransfer()) {
				HeatStorageUtilities.transferHeatWithSurroundings(getStorage(), getLevel(), getPos(), HeatTransferAction.EXECUTE);
			}
		}
	}

	public HeatStorageComponent setMaxHeat(int heat) {
		this.defaultCapacity = heat;
		return this;
	}

	public HeatStorageComponent setMaxConductivity(float conducivity) {
		this.defaultConductivity = conducivity;
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

	public boolean isRecoveringFromMeltdown() {
		return remainingMeltdownTicks > 0;
	}

	public int getMeltdownRecoveryTicks() {
		return meltdownRecoveryTime;
	}

	public HeatStorageComponent setMeltdownRecoveryTicks(int meltdownRecoveryTime) {
		this.meltdownRecoveryTime = meltdownRecoveryTime;
		this.hasMeltdownBehavior = meltdownRecoveryTime > 0;
		return this;
	}

	public int getMeltdownRecoveryTicksRemaining() {
		return remainingMeltdownTicks;
	}

	public boolean isOverheated() {
		return this.heatStorage.getCurrentHeat() > this.heatStorage.getOverheatThreshold();
	}

	public boolean isAboveMinimumHeat() {
		return this.heatStorage.getCurrentHeat() > this.heatStorage.getMinimumHeatThreshold();
	}

	/**
	 * This method syncs the current state of this energy storage component to all
	 * clients within a 64 block radius.
	 */
	public void syncToClient() {
		if (!getLevel().isClientSide) {
			PacketHeatStorageComponent syncPacket = new PacketHeatStorageComponent(this, getPos(), this.getComponentName());
			StaticPowerMessageHandler.sendMessageToPlayerInArea(StaticPowerMessageHandler.MAIN_PACKET_CHANNEL, getLevel(), getPos(), 32, syncPacket);
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
	public HeatStorageComponent setCapabiltiyFilter(TriFunction<Integer, Direction, HeatManipulationAction, Boolean> filter) {
		this.filter = filter;
		return this;
	}

	public boolean isExposedAsCapability() {
		return exposeAsCapability;
	}

	public HeatStorageComponent setExposedAsCapability(boolean exposeAsCapability) {
		this.exposeAsCapability = exposeAsCapability;
		return this;
	}

	public boolean getEnableAutomaticHeatTransfer() {
		return enableAutomaticHeatTransfer;
	}

	public HeatStorageComponent setEnableAutomaticHeatTransfer(boolean enableAutomaticHeatTransfer) {
		this.enableAutomaticHeatTransfer = enableAutomaticHeatTransfer;
		return this;
	}

	/**
	 * Ignores any checks that would hide the capability (like if the component is
	 * enabled, or if capabilities are disabled).
	 * 
	 * @param <T>
	 * @param cap
	 * @param side
	 * @return
	 */
	public <T> LazyOptional<T> manuallyGetCapability(Capability<T> cap, Direction side) {
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

	@Override
	public <T> LazyOptional<T> provideCapability(Capability<T> cap, Direction side) {
		if (isEnabled() && exposeAsCapability) {
			return manuallyGetCapability(cap, side);
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
		getStorage().setMaximumHeat((int) (defaultCapacity * heatCapacityUpgradeMultiplier));
		getStorage().setConductivity(defaultConductivity * heatConductivityMultiplier);
	}

	private class HeatComponentCapabilityAccess implements IHeatStorage {
		protected final Direction side;

		public HeatComponentCapabilityAccess(Direction side) {
			this.side = side;
		}

		@Override
		public int heat(int amountToHeat, HeatTransferAction action) {
			if (HeatStorageComponent.this.filter != null && !HeatStorageComponent.this.filter.apply(amountToHeat, side, HeatManipulationAction.HEAT)) {
				return 0;
			}
			return HeatStorageComponent.this.getStorage().heat(amountToHeat, action);
		}

		@Override
		public int cool(int amountToCool, HeatTransferAction action) {
			if (HeatStorageComponent.this.filter != null && !HeatStorageComponent.this.filter.apply(amountToCool, side, HeatManipulationAction.COOL)) {
				return 0;
			}
			return HeatStorageComponent.this.getStorage().cool(amountToCool, action);
		}

		@Override
		public int getCurrentHeat() {
			return HeatStorageComponent.this.getStorage().getCurrentHeat();
		}

		@Override
		public int getMaximumHeat() {
			return HeatStorageComponent.this.getStorage().getMaximumHeat();
		}

		@Override
		public float getConductivity() {
			return HeatStorageComponent.this.getStorage().getConductivity();
		}

		@Override
		public int getOverheatThreshold() {
			return HeatStorageComponent.this.getStorage().getOverheatThreshold();
		}
	}
}
