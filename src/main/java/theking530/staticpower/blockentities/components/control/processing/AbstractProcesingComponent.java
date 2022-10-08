package theking530.staticpower.blockentities.components.control.processing;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.block.state.BlockState;
import theking530.api.energy.PowerStack;
import theking530.api.upgrades.UpgradeTypes;
import theking530.staticcore.gui.text.PowerTextFormatting;
import theking530.staticpower.StaticPower;
import theking530.staticpower.blockentities.components.AbstractBlockEntityComponent;
import theking530.staticpower.blockentities.components.control.ProcesingComponentSyncPacket;
import theking530.staticpower.blockentities.components.control.RedstoneControlComponent;
import theking530.staticpower.blockentities.components.energy.PowerStorageComponent;
import theking530.staticpower.blockentities.components.items.UpgradeInventoryComponent;
import theking530.staticpower.blockentities.components.items.UpgradeInventoryComponent.UpgradeItemWrapper;
import theking530.staticpower.blockentities.components.serialization.SaveSerialize;
import theking530.staticpower.blockentities.components.serialization.UpdateSerialize;
import theking530.staticpower.blocks.tileentity.StaticPowerMachineBlock;
import theking530.staticpower.network.StaticPowerMessageHandler;

public abstract class AbstractProcesingComponent<T extends AbstractProcesingComponent<?>> extends AbstractBlockEntityComponent {
	private static final int SYNC_PACKET_UPDATE_RADIUS = 32;
	private static final int SYNC_UPDATE_DELTA_THRESHOLD = 20;

	private boolean shouldControlOnBlockState;
	protected UpgradeInventoryComponent upgradeInventory;
	protected PowerStorageComponent powerComponent;
	protected RedstoneControlComponent redstoneControlComponent;

	@UpdateSerialize
	protected MutableComponent processingErrorMessage;
	@UpdateSerialize
	protected boolean processingStoppedDueToError;

	@UpdateSerialize
	private int processingTime;
	@UpdateSerialize
	private int currentProcessingTime;
	@SaveSerialize
	private int defaultProcessingTime;
	@SaveSerialize
	private int tickDownRate;

	@UpdateSerialize
	private boolean processing;
	@UpdateSerialize
	private boolean hasStarted;
	@UpdateSerialize
	private boolean processingPaused;

	@SaveSerialize
	private float processingSpeedUpgradeMultiplier;

	@UpdateSerialize
	protected double powerUsage;
	@SaveSerialize
	protected double defaultPowerUsage;

	/**
	 * The power multiplier as calculated from the speed upgrade.
	 */
	@UpdateSerialize
	private float powerUsageIncreaseMultiplier;
	/**
	 * External power usage multiplier that can be used by implementers to set a
	 * power multiple. Defaults to 1.0f;
	 */
	@UpdateSerialize
	private float powerMultiplier;

	@SaveSerialize
	private final boolean serverOnly;
	@SaveSerialize
	private boolean performedWorkLastTick;
	@SaveSerialize
	private int blockStateOffTimer;

	private int lastSyncProcessingTime;

	public AbstractProcesingComponent(String name, int processingTime, boolean serverOnly) {
		super(name);
		this.processingTime = processingTime;
		this.defaultProcessingTime = processingTime;
		this.processing = false;
		this.hasStarted = false;
		this.serverOnly = serverOnly;
		this.shouldControlOnBlockState = false;
		this.tickDownRate = 1;
		this.performedWorkLastTick = false;
		this.processingSpeedUpgradeMultiplier = 1.0f;
		this.powerUsageIncreaseMultiplier = 1.0f;
		this.processingErrorMessage = new TextComponent("");
		this.processingStoppedDueToError = false;
		this.powerMultiplier = 1.0f;
	}

