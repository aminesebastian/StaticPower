package theking530.staticpower.blockentities.components.control.processing;

import java.util.Optional;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import theking530.staticcore.productivity.ProductionTrackingToken;
import theking530.staticpower.blockentities.components.control.processing.interfaces.IRecipeProcessor;
import theking530.staticpower.blockentities.components.serialization.SaveSerialize;
import theking530.staticpower.blockentities.components.serialization.UpdateSerialize;
import theking530.staticpower.blockentities.components.team.TeamComponent;
import theking530.staticpower.container.FakeCraftingInventory;
import theking530.staticpower.data.crafting.AbstractMachineRecipe;
import theking530.staticpower.data.crafting.AbstractStaticPowerRecipe;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerRecipeRegistry;
import theking530.staticpower.data.crafting.wrappers.packager.PackagerRecipe;
import theking530.staticpower.init.ModProducts;

public class RecipeProcessingComponent<T extends Recipe<?>> extends AbstractProcesingComponent<RecipeProcessingComponent<T>> {
	public static final int MOVE_TIME = 8;

	private final RecipeType<T> recipeType;
	private final ProductionTrackingToken<ItemStack> productionToken;
	private final IRecipeProcessor<T> processor;

	@SaveSerialize
	private final ProcessingOutputContainer outputContainer;
	@UpdateSerialize
	private int moveTimer;
	@UpdateSerialize
	private int moveTime;

	public RecipeProcessingComponent(String name, RecipeType<T> recipeType, IRecipeProcessor<T> processor) {
		this(name, 0, recipeType, processor);
	}

	public RecipeProcessingComponent(String name, int processingTime, RecipeType<T> recipeType, IRecipeProcessor<T> processor) {
		super(name, processingTime, true);
		this.recipeType = recipeType;
		this.processor = processor;
		this.moveTime = MOVE_TIME;
		moveTimer = 0;
		productionToken = ModProducts.Item.get().getProductivityToken();
		outputContainer = new ProcessingOutputContainer();
	}

	@Override
	public boolean process() {
		if (!isProcessing()) {
			// Try to update the power cost and processing times if we have a valid recipe.
			Optional<T> recipe = getPendingRecipe();
			if (recipe.isPresent()) {
				if (recipe.get() instanceof AbstractMachineRecipe) {
					AbstractMachineRecipe machineRecipe = (AbstractMachineRecipe) recipe.get();
					setMaxProcessingTime(machineRecipe.getProcessingTime());
					setProcessingPowerUsage(machineRecipe.getPowerCost());
				}
			}

			ProcessingCheckState moveState = performMove();
			if (moveState.isError()) {
				this.processingErrorMessage = moveState.getErrorMessage();
				this.processingStoppedDueToError = true;
				return false;
			}

			// If the move was not successful, return early.
			if (!moveState.isOk()) {
				return false;
			}
		}
		updateProductionStatistics();
	}

	private void updateProductionStatistics() {
		if (!isClientSide()) {
			if (isProcessing()) {
				TeamComponent teamComp = getTileEntity().getComponent(TeamComponent.class);
				if (teamComp != null) {
					for (ItemStack output : outputContainer.getOutputItems()) {
						productionToken.setProductionPerSecond(teamComp.getOwningTeam(), output, 1.0 / (this.getMaxProcessingTime() / 20));
					}
					for (ItemStack input : outputContainer.getInputItems()) {
						productionToken.setConsumptionPerSection(teamComp.getOwningTeam(), input, 1.0 / (this.getMaxProcessingTime() / 20));
					}
				}
			} else {
				productionToken.invalidate();
			}
		}
	}

	public ProcessingOutputContainer getCurrentProcessingContainer() {
		return this.outputContainer;
	}

	public ProcessingCheckState performMove() {
		// Increment the move timer.
		moveTimer++;

		// Check if it elapsed..
		if (moveTimer > moveTime) {
			// Reset the move timer.
			moveTimer = 0;

			ProcessingCheckState canStart = canStartProcessing();
			if (canStart.isOk()) {
				moveTimer = 0;
				return ProcessingCheckState.ok();
			} else {
				outputContainer.clear();
				return canStart;
			}
		}
		outputContainer.clear();
		return ProcessingCheckState.skip();
	}

