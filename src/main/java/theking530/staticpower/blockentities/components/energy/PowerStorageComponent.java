package theking530.staticpower.blockentities.components.energy;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Explosion;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import theking530.api.energy.CapabilityStaticPower;
import theking530.api.energy.IStaticPowerStorage;
import theking530.api.energy.StaticPowerEnergyDataTypes.StaticVoltageRange;
import theking530.api.energy.StaticPowerStorage;
import theking530.api.upgrades.UpgradeTypes;
import theking530.staticpower.StaticPower;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blockentities.components.AbstractTileEntityComponent;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticpower.blockentities.components.items.UpgradeInventoryComponent;
import theking530.staticpower.blockentities.components.items.UpgradeInventoryComponent.UpgradeItemWrapper;
import theking530.staticpower.blockentities.components.serialization.UpdateSerialize;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.network.StaticPowerMessageHandler;

public class PowerStorageComponent extends AbstractTileEntityComponent implements IStaticPowerStorage {
	public static final int OVER_VOLTAGE_EXPLOSION_TICKS = 40;
	private static final int SYNC_PACKET_UPDATE_RADIUS = 64;
	private static final double ENERGY_SYNC_MAX_DELTA = 10;

	@UpdateSerialize
	private final StaticPowerStorage storage;
	@UpdateSerialize
	private float powerCapacityUpgradeMultiplier;
	@UpdateSerialize
	private float powerOutputUpgradeMultiplier;

	private double baseCapacity;
	private StaticVoltageRange baseInputVoltageRange;
	private double baseMaximumInputCurrent;
	private double baseVoltageOutput;
	private double baseMaximumOutputCurrent;

	private SideConfigurationComponent sideConfig;
	private UpgradeInventoryComponent upgradeInventory;

	private final Map<Direction, SidedEnergyProxy> powerInterfaces;
	private boolean exposeAsCapability;

	private boolean issueSyncPackets;
	private double lastSyncEnergy;
	private boolean pendingManualSync;

	private boolean shouldExplodeWhenOverVolted;
	private int overVoltageExplosionTimeRemaining;
	private boolean pendingOverVoltageExplosion;

	public PowerStorageComponent(String name, ResourceLocation staticPowerTier) {
		this(name, 0, 0, 0, 0, 0, 0);
		issueSyncPackets = true;

		// Get the tier.
		StaticPowerTier tierObject = StaticPowerConfig.getTier(staticPowerTier);
		setCapacity(tierObject.defaultMachinePowerCapacity.get());
		setInputVoltageRange(
				new StaticVoltageRange(tierObject.getDefaultMachineInputVoltageRange().minimumVoltage(), tierObject.getDefaultMachineInputVoltageRange().maximumVoltage()));
		setMaximumInputCurrent(tierObject.defaultMachineMaximumInputCurrent.get());
		setMaximumOutputCurrent(tierObject.defaultMachineMaximumOutputCurrent.get());
		setOutputVoltage(tierObject.defaultMachineMaximumOutputVoltage.get());
	}

	public PowerStorageComponent(String name, double capacity, double minInputVoltage, double maxInputVoltage, double maxInputcurrent, double voltageOutput,
			double maxCurrentOutput) {
		super(name);
		exposeAsCapability = true;
		shouldExplodeWhenOverVolted = true;
		overVoltageExplosionTimeRemaining = 0;
		pendingOverVoltageExplosion = false;

		baseCapacity = capacity;
		baseInputVoltageRange = new StaticVoltageRange(minInputVoltage, maxInputVoltage);
		baseVoltageOutput = voltageOutput;
		baseMaximumOutputCurrent = maxCurrentOutput;

		storage = new StaticPowerStorage(capacity, new StaticVoltageRange(minInputVoltage, maxInputVoltage), maxInputcurrent, voltageOutput, maxCurrentOutput) {
			@Override
			public double addPower(double voltage, double power, boolean simulate) {
				if (!simulate && voltage > getInputVoltageRange().maximumVoltage()) {
					handleOvervoltage(voltage, power);
					return 0;
				} else {
					return super.addPower(voltage, power, simulate);
				}
			}
		};

		powerInterfaces = new HashMap<>();
		for (Direction dir : Direction.values()) {
			powerInterfaces.put(dir, new SidedEnergyProxy(dir, this) {
				@Override
				public boolean canAcceptPower(Direction side) {
					if (sideConfig != null) {
						return sideConfig.getWorldSpaceDirectionConfiguration(side).isInputMode();
					}
					return true;
				}

				@Override
				public boolean doesProvidePower(Direction side) {
					if (sideConfig != null) {
						return sideConfig.getWorldSpaceDirectionConfiguration(side).isOutputMode();
					}
					return true;
				}
			});
		}
	}

	@Override
	public void preProcessUpdate() {
		if (!isClientSide()) {
			// Check for upgrades.
			checkUpgrades();

			if (pendingManualSync) {
				sendSynchronizationPacket();
			}

			storage.captureEnergyMetric();
		}
	}

