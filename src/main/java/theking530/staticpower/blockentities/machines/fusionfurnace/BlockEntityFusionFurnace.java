package theking530.staticpower.blockentities.machines.fusionfurnace;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blockentities.BlockEntityMachine;
import theking530.staticpower.blockentities.components.control.RecipeProcessingComponent;
import theking530.staticpower.blockentities.components.control.AbstractProcesingComponent.ProcessingCheckState;
import theking530.staticpower.blockentities.components.control.RecipeProcessingComponent.RecipeProcessingPhase;
import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.blockentities.components.items.BatteryInventoryComponent;
import theking530.staticpower.blockentities.components.items.InputServoComponent;
import theking530.staticpower.blockentities.components.items.InventoryComponent;
import theking530.staticpower.blockentities.components.items.OutputServoComponent;
import theking530.staticpower.blockentities.components.items.UpgradeInventoryComponent;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.wrappers.fusionfurnace.FusionFurnaceRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.utilities.InventoryUtilities;

public class BlockEntityFusionFurnace extends BlockEntityMachine {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityFusionFurnace> TYPE = new BlockEntityTypeAllocator<>((type, pos, state) -> new BlockEntityFusionFurnace(pos, state),
			ModBlocks.FusionFurnace);

	public final InventoryComponent inputInventory;
	public final InventoryComponent outputInventory;
	public final InventoryComponent internalInventory;
	public final BatteryInventoryComponent batteryInventory;
	public final UpgradeInventoryComponent upgradesInventory;
	public final RecipeProcessingComponent<FusionFurnaceRecipe> processingComponent;

	public BlockEntityFusionFurnace(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);
		this.disableFaceInteraction();

		// Setup the input inventory with no filtering (no point since there are
		// multiple inputs).
		registerComponent(inputInventory = new InventoryComponent("InputInventory", 5, MachineSideMode.Input).setShiftClickEnabled(true));

		// Setup all the other inventories.
		registerComponent(internalInventory = new InventoryComponent("InternalInventory", 5));
		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 3, MachineSideMode.Output));
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", powerStorage));
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));

		// Setup the processing component.
		registerComponent(
				processingComponent = new RecipeProcessingComponent<FusionFurnaceRecipe>("ProcessingComponent", StaticPowerConfig.SERVER.fusionFurnaceProcessingTime.get(),
						RecipeProcessingComponent.MOVE_TIME, FusionFurnaceRecipe.RECIPE_TYPE, this::getMatchParameters, this::canProcessRecipe, this::moveInputs, this::processingCompleted));

		// Initialize the processing component to work with the redstone control
		// component, upgrade component and energy component.
		processingComponent.setShouldControlBlockState(true);
		processingComponent.setUpgradeInventory(upgradesInventory);
		processingComponent.setPowerComponent(powerStorage);
		processingComponent.setRedstoneControlComponent(redstoneControlComponent);

		// Setup the I/O servos.
		registerComponent(new InputServoComponent("InputServo", 4, inputInventory));
		registerComponent(new OutputServoComponent("OutputServo", 4, outputInventory));

		// Set the energy storage upgrade inventory.
		powerStorage.setUpgradeInventory(upgradesInventory);
	}

	protected RecipeMatchParameters getMatchParameters(RecipeProcessingPhase location) {
		if (location == RecipeProcessingPhase.PROCESSING) {
			return new RecipeMatchParameters(internalInventory.getStackInSlot(0), internalInventory.getStackInSlot(1), internalInventory.getStackInSlot(2),
					internalInventory.getStackInSlot(3), internalInventory.getStackInSlot(4));
		} else {
			return new RecipeMatchParameters(inputInventory.getStackInSlot(0), inputInventory.getStackInSlot(1), inputInventory.getStackInSlot(2), inputInventory.getStackInSlot(3),
					inputInventory.getStackInSlot(4));
		}
	}

	protected ProcessingCheckState moveInputs(FusionFurnaceRecipe recipe) {
		// If the items can be insert into the output, transfer the items and return
		// true.
		if (!InventoryUtilities.canFullyInsertAllItemsIntoInventory(outputInventory, recipe.getOutput().getItem())) {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}

		// Transfer the items.
		for (int i = 0; i < 5; i++) {
			int count = recipe.getRequiredCountOfItem(inputInventory.getStackInSlot(i));
			if (count > 0) {
				transferItemInternally(count, inputInventory, i, internalInventory, i);
			}
		}

		// Set the power usage.
		processingComponent.setProcessingPowerUsage(recipe.getPowerCost());
		processingComponent.setMaxProcessingTime(recipe.getProcessingTime());

		return ProcessingCheckState.ok();
	}

	protected ProcessingCheckState canProcessRecipe(FusionFurnaceRecipe recipe, RecipeProcessingPhase location) {
		if (!InventoryUtilities.canFullyInsertItemIntoInventory(outputInventory, recipe.getOutput().getItem())) {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}
		return ProcessingCheckState.ok();
	}

	protected ProcessingCheckState processingCompleted(FusionFurnaceRecipe recipe) {
		// Insert the output into the output inventory.
		ItemStack output = recipe.getOutput().calculateOutput();
		InventoryUtilities.insertItemIntoInventory(outputInventory, output, false);

		// Clear the internal inventory.
		internalInventory.setStackInSlot(0, ItemStack.EMPTY);
		internalInventory.setStackInSlot(1, ItemStack.EMPTY);
		internalInventory.setStackInSlot(2, ItemStack.EMPTY);
		internalInventory.setStackInSlot(3, ItemStack.EMPTY);
		internalInventory.setStackInSlot(4, ItemStack.EMPTY);
		return ProcessingCheckState.ok();
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerFusionFurnace(windowId, inventory, this);
	}
}