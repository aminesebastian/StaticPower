package theking530.staticcore.blockentity.components.control.processing.recipe;

import net.minecraft.world.item.crafting.Recipe;
import theking530.staticcore.blockentity.components.control.processing.IProcessor;
import theking530.staticcore.blockentity.components.control.processing.ProcessingCheckState;
import theking530.staticcore.blockentity.components.control.processing.ProcessingContainer;
import theking530.staticcore.blockentity.components.control.processing.ProcessingOutputContainer;
import theking530.staticcore.crafting.RecipeMatchParameters;

public interface IRecipeProcessor<T extends Recipe<?>> extends IProcessor {
	public RecipeMatchParameters getRecipeMatchParameters(RecipeProcessingComponent<T> component);

	public ProcessingCheckState captureOutputs(RecipeProcessingComponent<T> component, T recipe,
			ProcessingOutputContainer outputContainer);

	public void onRecipeProcessingStarted(RecipeProcessingComponent<T> component, T recipe,
			ProcessingOutputContainer outputContainer, ProcessingContainer processingContainer);
}
