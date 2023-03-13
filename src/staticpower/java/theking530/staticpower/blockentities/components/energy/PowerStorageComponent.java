package theking530.staticpower.blockentities.components.energy;

import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
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
import theking530.api.upgrades.UpgradeTypes;
import theking530.staticcore.utilities.SDMath;
import theking530.staticpower.StaticPower;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blockentities.components.AbstractBlockEntityComponent;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticpower.blockentities.components.items.UpgradeInventoryComponent;
import theking530.staticpower.blockentities.components.items.UpgradeInventoryComponent.UpgradeItemWrapper;
import theking530.staticpower.blockentities.components.serialization.UpdateSerialize;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.network.StaticPowerMessageHandler;

public class PowerStorageComponent extends AbstractBlockEntityComponent implements ISidedStaticPowerStorage {
	private static final int SYNC_PACKET_UPDATE_RADIUS = 64;
	private static final double ENERGY_SYNC_MAX_DELTA_PERCENT = 0.01;

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

	@UpdateSerialize
	private boolean isPendingOverVoltageExplostion;
	private boolean receivedExplosivePowerCurrentTick;
	private boolean receivedExplosivePowerLastTick;
	private boolean shouldExplodeWhenOverVolted;
	private int electricalExplosionTimeRemaining;

	public PowerStorageComponent(String name, ResourceLocation staticPowerTier, boolean canAcceptExternalPower, boolean canOutputExternalPower) {
		this(name, 0, StaticPowerVoltage.ZERO, StaticPowerVoltage.ZERO, 0, new CurrentType[] { CurrentType.DIRECT }, StaticPowerVoltage.ZERO, 0, CurrentType.DIRECT,
				canAcceptExternalPower, canOutputExternalPower);
		issueSyncPackets = true;

		// Get the tier.
		StaticPowerTier tierObject = StaticPowerConfig.getTier(staticPowerTier);
		setCapacity(tierObject.powerConfiguration.defaultPowerCapacity.get());
		setInputVoltageRange(tierObject.powerConfiguration.getDefaultInputVoltageRange().copy());
		setMaximumInputPower(tierObject.powerConfiguration.defaultMaximumPowerInput.get());
		setMaximumOutputPower(tierObject.powerConfiguration.defaultMaximumPowerOutput.get());
		setOutputVoltage(tierObject.powerConfiguration.defaultOutputVoltage.get());
	}

