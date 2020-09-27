package theking530.staticpower.tileentities.components.control;

import net.minecraft.block.BlockState;
import net.minecraft.util.text.StringTextComponent;
import theking530.api.IUpgradeItem.UpgradeType;
import theking530.staticpower.blocks.tileentity.StaticPowerMachineBlock;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.tileentities.components.AbstractTileEntityComponent;
import theking530.staticpower.tileentities.components.items.UpgradeInventoryComponent;
import theking530.staticpower.tileentities.components.items.UpgradeInventoryComponent.UpgradeItemWrapper;
import theking530.staticpower.tileentities.components.power.EnergyStorageComponent;
import theking530.staticpower.tileentities.components.serialization.SaveSerialize;
import theking530.staticpower.tileentities.components.serialization.UpdateSerialize;

public abstract class AbstractProcesingComponent extends AbstractTileEntityComponent {
	private boolean shouldControlOnBlockState;
	protected UpgradeInventoryComponent upgradeInventory;
	protected EnergyStorageComponent powerComponent;
	protected RedstoneControlComponent redstoneControlComponent;

	@UpdateSerialize
	protected String processingErrorMessage;
	@UpdateSerialize
	protected boolean processingStoppedDueToError;

	@UpdateSerialize
	private int processingTime;
	@UpdateSerialize
	private int currentProcessingTime;
	@SaveSerialize
	private int tickDownRate;
	@SaveSerialize
	private int defaultProcessingTime;

	@UpdateSerialize
	private boolean processing;
	@UpdateSerialize
	private boolean hasStarted;
	@UpdateSerialize
	private boolean processingPaused;

	@SaveSerialize
	private float processingSpeedUpgradeMultiplier;

	@UpdateSerialize
	protected int powerUsage;
	@SaveSerialize
	protected int defaultPowerUsage;
	@SaveSerialize
	protected boolean hasProcessingPowerCost;

