package theking530.staticcore.blockentity.components.energy;

import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Explosion;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import theking530.api.energy.CapabilityStaticPower;
import theking530.api.energy.CurrentType;
import theking530.api.energy.IStaticPowerEnergyTracker;
import theking530.api.energy.PowerStack;
import theking530.api.energy.StaticPowerStorage;
import theking530.api.energy.StaticPowerVoltage;
import theking530.api.energy.StaticVoltageRange;
import theking530.api.energy.sided.ISidedStaticPowerStorage;
import theking530.api.energy.sided.SidedStaticPowerCapabilityWrapper;
import theking530.api.energy.utilities.StaticPowerEnergyUtilities;
import theking530.api.energy.utilities.StaticPowerEnergyUtilities.ElectricalExplosionTrigger;
import theking530.staticcore.StaticCore;
import theking530.staticcore.StaticCoreConfig;
import theking530.staticcore.blockentity.components.AbstractBlockEntityComponent;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticcore.blockentity.components.items.UpgradeInventoryComponent;
import theking530.staticcore.blockentity.components.items.UpgradeInventoryComponent.UpgradeItemWrapper;
import theking530.staticcore.blockentity.components.serialization.UpdateSerialize;
import theking530.staticcore.init.StaticCoreUpgradeTypes;
import theking530.staticcore.init.StaticCoreUpgradeTypes.CombinedPowerUpgradeValue;
import theking530.staticcore.network.StaticCoreMessageHandler;
import theking530.staticcore.utilities.math.SDMath;

public class PowerStorageComponent extends AbstractBlockEntityComponent implements ISidedStaticPowerStorage {
	private static final int SYNC_PACKET_UPDATE_RADIUS = 64;
	private static final double ENERGY_SYNC_MAX_DELTA_PERCENT = 0.05;
	private static final int DIRTY_SYNC_TIME = 20;

	@UpdateSerialize
	protected final StaticPowerStorage storage;
	@UpdateSerialize
	protected float powerCapacityUpgradeMultiplier;
	@UpdateSerialize
	protected float powerOutputUpgradeMultiplier;
	protected IPowerStorageComponentFilter powerFilter;

	private double baseCapacity;
	private StaticVoltageRange baseInputVoltageRange;
	private double baseMaximumInputPower;
	private StaticPowerVoltage baseVoltageOutput;
	private double baseMaximumOutputPower;

	private SideConfigurationComponent sideConfig;
	private UpgradeInventoryComponent upgradeInventory;

	private final SidedStaticPowerCapabilityWrapper capabilityWrapper;
	private boolean exposeAsCapability;

	private boolean issueSyncPackets;
	private double lastSyncEnergy;
	private boolean pendingManualSync;
	private long lastSyncTime;

	@UpdateSerialize
	private boolean isPendingOverVoltageExplostion;
	private boolean receivedExplosivePowerCurrentTick;
	private boolean receivedExplosivePowerLastTick;
	private boolean shouldExplodeWhenOverVolted;
	private int electricalExplosionTimeRemaining;

	protected boolean isDirty;

	public PowerStorageComponent(String name, double capacity, StaticPowerVoltage minInputVoltage,
			StaticPowerVoltage maxInputVoltage, double maxInputPower, CurrentType[] acceptableInputCurrents,
			StaticPowerVoltage voltageOutput, double maxPowerOutput, CurrentType outputCurrentType,
			boolean canAcceptExternalPower, boolean canOutputExternalPower) {
		super(name);
		issueSyncPackets = true;
		exposeAsCapability = true;
		shouldExplodeWhenOverVolted = true;
		electricalExplosionTimeRemaining = -1;
		receivedExplosivePowerCurrentTick = false;

		baseCapacity = capacity;
		baseInputVoltageRange = new StaticVoltageRange(minInputVoltage, maxInputVoltage);
		baseVoltageOutput = voltageOutput;
		baseMaximumInputPower = maxInputPower;
		baseMaximumOutputPower = maxPowerOutput;

		storage = new StaticPowerStorage(capacity, new StaticVoltageRange(minInputVoltage, maxInputVoltage),
				maxInputPower, acceptableInputCurrents, voltageOutput, maxPowerOutput, outputCurrentType,
				canAcceptExternalPower, canOutputExternalPower, true);

		capabilityWrapper = new SidedStaticPowerCapabilityWrapper(this);
	}

