package theking530.staticpower.blockentities.machines.lathe;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
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
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationPreset;
import theking530.staticpower.blockentities.components.fluids.FluidOutputServoComponent;
import theking530.staticpower.blockentities.components.fluids.FluidTankComponent;
import theking530.staticpower.blockentities.components.items.BatteryInventoryComponent;
import theking530.staticpower.blockentities.components.items.FluidContainerInventoryComponent;
import theking530.staticpower.blockentities.components.items.FluidContainerInventoryComponent.FluidContainerInteractionMode;
import theking530.staticpower.blockentities.components.items.InputServoComponent;
import theking530.staticpower.blockentities.components.items.InventoryComponent;
import theking530.staticpower.blockentities.components.items.OutputServoComponent;
import theking530.staticpower.blockentities.components.items.UpgradeInventoryComponent;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.wrappers.lathe.LatheRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.utilities.InventoryUtilities;

public class BlockEntityLathe extends BlockEntityMachine implements IRecipeProcessor<LatheRecipe> {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityLathe> TYPE = new BlockEntityTypeAllocator<>("lathe", (type, pos, state) -> new BlockEntityLathe(pos, state),
			ModBlocks.Lathe);

	public final InventoryComponent inputInventory;
	public final InventoryComponent mainOutputInventory;
	public final InventoryComponent secondaryOutputInventory;
	public final FluidContainerInventoryComponent fluidContainerComponent;
	public final BatteryInventoryComponent batteryInventory;
	public final UpgradeInventoryComponent upgradesInventory;

	public final RecipeProcessingComponent<LatheRecipe> processingComponent;
	public final FluidTankComponent fluidTankComponent;

	public BlockEntityLathe(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);

		// Get the tier object.
		StaticPowerTier tierObject = StaticPowerConfig.getTier(getTier());

		registerComponent(inputInventory = new InventoryComponent("InputInventory", 9, MachineSideMode.Input).setShiftClickEnabled(true).setSlotsLockable(true));

		// Setup all the other inventories.
		registerComponent(mainOutputInventory = new InventoryComponent("MainOutputInventory", 1, MachineSideMode.Output2));
		registerComponent(secondaryOutputInventory = new InventoryComponent("SecondaryOutputInventory", 1, MachineSideMode.Output3));
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", powerStorage));
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));

		// Setup the processing component to work with the redstone control component,
		// upgrade component and energy component.
		registerComponent(processingComponent = new RecipeProcessingComponent<LatheRecipe>("ProcessingComponent", LatheRecipe.RECIPE_TYPE, this));

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
		registerComponent(new FluidOutputServoComponent("FluidOutputServoComponent", 100, fluidTankComponent, MachineSideMode.Output));

		// Register components to allow the lumbermill to fill buckets in the GUI.
		registerComponent(
				fluidContainerComponent = new FluidContainerInventoryComponent("FluidFillContainerServo", fluidTankComponent).setMode(FluidContainerInteractionMode.FILL));

		// Set the energy storage upgrade inventory.
		powerStorage.setUpgradeInventory(upgradesInventory);
	}

	@Override
	public RecipeMatchParameters getRecipeMatchParameters(RecipeProcessingComponent<LatheRecipe> component) {
		return new RecipeMatchParameters(inputInventory.getStackInSlot(0), inputInventory.getStackInSlot(1), inputInventory.getStackInSlot(2), inputInventory.getStackInSlot(3),
				inputInventory.getStackInSlot(4), inputInventory.getStackInSlot(5), inputInventory.getStackInSlot(6), inputInventory.getStackInSlot(7),
				inputInventory.getStackInSlot(8));
	}

	@Override
	public void captureInputsAndProducts(RecipeProcessingComponent<LatheRecipe> component, LatheRecipe recipe, ProcessingOutputContainer outputContainer) {
		// Move the items.
		for (int i = 0; i < 9; i++) {
			outputContainer.addInputItem(inputInventory.extractItem(i, recipe.getInputs().get(i).getCount(), true), CaptureType.BOTH);
		}

		outputContainer.addOutputItem(recipe.getPrimaryOutput().calculateOutput(), CaptureType.BOTH);
		outputContainer.addOutputItem(recipe.getSecondaryOutput().calculateOutput(), CaptureType.BOTH);
		outputContainer.addOutputFluid(recipe.getOutputFluid(), CaptureType.BOTH);

		// Set the power usage.
		component.setProcessingPowerUsage(recipe.getPowerCost());
		component.setMaxProcessingTime(recipe.getProcessingTime());
	}

	@Override
	public void processingStarted(RecipeProcessingComponent<LatheRecipe> component, LatheRecipe recipe, ProcessingOutputContainer outputContainer) {
		// Move the items.
		for (int i = 0; i < 9; i++) {
			inputInventory.extractItem(i, recipe.getInputs().get(i).getCount(), false);
		}
	}

	@Override
	public ProcessingCheckState canStartProcessing(RecipeProcessingComponent<LatheRecipe> component, LatheRecipe recipe, ProcessingOutputContainer outputContainer) {
		if (!InventoryUtilities.canFullyInsertStackIntoSlot(mainOutputInventory, 0, outputContainer.getOutputItem(0).item())) {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}
		if (outputContainer.getOutputItems().size() > 1) {
			if (!InventoryUtilities.canFullyInsertStackIntoSlot(secondaryOutputInventory, 0, outputContainer.getOutputItem(1).item())) {
				return ProcessingCheckState.outputsCannotTakeRecipe();
			}
		}

		if (outputContainer.hasOutputFluids()) {
			if (fluidTankComponent.fill(outputContainer.getOutputFluid(0).fluid(), FluidAction.SIMULATE) != outputContainer.getOutputFluid(0).fluid().getAmount()) {
				return ProcessingCheckState.fluidOutputFull();
			}
		}
		return ProcessingCheckState.ok();
	}

	@Override
	public void processingCompleted(RecipeProcessingComponent<LatheRecipe> component, LatheRecipe recipe, ProcessingOutputContainer outputContainer) {
		mainOutputInventory.insertItem(0, outputContainer.getOutputItem(0).item().copy(), false);
		if (outputContainer.getOutputItems().size() > 1) {
			secondaryOutputInventory.insertItem(0, outputContainer.getOutputItem(1).item().copy(), false);
		}
		fluidTankComponent.fill(outputContainer.getOutputFluid(0).fluid(), FluidAction.EXECUTE);
	}

	@Override
	protected SideConfigurationPreset getDefaultSideConfiguration() {
		return LatheSideConfiguration.INSTANCE;
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerLathe(windowId, inventory, this);
	}
}