	public PowerStorageComponent(String name, double capacity, StaticPowerVoltage minInputVoltage, StaticPowerVoltage maxInputVoltage, double maxInputPower,
			CurrentType[] acceptableInputCurrents, StaticPowerVoltage voltageOutput, double maxPowerOutput, CurrentType outputCurrentType, boolean canAcceptExternalPower,
			boolean canOutputExternalPower) {
		super(name);

		exposeAsCapability = true;
		shouldExplodeWhenOverVolted = true;
		electricalExplosionTimeRemaining = -1;
		receivedExplosivePowerCurrentTick = false;

		baseCapacity = capacity;
		baseInputVoltageRange = new StaticVoltageRange(minInputVoltage, maxInputVoltage);
		baseVoltageOutput = voltageOutput;
		baseMaximumOutputPower = maxPowerOutput;

		storage = new StaticPowerStorage(capacity, new StaticVoltageRange(minInputVoltage, maxInputVoltage), maxInputPower, acceptableInputCurrents, voltageOutput, maxPowerOutput,
				outputCurrentType, canAcceptExternalPower, canOutputExternalPower, true);

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
				getLevel().addParticle(ParticleTypes.LARGE_SMOKE, getPos().getX() + randomX + 0.5, getPos().getY() + randomY + 0.5, getPos().getZ() + randomZ + 0.5, 0.0f, 0.0f,
						0.0f);
			}
			getLevel().addParticle(ParticleTypes.ELECTRIC_SPARK, getPos().getX() + randomX + 0.5, getPos().getY() + 1.0, getPos().getZ() + randomZ + 0.5, 0.0f, 0.0f, 0.0f);
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
		return storage.addPower(stack, simulate);
	}

	@Override
	public PowerStack drainPower(double power, boolean simulate) {
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
				if (receivedExplosivePowerCurrentTick || StaticPowerEnergyUtilities.shouldPowerStackTriggerExplosion(stack, this) != ElectricalExplosionTrigger.NONE) {
					receivedExplosivePowerCurrentTick = true;
					// Make a new power stack with 0 power but the accurate voltage and current type
					// to the player will see it in the UI (if only for a moment ;))
					return addPower(new PowerStack(0, stack.getVoltage(), stack.getCurrentType()), simulate);
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
		// Get the upgrade.
		UpgradeItemWrapper upgrade = upgradeInventory.getMaxTierItemForUpgradeType(UpgradeTypes.POWER);

		// If it is not valid, set the values back to the defaults. Otherwise, set the
		// new processing speeds.
		if (upgrade.isEmpty()) {
			powerCapacityUpgradeMultiplier = 1.0f;
			powerOutputUpgradeMultiplier = 1.0f;
		} else {
			powerCapacityUpgradeMultiplier = (float) (1.0f + (upgrade.getTier().upgradeConfiguration.powerUpgrade.get() * upgrade.getUpgradeWeight()));
			powerOutputUpgradeMultiplier = (float) (1.0f + (upgrade.getTier().upgradeConfiguration.powerIOUpgrade.get() * upgrade.getUpgradeWeight()));
		}

		// Set the new values.
		storage.setCapacity(baseCapacity * powerCapacityUpgradeMultiplier);
		storage.setMaximumInputPower(baseMaximumInputPower * powerOutputUpgradeMultiplier);
		storage.setOutputVoltage(baseVoltageOutput);
		storage.setMaximumOutputPower(baseMaximumOutputPower * powerOutputUpgradeMultiplier);

		// Handle the input transformer upgrade.
		UpgradeItemWrapper powerUpgrade = upgradeInventory.getMaxTierItemForUpgradeType(UpgradeTypes.POWER_TRANSFORMER);
		if (powerUpgrade.isEmpty()) {
			storage.setInputVoltageRange(new StaticVoltageRange(baseInputVoltageRange.minimumVoltage(), baseInputVoltageRange.maximumVoltage()));
		} else {
			if (powerUpgrade.getTierId() == StaticPowerTiers.ADVANCED) {
				if (storage.getInputVoltageRange().maximumVoltage().isLessThan(StaticPowerVoltage.MEDIUM)) {
					storage.setInputVoltageRange(new StaticVoltageRange(baseInputVoltageRange.minimumVoltage(), StaticPowerVoltage.MEDIUM));
				}
			} else if (powerUpgrade.getTierId() == StaticPowerTiers.STATIC) {
				if (storage.getInputVoltageRange().maximumVoltage().isLessThan(StaticPowerVoltage.HIGH)) {
					storage.setInputVoltageRange(new StaticVoltageRange(baseInputVoltageRange.minimumVoltage(), StaticPowerVoltage.HIGH));
				}
			} else if (powerUpgrade.getTierId() == StaticPowerTiers.ENERGIZED) {
				if (storage.getInputVoltageRange().maximumVoltage().isLessThan(StaticPowerVoltage.EXTREME)) {
					storage.setInputVoltageRange(new StaticVoltageRange(baseInputVoltageRange.minimumVoltage(), StaticPowerVoltage.EXTREME));
				}
			} else if (powerUpgrade.getTierId() == StaticPowerTiers.LUMUM) {
				if (storage.getInputVoltageRange().maximumVoltage().isLessThan(StaticPowerVoltage.BONKERS)) {
					storage.setInputVoltageRange(new StaticVoltageRange(baseInputVoltageRange.minimumVoltage(), StaticPowerVoltage.BONKERS));
				}
			}
		}
	}

	protected void handleNetworkSynchronization() {
		// Get the current delta between the amount of power we have and the power we
		// had last tick.
		double delta = Math.abs(getStoredPower() - lastSyncEnergy);

		// Determine if we should sync.
		boolean shouldSync = (delta / getCapacity()) >= ENERGY_SYNC_MAX_DELTA_PERCENT;
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
			StaticPower.LOGGER.warn("#synchronizeToClient (called at %1$s) should only be called from the server!", getPos().toString());
			return;
		}

		// Send the packet to all clients within the requested radius.
		PowerStorageComponentSyncPacket msg = new PowerStorageComponentSyncPacket(getPos(), this);
		StaticPowerMessageHandler.sendMessageToPlayerInArea(StaticPowerMessageHandler.MAIN_PACKET_CHANNEL, getLevel(), getPos(), SYNC_PACKET_UPDATE_RADIUS, msg);
		lastSyncEnergy = getStoredPower();
		pendingManualSync = false;
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
				getLevel().explode(null, getPos().getX() + 0.5, getPos().getY() + 0.5, getPos().getZ() + 0.5, 1, Explosion.BlockInteraction.BREAK);
			}
		}
	}

	protected void initiateExplosionTimer() {
		// TODO: Add particle effects here.
		electricalExplosionTimeRemaining = StaticPowerConfig.SERVER.overvoltageExplodeTime.get();
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
