package theking530.staticcore.blockentity.components.control.processing.recipe;

import java.util.Optional;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import theking530.staticcore.blockentity.components.control.processing.ProcessingCheckState;
import theking530.staticcore.blockentity.components.control.processing.ProcessingContainer;
import theking530.staticcore.blockentity.components.control.processing.ProcessingOutputContainer;
import theking530.staticcore.blockentity.components.control.processing.basic.BasicProcessingComponent;
import theking530.staticcore.container.FakeCraftingInventory;
import theking530.staticcore.crafting.AbstractMachineRecipe;
import theking530.staticcore.crafting.CraftingUtilities;
import theking530.staticcore.crafting.RecipeMatchParameters;

public class RecipeProcessingComponent<T extends Recipe<?>>
		extends BasicProcessingComponent<RecipeProcessingComponent<T>, RecipeProcessingComponentSyncPacket> {

	public static final int MOVE_TIME = 8;
	private final RecipeType<T> recipeType;
	private ResourceLocation recipeId;
	private ProcessingOutputContainer outputContainer;

	public RecipeProcessingComponent(String name, int processingTime, RecipeType<T> recipeType) {
		super(name, processingTime);
		this.recipeType = recipeType;
		this.setPreProductionTime(MOVE_TIME);
		this.recipeId = null;
		this.outputContainer = new ProcessingOutputContainer();
	}

	public RecipeType<T> getRecipeType() {
		return recipeType;
	}

	@Override
	protected ProcessingCheckState canStartProcessing() {
		outputContainer.clear();
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
			setDefaultPowerUsage(machineRecipe.getPowerCost());
			setDefaultMaxProcessingTime(machineRecipe.getProcessingTime());
		}

		outputContainer.open();
		recipeId = recipe.getId();
		ProcessingCheckState captureResult = getRecipeProcessingOwner().get().captureOutputs(this, recipe,
				outputContainer);
		outputContainer.close();

		if (!captureResult.isOk()) {
			return captureResult;
		}

		return super.canStartProcessing();
	}

	@Override
	protected void onProcessingStarted(ProcessingContainer processingContainer) {
		RecipeMatchParameters params = getRecipeProcessingOwner().get().getRecipeMatchParameters(this);
		Optional<T> recipeCheck = getRecipe(params);
		T recipe = recipeCheck.get();
		getRecipeProcessingOwner().get().onRecipeProcessingStarted(this, recipe, outputContainer, processingContainer);
		processingContainer.mergeOutputContainer(outputContainer);
	}

	@Override
	protected void resetToIdle() {
		super.resetToIdle();
		this.recipeId = null;
	}

	@Override
	protected ProcessingCheckState canContinueProcessing() {
		ProcessingCheckState superCheck = super.canContinueProcessing();
		if (!superCheck.isOk()) {
			return superCheck;
		}

		if (getProcessingRecipe().isEmpty()) {
			return ProcessingCheckState.cancel();
		}

		return ProcessingCheckState.ok();
	}

	@Override
	protected ProcessingCheckState canCompleteProcessing() {
		ProcessingCheckState superCheck = super.canCompleteProcessing();
		if (!superCheck.isOk()) {
			return superCheck;
		}

		if (getProcessingRecipe().isEmpty()) {
			return ProcessingCheckState.cancel();
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
