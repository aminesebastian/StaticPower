package theking530.staticcore.blockentity.components.control.processing;

public interface IProcessor<T extends AbstractProcessingComponent<?, ?>> {
	public default ProcessingCheckState canStartProcessing(T component, ProcessingContainer processingContainer) {
		return ProcessingCheckState.ok();
	}

	public default ProcessingCheckState canContinueProcessing(T component, ProcessingContainer processingContainer) {
		return ProcessingCheckState.ok();
	}

	public default ProcessingCheckState canCompleteProcessing(T component, ProcessingContainer processingContainer) {
		return ProcessingCheckState.ok();
	}

	public default void onProcessingStarted(T component, ProcessingContainer processingContainer) {
	}

	public default void onProcessingProgressMade(T component, ProcessingContainer processingContainer) {
		
	}

	public default void onProcessingCanceled(T component, ProcessingContainer processingContainer) {
	}

	public default void onProcessingPausedDueToError(T component, ProcessingContainer processingContainer) {
	}

	public default void onProcessingCompleted(T component, ProcessingContainer outputContainer) {
	}

}
