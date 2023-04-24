package theking530.staticcore.blockentity.components.control.processing.recipe;

import java.util.Optional;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import theking530.staticcore.blockentity.components.control.processing.ConcretizedProductContainer;
import theking530.staticcore.blockentity.components.control.processing.ProcessingCheckState;
import theking530.staticcore.blockentity.components.control.processing.ProcessingContainer;
import theking530.staticcore.blockentity.components.control.processing.machine.AbstractMachineProcessingComponent;
import theking530.staticcore.container.FakeCraftingInventory;
import theking530.staticcore.crafting.AbstractMachineRecipe;
import theking530.staticcore.crafting.CraftingUtilities;
import theking530.staticcore.crafting.RecipeMatchParameters;

public class RecipeProcessingComponent<T extends Recipe<?>>
		extends AbstractMachineProcessingComponent<RecipeProcessingComponent<T>, RecipeProcessingComponentSyncPacket> {

	public static final int MOVE_TIME = 8;
	private final RecipeType<T> recipeType;
	private ResourceLocation recipeId;
	private ConcretizedProductContainer concretizedOutputContainer;

	public RecipeProcessingComponent(String name, int processingTime, RecipeType<T> recipeType) {
		super(name, processingTime);
		this.recipeType = recipeType;
		this.setPreProductionTime(MOVE_TIME);
		this.recipeId = null;
		this.concretizedOutputContainer = new ConcretizedProductContainer();
	}

	public RecipeType<T> getRecipeType() {
		return recipeType;
	}

	@Override
	protected ProcessingCheckState canStartProcessing() {
		concretizedOutputContainer.clear();
		RecipeMatchParameters params = getRecipeProcessingOwner().get().getRecipeMatchParameters(this);
		Optional<T> recipeCheck = getRecipe(params);
		if (!recipeCheck.isPresent()) {
			return ProcessingCheckState.skip();
		}

		// If we have a valid recipe, then check if it is a machine recipe. If so,
		// update the power usage and processing time.
		T recipe = recipeCheck.get();
		if (recipe instanceof AbstractMachineRecipe) {
			AbstractMachineRecipe machineRecipe = (AbstractMachineRecipe) recipe;
			setBasePowerUsage(machineRecipe.getPowerCost());
			setBaseProcessingTime(machineRecipe.getProcessingTime());
		}

		concretizedOutputContainer.open();
		recipeId = recipe.getId();
		getRecipeProcessingOwner().get().captureOutputs(this, recipe, concretizedOutputContainer);
		concretizedOutputContainer.close();

		getRecipeProcessingOwner().get().prepareComponentForProcessing(this, recipe, concretizedOutputContainer);

		ProcessingCheckState captureResult = getRecipeProcessingOwner().get().canStartProcessingRecipe(this, recipe,
				concretizedOutputContainer);

		if (!captureResult.isOk()) {
			return captureResult;
		}

		return super.canStartProcessing();
	}

	@Override
	protected void onProcessingStarted(ProcessingContainer processingContainer) {
		processingContainer.absorbIntoOutputs(concretizedOutputContainer);
		getRecipeProcessingOwner().get().captureInputs(this, getProcessingRecipe().get(), processingContainer,
				processingContainer.getInputs());
		super.onProcessingStarted(processingContainer);
	}

	@Override
	protected void resetToIdle() {
		super.resetToIdle();
		recipeId = null;
		concretizedOutputContainer.clear();
		concretizedOutputContainer.close();
	}

	@Override
	protected ProcessingCheckState canContinueProcessing() {
		if (getProcessingRecipe().isEmpty()) {
			return ProcessingCheckState.cancel();
		}

		ProcessingCheckState superCheck = super.canContinueProcessing();
		if (!superCheck.isOk()) {
			return superCheck;
		}

		return ProcessingCheckState.ok();
	}

	@Override
	protected ProcessingCheckState canCompleteProcessing() {
		if (getProcessingRecipe().isEmpty()) {
			return ProcessingCheckState.cancel();
		}

		ProcessingCheckState superCheck = super.canCompleteProcessing();
		if (!superCheck.isOk()) {
			return superCheck;
		}

		return ProcessingCheckState.ok();
	}

	public Optional<T> getPendingRecipe() {
		RecipeMatchParameters params = getRecipeProcessingOwner().get().getRecipeMatchParameters(this);
		return getRecipe(params);
	}

	@SuppressWarnings("unchecked")
	public Optional<T> getProcessingRecipe() {
		if (recipeId == null) {
			return Optional.empty();
		}

		Optional<? extends Recipe<?>> recipe = this.getLevel().getRecipeManager().byKey(recipeId);
		if (recipe.isEmpty() || recipe.get().getType() != recipeType) {
			return Optional.empty();
		}
		return Optional.of((T) recipe.get());
	}

	/**
	 * First attempts to get the recipe being currently processed. If we are not
	 * processing, then it attempts to get the pending recipe.
	 * 
	 * @return
	 */
	public Optional<T> getProcessingOrPendingRecipe() {
		Optional<T> recipe = this.getProcessingRecipe();
		if (!recipe.isPresent()) {
			recipe = this.getPendingRecipe();
		}
		return recipe;
	}

	@SuppressWarnings("unchecked")
	public Optional<T> getRecipe(RecipeMatchParameters matchParameters) {
		if (recipeType == RecipeType.SMELTING) {
			return (Optional<T>) CraftingUtilities.getRecipe(new SimpleContainer(matchParameters.getItems()[0]),
					getLevel());
		} else if (recipeType == RecipeType.CRAFTING) {
			FakeCraftingInventory craftingInv = new FakeCraftingInventory(3, 3);
			for (int i = 0; i < 9; i++) {
				craftingInv.setItem(i, matchParameters.getItems()[i]);
			}
			return (Optional<T>) CraftingUtilities.getRecipe(craftingInv, getLevel());
		} else {
			RecipeType<Recipe<RecipeMatchParameters>> castType = (RecipeType<Recipe<RecipeMatchParameters>>) recipeType;
			return (Optional<T>) CraftingUtilities.getRecipe(castType, matchParameters, getLevel());
		}
	}

	@Override
	protected RecipeProcessingComponentSyncPacket createSynchronizationPacket() {
		return new RecipeProcessingComponentSyncPacket(getPos(), this);
	}

	protected void handleClientSynchronizeData(RecipeProcessingComponentSyncPacket packet) {
		super.handleClientSynchronizeData(packet);
		this.recipeId = packet.getRecipeId();
	}

	@SuppressWarnings("unchecked")
	protected Optional<IRecipeProcessor<T>> getRecipeProcessingOwner() {
		if (getBlockEntity() instanceof IRecipeProcessor) {
			return Optional.of((IRecipeProcessor<T>) getBlockEntity());
		} else {
			throw new RuntimeException(
					"The `RecipeProcessingComponent` component requires the owning BlockEntity to implement `RecipeProcessingInterface`.");
		}
	}
}