	@Override
	protected ProcessingCheckState canStartProcessing() {
		// Attempt to get the recipe. If it does not exist, skip.
		Optional<T> recipe = getPendingRecipe();
		if (!recipe.isPresent()) {
			return ProcessingCheckState.skip();
		}

		// If the recipe is valid, set the usage stats. We do this again here just in
		// case someone manually moved to the internal inventory.
		if (recipe.get() instanceof AbstractMachineRecipe) {
			AbstractMachineRecipe machineRecipe = (AbstractMachineRecipe) recipe.get();
			setMaxProcessingTime(machineRecipe.getProcessingTime());
			setProcessingPowerUsage(machineRecipe.getPowerCost());
		}

		// This is the ONLY time we should ever be opening this output container.
		outputContainer.open(recipe.get().getId());
		processor.captureInputsAndProducts(this, recipe.get(), outputContainer);
		outputContainer.close();
		return processor.canStartProcessing(this, recipe.get(), outputContainer);
	}

	@Override
	protected ProcessingCheckState canContinueProcessing() {
		// Get the recipe. Skip if it does not exist.
		Optional<T> recipe = getCurrentlyProcessingRecipe();
		if (!recipe.isPresent()) {
			return ProcessingCheckState.ok();
		}

		// Now check the callback.
		return processor.canContinueProcessing(this, recipe.get(), outputContainer);
	}

	@Override
	protected void onProcessingCompleted() {
		// If we can immediately start processing again, do so without a move delay.
		processor.processingCompleted(this, getCurrentlyProcessingRecipe().get(), outputContainer);

		TeamComponent teamComp = getTileEntity().getComponent(TeamComponent.class);
		if (teamComp != null) {
			for (ItemStack output : outputContainer.getOutputItems()) {
				productionToken.produced(teamComp.getOwningTeam(), output, output.getCount());
			}
			for (ItemStack input : outputContainer.getInputItems()) {
				productionToken.produced(teamComp.getOwningTeam(), input, input.getCount());
			}
		}
		outputContainer.clear();
		productionToken.invalidate();
		if (canStartProcessing().isOk()) {
			moveTimer = moveTime;
		}
	}

	public Optional<T> getPendingRecipe() {
		return getRecipeMatchingParameters(processor.getRecipeMatchParameters(this));
	}

	public Optional<T> getCurrentlyProcessingRecipe() {
		ResourceLocation recipeId = this.outputContainer.getRecipe();
		if (recipeId == null) {
			return Optional.empty();
		}
		return StaticPowerRecipeRegistry.getRawRecipe(recipeType, recipeId);
	}

	@SuppressWarnings("unchecked")
	public Optional<T> getRecipeMatchingParameters(RecipeMatchParameters matchParameters) {
		// Check for the recipe.
		if (recipeType == RecipeType.SMELTING) {
			return (Optional<T>) getLevel().getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SimpleContainer(matchParameters.getItems()[0]), getLevel());
		} else if (recipeType == RecipeType.CRAFTING) {
			FakeCraftingInventory craftingInv = new FakeCraftingInventory(3, 3);
			for (int i = 0; i < 9; i++) {
				craftingInv.setItem(i, matchParameters.getItems()[i]);
			}
			return (Optional<T>) getLevel().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, craftingInv, getLevel());
		} else if (recipeType == PackagerRecipe.RECIPE_TYPE) { // Hate this, but can't think of a better way around this since we can't cache
																// the recipes after all the latent tag changes.
			int size = matchParameters.getCustomParameterContainer().getInt("size");
			return (Optional<T>) StaticPowerRecipeRegistry.getPackagerRecipe(getLevel().getRecipeManager(), matchParameters.getItems()[0].copy(), size);
		} else {
			RecipeType<AbstractStaticPowerRecipe> spRecipe = (RecipeType<AbstractStaticPowerRecipe>) recipeType;
			if (spRecipe != null) {
				return (Optional<T>) StaticPowerRecipeRegistry.getRecipe((RecipeType<AbstractStaticPowerRecipe>) recipeType, matchParameters);
			}
		}

		// If nothing was found, return an empty optional.
		return Optional.empty();
	}

	/**
	 * First attempts to get the recipe using the processing phase. If one is not
	 * found, then we fall back to the pre_processing phase.
	 * 
	 * @return
	 */
	public Optional<T> getCurrentOrPendingRecipe() {
		Optional<T> recipe = this.getCurrentlyProcessingRecipe();
		if (!recipe.isPresent()) {
			recipe = this.getPendingRecipe();
		}
		return recipe;
	}

	@Override
	protected void onProcessingStarted() {

	}

	@Override
	protected void onProcessingCanceled() {
		outputContainer.clear();
		productionToken.invalidate();
	}

	@Override
	protected void onProcessingPausedDueToError() {
		productionToken.invalidate();

	}

	public int getMoveTime() {
		return moveTime;
	}

	public void setMoveTime(int moveTime) {
		this.moveTime = moveTime;
	}
}
