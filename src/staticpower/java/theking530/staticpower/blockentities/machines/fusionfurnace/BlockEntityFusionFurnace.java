package theking530.staticpower.blockentities.machines.fusionfurnace;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.blockentity.components.control.processing.ConcretizedProductContainer;
import theking530.staticcore.blockentity.components.control.processing.ProcessingCheckState;
import theking530.staticcore.blockentity.components.control.processing.ProcessingContainer;
import theking530.staticcore.blockentity.components.control.processing.ProcessingContainer.CaptureType;
import theking530.staticcore.blockentity.components.control.processing.recipe.IRecipeProcessor;
import theking530.staticcore.blockentity.components.control.processing.recipe.RecipeProcessingComponent;
import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.blockentity.components.items.BatteryInventoryComponent;
import theking530.staticcore.blockentity.components.items.InputServoComponent;
import theking530.staticcore.blockentity.components.items.InventoryComponent;
import theking530.staticcore.blockentity.components.items.OutputServoComponent;
import theking530.staticcore.blockentity.components.items.UpgradeInventoryComponent;
import theking530.staticcore.crafting.RecipeMatchParameters;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticcore.utilities.item.InventoryUtilities;
import theking530.staticpower.blockentities.BlockEntityMachine;
import theking530.staticpower.data.crafting.wrappers.fusionfurnace.FusionFurnaceRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModRecipeTypes;

public class BlockEntityFusionFurnace extends BlockEntityMachine implements IRecipeProcessor<FusionFurnaceRecipe> {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityFusionFurnace> TYPE = new BlockEntityTypeAllocator<>(
			"fusion_furnace", (type, pos, state) -> new BlockEntityFusionFurnace(pos, state), ModBlocks.FusionFurnace);

	public final InventoryComponent inputInventory;
	public final InventoryComponent outputInventory;
	public final BatteryInventoryComponent batteryInventory;
	public final UpgradeInventoryComponent upgradesInventory;
	public final RecipeProcessingComponent<FusionFurnaceRecipe> processingComponent;

	public BlockEntityFusionFurnace(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);

		// Setup the input inventory with no filtering (no point since there are
		// multiple inputs).
		registerComponent(inputInventory = new InventoryComponent("InputInventory", 5, MachineSideMode.Input)
				.setShiftClickEnabled(true));

		// Setup all the other inventories.
		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 3, MachineSideMode.Output));
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", powerStorage));
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));

		// Setup the processing component.
		registerComponent(processingComponent = new RecipeProcessingComponent<FusionFurnaceRecipe>(
				"ProcessingComponent", 0, ModRecipeTypes.FUSION_FURNACE_RECIPE_TYPE.get()));

		// Initialize the processing component to work with the redstone control
		// component, upgrade component and energy component.
		processingComponent.setShouldControlOnBlockState(true);
		processingComponent.setUpgradeInventory(upgradesInventory);
		processingComponent.setPowerComponent(powerStorage);
		processingComponent.setRedstoneControlComponent(redstoneControlComponent);

		// Setup the I/O servos.
		registerComponent(new InputServoComponent("InputServo", 4, inputInventory));
		registerComponent(new OutputServoComponent("OutputServo", 4, outputInventory));

		// Set the energy storage upgrade inventory.
		powerStorage.setUpgradeInventory(upgradesInventory);
	}

	@Override
	public RecipeMatchParameters getRecipeMatchParameters(RecipeProcessingComponent<FusionFurnaceRecipe> component) {
		return new RecipeMatchParameters(inputInventory.getStackInSlot(0), inputInventory.getStackInSlot(1),
				inputInventory.getStackInSlot(2), inputInventory.getStackInSlot(3), inputInventory.getStackInSlot(4));
	}

	@Override
	public void captureOutputs(RecipeProcessingComponent<FusionFurnaceRecipe> component, FusionFurnaceRecipe recipe,
			ConcretizedProductContainer outputContainer) {
		outputContainer.addItem(recipe.getOutput().calculateOutput(), CaptureType.BOTH);
	}

	@Override
	public ProcessingCheckState canStartProcessingRecipe(RecipeProcessingComponent<FusionFurnaceRecipe> component,
			FusionFurnaceRecipe recipe, ConcretizedProductContainer outputContainer) {
		if (!InventoryUtilities.canFullyInsertAllItemsIntoInventory(outputInventory, outputContainer.getItems())) {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}
		return ProcessingCheckState.ok();
	}

	@Override
	public void captureInputs(RecipeProcessingComponent<FusionFurnaceRecipe> component, FusionFurnaceRecipe recipe,
			ProcessingContainer processingContainer, ConcretizedProductContainer inputContainer) {
		for (int i = 0; i < 5; i++) {
			int count = component.getProcessingRecipe().get().getRequiredCountOfItem(inputInventory.getStackInSlot(i));
			if (count > 0) {
				inputContainer.addItem(inputInventory.extractItem(i, count, false), CaptureType.BOTH);
			}
		}
	}

	@Override
	public void onProcessingCompleted(RecipeProcessingComponent<FusionFurnaceRecipe> component,
			ProcessingContainer processingContainer) {
		outputInventory.insertItem(0, processingContainer.getOutputs().getItem(0).copy(), false);
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerFusionFurnace(windowId, inventory, this);
	}

}