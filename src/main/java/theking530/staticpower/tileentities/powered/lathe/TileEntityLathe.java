package theking530.staticpower.tileentities.powered.lathe;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import theking530.staticcore.initialization.tileentity.TileEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticcore.utilities.SDMath;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.wrappers.lathe.LatheRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.tileentities.TileEntityMachine;
import theking530.staticpower.tileentities.components.control.AbstractProcesingComponent.ProcessingCheckState;
import theking530.staticpower.tileentities.components.control.RecipeProcessingComponent;
import theking530.staticpower.tileentities.components.control.RecipeProcessingComponent.RecipeProcessingLocation;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticpower.tileentities.components.fluids.FluidOutputServoComponent;
import theking530.staticpower.tileentities.components.fluids.FluidTankComponent;
import theking530.staticpower.tileentities.components.items.BatteryInventoryComponent;
import theking530.staticpower.tileentities.components.items.FluidContainerInventoryComponent;
import theking530.staticpower.tileentities.components.items.FluidContainerInventoryComponent.FluidContainerInteractionMode;
import theking530.staticpower.tileentities.components.items.InputServoComponent;
import theking530.staticpower.tileentities.components.items.InventoryComponent;
import theking530.staticpower.tileentities.components.items.OutputServoComponent;
import theking530.staticpower.tileentities.components.items.UpgradeInventoryComponent;
import theking530.staticpower.utilities.InventoryUtilities;

public class TileEntityLathe extends TileEntityMachine {
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityLathe> TYPE = new TileEntityTypeAllocator<>((type) -> new TileEntityLathe(), ModBlocks.Lathe);

	public static final int DEFAULT_PROCESSING_TIME = 150;
	public static final int DEFAULT_PROCESSING_COST = 5;
	public static final int DEFAULT_MOVING_TIME = 4;
	public static final int DEFAULT_TANK_SIZE = 5000;

	public final InventoryComponent inputInventory;
	public final InventoryComponent mainOutputInventory;
	public final InventoryComponent secondaryOutputInventory;
	public final FluidContainerInventoryComponent fluidContainerComponent;
	public final InventoryComponent internalInventory;
	public final BatteryInventoryComponent batteryInventory;
	public final UpgradeInventoryComponent upgradesInventory;

	public final RecipeProcessingComponent<LatheRecipe> processingComponent;
	public final FluidTankComponent fluidTankComponent;

