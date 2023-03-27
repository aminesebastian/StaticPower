package theking530.staticpower.blockentities.machines.crucible;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import theking530.api.heat.HeatStorageUtilities;
import theking530.api.heat.IHeatStorage.HeatTransferAction;
import theking530.staticcore.blockentity.components.control.processing.ProcessingCheckState;
import theking530.staticcore.blockentity.components.control.processing.ProcessingOutputContainer;
import theking530.staticcore.blockentity.components.control.processing.ProcessingOutputContainer.CaptureType;
import theking530.staticcore.blockentity.components.control.processing.RecipeProcessingComponent;
import theking530.staticcore.blockentity.components.control.processing.interfaces.IRecipeProcessor;
import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.blockentity.components.fluids.FluidOutputServoComponent;
import theking530.staticcore.blockentity.components.fluids.FluidTankComponent;
import theking530.staticcore.blockentity.components.heat.HeatStorageComponent;
import theking530.staticcore.blockentity.components.items.BatteryInventoryComponent;
import theking530.staticcore.blockentity.components.items.FluidContainerInventoryComponent;
import theking530.staticcore.blockentity.components.items.FluidContainerInventoryComponent.FluidContainerInteractionMode;
import theking530.staticcore.blockentity.components.items.InputServoComponent;
import theking530.staticcore.blockentity.components.items.InventoryComponent;
import theking530.staticcore.blockentity.components.items.ItemStackHandlerFilter;
import theking530.staticcore.blockentity.components.items.OutputServoComponent;
import theking530.staticcore.blockentity.components.items.UpgradeInventoryComponent;
import theking530.staticcore.crafting.RecipeMatchParameters;
import theking530.staticcore.data.StaticCoreTier;
import theking530.staticcore.gui.text.GuiTextUtilities;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticcore.utilities.item.InventoryUtilities;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blockentities.BlockEntityMachine;
import theking530.staticpower.data.crafting.wrappers.crucible.CrucibleRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModRecipeTypes;

public class BlockEntityCrucible extends BlockEntityMachine implements IRecipeProcessor<CrucibleRecipe> {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityCrucible> TYPE = new BlockEntityTypeAllocator<>("crucible", (type, pos, state) -> new BlockEntityCrucible(pos, state),
			ModBlocks.Crucible);

	public final InventoryComponent inputInventory;
	public final InventoryComponent outputInventory;
	public final BatteryInventoryComponent batteryInventory;
	public final UpgradeInventoryComponent upgradesInventory;
	public final FluidContainerInventoryComponent fluidContainerComponent;
	public final HeatStorageComponent heatStorage;

	public final RecipeProcessingComponent<CrucibleRecipe> processingComponent;
	public final FluidTankComponent fluidTankComponent;

	public BlockEntityCrucible(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);

		// Get the tier object.
		StaticCoreTier tier = getTierObject();

		// Setup the input inventory to only accept items that have a valid recipe.
		registerComponent(inputInventory = new InventoryComponent("InputInventory", 1, MachineSideMode.Input).setShiftClickEnabled(true).setFilter(new ItemStackHandlerFilter() {
			public boolean canInsertItem(int slot, ItemStack stack) {
				return processingComponent.getRecipeMatchingParameters(new RecipeMatchParameters(stack).ignoreItemCounts()).isPresent();
			}

		}));

