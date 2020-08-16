package theking530.staticpower.tileentities.components.control;

import java.util.function.Supplier;

import javax.annotation.Nonnull;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import theking530.staticpower.items.upgrades.IUpgradeItem.UpgradeType;
import theking530.staticpower.tileentities.StaticPowerMachineBlock;
import theking530.staticpower.tileentities.components.AbstractTileEntityComponent;
import theking530.staticpower.tileentities.components.items.UpgradeInventoryComponent;
import theking530.staticpower.tileentities.components.items.UpgradeInventoryComponent.UpgradeItemWrapper;

public class MachineProcessingComponent extends AbstractTileEntityComponent {
	private final boolean serverOnly;
	private final Supplier<Boolean> canStartProcessingCallback;
	private final Supplier<Boolean> canContinueProcessingCallback;
	private final Supplier<Boolean> processingEndedCallback;

	private Runnable processingStartedCallback;

	private int tickDownRate;
	private int procesingTimer;
	private int defaultProcessingTime;
	private int currentProcessingTime;
	private int blockStateOffTimer;
	private float upgradeMultiplier;
	private boolean processing;
	private boolean hasStarted;
	private boolean processingPaused;
	private boolean shouldControlOnBlockState;
	private boolean performedWorkLastTick;
	private UpgradeInventoryComponent upgradeInventory;

	public MachineProcessingComponent(String name, int processingTime, @Nonnull Supplier<Boolean> canStartProcessingCallback, @Nonnull Supplier<Boolean> canContinueProcessingCallback,
			@Nonnull Supplier<Boolean> processingEndedCallback, boolean serverOnly) {
		super(name);
		this.canStartProcessingCallback = canStartProcessingCallback;
		this.canContinueProcessingCallback = canContinueProcessingCallback;
		this.processingEndedCallback = processingEndedCallback;
		this.procesingTimer = processingTime;
		this.defaultProcessingTime = processingTime;
		this.processing = false;
		this.hasStarted = false;
		this.serverOnly = serverOnly;
		this.shouldControlOnBlockState = false;
		this.tickDownRate = 1;
		this.performedWorkLastTick = false;
		this.upgradeMultiplier = 1.0f;
	}

	public MachineProcessingComponent(String name, int processingTime, @Nonnull Supplier<Boolean> processingEndedCallback, boolean serverOnly) {
		this(name, processingTime, () -> false, () -> true, processingEndedCallback, serverOnly);
	}

	public void preProcessUpdate() {
		// Check for upgrades.
		checkUpgrades();

		// If we should only run on the server, do nothing.
		if (serverOnly && getWorld().isRemote) {
			return;
		}

		// Reset the performed work last tick.
		performedWorkLastTick = false;

		// If this is when we first start processing, raise the start processing event.
		if (!hasStarted && canStartProcessingCallback.get()) {
			startProcessing();
		}

		// Set the can continue processing state if we have already started. This is to
		// allow for responsive stopping of processing if needed.
		if (hasStarted) {
			processing = canContinueProcessingCallback.get();
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

			// If we can can continue processing, do so, otherwise, stop. If we have
			// completed the processing, try to complete it using the callback.
			// If the callback is true, we reset the state of the component back to initial
			currentProcessingTime += tickDownRate;
			performedWorkLastTick = true;
			if (currentProcessingTime >= procesingTimer) {
				if (processingCompleted()) {
					currentProcessingTime = 0;
					blockStateOffTimer = 0;
					processing = false;
					hasStarted = false;
				}
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
		processing = false;
		currentProcessingTime = 0;
	}

	private boolean processingCompleted() {
		if (processingEndedCallback.get()) {
			return true;
		}
		return false;
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
		return currentProcessingTime > procesingTimer;
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
		return procesingTimer;
	}

	public void setMaxProcessingTime(int newTime) {
		defaultProcessingTime = newTime;
		procesingTimer = (int) (defaultProcessingTime * upgradeMultiplier);
	}

	public MachineProcessingComponent setUpgradeInventory(UpgradeInventoryComponent inventory) {
		upgradeInventory = inventory;
		return this;
	}

	protected void checkUpgrades() {
		// Do nothing if there is no upgrade inventory.
		if (upgradeInventory == null) {
			return;
		}
		// Get the upgrade.
		UpgradeItemWrapper upgrade = upgradeInventory.getMaxTierItemForUpgradeType(UpgradeType.SPEED);

		// If it is not valid, set the values back to the defaults. Otherwise, set the
		// new processing speeds.
		if (upgrade.isEmpty()) {
			upgradeMultiplier = 1.0f;
		} else {
			upgradeMultiplier = upgrade.getTier().getProcessingSpeedUpgrade() * upgrade.getUpgradeWeight();
		}

		// Set the processing time.
		procesingTimer = (int) (defaultProcessingTime / upgradeMultiplier);
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
		return (int) (((float) (currentProcessingTime) / procesingTimer) * scaleValue);
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

	@Override
	public CompoundNBT serializeUpdateNbt(CompoundNBT nbt, boolean fromUpdate) {
		super.serializeUpdateNbt(nbt, fromUpdate);
		nbt.putBoolean("processing", processing);
		nbt.putBoolean("processing_paused", processingPaused);
		nbt.putInt("processing_time", procesingTimer);
		nbt.putInt("current_processing_time", currentProcessingTime);
		nbt.putInt("tick_down_rate", tickDownRate);
		nbt.putBoolean("has_started", hasStarted);
		nbt.putFloat("upgrade_multiplier", upgradeMultiplier);
		nbt.putInt("default_processing_time", defaultProcessingTime);
		return nbt;
	}

	@Override
	public void deserializeUpdateNbt(CompoundNBT nbt, boolean fromUpdate) {
		super.deserializeUpdateNbt(nbt, fromUpdate);
		processing = nbt.getBoolean("processing");
		processingPaused = nbt.getBoolean("processing_paused");
		procesingTimer = nbt.getInt("processing_time");
		currentProcessingTime = nbt.getInt("current_processing_time");
		tickDownRate = nbt.getInt("tick_down_rate");
		hasStarted = nbt.getBoolean("has_started");
		upgradeMultiplier = nbt.getFloat("upgrade_multiplier");
		defaultProcessingTime = nbt.getInt("default_processing_time");
	}
}
