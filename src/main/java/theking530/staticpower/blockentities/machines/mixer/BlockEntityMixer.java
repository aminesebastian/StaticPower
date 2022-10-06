package theking530.staticpower.blockentities.machines.mixer;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.blockentities.BlockEntityMachine;
import theking530.staticpower.blockentities.components.control.processing.ProcessingCheckState;
import theking530.staticpower.blockentities.components.control.processing.ProcessingOutputContainer;
import theking530.staticpower.blockentities.components.control.processing.RecipeProcessingComponent;
import theking530.staticpower.blockentities.components.control.processing.interfaces.IRecipeProcessor;
import theking530.staticpower.blockentities.components.control.sideconfiguration.DefaultSideConfiguration;
import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticpower.blockentities.components.fluids.FluidInputServoComponent;
import theking530.staticpower.blockentities.components.fluids.FluidOutputServoComponent;
import theking530.staticpower.blockentities.components.fluids.FluidTankComponent;
import theking530.staticpower.blockentities.components.items.BatteryInventoryComponent;
import theking530.staticpower.blockentities.components.items.FluidContainerInventoryComponent;
import theking530.staticpower.blockentities.components.items.FluidContainerInventoryComponent.FluidContainerInteractionMode;
import theking530.staticpower.blockentities.components.items.InputServoComponent;
import theking530.staticpower.blockentities.components.items.InventoryComponent;
import theking530.staticpower.blockentities.components.items.UpgradeInventoryComponent;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.wrappers.mixer.MixerRecipe;
import theking530.staticpower.init.ModBlocks;

public class BlockEntityMixer extends BlockEntityMachine implements IRecipeProcessor<MixerRecipe> {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityMixer> TYPE = new BlockEntityTypeAllocator<BlockEntityMixer>((type, pos, state) -> new BlockEntityMixer(pos, state),
			ModBlocks.Mixer);

	public final InventoryComponent input1Inventory;
	public final InventoryComponent input2Inventory;
	public final InventoryComponent batteryInventory;

	public final FluidTankComponent fluidInput1;
	public final FluidTankComponent fluidInput2;
	public final FluidTankComponent fluidOutput;

	public final FluidContainerInventoryComponent fluidContainerComponent;
	public final UpgradeInventoryComponent upgradesInventory;
	public final RecipeProcessingComponent<MixerRecipe> processingComponent;

	public BlockEntityMixer(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);

		// Get the tier object.
		StaticPowerTier tier = getTierObject();

		// Setup the inventories.
		registerComponent(input1Inventory = new InventoryComponent("Input1Inventory", 1, MachineSideMode.Input2).setShiftClickEnabled(true));
		registerComponent(input2Inventory = new InventoryComponent("Input2Inventory", 1, MachineSideMode.Input3).setShiftClickEnabled(true));
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", powerStorage));
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));

		// Setup the processing component.
		registerComponent(processingComponent = new RecipeProcessingComponent<MixerRecipe>("ProcessingComponent", MixerRecipe.RECIPE_TYPE, this));
		processingComponent.setShouldControlBlockState(true);
		processingComponent.setUpgradeInventory(upgradesInventory);
		processingComponent.setPowerComponent(powerStorage);
		processingComponent.setRedstoneControlComponent(redstoneControlComponent);

		// Setup the fluid tanks.
		registerComponent(fluidInput1 = new FluidTankComponent("FluidTank1", tier.defaultTankCapacity.get()).setCapabilityExposedModes(MachineSideMode.Input2)
				.setUpgradeInventory(upgradesInventory));
		registerComponent(fluidInput2 = new FluidTankComponent("FluidTank2", tier.defaultTankCapacity.get()).setCapabilityExposedModes(MachineSideMode.Input3)
				.setUpgradeInventory(upgradesInventory));
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
		powerStorage.setUpgradeInventory(upgradesInventory);
	}

	@Override
	public RecipeMatchParameters getRecipeMatchParameters(RecipeProcessingComponent<MixerRecipe> component) {
		return new RecipeMatchParameters().setItems(input1Inventory.getStackInSlot(0), input2Inventory.getStackInSlot(0)).setFluids(fluidInput1.getFluid(), fluidInput2.getFluid());
	}

	@Override
	public void captureInputsAndProducts(RecipeProcessingComponent<MixerRecipe> component, MixerRecipe recipe, ProcessingOutputContainer outputContainer) {
		if (recipe.hasPrimaryItemInput()) {
			outputContainer.addInputItem(input1Inventory.extractItem(0, recipe.getPrimaryItemInput().getCount(), false));
		}
		if (recipe.hasSecondaryItemInput()) {
			outputContainer.addInputItem(input2Inventory.extractItem(0, recipe.getSecondaryItemInput().getCount(), false));
		}

		if (recipe.hasPrimaryFluidInput()) {
			outputContainer.addInputFluid(fluidInput1.getFluid(), recipe.getPrimaryFluidInput().getAmount());
		}
		if (recipe.hasSecondaryFluidInput()) {
			outputContainer.addInputFluid(fluidInput2.getFluid(), recipe.getSecondaryFluidInput().getAmount());
		}

		outputContainer.addOutputFluid(recipe.getOutput().copy());

		// Set the power usage.
		component.setProcessingPowerUsage(recipe.getPowerCost());
		component.setMaxProcessingTime(recipe.getProcessingTime());
	}

	@Override
	public ProcessingCheckState canStartProcessing(RecipeProcessingComponent<MixerRecipe> component, MixerRecipe recipe, ProcessingOutputContainer outputContainer) {
		if (fluidOutput.fill(outputContainer.getOutputFluid(0), FluidAction.SIMULATE) != outputContainer.getOutputFluid(0).getAmount()) {
			return ProcessingCheckState.fluidOutputFull();
		}
		return ProcessingCheckState.ok();
	}

	@Override
	public void processingCompleted(RecipeProcessingComponent<MixerRecipe> component, MixerRecipe recipe, ProcessingOutputContainer outputContainer) {
		fluidOutput.fill(outputContainer.getOutputFluid(0), FluidAction.EXECUTE);

		// Drain the fluid.
		if (outputContainer.getInputFluids().size() > 0) {
			fluidInput1.drain(outputContainer.getInputFluid(0).getAmount(), FluidAction.EXECUTE);
			if (outputContainer.getInputFluids().size() > 1) {
				fluidInput2.drain(outputContainer.getInputFluid(1).getAmount(), FluidAction.EXECUTE);
			}
		}
	}

	@Override
	protected boolean isValidSideConfiguration(BlockSide side, MachineSideMode mode) {
		return mode == MachineSideMode.Disabled || mode == MachineSideMode.Output || mode == MachineSideMode.Input || mode == MachineSideMode.Input2
				|| mode == MachineSideMode.Input3;
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
