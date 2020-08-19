package theking530.staticpower.tileentities.components.control;

import java.util.function.Supplier;

import javax.annotation.Nonnull;

import net.minecraft.block.BlockState;
import net.minecraft.util.text.StringTextComponent;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.items.upgrades.IUpgradeItem.UpgradeType;
import theking530.staticpower.tileentities.StaticPowerMachineBlock;
import theking530.staticpower.tileentities.components.AbstractTileEntityComponent;
import theking530.staticpower.tileentities.components.items.UpgradeInventoryComponent;
import theking530.staticpower.tileentities.components.items.UpgradeInventoryComponent.UpgradeItemWrapper;
import theking530.staticpower.tileentities.components.power.EnergyStorageComponent;
import theking530.staticpower.tileentities.components.serialization.SaveSerialize;
import theking530.staticpower.tileentities.components.serialization.UpdateSerialize;

public class MachineProcessingComponent extends AbstractTileEntityComponent {
	protected Supplier<ProcessingCheckState> canStartProcessingCallback;
	protected Supplier<ProcessingCheckState> canContinueProcessingCallback;
	protected Supplier<ProcessingCheckState> processingEndedCallback;

	private Runnable processingStartedCallback;
	private boolean shouldControlOnBlockState;
	private UpgradeInventoryComponent upgradeInventory;
	private EnergyStorageComponent powerComponent;
	private RedstoneControlComponent redstoneControlComponent;

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
	private int powerUsage;
	@SaveSerialize
	private int defaultPowerUsage;
	@SaveSerialize
	private boolean hasProcessingPowerCost;

	@UpdateSerialize
	private int completedPowerUsage;
	@SaveSerialize
	private int completedDefaultPowerUsage;
	@SaveSerialize
	private boolean hasCompletedPowerCost;

	@SaveSerialize
	private float powerUpgradeMultiplier;

	@SaveSerialize
	private final boolean serverOnly;
	@SaveSerialize
	private boolean performedWorkLastTick;
	@SaveSerialize
	private int blockStateOffTimer;

	public MachineProcessingComponent(String name, int processingTime, @Nonnull Supplier<ProcessingCheckState> canStartProcessingCallback,
			@Nonnull Supplier<ProcessingCheckState> canContinueProcessingCallback, @Nonnull Supplier<ProcessingCheckState> processingEndedCallback, boolean serverOnly) {
		super(name);
		this.canStartProcessingCallback = canStartProcessingCallback;
		this.canContinueProcessingCallback = canContinueProcessingCallback;
		this.processingEndedCallback = processingEndedCallback;
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
		this.powerUpgradeMultiplier = 1.0f;
		this.hasCompletedPowerCost = false;
		this.processingErrorMessage = "";
		this.processingStoppedDueToError = false;
	}

