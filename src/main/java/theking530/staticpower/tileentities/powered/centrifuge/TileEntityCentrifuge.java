package theking530.staticpower.tileentities.powered.centrifuge;

import java.util.Optional;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import theking530.common.utilities.SDMath;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerRecipeRegistry;
import theking530.staticpower.data.crafting.wrappers.centrifuge.CentrifugeRecipe;
import theking530.staticpower.init.ModTileEntityTypes;
import theking530.staticpower.tileentities.TileEntityMachine;
import theking530.staticpower.tileentities.components.control.BatteryComponent;
import theking530.staticpower.tileentities.components.control.MachineProcessingComponent;
import theking530.staticpower.tileentities.components.items.InputServoComponent;
import theking530.staticpower.tileentities.components.items.InventoryComponent;
import theking530.staticpower.tileentities.components.items.OutputServoComponent;
import theking530.staticpower.tileentities.utilities.MachineSideMode;
import theking530.staticpower.tileentities.utilities.interfaces.ItemStackHandlerFilter;
import theking530.staticpower.utilities.InventoryUtilities;

public class TileEntityCentrifuge extends TileEntityMachine {
	public static final int DEFAULT_PROCESSING_TIME = 100;
	public static final int DEFAULT_PROCESSING_COST = 10;
	public static final int DEFAULT_MOVING_TIME = 4;
	public static final int DEFAULT_MAX_SPEED = 500;

	public final InventoryComponent inputInventory;

	public final InventoryComponent firstOutputInventory;
	public final InventoryComponent secondOutputInventory;
	public final InventoryComponent thirdOutputInventory;

	public final InventoryComponent internalInventory;
	public final InventoryComponent batteryInventory;
	public final InventoryComponent upgradesInventory;
	public final MachineProcessingComponent moveComponent;
	public final MachineProcessingComponent processingComponent;

	private int currentSpeed;

	public TileEntityCentrifuge() {
		super(ModTileEntityTypes.CENTRIFUGE);
		currentSpeed = 0;
		disableFaceInteraction();

		registerComponent(inputInventory = new InventoryComponent("InputInventory", 1, MachineSideMode.Input).setFilter(new ItemStackHandlerFilter() {
			public boolean canInsertItem(int slot, ItemStack stack) {
				return getRecipe(stack).isPresent();

			}
		}));

		registerComponent(firstOutputInventory = new InventoryComponent("FirstOutputInventory", 1, MachineSideMode.Output));
		registerComponent(secondOutputInventory = new InventoryComponent("SecondOutputInventory", 1, MachineSideMode.Output2));
		registerComponent(thirdOutputInventory = new InventoryComponent("ThirdOutputInventory", 1, MachineSideMode.Output3));

		registerComponent(internalInventory = new InventoryComponent("InternalInventory", 1, MachineSideMode.Never));
		registerComponent(batteryInventory = new InventoryComponent("BatteryInventory", 1, MachineSideMode.Never));
		registerComponent(upgradesInventory = new InventoryComponent("UpgradeInventory", 3, MachineSideMode.Never));
		registerComponent(moveComponent = new MachineProcessingComponent("MoveComponent", DEFAULT_MOVING_TIME, this::canMoveFromInputToProcessing, () -> true, this::movingCompleted, true));
		registerComponent(processingComponent = new MachineProcessingComponent("ProcessingComponent", DEFAULT_PROCESSING_TIME, this::canProcess, this::canProcess, this::processingCompleted, true)
				.setShouldControlBlockState(true));

		registerComponent(new InputServoComponent("InputServo", 4, inputInventory));

		registerComponent(new OutputServoComponent("OutputServo1", 4, firstOutputInventory));
		registerComponent(new OutputServoComponent("OutputServo2", 4, secondOutputInventory));
		registerComponent(new OutputServoComponent("OutputServo3", 4, thirdOutputInventory));

		registerComponent(new BatteryComponent("BatteryComponent", batteryInventory, 0, energyStorage.getStorage()));
	}

	/**
	 * Checks to make sure we can start the processing process.
	 * 
	 * @return
	 */
	public boolean canMoveFromInputToProcessing() {
		if (!redstoneControlComponent.passesRedstoneCheck()) {
			return false;
		}
		// Check if there is a valid recipe.
		if (hasValidRecipe() && !moveComponent.isProcessing() && !processingComponent.isProcessing() && internalInventory.getStackInSlot(0).isEmpty()) {
			// Gets the recipe and its outputs.
			Optional<CentrifugeRecipe> recipe = getRecipe(inputInventory.getStackInSlot(0));

			// If we don't have enough inputs, return false.
			if (inputInventory.getStackInSlot(0).getCount() < recipe.get().getInput().getCount()) {
				return false;
			}

			// Check the required speed.
			if (currentSpeed < recipe.get().getMinimumSpeed()) {
				return false;
			}

			// If the items cannot be insert into the output, return false.
			if (!canInsertRecipeIntoOutputs(recipe.get())) {
				return false;
			}

			// If we passed all the previous checks, return true.
			return energyStorage.hasEnoughPower(recipe.get().getPowerCost());
		}
		return false;
	}

