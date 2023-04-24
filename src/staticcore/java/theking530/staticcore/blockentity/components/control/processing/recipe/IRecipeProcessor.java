package theking530.staticcore.blockentity.components.control.processing.recipe;

import net.minecraft.world.item.crafting.Recipe;
import theking530.staticcore.blockentity.components.control.processing.ConcretizedProductContainer;
import theking530.staticcore.blockentity.components.control.processing.IProcessor;
import theking530.staticcore.blockentity.components.control.processing.ProcessingCheckState;
import theking530.staticcore.blockentity.components.control.processing.ProcessingContainer;
import theking530.staticcore.crafting.RecipeMatchParameters;

public interface IRecipeProcessor<T extends Recipe<?>> extends IProcessor<RecipeProcessingComponent<T>> {
	/**
	 * This method should return all the inputs that the recipes this processor is
	 * responsible for requires.
	 * 
	 * @param component
	 * @return
	 */
	public RecipeMatchParameters getRecipeMatchParameters(RecipeProcessingComponent<T> component);

	/**
	 * This is where implementers should capture all outputs. The value here is you
	 * can capture any outputs that need to be concretized (eg. Outputs that have
	 * random chance, etc).
	 * 
	 * @param component
	 * @param recipe
	 * @param outputContainer
	 */
	public void captureOutputs(RecipeProcessingComponent<T> component, T recipe,
			ConcretizedProductContainer outputContainer);

	/**
	 * This is where implementers can modify the processing component and set
	 * processing times/power usage/heat usage, etc.
	 * 
	 * @param component
	 * @param recipe
	 * @param outputContainer
	 * @return
	 */
	public default void prepareComponentForProcessing(RecipeProcessingComponent<T> component, T recipe,
			ConcretizedProductContainer outputContainer) {
	}

	/**
	 * This is where implementers should check for any recipe specific conditions
	 * that should be met before processing starts.
	 * 
	 * @param component
	 * @param recipe          The recipe that is pending processing.
	 * @param outputContainer All outputs from this recipe should be populated into
	 *                        this container.
	 * @return
	 */
	public ProcessingCheckState canStartProcessingRecipe(RecipeProcessingComponent<T> component, T recipe,
			ConcretizedProductContainer outputContainer);

	/**
	 * This is where implementers should capture all inputs. This method is called
	 * when processing starts so the inputs here can and should be removed from
	 * input inventories. Any items added here will be dropped if the owning tile
	 * entity is broken during production.
	 * 
	 * @param component
	 * @param recipe
	 * @param inputContainer
	 */
	public void captureInputs(RecipeProcessingComponent<T> component, T recipe, ProcessingContainer processingContainer,
			ConcretizedProductContainer inputContainer);

}
