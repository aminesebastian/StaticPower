package theking530.staticpower.blockentities.machines.fluidinfuser;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fml.loading.FMLEnvironment;
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
import theking530.staticpower.blockentities.components.items.InputServoComponent;
import theking530.staticpower.blockentities.components.items.InventoryComponent;
import theking530.staticpower.blockentities.components.items.OutputServoComponent;
import theking530.staticpower.blockentities.components.items.UpgradeInventoryComponent;
import theking530.staticpower.blockentities.components.items.FluidContainerInventoryComponent.FluidContainerInteractionMode;
import theking530.staticpower.client.rendering.blockentity.BlockEntityRenderFluidInfuser;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.wrappers.fluidinfusion.FluidInfusionRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.utilities.InventoryUtilities;

public class BlockEntityFluidInfuser extends BlockEntityMachine {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityFluidInfuser> TYPE = new BlockEntityTypeAllocator<BlockEntityFluidInfuser>((type, pos, state) -> new BlockEntityFluidInfuser(pos, state),
			ModBlocks.FluidInfuser);

	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setTileEntitySpecialRenderer(BlockEntityRenderFluidInfuser::new);
		}
	}

	public final InventoryComponent inputInventory;
	public final InventoryComponent internalInventory;
	public final InventoryComponent outputInventory;
	public final InventoryComponent batteryInventory;
	public final FluidContainerInventoryComponent fluidContainerComponent;
	public final UpgradeInventoryComponent upgradesInventory;
	public final RecipeProcessingComponent<FluidInfusionRecipe> processingComponent;
	public final FluidTankComponent fluidTankComponent;

	public BlockEntityFluidInfuser(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);

		// Get the tier object.
		StaticPowerTier tier = getTierObject();

		// Setup the inventories.
		registerComponent(inputInventory = new InventoryComponent("InputInventory", 1, MachineSideMode.Input).setShiftClickEnabled(true));
		registerComponent(internalInventory = new InventoryComponent("InternalInventory", 1));
		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 1, MachineSideMode.Output));
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", powerStorage));
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));

		// Setup the processing component.
		registerComponent(processingComponent = new RecipeProcessingComponent<FluidInfusionRecipe>("ProcessingComponent", StaticPowerConfig.SERVER.fluidInfuserProcessingTime.get(),
				RecipeProcessingComponent.MOVE_TIME, FluidInfusionRecipe.RECIPE_TYPE, this::getMatchParameters, this::canProcessRecipe, this::moveInputs, this::processingCompleted));

		// Initialize the processing component to work with the redstone control
		// component, upgrade component and energy component.
		processingComponent.setShouldControlBlockState(true);
		processingComponent.setUpgradeInventory(upgradesInventory);
		processingComponent.setPowerComponent(powerStorage);
		processingComponent.setRedstoneControlComponent(redstoneControlComponent);

		// Setup the I/O servos.
		registerComponent(new InputServoComponent("InputServo", 4, inputInventory, 0));
		registerComponent(new OutputServoComponent("OutputServo", 4, outputInventory, 0));

		// Setup the fluid tanks and servo.
		registerComponent(fluidTankComponent = new FluidTankComponent("FluidTank", tier.defaultTankCapacity.get()).setCapabilityExposedModes(MachineSideMode.Input)
				.setUpgradeInventory(upgradesInventory));
		fluidTankComponent.setCanDrain(false);
		fluidTankComponent.setAutoSyncPacketsEnabled(true);

		registerComponent(new FluidInputServoComponent("FluidInputServoComponent", 100, fluidTankComponent, MachineSideMode.Input));

		// Create the fluid container component.
		registerComponent(fluidContainerComponent = new FluidContainerInventoryComponent("FluidContainerServo", fluidTankComponent).setMode(FluidContainerInteractionMode.DRAIN));

		// Set the energy storage upgrade inventory.
		powerStorage.setUpgradeInventory(upgradesInventory);
	}

	protected RecipeMatchParameters getMatchParameters(RecipeProcessingPhase location) {
		if (location == RecipeProcessingPhase.PROCESSING) {
			return new RecipeMatchParameters(internalInventory.getStackInSlot(0)).setFluids(fluidTankComponent.getFluid());
		} else {
			return new RecipeMatchParameters(inputInventory.getStackInSlot(0)).setFluids(fluidTankComponent.getFluid());
		}
	}

	protected ProcessingCheckState moveInputs(FluidInfusionRecipe recipe) {
		// If the items can be insert into the output, transfer the items and return
		// true.
		if (!InventoryUtilities.canFullyInsertAllItemsIntoInventory(outputInventory, recipe.getOutput().getItem())) {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}

		transferItemInternally(recipe.getInput().getCount(), inputInventory, 0, internalInventory, 0);

		// Set the power usage.
		processingComponent.setProcessingPowerUsage(recipe.getPowerCost());
		processingComponent.setMaxProcessingTime(recipe.getProcessingTime());

		return ProcessingCheckState.ok();
	}

	protected ProcessingCheckState canProcessRecipe(FluidInfusionRecipe recipe, RecipeProcessingPhase location) {
		if (!InventoryUtilities.canFullyInsertItemIntoInventory(outputInventory, recipe.getOutput().getItem())) {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}
		return ProcessingCheckState.ok();
	}

	protected ProcessingCheckState processingCompleted(FluidInfusionRecipe recipe) {
		// Output the item if the dice roll passes.
		ItemStack output = recipe.getOutput().calculateOutput();
		outputInventory.insertItem(0, output, false);

		// Drain the fluid.
		fluidTankComponent.drain(recipe.getRequiredFluid().getAmount(), FluidAction.EXECUTE);

		// Clear the internal inventory.
		InventoryUtilities.clearInventory(internalInventory);
		return ProcessingCheckState.ok();
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerFluidInfuser(windowId, inventory, this);
	}
}
