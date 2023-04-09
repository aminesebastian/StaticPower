package theking530.staticcore.blockentity.components.control.oldprocessing.interfaces;

import net.minecraft.world.item.crafting.Recipe;
import theking530.staticcore.blockentity.components.control.oldprocessing.OldProcessingContainer;
import theking530.staticcore.blockentity.components.control.oldprocessing.OldRecipeProcessingComponent;
import theking530.staticcore.blockentity.components.control.processing.ProcessingCheckState;
import theking530.staticcore.crafting.RecipeMatchParameters;

public interface IOldRecipeProcessor<T extends Recipe<?>> {
	public RecipeMatchParameters getRecipeMatchParameters(OldRecipeProcessingComponent<T> component);

	public void captureInputsAndProducts(OldRecipeProcessingComponent<T> component, T recipe, OldProcessingContainer outputContainer);

	public ProcessingCheckState canStartProcessing(OldRecipeProcessingComponent<T> component, T recipe, OldProcessingContainer outputContainer);

	public default ProcessingCheckState canContinueProcessing(OldRecipeProcessingComponent<T> component, T recipe, OldProcessingContainer outputContainer) {
		return canStartProcessing(component, recipe, outputContainer);
	}

	public default void processingStarted(OldRecipeProcessingComponent<T> component, T recipe, OldProcessingContainer outputContainer) {

	}

	public default void processingCompleted(OldRecipeProcessingComponent<T> component, T recipe, OldProcessingContainer outputContainer) {
	}
}
