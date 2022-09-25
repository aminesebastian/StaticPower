package theking530.staticpower.blockentities.machines.lumbermill;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blockentities.BlockEntityMachine;
import theking530.staticpower.blockentities.components.control.RecipeProcessingComponent;
import theking530.staticpower.blockentities.components.control.AbstractProcesingComponent.ProcessingCheckState;
import theking530.staticpower.blockentities.components.control.RecipeProcessingComponent.RecipeProcessingPhase;
import theking530.staticpower.blockentities.components.control.sideconfiguration.DefaultSideConfiguration;
import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticpower.blockentities.components.fluids.FluidOutputServoComponent;
import theking530.staticpower.blockentities.components.fluids.FluidTankComponent;
import theking530.staticpower.blockentities.components.items.BatteryInventoryComponent;
import theking530.staticpower.blockentities.components.items.FluidContainerInventoryComponent;
import theking530.staticpower.blockentities.components.items.InputServoComponent;
import theking530.staticpower.blockentities.components.items.InventoryComponent;
import theking530.staticpower.blockentities.components.items.ItemStackHandlerFilter;
import theking530.staticpower.blockentities.components.items.OutputServoComponent;
import theking530.staticpower.blockentities.components.items.UpgradeInventoryComponent;
import theking530.staticpower.blockentities.components.items.FluidContainerInventoryComponent.FluidContainerInteractionMode;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.wrappers.lumbermill.LumberMillRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.utilities.InventoryUtilities;

public class BlockEntityLumberMill extends BlockEntityMachine {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityLumberMill> TYPE = new BlockEntityTypeAllocator<>((type, pos, state) -> new BlockEntityLumberMill(pos, state),
			ModBlocks.LumberMill);

	public final InventoryComponent inputInventory;
	public final InventoryComponent mainOutputInventory;
	public final InventoryComponent secondaryOutputInventory;
	public final FluidContainerInventoryComponent fluidContainerComponent;
	public final InventoryComponent internalInventory;
	public final BatteryInventoryComponent batteryInventory;
	public final UpgradeInventoryComponent upgradesInventory;

	public final RecipeProcessingComponent<LumberMillRecipe> processingComponent;
	public final FluidTankComponent fluidTankComponent;

	public BlockEntityLumberMill(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);

		// Get the tier.
		StaticPowerTier tierObject = StaticPowerConfig.getTier(getTier());

		// Create the input inventory.
		registerComponent(inputInventory = new InventoryComponent("InputInventory", 1, MachineSideMode.Input).setShiftClickEnabled(true).setFilter(new ItemStackHandlerFilter() {
			public boolean canInsertItem(int slot, ItemStack stack) {
				return processingComponent.getRecipeMatchingParameters(new RecipeMatchParameters(stack).ignoreItemCounts()).isPresent();
			}
		}));

