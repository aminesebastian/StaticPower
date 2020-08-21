package theking530.staticpower.tileentities.components.control;

import java.util.function.Supplier;

import javax.annotation.Nonnull;

public class MachineProcessingComponent extends AbstractProcesingComponent {
	protected Supplier<ProcessingCheckState> canStartProcessingCallback;
	protected Supplier<ProcessingCheckState> canContinueProcessingCallback;
	protected Supplier<ProcessingCheckState> processingEndedCallback;

	private Runnable processingStartedCallback;

	public MachineProcessingComponent(String name, int processingTime, @Nonnull Supplier<ProcessingCheckState> canStartProcessingCallback,
			@Nonnull Supplier<ProcessingCheckState> canContinueProcessingCallback, @Nonnull Supplier<ProcessingCheckState> processingEndedCallback, boolean serverOnly) {
		super(name, processingTime, serverOnly);
		this.canStartProcessingCallback = canStartProcessingCallback;
		this.canContinueProcessingCallback = canContinueProcessingCallback;
		this.processingEndedCallback = processingEndedCallback;
	}

	public MachineProcessingComponent(String name, int processingTime, @Nonnull Supplier<ProcessingCheckState> processingEndedCallback, boolean serverOnly) {
		this(name, processingTime, () -> ProcessingCheckState.error(""), () -> ProcessingCheckState.ok(), processingEndedCallback, serverOnly);
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

		// If the above are met, return the callback's result.
		return canStartProcessingCallback.get();
	}

	protected ProcessingCheckState canContinueProcessing() {
		// Check the parent state.
		ProcessingCheckState superCall = super.canCompleteProcessing();
		if (!superCall.isOk()) {
			return superCall;
		}

		// If the above are met, return the callback's result.
		return canContinueProcessingCallback.get();
	}

	protected ProcessingCheckState canCompleteProcessing() {
		// Check the parent state.
		ProcessingCheckState superCall = super.canCompleteProcessing();
		if (!superCall.isOk()) {
			return superCall;
		}

		// If the above are met, return the callback's result.
		return processingEndedCallback.get();
	}

	@Override
	protected void onProcessingStarted() {
		if (processingStartedCallback != null) {
			processingStartedCallback.run();
		}
	}
}
