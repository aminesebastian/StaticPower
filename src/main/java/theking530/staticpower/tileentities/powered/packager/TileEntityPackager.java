package theking530.staticpower.tileentities.powered.packager;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.initialization.tileentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.wrappers.packager.PackagerRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.tileentities.TileEntityMachine;
import theking530.staticpower.tileentities.components.control.AbstractProcesingComponent.ProcessingCheckState;
import theking530.staticpower.tileentities.components.control.RecipeProcessingComponent;
import theking530.staticpower.tileentities.components.control.RecipeProcessingComponent.RecipeProcessingPhase;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.tileentities.components.items.BatteryInventoryComponent;
import theking530.staticpower.tileentities.components.items.InputServoComponent;
import theking530.staticpower.tileentities.components.items.InventoryComponent;
import theking530.staticpower.tileentities.components.items.ItemStackHandlerFilter;
import theking530.staticpower.tileentities.components.items.OutputServoComponent;
import theking530.staticpower.tileentities.components.items.UpgradeInventoryComponent;
import theking530.staticpower.tileentities.components.serialization.UpdateSerialize;
import theking530.staticpower.utilities.InventoryUtilities;

/**
 * Baseic furnace machine. Same as a Vanila furnace except powered.
 * 
 * @author Amine Sebastian
 *
 */
public class TileEntityPackager extends TileEntityMachine {
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityPackager> TYPE = new BlockEntityTypeAllocator<>((type, pos, state) -> new TileEntityPackager(pos, state), ModBlocks.Packager);

	/** The input inventory containing the items to pack. */
	public final InventoryComponent inputInventory;
	/** The output inventory where packaged items will be placed. */
	public final InventoryComponent outputInventory;
	/** The internal inventory where items that are being process are placed. */
	public final InventoryComponent internalInventory;
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
	protected int gridSize;;
	/** This value keeps track of the last grid size used when processing. */
	@UpdateSerialize
	protected int currentProcessingGridSize;

	public TileEntityPackager(BlockPos pos, BlockState state) {
		super(TYPE, pos, state, StaticPowerTiers.BASIC);

		// Default to a 2x2.
		gridSize = 2;
		currentProcessingGridSize = gridSize;

		// Setup the input inventory to only accept items that have a valid recipe.
		registerComponent(inputInventory = new InventoryComponent("InputInventory", 1, MachineSideMode.Input).setShiftClickEnabled(true).setFilter(new ItemStackHandlerFilter() {
			public boolean canInsertItem(int slot, ItemStack stack) {
				return processingComponent.getRecipeMatchingParameters(new RecipeMatchParameters(stack).setIntParameter("size", gridSize).ignoreItemCounts()).isPresent();
			}
		}));

		// Setup all the other inventories.
		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 1, MachineSideMode.Output));
		registerComponent(internalInventory = new InventoryComponent("InternalInventory", 1));
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", energyStorage));
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));

		// Setup the processing component.
		registerComponent(processingComponent = new RecipeProcessingComponent<PackagerRecipe>("ProcessingComponent", 1, PackagerRecipe.RECIPE_TYPE, this::getMatchParameters,
				this::canProcessRecipe, this::moveInputs, this::processingCompleted));

		// Initialize the processing component to work with the redstone control
		// component, upgrade component and energy component.
		processingComponent.setShouldControlBlockState(true);
		processingComponent.setUpgradeInventory(upgradesInventory);
		processingComponent.setEnergyComponent(energyStorage);
		processingComponent.setRedstoneControlComponent(redstoneControlComponent);
		processingComponent.setProcessingPowerUsage(StaticPowerConfig.SERVER.packagerPowerUsage.get());

		// Setup the I/O servos.
		registerComponent(new InputServoComponent("InputServo", inputInventory));
		registerComponent(new OutputServoComponent("OutputServo", outputInventory));

		// Set the energy storage upgrade inventory.
		energyStorage.setUpgradeInventory(upgradesInventory);
	}

	protected RecipeMatchParameters getMatchParameters(RecipeProcessingPhase location) {
		if (location == RecipeProcessingPhase.PROCESSING) {
			return new RecipeMatchParameters(internalInventory.getStackInSlot(0)).setIntParameter("size", currentProcessingGridSize);
		} else {
			return new RecipeMatchParameters(inputInventory.getStackInSlot(0)).setIntParameter("size", gridSize);
		}
	}

	protected ProcessingCheckState moveInputs(PackagerRecipe recipe) {
		// Make sure we can insert the output into the output slot.
		if (!InventoryUtilities.canFullyInsertStackIntoSlot(outputInventory, 0, recipe.getOutput().getItem())) {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}

		// Move the input to the internal inventory.
		transferItemInternally(recipe.getInputIngredient().getCount(), inputInventory, 0, internalInventory, 0);

		// Update the processing/power.
		processingComponent.setMaxProcessingTime(recipe.getProcessingTime());
		processingComponent.setProcessingPowerUsage(recipe.getPowerCost());

		// Update the internal grid size.
		currentProcessingGridSize = gridSize;

		// Sync
		return ProcessingCheckState.ok();
	}

	protected ProcessingCheckState canProcessRecipe(PackagerRecipe recipe, RecipeProcessingPhase location) {
		if (!InventoryUtilities.canFullyInsertStackIntoSlot(outputInventory, 0, recipe.getOutput().getItem())) {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}
		return ProcessingCheckState.ok();
	}

	protected ProcessingCheckState processingCompleted(PackagerRecipe recipe) {
		ItemStack output = recipe.getOutput().calculateOutput();
		outputInventory.insertItem(0, output, false);
		internalInventory.setStackInSlot(0, ItemStack.EMPTY);
		return ProcessingCheckState.ok();
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