	public MachineProcessingComponent(String name, int processingTime, @Nonnull Supplier<ProcessingCheckState> processingEndedCallback, boolean serverOnly) {
		this(name, processingTime, () -> ProcessingCheckState.error(""), () -> ProcessingCheckState.ok(), processingEndedCallback, serverOnly);
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

		// Reset the performed work last tick.
		performedWorkLastTick = false;

		// If this is when we first start processing, raise the start processing event.
		if (!hasStarted) {
			// Get start check.
			ProcessingCheckState machineComponentStartState = passesAllProcessingStartChecks();
			if (!machineComponentStartState.isOk()) {
				processingStoppedDueToError = true;
				processingErrorMessage = machineComponentStartState.getErrorMessage();
			} else {
				// Get the start state.
				ProcessingCheckState processingState = canStartProcessingCallback.get();

				// If its okay, start processing. If its an error, raise the error. If its a
				// skip or cancel, do nothing.
				if (processingState.isOk()) {
					startProcessing();
				} else if (processingState.isError()) {
					processingStoppedDueToError = true;
					processingErrorMessage = processingState.getErrorMessage();
				}
			}

		}

		// Set the can continue processing state if we have already started. This is to
		// allow for responsive stopping of processing if needed.
		if (hasStarted) {
			// Check to see if we can continue processing.
			ProcessingCheckState machineComponentProcessingCheck = passesAllProcessingChecks();
			if (machineComponentProcessingCheck.isOk()) {
				// Get the continue state.
				ProcessingCheckState continueState = canContinueProcessingCallback.get();

				// Clear the states.
				processingErrorMessage = continueState.getErrorMessage();
				processingStoppedDueToError = false;
				processing = false;

				// If its a cancel, cancel processing.
				if (continueState.isCancel()) {
					cancelProcessing();
				} else if (continueState.isOk()) {
					// Just keep going.
					processing = true;
				} else if (continueState.isError()) {
					// Set the error info.
					processingErrorMessage = continueState.getErrorMessage();
					processingStoppedDueToError = true;
				}
			} else {
				processing = false;
			}
		}

		// If we're currently processing.
		if (processing) {
			// Reset the off timer.
			blockStateOffTimer = 0;

			// If the processing is paused, do nothing.
			if (processingPaused) {
				return;
			}

			// Update the block's on state.
			setIsOnBlockState(true);

			// Use power if requested to.
			if (hasProcessingPowerCost && powerComponent != null) {
				powerComponent.getStorage().extractEnergy(powerUsage, false);
			}

			performedWorkLastTick = true;

			// If we can can continue processing, do so, otherwise, stop. If we have
			// completed the processing, try to complete it using the callback.
			// If the callback is true, we reset the state of the component back to initial
			if (currentProcessingTime >= processingTime) {
				ProcessingCheckState completedState = processingCompleted();
				if (completedState.isOk()) {
					// Use the complete power if requested to.
					if (hasCompletedPowerCost && powerComponent != null) {
						powerComponent.useBulkPower(completedPowerUsage);
					}
				}
				if (!completedState.isError()) {
					currentProcessingTime = 0;
					blockStateOffTimer = 0;
					processing = false;
					hasStarted = false;
					processingStoppedDueToError = false;
				}
			} else {
				currentProcessingTime += tickDownRate;
			}
		} else {
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
			if (processingStartedCallback != null) {
				processingStartedCallback.run();
			}

		} else if (processingPaused) {
			continueProcessing();
		}
	}

