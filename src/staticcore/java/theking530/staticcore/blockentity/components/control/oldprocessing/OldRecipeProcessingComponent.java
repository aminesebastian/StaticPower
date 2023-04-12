package theking530.staticcore.blockentity.components.control.oldprocessing;

import java.util.Optional;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import theking530.staticcore.blockentity.components.control.oldprocessing.OldProcessingContainer.CaptureType;
import theking530.staticcore.blockentity.components.control.oldprocessing.OldProcessingContainer.ProcessingFluidWrapper;
import theking530.staticcore.blockentity.components.control.oldprocessing.OldProcessingContainer.ProcessingItemWrapper;
import theking530.staticcore.blockentity.components.control.oldprocessing.interfaces.IOldRecipeProcessor;
import theking530.staticcore.blockentity.components.control.processing.ProcessingCheckState;
import theking530.staticcore.blockentity.components.serialization.SaveSerialize;
import theking530.staticcore.blockentity.components.serialization.UpdateSerialize;
import theking530.staticcore.blockentity.components.team.TeamComponent;
import theking530.staticcore.container.FakeCraftingInventory;
import theking530.staticcore.crafting.AbstractMachineRecipe;
import theking530.staticcore.crafting.AbstractStaticPowerRecipe;
import theking530.staticcore.crafting.CraftingUtilities;
import theking530.staticcore.crafting.RecipeMatchParameters;
import theking530.staticcore.teams.ServerTeam;
import theking530.staticcore.utilities.math.SDMath;
import theking530.staticcore.world.WorldUtilities;

