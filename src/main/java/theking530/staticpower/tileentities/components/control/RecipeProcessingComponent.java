package theking530.staticpower.tileentities.components.control;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import theking530.staticpower.data.crafting.AbstractMachineRecipe;
import theking530.staticpower.data.crafting.AbstractStaticPowerRecipe;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerRecipeRegistry;
import theking530.staticpower.tileentities.components.energy.PowerStorageComponent;
import theking530.staticpower.tileentities.components.items.UpgradeInventoryComponent;
import theking530.staticpower.tileentities.components.serialization.UpdateSerialize;

public class RecipeProcessingComponent<T extends Recipe<Container>> extends AbstractProcesingComponent {
	public enum RecipeProcessingPhase {
		PRE_PROCESSING, PROCESSING
	}

	public static final int MOVE_TIME = 8;

	private final BiFunction<T, RecipeProcessingPhase, ProcessingCheckState> canProcessRecipe;
	private final Function<RecipeProcessingPhase, RecipeMatchParameters> getMatchParameters;
	private final Consumer<T> performInputMove;
	private final Consumer<T> recipeProcessingCompleted;
	private final RecipeType<T> recipeType;

	@UpdateSerialize
	private int moveTimer;

	public RecipeProcessingComponent(String name, int processingTime, RecipeType<T> recipeType, Function<RecipeProcessingPhase, RecipeMatchParameters> getMatchParameters,
			BiFunction<T, RecipeProcessingPhase, ProcessingCheckState> canProcessRecipe, Consumer<T> performInputMove, Consumer<T> recipeProcessingCompleted) {
		super(name, processingTime, true);

		// Capture the recipe type.
		this.recipeType = recipeType;

		// Set the recipe callbacks.
		this.canProcessRecipe = canProcessRecipe;
		this.recipeProcessingCompleted = recipeProcessingCompleted;
		this.performInputMove = performInputMove;
		this.getMatchParameters = getMatchParameters;

		// Set the initial move timer.
		moveTimer = 0;
	}

	@Override
	public void process() {
		// If we are not processing, check to see if we can perform a move from input to
		// processing inventory. If erorred, set the error state. If not error, but not
		// okay (cancel/skip), return early. If okay, continue as usual.
		if (!isProcessing()) {
			ProcessingCheckState moveState = performMove();
			if (moveState.isError()) {
				this.processingErrorMessage = moveState.getErrorMessage();
				this.processingStoppedDueToError = true;
			}

			// If the move was not successful, return early.
			if (!moveState.isOk()) {
				return;
			}
		}

		super.process();
	}

	public ProcessingCheckState performMove() {
		// Increment the move timer.
		moveTimer++;

		// Check if it elapsed..
		if (moveTimer > MOVE_TIME) {
			// Reset the move timer.
			moveTimer = 0;

			// If we can move the inputs to the internal, try doing so. If not, clear the
			// error and skip.
			ProcessingCheckState internalMoveState = canMoveInputsToInternal();
			if (internalMoveState.isOk()) {
				// We can just GET the recipe here because the call to #canMoveInputsToInternal
				// already checks for a valid recipe.
				performInputMove.accept(getRecipeMatchingParameters(getMatchParameters.apply(RecipeProcessingPhase.PRE_PROCESSING)).get());
				moveTimer = 0;
				return ProcessingCheckState.ok();
			} else {
				return internalMoveState;
			}
		}
		return ProcessingCheckState.skip();
	}

	/**
	 * Checks to see if we can move the inputs to the internal inventory for
	 * processing. This is a separate module in this component so it peforms many
	 * duplicate checks. Users could let this do all the work OR manually move items
	 * to the internal inventory. In that case, the processing needs to perform some
	 * checks.
	 * 
	 * @return
	 */
	protected ProcessingCheckState canMoveInputsToInternal() {
		// Attempt to get the recipe. If it does not exist, skip.
		Optional<T> recipe = getRecipe(RecipeProcessingPhase.PRE_PROCESSING);
		if (!recipe.isPresent()) {
			return ProcessingCheckState.skip();
		}

		// If the recipe is valid, set the usage stats.
		if (recipe.get() instanceof AbstractMachineRecipe) {
			AbstractMachineRecipe machineRecipe = (AbstractMachineRecipe) recipe.get();
			setMaxProcessingTime(machineRecipe.getProcessingTime());
			setProcessingPowerUsage(machineRecipe.getPowerCost());
		}

		// Check the redstone state.
		ProcessingCheckState redstoneState;
		if (!(redstoneState = checkRedstoneState()).isOk()) {
			return redstoneState;
		}

		// Check the power state.
		ProcessingCheckState powerState;
		if (!(powerState = checkPowerRequirements()).isOk()) {
			return powerState;
		}

		// If we made it this far, check the start processing callback.
		return canProcessRecipe.apply(recipe.get(), RecipeProcessingPhase.PRE_PROCESSING);
	}

	@Override
	protected ProcessingCheckState canStartProcessing() {
		// Attempt to get the recipe. If it does not exist, skip.
		Optional<T> recipe = getRecipe(RecipeProcessingPhase.PROCESSING);
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

		// Check the super call.
		ProcessingCheckState superCall = super.canStartProcessing();
		if (!superCall.isOk()) {
			return superCall;
		}

		// If we made it this far, check the start processing callback.
		return canProcessRecipe.apply(recipe.get(), RecipeProcessingPhase.PROCESSING);
	}

