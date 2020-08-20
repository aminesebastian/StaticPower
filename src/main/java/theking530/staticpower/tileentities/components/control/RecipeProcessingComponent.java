package theking530.staticpower.tileentities.components.control;

import java.util.Optional;
import java.util.function.Function;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import theking530.staticpower.data.crafting.AbstractMachineRecipe;
import theking530.staticpower.data.crafting.AbstractStaticPowerRecipe;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerRecipeRegistry;
import theking530.staticpower.tileentities.components.serialization.UpdateSerialize;

public class RecipeProcessingComponent<T extends IRecipe<IInventory>> extends MachineProcessingComponent {
	public enum RecipeProcessingLocation {
		INPUT, INTERNAL
	}

	public static final int MOVE_TIME = 5;

	/**
	 * This function is called both when checking to see if we can move the inputs
	 * and also when starting the processing.
	 */
	private final Function<T, ProcessingCheckState> canStartProcessingRecipe;
	private final Function<T, ProcessingCheckState> recipeProcessingCompleted;
	private final Function<RecipeProcessingLocation, RecipeMatchParameters> getMatchParameters;
	private final Function<T, ProcessingCheckState> performInputMove;
	private final IRecipeType<T> recipeType;

	private Function<T, ProcessingCheckState> canContinueProcessingRecipe;

	@UpdateSerialize
	private int moveTimer;

	public RecipeProcessingComponent(String name, IRecipeType<T> recipeType, int processingTime, Function<RecipeProcessingLocation, RecipeMatchParameters> getMatchParameters,
			Function<T, ProcessingCheckState> performInputMove, Function<T, ProcessingCheckState> canProcessRecipe, Function<T, ProcessingCheckState> recipeProcessingCompleted) {
		super(name, processingTime, null, null, null, true);

		// Capture the recipe type.
		this.recipeType = recipeType;

		// Set the recipe callbacks.
		this.canStartProcessingRecipe = canProcessRecipe;
		this.canContinueProcessingRecipe = canProcessRecipe;
		this.recipeProcessingCompleted = recipeProcessingCompleted;
		this.performInputMove = performInputMove;

		// Use the default callbacks internally.
		this.canStartProcessingCallback = () -> ProcessingCheckState.ok();// We can just return true here because we are overriding the parent's check.
		this.canContinueProcessingCallback = this::canContinueProcessing;
		this.processingEndedCallback = this::processingCompleted;
		this.getMatchParameters = getMatchParameters;

		// Set the initial move timer.
		moveTimer = 0;
	}

	@Override
	public void postProcessUpdate() {
		// If on the client, do nothing.
		if (getWorld().isRemote) {
			return;
		}

		// If we're still processing, do nothing.
		if (isProcessing()) {
			return;
		}

		// If we can't move the inputs to the internal, nothing.
		ProcessingCheckState internalMoveState = canMoveInputsToInternal();
		if (!internalMoveState.isOk()) {
			setProcessingErrorMessage(internalMoveState.getErrorMessage());
			moveTimer = 0;
			processingStoppedDueToError = true;
			return;
		}else {
			processingStoppedDueToError = false;
		}

		// Increment the move timer.
		moveTimer++;

		// Check if it elapsed..
		if (moveTimer > MOVE_TIME) {
			// We can just GET the recipe here because the call to #canMoveInputsToInternal
			// already checks for a valid recipe.
			performInputMove.apply(getRecipe(getMatchParameters.apply(RecipeProcessingLocation.INPUT)).get());
			moveTimer = 0;
		}
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
		Optional<T> recipe = getRecipe(RecipeProcessingLocation.INPUT);
		if (!recipe.isPresent()) {
			return ProcessingCheckState.skip();
		}

		// If the recipe is valid, set the usage stats.
		if (recipe.get() instanceof AbstractMachineRecipe) {
			AbstractMachineRecipe machineRecipe = (AbstractMachineRecipe) recipe.get();
			setMaxProcessingTime(machineRecipe.getProcessingTime());
			setProcessingPowerUsage(machineRecipe.getPowerCost());
		}

		// Check power states.
		ProcessingCheckState processingCheck = checkPowerRequirements();
		if (!processingCheck.isOk()) {
			return processingCheck;
		}

		// If we made it this far, check the start processing callback.
		return canStartProcessingRecipe.apply(recipe.get());
	}

	@Override
	protected ProcessingCheckState checkProcessingStartState() {
		// Attempt to get the recipe. If it does not exist, skip.
		Optional<T> recipe = getRecipe(RecipeProcessingLocation.INTERNAL);
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
		ProcessingCheckState superCall = super.checkProcessingStartState();
		if (!superCall.isOk()) {
			return superCall;
		}

		// If we made it this far, check the start processing callback.
		return canStartProcessingRecipe.apply(recipe.get());
	}

	protected ProcessingCheckState canContinueProcessing() {
		// Get the recipe. Skip if it does not exist.
		Optional<T> recipe = getRecipe(RecipeProcessingLocation.INTERNAL);
		if (!recipe.isPresent()) {
			return ProcessingCheckState.ok();
		}
		// Now check the callback.
		return canContinueProcessingRecipe.apply(recipe.get());
	}

	protected ProcessingCheckState processingCompleted() {
		// Get the recipe.
		Optional<T> recipe = getRecipe(RecipeProcessingLocation.INTERNAL);

		// If there is a recipe, see if we can complete it. If there is no recipe, just
		// return true so we don't get stuck in a loop. This is an edge case where a
		// user may save mid processing, remove the recipe, and then reload.
		if (recipe.isPresent()) {
			return recipeProcessingCompleted.apply(recipe.get());
		}
		return ProcessingCheckState.ok();
	}

	public RecipeProcessingComponent<T> setCanContinueProcessingLambda(Function<T, ProcessingCheckState> canContinueProcessingRecipe) {
		this.canContinueProcessingRecipe = canContinueProcessingRecipe;
		return this;
	}

	private Optional<T> getRecipe(RecipeProcessingLocation location) {
		// Get the recipe.
		RecipeMatchParameters matchParameters = getMatchParameters.apply(location);
		return getRecipe(matchParameters);
	}

	@SuppressWarnings("unchecked")
	public Optional<T> getRecipe(RecipeMatchParameters matchParameters) {
		// Check for the recipe.
		if (recipeType == IRecipeType.SMELTING) {
			return (Optional<T>) getWorld().getRecipeManager().getRecipe(IRecipeType.SMELTING, new Inventory(matchParameters.getItems()[0]), getWorld());
		} else {
			IRecipeType<AbstractStaticPowerRecipe> spRecipe = (IRecipeType<AbstractStaticPowerRecipe>) recipeType;
			if (spRecipe != null) {
				return (Optional<T>) StaticPowerRecipeRegistry.getRecipe((IRecipeType<AbstractStaticPowerRecipe>) recipeType, matchParameters);
			}
		}

		// If nothing was found, return an empty optional.
		return Optional.empty();
	}
}
