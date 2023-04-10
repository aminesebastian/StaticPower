package theking530.staticcore.blockentity.components.control.processing;

import theking530.staticcore.blockentity.components.control.processing.basic.BasicProcessingComponent;

public interface IProcessor {
	public default ProcessingCheckState canStartProcessing(BasicProcessingComponent<?, ?> component,
			ProcessingContainer outputContainer) {
		return ProcessingCheckState.ok();
	}

	public default ProcessingCheckState canContinueProcessing(BasicProcessingComponent<?, ?> component,
			ProcessingContainer outputContainer) {
		return ProcessingCheckState.ok();
	}

	public default ProcessingCheckState canCompleteProcessing(BasicProcessingComponent<?, ?> component,
			ProcessingContainer outputContainer) {
		return ProcessingCheckState.ok();
	}

	public default void onProcessingStarted(BasicProcessingComponent<?, ?> component,
			ProcessingContainer outputContainer) {
	}

	public default void onProcessingCanceled(BasicProcessingComponent<?, ?> component,
			ProcessingContainer outputContainer) {
	}

	public default void onProcessingPausedDueToError(BasicProcessingComponent<?, ?> component,
			ProcessingContainer outputContainer) {
	}

	public default void onProcessingCompleted(BasicProcessingComponent<?, ?> component,
			ProcessingContainer outputContainer) {
	}

}
