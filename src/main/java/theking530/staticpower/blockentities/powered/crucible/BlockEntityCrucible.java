package theking530.staticpower.blockentities.powered.crucible;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import theking530.api.heat.HeatStorageUtilities;
import theking530.api.heat.IHeatStorage.HeatTransferAction;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blockentities.BlockEntityMachine;
import theking530.staticpower.blockentities.components.control.RecipeProcessingComponent;
import theking530.staticpower.blockentities.components.control.AbstractProcesingComponent.ProcessingCheckState;
import theking530.staticpower.blockentities.components.control.RecipeProcessingComponent.RecipeProcessingPhase;
import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.blockentities.components.fluids.FluidOutputServoComponent;
import theking530.staticpower.blockentities.components.fluids.FluidTankComponent;
import theking530.staticpower.blockentities.components.heat.HeatStorageComponent;
import theking530.staticpower.blockentities.components.items.BatteryInventoryComponent;
import theking530.staticpower.blockentities.components.items.FluidContainerInventoryComponent;
import theking530.staticpower.blockentities.components.items.InputServoComponent;
import theking530.staticpower.blockentities.components.items.InventoryComponent;
import theking530.staticpower.blockentities.components.items.ItemStackHandlerFilter;
import theking530.staticpower.blockentities.components.items.OutputServoComponent;
import theking530.staticpower.blockentities.components.items.UpgradeInventoryComponent;
import theking530.staticpower.blockentities.components.items.FluidContainerInventoryComponent.FluidContainerInteractionMode;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.wrappers.crucible.CrucibleRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.utilities.InventoryUtilities;

public class BlockEntityCrucible extends BlockEntityMachine {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityCrucible> TYPE = new BlockEntityTypeAllocator<>((type, pos, state) -> new BlockEntityCrucible(pos, state),
			ModBlocks.Crucible);

	public final InventoryComponent inputInventory;
	public final InventoryComponent internalInventory;
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
		StaticPowerTier tier = getTierObject();

		// Setup the input inventory to only accept items that have a valid recipe.
		registerComponent(inputInventory = new InventoryComponent("InputInventory", 1, MachineSideMode.Input).setShiftClickEnabled(true).setFilter(new ItemStackHandlerFilter() {
			public boolean canInsertItem(int slot, ItemStack stack) {
				return processingComponent.getRecipeMatchingParameters(new RecipeMatchParameters(stack).ignoreItemCounts()).isPresent();
			}

		}));

		// Setup all the other inventories.
		registerComponent(internalInventory = new InventoryComponent("InternalInventory", 1));
		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 1, MachineSideMode.Output));
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", powerStorage));
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));

		// Register the heate component.
		registerComponent(
				heatStorage = new HeatStorageComponent("HeatStorageComponent", tier.defaultMachineOverheatTemperature.get(), tier.defaultMachineMaximumTemperature.get(), 1.0f));

		// Setup the processing component.
		registerComponent(processingComponent = new RecipeProcessingComponent<CrucibleRecipe>("ProcessingComponent", StaticPowerConfig.SERVER.crucibleProcessingTime.get(),
				CrucibleRecipe.RECIPE_TYPE, this::getMatchParameters, this::canProcessRecipe, this::moveInputs, this::processingCompleted));

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
		fluidTankComponent.setCanFill(false);
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

	protected RecipeMatchParameters getMatchParameters(RecipeProcessingPhase location) {
		if (location == RecipeProcessingPhase.PROCESSING) {
			return new RecipeMatchParameters(internalInventory.getStackInSlot(0));
		} else {
			return new RecipeMatchParameters(inputInventory.getStackInSlot(0));
		}
	}

	protected ProcessingCheckState moveInputs(CrucibleRecipe recipe) {
		// If this recipe has an item output that we cannot put into the output slot,
		// continue waiting.
		if (recipe.hasItemOutput() && !InventoryUtilities.canFullyInsertStackIntoSlot(outputInventory, 0, recipe.getOutput().getItem())) {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}

		// Check the heat.
		if (heatStorage.getCurrentHeat() < recipe.getMinimumTemperature()) {
			return ProcessingCheckState
					.error("Minimum heat temperature of " + GuiTextUtilities.formatHeatToString(recipe.getMinimumTemperature()).getString() + " has not been reached!");
		}

		// If this recipe has a fluid output that we cannot put into the output tank,
		// continue waiting.
		if (fluidTankComponent.fill(recipe.getOutputFluid(), FluidAction.SIMULATE) != recipe.getOutputFluid().getAmount()) {
			return ProcessingCheckState.outputTankCannotTakeFluid();
		}

		// Set the power usage.
		processingComponent.setProcessingPowerUsage(recipe.getPowerCost());
		processingComponent.setMaxProcessingTime(recipe.getProcessingTime());

		// Transfer the items to the internal inventory.
		transferItemInternally(inputInventory, 0, internalInventory, 0);
		return ProcessingCheckState.ok();
	}

	protected ProcessingCheckState canProcessRecipe(CrucibleRecipe recipe, RecipeProcessingPhase location) {
		// If this recipe has an item output that we cannot put into the output slot,
		// continue waiting.
		if (recipe.hasItemOutput() && !InventoryUtilities.canFullyInsertStackIntoSlot(outputInventory, 0, recipe.getOutput().getItem())) {
			return ProcessingCheckState.outputsCannotTakeRecipe();
		}

		// Check the heat.
		if (heatStorage.getCurrentHeat() < recipe.getMinimumTemperature()) {
			return ProcessingCheckState
					.error("Minimum heat temperature of " + GuiTextUtilities.formatHeatToString(recipe.getMinimumTemperature()).getString() + " has not been reached!");
		}

		// If this recipe has a fluid output that we cannot put into the output tank,
		// continue waiting.
		if (fluidTankComponent.fill(recipe.getOutputFluid(), FluidAction.SIMULATE) != recipe.getOutputFluid().getAmount()) {
			if (!fluidTankComponent.getFluid().isEmpty() && fluidTankComponent.getFluid().getFluid() != recipe.getOutputFluid().getFluid()) {
				return ProcessingCheckState.outputFluidDoesNotMatch();
			} else {
				return ProcessingCheckState.outputTankCannotTakeFluid();
			}
		}
		return ProcessingCheckState.ok();
	}

	protected ProcessingCheckState processingCompleted(CrucibleRecipe recipe) {
		// Insert the outputs
		// Check the dice roll for the output.
		if (recipe.hasItemOutput()) {
			ItemStack output = recipe.getOutput().calculateOutput();
			outputInventory.insertItem(0, output, false);
		}

		// Fill the output tank.
		fluidTankComponent.fill(recipe.getOutputFluid(), FluidAction.EXECUTE);

		// Clear the internal inventory.
		internalInventory.setStackInSlot(0, ItemStack.EMPTY);
		return ProcessingCheckState.ok();
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerCrucible(windowId, inventory, this);
	}
}