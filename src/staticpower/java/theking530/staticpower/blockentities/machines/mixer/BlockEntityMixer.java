package theking530.staticpower.blockentities.machines.mixer;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import theking530.staticcore.blockentity.components.control.processing.ConcretizedProductContainer;
import theking530.staticcore.blockentity.components.control.processing.ProcessingCheckState;
import theking530.staticcore.blockentity.components.control.processing.ProcessingContainer;
import theking530.staticcore.blockentity.components.control.processing.ProcessingContainer.CaptureType;
import theking530.staticcore.blockentity.components.control.processing.recipe.IRecipeProcessor;
import theking530.staticcore.blockentity.components.control.processing.recipe.RecipeProcessingComponent;
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
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blockentities.BlockEntityMachine;
import theking530.staticpower.data.crafting.wrappers.mixer.MixerRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModRecipeTypes;

public class BlockEntityMixer extends BlockEntityMachine implements IRecipeProcessor<MixerRecipe> {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityMixer> TYPE = new BlockEntityTypeAllocator<BlockEntityMixer>(
			"mixer", (type, pos, state) -> new BlockEntityMixer(pos, state), ModBlocks.Mixer);

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
		StaticCoreTier tier = getTierObject();

		// Setup the inventories.
		registerComponent(input1Inventory = new InventoryComponent("Input1Inventory", 1, MachineSideMode.Input2)
				.setShiftClickEnabled(true));
		registerComponent(input2Inventory = new InventoryComponent("Input2Inventory", 1, MachineSideMode.Input3)
				.setShiftClickEnabled(true));
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", powerStorage));
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));

		// Setup the processing component.
		registerComponent(processingComponent = new RecipeProcessingComponent<MixerRecipe>("ProcessingComponent",
				StaticPowerConfig.SERVER.mixerProcessingTime.get(), ModRecipeTypes.MIXER_RECIPE_TYPE.get()));
		processingComponent.setShouldControlOnBlockState(true);
		processingComponent.setUpgradeInventory(upgradesInventory);
		processingComponent.setPowerComponent(powerStorage);
		processingComponent.setRedstoneControlComponent(redstoneControlComponent);

		// Setup the fluid tanks.
		registerComponent(fluidInput1 = new FluidTankComponent("FluidTank1", tier.defaultTankCapacity.get())
				.setCapabilityExposedModes(MachineSideMode.Input2).setUpgradeInventory(upgradesInventory));
		registerComponent(fluidInput2 = new FluidTankComponent("FluidTank2", tier.defaultTankCapacity.get())
				.setCapabilityExposedModes(MachineSideMode.Input3).setUpgradeInventory(upgradesInventory));
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
		registerComponent(
				fluidContainerComponent = new FluidContainerInventoryComponent("FluidContainerServo", fluidOutput)
						.setMode(FluidContainerInteractionMode.DRAIN));

		// Set the energy storage upgrade inventory.
		powerStorage.setUpgradeInventory(upgradesInventory);
	}

	@Override
	public RecipeMatchParameters getRecipeMatchParameters(RecipeProcessingComponent<MixerRecipe> component) {
		return new RecipeMatchParameters(getTeamComponent().getOwningTeamId())
				.setItems(input1Inventory.getStackInSlot(0), input2Inventory.getStackInSlot(0))
				.setFluids(fluidInput1.getFluid(), fluidInput2.getFluid());
	}

	@Override
	protected SideConfigurationPreset getDefaultSideConfiguration() {
		return MixerSideConfiguration.INSTANCE;
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerMixer(windowId, inventory, this);
	}

	@Override
	public void captureOutputs(RecipeProcessingComponent<MixerRecipe> component, MixerRecipe recipe,
			ConcretizedProductContainer outputContainer) {
		outputContainer.addFluid(recipe.getOutput().copy(), CaptureType.BOTH);
	}

	@Override
	public ProcessingCheckState canStartProcessingRecipe(RecipeProcessingComponent<MixerRecipe> component,
			MixerRecipe recipe, ConcretizedProductContainer outputContainer) {
		if (fluidOutput.fill(outputContainer.getFluid(0), FluidAction.SIMULATE) != outputContainer.getFluid(0)
				.getAmount()) {
			return ProcessingCheckState.fluidOutputFull();
		}
		return ProcessingCheckState.ok();
	}

	@Override
	public void captureInputs(RecipeProcessingComponent<MixerRecipe> component, MixerRecipe recipe,
			ProcessingContainer processingContainer, ConcretizedProductContainer inputContainer) {
		if (recipe.hasPrimaryItemInput()) {
			inputContainer.addItem(input1Inventory.extractItem(0, recipe.getPrimaryItemInput().getCount(), false));
		}
		if (recipe.hasSecondaryItemInput()) {
			inputContainer.addItem(input2Inventory.extractItem(0, recipe.getSecondaryItemInput().getCount(), false));
		}

		if (recipe.hasPrimaryFluidInput()) {
			inputContainer.addFluid(fluidInput1.getFluid(), recipe.getPrimaryFluidInput().getAmount());
		}
		if (recipe.hasSecondaryFluidInput()) {
			inputContainer.addFluid(fluidInput2.getFluid(), recipe.getSecondaryFluidInput().getAmount());
		}
	}

	@Override
	public void onProcessingCompleted(RecipeProcessingComponent<MixerRecipe> component,
			ProcessingContainer processingContainer) {
		fluidOutput.fill(processingContainer.getOutputs().getFluid(0).copy(), FluidAction.EXECUTE);
	}
}
