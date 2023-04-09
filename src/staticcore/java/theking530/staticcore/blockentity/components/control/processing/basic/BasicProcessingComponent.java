package theking530.staticcore.blockentity.components.control.processing.basic;

import java.util.Optional;

import javax.annotation.Nullable;

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
import theking530.staticcore.utilities.math.SDMath;

public class BasicProcessingComponent<T extends AbstractProcessingComponent<T, K>, K extends BasicProcessingComponentSyncPacket>
		extends AbstractProcessingComponent<T, K> {

	public static final int POWER_SMOOTHING_FACTOR = 10;

	/**
	 * This is how much power this processing component should use per tick, before
	 * any modifiers or upgrades.
	 */
	@SaveSerialize
	protected double defaultPowerUsage;
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

	public BasicProcessingComponent(String name, int processingTime) {
		super(name, processingTime);
		this.modulateProcessingTimeByPowerSatisfaction = true;
	}

	@Override
	public void preProcessUpdate() {
		powerSatisfaction = calculatePowerSatisfaction();
		powerUsage = calculatePowerUsage();
		super.preProcessUpdate();
	}

	@Override
	protected void onProcessingProgressMade(ProcessingContainer processingContainer) {
		super.onProcessingProgressMade(processingContainer);
		if (this.powerComponent != null) {
			this.powerComponent.drainPower(getPowerUsage(), false);
		}
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
		if (getDefaultPowerUsage() > powerComponent.getCapacity()) {
			return 0;
		}

		double currentTickSatisfaction = 0;
		if (getDefaultPowerUsage() == 0 || powerComponent == null) {
			currentTickSatisfaction = 1;
		} else if (powerComponent.getStoredPower() == powerComponent.getCapacity()) {
			currentTickSatisfaction = 1;
		} else {
			currentTickSatisfaction = SDMath.clamp(powerComponent.getAveragePowerAddedPerTick() / defaultPowerUsage, 0,
					1);
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
	public T setDefaultPowerUsage(double defaultPowerUsage) {
		this.defaultPowerUsage = defaultPowerUsage;
		return (T) this;
	}

	public final double getDefaultPowerUsage() {
		return defaultPowerUsage;
	}

	protected double calculatePowerUsage() {
		return defaultPowerUsage * powerSatisfaction;
	}

	public final double getPowerUsage() {
		return powerUsage;
	}

	public final double getPowerSatisfaction() {
		return powerSatisfaction;
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

	@Override
	protected void onProcessingStarted(ProcessingContainer processingContainer) {
		Optional<IProcessor> processingInterface = getProcessingOwner();
		if (processingInterface.isPresent()) {
			processingInterface.get().onProcessingStarted(this, processingContainer);
		}
	}

	@Override
	protected void onProcessingCanceled(ProcessingContainer processingContainer) {
		Optional<IProcessor> processingInterface = getProcessingOwner();
		if (processingInterface.isPresent()) {
			processingInterface.get().onProcessingCanceled(this, processingContainer);
		}
	}

	@Override
	protected void onProcessingPausedDueToError(ProcessingContainer processingContainer) {
		Optional<IProcessor> processingInterface = getProcessingOwner();
		if (processingInterface.isPresent()) {
			processingInterface.get().onProcessingPausedDueToError(this, processingContainer);
		}
	}

	@Override
	protected void onProcessingCompleted(ProcessingContainer processingContainer) {
		Optional<IProcessor> processingInterface = getProcessingOwner();
		if (processingInterface.isPresent()) {
			processingInterface.get().onProcessingCompleted(this, processingContainer);
		}
	}

	protected ProcessingCheckState checkPowerRequirements() {
		if (powerComponent == null) {
			return ProcessingCheckState.ok();
		}

		if (defaultPowerUsage > 0) {
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
	protected void updateProductionStatistics(TeamComponent teamComp) {
		super.updateProductionStatistics(teamComp);
		if (powerComponent != null && getPowerUsage() > 0) {
			getProductionToken(StaticCoreProductTypes.Power.get()).setConsumptionPerSecond(teamComp.getOwningTeam(),
					getPowerProductInterfaceId(), getPowerUsage() * 20, defaultPowerUsage * 20);
		}
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	protected K createSynchronizationPacket() {
		return (K) new BasicProcessingComponentSyncPacket(getPos(), this);
	}

	protected Optional<IProcessor> getProcessingOwner() {
		if (getBlockEntity() instanceof IProcessor) {
			return Optional.of((IProcessor) getBlockEntity());
		}
		return Optional.empty();
	}
}
