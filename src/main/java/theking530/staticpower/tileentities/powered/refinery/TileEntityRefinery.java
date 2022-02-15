package theking530.staticpower.tileentities.powered.refinery;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import theking530.staticcore.initialization.tileentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.wrappers.refinery.RefineryRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.tileentities.TileEntityMachine;
import theking530.staticpower.tileentities.components.control.AbstractProcesingComponent.ProcessingCheckState;
import theking530.staticpower.tileentities.components.control.RecipeProcessingComponent;
import theking530.staticpower.tileentities.components.control.RecipeProcessingComponent.RecipeProcessingLocation;
import theking530.staticpower.tileentities.components.control.sideconfiguration.DefaultSideConfiguration;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticpower.tileentities.components.fluids.FluidInputServoComponent;
import theking530.staticpower.tileentities.components.fluids.FluidOutputServoComponent;
import theking530.staticpower.tileentities.components.fluids.FluidTankComponent;
import theking530.staticpower.tileentities.components.items.BatteryInventoryComponent;
import theking530.staticpower.tileentities.components.items.FluidContainerInventoryComponent;
import theking530.staticpower.tileentities.components.items.FluidContainerInventoryComponent.FluidContainerInteractionMode;
import theking530.staticpower.tileentities.components.items.InputServoComponent;
import theking530.staticpower.tileentities.components.items.InventoryComponent;
import theking530.staticpower.tileentities.components.items.UpgradeInventoryComponent;
import theking530.staticpower.utilities.InventoryUtilities;

public class TileEntityRefinery extends TileEntityMachine {
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityRefinery> TYPE = new BlockEntityTypeAllocator<TileEntityRefinery>((type, pos, state) -> new TileEntityRefinery(pos, state),
			ModBlocks.Refinery);

	public final InventoryComponent catalystInventory;
	public final InventoryComponent internalInventory;
	public final InventoryComponent batteryInventory;

	public final FluidTankComponent fluidInput1;
	public final FluidTankComponent fluidInput2;

	public final FluidTankComponent fluidOutput1;
	public final FluidTankComponent fluidOutput2;
	public final FluidTankComponent fluidOutput3;

	public final FluidContainerInventoryComponent fluidContainerComponent;
	public final UpgradeInventoryComponent upgradesInventory;
	public final RecipeProcessingComponent<RefineryRecipe> processingComponent;