		// Setup all the other inventories.
		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 1, MachineSideMode.Output));
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", powerStorage));
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));

		// Register the heate component.
		registerComponent(
				heatStorage = new HeatStorageComponent("HeatStorageComponent", tier.defaultMachineOverheatTemperature.get(), tier.defaultMachineMaximumTemperature.get(), 1.0f));

		// Setup the processing component.
		registerComponent(processingComponent = new RecipeProcessingComponent<CrucibleRecipe>("ProcessingComponent", ModRecipeTypes.CRUCIBLE_RECIPE_TYPE.get(), this));

		// Initialize the processing component to work with the redstone control
		// component, upgrade component and energy component.
		processingComponent.setShouldControlBlockState(true);
		processingComponent.setUpgradeInventory(upgradesInventory);
		processingComponent.setPowerComponent(powerStorage);
		processingComponent.setRedstoneControlComponent(redstoneControlComponent);

		// Setup the I/O servos.
		registerComponent(new OutputServoComponent("OutputServo", 2, outputInventory));
		registerComponent(new InputServoComponent("InputServo", 2, inputInventory));

		// Setup the fluid tank and fluid output servo.
		registerComponent(fluidTankComponent = new FluidTankComponent("FluidTank", tier.defaultTankCapacity.get()).setCapabilityExposedModes(MachineSideMode.Output)
				.setUpgradeInventory(upgradesInventory));
		registerComponent(new FluidOutputServoComponent("FluidOutputServoComponent", 100, fluidTankComponent, MachineSideMode.Output));
		registerComponent(fluidContainerComponent = new FluidContainerInventoryComponent("FluidContainerServo", fluidTankComponent).setMode(FluidContainerInteractionMode.FILL));

		// Set the energy storage upgrade inventory.
		powerStorage.setUpgradeInventory(upgradesInventory);
	}

	@Override
	public void process() {
		super.process();
		if (!level.isClientSide && redstoneControlComponent.passesRedstoneCheck()) {
			if (powerStorage.canSupplyPower(StaticPowerConfig.SERVER.crucibleHeatPowerUsage.get())
					&& HeatStorageUtilities.canFullyAbsorbHeat(heatStorage, StaticPowerConfig.SERVER.crucibleHeatGenerationPerTick.get())) {
				heatStorage.heat(StaticPowerConfig.SERVER.crucibleHeatGenerationPerTick.get(), HeatTransferAction.EXECUTE);
				powerStorage.drainPower(StaticPowerConfig.SERVER.crucibleHeatPowerUsage.get(), false);
			}
		}
	}

	@Override
	public RecipeMatchParameters getRecipeMatchParameters(RecipeProcessingComponent<CrucibleRecipe> component) {
		return new RecipeMatchParameters(inputInventory.getStackInSlot(0));
	}

	@Override
	public void captureInputsAndProducts(RecipeProcessingComponent<CrucibleRecipe> component, CrucibleRecipe recipe, ProcessingOutputContainer outputContainer) {
		outputContainer.addInputItem(inputInventory.extractItem(0, recipe.getInput().getCount(), true), CaptureType.BOTH);
		outputContainer.addOutputFluid(recipe.getOutputFluid(), CaptureType.BOTH);
		outputContainer.addOutputItem(recipe.getOutput().calculateOutput(), CaptureType.BOTH);

		// Set the power usage.
		component.setProcessingPowerUsage(recipe.getPowerCost());
		component.setMaxProcessingTime(recipe.getProcessingTime());
	}

	@Override
	public void processingStarted(RecipeProcessingComponent<CrucibleRecipe> component, CrucibleRecipe recipe, ProcessingOutputContainer outputContainer) {
		inputInventory.extractItem(0, recipe.getInput().getCount(), false);
	}

	@Override
	public ProcessingCheckState canStartProcessing(RecipeProcessingComponent<CrucibleRecipe> component, CrucibleRecipe recipe, ProcessingOutputContainer outputContainer) {
		// If this recipe has an item output that we cannot put into the output slot,
		// continue waiting.
		if (outputContainer.hasOutputItems() && !InventoryUtilities.canFullyInsertStackIntoSlot(outputInventory, 0, outputContainer.getOutputItem(0).item())) {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}

		// Check the heat.
		if (heatStorage.getCurrentHeat() < recipe.getProcessingSection().getMinimumHeat()) {
			return ProcessingCheckState.error(
					"Minimum heat temperature of " + GuiTextUtilities.formatHeatToString(recipe.getProcessingSection().getMinimumHeat()).getString() + " has not been reached!");
		}

		// If this recipe has a fluid output that we cannot put into the output tank,
		// continue waiting.
		if (fluidTankComponent.fill(outputContainer.getOutputFluid(0).fluid(), FluidAction.SIMULATE) != outputContainer.getOutputFluid(0).fluid().getAmount()) {
			if (!fluidTankComponent.getFluid().isEmpty() && fluidTankComponent.getFluid().isFluidEqual(outputContainer.getOutputFluid(0).fluid())) {
				return ProcessingCheckState.outputFluidDoesNotMatch();
			} else {
				return ProcessingCheckState.fluidOutputFull();
			}
		}
		return ProcessingCheckState.ok();
	}

	@Override
	public void processingCompleted(RecipeProcessingComponent<CrucibleRecipe> component, CrucibleRecipe recipe, ProcessingOutputContainer outputContainer) {
		if (outputContainer.hasOutputItems()) {
			outputInventory.insertItem(0, outputContainer.getOutputItem(0).item().copy(), false);
		}

		fluidTankComponent.fill(outputContainer.getOutputFluid(0).fluid(), FluidAction.EXECUTE);
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerCrucible(windowId, inventory, this);
	}
}