	@SuppressWarnings("resource")
	public void preProcessUpdate() {
		// Check for upgrades on the server.
		if (!getLevel().isClientSide) {
			checkUpgrades();
		}

		// If we should only run on the server, do nothing.
		if (serverOnly && getLevel().isClientSide) {
			return;
		}

		// Process.
		boolean performedWork = process();

		// Check for changing to the off state.
		if (!performedWork) {
			// If the block state is on, and processing is false, start the blockStateOff
			// timer. If it elapses, set the block state to off. Otherwise, start
			// incrementing it.
			if (getIsOnBlockState()) {
				if (blockStateOffTimer > 20) {
					setIsOnBlockState(false);
					blockStateOffTimer = 0;
				} else {
					blockStateOffTimer++;
				}
			}
		} else {
			// Reset the off timer.
			blockStateOffTimer = 0;
			// Update the block's on state.
			setIsOnBlockState(true);
		}
		// Set that we performed work on the last tick.
		performedWorkLastTick = performedWork;
	}

	public boolean process() {
		if (!isClientSide()) {
			sendSynchronizationPacket();
		}

		// Check if we have not started.
		if (!hasStarted) {
			// If we have not, check the starting state.
			ProcessingCheckState startProcessingState = internalCanStartProcessing();
			// If it is an error, set the error info, otherwise, determine if it was okay
			// and we can start.
			if (startProcessingState.isError()) {
				processingStoppedDueToError = true;
				processingErrorMessage = startProcessingState.getErrorMessage();
				onProcessingPausedDueToError();
				return false;
			} else {
				// Set the error state to false.
				processingStoppedDueToError = false;
				// If we're okay, start processing.
				if (startProcessingState.isOk()) {
					startProcessing();
				}
			}
		}

		// Set the can continue processing state if we have already started. This is to
		// allow for responsive stopping of processing if needed.
		if (hasStarted) {
			// Always set processing to false first.
			processing = false;

			// Check to see if we can continue processing.
			ProcessingCheckState canContinueProcessing = internalCanContinueProcessing();
			if (canContinueProcessing.isError()) {
				processingStoppedDueToError = true;
				processingErrorMessage = canContinueProcessing.getErrorMessage();
				onProcessingPausedDueToError();
				return false;
			} else {
				// Get out of the error state.
				processingStoppedDueToError = false;

				// Determine if we should pause or continue.
				if (canContinueProcessing.isOk()) {
					processing = true;
				} else if (canContinueProcessing.isCancel()) {
					cancelProcessing();
					return false;
				}
			}
		}

		// If we're currently processing.
		if (processing) {
			// If the processing is paused, do nothing.
			if (processingPaused) {
				return false;
			}

			// Use power if requested to.
			if (powerUsage > 0 && powerComponent != null && currentProcessingTime < processingTime) {
				powerComponent.drainPower(getPowerUsage(), false);
			}

			// If we can can continue processing, do so, otherwise, stop. If we have
			// completed the processing, try to complete it using the callback.
			// If the callback is true, we reset the state of the component back to initial
			if (currentProcessingTime >= processingTime) {
				ProcessingCheckState completedState = canCompleteProcessing();

				// If there is an error, freeze processing and just set the error message.
				if (completedState.isError()) {
					processingStoppedDueToError = true;
					processingErrorMessage = completedState.getErrorMessage();
					onProcessingPausedDueToError();
					return false;
				} else {
					// If it is cancel or an ok, finish processing. If it is skip, do nothing.
					if (completedState.isOk() || completedState.isCancel()) {
						// Stop processing since we completed.
						currentProcessingTime = 0;
						blockStateOffTimer = 0;
						processing = false;
						hasStarted = false;
						processingStoppedDueToError = false;
						onProcessingCompleted();
					}
				}
			} else {
				currentProcessingTime += tickDownRate;
			}
			return true;
		}
		return false;
	}

