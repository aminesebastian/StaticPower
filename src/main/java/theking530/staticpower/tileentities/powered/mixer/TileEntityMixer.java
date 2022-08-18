package theking530.staticpower.tileentities.powered.mixer;

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
import theking530.staticpower.data.crafting.wrappers.mixer.MixerRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.tileentities.TileEntityMachine;
import theking530.staticpower.tileentities.components.control.AbstractProcesingComponent.ProcessingCheckState;
import theking530.staticpower.tileentities.components.control.RecipeProcessingComponent;
import theking530.staticpower.tileentities.components.control.RecipeProcessingComponent.RecipeProcessingLocation;
import theking530.staticpower.tileentities.components.control.sideconfiguration.DefaultSideConfiguration;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;
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

public class TileEntityMixer extends TileEntityMachine {
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityMixer> TYPE = new BlockEntityTypeAllocator<TileEntityMixer>((type, pos, state) -> new TileEntityMixer(pos, state), ModBlocks.Mixer);

	public final InventoryComponent input1Inventory;
	public final InventoryComponent input2Inventory;
	public final InventoryComponent internalInventory;
	public final InventoryComponent batteryInventory;

	public final FluidTankComponent fluidInput1;
	public final FluidTankComponent fluidInput2;
	public final FluidTankComponent fluidOutput;

	public final FluidContainerInventoryComponent fluidContainerComponent;
	public final UpgradeInventoryComponent upgradesInventory;
	public final RecipeProcessingComponent<MixerRecipe> processingComponent;

