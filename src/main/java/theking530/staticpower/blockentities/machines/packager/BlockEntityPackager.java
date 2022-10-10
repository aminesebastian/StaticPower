package theking530.staticpower.blockentities.machines.packager;

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
import theking530.staticpower.blockentities.components.control.processing.ProcessingCheckState;
import theking530.staticpower.blockentities.components.control.processing.ProcessingOutputContainer;
import theking530.staticpower.blockentities.components.control.processing.ProcessingOutputContainer.CaptureType;
import theking530.staticpower.blockentities.components.control.processing.RecipeProcessingComponent;
import theking530.staticpower.blockentities.components.control.processing.interfaces.IRecipeProcessor;
import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.blockentities.components.items.BatteryInventoryComponent;
import theking530.staticpower.blockentities.components.items.InputServoComponent;
import theking530.staticpower.blockentities.components.items.InventoryComponent;
import theking530.staticpower.blockentities.components.items.ItemStackHandlerFilter;
import theking530.staticpower.blockentities.components.items.OutputServoComponent;
import theking530.staticpower.blockentities.components.items.UpgradeInventoryComponent;
import theking530.staticpower.blockentities.components.serialization.UpdateSerialize;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.wrappers.packager.PackagerRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.utilities.InventoryUtilities;

/**
 * Baseic furnace machine. Same as a Vanila furnace except powered.
 * 
 * @author Amine Sebastian
 *
 */
public class BlockEntityPackager extends BlockEntityMachine implements IRecipeProcessor<PackagerRecipe> {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityPackager> TYPE = new BlockEntityTypeAllocator<>((type, pos, state) -> new BlockEntityPackager(pos, state),
			ModBlocks.Packager);

	/** The input inventory containing the items to pack. */
	public final InventoryComponent inputInventory;
	/** The output inventory where packaged items will be placed. */
	public final InventoryComponent outputInventory;
	/** The battery inventory that automatically charges the storage. */
	public final BatteryInventoryComponent batteryInventory;
	/** The upgrades inventory handles power and processing speed upgrades. */
	public final UpgradeInventoryComponent upgradesInventory;
	/**
	 * The processing component that handles the recipe processing of this machine.
	 */
	public final RecipeProcessingComponent<PackagerRecipe> processingComponent;
	/** The crafting grid size to use (2x2 vs 3x3). */
	@UpdateSerialize
	protected int gridSize;

	public BlockEntityPackager(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);

		// Default to a 2x2.
		gridSize = 2;

		// Setup the input inventory to only accept items that have a valid recipe.
		registerComponent(inputInventory = new InventoryComponent("InputInventory", 1, MachineSideMode.Input).setShiftClickEnabled(true).setFilter(new ItemStackHandlerFilter() {
			public boolean canInsertItem(int slot, ItemStack stack) {
				return processingComponent.getRecipeMatchingParameters(new RecipeMatchParameters(stack).setIntParameter("size", gridSize).ignoreItemCounts()).isPresent();
			}
		}));

		// Setup all the other inventories.
		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 1, MachineSideMode.Output));
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", powerStorage));
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));

		// Setup the processing component.
		registerComponent(processingComponent = new RecipeProcessingComponent<PackagerRecipe>("ProcessingComponent", PackagerRecipe.RECIPE_TYPE, this));

		// Initialize the processing component to work with the redstone control
		// component, upgrade component and energy component.
		processingComponent.setShouldControlBlockState(true);
		processingComponent.setUpgradeInventory(upgradesInventory);
		processingComponent.setPowerComponent(powerStorage);
		processingComponent.setRedstoneControlComponent(redstoneControlComponent);
		processingComponent.setProcessingPowerUsage(StaticPowerConfig.SERVER.packagerPowerUsage.get());

		// Setup the I/O servos.
		registerComponent(new InputServoComponent("InputServo", inputInventory));
		registerComponent(new OutputServoComponent("OutputServo", outputInventory));

		// Set the energy storage upgrade inventory.
		powerStorage.setUpgradeInventory(upgradesInventory);
	}

	@Override
	public RecipeMatchParameters getRecipeMatchParameters(RecipeProcessingComponent<PackagerRecipe> component) {
		return new RecipeMatchParameters(inputInventory.getStackInSlot(0)).setIntParameter("size", gridSize);
	}

	@Override
	public void captureInputsAndProducts(RecipeProcessingComponent<PackagerRecipe> component, PackagerRecipe recipe, ProcessingOutputContainer outputContainer) {
		// Move the input to the internal inventory.
		outputContainer.addInputItem(inputInventory.extractItem(0, recipe.getInputIngredient().getCount(), true), CaptureType.BOTH);
		outputContainer.addOutputItem(recipe.getOutput().calculateOutput(), CaptureType.BOTH);
		outputContainer.getCustomParameterContainer().putInt("size", gridSize);

		// Update the processing/power.
		component.setMaxProcessingTime(recipe.getProcessingTime());
		component.setProcessingPowerUsage(recipe.getPowerCost());
	}

	@Override
	public void processingStarted(RecipeProcessingComponent<PackagerRecipe> component, PackagerRecipe recipe, ProcessingOutputContainer outputContainer) {
		inputInventory.extractItem(0, recipe.getInputIngredient().getCount(), false);
	}

	@Override
	public ProcessingCheckState canStartProcessing(RecipeProcessingComponent<PackagerRecipe> component, PackagerRecipe recipe, ProcessingOutputContainer outputContainer) {
		if (!InventoryUtilities.canFullyInsertStackIntoSlot(outputInventory, 0, outputContainer.getOutputItem(0).item())) {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}
		return ProcessingCheckState.ok();
	}

	@Override
	public void processingCompleted(RecipeProcessingComponent<PackagerRecipe> component, PackagerRecipe recipe, ProcessingOutputContainer outputContainer) {
		outputInventory.insertItem(0, outputContainer.getOutputItem(0).item().copy(), false);
	}

	public void setRecipeSize(int size) {
		this.gridSize = size;
	}

	public int getRecipeSize() {
		return gridSize;
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerPackager(windowId, inventory, this);
	}

}
