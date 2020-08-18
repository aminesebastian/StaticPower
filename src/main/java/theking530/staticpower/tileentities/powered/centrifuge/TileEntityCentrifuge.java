package theking530.staticpower.tileentities.powered.centrifuge;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import theking530.common.utilities.SDMath;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.wrappers.centrifuge.CentrifugeRecipe;
import theking530.staticpower.init.ModTileEntityTypes;
import theking530.staticpower.tileentities.TileEntityMachine;
import theking530.staticpower.tileentities.components.control.BatteryInventoryComponent;
import theking530.staticpower.tileentities.components.control.RecipeProcessingComponent;
import theking530.staticpower.tileentities.components.control.MachineProcessingComponent.ProcessingCheckState;
import theking530.staticpower.tileentities.components.control.RecipeProcessingComponent.RecipeProcessingLocation;
import theking530.staticpower.tileentities.components.items.InputServoComponent;
import theking530.staticpower.tileentities.components.items.InventoryComponent;
import theking530.staticpower.tileentities.components.items.OutputServoComponent;
import theking530.staticpower.tileentities.components.items.UpgradeInventoryComponent;
import theking530.staticpower.tileentities.components.serialization.UpdateSerialize;
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
	public final BatteryInventoryComponent batteryInventory;
	public final UpgradeInventoryComponent upgradesInventory;
	public final RecipeProcessingComponent<CentrifugeRecipe> processingComponent;

	@UpdateSerialize
	private int currentSpeed;

	public TileEntityCentrifuge() {
		super(ModTileEntityTypes.CENTRIFUGE);
		// Initialize the current speed.
		currentSpeed = 0;

		// Setup the input inventory to only accept items that have a valid recipe.
		registerComponent(inputInventory = new InventoryComponent("InputInventory", 1, MachineSideMode.Input).setFilter(new ItemStackHandlerFilter() {
			public boolean canInsertItem(int slot, ItemStack stack) {
				return processingComponent.getRecipe(new RecipeMatchParameters(stack)).isPresent();

			}
		}));

		// Setup all the other inventories.
		registerComponent(firstOutputInventory = new InventoryComponent("FirstOutputInventory", 1, MachineSideMode.Output));
		registerComponent(secondOutputInventory = new InventoryComponent("SecondOutputInventory", 1, MachineSideMode.Output2));
		registerComponent(thirdOutputInventory = new InventoryComponent("ThirdOutputInventory", 1, MachineSideMode.Output3));
		registerComponent(internalInventory = new InventoryComponent("InternalInventory", 1));
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", energyStorage.getStorage()));
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));

		// Setup the processing component.
		registerComponent(processingComponent = new RecipeProcessingComponent<CentrifugeRecipe>("ProcessingComponent", CentrifugeRecipe.RECIPE_TYPE, DEFAULT_PROCESSING_TIME, this::getMatchParameters,
				this::moveInputs, this::canProcessRecipe, this::processingCompleted));

		// Initialize the processing component to work with the redstone control
		// component, upgrade component and energy component.
		processingComponent.setShouldControlBlockState(true);
		processingComponent.setUpgradeInventory(upgradesInventory);
		processingComponent.setEnergyComponent(energyStorage);
		processingComponent.setRedstoneControlComponent(redstoneControlComponent);
		processingComponent.setProcessingPowerUsage(DEFAULT_PROCESSING_COST);

		// Setup the I/O servos.
		registerComponent(new InputServoComponent("InputServo", 4, inputInventory));
		registerComponent(new OutputServoComponent("OutputServo1", 4, firstOutputInventory));
		registerComponent(new OutputServoComponent("OutputServo2", 4, secondOutputInventory));
		registerComponent(new OutputServoComponent("OutputServo3", 4, thirdOutputInventory));

		// Set the energy storage upgrade inventory.
		energyStorage.setUpgradeInventory(upgradesInventory);
	}

	protected RecipeMatchParameters getMatchParameters(RecipeProcessingLocation location) {
		if (location == RecipeProcessingLocation.INTERNAL) {
			return new RecipeMatchParameters(internalInventory.getStackInSlot(0));
		} else {
			return new RecipeMatchParameters(inputInventory.getStackInSlot(0));
		}
	}

	protected ProcessingCheckState moveInputs(CentrifugeRecipe recipe) {
		// Check the required speed.
		if (currentSpeed < recipe.getMinimumSpeed()) {
			return ProcessingCheckState.error("Centrifuge not up to required speed of: " + recipe.getMinimumSpeed());
		}

		// If we don't have enough inputs, return false.
		if (inputInventory.getStackInSlot(0).getCount() < recipe.getInput().getCount()) {
			return ProcessingCheckState.skip();
		}

		// If the items can be insert into the output, transfer the items and return
		// true.
		if (internalInventory.getStackInSlot(0).isEmpty() && canInsertRecipeIntoOutputs(recipe)) {
			transferItemInternally(inputInventory, 0, internalInventory, 0);
			markTileEntityForSynchronization();
			return ProcessingCheckState.ok();
		} else {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}
	}

	protected ProcessingCheckState canProcessRecipe(CentrifugeRecipe recipe) {
		if (!canInsertRecipeIntoOutputs(recipe)) {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}

		if (currentSpeed < recipe.getMinimumSpeed()) {
			return ProcessingCheckState.error("Centrifuge not up to required speed of: " + recipe.getMinimumSpeed());
		}
		return ProcessingCheckState.ok();
	}

	protected ProcessingCheckState processingCompleted(CentrifugeRecipe recipe) {
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
			return ProcessingCheckState.ok();
		}
		return ProcessingCheckState.outputsCannotTakeRecipe();
	}

	@Override
	public void process() {
		// Maintain the spin.
		if (!getWorld().isRemote) {
			if (energyStorage.hasEnoughPower(1)) {
				energyStorage.useBulkPower(1);
				currentSpeed = SDMath.clamp(currentSpeed + 1, 0, DEFAULT_MAX_SPEED);
			} else {
				currentSpeed = SDMath.clamp(currentSpeed - 1, 0, DEFAULT_MAX_SPEED);
			}
		}
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
}