	public TileEntityMixer(BlockPos pos, BlockState state) {
		super(TYPE, pos, state, StaticPowerTiers.ADVANCED);

		// Get the tier object.
		StaticPowerTier tier = StaticPowerConfig.getTier(StaticPowerTiers.ADVANCED);

		// Setup the inventories.
		registerComponent(input1Inventory = new InventoryComponent("Input1Inventory", 1, MachineSideMode.Input2).setShiftClickEnabled(true));
		registerComponent(input2Inventory = new InventoryComponent("Input2Inventory", 1, MachineSideMode.Input3).setShiftClickEnabled(true));
		registerComponent(internalInventory = new InventoryComponent("InternalInventory", 2));
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", energyStorage.getStorage()));
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));

		// Setup the processing component.
		registerComponent(processingComponent = new RecipeProcessingComponent<MixerRecipe>("ProcessingComponent", MixerRecipe.RECIPE_TYPE, 1, this::getMatchParameters, this::moveInputs,
				this::canProcessRecipe, this::processingCompleted));

		// Initialize the processing component to work with the redstone control
		// component, upgrade component and energy component.
		processingComponent.setShouldControlBlockState(true);
		processingComponent.setUpgradeInventory(upgradesInventory);
		processingComponent.setEnergyComponent(energyStorage);
		processingComponent.setRedstoneControlComponent(redstoneControlComponent);

		// Setup the fluid tanks.
		registerComponent(fluidInput1 = new FluidTankComponent("FluidTank1", tier.defaultTankCapacity.get()).setCapabilityExposedModes(MachineSideMode.Input2).setUpgradeInventory(upgradesInventory));
		registerComponent(fluidInput2 = new FluidTankComponent("FluidTank2", tier.defaultTankCapacity.get()).setCapabilityExposedModes(MachineSideMode.Input3).setUpgradeInventory(upgradesInventory));
		registerComponent(fluidOutput = new FluidTankComponent("FluidTankOutput", tier.defaultTankCapacity.get()));
		fluidOutput.setCapabilityExposedModes(MachineSideMode.Output);
		fluidOutput.setUpgradeInventory(upgradesInventory);
		fluidOutput.setAutoSyncPacketsEnabled(true);

		// Setup the I/O servos.
		registerComponent(new InputServoComponent("Input1Servo", 4, input1Inventory, 0));
		registerComponent(new InputServoComponent("Input2Servo", 4, input2Inventory, 0));
		registerComponent(new FluidInputServoComponent("FluidInput1Servo", 100, fluidInput1, MachineSideMode.Input2));
		registerComponent(new FluidInputServoComponent("FluidInput2Servo", 100, fluidInput2, MachineSideMode.Input3));
		registerComponent(new FluidOutputServoComponent("FluidOutputServo", 100, fluidOutput, MachineSideMode.Output));

		// Create the fluid container component.
		registerComponent(fluidContainerComponent = new FluidContainerInventoryComponent("FluidContainerServo", fluidOutput).setMode(FluidContainerInteractionMode.DRAIN));

		// Set the energy storage upgrade inventory.
		energyStorage.setUpgradeInventory(upgradesInventory);
	}

	protected RecipeMatchParameters getMatchParameters(RecipeProcessingLocation location) {
		if (location == RecipeProcessingLocation.INTERNAL) {
			return new RecipeMatchParameters().setItems(internalInventory.getStackInSlot(0), internalInventory.getStackInSlot(1)).setFluids(fluidInput1.getFluid(), fluidInput2.getFluid());
		} else {
			return new RecipeMatchParameters().setItems(input1Inventory.getStackInSlot(0), input2Inventory.getStackInSlot(0)).setFluids(fluidInput1.getFluid(), fluidInput2.getFluid());
		}
	}

	protected ProcessingCheckState moveInputs(MixerRecipe recipe) {
		// If this recipe has a fluid output that we cannot put into the output tank,
		// continue waiting.
		if (fluidOutput.fill(recipe.getOutput(), FluidAction.SIMULATE) != recipe.getOutput().getAmount()) {
			return ProcessingCheckState.outputTankCannotTakeFluid();
		}

		transferItemInternally(recipe.getPrimaryItemInput().getCount(), input1Inventory, 0, internalInventory, 0);
		transferItemInternally(recipe.getSecondaryItemInput().getCount(), input2Inventory, 0, internalInventory, 1);

		// Set the power usage.
		processingComponent.setProcessingPowerUsage(recipe.getPowerCost());
		processingComponent.setMaxProcessingTime(recipe.getProcessingTime());

		return ProcessingCheckState.ok();
	}

	protected ProcessingCheckState canProcessRecipe(MixerRecipe recipe) {
		// If this recipe has a fluid output that we cannot put into the output tank,
		// continue waiting.
		if (fluidOutput.fill(recipe.getOutput(), FluidAction.SIMULATE) != recipe.getOutput().getAmount()) {
			return ProcessingCheckState.outputTankCannotTakeFluid();
		}
		return ProcessingCheckState.ok();
	}

	protected ProcessingCheckState processingCompleted(MixerRecipe recipe) {
		// Output the mixed fluid.
		fluidOutput.fill(recipe.getOutput(), FluidAction.EXECUTE);

		// Drain the fluid.
		fluidInput1.drain(recipe.getPrimaryFluidInput().getAmount(), FluidAction.EXECUTE);
		fluidInput2.drain(recipe.getSecondaryFluidInput().getAmount(), FluidAction.EXECUTE);

		// Clear the internal inventory.
		InventoryUtilities.clearInventory(internalInventory);
		return ProcessingCheckState.ok();
	}

	@Override
	protected boolean isValidSideConfiguration(BlockSide side, MachineSideMode mode) {
		return mode == MachineSideMode.Disabled || mode == MachineSideMode.Output || mode == MachineSideMode.Input || mode == MachineSideMode.Input2 || mode == MachineSideMode.Input3;
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerMixer(windowId, inventory, this);
	}

	@Override
	protected DefaultSideConfiguration getDefaultSideConfiguration() {
		return DEFAULT_NO_FACE_SIDE_CONFIGURATION.copy().setSide(BlockSide.LEFT, true, MachineSideMode.Input2).setSide(BlockSide.RIGHT, true, MachineSideMode.Input3);
	}
}
