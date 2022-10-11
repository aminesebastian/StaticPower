package theking530.staticpower.blockentities.components.control.processing;

import java.util.function.Supplier;

import javax.annotation.Nonnull;

import theking530.staticpower.blockentities.components.serialization.SaveSerialize;

public class MachineProcessingComponent extends AbstractProcesingComponent<MachineProcessingComponent> {
	public static final int DEFAULT_MOVING_TIME = 4;

	protected Supplier<ProcessingCheckState> canStartProcessingCallback;
	protected Supplier<ProcessingCheckState> canContinueProcessingCallback;
	protected Supplier<ProcessingCheckState> processingEndedCallback;

	@SaveSerialize
	private final ProcessingOutputContainer outputContainer;
	private Runnable processingStartedCallback;

	public MachineProcessingComponent(String name, int processingTime, @Nonnull Supplier<ProcessingCheckState> canStartProcessingCallback,
			@Nonnull Supplier<ProcessingCheckState> canContinueProcessingCallback, @Nonnull Supplier<ProcessingCheckState> processingEndedCallback, boolean serverOnly) {
		super(name, processingTime, serverOnly);
		this.canStartProcessingCallback = canStartProcessingCallback;
		this.canContinueProcessingCallback = canContinueProcessingCallback;
		this.processingEndedCallback = processingEndedCallback;
		outputContainer = new ProcessingOutputContainer();
	}

	public MachineProcessingComponent(String name, int processingTime, @Nonnull Supplier<ProcessingCheckState> processingEndedCallback, boolean serverOnly) {
		this(name, processingTime, () -> ProcessingCheckState.error(""), () -> ProcessingCheckState.ok(), processingEndedCallback, serverOnly);
	}

	public static MachineProcessingComponent createMovingProcessingComponent(String name, @Nonnull Supplier<ProcessingCheckState> canStartProcessingCallback,
			@Nonnull Supplier<ProcessingCheckState> canContinueProcessingCallback, @Nonnull Supplier<ProcessingCheckState> processingEndedCallback, boolean serverOnly) {
		return new MachineProcessingComponent(name, DEFAULT_MOVING_TIME, processingEndedCallback, processingEndedCallback, processingEndedCallback, serverOnly);
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

	public ProcessingOutputContainer getOutputContainer() {
		return this.outputContainer;
	}

	public MachineProcessingComponent setProcessingStartedCallback(Runnable processingStartedCallback) {
		this.processingStartedCallback = processingStartedCallback;
		return this;
	}

}