	@UpdateSerialize
	protected int completedPowerUsage;
	@SaveSerialize
	protected int completedDefaultPowerUsage;
	@SaveSerialize
	protected boolean hasCompletedPowerCost;

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
		this.hasProcessingPowerCost = false;
		this.powerUsageIncreaseMultiplier = 1.0f;
		this.hasCompletedPowerCost = false;
		this.processingErrorMessage = "";
		this.processingStoppedDueToError = false;
		this.powerMultiplier = 1.0f;
	}

	public void preProcessUpdate() {
		// Check for upgrades on the server.
		if (!getWorld().isRemote) {
			checkUpgrades();
		}

		// If we should only run on the server, do nothing.
		if (serverOnly && getWorld().isRemote) {
			return;
		}

		// Process.
		performedWorkLastTick = false;
		process();

		// Check for changing to the off state.
		if (!processing) {
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
			// Set that we performed work on the last tick.
			performedWorkLastTick = true;
		}
	}

	public void process() {
		// Check if we have not started.
		if (!hasStarted) {
			// If we have not, check the starting state.
			ProcessingCheckState startProcessingState = canStartProcessing();
			// If it is an error, set the error info, otherwise, determine if it was okay
			// and we can start.
			if (startProcessingState.isError()) {
				processingStoppedDueToError = true;
				processingErrorMessage = startProcessingState.getErrorMessage();
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
			ProcessingCheckState canContinueProcessing = canContinueProcessing();
			if (canContinueProcessing.isError()) {
				processingStoppedDueToError = true;
				processingErrorMessage = canContinueProcessing.getErrorMessage();
			} else {
				// Get out of the error state.
				processingStoppedDueToError = false;

				// Determine if we should pause or continue.
				if (canContinueProcessing.isOk()) {
					processing = true;
				} else if (canContinueProcessing.isCancel()) {
					cancelProcessing();
				}
			}
		}

		// If we're currently processing.
		if (processing) {
			// If the processing is paused, do nothing.
			if (processingPaused) {
				return;
			}

			// Use power if requested to.
			if (hasProcessingPowerCost && powerComponent != null && currentProcessingTime < processingTime) {
				powerComponent.getStorage().drainPower(getPowerUsage(), false);
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
				} else {
					// If it is okay, then complete processing.
					if (completedState.isOk()) {
						// Use the complete power if requested to.
						if (hasCompletedPowerCost && powerComponent != null) {
							powerComponent.useBulkPower(getCompletedPowerUsage());
						}
					}
					// If it is cancel or an ok, finish processing. If it is skip, do nothing.
					if (completedState.isOk() || completedState.isCancel()) {
						// Stop processing since we completed.
						currentProcessingTime = 0;
						blockStateOffTimer = 0;
						processing = false;
						hasStarted = false;
						processingStoppedDueToError = false;
					}
				}
			} else {
				currentProcessingTime += tickDownRate;
			}
		}
	}

	/**
	 * Starts processing if this component was not already processing. If we were
	 * already processing, checks to see if we are paused, and unpauses.
	 */
	public void startProcessing() {
		// If we should only run on the server, do nothing.
		if (serverOnly && getWorld().isRemote) {
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
			continueProcessing();
		}
	}

	public void pauseProcessing() {
		// If we should only run on the server, do nothing.
		if (serverOnly && getWorld().isRemote) {
			return;
		}
		setIsOnBlockState(false);
		processingPaused = true;
	}

	public void continueProcessing() {
		// If we should only run on the server, do nothing.
		if (serverOnly && getWorld().isRemote) {
			return;
		}
		setIsOnBlockState(true);
		processingPaused = false;
	}

	public void cancelProcessing() {
		// If we should only run on the server, do nothing.
		if (serverOnly && getWorld().isRemote) {
			return;
		}
		currentProcessingTime = 0;
		processing = false;
		hasStarted = false;
		processingStoppedDueToError = false;
	}

	protected ProcessingCheckState canStartProcessing() {
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

		// If the above are met, return ok.
		return ProcessingCheckState.ok();
	}

	protected ProcessingCheckState canContinueProcessing() {
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

		// If the above are met, return ok.
		return ProcessingCheckState.ok();
	}

	protected ProcessingCheckState canCompleteProcessing() {
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

		// If the above are met, return ok.
		return ProcessingCheckState.ok();
	}

	protected void onProcessingStarted() {

	}

	/**
	 * Returns true if the current processing time is equal to the maximum
	 * processing time for this component. This is useful to check in combination
	 * with {@link #isProcessing()} to consider whether or not to consumer power.
	 * For example, {@link #isProcessing()} may return true even when
	 * {@link #isDone()} is true because {@link #isProcessing()} waits until
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
	public boolean isPerformingWork() {
		return performedWorkLastTick;
	}

	public boolean isProcessing() {
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

	public AbstractProcesingComponent setUpgradeInventory(UpgradeInventoryComponent inventory) {
		upgradeInventory = inventory;
		return this;
	}

	public AbstractProcesingComponent setEnergyComponent(EnergyStorageComponent energyComponent) {
		this.powerComponent = energyComponent;
		return this;
	}

	public AbstractProcesingComponent setProcessingPowerUsage(int power) {
		hasProcessingPowerCost = true;
		defaultPowerUsage = power;
		powerUsage = (int) (defaultPowerUsage * powerUsageIncreaseMultiplier);
		return this;
	}

	public AbstractProcesingComponent setCompletedPowerUsage(int power) {
		hasCompletedPowerCost = true;
		completedDefaultPowerUsage = power;
		completedPowerUsage = (int) (completedDefaultPowerUsage * powerUsageIncreaseMultiplier);
		return this;
	}

	public AbstractProcesingComponent setPowerUsageMuiltiplier(float multiplier) {
		powerMultiplier = multiplier;
		return this;
	}

	public AbstractProcesingComponent disableProcessingPowerUsage() {
		hasProcessingPowerCost = false;
		return this;
	}

	public AbstractProcesingComponent disableCompletedPowerUsage() {
		hasCompletedPowerCost = false;
		return this;
	}

	public AbstractProcesingComponent setRedstoneControlComponent(RedstoneControlComponent redstoneControlComponent) {
		this.redstoneControlComponent = redstoneControlComponent;
		return this;
	}

	/**
	 * Sets this machine processing component responsible for maintaining the IS_ON
	 * blockstate of the owning tile entity's block.
	 * 
	 * @param shouldControl
	 * @return
	 */
	public AbstractProcesingComponent setShouldControlBlockState(boolean shouldControl) {
		this.shouldControlOnBlockState = shouldControl;
		return this;
	}

	public float getPowerUsageMultiplier() {
		return powerMultiplier;
	}

	public int getPowerUsage() {
		return (int) (powerUsage * powerMultiplier);
	}

	public int getCompletedPowerUsage() {
		return (int) (completedPowerUsage * powerMultiplier);
	}

	protected void checkUpgrades() {
		// Do nothing if there is no upgrade inventory.
		if (upgradeInventory == null) {
			return;
		}
		// Get the speed upgrade.
		UpgradeItemWrapper speedUpgrade = upgradeInventory.getMaxTierItemForUpgradeType(UpgradeType.SPEED);

		// If it is not valid, set the values back to the defaults. Otherwise, set the
		// new processing speeds.
		if (speedUpgrade.isEmpty()) {
			processingSpeedUpgradeMultiplier = 1.0f;
			powerUsageIncreaseMultiplier = 1.0f;
		} else {
			processingSpeedUpgradeMultiplier = (1.0f + speedUpgrade.getTier().getProcessingSpeedUpgrade())
					* speedUpgrade.getUpgradeWeight();
			powerUsageIncreaseMultiplier = (1.0f + speedUpgrade.getTier().getProcessingSpeedPowerCost())
					* speedUpgrade.getUpgradeWeight();
		}

		// Set the processing time.
		processingTime = (int) (defaultProcessingTime / processingSpeedUpgradeMultiplier);
		if (processingTime == 0) {
			processingTime = 1;
		}

		// Set the power usages.
		powerUsage = (int) (defaultPowerUsage * powerUsageIncreaseMultiplier);
		completedPowerUsage = (int) (completedDefaultPowerUsage * powerUsageIncreaseMultiplier);
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

	public String getProcessingErrorMessage() {
		return processingErrorMessage;
	}

	protected void setProcessingErrorMessage(String errorMessage) {
		processingErrorMessage = errorMessage;
	}

	public boolean isProcessingStoppedDueToError() {
		return processingStoppedDueToError;
	}

	protected ProcessingCheckState checkPowerRequirements() {
		// Check the processing power cost.
		if (hasProcessingPowerCost && !powerComponent.hasEnoughPower(getPowerUsage())) {
			return ProcessingCheckState.error(new StringTextComponent("Not Enough Power!").getString());
		}
		// Check the processing power rate.
		if (hasProcessingPowerCost && powerComponent.getStorage().getMaxDrain() < getPowerUsage()) {
			return ProcessingCheckState.error(new StringTextComponent("Recipe's power per tick requirement (")
					.append(GuiTextUtilities.formatEnergyRateToString(getPowerUsage()))
					.appendString(") is larger than the max for this machine!").getString());
		}

		// Check the completion power cost.
		if (hasCompletedPowerCost && !powerComponent.hasEnoughPower(getCompletedPowerUsage())) {
			return ProcessingCheckState.error(new StringTextComponent("Not Enough Power!").getString());
		}

		// If we made it this far, return true.
		return ProcessingCheckState.ok();
	}

	protected ProcessingCheckState checkRedstoneState() {
		// Check the redstone control component.
		if (redstoneControlComponent != null && !redstoneControlComponent.passesRedstoneCheck()) {
			return ProcessingCheckState
					.error(new StringTextComponent("Redstone Control Mode Not Satisfied.").getString());
		}

		return ProcessingCheckState.ok();
	}

	protected void setIsOnBlockState(boolean on) {
		if (!getWorld().isRemote && shouldControlOnBlockState) {
			BlockState currentState = getWorld().getBlockState(getPos());
			if (currentState.hasProperty(StaticPowerMachineBlock.IS_ON)) {
				if (currentState.get(StaticPowerMachineBlock.IS_ON) != on) {
					getWorld().setBlockState(getPos(), currentState.with(StaticPowerMachineBlock.IS_ON, on), 2);
				}
			}
		}
	}

	public boolean getIsOnBlockState() {
		if (!shouldControlOnBlockState) {
			return false;
		}
		BlockState currentState = getWorld().getBlockState(getPos());
		if (currentState.hasProperty(StaticPowerMachineBlock.IS_ON)) {
			return currentState.get(StaticPowerMachineBlock.IS_ON);
		}
		return false;
	}

	public static class ProcessingCheckState {
		public enum ProcessingState {
			SKIP, ERROR, OK, CANCEL
		}

		private final ProcessingState state;
		private final String errorMessage;

		private ProcessingCheckState(ProcessingState state, String errorMessage) {
			this.state = state;
			this.errorMessage = errorMessage;
		}

		public ProcessingState getState() {
			return state;
		}

		public String getErrorMessage() {
			return errorMessage;
		}

		public boolean isOk() {
			return state == ProcessingState.OK;
		}

		public boolean isError() {
			return state == ProcessingState.ERROR;
		}

		public boolean isSkip() {
			return state == ProcessingState.SKIP;
		}

		public boolean isCancel() {
			return state == ProcessingState.CANCEL;
		}

		public static ProcessingCheckState skip() {
			return new ProcessingCheckState(ProcessingState.SKIP, "");
		}

		public static ProcessingCheckState ok() {
			return new ProcessingCheckState(ProcessingState.OK, "");
		}

		public static ProcessingCheckState cancel() {
			return new ProcessingCheckState(ProcessingState.CANCEL, "");
		}

		public static ProcessingCheckState error(String errorMessage) {
			return new ProcessingCheckState(ProcessingState.ERROR, errorMessage);
		}

		public static ProcessingCheckState notCorrectFluid() {
			return new ProcessingCheckState(ProcessingState.ERROR, "Requires different input fluid.");
		}

		public static ProcessingCheckState notEnoughFluid() {
			return new ProcessingCheckState(ProcessingState.ERROR, "Requires more fluid.");
		}

		public static ProcessingCheckState fluidOutputFull() {
			return new ProcessingCheckState(ProcessingState.ERROR, "Output tank does not have enough space!");
		}

		public static ProcessingCheckState powerOutputFull() {
			return new ProcessingCheckState(ProcessingState.ERROR, "Energy storage is full!");
		}

		public static ProcessingCheckState outputTankCannotTakeFluid() {
			return new ProcessingCheckState(ProcessingState.ERROR,
					"Tank does not have enough space for recipe output.");
		}

		public static ProcessingCheckState outputFluidDoesNotMatch() {
			return new ProcessingCheckState(ProcessingState.ERROR, "Recipe fluid does not match fluid in tank.");
		}

		public static ProcessingCheckState internalInventoryNotEmpty() {
			return new ProcessingCheckState(ProcessingState.ERROR, "Machine's internal buffer not empty.");
		}

		public static ProcessingCheckState outputsCannotTakeRecipe() {
			return new ProcessingCheckState(ProcessingState.ERROR, "Recipe output cannot be placed in output slot(s).");
		}
	}
}