	/**
	 * Starts processing if this component was not already processing. If we were
	 * already processing, checks to see if we are paused, and unpauses.
	 */
	private void startProcessing() {
		// If we should only run on the server, do nothing.
		if (serverOnly && getLevel().isClientSide) {
			return;
		}

		// DO NOT CHANGE THIS LOGIC, VERY IMPORTANT IT REMAINS THE SAME.
		if (!processing) {
			processing = true;
			processingPaused = false;

			setIsOnBlockState(true);
			hasStarted = true;
			onProcessingStarted();

		} else if (processingPaused) {
			resumeProcessing();
		}
	}

	public void pauseProcessing() {
		// If we should only run on the server, do nothing.
		if (serverOnly && getLevel().isClientSide) {
			return;
		}
		setIsOnBlockState(false);
		processingPaused = true;
	}

	public void resumeProcessing() {
		// If we should only run on the server, do nothing.
		if (serverOnly && getLevel().isClientSide) {
			return;
		}
		setIsOnBlockState(true);
		processingPaused = false;
	}

	public void cancelProcessing() {
		// If we should only run on the server, do nothing.
		if (serverOnly && getLevel().isClientSide) {
			return;
		}
		currentProcessingTime = 0;
		processing = false;
		hasStarted = false;
		processingStoppedDueToError = false;
		onProcessingCanceled();
	}

	private ProcessingCheckState internalCanStartProcessing() {
		// Check the redstone state.
		ProcessingCheckState redstoneState;
		if (!(redstoneState = checkRedstoneState()).isOk()) {
			return redstoneState;
		}

		// Check the power state.
		ProcessingCheckState powerState;
		if (!(powerState = checkPowerRequirements()).isOk()) {
			return powerState;
		}

		// If the above are met, check the non-internal call.
		return canStartProcessing();
	}

	private ProcessingCheckState internalCanContinueProcessing() {
		// Check the redstone state.
		ProcessingCheckState redstoneState;
		if (!(redstoneState = checkRedstoneState()).isOk()) {
			return redstoneState;
		}

		// Check the power state.
		// Only do this if there is still processing to be done.
		ProcessingCheckState powerState;
		if (currentProcessingTime < processingTime && !(powerState = checkPowerRequirements()).isOk()) {
			return powerState;
		}

		// If the above are met, check the non-internal call.
		return canContinueProcessing();
	}

	protected abstract ProcessingCheckState canStartProcessing();

	protected abstract ProcessingCheckState canContinueProcessing();

	protected ProcessingCheckState canCompleteProcessing() {
		return ProcessingCheckState.ok();
	}

	protected void onProcessingStarted() {

	}

	protected void onProcessingCanceled() {

	}

	protected void onProcessingPausedDueToError() {

	}

	protected void onProcessingCompleted() {

	}

	/**
	 * Returns true if the current processing time is equal to the maximum
	 * processing time for this component. This is useful to check in combination
	 * with {@link #hasProcessingStarted()} to consider whether or not to consumer power.
	 * For example, {@link #hasProcessingStarted()} may return true even when
	 * {@link #isDone()} is true because {@link #hasProcessingStarted()} waits until
	 * {@link #processingEndedCallback} returns true. So if a user has a process
	 * that only completes when it is able to put an item into an inventory and that
	 * inventory is full, it will continually be processing. But it will have
	 * completed the work and thereform shouldn't take any more power.
	 * 
	 * @return
	 */
	public boolean isDone() {
		return currentProcessingTime > processingTime;
	}

	/**
	 * Helper method to determine the processing component is actually performing
	 * work. This is useful to check for triggering power draw. If this returns
	 * true, it indicates that this component is doing work that should cost power.
	 * 
	 * @return
	 */
	public boolean isCurrentlyProcessing() {
		return performedWorkLastTick;
	}

	public boolean hasProcessingStarted() {
		return hasStarted;
	}

	public boolean isProcessingPaused() {
		return processingPaused;
	}

	public int getCurrentProcessingTime() {
		return currentProcessingTime;
	}

