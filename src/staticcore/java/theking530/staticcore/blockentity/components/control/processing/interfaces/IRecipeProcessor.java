package theking530.staticcore.blockentity.components.control.processing.interfaces;

import net.minecraft.world.item.crafting.Recipe;
import theking530.staticcore.blockentity.components.control.processing.ProcessingCheckState;
import theking530.staticcore.blockentity.components.control.processing.ProcessingOutputContainer;
import theking530.staticcore.blockentity.components.control.processing.RecipeProcessingComponent;
import theking530.staticcore.crafting.RecipeMatchParameters;

public interface IRecipeProcessor<T extends Recipe<?>> {
	public RecipeMatchParameters getRecipeMatchParameters(RecipeProcessingComponent<T> component);

	public void captureInputsAndProducts(RecipeProcessingComponent<T> component, T recipe, ProcessingOutputContainer outputContainer);

	public ProcessingCheckState canStartProcessing(RecipeProcessingComponent<T> component, T recipe, ProcessingOutputContainer outputContainer);

	public default ProcessingCheckState canContinueProcessing(RecipeProcessingComponent<T> component, T recipe, ProcessingOutputContainer outputContainer) {
		return canStartProcessing(component, recipe, outputContainer);
	}

	public default void processingStarted(RecipeProcessingComponent<T> component, T recipe, ProcessingOutputContainer outputContainer) {

	}

	public default void processingCompleted(RecipeProcessingComponent<T> component, T recipe, ProcessingOutputContainer outputContainer) {
	}
}
