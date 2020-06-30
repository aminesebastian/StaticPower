package theking530.staticpower.tileentities.components;

import java.util.function.Supplier;

import javax.annotation.Nonnull;

import net.minecraft.nbt.CompoundNBT;

public class MachineProcessingComponent extends AbstractTileEntityComponent {
	private final boolean serverOnly;
	private final Supplier<Boolean> canStartProcessingCallback;
	private final Supplier<Boolean> canContinueProcessingCallback;
	private final Supplier<Boolean> processingEndedCallback;

	private int processingTime;
	private int currentProcessingTime;
	private boolean processing;
	private boolean processingPaused;

	public MachineProcessingComponent(String name, int processingTime, @Nonnull Supplier<Boolean> canStartProcessingCallback, @Nonnull Supplier<Boolean> canContinueProcessingCallback, @Nonnull Supplier<Boolean> processingEndedCallback,
			boolean serverOnly) {
		super(name);
		this.canStartProcessingCallback = canStartProcessingCallback;
		this.canContinueProcessingCallback = canContinueProcessingCallback;
		this.processingEndedCallback = processingEndedCallback;
		this.processingTime = processingTime;
		this.processing = false;
		this.serverOnly = serverOnly;
	}

	public MachineProcessingComponent(String name, int processingTime, @Nonnull Supplier<Boolean> processingEndedCallback, boolean serverOnly) {
		this(name, processingTime, () -> false, () -> true, processingEndedCallback, serverOnly);
	}

	public void preProcessUpdate() {
		// If we should only run on the server, do nothing.
		if (serverOnly && getWorld().isRemote) {
			return;
		}

		if (!processing && !processingPaused && canStartProcessingCallback.get()) {
			startProcessing();
		}

		if (processing) {
			if (processingPaused) {
				return;
			}
			if (canContinueProcessingCallback.get()) {
				if (currentProcessingTime < processingTime) {
					currentProcessingTime++;
				}
				if (currentProcessingTime >= processingTime) {
					if (processingCompleted()) {
						currentProcessingTime = 0;
						processing = false;
					}
				}
			} else {
				processing = false;
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
		} else if (processingPaused) {
			continueProcessing();
		}
	}

	public void pauseProcessing() {
		// If we should only run on the server, do nothing.
		if (serverOnly && getWorld().isRemote) {
			return;
		}

		processingPaused = true;
	}

	public void continueProcessing() {
		// If we should only run on the server, do nothing.
		if (serverOnly && getWorld().isRemote) {
			return;
		}

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
		return processingEndedCallback.get();
	}

	public boolean isProcessing() {
		return processing;
	}

	public boolean isProcessingPaused() {
		return processingPaused;
	}

	public int getCurrentProcessingTime() {
		return currentProcessingTime;
	}

	public int getProcessingTime() {
		return processingTime;
	}

	public void setProcessingTime(int newTime) {
		processingTime = newTime;
	}

	public int getProgressScaled(int scaleValue) {
		return (int) (((float) (currentProcessingTime) / processingTime) * scaleValue);
	}

	@Override
	public CompoundNBT serializeUpdateNbt(CompoundNBT nbt, boolean fromUpdate) {
		super.serializeUpdateNbt(nbt, fromUpdate);
		nbt.putBoolean("processing", processing);
		nbt.putBoolean("processingPaused", processingPaused);
		nbt.putInt("currentTime", currentProcessingTime);
		return nbt;
	}

	@Override
	public void deserializeUpdateNbt(CompoundNBT nbt, boolean fromUpdate) {
		super.deserializeUpdateNbt(nbt, fromUpdate);
		processing = nbt.getBoolean("processing");
		processingPaused = nbt.getBoolean("processingPaused");
		currentProcessingTime = nbt.getInt("currentTime");
	}
}
