package theking530.staticpower.tileentities.components;

import java.util.function.Supplier;

import javax.annotation.Nonnull;

import net.minecraft.nbt.CompoundNBT;

public class MachineProcessingComponent extends AbstractTileEntityComponent {
	private int processingTime;
	private int currentProcessingTime;
	private boolean processing;
	private boolean processingPaused;
	private boolean serverOnly;
	private Supplier<Boolean> processingEndedCallback;

	public MachineProcessingComponent(String name, int processingTime, @Nonnull Supplier<Boolean> onProcessingCompleted, boolean serverOnly) {
		super(name);
		this.processingEndedCallback = onProcessingCompleted;
		this.processingTime = processingTime;
		this.processing = false;
		serverOnly = true;
	}

	public MachineProcessingComponent(String name, int processingTime, @Nonnull Supplier<Boolean> onProcessingCompleted) {
		this(name, processingTime, onProcessingCompleted, false);
	}

	public void preProcessUpdate() {
		// If we should only run on the server, do nothing.
		if (serverOnly && getWorld().isRemote) {
			return;
		}

		if (processing && !processingPaused) {
			if (currentProcessingTime < processingTime) {
				currentProcessingTime++;
			}
			if (currentProcessingTime >= processingTime) {
				if (processingCompleted()) {
					currentProcessingTime = 0;
					processing = false;
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
		
		if (!processing) {
			processing = true;
			processingPaused = false;
			currentProcessingTime = 0;
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
