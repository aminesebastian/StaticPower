package theking530.staticpower.tileentities.powered.lathe;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import theking530.staticcore.initialization.tileentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.wrappers.lathe.LatheRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.tileentities.TileEntityMachine;
import theking530.staticpower.tileentities.components.control.AbstractProcesingComponent.ProcessingCheckState;
import theking530.staticpower.tileentities.components.control.RecipeProcessingComponent;
import theking530.staticpower.tileentities.components.control.RecipeProcessingComponent.RecipeProcessingPhase;
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
	public static final BlockEntityTypeAllocator<TileEntityLathe> TYPE = new BlockEntityTypeAllocator<>((type, pos, state) -> new TileEntityLathe(pos, state), ModBlocks.Lathe);

	public final InventoryComponent inputInventory;
	public final InventoryComponent mainOutputInventory;
	public final InventoryComponent secondaryOutputInventory;
	public final FluidContainerInventoryComponent fluidContainerComponent;
	public final InventoryComponent internalInventory;
	public final BatteryInventoryComponent batteryInventory;
	public final UpgradeInventoryComponent upgradesInventory;

	public final RecipeProcessingComponent<LatheRecipe> processingComponent;
	public final FluidTankComponent fluidTankComponent;

	public TileEntityLathe(BlockPos pos, BlockState state) {
		super(TYPE, pos, state, StaticPowerTiers.STATIC);

		// Get the tier object.
		StaticPowerTier tierObject = StaticPowerConfig.getTier(getTier());

		registerComponent(inputInventory = new InventoryComponent("InputInventory", 9, MachineSideMode.Input).setShiftClickEnabled(true).setSlotsLockable(true));

		// Setup all the other inventories.
		registerComponent(internalInventory = new InventoryComponent("InternalInventory", 9));
		registerComponent(mainOutputInventory = new InventoryComponent("MainOutputInventory", 1, MachineSideMode.Output2));
		registerComponent(secondaryOutputInventory = new InventoryComponent("SecondaryOutputInventory", 1, MachineSideMode.Output3));
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", powerStorage));
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));

		// Setup the processing component to work with the redstone control component,
		// upgrade component and energy component.
		registerComponent(processingComponent = new RecipeProcessingComponent<LatheRecipe>("ProcessingComponent", 1, LatheRecipe.RECIPE_TYPE, this::getMatchParameters, this::canProcessRecipe,
				this::moveInputs, this::processingCompleted));

		// Initialize the processing component to work with the redstone control
		// component, upgrade component and energy component.
		processingComponent.setShouldControlBlockState(true);
		processingComponent.setUpgradeInventory(upgradesInventory);
		processingComponent.setPowerComponent(powerStorage);
		processingComponent.setRedstoneControlComponent(redstoneControlComponent);

		// Setup the I/O servos.
		registerComponent(new InputServoComponent("InputServo", inputInventory).setRoundRobin(true));
		registerComponent(new OutputServoComponent("OutputServo", mainOutputInventory));
		registerComponent(new OutputServoComponent("SecondaryOutputServo", secondaryOutputInventory));

		// Setup the fluid tank and fluid servo.
		registerComponent(fluidTankComponent = new FluidTankComponent("FluidTank", tierObject.defaultTankCapacity.get()).setCapabilityExposedModes(MachineSideMode.Output)
				.setUpgradeInventory(upgradesInventory));
		fluidTankComponent.setCanFill(false);
		registerComponent(new FluidOutputServoComponent("FluidOutputServoComponent", 100, fluidTankComponent, MachineSideMode.Output));

		// Register components to allow the lumbermill to fill buckets in the GUI.
		registerComponent(fluidContainerComponent = new FluidContainerInventoryComponent("FluidFillContainerServo", fluidTankComponent).setMode(FluidContainerInteractionMode.FILL));

		// Set the energy storage upgrade inventory.
		powerStorage.setUpgradeInventory(upgradesInventory);
	}

	protected RecipeMatchParameters getMatchParameters(RecipeProcessingPhase location) {
		if (location == RecipeProcessingPhase.PROCESSING) {
			return new RecipeMatchParameters(internalInventory.getStackInSlot(0), internalInventory.getStackInSlot(1), internalInventory.getStackInSlot(2),
					internalInventory.getStackInSlot(3), internalInventory.getStackInSlot(4), internalInventory.getStackInSlot(5), internalInventory.getStackInSlot(6),
					internalInventory.getStackInSlot(7), internalInventory.getStackInSlot(8));
		} else {
			return new RecipeMatchParameters(inputInventory.getStackInSlot(0), inputInventory.getStackInSlot(1), inputInventory.getStackInSlot(2), inputInventory.getStackInSlot(3),
					inputInventory.getStackInSlot(4), inputInventory.getStackInSlot(5), inputInventory.getStackInSlot(6), inputInventory.getStackInSlot(7), inputInventory.getStackInSlot(8));
		}
	}

	protected ProcessingCheckState moveInputs(LatheRecipe recipe) {
		// If the recipe cannot be insert into the output, return false.
		if (!canOutputsTakeRecipeResult(recipe)) {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}

		// Move the items.
		for (int i = 0; i < 9; i++) {
			transferItemInternally(recipe.getInputs().get(i).getCount(), inputInventory, i, internalInventory, i);
		}

		// Set the power usage.
		this.processingComponent.setProcessingPowerUsage(recipe.getPowerCost());
		this.processingComponent.setMaxProcessingTime(recipe.getProcessingTime());

		return ProcessingCheckState.ok();
	}

	protected ProcessingCheckState canProcessRecipe(LatheRecipe recipe, RecipeProcessingPhase location) {
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
		ItemStack primaryOutput = recipe.getPrimaryOutput().calculateOutput();
		ItemStack secondaryOutput = recipe.getSecondaryOutput().calculateOutput();

		mainOutputInventory.insertItem(0, primaryOutput, false);
		secondaryOutputInventory.insertItem(0, secondaryOutput, false);

		fluidTankComponent.fill(recipe.getOutputFluid(), FluidAction.EXECUTE);

		InventoryUtilities.clearInventory(internalInventory);
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
		return mode == MachineSideMode.Disabled || mode == MachineSideMode.Output || mode == MachineSideMode.Input || mode == MachineSideMode.Output2
				|| mode == MachineSideMode.Output3;
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerLathe(windowId, inventory, this);
	}
}
