package theking530.staticpower.blockentities.machines.carpenter;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import theking530.staticcore.StaticCoreConfig;
import theking530.staticcore.blockentity.components.control.processing.ConcretizedProductContainer;
import theking530.staticcore.blockentity.components.control.processing.ProcessingCheckState;
import theking530.staticcore.blockentity.components.control.processing.ProcessingContainer;
import theking530.staticcore.blockentity.components.control.processing.ProcessingContainer.CaptureType;
import theking530.staticcore.blockentity.components.control.processing.recipe.IRecipeProcessor;
import theking530.staticcore.blockentity.components.control.processing.recipe.RecipeProcessingComponent;
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

public class BlockEntityCarpenter extends BlockEntityMachine implements IRecipeProcessor<CarpenterRecipe> {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityCarpenter> TYPE = new BlockEntityTypeAllocator<>(
			"carpenter", (type, pos, state) -> new BlockEntityCarpenter(pos, state), ModBlocks.Carpenter);

	public final InventoryComponent inputInventory;
	public final InventoryComponent mainOutputInventory;
	public final InventoryComponent secondaryOutputInventory;
	public final FluidContainerInventoryComponent fluidContainerComponent;
	public final BatteryInventoryComponent batteryInventory;
	public final UpgradeInventoryComponent upgradesInventory;

	public final RecipeProcessingComponent<CarpenterRecipe> processingComponent;
	public final FluidTankComponent fluidTankComponent;

	public BlockEntityCarpenter(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);

		// Get the tier object.
		StaticCoreTier tierObject = StaticCoreConfig.getTier(getTier());

		registerComponent(inputInventory = new InventoryComponent("InputInventory", 9, MachineSideMode.Input)
				.setShiftClickEnabled(true).setSlotsLockable(true));

		// Setup all the other inventories.
		registerComponent(
				mainOutputInventory = new InventoryComponent("MainOutputInventory", 1, MachineSideMode.Output2));
		registerComponent(secondaryOutputInventory = new InventoryComponent("SecondaryOutputInventory", 1,
				MachineSideMode.Output3));
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", powerStorage));
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));

		// Setup the processing component to work with the redstone control component,
		// upgrade component and energy component.
		registerComponent(processingComponent = new RecipeProcessingComponent<CarpenterRecipe>("ProcessingComponent", 0,
				ModRecipeTypes.LATHE_RECIPE_TYPE.get()));

		// Initialize the processing component to work with the redstone control
		// component, upgrade component and energy component.
		processingComponent.setShouldControlOnBlockState(true);
		processingComponent.setUpgradeInventory(upgradesInventory);
		processingComponent.setPowerComponent(powerStorage);
		processingComponent.setRedstoneControlComponent(redstoneControlComponent);

		// Setup the I/O servos.
		registerComponent(new InputServoComponent("InputServo", inputInventory).setRoundRobin(true));
		registerComponent(new OutputServoComponent("OutputServo", mainOutputInventory));
		registerComponent(new OutputServoComponent("SecondaryOutputServo", secondaryOutputInventory));

		// Setup the fluid tank and fluid servo.
		registerComponent(fluidTankComponent = new FluidTankComponent("FluidTank", tierObject.defaultTankCapacity.get())
				.setCapabilityExposedModes(MachineSideMode.Output).setUpgradeInventory(upgradesInventory));
		registerComponent(new FluidOutputServoComponent("FluidOutputServoComponent", 100, fluidTankComponent,
				MachineSideMode.Output));

		// Register components to allow the lumbermill to fill buckets in the GUI.
		registerComponent(fluidContainerComponent = new FluidContainerInventoryComponent("FluidFillContainerServo",
				fluidTankComponent).setMode(FluidContainerInteractionMode.FILL));

		// Set the energy storage upgrade inventory.
		powerStorage.setUpgradeInventory(upgradesInventory);
	}

	@Override
	public RecipeMatchParameters getRecipeMatchParameters(RecipeProcessingComponent<CarpenterRecipe> component) {
		return new RecipeMatchParameters(getTeamComponent().getOwningTeamId(), inputInventory.getStackInSlot(0),
				inputInventory.getStackInSlot(1), inputInventory.getStackInSlot(2), inputInventory.getStackInSlot(3),
				inputInventory.getStackInSlot(4), inputInventory.getStackInSlot(5), inputInventory.getStackInSlot(6),
				inputInventory.getStackInSlot(7), inputInventory.getStackInSlot(8));
	}

	@Override
	protected SideConfigurationPreset getDefaultSideConfiguration() {
		return CarpenterSideConfiguration.INSTANCE;
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerLathe(windowId, inventory, this);
	}

	@Override
	public void captureOutputs(RecipeProcessingComponent<CarpenterRecipe> component, CarpenterRecipe recipe,
			ConcretizedProductContainer outputContainer) {
		outputContainer.addItem(recipe.getPrimaryOutput().calculateOutput(), CaptureType.BOTH);
		outputContainer.addItem(recipe.getSecondaryOutput().calculateOutput(), CaptureType.BOTH);
		outputContainer.addFluid(recipe.getOutputFluid(), CaptureType.BOTH);
	}

	@Override
	public ProcessingCheckState canStartProcessingRecipe(RecipeProcessingComponent<CarpenterRecipe> component,
			CarpenterRecipe recipe, ConcretizedProductContainer outputContainer) {
		if (!InventoryUtilities.canFullyInsertStackIntoSlot(mainOutputInventory, 0, outputContainer.getItem(0))) {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}
		if (outputContainer.getItems().size() > 1) {
			if (!InventoryUtilities.canFullyInsertStackIntoSlot(secondaryOutputInventory, 0,
					outputContainer.getItem(1))) {
				return ProcessingCheckState.outputsCannotTakeRecipe();
			}
		}

		if (outputContainer.hasFluids()) {
			if (fluidTankComponent.fill(outputContainer.getFluid(0), FluidAction.SIMULATE) != outputContainer
					.getFluid(0).getAmount()) {
				return ProcessingCheckState.fluidOutputFull();
			}
		}
		return ProcessingCheckState.ok();
	}

	@Override
	public void captureInputs(RecipeProcessingComponent<CarpenterRecipe> component, CarpenterRecipe recipe,
			ProcessingContainer processingContainer, ConcretizedProductContainer inputContainer) {
		for (int i = 0; i < 9; i++) {
			inputContainer.addItem(inputInventory.extractItem(i, recipe.getInputs().get(i).getCount(), true),
					CaptureType.BOTH);
		}
	}

	@Override
	public void onProcessingCompleted(RecipeProcessingComponent<CarpenterRecipe> component,
			ProcessingContainer processingContainer) {
		mainOutputInventory.insertItem(0, processingContainer.getOutputs().getItem(0).copy(), false);
		if (processingContainer.getOutputs().getItems().size() > 1) {
			secondaryOutputInventory.insertItem(0, processingContainer.getOutputs().getItem(1).copy(), false);
		}
		fluidTankComponent.fill(processingContainer.getOutputs().getFluid(0), FluidAction.EXECUTE);
	}
}
