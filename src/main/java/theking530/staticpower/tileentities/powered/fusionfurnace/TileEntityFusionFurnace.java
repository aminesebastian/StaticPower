package theking530.staticpower.tileentities.powered.fusionfurnace;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import theking530.common.utilities.SDMath;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.wrappers.fusionfurnace.FusionFurnaceRecipe;
import theking530.staticpower.init.ModTileEntityTypes;
import theking530.staticpower.tileentities.TileEntityMachine;
import theking530.staticpower.tileentities.components.control.BatteryInventoryComponent;
import theking530.staticpower.tileentities.components.control.MachineProcessingComponent;
import theking530.staticpower.tileentities.components.control.MachineProcessingComponent.ProcessingCheckState;
import theking530.staticpower.tileentities.components.control.RecipeProcessingComponent;
import theking530.staticpower.tileentities.components.control.RecipeProcessingComponent.RecipeProcessingLocation;
import theking530.staticpower.tileentities.components.items.InputServoComponent;
import theking530.staticpower.tileentities.components.items.InventoryComponent;
import theking530.staticpower.tileentities.components.items.OutputServoComponent;
import theking530.staticpower.tileentities.components.items.UpgradeInventoryComponent;
import theking530.staticpower.tileentities.utilities.MachineSideMode;
import theking530.staticpower.utilities.InventoryUtilities;

public class TileEntityFusionFurnace extends TileEntityMachine {
	public static final int DEFAULT_PROCESSING_TIME = 200;
	public static final int DEFAULT_PROCESSING_COST = 20;
	public static final int DEFAULT_MOVING_TIME = 4;

	public final InventoryComponent inputInventory;
	public final InventoryComponent outputInventory;
	public final InventoryComponent internalInventory;
	public final BatteryInventoryComponent batteryInventory;
	public final UpgradeInventoryComponent upgradesInventory;
	public final MachineProcessingComponent processingComponent;

	public TileEntityFusionFurnace() {
		super(ModTileEntityTypes.FUSION_FURNACE);
		this.disableFaceInteraction();

		// Setup the input inventory with no filtering (no point since there are
		// multiple inputs).
		registerComponent(inputInventory = new InventoryComponent("InputInventory", 5, MachineSideMode.Input));

		// Setup all the other inventories.
		registerComponent(internalInventory = new InventoryComponent("InternalInventory", 5));
		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 3, MachineSideMode.Output));
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", energyStorage.getStorage()));
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));

		// Setup the processing component.
		registerComponent(processingComponent = new RecipeProcessingComponent<FusionFurnaceRecipe>("ProcessingComponent", FusionFurnaceRecipe.RECIPE_TYPE, 1, this::getMatchParameters,
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
		registerComponent(new OutputServoComponent("OutputServo", 4, outputInventory));

		// Set the energy storage upgrade inventory.
		energyStorage.setUpgradeInventory(upgradesInventory);
	}

	protected RecipeMatchParameters getMatchParameters(RecipeProcessingLocation location) {
		if (location == RecipeProcessingLocation.INTERNAL) {
			return new RecipeMatchParameters(internalInventory.getStackInSlot(0), internalInventory.getStackInSlot(1), internalInventory.getStackInSlot(2), internalInventory.getStackInSlot(3),
					internalInventory.getStackInSlot(4));
		} else {
			return new RecipeMatchParameters(inputInventory.getStackInSlot(0), inputInventory.getStackInSlot(1), inputInventory.getStackInSlot(2), inputInventory.getStackInSlot(3),
					inputInventory.getStackInSlot(4));
		}
	}

	protected ProcessingCheckState moveInputs(FusionFurnaceRecipe recipe) {
		// If the items can be insert into the output, transfer the items and return
		// true.
		if (!internalInventory.getStackInSlot(0).isEmpty()) {
			return ProcessingCheckState.internalInventoryNotEmpty();
		}
		if (InventoryUtilities.canFullyInsertAllItemsIntoInventory(outputInventory, recipe.getRecipeOutput())) {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}

		// Transfer the items.
		for (int i = 0; i < 5; i++) {
			int count = recipe.getRequiredCountOfItem(inputInventory.getStackInSlot(i));
			if (count > 0) {
				transferItemInternally(count, inputInventory, i, internalInventory, i);
			}
		}

		return ProcessingCheckState.ok();
	}

	protected ProcessingCheckState canProcessRecipe(FusionFurnaceRecipe recipe) {
		if (!InventoryUtilities.canFullyInsertItemIntoInventory(outputInventory, recipe.getRecipeOutput())) {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}
		return ProcessingCheckState.ok();
	}

	protected ProcessingCheckState processingCompleted(FusionFurnaceRecipe recipe) {
		// Insert the output into the output inventory.
		if (SDMath.diceRoll(recipe.getOutput().getOutputChance())) {
			InventoryUtilities.insertItemIntoInventory(outputInventory, recipe.getOutput().getItem().copy(), false);
		}

		// Clear the internal inventory.
		internalInventory.setStackInSlot(0, ItemStack.EMPTY);
		internalInventory.setStackInSlot(1, ItemStack.EMPTY);
		internalInventory.setStackInSlot(2, ItemStack.EMPTY);
		internalInventory.setStackInSlot(3, ItemStack.EMPTY);
		internalInventory.setStackInSlot(4, ItemStack.EMPTY);

		markTileEntityForSynchronization();
		return ProcessingCheckState.ok();
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerFusionFurnace(windowId, inventory, this);
	}
}