	/**
	 * Once again, check to make sure the input item has not been removed or changed
	 * since we started the move process. If still valid, move a single input item
	 * to the internal inventory and being processing. Return true regardless so the
	 * movement component resets.
	 * 
	 * @return
	 */
	protected boolean movingCompleted() {
		if (hasValidRecipe()) {
			Optional<CentrifugeRecipe> recipe = getRecipe(inputInventory.getStackInSlot(0));
			transferItemInternally(recipe.get().getInput().getCount(), inputInventory, 0, internalInventory, 0);
			markTileEntityForSynchronization();
		}
		return true;
	}

	/**
	 * Indicates if we can start or continue processing. If this returns false and
	 * we are processing, processing pauses. If we are not processing and this
	 * returns true, we will start processing.
	 * 
	 * @return
	 */
	public boolean canProcess() {
		CentrifugeRecipe recipe = getRecipe(internalInventory.getStackInSlot(0)).orElse(null);
		return recipe != null && redstoneControlComponent.passesRedstoneCheck() && energyStorage.hasEnoughPower(recipe.getPowerCost()) && canInsertRecipeIntoOutputs(recipe)
				&& currentSpeed >= recipe.getMinimumSpeed();
	}

	/**
	 * Once the processing is completed, place the output in the output slot (if
	 * possible). If not, return false. This method will continue to be called until
	 * true is returned.
	 * 
	 * @return
	 */
	protected boolean processingCompleted() {
		// If on the server.
		if (!getWorld().isRemote) {
			// Get the recipe.
			CentrifugeRecipe recipe = getRecipe(internalInventory.getStackInSlot(0)).orElse(null);

			// Covers the edge case where they remove the recipe and restart the game, this
			// may be null. Just return true if that is the case.
			if (recipe == null) {
				return true;
			}

			// Ensure the output slots can take the recipe.
			if (canInsertRecipeIntoOutputs(recipe)) {
				// For each output, insert the contents into the output based on the percentage
				// chance. The clear the internal inventory, mark for synchronization, and
				// return true.
				if (SDMath.diceRoll(recipe.getOutput1().getOutputChance())) {
					InventoryUtilities.insertItemIntoInventory(firstOutputInventory, recipe.getOutput1().getItem().copy(), false);
				}
				if (!recipe.getOutput2().isEmpty() && SDMath.diceRoll(recipe.getOutput2().getOutputChance())) {
					InventoryUtilities.insertItemIntoInventory(secondOutputInventory, recipe.getOutput2().getItem().copy(), false);
				}
				if (!recipe.getOutput3().isEmpty() && SDMath.diceRoll(recipe.getOutput3().getOutputChance())) {
					InventoryUtilities.insertItemIntoInventory(thirdOutputInventory, recipe.getOutput3().getItem().copy(), false);
				}

				internalInventory.setStackInSlot(0, ItemStack.EMPTY);
				markTileEntityForSynchronization();
				return true;
			}
		}
		return false;
	}

	@Override
	public void process() {
		if (!getWorld().isRemote) {
			// Maintain the spin.
			if (energyStorage.hasEnoughPower(1)) {
				energyStorage.usePower(1);
				currentSpeed = SDMath.clamp(currentSpeed + 1, 0, DEFAULT_MAX_SPEED);
			} else {
				currentSpeed = SDMath.clamp(currentSpeed - 1, 0, DEFAULT_MAX_SPEED);
			}

			if (processingComponent.isPerformingWork()) {
				getRecipe(internalInventory.getStackInSlot(0)).ifPresent(recipe -> {
					energyStorage.usePower(recipe.getPowerCost());
				});
			}
		}
	}

	/**
	 * Checks to see if the input item forms a valid recipe.
	 * 
	 * @return
	 */
	public boolean hasValidRecipe() {
		return getRecipe(inputInventory.getStackInSlot(0)).isPresent();
	}

	/**
	 * Checks if the provided itemstack forms a valid recipe.
	 * 
	 * @param itemStackInput The itemstack to check for.
	 * @return
	 */
	public Optional<CentrifugeRecipe> getRecipe(ItemStack itemStackInput) {
		return StaticPowerRecipeRegistry.getRecipe(CentrifugeRecipe.RECIPE_TYPE, new RecipeMatchParameters(itemStackInput).setStoredEnergy(energyStorage.getStorage().getEnergyStored()));
	}

	public int getCurrentSpeed() {
		return currentSpeed;
	}

	protected boolean canInsertRecipeIntoOutputs(CentrifugeRecipe recipe) {
		if (!InventoryUtilities.canFullyInsertItemIntoInventory(firstOutputInventory, recipe.getOutput1().getItem())) {
			return false;
		}
		if (!InventoryUtilities.canFullyInsertItemIntoInventory(secondOutputInventory, recipe.getOutput2().getItem())) {
			return false;
		}
		if (!InventoryUtilities.canFullyInsertItemIntoInventory(thirdOutputInventory, recipe.getOutput3().getItem())) {
			return false;
		}
		return true;
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerCentrifuge(windowId, inventory, this);
	}

	public CompoundNBT serializeUpdateNbt(CompoundNBT nbt, boolean fromUpdate) {
		super.serializeUpdateNbt(nbt, fromUpdate);
		nbt.putInt("current_speed", currentSpeed);
		return nbt;
	}

	public void deserializeUpdateNbt(CompoundNBT nbt, boolean fromUpdate) {
		super.deserializeUpdateNbt(nbt, fromUpdate);
		currentSpeed = nbt.getInt("current_speed");
	}
}