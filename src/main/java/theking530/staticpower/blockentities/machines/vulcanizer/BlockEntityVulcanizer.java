package theking530.staticpower.blockentities.machines.vulcanizer;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blockentities.BlockEntityMachine;
import theking530.staticpower.blockentities.components.control.RecipeProcessingComponent;
import theking530.staticpower.blockentities.components.control.AbstractProcesingComponent.ProcessingCheckState;
import theking530.staticpower.blockentities.components.control.RecipeProcessingComponent.RecipeProcessingPhase;
import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.blockentities.components.fluids.FluidInputServoComponent;
import theking530.staticpower.blockentities.components.fluids.FluidTankComponent;
import theking530.staticpower.blockentities.components.items.BatteryInventoryComponent;
import theking530.staticpower.blockentities.components.items.FluidContainerInventoryComponent;
import theking530.staticpower.blockentities.components.items.InventoryComponent;
import theking530.staticpower.blockentities.components.items.OutputServoComponent;
import theking530.staticpower.blockentities.components.items.UpgradeInventoryComponent;
import theking530.staticpower.blockentities.components.items.FluidContainerInventoryComponent.FluidContainerInteractionMode;
import theking530.staticpower.blockentities.components.serialization.UpdateSerialize;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.wrappers.vulcanizer.VulcanizerRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.utilities.InventoryUtilities;

public class BlockEntityVulcanizer extends BlockEntityMachine {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityVulcanizer> TYPE = new BlockEntityTypeAllocator<BlockEntityVulcanizer>(
			(type, pos, state) -> new BlockEntityVulcanizer(pos, state), ModBlocks.Vulcanizer);

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
		StaticPowerTier tierObject = getTierObject();

		// Setup the inventories.
		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 1, MachineSideMode.Output));
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", powerStorage));
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));

		// Setup the processing component.
		registerComponent(processingComponent = new RecipeProcessingComponent<VulcanizerRecipe>("ProcessingComponent", StaticPowerConfig.SERVER.vulcanizerProcessingTime.get(),
				VulcanizerRecipe.RECIPE_TYPE, this::getMatchParameters, this::canProcessRecipe, this::moveInputs, this::processingCompleted));

		// Initialize the processing component to work with the redstone control
		// component, upgrade component and energy component.
		processingComponent.setShouldControlBlockState(true);
		processingComponent.setUpgradeInventory(upgradesInventory);
		processingComponent.setPowerComponent(powerStorage);
		processingComponent.setRedstoneControlComponent(redstoneControlComponent);

		// Setup the I/O servos.
		registerComponent(new OutputServoComponent("OutputServo", 4, outputInventory, 0));

		// Setup the fluid tanks and servo.
		registerComponent(fluidTankComponent = new FluidTankComponent("FluidTank", tierObject.defaultTankCapacity.get(), (fluidStack) -> {
			return processingComponent.getRecipeMatchingParameters(new RecipeMatchParameters(fluidStack)).isPresent();
		}));

		fluidTankComponent.setCapabilityExposedModes(MachineSideMode.Input);
		fluidTankComponent.setUpgradeInventory(upgradesInventory);
		fluidTankComponent.setCanDrain(false);

		registerComponent(new FluidInputServoComponent("FluidInputServoComponent", 100, fluidTankComponent, MachineSideMode.Input));

		// Create the fluid container component.
		registerComponent(fluidContainerComponent = new FluidContainerInventoryComponent("FluidContainerServo", fluidTankComponent).setMode(FluidContainerInteractionMode.DRAIN));

		// Set the energy storage upgrade inventory.
		powerStorage.setUpgradeInventory(upgradesInventory);

		// Initialize the current processing stack.
		currentProcessingFluidStack = FluidStack.EMPTY;
	}

	protected RecipeMatchParameters getMatchParameters(RecipeProcessingPhase location) {
		if (location == RecipeProcessingPhase.PROCESSING) {
			return new RecipeMatchParameters(currentProcessingFluidStack);
		} else {
			return new RecipeMatchParameters(fluidTankComponent.getFluid());
		}
	}

	protected ProcessingCheckState moveInputs(VulcanizerRecipe recipe) {
		// If the items can be insert into the output, transfer the items and return
		// true.
		if (!InventoryUtilities.canFullyInsertAllItemsIntoInventory(outputInventory, recipe.getRawOutputItem())) {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}

		// Capture the processing fluidstack.
		currentProcessingFluidStack = this.fluidTankComponent.drain(recipe.getInputFluid(), FluidAction.EXECUTE);

		// Set the power usage.
		processingComponent.setProcessingPowerUsage(recipe.getPowerCost());
		processingComponent.setMaxProcessingTime(recipe.getProcessingTime());

		return ProcessingCheckState.ok();
	}

	protected ProcessingCheckState canProcessRecipe(VulcanizerRecipe recipe, RecipeProcessingPhase location) {
		if (!InventoryUtilities.canFullyInsertItemIntoInventory(outputInventory, recipe.getRawOutputItem())) {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}
		return ProcessingCheckState.ok();
	}

	protected ProcessingCheckState processingCompleted(VulcanizerRecipe recipe) {
		// Output the item if the dice roll passes.
		ItemStack outputItem = recipe.getOutput().calculateOutput();
		outputInventory.insertItem(0, outputItem, false);

		// Clear the processing stack.
		currentProcessingFluidStack = FluidStack.EMPTY;
		return ProcessingCheckState.ok();
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerVulcanizer(windowId, inventory, this);
	}
}
