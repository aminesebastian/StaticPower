package theking530.staticpower.blockentities.components.control.processing.interfaces;

import net.minecraft.world.item.crafting.Recipe;
import theking530.staticpower.blockentities.components.control.processing.ProcessingCheckState;
import theking530.staticpower.blockentities.components.control.processing.ProcessingOutputContainer;
import theking530.staticpower.blockentities.components.control.processing.RecipeProcessingComponent;
import theking530.staticpower.data.crafting.RecipeMatchParameters;

public interface IRecipeProcessor<T extends Recipe<?>> {
	public RecipeMatchParameters getRecipeMatchParameters(RecipeProcessingComponent<T> component);

	public void captureInputsAndProducts(RecipeProcessingComponent<T> component, T recipe, ProcessingOutputContainer outputContainer);

	public ProcessingCheckState canStartProcessing(RecipeProcessingComponent<T> component, T recipe, ProcessingOutputContainer outputContainer);

	public void processingCompleted(RecipeProcessingComponent<T> component, T recipe, ProcessingOutputContainer outputContainer);

	public default ProcessingCheckState canContinueProcessing(RecipeProcessingComponent<T> component, T recipe, ProcessingOutputContainer outputContainer) {
		return canStartProcessing(component, recipe, outputContainer);
	}
}
