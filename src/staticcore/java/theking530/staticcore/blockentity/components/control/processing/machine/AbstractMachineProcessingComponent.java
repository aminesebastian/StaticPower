package theking530.staticcore.blockentity.components.control.processing.machine;

import java.util.Optional;

import javax.annotation.Nullable;

import theking530.api.energy.PowerStack;
import theking530.api.team.ITeamOwnable;
import theking530.staticcore.blockentity.components.control.RedstoneControlComponent;
import theking530.staticcore.blockentity.components.control.processing.AbstractProcessingComponent;
import theking530.staticcore.blockentity.components.control.processing.IProcessor;
import theking530.staticcore.blockentity.components.control.processing.ProcessingCheckState;
import theking530.staticcore.blockentity.components.control.processing.ProcessingContainer;
import theking530.staticcore.blockentity.components.energy.PowerStorageComponent;
import theking530.staticcore.blockentity.components.items.UpgradeInventoryComponent;
import theking530.staticcore.blockentity.components.serialization.SaveSerialize;
import theking530.staticcore.blockentity.components.serialization.UpdateSerialize;
import theking530.staticcore.blockentity.components.team.TeamComponent;
import theking530.staticcore.init.StaticCoreProductTypes;
import theking530.staticcore.teams.ServerTeam;

