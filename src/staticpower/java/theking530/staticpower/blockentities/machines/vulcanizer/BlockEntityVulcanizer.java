package theking530.staticpower.blockentities.machines.vulcanizer;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.blockentity.components.control.processing.ConcretizedProductContainer;
import theking530.staticcore.blockentity.components.control.processing.ProcessingCheckState;
import theking530.staticcore.blockentity.components.control.processing.ProcessingContainer;
import theking530.staticcore.blockentity.components.control.processing.ProcessingContainer.CaptureType;
import theking530.staticcore.blockentity.components.control.processing.recipe.IRecipeProcessor;
import theking530.staticcore.blockentity.components.control.processing.recipe.RecipeProcessingComponent;
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

public class BlockEntityVulcanizer extends BlockEntityMachine implements IRecipeProcessor<VulcanizerRecipe> {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityVulcanizer> TYPE = new BlockEntityTypeAllocator<BlockEntityVulcanizer>(
			"vulcanizer", (type, pos, state) -> new BlockEntityVulcanizer(pos, state), ModBlocks.Vulcanizer);

	public final InventoryComponent inputInventory;
	public final InventoryComponent outputInventory;
	public final InventoryComponent batteryInventory;
	public final UpgradeInventoryComponent upgradesInventory;
	public final FluidContainerInventoryComponent fluidContainerComponent;
	public final RecipeProcessingComponent<VulcanizerRecipe> processingComponent;
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
		registerComponent(processingComponent = new RecipeProcessingComponent<VulcanizerRecipe>("ProcessingComponent",
				StaticPowerConfig.SERVER.vulcanizerProcessingTime.get(), ModRecipeTypes.VULCANIZER_RECIPE_TYPE.get()));
		processingComponent.setShouldControlOnBlockState(true);
		processingComponent.setUpgradeInventory(upgradesInventory);
		processingComponent.setPowerComponent(powerStorage);
		processingComponent.setRedstoneControlComponent(redstoneControlComponent);

		// Setup the I/O servos.
		registerComponent(new InputServoComponent("InputServo", 4, inputInventory, 0));
		registerComponent(new OutputServoComponent("OutputServo", 4, outputInventory, 0));

		// Setup the fluid tanks and servo.
		registerComponent(fluidTankComponent = new FluidTankComponent("FluidTank", tierObject.defaultTankCapacity.get(),
				(fluidStack) -> {
					return processingComponent
							.getRecipe(new RecipeMatchParameters(getTeamComponent().getOwningTeamId(), fluidStack))
							.isPresent();
				}));

		fluidTankComponent.setCapabilityExposedModes(MachineSideMode.Input);
		fluidTankComponent.setUpgradeInventory(upgradesInventory);

		registerComponent(new FluidInputServoComponent("FluidInputServoComponent", 100, fluidTankComponent,
				MachineSideMode.Input));

		// Create the fluid container component.
		registerComponent(fluidContainerComponent = new FluidContainerInventoryComponent("FluidContainerServo",
				fluidTankComponent).setMode(FluidContainerInteractionMode.DRAIN));

		// Set the energy storage upgrade inventory.
		powerStorage.setUpgradeInventory(upgradesInventory);

		// Initialize the current processing stack.
		currentProcessingFluidStack = FluidStack.EMPTY;
	}

	@Override
	public RecipeMatchParameters getRecipeMatchParameters(RecipeProcessingComponent<VulcanizerRecipe> component) {
		return new RecipeMatchParameters(getTeamComponent().getOwningTeamId(), fluidTankComponent.getFluid())
				.setItems(inputInventory.getStackInSlot(0));
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerVulcanizer(windowId, inventory, this);
	}

	@Override
	public void captureOutputs(RecipeProcessingComponent<VulcanizerRecipe> component, VulcanizerRecipe recipe,
			ConcretizedProductContainer outputContainer) {
		outputContainer.addItem(recipe.getOutput().calculateOutput(), CaptureType.BOTH);

	}

	@Override
	public void captureInputs(RecipeProcessingComponent<VulcanizerRecipe> component, VulcanizerRecipe recipe,
			ProcessingContainer processingContainer, ConcretizedProductContainer inputContainer) {
		if (recipe.hasInputItem()) {
			inputContainer.addItem(inputInventory.extractItem(0, recipe.getInputItem().getCount(), false),
					CaptureType.BOTH);
		}
		inputContainer.addFluid(fluidTankComponent.getFluid(), recipe.getInputFluid().getAmount(), CaptureType.BOTH);
	}

	@Override
	public ProcessingCheckState canStartProcessingRecipe(RecipeProcessingComponent<VulcanizerRecipe> component,
			VulcanizerRecipe recipe, ConcretizedProductContainer outputContainer) {
		if (!InventoryUtilities.canFullyInsertAllItemsIntoInventory(outputInventory, outputContainer.getItem(0))) {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}
		return ProcessingCheckState.ok();
	}

	@Override
	public void onProcessingCompleted(RecipeProcessingComponent<VulcanizerRecipe> component,
			ProcessingContainer processingContainer) {
		outputInventory.insertItem(0, processingContainer.getOutputs().getItem(0).copy(), false);
	}

}