	@Override
	public void postProcessUpdate() {
		if (!isClientSide()) {
			if (pendingOverVoltageExplosion) {
				overVoltageExplosionTimeRemaining--;
				if (overVoltageExplosionTimeRemaining <= 0) {
					getLevel().explode(null, getPos().getX() + 0.5, getPos().getY() + 0.5, getPos().getZ() + 0.5, 1, Explosion.BlockInteraction.BREAK);
				}
			}

			// Synchronize whenever we need to.
			if (issueSyncPackets) {
				handleNetworkSynchronization();
			}
		}
	}

	protected void checkUpgrades() {
		// Do nothing if there is no upgrade inventory.
		if (upgradeInventory == null) {
			return;
		}
		// Get the upgrade.
		UpgradeItemWrapper upgrade = upgradeInventory.getMaxTierItemForUpgradeType(UpgradeTypes.POWER);

		// If it is not valid, set the values back to the defaults. Otherwise, set the
		// new processing speeds.
		if (upgrade.isEmpty()) {
			powerCapacityUpgradeMultiplier = 1.0f;
			powerOutputUpgradeMultiplier = 1.0f;
		} else {
			powerCapacityUpgradeMultiplier = (float) (1.0f + (upgrade.getTier().powerUpgrade.get() * upgrade.getUpgradeWeight()));
			powerOutputUpgradeMultiplier = (float) (1.0f + (upgrade.getTier().powerIOUpgrade.get() * upgrade.getUpgradeWeight()));
		}

		// Set the new values.
		storage.setCapacity(baseCapacity * powerCapacityUpgradeMultiplier);
		storage.setInputVoltageRange(new StaticVoltageRange(baseInputVoltageRange.minimumVoltage() * powerOutputUpgradeMultiplier,
				baseInputVoltageRange.maximumVoltage() * powerOutputUpgradeMultiplier));
		storage.setMaximumInputCurrent(baseMaximumInputCurrent * powerOutputUpgradeMultiplier);
		storage.setOutputVoltage(baseVoltageOutput * powerOutputUpgradeMultiplier);
		storage.setMaximumOutputCurrent(baseMaximumOutputCurrent * powerOutputUpgradeMultiplier);
	}

	protected void handleNetworkSynchronization() {
		// Get the current delta between the amount of power we have and the power we
		// had last tick.
		double delta = Math.abs(getStoredPower() - lastSyncEnergy);

		// Determine if we should sync.
		boolean shouldSync = delta > ENERGY_SYNC_MAX_DELTA;
		if (!shouldSync) {
			shouldSync = getStoredPower() == 0 && lastSyncEnergy != 0;
		}
		if (!shouldSync) {
			shouldSync = getStoredPower() == getCapacity() && lastSyncEnergy != getCapacity();
		}

		// If we should sync, perform the sync.
		if (shouldSync) {
			sendSynchronizationPacket();
		}
	}

	public void markDirty() {
		pendingManualSync = true;

		// We have to do this safety check here just in case #markDirty() is called
		// through the constructor.
		if (getTileEntity() != null) {
			getTileEntity().setChanged();
		}
	}

	protected void sendSynchronizationPacket() {
		if (getLevel().isClientSide()) {
			StaticPower.LOGGER.warn("#synchronizeToClient (called at %1$s) should only be called from the server!", getPos().toString());
			return;
		}

		// Send the packet to all clients within the requested radius.
		PowerStorageComponentSyncPacket msg = new PowerStorageComponentSyncPacket(getPos(), this);
		StaticPowerMessageHandler.sendMessageToPlayerInArea(StaticPowerMessageHandler.MAIN_PACKET_CHANNEL, getLevel(), getPos(), SYNC_PACKET_UPDATE_RADIUS, msg);
		lastSyncEnergy = getStoredPower();
	}

	protected void handleOvervoltage(double voltage, double power) {
		if (power == 0) {
			return;
		}
		if (pendingOverVoltageExplosion) {
			return;
		}
		if (shouldExplodeWhenOverVolted) {
			pendingOverVoltageExplosion = true;
			overVoltageExplosionTimeRemaining = OVER_VOLTAGE_EXPLOSION_TICKS;
			getLevel().playSound(null, getPos(), SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0f, 1.5f);
			// TODO: Add particle effects here.
		}
	}

	public PowerStorageComponent setSideConfiguration(SideConfigurationComponent sideConfig) {
		this.sideConfig = sideConfig;
		return this;
	}

	public PowerStorageComponent setCapacity(double capacity) {
		storage.setCapacity(capacity);
		baseCapacity = capacity;
		markDirty();
		return this;
	}

	public PowerStorageComponent setDoesProvidePower(boolean doesProvidePower) {
		storage.setDoesProvidePower(doesProvidePower);
		markDirty();
		return this;
	}

	public PowerStorageComponent setCanAcceptPower(boolean canAcceptPower) {
		storage.setCanAcceptPower(canAcceptPower);
		markDirty();
		return this;
	}

