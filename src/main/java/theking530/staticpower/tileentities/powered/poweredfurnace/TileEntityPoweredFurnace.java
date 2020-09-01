package theking530.staticpower.tileentities.powered.poweredfurnace;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipeType;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.init.ModTileEntityTypes;
import theking530.staticpower.tileentities.TileEntityMachine;
import theking530.staticpower.tileentities.components.control.AbstractProcesingComponent.ProcessingCheckState;
import theking530.staticpower.tileentities.components.control.RecipeProcessingComponent;
import theking530.staticpower.tileentities.components.control.RecipeProcessingComponent.RecipeProcessingLocation;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.tileentities.components.items.BatteryInventoryComponent;
import theking530.staticpower.tileentities.components.items.CompoundInventoryComponent;
import theking530.staticpower.tileentities.components.items.InputServoComponent;
import theking530.staticpower.tileentities.components.items.InventoryComponent;
import theking530.staticpower.tileentities.components.items.ItemStackHandlerFilter;
import theking530.staticpower.tileentities.components.items.OutputServoComponent;
import theking530.staticpower.tileentities.components.items.UpgradeInventoryComponent;
import theking530.staticpower.utilities.InventoryUtilities;

/**
 * Baseic furnace machine. Same as a Vanila furnace except powered.
 * 
 * @author Amine Sebastian
 *
 */
public class TileEntityPoweredFurnace extends TileEntityMachine {
	/**
	 * Indicates how many times faster this block will perform compared to the
	 * vanila furnace.
	 */
	public static final float DEFAULT_PROCESSING_TIME_MULT = 2.0f;
	public static final int DEFAULT_PROCESSING_COST = 5;
	public static final int DEFAULT_MOVING_TIME = 4;

	public final InventoryComponent inputInventory;
	public final InventoryComponent outputInventory;
	public final InventoryComponent internalInventory;
	public final BatteryInventoryComponent batteryInventory;
	public final UpgradeInventoryComponent upgradesInventory;
	public final RecipeProcessingComponent<FurnaceRecipe> processingComponent;

	public TileEntityPoweredFurnace() {
		super(ModTileEntityTypes.POWERED_FURNACE);

		// Setup the input inventory to only accept items that have a valid recipe.
		registerComponent(inputInventory = new InventoryComponent("InputInventory", 1, MachineSideMode.Input).setShiftClickEnabled(true).setFilter(new ItemStackHandlerFilter() {
			public boolean canInsertItem(int slot, ItemStack stack) {
				return processingComponent.getRecipe(new RecipeMatchParameters(stack)).isPresent();
			}
		}));

		// Setup all the other inventories.
		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 1, MachineSideMode.Output));
		registerComponent(internalInventory = new InventoryComponent("InternalInventory", 1));
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", energyStorage.getStorage()));
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));
		registerComponent(new CompoundInventoryComponent("CompoundInventory", inputInventory, outputInventory));

		// Setup the processing component.
		registerComponent(processingComponent = new RecipeProcessingComponent<FurnaceRecipe>("ProcessingComponent", IRecipeType.SMELTING, 1, this::getMatchParameters, this::moveInputs,
				this::canProcessRecipe, this::processingCompleted));

		// Initialize the processing component to work with the redstone control
		// component, upgrade component and energy component.
		processingComponent.setShouldControlBlockState(true);
		processingComponent.setUpgradeInventory(upgradesInventory);
		processingComponent.setEnergyComponent(energyStorage);
		processingComponent.setRedstoneControlComponent(redstoneControlComponent);
		processingComponent.setProcessingPowerUsage(DEFAULT_PROCESSING_COST);

		// Setup the I/O servos.
		registerComponent(new InputServoComponent("InputServo", 4, inputInventory, 0));
		registerComponent(new OutputServoComponent("OutputServo", 4, outputInventory, 0));

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

	protected ProcessingCheckState moveInputs(FurnaceRecipe recipe) {
		if (!InventoryUtilities.canFullyInsertStackIntoSlot(outputInventory, 0, recipe.getRecipeOutput())) {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}
		transferItemInternally(inputInventory, 0, internalInventory, 0);
		processingComponent.setMaxProcessingTime(TileEntityPoweredFurnace.getCookTime(recipe));
		markTileEntityForSynchronization();
		return ProcessingCheckState.ok();
	}

	protected ProcessingCheckState canProcessRecipe(FurnaceRecipe recipe) {
		if (!InventoryUtilities.canFullyInsertStackIntoSlot(outputInventory, 0, recipe.getRecipeOutput())) {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}
		return ProcessingCheckState.ok();
	}

	protected ProcessingCheckState processingCompleted(FurnaceRecipe recipe) {
		outputInventory.insertItem(0, recipe.getRecipeOutput().copy(), false);
		internalInventory.setStackInSlot(0, ItemStack.EMPTY);
		markTileEntityForSynchronization();
		return ProcessingCheckState.ok();
	}

	public static int getCookTime(FurnaceRecipe recipe) {
		return (int) (recipe.getCookTime() / DEFAULT_PROCESSING_TIME_MULT);
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerPoweredFurnace(windowId, inventory, this);
	}
}
