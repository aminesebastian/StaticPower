package theking530.staticpower.blockentities.nonpowered.condenser;

import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import theking530.api.heat.IHeatStorage.HeatTransferAction;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.blockentities.BlockEntityConfigurable;
import theking530.staticpower.blockentities.components.control.processing.MachineProcessingComponent;
import theking530.staticpower.blockentities.components.control.processing.ProcessingCheckState;
import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.blockentities.components.fluids.FluidInputServoComponent;
import theking530.staticpower.blockentities.components.fluids.FluidOutputServoComponent;
import theking530.staticpower.blockentities.components.fluids.FluidTankComponent;
import theking530.staticpower.blockentities.components.heat.HeatStorageComponent;
import theking530.staticpower.blockentities.components.items.UpgradeInventoryComponent;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerRecipeRegistry;
import theking530.staticpower.data.crafting.wrappers.condensation.CondensationRecipe;
import theking530.staticpower.init.ModBlocks;

public class BlockEntityCondenser extends BlockEntityConfigurable {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityCondenser> TYPE = new BlockEntityTypeAllocator<>((type, pos, state) -> new BlockEntityCondenser(pos, state),
			ModBlocks.Condenser);

	public static final int DEFAULT_PROCESSING_TIME = 5;
	public static final int DEFAULT_HEAT_GENERATION = 50;

	public final UpgradeInventoryComponent upgradesInventory;
	public final MachineProcessingComponent processingComponent;
	public final FluidTankComponent inputTankComponent;
	public final FluidTankComponent outputTankComponent;
	public final HeatStorageComponent heatStorage;

	public BlockEntityCondenser(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);

		// Get the tier.
		StaticPowerTier tierObject = getTierObject();

		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));
		registerComponent(processingComponent = new MachineProcessingComponent("ProcessingComponent", DEFAULT_PROCESSING_TIME, this::canProcess, this::canProcess,
				this::processingCompleted, true).setShouldControlBlockState(true).setProcessingStartedCallback(this::processingStarted).setUpgradeInventory(upgradesInventory)
				.setRedstoneControlComponent(redstoneControlComponent));

		registerComponent(inputTankComponent = new FluidTankComponent("InputFluidTank", tierObject.defaultTankCapacity.get(), (fluidStack) -> {
			return isValidInput(fluidStack, true);
		}).setCapabilityExposedModes(MachineSideMode.Input).setUpgradeInventory(upgradesInventory));
		inputTankComponent.setCanDrain(false);

		registerComponent(outputTankComponent = new FluidTankComponent("OutputFluidTank", tierObject.defaultTankCapacity.get()).setCapabilityExposedModes(MachineSideMode.Output)
				.setUpgradeInventory(upgradesInventory));
		outputTankComponent.setCanFill(false);

		registerComponent(new FluidInputServoComponent("FluidInputServoComponent", 100, inputTankComponent, MachineSideMode.Input));
		registerComponent(new FluidOutputServoComponent("FluidOutputServoComponent", 100, outputTankComponent, MachineSideMode.Output));

		registerComponent(heatStorage = new HeatStorageComponent("HeatStorageComponent", tierObject.defaultMachineOverheatTemperature.get(),
				tierObject.defaultMachineMaximumTemperature.get(), 1.0f));
	}

	protected ProcessingCheckState canProcess() {
		// Check if we have a valid input. If not, just skip.
		if (isValidInput(inputTankComponent.getFluid(), false)) {
			// Get the recipe.
			CondensationRecipe recipe = getRecipe(inputTankComponent.getFluid(), false).orElse(null);

			// Check if the output fluid matches the already exists fluid if one exists.
			if (!outputTankComponent.getFluid().isEmpty() && !outputTankComponent.getFluid().isFluidEqual(recipe.getOutputFluid())) {
				return ProcessingCheckState.outputFluidDoesNotMatch();
			}
			// Check the fluid capacity.
			if (outputTankComponent.getFluidAmount() + recipe.getOutputFluid().getAmount() > outputTankComponent.getCapacity()) {
				return ProcessingCheckState.fluidOutputFull();
			}

			// Check the heat level.
			if (heatStorage.getCurrentHeat() + recipe.getHeatGeneration() > heatStorage.getOverheatThreshold()) {
				return ProcessingCheckState.error("Machine is too hot!");
			}

			// If all the checks pass, return ok.
			return ProcessingCheckState.ok();
		} else {
			return ProcessingCheckState.skip();
		}
	}

	protected void processingStarted() {
		CondensationRecipe recipe = getRecipe(inputTankComponent.getFluid(), false).orElse(null);
		if (recipe != null) {
			this.processingComponent.setMaxProcessingTime(recipe.getProcessingTime());
		}
	}

	/**
	 * Once the processing is completed, place the output in the output slot (if
	 * possible). If not, return false. This method will continue to be called until
	 * true is returned.
	 * 
	 * @return
	 */
	protected ProcessingCheckState processingCompleted() {

		// If we have an item in the internal inventory, but not a valid output, just
		// return true. It is possible that a recipe was modified and no longer is
		// valid.
		if (!isValidInput(inputTankComponent.getFluid(), false)) {
			return ProcessingCheckState.ok();
		}

		// Get recipe.
		CondensationRecipe recipe = getRecipe(inputTankComponent.getFluid(), false).orElse(null);

		// Check the heat level.
		if (heatStorage.getCurrentHeat() + recipe.getHeatGeneration() > heatStorage.getOverheatThreshold()) {
			return ProcessingCheckState.error("Machine is too hot!");
		}

		// If we can't store the filled output in the output slot, return false.
		if (outputTankComponent.getFluidAmount() + recipe.getOutputFluid().getAmount() > outputTankComponent.getCapacity()) {
			return ProcessingCheckState.fluidOutputFull();
		}

		if (!outputTankComponent.getFluid().isEmpty() && !outputTankComponent.getFluid().isFluidEqual(recipe.getOutputFluid())) {
			return ProcessingCheckState.outputFluidDoesNotMatch();
		}

		// Drain the input fluid.
		inputTankComponent.drain(recipe.getInputFluid().getAmount(), FluidAction.EXECUTE);

		// Fill the output fluid.
		outputTankComponent.fill(recipe.getOutputFluid(), FluidAction.EXECUTE);

		// Use the heat.
		heatStorage.heat(recipe.getHeatGeneration(), HeatTransferAction.EXECUTE);
		return ProcessingCheckState.ok();
	}

	public boolean isValidInput(FluidStack stack, boolean ignoreAmount) {
		return getRecipe(stack, ignoreAmount).isPresent();
	}

	protected Optional<CondensationRecipe> getRecipe(FluidStack stack, boolean ignoreAmount) {
		RecipeMatchParameters matchParams = new RecipeMatchParameters(stack);
		if (ignoreAmount) {
			matchParams.ignoreFluidAmounts();
		}
		return StaticPowerRecipeRegistry.getRecipe(CondensationRecipe.RECIPE_TYPE, matchParams);
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerCondenser(windowId, inventory, this);
	}
}