		// Setup all the other inventories.
		registerComponent(internalInventory = new InventoryComponent("InternalInventory", 1));
		registerComponent(mainOutputInventory = new InventoryComponent("MainOutputInventory", 1, MachineSideMode.Output2));
		registerComponent(secondaryOutputInventory = new InventoryComponent("SecondaryOutputInventory", 1, MachineSideMode.Output3));
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", powerStorage));
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));

		// Setup the processing component to work with the redstone control component,
		// upgrade component and energy component.
		registerComponent(processingComponent = new RecipeProcessingComponent<LumberMillRecipe>("ProcessingComponent", 1, RecipeProcessingComponent.MOVE_TIME, LumberMillRecipe.RECIPE_TYPE,
				this::getMatchParameters, this::canProcessRecipe, this::moveInputs, this::processingCompleted));

		// Initialize the processing component to work with the redstone control
		// component, upgrade component and energy component.
		processingComponent.setShouldControlBlockState(true);
		processingComponent.setUpgradeInventory(upgradesInventory);
		processingComponent.setPowerComponent(powerStorage);
		processingComponent.setRedstoneControlComponent(redstoneControlComponent);
		// Setup the I/O servos.
		registerComponent(new InputServoComponent("InputServo", inputInventory));
		registerComponent(new OutputServoComponent("OutputServo", mainOutputInventory));
		registerComponent(new OutputServoComponent("SecondaryOutputServo", secondaryOutputInventory));

		// Setup the fluid tank and fluid servo.
		registerComponent(fluidTankComponent = new FluidTankComponent("FluidTank", tierObject.defaultTankCapacity.get()).setCapabilityExposedModes(MachineSideMode.Output)
				.setUpgradeInventory(upgradesInventory));
		fluidTankComponent.setCanFill(false);
		registerComponent(new FluidOutputServoComponent("FluidOutputServoComponent", 100, fluidTankComponent, MachineSideMode.Output));

		// Register components to allow the lumbermill to fill buckets in the GUI.
		registerComponent(
				fluidContainerComponent = new FluidContainerInventoryComponent("FluidFillContainerServo", fluidTankComponent).setMode(FluidContainerInteractionMode.FILL));

		// Set the energy storage upgrade inventory.
		powerStorage.setUpgradeInventory(upgradesInventory);
	}

	protected RecipeMatchParameters getMatchParameters(RecipeProcessingPhase location) {
		if (location == RecipeProcessingPhase.PROCESSING) {
			return new RecipeMatchParameters(internalInventory.getStackInSlot(0));
		} else {
			return new RecipeMatchParameters(inputInventory.getStackInSlot(0));
		}
	}

	protected ProcessingCheckState moveInputs(LumberMillRecipe recipe) {
		// If the recipe cannot be insert into the output, return false.
		if (!canOutputsTakeRecipeResult(recipe)) {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}

		// Move the item.
		transferItemInternally(inputInventory, 0, internalInventory, 0);

		// Set the power usage.
		this.processingComponent.setProcessingPowerUsage(recipe.getPowerCost());
		this.processingComponent.setMaxProcessingTime(recipe.getProcessingTime());

		return ProcessingCheckState.ok();
	}

	protected ProcessingCheckState canProcessRecipe(LumberMillRecipe recipe, RecipeProcessingPhase location) {
		// If the recipe cannot be insert into the output, return false.
		if (!canOutputsTakeRecipeResult(recipe)) {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}

		if (fluidTankComponent.fill(recipe.getOutputFluid(), FluidAction.SIMULATE) != recipe.getOutputFluid().getAmount()) {
			return ProcessingCheckState.fluidOutputFull();
		}
		return ProcessingCheckState.ok();
	}

	protected ProcessingCheckState processingCompleted(LumberMillRecipe recipe) {
		ItemStack primaryOutput = recipe.getPrimaryOutput().calculateOutput();
		ItemStack secondaryOutput = recipe.getSecondaryOutput().calculateOutput();

		mainOutputInventory.insertItem(0, primaryOutput, false);
		secondaryOutputInventory.insertItem(0, secondaryOutput, false);

		fluidTankComponent.fill(recipe.getOutputFluid(), FluidAction.EXECUTE);

		internalInventory.setStackInSlot(0, ItemStack.EMPTY);
		return ProcessingCheckState.ok();
	}

	/**
	 * Ensure the output slots can take the results of the crafting.
	 * 
	 * @param recipe
	 * @return
	 */
	protected boolean canOutputsTakeRecipeResult(LumberMillRecipe recipe) {
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
		return new ContainerLumberMill(windowId, inventory, this);
	}

	@Override
	protected DefaultSideConfiguration getDefaultSideConfiguration() {
		return DEFAULT_NO_FACE_SIDE_CONFIGURATION.copy().setSide(BlockSide.BACK, true, MachineSideMode.Output2).setSide(BlockSide.RIGHT, true, MachineSideMode.Output3);
	}
}
