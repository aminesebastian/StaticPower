package theking530.staticcore.blockentity.components.control.processing;

public interface IProcessor {
	public default ProcessingCheckState canStartProcessing(AbstractProcessingComponent<?, ?> component,
			ProcessingContainer outputContainer) {
		return ProcessingCheckState.ok();
	}

	public default ProcessingCheckState canContinueProcessing(AbstractProcessingComponent<?, ?> component,
			ProcessingContainer outputContainer) {
		return ProcessingCheckState.ok();
	}

	public default ProcessingCheckState canCompleteProcessing(AbstractProcessingComponent<?, ?> component,
			ProcessingContainer outputContainer) {
		return ProcessingCheckState.ok();
	}

	public default void onProcessingStarted(AbstractProcessingComponent<?, ?> component,
			ProcessingContainer outputContainer) {
	}

	public default void onProcessingCanceled(AbstractProcessingComponent<?, ?> component,
			ProcessingContainer outputContainer) {
	}

	public default void onProcessingPausedDueToError(AbstractProcessingComponent<?, ?> component,
			ProcessingContainer outputContainer) {
	}

	public default void onProcessingCompleted(AbstractProcessingComponent<?, ?> component,
			ProcessingContainer outputContainer) {
	}

}
