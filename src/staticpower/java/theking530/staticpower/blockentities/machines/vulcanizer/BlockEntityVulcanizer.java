package theking530.staticpower.blockentities.machines.vulcanizer;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import theking530.staticcore.blockentity.components.control.oldprocessing.OldProcessingContainer;
import theking530.staticcore.blockentity.components.control.oldprocessing.OldProcessingContainer.CaptureType;
import theking530.staticcore.blockentity.components.control.oldprocessing.OldRecipeProcessingComponent;
import theking530.staticcore.blockentity.components.control.oldprocessing.interfaces.IOldRecipeProcessor;
import theking530.staticcore.blockentity.components.control.processing.ProcessingCheckState;
import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.blockentity.components.fluids.FluidInputServoComponent;
import theking530.staticcore.blockentity.components.fluids.FluidTankComponent;
import theking530.staticcore.blockentity.components.items.BatteryInventoryComponent;
import theking530.staticcore.blockentity.components.items.FluidContainerInventoryComponent;
import theking530.staticcore.blockentity.components.items.FluidContainerInventoryComponent.FluidContainerInteractionMode;
import theking530.staticcore.blockentity.components.items.InputServoComponent;
import theking530.staticcore.blockentity.components.items.InventoryComponent;
import theking530.staticcore.blockentity.components.items.OutputServoComponent;
import theking530.staticcore.blockentity.components.items.UpgradeInventoryComponent;
import theking530.staticcore.blockentity.components.serialization.UpdateSerialize;
import theking530.staticcore.crafting.RecipeMatchParameters;
import theking530.staticcore.data.StaticCoreTier;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticcore.utilities.item.InventoryUtilities;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blockentities.BlockEntityMachine;
import theking530.staticpower.data.crafting.wrappers.vulcanizer.VulcanizerRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModRecipeTypes;

public class BlockEntityVulcanizer extends BlockEntityMachine implements IOldRecipeProcessor<VulcanizerRecipe> {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityVulcanizer> TYPE = new BlockEntityTypeAllocator<BlockEntityVulcanizer>("vulcanizer",
			(type, pos, state) -> new BlockEntityVulcanizer(pos, state), ModBlocks.Vulcanizer);

	public final InventoryComponent inputInventory;
	public final InventoryComponent outputInventory;
	public final InventoryComponent batteryInventory;
	public final UpgradeInventoryComponent upgradesInventory;
	public final FluidContainerInventoryComponent fluidContainerComponent;
	public final OldRecipeProcessingComponent<VulcanizerRecipe> processingComponent;
	public final FluidTankComponent fluidTankComponent;

	@UpdateSerialize
	private FluidStack currentProcessingFluidStack;

	public BlockEntityVulcanizer(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);

		// Get the tier object.
		StaticCoreTier tierObject = getTierObject();

		// Setup the inventories.
		registerComponent(inputInventory = new InventoryComponent("InputInventory", 1, MachineSideMode.Input));
		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 1, MachineSideMode.Output));
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", powerStorage));
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));

		// Setup the processing component.
		registerComponent(processingComponent = new OldRecipeProcessingComponent<VulcanizerRecipe>("ProcessingComponent", StaticPowerConfig.SERVER.vulcanizerProcessingTime.get(),
				ModRecipeTypes.VULCANIZER_RECIPE_TYPE.get(), this));
		processingComponent.setShouldControlBlockState(true);
		processingComponent.setUpgradeInventory(upgradesInventory);
		processingComponent.setPowerComponent(powerStorage);
		processingComponent.setRedstoneControlComponent(redstoneControlComponent);

		// Setup the I/O servos.
		registerComponent(new InputServoComponent("InputServo", 4, inputInventory, 0));
		registerComponent(new OutputServoComponent("OutputServo", 4, outputInventory, 0));

		// Setup the fluid tanks and servo.
		registerComponent(fluidTankComponent = new FluidTankComponent("FluidTank", tierObject.defaultTankCapacity.get(), (fluidStack) -> {
			return processingComponent.getRecipeMatchingParameters(new RecipeMatchParameters(fluidStack)).isPresent();
		}));

		fluidTankComponent.setCapabilityExposedModes(MachineSideMode.Input);
		fluidTankComponent.setUpgradeInventory(upgradesInventory);

		registerComponent(new FluidInputServoComponent("FluidInputServoComponent", 100, fluidTankComponent, MachineSideMode.Input));

		// Create the fluid container component.
		registerComponent(fluidContainerComponent = new FluidContainerInventoryComponent("FluidContainerServo", fluidTankComponent).setMode(FluidContainerInteractionMode.DRAIN));

		// Set the energy storage upgrade inventory.
		powerStorage.setUpgradeInventory(upgradesInventory);

		// Initialize the current processing stack.
		currentProcessingFluidStack = FluidStack.EMPTY;
	}

	@Override
	public RecipeMatchParameters getRecipeMatchParameters(OldRecipeProcessingComponent<VulcanizerRecipe> component) {
		return new RecipeMatchParameters(fluidTankComponent.getFluid()).setItems(inputInventory.getStackInSlot(0));
	}

	@Override
	public ProcessingCheckState canStartProcessing(OldRecipeProcessingComponent<VulcanizerRecipe> component, VulcanizerRecipe recipe, OldProcessingContainer outputContainer) {
		if (!InventoryUtilities.canFullyInsertAllItemsIntoInventory(outputInventory, outputContainer.getOutputItems().get(0).item())) {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}
		return ProcessingCheckState.ok();
	}

	@Override
	public void captureInputsAndProducts(OldRecipeProcessingComponent<VulcanizerRecipe> component, VulcanizerRecipe recipe, OldProcessingContainer outputContainer) {
		component.setProcessingPowerUsage(recipe.getPowerCost());
		component.setMaxProcessingTime(recipe.getProcessingTime());
		if (recipe.hasInputItem()) {
			outputContainer.addInputItem(inputInventory.extractItem(0, recipe.getInputItem().getCount(), false), CaptureType.BOTH);
		}
		outputContainer.addInputFluid(fluidTankComponent.getFluid(), recipe.getInputFluid().getAmount(), CaptureType.BOTH);
		outputContainer.addOutputItem(recipe.getOutput().calculateOutput(), CaptureType.BOTH);
	}

	@Override
	public void processingCompleted(OldRecipeProcessingComponent<VulcanizerRecipe> component, VulcanizerRecipe recipe, OldProcessingContainer outputContainer) {
		fluidTankComponent.drain(outputContainer.getInputFluids().get(0).fluid(), FluidAction.EXECUTE);
		outputInventory.insertItem(0, outputContainer.getOutputItems().get(0).item().copy(), false);
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerVulcanizer(windowId, inventory, this);
	}
}
