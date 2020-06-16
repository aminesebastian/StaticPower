package theking530.staticpower.tileentities.components;

import java.util.function.Supplier;

import javax.annotation.Nonnull;

import net.minecraft.nbt.CompoundNBT;

public class MachineProcessingComponent extends AbstractTileEntityComponent {
	private int processingTime;
	private int currentProcessingTime;
	private boolean processing;
	private boolean processingPaused;
	private Supplier<Boolean> processingEndedCallback;

	public MachineProcessingComponent(String name, int processingTime, @Nonnull Supplier<Boolean> onProcessingEnded) {
		super(name);
		this.processingEndedCallback = onProcessingEnded;
		this.processingTime = processingTime;
		this.processing = false;
	}

	public void preProcessUpdate() {
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

	public void startProcessing() {
		processing = true;
		processingPaused = false;
		currentProcessingTime = 0;
	}

	public void pauseProcessing() {
		processingPaused = true;
	}

	public void continueProcessing() {
		processingPaused = false;
	}

	public void cancelProcessing() {
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

	public int getProcessingTimeRemaining() {
		return currentProcessingTime;
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
