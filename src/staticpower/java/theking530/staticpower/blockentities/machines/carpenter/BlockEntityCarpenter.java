package theking530.staticpower.blockentities.machines.carpenter;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import theking530.staticcore.StaticCoreConfig;
import theking530.staticcore.blockentity.components.control.oldprocessing.OldProcessingContainer;
import theking530.staticcore.blockentity.components.control.oldprocessing.OldProcessingContainer.CaptureType;
import theking530.staticcore.blockentity.components.control.oldprocessing.OldRecipeProcessingComponent;
import theking530.staticcore.blockentity.components.control.oldprocessing.interfaces.IOldRecipeProcessor;
import theking530.staticcore.blockentity.components.control.processing.ProcessingCheckState;
import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationPreset;
import theking530.staticcore.blockentity.components.fluids.FluidOutputServoComponent;
import theking530.staticcore.blockentity.components.fluids.FluidTankComponent;
import theking530.staticcore.blockentity.components.items.BatteryInventoryComponent;
import theking530.staticcore.blockentity.components.items.FluidContainerInventoryComponent;
import theking530.staticcore.blockentity.components.items.FluidContainerInventoryComponent.FluidContainerInteractionMode;
import theking530.staticcore.blockentity.components.items.InputServoComponent;
import theking530.staticcore.blockentity.components.items.InventoryComponent;
import theking530.staticcore.blockentity.components.items.OutputServoComponent;
import theking530.staticcore.blockentity.components.items.UpgradeInventoryComponent;
import theking530.staticcore.crafting.RecipeMatchParameters;
import theking530.staticcore.data.StaticCoreTier;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticcore.utilities.item.InventoryUtilities;
import theking530.staticpower.blockentities.BlockEntityMachine;
import theking530.staticpower.data.crafting.wrappers.carpenter.CarpenterRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModRecipeTypes;

public class BlockEntityCarpenter extends BlockEntityMachine implements IOldRecipeProcessor<CarpenterRecipe> {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityCarpenter> TYPE = new BlockEntityTypeAllocator<>("carpenter", (type, pos, state) -> new BlockEntityCarpenter(pos, state),
			ModBlocks.Carpenter);

	public final InventoryComponent inputInventory;
	public final InventoryComponent mainOutputInventory;
	public final InventoryComponent secondaryOutputInventory;
	public final FluidContainerInventoryComponent fluidContainerComponent;
	public final BatteryInventoryComponent batteryInventory;
	public final UpgradeInventoryComponent upgradesInventory;

	public final OldRecipeProcessingComponent<CarpenterRecipe> processingComponent;
	public final FluidTankComponent fluidTankComponent;

	public BlockEntityCarpenter(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);

		// Get the tier object.
		StaticCoreTier tierObject = StaticCoreConfig.getTier(getTier());

		registerComponent(inputInventory = new InventoryComponent("InputInventory", 9, MachineSideMode.Input).setShiftClickEnabled(true).setSlotsLockable(true));

		// Setup all the other inventories.
		registerComponent(mainOutputInventory = new InventoryComponent("MainOutputInventory", 1, MachineSideMode.Output2));
		registerComponent(secondaryOutputInventory = new InventoryComponent("SecondaryOutputInventory", 1, MachineSideMode.Output3));
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", powerStorage));
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));

		// Setup the processing component to work with the redstone control component,
		// upgrade component and energy component.
		registerComponent(processingComponent = new OldRecipeProcessingComponent<CarpenterRecipe>("ProcessingComponent", ModRecipeTypes.LATHE_RECIPE_TYPE.get(), this));

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
	public RecipeMatchParameters getRecipeMatchParameters(OldRecipeProcessingComponent<CarpenterRecipe> component) {
		return new RecipeMatchParameters(inputInventory.getStackInSlot(0), inputInventory.getStackInSlot(1), inputInventory.getStackInSlot(2), inputInventory.getStackInSlot(3),
				inputInventory.getStackInSlot(4), inputInventory.getStackInSlot(5), inputInventory.getStackInSlot(6), inputInventory.getStackInSlot(7),
				inputInventory.getStackInSlot(8));
	}

	@Override
	public void captureInputsAndProducts(OldRecipeProcessingComponent<CarpenterRecipe> component, CarpenterRecipe recipe, OldProcessingContainer outputContainer) {
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
	public void processingStarted(OldRecipeProcessingComponent<CarpenterRecipe> component, CarpenterRecipe recipe, OldProcessingContainer outputContainer) {
		// Move the items.
		for (int i = 0; i < 9; i++) {
			inputInventory.extractItem(i, recipe.getInputs().get(i).getCount(), false);
		}
	}

	@Override
	public ProcessingCheckState canStartProcessing(OldRecipeProcessingComponent<CarpenterRecipe> component, CarpenterRecipe recipe, OldProcessingContainer outputContainer) {
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
	public void processingCompleted(OldRecipeProcessingComponent<CarpenterRecipe> component, CarpenterRecipe recipe, OldProcessingContainer outputContainer) {
		mainOutputInventory.insertItem(0, outputContainer.getOutputItem(0).item().copy(), false);
		if (outputContainer.getOutputItems().size() > 1) {
			secondaryOutputInventory.insertItem(0, outputContainer.getOutputItem(1).item().copy(), false);
		}
		fluidTankComponent.fill(outputContainer.getOutputFluid(0).fluid(), FluidAction.EXECUTE);
	}

	@Override
	protected SideConfigurationPreset getDefaultSideConfiguration() {
		return CarpenterSideConfiguration.INSTANCE;
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerLathe(windowId, inventory, this);
	}
}