	public PowerStorageComponent setOutputVoltage(double voltageOutput) {
		storage.setOutputVoltage(voltageOutput);
		baseVoltageOutput = voltageOutput;
		markDirty();
		return this;
	}

	public PowerStorageComponent setMaximumOutputCurrent(double currentOutput) {
		storage.setMaximumOutputCurrent(currentOutput);
		baseMaximumOutputCurrent = currentOutput;
		markDirty();
		return this;
	}

	public PowerStorageComponent setExposeAsCapability(boolean exposeAsCapability) {
		this.exposeAsCapability = exposeAsCapability;
		markDirty();
		return this;
	}

	public PowerStorageComponent setInputVoltageRange(StaticVoltageRange voltageRange) {
		storage.setInputVoltageRange(voltageRange);
		baseInputVoltageRange = voltageRange;
		markDirty();
		return this;
	}

	public PowerStorageComponent setMaximumInputCurrent(double current) {
		storage.setMaximumInputCurrent(current);
		baseMaximumInputCurrent = current;
		markDirty();
		return this;
	}

	public PowerStorageComponent setShouldExplodeWhenOverVolted(boolean shouldExplodeWhenOverVolted) {
		this.shouldExplodeWhenOverVolted = shouldExplodeWhenOverVolted;
		markDirty();
		return this;
	}

	public PowerStorageComponent setUpgradeInventory(UpgradeInventoryComponent upgradeInventory) {
		this.upgradeInventory = upgradeInventory;
		return this;
	}

	public boolean isShouldExplodeWhenOverVolted() {
		return shouldExplodeWhenOverVolted;
	}

	@Override
	public StaticVoltageRange getInputVoltageRange() {
		return storage.getInputVoltageRange();
	}

	@Override
	public double getMaximumCurrentInput() {
		return storage.getMaximumCurrentInput();
	}

	@Override
	public double getStoredPower() {
		return storage.getStoredPower();
	}

	@Override
	public double getCapacity() {
		return storage.getCapacity();
	}

	@Override
	public double getVoltageOutput() {
		return storage.getVoltageOutput();
	}

	@Override
	public double getMaximumCurrentOutput() {
		return storage.getMaximumCurrentOutput();
	}

	@Override
	public double addPower(double voltage, double power, boolean simulate) {
		return storage.addPower(voltage, power, simulate);
	}

	@Override
	public double drainPower(double power, boolean simulate) {
		return storage.drainPower(power, simulate);
	}

	@Override
	public boolean canAcceptPower() {
		return storage.canAcceptPower();
	}

	@Override
	public boolean doesProvidePower() {
		return storage.doesProvidePower();
	}

//	public double addPowerIgnoringVoltageLimitations(double power) {
//		return storage.addPowerIgnoringVoltageLimitations(power);
//	}
//
//	public double usePowerIgnoringVoltageLimitations(double power) {
//		return storage.usePowerIgnoringVoltageLimitations(power);
//	}

	public double getAveragePowerUsedPerTick() {
		return storage.getAveragePowerUsedPerTick();
	}

	public double getAveragePowerAddedPerTick() {
		return storage.getAveragePowerAddedPerTick();
	}

	public boolean canSupplyPower(double power) {
		return storage.canSupplyPower(power);
	}

	public double getMaxOutputPower() {
		return storage.getMaxOutputPower();
	}

	public boolean canAcceptPower(double power) {
		return storage.canAcceptPower(power);
	}

	public PowerStorageComponent setAutoSyncPacketsEnabled(boolean enabled) {
		issueSyncPackets = enabled;
		return this;
	}

	public double getBaseCapacity() {
		return baseCapacity;
	}

	public StaticVoltageRange getBaseInputVoltage() {
		return baseInputVoltageRange;
	}

	public double getBaseVoltageOutput() {
		return baseVoltageOutput;
	}

	public double getBaseCurrentOutput() {
		return baseMaximumOutputCurrent;
	}

	@Override
	public <T> LazyOptional<T> provideCapability(Capability<T> cap, Direction side) {
		// Still expose even if exposeAsCapability is false if the side is null. This is
		// used for JADE and other overlays.
		if (isEnabled() && (side == null || exposeAsCapability)) {
			return manuallyGetCapability(cap, side);
		}

		return LazyOptional.empty();
	}

	public <T> LazyOptional<T> manuallyGetCapability(Capability<T> cap, Direction side) {
		if (cap == CapabilityStaticPower.STATIC_VOLT_CAPABILITY) {
			if (side == null) {
				return LazyOptional.of(() -> this).cast();
			} else {
				return LazyOptional.of(() -> powerInterfaces.get(side)).cast();
			}
		}
		return LazyOptional.empty();
	}

	public CompoundTag serializeClientSynchronizeData(CompoundTag nbt, boolean fromUpdate) {
		this.serializeUpdateNbt(nbt, fromUpdate);

		return nbt;
	}

	public void recieveClientSynchronizeData(CompoundTag nbt, boolean fromUpdate) {
		this.deserializeUpdateNbt(nbt, fromUpdate);

	}

}
