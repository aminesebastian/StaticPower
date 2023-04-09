package theking530.staticcore.blockentity.components.control.oldprocessing;

import java.util.function.Supplier;

import javax.annotation.Nonnull;

import theking530.staticcore.blockentity.components.control.processing.ProcessingCheckState;
import theking530.staticcore.blockentity.components.serialization.SaveSerialize;

public class OldMachineProcessingComponent extends OldAbstractProcesingComponent<OldMachineProcessingComponent> {
	public static final int DEFAULT_MOVING_TIME = 4;

	protected Supplier<ProcessingCheckState> canStartProcessingCallback;
	protected Supplier<ProcessingCheckState> canContinueProcessingCallback;
	protected Supplier<ProcessingCheckState> processingEndedCallback;

	@SaveSerialize
	private final OldProcessingContainer outputContainer;
	private Runnable processingStartedCallback;

	public OldMachineProcessingComponent(String name, int processingTime, @Nonnull Supplier<ProcessingCheckState> canStartProcessingCallback,
			@Nonnull Supplier<ProcessingCheckState> canContinueProcessingCallback, @Nonnull Supplier<ProcessingCheckState> processingEndedCallback, boolean serverOnly) {
		super(name, processingTime, serverOnly);
		this.canStartProcessingCallback = canStartProcessingCallback;
		this.canContinueProcessingCallback = canContinueProcessingCallback;
		this.processingEndedCallback = processingEndedCallback;
		outputContainer = new OldProcessingContainer();
	}

	public OldMachineProcessingComponent(String name, int processingTime, @Nonnull Supplier<ProcessingCheckState> processingEndedCallback) {
		this(name, processingTime, () -> ProcessingCheckState.error(""), () -> ProcessingCheckState.ok(), processingEndedCallback, true);
	}

	public static OldMachineProcessingComponent createMovingProcessingComponent(String name, @Nonnull Supplier<ProcessingCheckState> canStartProcessingCallback,
			@Nonnull Supplier<ProcessingCheckState> canContinueProcessingCallback, @Nonnull Supplier<ProcessingCheckState> processingEndedCallback) {
		return new OldMachineProcessingComponent(name, DEFAULT_MOVING_TIME, processingEndedCallback, processingEndedCallback, processingEndedCallback, true);
	}

	@Override
	protected ProcessingCheckState canStartProcessing() {
		return canStartProcessingCallback.get();
	}

	@Override
	protected ProcessingCheckState canContinueProcessing() {
		return canContinueProcessingCallback.get();
	}

	@Override
	protected ProcessingCheckState canCompleteProcessing() {
		return processingEndedCallback.get();
	}

	@Override
	protected void onProcessingStarted() {
		if (processingStartedCallback != null) {
			processingStartedCallback.run();
		}
	}

	public OldProcessingContainer getOutputContainer() {
		return this.outputContainer;
	}

	public OldMachineProcessingComponent setProcessingStartedCallback(Runnable processingStartedCallback) {
		this.processingStartedCallback = processingStartedCallback;
		return this;
	}

}