	public TileEntityRefinery(BlockPos pos, BlockState state) {
		super(TYPE, pos, state, StaticPowerTiers.ADVANCED);
		enableFaceInteraction();

		// Get the tier object.
		StaticPowerTier tier = StaticPowerConfig.getTier(StaticPowerTiers.ADVANCED);

		// Setup the inventories.
		registerComponent(catalystInventory = new InventoryComponent("CatalystInventory", 1, MachineSideMode.Input).setShiftClickEnabled(true));
		registerComponent(internalInventory = new InventoryComponent("InternalInventory", 1));
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", energyStorage.getStorage()));
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));

		// Setup the processing component.
		registerComponent(processingComponent = new RecipeProcessingComponent<RefineryRecipe>("ProcessingComponent", RefineryRecipe.RECIPE_TYPE, 1, this::getMatchParameters, this::moveInputs,
				this::canProcessRecipe, this::processingCompleted));

		// Initialize the processing component to work with the redstone control
		// component, upgrade component and energy component.
		processingComponent.setShouldControlBlockState(true);
		processingComponent.setUpgradeInventory(upgradesInventory);
		processingComponent.setEnergyComponent(energyStorage);
		processingComponent.setRedstoneControlComponent(redstoneControlComponent);

		// Setup the input fluid tanks.
		registerComponent(fluidInput1 = new FluidTankComponent("FluidTank1", tier.defaultTankCapacity.get()).setCapabilityExposedModes(MachineSideMode.Input2).setUpgradeInventory(upgradesInventory));
		registerComponent(fluidInput2 = new FluidTankComponent("FluidTank2", tier.defaultTankCapacity.get()).setCapabilityExposedModes(MachineSideMode.Input3).setUpgradeInventory(upgradesInventory));

		// Setup the output fluid tanks.
		registerComponent(fluidOutput1 = new FluidTankComponent("FluidTankOutput1", tier.defaultTankCapacity.get()));
		fluidOutput1.setCapabilityExposedModes(MachineSideMode.Output);
		fluidOutput1.setUpgradeInventory(upgradesInventory);
		fluidOutput1.setAutoSyncPacketsEnabled(true);

		registerComponent(fluidOutput2 = new FluidTankComponent("FluidTankOutput2", tier.defaultTankCapacity.get()));
		fluidOutput2.setCapabilityExposedModes(MachineSideMode.Output2);
		fluidOutput2.setUpgradeInventory(upgradesInventory);
		fluidOutput2.setAutoSyncPacketsEnabled(true);

		registerComponent(fluidOutput3 = new FluidTankComponent("FluidTankOutput3", tier.defaultTankCapacity.get()));
		fluidOutput3.setCapabilityExposedModes(MachineSideMode.Output3);
		fluidOutput3.setUpgradeInventory(upgradesInventory);
		fluidOutput3.setAutoSyncPacketsEnabled(true);

		// Setup the I/O servos.
		registerComponent(new InputServoComponent("InputServo", 4, catalystInventory, 0));
		registerComponent(new FluidInputServoComponent("FluidInput1Servo", 100, fluidInput1, MachineSideMode.Input2));
		registerComponent(new FluidInputServoComponent("FluidInput2Servo", 100, fluidInput2, MachineSideMode.Input3));

		registerComponent(new FluidOutputServoComponent("FluidOutput1Servo", 100, fluidOutput1, MachineSideMode.Output));
		registerComponent(new FluidOutputServoComponent("FluidOutput2Servo", 100, fluidOutput2, MachineSideMode.Output2));
		registerComponent(new FluidOutputServoComponent("FluidOutput3Servo", 100, fluidOutput3, MachineSideMode.Output3));

		// Create the fluid container component.
		registerComponent(fluidContainerComponent = new FluidContainerInventoryComponent("FluidContainerServo", fluidInput1).setMode(FluidContainerInteractionMode.FILL));

		// Set the energy storage upgrade inventory.
		energyStorage.setUpgradeInventory(upgradesInventory);
	}

	protected RecipeMatchParameters getMatchParameters(RecipeProcessingLocation location) {
		if (location == RecipeProcessingLocation.INTERNAL) {
			return new RecipeMatchParameters().setItems(internalInventory.getStackInSlot(0)).setFluids(fluidInput1.getFluid(), fluidInput2.getFluid());
		} else {
			return new RecipeMatchParameters().setItems(catalystInventory.getStackInSlot(0)).setFluids(fluidInput1.getFluid(), fluidInput2.getFluid());
		}
	}

	protected ProcessingCheckState moveInputs(RefineryRecipe recipe) {
		// If this recipe has a fluid output that we cannot put into the output tank,
		// continue waiting.
		if (fluidOutput1.fill(recipe.getFluidOutput1(), FluidAction.SIMULATE) != recipe.getFluidOutput1().getAmount()) {
			return ProcessingCheckState.outputTankCannotTakeFluid();
		}
		if (fluidOutput2.fill(recipe.getFluidOutput2(), FluidAction.SIMULATE) != recipe.getFluidOutput2().getAmount()) {
			return ProcessingCheckState.outputTankCannotTakeFluid();
		}
		if (fluidOutput3.fill(recipe.getFluidOutput3(), FluidAction.SIMULATE) != recipe.getFluidOutput3().getAmount()) {
			return ProcessingCheckState.outputTankCannotTakeFluid();
		}

		transferItemInternally(recipe.getCatalyst().getCount(), catalystInventory, 0, internalInventory, 0);

		// Set the power usage.
		processingComponent.setProcessingPowerUsage(recipe.getPowerCost());
		processingComponent.setMaxProcessingTime(recipe.getProcessingTime());

		return ProcessingCheckState.ok();
	}

	protected ProcessingCheckState canProcessRecipe(RefineryRecipe recipe) {
		// If this recipe has a fluid output that we cannot put into the output tank,
		// continue waiting.
		if (fluidOutput1.fill(recipe.getFluidOutput1(), FluidAction.SIMULATE) != recipe.getFluidOutput1().getAmount()) {
			return ProcessingCheckState.outputTankCannotTakeFluid();
		}
		if (fluidOutput2.fill(recipe.getFluidOutput2(), FluidAction.SIMULATE) != recipe.getFluidOutput2().getAmount()) {
			return ProcessingCheckState.outputTankCannotTakeFluid();
		}
		if (fluidOutput3.fill(recipe.getFluidOutput3(), FluidAction.SIMULATE) != recipe.getFluidOutput3().getAmount()) {
			return ProcessingCheckState.outputTankCannotTakeFluid();
		}
		return ProcessingCheckState.ok();
	}

	protected ProcessingCheckState processingCompleted(RefineryRecipe recipe) {
		// Output the mixed fluid.
		fluidOutput1.fill(recipe.getFluidOutput1(), FluidAction.EXECUTE);
		fluidOutput2.fill(recipe.getFluidOutput2(), FluidAction.EXECUTE);
		fluidOutput3.fill(recipe.getFluidOutput3(), FluidAction.EXECUTE);

		// Drain the fluid.
		fluidInput1.drain(recipe.getPrimaryFluidInput().getAmount(), FluidAction.EXECUTE);
		fluidInput2.drain(recipe.getSecondaryFluidInput().getAmount(), FluidAction.EXECUTE);

		// Clear the internal inventory.
		InventoryUtilities.clearInventory(internalInventory);
		return ProcessingCheckState.ok();
	}

	@Override
	protected boolean isValidSideConfiguration(BlockSide side, MachineSideMode mode) {
		return mode == MachineSideMode.Disabled || mode == MachineSideMode.Regular || mode == MachineSideMode.Output || mode == MachineSideMode.Output2 || mode == MachineSideMode.Output3
				|| mode == MachineSideMode.Input || mode == MachineSideMode.Input2 || mode == MachineSideMode.Input3;
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerRefinery(windowId, inventory, this);
	}

	@Override
	protected DefaultSideConfiguration getDefaultSideConfiguration() {
		return SideConfigurationComponent.DEFAULT_SIDE_CONFIGURATION.copy().setSide(BlockSide.FRONT, true, MachineSideMode.Input).setSide(BlockSide.LEFT, true, MachineSideMode.Output).setSide(BlockSide.BACK, true, MachineSideMode.Output2)
				.setSide(BlockSide.RIGHT, true, MachineSideMode.Output3).setSide(BlockSide.TOP, true, MachineSideMode.Input2).setSide(BlockSide.BOTTOM, true, MachineSideMode.Input3);
	}
}