public abstract class AbstractMachineProcessingComponent<T extends AbstractProcessingComponent<T, K>, K extends MachineProcessingComponentSyncPacket>
		extends AbstractProcessingComponent<T, K> {

	protected static final int POWER_SMOOTHING_FACTOR = 10;

	/**
	 * This is how much power this processing component should use per tick, before
	 * any modifiers or upgrades.
	 */
	@SaveSerialize
	protected double basePowerUsage;
	/**
	 * This is the amount of power this processing component should use per tick
	 * after modifiers and upgrades.
	 */
	@UpdateSerialize
	protected double powerUsage;
	/**
	 * This represents the percentage of the requiredPowerUsage is satisfied by the
	 * input to the {@link #powerComponent};
	 */
	@UpdateSerialize
	private double powerSatisfaction;

	@SaveSerialize
	/**
	 * If true, the processing time will decrease proportional to the amount of
	 * power we have coming into the powerComponent vs the amount of power we
	 * require. This does nothing if there is no {@link #powerComponent}.
	 */
	private boolean modulateProcessingTimeByPowerSatisfaction;

	/**
	 * The optional upgrade inventory that we use to modulate the processing time.
	 */
	protected @Nullable UpgradeInventoryComponent upgradeInventory;
	/**
	 * The optional power inventory that we use to check whether or not we have
	 * enough power to continue processing.
	 */
	protected @Nullable PowerStorageComponent powerComponent;
	/**
	 * The optional redstone control component that we can use to control the on/off
	 * state of the processing.
	 */
	protected @Nullable RedstoneControlComponent redstoneControlComponent;

	public AbstractMachineProcessingComponent(String name, int processingTime) {
		super(name, processingTime);
		this.modulateProcessingTimeByPowerSatisfaction = true;
	}

	@Override
	public void preProcessUpdate() {
		// Do nothing on the client.
		if (getLevel().isClientSide()) {
			return;
		}

		powerSatisfaction = calculatePowerSatisfaction();
		powerUsage = calculatePowerUsage();
		super.preProcessUpdate();
	}

	@Override
	protected int modifyProcessingTime(int defaultProcessingTime) {
		int output = defaultProcessingTime;
		if (modulateProcessingTimeByPowerSatisfaction) {
			// Slow down processing proportionally to the power satisfaction.
			// The lower the satisfaction, the slower the processing.
			// DO NOT return 0 here if power satisfaction is 0. We always want a non-zero
			// number here. Zero power satisfaction will be a processing error.
			if (powerSatisfaction > 0) {
				output /= powerSatisfaction;
			}
		}
		return output;
	}

	protected double calculatePowerSatisfaction() {
		if (powerComponent == null || getBasePowerUsage() == 0) {
			return 1;
		}

		if (powerComponent.getStoredPower() < getBasePowerUsage()) {
			return 0;
		}

		double currentTickSatisfaction = 0;
		double powerPercentage = powerComponent.getStoredPower() / powerComponent.getCapacity();

		if (powerPercentage > 0.9f) {
			currentTickSatisfaction = 1.0f;
		} else if (powerPercentage > 0.0f) {
			currentTickSatisfaction = powerPercentage;
		} else {
			double percentOfBaseUsageSatisfied = powerComponent.getAveragePowerAddedPerTick() / getBasePowerUsage();
			currentTickSatisfaction = Math.min(1, percentOfBaseUsageSatisfied);
		}

		double smoothedSatisfaction = (currentTickSatisfaction + (powerSatisfaction * POWER_SMOOTHING_FACTOR))
				/ (POWER_SMOOTHING_FACTOR + 1);
		if (smoothedSatisfaction < 0.001) {
			if (currentTickSatisfaction > 0) {
				smoothedSatisfaction = currentTickSatisfaction;
			} else {
				smoothedSatisfaction = 0;
			}
		}

		return smoothedSatisfaction;
	}

	@SuppressWarnings("unchecked")
	public T setModulateProcessingTimeByPowerSatisfaction(boolean modulateProcessingTimeByPowerSatisfaction) {
		this.modulateProcessingTimeByPowerSatisfaction = modulateProcessingTimeByPowerSatisfaction;
		return (T) this;
	}

	public boolean shouldModulateProcessingTimeByPowerSatisfaction() {
		return modulateProcessingTimeByPowerSatisfaction;
	}

	@SuppressWarnings("unchecked")
	public T setBasePowerUsage(double defaultPowerUsage) {
		this.basePowerUsage = defaultPowerUsage;
		return (T) this;
	}

	public final double getBasePowerUsage() {
		return basePowerUsage;
	}

	protected double calculatePowerUsage() {
		double rawUsage = basePowerUsage * powerSatisfaction;
		return Math.ceil(rawUsage); // SDMath.clamp(rawUsage, basePowerUsage / 2, basePowerUsage);
	}

	public final double getPowerUsage() {
		return powerUsage;
	}

	public final double getPowerSatisfaction() {
		return powerSatisfaction;
	}

	public float getCalculatedPowerUsageMultipler() {
		return 1;
	}

	public float getCalculatedHeatGenerationMultiplier() {
		return 1;
	}

	@SuppressWarnings("unchecked")
	public T setRedstoneControlComponent(RedstoneControlComponent redstoneControlComponent) {
		this.redstoneControlComponent = redstoneControlComponent;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T setUpgradeInventory(UpgradeInventoryComponent inventory) {
		upgradeInventory = inventory;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T setPowerComponent(PowerStorageComponent energyComponent) {
		this.powerComponent = energyComponent;
		modulateProcessingTimeByPowerSatisfaction = true;
		return (T) this;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected ProcessingCheckState canStartProcessing() {
		ProcessingCheckState powerRequirementsCheck = checkPowerRequirements();
		if (!powerRequirementsCheck.isOk()) {
			return powerRequirementsCheck;
		}

		ProcessingCheckState redstoneControlCheck = checkRedstoneState();
		if (!redstoneControlCheck.isOk()) {
			return redstoneControlCheck;
		}

		Optional<IProcessor> processingInterface = getProcessingOwner();
		if (processingInterface.isPresent()) {
			ProcessingCheckState ownerCheckState = processingInterface.get().canStartProcessing(this,
					getProcessingContainer());
			if (!ownerCheckState.isOk()) {
				return ownerCheckState;
			}
		}

		return ProcessingCheckState.ok();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected ProcessingCheckState canContinueProcessing() {
		ProcessingCheckState powerRequirementsCheck = checkPowerRequirements();
		if (!powerRequirementsCheck.isOk()) {
			return powerRequirementsCheck;
		}

		ProcessingCheckState redstoneControlCheck = checkRedstoneState();
		if (!redstoneControlCheck.isOk()) {
			return redstoneControlCheck;
		}

		Optional<IProcessor> processingInterface = getProcessingOwner();
		if (processingInterface.isPresent()) {
			ProcessingCheckState ownerCheckState = processingInterface.get().canContinueProcessing(this,
					getProcessingContainer());
			if (!ownerCheckState.isOk()) {
				return ownerCheckState;
			}
		}

		return ProcessingCheckState.ok();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected ProcessingCheckState canCompleteProcessing() {
		Optional<IProcessor> processingInterface = getProcessingOwner();
		if (processingInterface.isPresent()) {
			ProcessingCheckState ownerCheckState = processingInterface.get().canCompleteProcessing(this,
					getProcessingContainer());
			if (!ownerCheckState.isOk()) {
				return ownerCheckState;
			}
		}

		return ProcessingCheckState.ok();
	}

	protected ProcessingCheckState checkRedstoneState() {
		if (redstoneControlComponent != null && !redstoneControlComponent.passesRedstoneCheck()) {
			return ProcessingCheckState.doesNotPassRedstoneControlCheck();
		}

		return ProcessingCheckState.ok();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected void onProcessingStarted(ProcessingContainer processingContainer) {
		Optional<IProcessor> processingInterface = getProcessingOwner();
		if (processingInterface.isPresent()) {
			processingInterface.get().onProcessingStarted(this, processingContainer);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected void onProcessingProgressMade(ProcessingContainer processingContainer) {
		super.onProcessingProgressMade(processingContainer);
		if (this.powerComponent != null) {
			PowerStack drained = this.powerComponent.drainPower(getPowerUsage(), false);

			// Update all the production statistics.
			ITeamOwnable teamComp = getBlockEntity().getComponent(TeamComponent.class);
			if (teamComp != null && teamComp.getOwningTeam() != null) {
				this.getProductionToken(StaticCoreProductTypes.Power.get())
						.consumed((ServerTeam) teamComp.getOwningTeam(), getPowerProducerId(), drained.getPower());
			}

		}

		Optional<IProcessor> processingInterface = getProcessingOwner();
		if (processingInterface.isPresent()) {
			processingInterface.get().onProcessingProgressMade(this, processingContainer);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected void onProcessingCanceled(ProcessingContainer processingContainer) {
		super.onProcessingCanceled(processingContainer);
		Optional<IProcessor> processingInterface = getProcessingOwner();
		if (processingInterface.isPresent()) {
			processingInterface.get().onProcessingCanceled(this, processingContainer);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected void onProcessingPausedDueToError(ProcessingContainer processingContainer) {
		super.onProcessingPausedDueToError(processingContainer);
		Optional<IProcessor> processingInterface = getProcessingOwner();
		if (processingInterface.isPresent()) {
			processingInterface.get().onProcessingPausedDueToError(this, processingContainer);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected void onProcessingCompleted(ProcessingContainer processingContainer) {
		super.onProcessingCompleted(processingContainer);

		Optional<IProcessor> processingInterface = getProcessingOwner();
		if (processingInterface.isPresent()) {
			processingInterface.get().onProcessingCompleted(this, processingContainer);
		}
	}

	protected ProcessingCheckState checkPowerRequirements() {
		if (powerComponent == null) {
			return ProcessingCheckState.ok();
		}

		if (getBasePowerUsage() > 0) {
			if (getPowerSatisfaction() <= 0.0) {
				return ProcessingCheckState.notEnoughPower(getPowerUsage());
			}
			if (powerComponent.getStoredPower() < getPowerUsage()) {
				return ProcessingCheckState.notEnoughPower(getPowerUsage());
			}
			if (powerComponent.getMaximumPowerOutput() < getPowerUsage()) {
				return ProcessingCheckState.powerUsageTooHigh(getPowerUsage());
			}
		}

		return ProcessingCheckState.ok();
	}

	@Override
	protected void updateProductionRates(ITeamOwnable teamComp) {
		super.updateProductionRates(teamComp);
		// We capture this here and not as a product when processing starts because the
		// value can change over time as the power satisfaction changes.
		if (powerComponent != null && getPowerUsage() > 0) {
			getProductionToken(StaticCoreProductTypes.Power.get()).setConsumptionPerSecond(
					(ServerTeam) teamComp.getOwningTeam(), getPowerProducerId(),
					getPowerUsage() * 20 * powerSatisfaction, getPowerUsage() * 20);
		}
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	protected K createSynchronizationPacket() {
		return (K) new MachineProcessingComponentSyncPacket(getPos(), this);
	}

	@SuppressWarnings({ "rawtypes" })
	protected Optional<IProcessor> getProcessingOwner() {
		if (getBlockEntity() instanceof IProcessor) {
			return Optional.of((IProcessor) getBlockEntity());
		}
		return Optional.empty();
	}
}