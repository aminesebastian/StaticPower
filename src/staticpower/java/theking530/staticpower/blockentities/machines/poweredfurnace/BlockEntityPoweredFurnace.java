package theking530.staticpower.blockentities.machines.poweredfurnace;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.blockentity.components.control.oldprocessing.OldProcessingContainer;
import theking530.staticcore.blockentity.components.control.oldprocessing.OldProcessingContainer.CaptureType;
import theking530.staticcore.blockentity.components.control.oldprocessing.OldRecipeProcessingComponent;
import theking530.staticcore.blockentity.components.control.oldprocessing.interfaces.IOldRecipeProcessor;
import theking530.staticcore.blockentity.components.control.processing.ProcessingCheckState;
import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.blockentity.components.items.BatteryInventoryComponent;
import theking530.staticcore.blockentity.components.items.InputServoComponent;
import theking530.staticcore.blockentity.components.items.InventoryComponent;
import theking530.staticcore.blockentity.components.items.ItemStackHandlerFilter;
import theking530.staticcore.blockentity.components.items.OutputServoComponent;
import theking530.staticcore.blockentity.components.items.UpgradeInventoryComponent;
import theking530.staticcore.crafting.RecipeMatchParameters;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticcore.utilities.item.InventoryUtilities;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blockentities.BlockEntityMachine;
import theking530.staticpower.init.ModBlocks;

/**
 * Baseic furnace machine. Same as a Vanila furnace except powered.
 * 
 * @author Amine Sebastian
 *
 */
public class BlockEntityPoweredFurnace extends BlockEntityMachine implements IOldRecipeProcessor<SmeltingRecipe> {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityPoweredFurnace> TYPE = new BlockEntityTypeAllocator<>("powered_furnace",
			(type, pos, state) -> new BlockEntityPoweredFurnace(pos, state), ModBlocks.PoweredFurnace);

	/**
	 * Indicates how many times faster this block will perform compared to the
	 * vanila furnace.
	 */
	public static final float DEFAULT_PROCESSING_TIME_MULT = 1.25f;

	public final InventoryComponent inputInventory;
	public final InventoryComponent outputInventory;
	public final BatteryInventoryComponent batteryInventory;
	public final UpgradeInventoryComponent upgradesInventory;
	public final OldRecipeProcessingComponent<SmeltingRecipe> processingComponent;

	public BlockEntityPoweredFurnace(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);

		// Setup the input inventory to only accept items that have a valid recipe.
		registerComponent(inputInventory = new InventoryComponent("InputInventory", 1, MachineSideMode.Input).setShiftClickEnabled(true).setFilter(new ItemStackHandlerFilter() {
			public boolean canInsertItem(int slot, ItemStack stack) {
				return processingComponent.getRecipeMatchingParameters(new RecipeMatchParameters(stack).ignoreItemCounts()).isPresent();
			}
		}));

		// Setup all the other inventories.
		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 1, MachineSideMode.Output));
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", powerStorage));
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));

		// Setup the processing component.
		registerComponent(processingComponent = new OldRecipeProcessingComponent<SmeltingRecipe>("ProcessingComponent", RecipeType.SMELTING, this));
		processingComponent.setShouldControlBlockState(true);
		processingComponent.setUpgradeInventory(upgradesInventory);
		processingComponent.setPowerComponent(powerStorage);
		processingComponent.setRedstoneControlComponent(redstoneControlComponent);
		processingComponent.setProcessingPowerUsage(StaticPowerConfig.SERVER.poweredFurnacePowerUsage.get());

		// Setup the I/O servos.
		registerComponent(new InputServoComponent("InputServo", inputInventory));
		registerComponent(new OutputServoComponent("OutputServo", outputInventory));

		// Set the energy storage upgrade inventory.
		powerStorage.setUpgradeInventory(upgradesInventory);
	}

	@Override
	public RecipeMatchParameters getRecipeMatchParameters(OldRecipeProcessingComponent<SmeltingRecipe> component) {
		return new RecipeMatchParameters(inputInventory.getStackInSlot(0));
	}

	@Override
	public ProcessingCheckState canStartProcessing(OldRecipeProcessingComponent<SmeltingRecipe> component, SmeltingRecipe recipe, OldProcessingContainer outputContainer) {
		if (!InventoryUtilities.canFullyInsertStackIntoSlot(outputInventory, 0, outputContainer.getOutputItems().get(0).item())) {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}
		return ProcessingCheckState.ok();
	}

	@Override
	public void captureInputsAndProducts(OldRecipeProcessingComponent<SmeltingRecipe> component, SmeltingRecipe recipe, OldProcessingContainer outputContainer) {
		outputContainer.addInputItem(inputInventory.extractItem(0, 1, true), CaptureType.BOTH);
		outputContainer.addOutputItem(recipe.getResultItem().copy(), CaptureType.BOTH);
		component.setMaxProcessingTime(getCookTime(recipe));
	}

	@Override
	public void processingStarted(OldRecipeProcessingComponent<SmeltingRecipe> component, SmeltingRecipe recipe, OldProcessingContainer outputContainer) {
		inputInventory.extractItem(0, 1, false);
	}

	@Override
	public void processingCompleted(OldRecipeProcessingComponent<SmeltingRecipe> component, SmeltingRecipe recipe, OldProcessingContainer outputContainer) {
		outputInventory.insertItem(0, outputContainer.getOutputItem(0).item().copy(), false);
	}

	public static int getCookTime(SmeltingRecipe recipe) {
		return (int) (recipe.getCookingTime() / DEFAULT_PROCESSING_TIME_MULT);
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerPoweredFurnace(windowId, inventory, this);
	}
}