	public int getMaxProcessingTime() {
		return processingTime;
	}

	public void setMaxProcessingTime(int newTime) {
		defaultProcessingTime = newTime;
		processingTime = (int) (defaultProcessingTime * processingSpeedUpgradeMultiplier);
		if (processingTime == 0) {
			processingTime = 1;
		}
	}

	public float getCalculatedPowerUsageMultipler() {
		return powerUsageIncreaseMultiplier;
	}

	public float getCalculatedHeatGenerationMultiplier() {
		return powerUsageIncreaseMultiplier;
	}

	@SuppressWarnings("unchecked")
	public T setUpgradeInventory(UpgradeInventoryComponent inventory) {
		upgradeInventory = inventory;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T setPowerComponent(PowerStorageComponent energyComponent) {
		this.powerComponent = energyComponent;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T setProcessingPowerUsage(double power) {
		if (power <= 0) {
			return (T) this;
		}

		defaultPowerUsage = power;
		powerUsage = defaultPowerUsage * powerUsageIncreaseMultiplier;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T setRedstoneControlComponent(RedstoneControlComponent redstoneControlComponent) {
		this.redstoneControlComponent = redstoneControlComponent;
		return (T) this;
	}

	/**
	 * Sets this machine processing component responsible for maintaining the IS_ON
	 * blockstate of the owning tile entity's block.
	 * 
	 * @param shouldControl
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public T setShouldControlBlockState(boolean shouldControl) {
		this.shouldControlOnBlockState = shouldControl;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T setPowerUsageMuiltiplier(float multiplier) {
		powerMultiplier = multiplier;
		return (T) this;
	}

	public float getPowerUsageMultiplier() {
		return powerMultiplier;
	}

	public double getPowerUsage() {
		return powerUsage * powerMultiplier;
	}

	protected void checkUpgrades() {
		// Do nothing if there is no upgrade inventory.
		if (upgradeInventory == null) {
			return;
		}
		// Get the speed upgrade.
		UpgradeItemWrapper speedUpgrade = upgradeInventory.getMaxTierItemForUpgradeType(UpgradeTypes.SPEED);

		// If it is not valid, set the values back to the defaults. Otherwise, set the
		// new processing speeds.
		if (speedUpgrade.isEmpty()) {
			processingSpeedUpgradeMultiplier = 1.0f;
			powerUsageIncreaseMultiplier = 1.0f;
		} else {
			processingSpeedUpgradeMultiplier = (float) (1.0f + (speedUpgrade.getTier().upgradeConfiguration.processingSpeedUpgrade.get()) * speedUpgrade.getUpgradeWeight());
			powerUsageIncreaseMultiplier = (float) (1.0f + (speedUpgrade.getTier().upgradeConfiguration.processingSpeedPowerCost.get()) * speedUpgrade.getUpgradeWeight());
		}

		// Set the processing time.
		processingTime = (int) (defaultProcessingTime / processingSpeedUpgradeMultiplier);
		if (processingTime == 0) {
			processingTime = 1;
		}

		// Set the power usages.
		powerUsage = (int) (defaultPowerUsage * powerUsageIncreaseMultiplier);
	}

	/**
	 * Gets how many time units are processed per tick.
	 * 
	 * @return
	 */
	public int getTimeUnitsPerTick() {
		return tickDownRate;
	}

	/**
	 * Sets how many units of time should be processed per tick. The default value
	 * is usually desired (1).
	 * 
	 * @param tickDownRate
	 */
	public void setTimeUnitsPerTick(int tickDownRate) {
		this.tickDownRate = tickDownRate;
	}

	public int getProgressScaled(int scaleValue) {
		return (int) (((float) (currentProcessingTime) / processingTime) * scaleValue);
	}

	public MutableComponent getProcessingErrorMessage() {
		return processingErrorMessage;
	}

	protected void setProcessingErrorMessage(MutableComponent errorMessage) {
		processingErrorMessage = errorMessage;
	}

	public boolean isProcessingStoppedDueToError() {
		return processingStoppedDueToError;
	}

	protected ProcessingCheckState checkPowerRequirements() {
		// If we have no power storage, then we're good.
		if (powerComponent == null) {
			return ProcessingCheckState.ok();
		}

		// Check the processing power cost.
		if (powerUsage > 0) {
			if (powerComponent != null) {
				if (powerComponent.getStoredPower() < getPowerUsage()) {
					return ProcessingCheckState.error(new TextComponent("Not Enough Power!").getString());
				}

				// Check the processing power rate.
				PowerStack drainedPower = powerComponent.drainPower(getPowerUsage(), true);
				if (drainedPower.getPower() < getPowerUsage()) {
					return ProcessingCheckState.error(new TextComponent("Recipe's power per tick requirement (")
							.append(PowerTextFormatting.formatPowerRateToString(getPowerUsage())).append(") is larger than the amount this machine can handle!").getString());
				}
			}
		}

		// If we made it this far, return true.
		return ProcessingCheckState.ok();
	}

	protected ProcessingCheckState checkRedstoneState() {
		// Check the redstone control component.
		if (redstoneControlComponent != null && !redstoneControlComponent.passesRedstoneCheck()) {
			return ProcessingCheckState.error(new TextComponent("Redstone Control Mode Not Satisfied.").getString());
		}

		return ProcessingCheckState.ok();
	}

	@SuppressWarnings("resource")
	protected void setIsOnBlockState(boolean on) {
		if (!getLevel().isClientSide && shouldControlOnBlockState) {
			BlockState currentState = getLevel().getBlockState(getPos());
			if (currentState.hasProperty(StaticPowerMachineBlock.IS_ON)) {
				if (currentState.getValue(StaticPowerMachineBlock.IS_ON) != on) {
					getLevel().setBlock(getPos(), currentState.setValue(StaticPowerMachineBlock.IS_ON, on), 2);
				}
			}
		}
	}

	public boolean getIsOnBlockState() {
		if (!shouldControlOnBlockState) {
			return false;
		}
		BlockState currentState = getLevel().getBlockState(getPos());
		if (currentState.hasProperty(StaticPowerMachineBlock.IS_ON)) {
			return currentState.getValue(StaticPowerMachineBlock.IS_ON);
		}
		return false;
	}

	protected void sendSynchronizationPacket() {
		if (getLevel().isClientSide()) {
			StaticPower.LOGGER.warn("#synchronizeToClient (called at %1$s) should only be called from the server!", getPos().toString());
			return;
		}

		boolean shouldSync = Math.abs(lastSyncProcessingTime - this.currentProcessingTime) >= SYNC_UPDATE_DELTA_THRESHOLD;
		shouldSync |= lastSyncProcessingTime == 0 && currentProcessingTime != 0;
		shouldSync |= currentProcessingTime == 0 && lastSyncProcessingTime != 0;

		if (shouldSync) {
			lastSyncProcessingTime = currentProcessingTime;
			// Send the packet to all clients within the requested radius.
			ProcesingComponentSyncPacket msg = new ProcesingComponentSyncPacket(getPos(), this);
			StaticPowerMessageHandler.sendMessageToPlayerInArea(StaticPowerMessageHandler.MAIN_PACKET_CHANNEL, getLevel(), getPos(), SYNC_PACKET_UPDATE_RADIUS, msg);
		}
	}

	public CompoundTag serializeClientSynchronizeData(CompoundTag nbt, boolean fromUpdate) {
		this.serializeUpdateNbt(nbt, fromUpdate);

		return nbt;
	}

	public void recieveClientSynchronizeData(CompoundTag nbt, boolean fromUpdate) {
		this.deserializeUpdateNbt(nbt, fromUpdate);

	}

}