	public TileEntityLathe() {
		super(TYPE);

		registerComponent(inputInventory = new InventoryComponent("InputInventory", 4, MachineSideMode.Input).setShiftClickEnabled(true).setSlotsLockable(true));

		// Setup all the other inventories.
		registerComponent(internalInventory = new InventoryComponent("InternalInventory", 4));
		registerComponent(mainOutputInventory = new InventoryComponent("MainOutputInventory", 1, MachineSideMode.Output2));
		registerComponent(secondaryOutputInventory = new InventoryComponent("SecondaryOutputInventory", 1, MachineSideMode.Output3));
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", energyStorage.getStorage()));
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));

		// Setup the processing component to work with the redstone control component,
		// upgrade component and energy component.
		registerComponent(processingComponent = new RecipeProcessingComponent<LatheRecipe>("ProcessingComponent", LatheRecipe.RECIPE_TYPE, 1, this::getMatchParameters, this::moveInputs,
				this::canProcessRecipe, this::processingCompleted));

		// Initialize the processing component to work with the redstone control
		// component, upgrade component and energy component.
		processingComponent.setShouldControlBlockState(true);
		processingComponent.setUpgradeInventory(upgradesInventory);
		processingComponent.setEnergyComponent(energyStorage);
		processingComponent.setRedstoneControlComponent(redstoneControlComponent);
		processingComponent.setProcessingPowerUsage(DEFAULT_PROCESSING_COST);

		// Setup the I/O servos.
		registerComponent(new InputServoComponent("InputServo", inputInventory).setRoundRobin(true));
		registerComponent(new OutputServoComponent("OutputServo", mainOutputInventory));
		registerComponent(new OutputServoComponent("SecondaryOutputServo", secondaryOutputInventory));

		// Setup the fluid tank and fluid servo.
		registerComponent(
				fluidTankComponent = new FluidTankComponent("FluidTank", DEFAULT_TANK_SIZE).setCapabilityExposedModes(MachineSideMode.Output).setUpgradeInventory(upgradesInventory));
		fluidTankComponent.setCanFill(false);
		registerComponent(new FluidOutputServoComponent("FluidOutputServoComponent", 100, fluidTankComponent, MachineSideMode.Output));

		// Register components to allow the lumbermill to fill buckets in the GUI.
		registerComponent(fluidContainerComponent = new FluidContainerInventoryComponent("FluidFillContainerServo", fluidTankComponent).setMode(FluidContainerInteractionMode.FILL));

		// Set the energy storage upgrade inventory.
		energyStorage.setUpgradeInventory(upgradesInventory);
	}

	protected RecipeMatchParameters getMatchParameters(RecipeProcessingLocation location) {
		if (location == RecipeProcessingLocation.INTERNAL) {
			return new RecipeMatchParameters(internalInventory.getStackInSlot(0), internalInventory.getStackInSlot(1), internalInventory.getStackInSlot(2),
					internalInventory.getStackInSlot(3));
		} else {
			return new RecipeMatchParameters(inputInventory.getStackInSlot(0), inputInventory.getStackInSlot(1), inputInventory.getStackInSlot(2), inputInventory.getStackInSlot(3));
		}
	}

	protected ProcessingCheckState moveInputs(LatheRecipe recipe) {
		// If the recipe cannot be insert into the output, return false.
		if (!canOutputsTakeRecipeResult(recipe)) {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}

		// Move the item.
		transferItemInternally(inputInventory, 0, internalInventory, 0);
		transferItemInternally(inputInventory, 1, internalInventory, 1);
		transferItemInternally(inputInventory, 2, internalInventory, 2);
		transferItemInternally(inputInventory, 3, internalInventory, 3);
		markTileEntityForSynchronization();
		return ProcessingCheckState.ok();
	}

	protected ProcessingCheckState canProcessRecipe(LatheRecipe recipe) {
		// If the recipe cannot be insert into the output, return false.
		if (!canOutputsTakeRecipeResult(recipe)) {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}

		if (fluidTankComponent.fill(recipe.getOutputFluid(), FluidAction.SIMULATE) != recipe.getOutputFluid().getAmount()) {
			return ProcessingCheckState.outputTankCannotTakeFluid();
		}
		return ProcessingCheckState.ok();
	}

	protected ProcessingCheckState processingCompleted(LatheRecipe recipe) {
		if (SDMath.diceRoll(recipe.getPrimaryOutput().getOutputChance())) {
			mainOutputInventory.insertItem(0, recipe.getPrimaryOutput().getItem().copy(), false);
		}
		if (SDMath.diceRoll(recipe.getSecondaryOutput().getOutputChance())) {
			secondaryOutputInventory.insertItem(0, recipe.getSecondaryOutput().getItem().copy(), false);
		}
		fluidTankComponent.fill(recipe.getOutputFluid(), FluidAction.EXECUTE);

		InventoryUtilities.clearInventory(internalInventory);
		markTileEntityForSynchronization();
		return ProcessingCheckState.ok();
	}

	/**
	 * Ensure the output slots can take the results of the crafting.
	 * 
	 * @param recipe
	 * @return
	 */
	protected boolean canOutputsTakeRecipeResult(LatheRecipe recipe) {
		if (!InventoryUtilities.canFullyInsertStackIntoSlot(mainOutputInventory, 0, recipe.getPrimaryOutput().getItem())) {
			return false;
		} else if (!InventoryUtilities.canFullyInsertStackIntoSlot(secondaryOutputInventory, 0, recipe.getSecondaryOutput().getItem())) {
			return false;
		} else if (fluidTankComponent.fill(recipe.getOutputFluid(), FluidAction.SIMULATE) != recipe.getOutputFluid().getAmount()) {
			return false;
		}
		return true;
	}

	@Override
	protected boolean isValidSideConfiguration(BlockSide side, MachineSideMode mode) {
		return mode == MachineSideMode.Disabled || mode == MachineSideMode.Regular || mode == MachineSideMode.Output || mode == MachineSideMode.Input || mode == MachineSideMode.Output2
				|| mode == MachineSideMode.Output3;
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerLathe(windowId, inventory, this);
	}
}