	public MachineProcessingComponent setProcessingStartedCallback(Runnable callback) {
		processingStartedCallback = callback;
		return this;
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

	private ProcessingCheckState processingCompleted() {
		return processingEndedCallback.get();
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

	public MachineProcessingComponent setUpgradeInventory(UpgradeInventoryComponent inventory) {
		upgradeInventory = inventory;
		return this;
	}

	public MachineProcessingComponent setEnergyComponent(EnergyStorageComponent energyComponent) {
		this.powerComponent = energyComponent;
		return this;
	}

	public MachineProcessingComponent setProcessingPowerUsage(int power) {
		hasProcessingPowerCost = true;
		defaultPowerUsage = power;
		powerUsage = (int) (defaultPowerUsage * powerUpgradeMultiplier);
		return this;
	}

	public MachineProcessingComponent setCompletedPowerUsage(int power) {
		hasCompletedPowerCost = true;
		completedDefaultPowerUsage = power;
		completedPowerUsage = (int) (completedDefaultPowerUsage * powerUpgradeMultiplier);
		return this;
	}

	public int getPowerUsage() {
		return powerUsage;
	}

	public int getCompletedPowerUsage() {
		return completedPowerUsage;
	}

	public MachineProcessingComponent disableProcessingPowerUsage() {
		hasProcessingPowerCost = false;
		return this;
	}

	public MachineProcessingComponent disableCompletedPowerUsage() {
		hasCompletedPowerCost = false;
		return this;
	}

	public MachineProcessingComponent setRedstoneControlComponent(RedstoneControlComponent redstoneControlComponent) {
		this.redstoneControlComponent = redstoneControlComponent;
		return this;
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
			powerUpgradeMultiplier = 1.0f;
		} else {
			processingSpeedUpgradeMultiplier = (1.0f + speedUpgrade.getTier().getProcessingSpeedUpgrade()) * speedUpgrade.getUpgradeWeight();
			powerUpgradeMultiplier = (1.0f + speedUpgrade.getTier().getProcessingSpeedPowerCost()) * speedUpgrade.getUpgradeWeight();
		}

		// Set the processing time.
		processingTime = (int) (defaultProcessingTime / processingSpeedUpgradeMultiplier);
		if (processingTime == 0) {
			processingTime = 1;
		}

		// Set the power usages.
		powerUsage = (int) (defaultPowerUsage * powerUpgradeMultiplier);
		completedPowerUsage = (int) (completedDefaultPowerUsage * powerUpgradeMultiplier);
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

	public void setProcessingErrorMessage(String errorMessage) {
		processingErrorMessage = errorMessage;
	}

	public boolean isProcessingStoppedDueToError() {
		return processingStoppedDueToError;
	}

	/**
	 * Sets this machine processing component responsible for maintaining the IS_ON
	 * blockstate of the owning tile entity's block.
	 * 
	 * @param shouldControl
	 * @return
	 */
	public MachineProcessingComponent setShouldControlBlockState(boolean shouldControl) {
		this.shouldControlOnBlockState = shouldControl;
		return this;
	}

	protected ProcessingCheckState passesAllProcessingStartChecks() {
		ProcessingCheckState processingCheck = this.passesAllProcessingChecks();
		if (!processingCheck.isOk()) {
			return processingCheck;
		}
		// Check the processing power cost for the whole process.
		int powerCost = powerUsage * processingTime;
		if (hasProcessingPowerCost && !powerComponent.hasEnoughPower(powerCost)) {
			int missingPower = powerCost - powerComponent.getStorage().getEnergyStored();
			return ProcessingCheckState.error(new StringTextComponent("This recipe requires an additional ").appendSibling(GuiTextUtilities.formatEnergyToString(missingPower)).getFormattedText());
		}

		return ProcessingCheckState.ok();
	}

	protected ProcessingCheckState passesAllProcessingChecks() {
		// Check the processing power cost.
		if (hasProcessingPowerCost && !powerComponent.hasEnoughPower(powerUsage)) {
			return ProcessingCheckState.error(new StringTextComponent("Not Enough Power!").getFormattedText());
		}
		// Check the processing power rate.
		if (hasProcessingPowerCost && powerComponent.getStorage().getMaxExtract() < powerUsage) {
			return ProcessingCheckState.error(new StringTextComponent("Recipe's power per tick requirement (").appendSibling(GuiTextUtilities.formatEnergyRateToString(powerUsage))
					.appendText(") is larger than the max for this machine!").getFormattedText());
		}
		// Check the completion power cost.
		if (hasCompletedPowerCost && !powerComponent.hasEnoughPower(completedPowerUsage)) {
			return ProcessingCheckState.error(new StringTextComponent("Not Enough Power!").getFormattedText());
		}
		// Check the processing power rate.
		if (hasProcessingPowerCost && powerComponent.getStorage().getEnergyStored() < completedPowerUsage) {
			return ProcessingCheckState.error(new StringTextComponent("Max Power Draw Too Log!").getFormattedText());
		}
		// Check the redstone control component.
		if (redstoneControlComponent != null && !redstoneControlComponent.passesRedstoneCheck()) {
			return ProcessingCheckState.error(new StringTextComponent("Redstone Control Mode Not Satisfied.").getFormattedText());
		}

		return ProcessingCheckState.ok();
	}

	protected void setIsOnBlockState(boolean on) {
		if (!getWorld().isRemote && shouldControlOnBlockState) {
			BlockState currentState = getWorld().getBlockState(getPos());
			if (currentState.has(StaticPowerMachineBlock.IS_ON)) {
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
		if (currentState.has(StaticPowerMachineBlock.IS_ON)) {
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

		public static ProcessingCheckState powerOutputFull() {
			return new ProcessingCheckState(ProcessingState.ERROR, "Energy storage is full!");
		}

		public static ProcessingCheckState outputTankCannotTakeFluid() {
			return new ProcessingCheckState(ProcessingState.ERROR, "Tank does not have enough space for recipe output.");
		}

		public static ProcessingCheckState outputFluidDoesNotMatch() {
			return new ProcessingCheckState(ProcessingState.ERROR, "Recipe fluid does not match fluid in tank.");
		}

		public static ProcessingCheckState internalInventoryNotEmpty() {
			return new ProcessingCheckState(ProcessingState.ERROR, "Machine's internal buffer not empty.");
		}

		public static ProcessingCheckState outputsCannotTakeRecipe() {
			return new ProcessingCheckState(ProcessingState.ERROR, "Recipe output does not stack with current item in output slots.");
		}
	}
}