	@Override
	public void preProcessUpdate() {
		if (!isClientSide()) {
			// Check for upgrades.
			checkUpgrades();

			if (pendingManualSync) {
				sendSynchronizationPacket();
			}
		}
	}

	@Override
	public void postProcessUpdate() {
		if (!isClientSide()) {
			tickVoltageBasedExplosion();

			// Synchronize whenever we need to.
			if (issueSyncPackets) {
				handleNetworkSynchronization();
			}

			storage.getEnergyTracker().tick(getLevel());
			receivedExplosivePowerLastTick = receivedExplosivePowerCurrentTick;
			receivedExplosivePowerCurrentTick = false;
		}

		if (isPendingOverVoltageExplostion) {
			float randomX = ((2 * getLevel().getRandom().nextFloat()) - 1.0f) * 0.75f;
			float randomY = ((2 * getLevel().getRandom().nextFloat()) - 1.0f) * 0.5f;
			float randomZ = ((2 * getLevel().getRandom().nextFloat()) - 1.0f) * 0.75f;

			if (SDMath.diceRoll(0.5)) {
				getLevel().addParticle(ParticleTypes.LARGE_SMOKE, getPos().getX() + randomX + 0.5,
						getPos().getY() + randomY + 0.5, getPos().getZ() + randomZ + 0.5, 0.0f, 0.0f, 0.0f);
			}
			getLevel().addParticle(ParticleTypes.ELECTRIC_SPARK, getPos().getX() + randomX + 0.5, getPos().getY() + 1.0,
					getPos().getZ() + randomZ + 0.5, 0.0f, 0.0f, 0.0f);
		}
	}

	public void markDirty() {
		pendingManualSync = true;

		// We have to do this safety check here just in case #markDirty() is called
		// through the constructor.
		if (getBlockEntity() != null) {
			getBlockEntity().setChanged();
		}
	}

	public PowerStorageComponent setSideConfiguration(SideConfigurationComponent sideConfig) {
		this.sideConfig = sideConfig;
		return this;
	}

	public PowerStorageComponent setPowerStorageFilter(IPowerStorageComponentFilter filter) {
		this.powerFilter = filter;
		return this;
	}

	public PowerStorageComponent setCapacity(double capacity) {
		if (capacity == storage.getCapacity()) {
			return this;
		}

		storage.setCapacity(capacity);
		baseCapacity = capacity;
		markDirty();
		return this;
	}

	public PowerStorageComponent setOutputVoltage(StaticPowerVoltage voltageOutput) {
		if (voltageOutput == storage.getOutputVoltage()) {
			return this;
		}

		storage.setOutputVoltage(voltageOutput);
		baseVoltageOutput = voltageOutput;
		markDirty();
		return this;
	}

	public PowerStorageComponent setMaximumOutputPower(double powerOutput) {
		if (powerOutput == storage.getMaximumPowerOutput()) {
			return this;
		}

		storage.setMaximumOutputPower(powerOutput);
		baseMaximumOutputPower = powerOutput;
		markDirty();
		return this;
	}

	public PowerStorageComponent setExposeAsCapability(boolean exposeAsCapability) {
		if (exposeAsCapability == this.exposeAsCapability) {
			return this;
		}

		this.exposeAsCapability = exposeAsCapability;
		markDirty();
		return this;
	}

	public PowerStorageComponent setInputVoltageRange(StaticVoltageRange voltageRange) {
		if (voltageRange.equals(storage.getInputVoltageRange())) {
			return this;
		}

		storage.setInputVoltageRange(voltageRange);
		baseInputVoltageRange = voltageRange;
		markDirty();
		return this;
	}