	@Override
	protected ProcessingCheckState canContinueProcessing() {
		// Check the super call.
		ProcessingCheckState superCall = super.canContinueProcessing();
		if (!superCall.isOk()) {
			return superCall;
		}

		// Get the recipe. Skip if it does not exist.
		Optional<T> recipe = getRecipe(RecipeProcessingPhase.PROCESSING);
		if (!recipe.isPresent()) {
			return ProcessingCheckState.ok();
		}

		// Now check the callback.
		return canProcessRecipe.apply(recipe.get(), RecipeProcessingPhase.PROCESSING);
	}

	protected ProcessingCheckState canCompleteProcessing() {
		// Check the parent state.
		ProcessingCheckState superCall = super.canCompleteProcessing();
		if (!superCall.isOk()) {
			return superCall;
		}

		// Get the recipe. Skip if it does not exist.
		Optional<T> recipe = getRecipe(RecipeProcessingPhase.PROCESSING);
		if (!recipe.isPresent()) {
			return ProcessingCheckState.ok();
		}

		// Now check the callback.
		recipeProcessingCompleted.accept(recipe.get());
		return ProcessingCheckState.ok();
	}

	protected ProcessingCheckState processingCompleted() {
		// Check the super call.
		ProcessingCheckState superCall = super.canContinueProcessing();
		if (!superCall.isOk()) {
			return superCall;
		}

		// Get the recipe.
		Optional<T> recipe = getRecipe(RecipeProcessingPhase.PROCESSING);

		// If there is a recipe, see if we can complete it. If there is no recipe, just
		// return true so we don't get stuck in a loop. This is an edge case where a
		// user may save mid processing, remove the recipe, and then reload.
		if (recipe.isPresent()) {
			recipeProcessingCompleted.accept(recipe.get());

			// If the processing completed, check to see if we have another recipe ready. If
			// so, set the move timer to the max value to make it immediately start. This is
			// so that the move timer isn't factored in with large operations.
			if (canMoveInputsToInternal().isOk()) {
				moveTimer = MOVE_TIME;
			}
		}
		return ProcessingCheckState.ok();
	}

	private Optional<T> getRecipe(RecipeProcessingPhase location) {
		// Get the recipe.
		RecipeMatchParameters matchParameters = getMatchParameters.apply(location);
		return getRecipeMatchingParameters(matchParameters);
	}

	@SuppressWarnings("unchecked")
	public Optional<T> getRecipeMatchingParameters(RecipeMatchParameters matchParameters) {
		// Check for the recipe.
		if (recipeType == RecipeType.SMELTING) {
			return (Optional<T>) getLevel().getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SimpleContainer(matchParameters.getItems()[0]), getLevel());
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
		Optional<T> recipe = getRecipeMatchingParameters(getMatchParameters.apply(RecipeProcessingPhase.PROCESSING));
		if (!recipe.isPresent()) {
			recipe = getRecipeMatchingParameters(getMatchParameters.apply(RecipeProcessingPhase.PRE_PROCESSING));
		}
		return recipe;
	}

	public Optional<T> getCurrentProcessingRecipe() {
		return getRecipe(RecipeProcessingPhase.PROCESSING);
	}

	@SuppressWarnings("unchecked")
	@Override
	public RecipeProcessingComponent<T> setUpgradeInventory(UpgradeInventoryComponent inventory) {
		return (RecipeProcessingComponent<T>) super.setUpgradeInventory(inventory);
	}

	@SuppressWarnings("unchecked")
	@Override
	public RecipeProcessingComponent<T> setPowerComponent(PowerStorageComponent energyComponent) {
		return (RecipeProcessingComponent<T>) super.setPowerComponent(energyComponent);
	}

	@SuppressWarnings("unchecked")
	@Override
	public RecipeProcessingComponent<T> setProcessingPowerUsage(double power) {
		return (RecipeProcessingComponent<T>) super.setProcessingPowerUsage(power);
	}

	@SuppressWarnings("unchecked")
	@Override
	public RecipeProcessingComponent<T> setPowerUsageMuiltiplier(float multiplier) {
		return (RecipeProcessingComponent<T>) super.setPowerUsageMuiltiplier(multiplier);
	}

	@SuppressWarnings("unchecked")
	@Override
	public RecipeProcessingComponent<T> disableProcessingPowerUsage() {
		return (RecipeProcessingComponent<T>) super.disableProcessingPowerUsage();
	}

	@SuppressWarnings("unchecked")
	@Override
	public RecipeProcessingComponent<T> setRedstoneControlComponent(RedstoneControlComponent redstoneControlComponent) {
		return (RecipeProcessingComponent<T>) super.setRedstoneControlComponent(redstoneControlComponent);
	}

	@SuppressWarnings("unchecked")
	@Override
	public RecipeProcessingComponent<T> setShouldControlBlockState(boolean shouldControl) {
		return (RecipeProcessingComponent<T>) super.setShouldControlBlockState(shouldControl);
	}

}
