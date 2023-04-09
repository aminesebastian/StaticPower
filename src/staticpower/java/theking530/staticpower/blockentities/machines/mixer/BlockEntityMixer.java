package theking530.staticpower.blockentities.machines.mixer;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import theking530.staticcore.blockentity.components.control.oldprocessing.OldProcessingContainer;
import theking530.staticcore.blockentity.components.control.oldprocessing.OldRecipeProcessingComponent;
import theking530.staticcore.blockentity.components.control.oldprocessing.OldProcessingContainer.CaptureType;
import theking530.staticcore.blockentity.components.control.oldprocessing.interfaces.IOldRecipeProcessor;
import theking530.staticcore.blockentity.components.control.processing.ProcessingCheckState;
import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationPreset;
import theking530.staticcore.blockentity.components.fluids.FluidInputServoComponent;
import theking530.staticcore.blockentity.components.fluids.FluidOutputServoComponent;
import theking530.staticcore.blockentity.components.fluids.FluidTankComponent;
import theking530.staticcore.blockentity.components.items.BatteryInventoryComponent;
import theking530.staticcore.blockentity.components.items.FluidContainerInventoryComponent;
import theking530.staticcore.blockentity.components.items.FluidContainerInventoryComponent.FluidContainerInteractionMode;
import theking530.staticcore.blockentity.components.items.InputServoComponent;
import theking530.staticcore.blockentity.components.items.InventoryComponent;
import theking530.staticcore.blockentity.components.items.UpgradeInventoryComponent;
import theking530.staticcore.crafting.RecipeMatchParameters;
import theking530.staticcore.data.StaticCoreTier;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.blockentities.BlockEntityMachine;
import theking530.staticpower.data.crafting.wrappers.mixer.MixerRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModRecipeTypes;

public class BlockEntityMixer extends BlockEntityMachine implements IOldRecipeProcessor<MixerRecipe> {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityMixer> TYPE = new BlockEntityTypeAllocator<BlockEntityMixer>("mixer",
			(type, pos, state) -> new BlockEntityMixer(pos, state), ModBlocks.Mixer);

	public final InventoryComponent input1Inventory;
	public final InventoryComponent input2Inventory;
	public final InventoryComponent batteryInventory;

	public final FluidTankComponent fluidInput1;
	public final FluidTankComponent fluidInput2;
	public final FluidTankComponent fluidOutput;

	public final FluidContainerInventoryComponent fluidContainerComponent;
	public final UpgradeInventoryComponent upgradesInventory;
	public final OldRecipeProcessingComponent<MixerRecipe> processingComponent;

	public BlockEntityMixer(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);

		// Get the tier object.
		StaticCoreTier tier = getTierObject();

		// Setup the inventories.
		registerComponent(input1Inventory = new InventoryComponent("Input1Inventory", 1, MachineSideMode.Input2).setShiftClickEnabled(true));
		registerComponent(input2Inventory = new InventoryComponent("Input2Inventory", 1, MachineSideMode.Input3).setShiftClickEnabled(true));
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", powerStorage));
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));

		// Setup the processing component.
		registerComponent(processingComponent = new OldRecipeProcessingComponent<MixerRecipe>("ProcessingComponent", ModRecipeTypes.MIXER_RECIPE_TYPE.get(), this));
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
	public RecipeMatchParameters getRecipeMatchParameters(OldRecipeProcessingComponent<MixerRecipe> component) {
		return new RecipeMatchParameters().setItems(input1Inventory.getStackInSlot(0), input2Inventory.getStackInSlot(0)).setFluids(fluidInput1.getFluid(), fluidInput2.getFluid());
	}

	@Override
	public void captureInputsAndProducts(OldRecipeProcessingComponent<MixerRecipe> component, MixerRecipe recipe, OldProcessingContainer outputContainer) {
		if (recipe.hasPrimaryItemInput()) {
			outputContainer.addInputItem(input1Inventory.extractItem(0, recipe.getPrimaryItemInput().getCount(), true), CaptureType.BOTH);
		}
		if (recipe.hasSecondaryItemInput()) {
			outputContainer.addInputItem(input2Inventory.extractItem(0, recipe.getSecondaryItemInput().getCount(), true), CaptureType.BOTH);
		}

		if (recipe.hasPrimaryFluidInput()) {
			outputContainer.addInputFluid(fluidInput1.getFluid(), recipe.getPrimaryFluidInput().getAmount(), CaptureType.BOTH);
		}
		if (recipe.hasSecondaryFluidInput()) {
			outputContainer.addInputFluid(fluidInput2.getFluid(), recipe.getSecondaryFluidInput().getAmount(), CaptureType.BOTH);
		}

		outputContainer.addOutputFluid(recipe.getOutput().copy(), CaptureType.BOTH);

		// Set the power usage.
		component.setProcessingPowerUsage(recipe.getPowerCost());
		component.setMaxProcessingTime(recipe.getProcessingTime());
	}

	@Override
	public void processingStarted(OldRecipeProcessingComponent<MixerRecipe> component, MixerRecipe recipe, OldProcessingContainer outputContainer) {
		if (recipe.hasPrimaryItemInput()) {
			input1Inventory.extractItem(0, recipe.getPrimaryItemInput().getCount(), false);
		}
		if (recipe.hasSecondaryItemInput()) {
			input2Inventory.extractItem(0, recipe.getSecondaryItemInput().getCount(), false);
		}
	}

	@Override
	public ProcessingCheckState canStartProcessing(OldRecipeProcessingComponent<MixerRecipe> component, MixerRecipe recipe, OldProcessingContainer outputContainer) {
		if (fluidOutput.fill(outputContainer.getOutputFluid(0).fluid(), FluidAction.SIMULATE) != outputContainer.getOutputFluid(0).fluid().getAmount()) {
			return ProcessingCheckState.fluidOutputFull();
		}
		return ProcessingCheckState.ok();
	}

	@Override
	public void processingCompleted(OldRecipeProcessingComponent<MixerRecipe> component, MixerRecipe recipe, OldProcessingContainer outputContainer) {
		fluidOutput.fill(outputContainer.getOutputFluid(0).fluid().copy(), FluidAction.EXECUTE);

		// Drain the fluid.
		if (outputContainer.getInputFluids().size() > 0) {
			fluidInput1.drain(outputContainer.getInputFluid(0).fluid(), FluidAction.EXECUTE);
			if (outputContainer.getInputFluids().size() > 1) {
				fluidInput2.drain(outputContainer.getInputFluid(1).fluid(), FluidAction.EXECUTE);
			}
		}
	}

	@Override
	protected SideConfigurationPreset getDefaultSideConfiguration() {
		return MixerSideConfiguration.INSTANCE;
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerMixer(windowId, inventory, this);
	}

}
