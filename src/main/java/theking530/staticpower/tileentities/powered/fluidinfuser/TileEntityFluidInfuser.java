package theking530.staticpower.tileentities.powered.fluidinfuser;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.tileentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.client.rendering.tileentity.TileEntityRenderFluidInfuser;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.wrappers.fluidinfusion.FluidInfusionRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.tileentities.TileEntityMachine;
import theking530.staticpower.tileentities.components.control.AbstractProcesingComponent.ProcessingCheckState;
import theking530.staticpower.tileentities.components.control.RecipeProcessingComponent;
import theking530.staticpower.tileentities.components.control.RecipeProcessingComponent.RecipeProcessingLocation;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.tileentities.components.fluids.FluidInputServoComponent;
import theking530.staticpower.tileentities.components.fluids.FluidTankComponent;
import theking530.staticpower.tileentities.components.items.BatteryInventoryComponent;
import theking530.staticpower.tileentities.components.items.FluidContainerInventoryComponent;
import theking530.staticpower.tileentities.components.items.FluidContainerInventoryComponent.FluidContainerInteractionMode;
import theking530.staticpower.tileentities.components.items.InputServoComponent;
import theking530.staticpower.tileentities.components.items.InventoryComponent;
import theking530.staticpower.tileentities.components.items.OutputServoComponent;
import theking530.staticpower.tileentities.components.items.UpgradeInventoryComponent;
import theking530.staticpower.utilities.InventoryUtilities;

public class TileEntityFluidInfuser extends TileEntityMachine {
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityFluidInfuser> TYPE = new BlockEntityTypeAllocator<TileEntityFluidInfuser>((type, pos, state) -> new TileEntityFluidInfuser(pos, state),
			ModBlocks.FluidInfuser);

	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setTileEntitySpecialRenderer(TileEntityRenderFluidInfuser::new);
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

	public TileEntityFluidInfuser(BlockPos pos, BlockState state) {
		super(TYPE, pos, state, StaticPowerTiers.BASIC);

		// Get the tier object.
		StaticPowerTier tier = StaticPowerConfig.getTier(StaticPowerTiers.BASIC);

		// Setup the inventories.
		registerComponent(inputInventory = new InventoryComponent("InputInventory", 1, MachineSideMode.Input).setShiftClickEnabled(true));
		registerComponent(internalInventory = new InventoryComponent("InternalInventory", 1));
		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 1, MachineSideMode.Output));
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", energyStorage.getStorage()));
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));

		// Setup the processing component.
		registerComponent(processingComponent = new RecipeProcessingComponent<FluidInfusionRecipe>("ProcessingComponent", FluidInfusionRecipe.RECIPE_TYPE,
				StaticPowerConfig.SERVER.fluidInfuserProcessingTime.get(), this::getMatchParameters, this::moveInputs, this::canProcessRecipe, this::processingCompleted));

		// Initialize the processing component to work with the redstone control
		// component, upgrade component and energy component.
		processingComponent.setShouldControlBlockState(true);
		processingComponent.setUpgradeInventory(upgradesInventory);
		processingComponent.setEnergyComponent(energyStorage);
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
		energyStorage.setUpgradeInventory(upgradesInventory);
	}

	protected RecipeMatchParameters getMatchParameters(RecipeProcessingLocation location) {
		if (location == RecipeProcessingLocation.INTERNAL) {
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

	protected ProcessingCheckState canProcessRecipe(FluidInfusionRecipe recipe) {
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