	public PowerStorageComponent setMaximumInputPower(double powerInput) {
		if (powerInput == storage.getMaximumPowerInput()) {
			return this;
		}

		storage.setMaximumInputPower(powerInput);
		baseMaximumInputPower = powerInput;
		markDirty();
		return this;
	}

	public PowerStorageComponent setInputCurrentTypes(CurrentType... types) {
		storage.setInputCurrentTypes(types);
		markDirty();
		return this;
	}

	public PowerStorageComponent setOutputCurrentType(CurrentType type) {
		if (type == storage.getOutputCurrentType()) {
			return this;
		}

		storage.setOutputCurrentType(type);
		return this;
	}

	public PowerStorageComponent setCanAcceptExternalPower(boolean canAcceptExternalPower) {
		if (canAcceptExternalPower == storage.canAcceptExternalPower()) {
			return this;
		}

		storage.setCanAcceptExternalPower(canAcceptExternalPower);
		return this;
	}

	public PowerStorageComponent setCanOutputExternalPower(boolean canOutputExternalPower) {
		if (canOutputExternalPower == storage.canOutputExternalPower()) {
			return this;
		}

		storage.setCanOutputExternalPower(canOutputExternalPower);
		return this;
	}

	public PowerStorageComponent setShouldExplodeWhenOverVolted(boolean shouldExplodeWhenOverVolted) {
		if (shouldExplodeWhenOverVolted == this.shouldExplodeWhenOverVolted) {
			return this;
		}

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

	public boolean canFullyAcceptPower(double power) {
		return storage.canFullyAcceptPower(power);
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
	public StaticVoltageRange getInputVoltageRange() {
		return storage.getInputVoltageRange();
	}

	@Override
	public double getMaximumPowerInput() {
		return storage.getMaximumPowerInput();
	}

	@Override
	public boolean canAcceptCurrentType(CurrentType type) {
		return storage.canAcceptCurrentType(type);
	}

	@Override
	public StaticPowerVoltage getOutputVoltage() {
		return storage.getOutputVoltage();
	}

	@Override
	public double getMaximumPowerOutput() {
		return storage.getMaximumPowerOutput();
	}

	@Override
	public CurrentType getOutputCurrentType() {
		return storage.getOutputCurrentType();
	}

	@Override
	public boolean canAcceptExternalPower() {
		return storage.canAcceptExternalPower();
	}

	@Override
	public boolean canOutputExternalPower() {
		return storage.canOutputExternalPower();
	}

	@Override
	public double addPower(PowerStack stack, boolean simulate) {
		markDirty();
		return storage.addPower(stack, simulate);
	}

	@Override
	public PowerStack drainPower(double power, boolean simulate) {
		markDirty();
		return storage.drainPower(power, simulate);
	}

	@Override
	public double addPower(Direction side, PowerStack stack, boolean simulate) {
		if (!storage.canAcceptExternalPower()) {
			return 0;
		}

		if (powerFilter != null) {
			if (!powerFilter.canAddPower(side, stack)) {
				return 0;
			}
		}

		if (sideConfig == null || sideConfig.getWorldSpaceDirectionConfiguration(side).isInputMode()) {
			// Only when not simulating, check if we should initiate an explosion.
			if (!simulate) {
				if (receivedExplosivePowerCurrentTick || StaticPowerEnergyUtilities
						.shouldPowerStackTriggerExplosion(stack, this) != ElectricalExplosionTrigger.NONE) {
					receivedExplosivePowerCurrentTick = true;
					// Make a new power stack with 0 power but the accurate voltage and current type
					// to the player will see it in the UI (if only for a moment ;))
					return addPower(stack.copyWithPower(0), simulate);
				}
			}

			return addPower(stack, simulate);
		}

		return 0;
	}

	@Override
	public PowerStack drainPower(Direction side, double power, boolean simulate) {
		if (!storage.canOutputExternalPower()) {
			return PowerStack.EMPTY;
		}

		if (powerFilter != null) {
			if (!powerFilter.canDrainPower(side, power)) {
				return PowerStack.EMPTY;
			}
		}

		if (sideConfig == null || sideConfig.getWorldSpaceDirectionConfiguration(side).isOutputMode()) {
			return drainPower(power, simulate);
		}
		return PowerStack.EMPTY;
	}

	@Override
	public IStaticPowerEnergyTracker getEnergyTracker() {
		return storage.getEnergyTracker();
	}

	public double getAveragePowerUsedPerTick() {
		return storage.getAveragePowerUsedPerTick();
	}

	public double getAveragePowerAddedPerTick() {
		return storage.getAveragePowerAddedPerTick();
	}

	public StaticPowerVoltage getLastRecievedVoltage() {
		return storage.getLastRecievedVoltage();
	}

	public double getLastRecievedCurrent() {
		return storage.getLastRecievedCurrent();
	}

	public CurrentType getLastRecievedCurrentType() {
		return storage.getLastRecievedCurrentType();
	}

	public boolean canSupplyPower(double power) {
		return storage.canSupplyPower(power);
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

	public StaticPowerVoltage getBaseVoltageOutput() {
		return baseVoltageOutput;
	}

	public double getBaseCurrentOutput() {
		return baseMaximumOutputPower;
	}

	@Override
	public <T> LazyOptional<T> provideCapability(Capability<T> cap, Direction side) {
		if (!isEnabled()) {
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

		if (sideConfig == null || !sideConfig.getWorldSpaceDirectionConfiguration(side).isDisabledMode()) {
			return manuallyGetCapability(cap, side);
		}

		return LazyOptional.empty();
	}

	public <T> LazyOptional<T> manuallyGetCapability(Capability<T> cap, Direction side) {
		if (cap == CapabilityStaticPower.STATIC_VOLT_CAPABILITY) {
			return LazyOptional.of(() -> capabilityWrapper.get(side)).cast();
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

	protected void checkUpgrades() {
		// Do nothing if there is no upgrade inventory.
		if (upgradeInventory == null) {
			return;
		}

		UpgradeItemWrapper<Double> powerCapacityUpgrade = upgradeInventory
				.getMaxTierItemForUpgradeType(StaticCoreUpgradeTypes.POWER_CAPACITY.get());
		if (powerCapacityUpgrade.isEmpty()) {
			powerCapacityUpgradeMultiplier = 1.0f;
		} else {
			powerCapacityUpgradeMultiplier = (float) (1.0f
					+ (powerCapacityUpgrade.getUpgradeValue() * powerCapacityUpgrade.getUpgradeWeight()));
		}

		UpgradeItemWrapper<Double> powerTransferUpgrade = upgradeInventory
				.getMaxTierItemForUpgradeType(StaticCoreUpgradeTypes.POWER_TRANSFER.get());
		if (powerTransferUpgrade.isEmpty()) {
			powerOutputUpgradeMultiplier = 1.0f;
		} else {
			powerOutputUpgradeMultiplier = (float) (1.0f
					+ (powerTransferUpgrade.getUpgradeValue() * powerTransferUpgrade.getUpgradeWeight()));
		}

		UpgradeItemWrapper<CombinedPowerUpgradeValue> combinedPowerUpgrade = upgradeInventory
				.getMaxTierItemForUpgradeType(StaticCoreUpgradeTypes.POWER_COMBINED_UPGRADE.get());
		if (combinedPowerUpgrade.isEmpty()) {
			powerOutputUpgradeMultiplier = 1.0f;
		} else {
			powerCapacityUpgradeMultiplier = (float) (1.0f + (combinedPowerUpgrade.getUpgradeValue().powerCapacity()
					* combinedPowerUpgrade.getUpgradeWeight()));
			powerOutputUpgradeMultiplier = (float) (1.0f + (combinedPowerUpgrade.getUpgradeValue().powerTransfer()
					* combinedPowerUpgrade.getUpgradeWeight()));
		}
		// Set the new values.
		storage.setCapacity(baseCapacity * powerCapacityUpgradeMultiplier);
		storage.setMaximumInputPower(baseMaximumInputPower * powerOutputUpgradeMultiplier);
		storage.setOutputVoltage(baseVoltageOutput);
		storage.setMaximumOutputPower(baseMaximumOutputPower * powerOutputUpgradeMultiplier);

		// Handle the input transformer upgrade.
		UpgradeItemWrapper<StaticPowerVoltage> powerTransformerUpgrade = upgradeInventory
				.getMaxTierItemForUpgradeType(StaticCoreUpgradeTypes.POWER_TRANSFORMER.get());
		if (powerTransformerUpgrade.isEmpty()) {
			storage.setInputVoltageRange(new StaticVoltageRange(baseInputVoltageRange.minimumVoltage(),
					baseInputVoltageRange.maximumVoltage()));
		} else {
			if (storage.getInputVoltageRange().maximumVoltage().isLessThan(powerTransformerUpgrade.getUpgradeValue())) {
				storage.setInputVoltageRange(new StaticVoltageRange(baseInputVoltageRange.minimumVoltage(),
						powerTransformerUpgrade.getUpgradeValue()));
			}
		}
	}

	protected void handleNetworkSynchronization() {
		long currentGameTime = getLevel().getGameTime();
		long timeSinceLastSync = currentGameTime - lastSyncTime;
		boolean shouldSync = timeSinceLastSync >= DIRTY_SYNC_TIME;

		if (!shouldSync) {
			double delta = Math.abs(getStoredPower() - lastSyncEnergy);
			shouldSync = (delta / getCapacity()) >= ENERGY_SYNC_MAX_DELTA_PERCENT;
		}
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

	protected void sendSynchronizationPacket() {
		if (getLevel().isClientSide()) {
			StaticCore.LOGGER.warn("#synchronizeToClient (called at %1$s) should only be called from the server!",
					getPos().toString());
			return;
		}

		// Send the packet to all clients within the requested radius.
		PowerStorageComponentSyncPacket msg = new PowerStorageComponentSyncPacket(getPos(), this);
		StaticCoreMessageHandler.sendMessageToPlayerInArea(StaticCoreMessageHandler.MAIN_PACKET_CHANNEL, getLevel(),
				getPos(), SYNC_PACKET_UPDATE_RADIUS, msg);
		lastSyncEnergy = getStoredPower();
		pendingManualSync = false;
		lastSyncTime = getLevel().getGameTime();
	}

	protected void tickVoltageBasedExplosion() {
		if (!shouldExplodeWhenOverVolted) {
			return;
		}

		if (!receivedExplosivePowerLastTick) {
			resetExplosionTimer();
			return;
		}

		if (!isPendingElectricalExplosion()) {
			initiateExplosionTimer();
		} else {
			electricalExplosionTimeRemaining--;
			if (electricalExplosionTimeRemaining <= 0) {
				getLevel().explode(null, getPos().getX() + 0.5, getPos().getY() + 0.5, getPos().getZ() + 0.5, 1,
						Explosion.BlockInteraction.BREAK);
			}
		}
	}

	protected void initiateExplosionTimer() {
		// TODO: Add particle effects here.
		electricalExplosionTimeRemaining = StaticCoreConfig.SERVER.overvoltageExplodeTime.get();
		getLevel().playSound(null, getPos(), SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0f, 1.5f);
		isPendingOverVoltageExplostion = true;
		sendSynchronizationPacket();
	}

	protected void resetExplosionTimer() {
		if (electricalExplosionTimeRemaining >= 0) {
			electricalExplosionTimeRemaining = -1;
			isPendingOverVoltageExplostion = false;
			sendSynchronizationPacket();
		}
	}

	protected boolean isPendingElectricalExplosion() {
		return isPendingOverVoltageExplostion;
	}
}