public class OldRecipeProcessingComponent<T extends Recipe<?>>
		extends OldAbstractProcesingComponent<OldRecipeProcessingComponent<T>> {
	public static final int MOVE_TIME = 8;

	private final RecipeType<T> recipeType;
	private final IOldRecipeProcessor<T> processor;

	@UpdateSerialize
	private final OldProcessingContainer outputContainer;
	@UpdateSerialize
	private int moveTimer;
	@UpdateSerialize
	private int moveTime;
	@SaveSerialize
	private float accumulatedExperience;

	public OldRecipeProcessingComponent(String name, RecipeType<T> recipeType, IOldRecipeProcessor<T> processor) {
		this(name, 0, recipeType, processor);
	}

	public OldRecipeProcessingComponent(String name, int processingTime, RecipeType<T> recipeType,
			IOldRecipeProcessor<T> processor) {
		super(name, processingTime, true);
		this.recipeType = recipeType;
		this.processor = processor;
		this.moveTime = MOVE_TIME;
		moveTimer = 0;
		outputContainer = new OldProcessingContainer();
	}

	@Override
	public boolean process() {
		if (!hasProcessingStarted()) {
			if (!attemptMove()) {
				return false;
			}
		}

		return super.process();
	}

	protected boolean attemptMove() {
		// Try to update the power cost and processing times if we have a valid recipe.
		Optional<T> recipe = getPendingRecipe();
		if (recipe.isPresent()) {
			if (recipe.get() instanceof AbstractMachineRecipe) {
				AbstractMachineRecipe machineRecipe = (AbstractMachineRecipe) recipe.get();
				setMaxProcessingTime(machineRecipe.getProcessingTime());
				setProcessingPowerUsage(machineRecipe.getPowerCost());
			}

			if (this.performedWorkLastTick()) {
				moveTimer = moveTime;
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
			return true;
		} else {
			this.processingStoppedDueToError = false;
			return false;
		}
	}

	@Override
	protected void updateProductionStatistics(TeamComponent teamComp) {
		super.updateProductionStatistics(teamComp);
		for (ProcessingItemWrapper output : outputContainer.getOutputItems()) {
			if (output.captureType() == CaptureType.BOTH || output.captureType() == CaptureType.RATE_ONLY) {
				getItemProductionToken().setProductionPerSecond((ServerTeam) teamComp.getOwningTeam(), output.item(),
						output.item().getCount() * (1.0 / (getMaxProcessingTime() / 20.0)),
						output.item().getCount() * (1.0 / (getFullPowerSatisfactionMaxProcessingTime() / 20.0)));
			}
		}
		for (ProcessingItemWrapper input : outputContainer.getInputItems()) {
			if (input.captureType() == CaptureType.BOTH || input.captureType() == CaptureType.RATE_ONLY) {
				getItemProductionToken().setConsumptionPerSecond((ServerTeam) teamComp.getOwningTeam(), input.item(),
						input.item().getCount() * (1.0 / (getMaxProcessingTime() / 20.0)),
						input.item().getCount() * (1.0 / (getFullPowerSatisfactionMaxProcessingTime() / 20.0)));
			}
		}

		for (ProcessingFluidWrapper output : outputContainer.getOutputFluids()) {
			if (output.captureType() == CaptureType.BOTH || output.captureType() == CaptureType.RATE_ONLY) {
				getFluidProductionToken().setProductionPerSecond((ServerTeam) teamComp.getOwningTeam(), output.fluid(),
						output.fluid().getAmount() * (1.0 / (getMaxProcessingTime() / 20.0)),
						output.fluid().getAmount() * (1.0 / (getFullPowerSatisfactionMaxProcessingTime() / 20.0)));
			}
		}
		for (ProcessingFluidWrapper input : outputContainer.getInputFluids()) {
			if (input.captureType() == CaptureType.BOTH || input.captureType() == CaptureType.RATE_ONLY) {
				getFluidProductionToken().setConsumptionPerSecond((ServerTeam) teamComp.getOwningTeam(), input.fluid(),
						input.fluid().getAmount() * (1.0 / (getMaxProcessingTime() / 20.0)),
						input.fluid().getAmount() * (1.0 / (getFullPowerSatisfactionMaxProcessingTime() / 20.0)));
			}
		}
	}

	public OldProcessingContainer getProcessingMaterials() {
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
		outputContainer.open();
		processor.captureInputsAndProducts(this, recipe.get(), outputContainer);
		outputContainer.close();
		return processor.canStartProcessing(this, recipe.get(), outputContainer);
	}

	@Override
	protected ProcessingCheckState canContinueProcessing() {
		// Get the recipe. Skip if it does not exist.
		Optional<T> recipe = getCurrentRecipe();
		if (!recipe.isPresent()) {
			return ProcessingCheckState.ok();
		}

		// Now check the callback.
		return processor.canContinueProcessing(this, recipe.get(), outputContainer);
	}

	@Override
	protected void onProcessingCompleted() {
		if (getCurrentRecipe().isPresent()) {
			// If we can immediately start processing again, do so without a move delay.
			processor.processingCompleted(this, getCurrentRecipe().get(), outputContainer);
		} else {
			System.out.println("wtf");
		}

		TeamComponent teamComp = getBlockEntity().getComponent(TeamComponent.class);
		if (teamComp != null) {
			for (ProcessingItemWrapper output : outputContainer.getOutputItems()) {
				if (output.captureType() == CaptureType.BOTH || output.captureType() == CaptureType.COUNT_ONLY) {
					getItemProductionToken().produced((ServerTeam) teamComp.getOwningTeam(), output.item(),
							output.item().getCount());
				}
			}
			for (ProcessingItemWrapper input : outputContainer.getInputItems()) {
				if (input.captureType() == CaptureType.BOTH || input.captureType() == CaptureType.COUNT_ONLY) {
					getItemProductionToken().consumed((ServerTeam) teamComp.getOwningTeam(), input.item(),
							input.item().getCount());
				}
			}

			for (ProcessingFluidWrapper output : outputContainer.getOutputFluids()) {
				if (output.captureType() == CaptureType.BOTH || output.captureType() == CaptureType.COUNT_ONLY) {
					getFluidProductionToken().produced((ServerTeam) teamComp.getOwningTeam(), output.fluid(),
							output.fluid().getAmount());
				}
			}
			for (ProcessingFluidWrapper input : outputContainer.getInputFluids()) {
				if (input.captureType() == CaptureType.BOTH || input.captureType() == CaptureType.COUNT_ONLY) {
					getFluidProductionToken().consumed((ServerTeam) teamComp.getOwningTeam(), input.fluid(),
							input.fluid().getAmount());
				}
			}
		}

		if (getCurrentRecipe().get() instanceof AbstractStaticPowerRecipe) {
			AbstractStaticPowerRecipe castRecipe = (AbstractStaticPowerRecipe) getCurrentRecipe().get();
			if (castRecipe.hasExperience()) {
				accumulatedExperience += castRecipe.getExperience();
			}
		}

		outputContainer.clear();
	}

	public Optional<T> getPendingRecipe() {
		return getRecipeMatchingParameters(processor.getRecipeMatchParameters(this));
	}

	@SuppressWarnings("unchecked")
	public Optional<T> getCurrentRecipe() {
		ResourceLocation recipeId = this.outputContainer.getRecipe();
		if (recipeId == null) {
			return Optional.empty();
		}

		return (Optional<T>) CraftingUtilities.getRecipeByKey(recipeId, getLevel());
	}

	@SuppressWarnings("unchecked")
	public Optional<T> getRecipeMatchingParameters(RecipeMatchParameters matchParameters) {
		// Check for the recipe.
		if (recipeType == RecipeType.SMELTING) {
			return (Optional<T>) getLevel().getRecipeManager().getRecipeFor(RecipeType.SMELTING,
					new SimpleContainer(matchParameters.getItems()[0]), getLevel());
		} else if (recipeType == RecipeType.CRAFTING) {
			FakeCraftingInventory craftingInv = new FakeCraftingInventory(3, 3);
			for (int i = 0; i < 9; i++) {
				craftingInv.setItem(i, matchParameters.getItems()[i]);
			}
			return (Optional<T>) getLevel().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, craftingInv,
					getLevel());
		} else {
			RecipeType<Recipe<RecipeMatchParameters>> castType = (RecipeType<Recipe<RecipeMatchParameters>>) recipeType;
			return (Optional<T>) CraftingUtilities.getRecipe(castType, matchParameters, getLevel());
		}
	}

	/**
	 * First attempts to get the recipe using the processing phase. If one is not
	 * found, then we fall back to the pre_processing phase.
	 * 
	 * @return
	 */
	public Optional<T> getCurrentOrPendingRecipe() {
		Optional<T> recipe = this.getCurrentRecipe();
		if (!recipe.isPresent()) {
			recipe = this.getPendingRecipe();
		}
		return recipe;
	}

	@Override
	protected void onProcessingStarted() {
		processor.processingStarted(this, getCurrentRecipe().get(), outputContainer);
	}

	public int getMoveTime() {
		return moveTime;
	}

	public void setMoveTime(int moveTime) {
		this.moveTime = moveTime;
	}

	public float getAccumulatedExperience() {
		return accumulatedExperience;
	}

	public void clearAccumulatedExperience() {
		accumulatedExperience = 0.0f;
	}

	public void applyExperience(Player player) {
		if (getAccumulatedExperience() > 0) {
			int points = (int) getAccumulatedExperience();
			float chanceOfAnotherPoint = getAccumulatedExperience() - points;
			if (chanceOfAnotherPoint > 0) {
				if (SDMath.diceRoll(chanceOfAnotherPoint)) {
					points += 1;
				}
			}

			WorldUtilities.dropExperience(getLevel(), getPos(), points);
			clearAccumulatedExperience();
		}
	}
}
