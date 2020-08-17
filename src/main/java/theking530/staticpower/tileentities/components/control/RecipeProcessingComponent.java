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

	private final Function<T, Boolean> canStartProcessingRecipe;
	private final Function<T, Boolean> recipeProcessingCompleted;
	private final Function<RecipeProcessingLocation, RecipeMatchParameters> getMatchParameters;
	private final Function<T, Boolean> performInputMove;
	private final IRecipeType<T> recipeType;

	private Function<T, Boolean> canContinueProcessingRecipe;

	@UpdateSerialize
	private int moveTimer;

	public RecipeProcessingComponent(String name, IRecipeType<T> recipeType, int processingTime, Function<RecipeProcessingLocation, RecipeMatchParameters> getMatchParameters,
			Function<T, Boolean> performInputMove, Function<T, Boolean> canProcessRecipe, Function<T, Boolean> recipeProcessingCompleted) {
		super(name, processingTime, null, null, null, true);

		// Capture the recipe type.
		this.recipeType = recipeType;

		// Set the started callback.
		this.setProcessingStartedCallback(this::processingStarted);

		// Set the recipe callbacks.
		this.canStartProcessingRecipe = canProcessRecipe;
		this.canContinueProcessingRecipe = canProcessRecipe;
		this.recipeProcessingCompleted = recipeProcessingCompleted;
		this.performInputMove = performInputMove;

		// Use the default callbacks internally.
		this.canStartProcessingCallback = this::canStartProcessing;
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
		if (!canMoveInputsToInternal()) {
			return;
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

	public boolean canMoveInputsToInternal() {
		Optional<T> recipe = getRecipe(getMatchParameters.apply(RecipeProcessingLocation.INPUT));
		return passesAllProcessingStartChecks() && recipe.isPresent() && canStartProcessingRecipe.apply(recipe.get());
	}

	protected boolean passesAllProcessingStartChecks() {
		return super.passesAllProcessingStartChecks();
	}

	protected boolean passesAllProcessingChecks() {
		return super.passesAllProcessingChecks();
	}

	protected boolean canStartProcessing() {
		// Get the recipe.
		RecipeMatchParameters matchParameters = getMatchParameters.apply(RecipeProcessingLocation.INTERNAL);
		Optional<T> recipe = getRecipe(matchParameters);

		// If its present, check to see if we can process that recipe, otherwise return
		// false.
		if (recipe.isPresent()) {
			return canStartProcessingRecipe.apply(recipe.get());
		} else {
			return false;
		}
	}

	protected void processingStarted() {
		// Get the recipe.
		RecipeMatchParameters matchParameters = getMatchParameters.apply(RecipeProcessingLocation.INTERNAL);
		Optional<T> recipe = getRecipe(matchParameters);

		// If this is a machine recipe, set the power usage and processing time. If
		// there is no recipe, do nothing.
		if (recipe.isPresent()) {
			if (recipe.get() instanceof AbstractMachineRecipe) {
				AbstractMachineRecipe machineRecipe = (AbstractMachineRecipe) recipe.get();
				setMaxProcessingTime(machineRecipe.getProcessingTime());
				setProcessingPowerUsage(machineRecipe.getPowerCost());
			}
		}
	}

	protected boolean canContinueProcessing() {
		// Get the recipe.
		RecipeMatchParameters matchParameters = getMatchParameters.apply(RecipeProcessingLocation.INTERNAL);
		Optional<T> recipe = getRecipe(matchParameters);

		// Make sure we can continue processing the recipe. If there is no recipe, just
		// return true so we don't get stuck in a loop. This is an edge case where a
		// user may save mid processing, remove the recipe, and then reload.
		if (recipe.isPresent()) {
			return canContinueProcessingRecipe.apply(recipe.get());
		}
		return true;
	}

	protected boolean processingCompleted() {
		// Get the recipe.
		RecipeMatchParameters matchParameters = getMatchParameters.apply(RecipeProcessingLocation.INTERNAL);
		Optional<T> recipe = getRecipe(matchParameters);

		// If there is a recipe, see if we can complete it. If there is no recipe, just
		// return true so we don't get stuck in a loop. This is an edge case where a
		// user may save mid processing, remove the recipe, and then reload.
		if (recipe.isPresent()) {
			return recipeProcessingCompleted.apply(recipe.get());
		}

		return true;
	}

	public RecipeProcessingComponent<T> setCanContinueProcessingLambda(Function<T, Boolean> canContinueProcessingRecipe) {
		this.canContinueProcessingRecipe = canContinueProcessingRecipe;
		return this;
	}

	public Optional<T> getRecipe() {
		// Get the recipe.
		RecipeMatchParameters matchParameters = getMatchParameters.apply(RecipeProcessingLocation.INPUT);
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
