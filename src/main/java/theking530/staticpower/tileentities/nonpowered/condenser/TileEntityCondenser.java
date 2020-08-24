package theking530.staticpower.tileentities.nonpowered.condenser;

import java.util.Optional;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerRecipeRegistry;
import theking530.staticpower.data.crafting.wrappers.condensation.CondensationRecipe;
import theking530.staticpower.init.ModTileEntityTypes;
import theking530.staticpower.tileentities.TileEntityConfigurable;
import theking530.staticpower.tileentities.components.control.AbstractProcesingComponent.ProcessingCheckState;
import theking530.staticpower.tileentities.components.control.MachineProcessingComponent;
import theking530.staticpower.tileentities.components.fluids.FluidInputServoComponent;
import theking530.staticpower.tileentities.components.fluids.FluidOutputServoComponent;
import theking530.staticpower.tileentities.components.fluids.FluidTankComponent;
import theking530.staticpower.tileentities.components.heat.HeatStorageComponent;
import theking530.staticpower.tileentities.components.items.UpgradeInventoryComponent;
import theking530.staticpower.tileentities.nonpowered.evaporator.TileEntityEvaporator;
import theking530.staticpower.tileentities.utilities.MachineSideMode;

public class TileEntityCondenser extends TileEntityConfigurable {
	public static final int DEFAULT_PROCESSING_TIME = 5;
	public static final int DEFAULT_TANK_SIZE = 5000;

	public final UpgradeInventoryComponent upgradesInventory;
	public final MachineProcessingComponent processingComponent;
	public final FluidTankComponent inputTankComponent;
	public final FluidTankComponent outputTankComponent;
	public final HeatStorageComponent heatStorage;

	public TileEntityCondenser() {
		super(ModTileEntityTypes.CONDENSER);

		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));
		registerComponent(processingComponent = new MachineProcessingComponent("ProcessingComponent", DEFAULT_PROCESSING_TIME, this::canProcess, this::canProcess, this::processingCompleted, true)
				.setShouldControlBlockState(true).setProcessingStartedCallback(this::processingStarted).setUpgradeInventory(upgradesInventory).setRedstoneControlComponent(redstoneControlComponent));

		registerComponent(inputTankComponent = new FluidTankComponent("InputFluidTank", DEFAULT_TANK_SIZE, (fluidStack) -> {
			return isValidInput(fluidStack);
		}).setCapabilityExposedModes(MachineSideMode.Input).setUpgradeInventory(upgradesInventory));
		inputTankComponent.setCanDrain(false);

		registerComponent(outputTankComponent = new FluidTankComponent("OutputFluidTank", DEFAULT_TANK_SIZE).setCapabilityExposedModes(MachineSideMode.Output).setUpgradeInventory(upgradesInventory));
		outputTankComponent.setCanFill(false);

		registerComponent(new FluidInputServoComponent("FluidInputServoComponent", 100, inputTankComponent, MachineSideMode.Input));
		registerComponent(new FluidOutputServoComponent("FluidOutputServoComponent", 100, outputTankComponent, MachineSideMode.Output));

		registerComponent(heatStorage = new HeatStorageComponent("HeatStorageComponent", 500.0f, 1.0f));
	}

	protected ProcessingCheckState canProcess() {
		// Check if we have a valid input. If not, just skip.
		if (isValidInput(inputTankComponent.getFluid())) {
			// Get the recipe.
			CondensationRecipe recipe = getRecipe(inputTankComponent.getFluid()).orElse(null);

			// Check if the output fluid matches the already exists fluid if one exists.
			if (!outputTankComponent.getFluid().isEmpty() && !outputTankComponent.getFluid().isFluidEqual(recipe.getOutputFluid())) {
				return ProcessingCheckState.outputFluidDoesNotMatch();
			}
			// Check the fluid capacity.
			if (outputTankComponent.getFluidAmount() + recipe.getOutputFluid().getAmount() > outputTankComponent.getCapacity()) {
				return ProcessingCheckState.outputTankCannotTakeFluid();
			}

			// Check the heat level.
			if (heatStorage.getStorage().getCurrentHeat() + TileEntityEvaporator.DEFAULT_EVAPORATION_HEAT > heatStorage.getStorage().getMaximumHeat()) {
				return ProcessingCheckState.error("Machine is too hot!");
			}

			// If all the checks pass, return ok.
			return ProcessingCheckState.ok();
		} else {
			return ProcessingCheckState.skip();
		}
	}

	protected void processingStarted() {
		CondensationRecipe recipe = getRecipe(inputTankComponent.getFluid()).orElse(null);
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
		if (!isValidInput(inputTankComponent.getFluid())) {
			return ProcessingCheckState.ok();
		}

		// Get recipe.
		CondensationRecipe recipe = getRecipe(inputTankComponent.getFluid()).orElse(null);

		// Check the heat level.
		if (heatStorage.getStorage().getCurrentHeat() + TileEntityEvaporator.DEFAULT_EVAPORATION_HEAT > heatStorage.getStorage().getMaximumHeat()) {
			return ProcessingCheckState.error("Machine is too hot!");
		}
		
		// If we can't store the filled output in the output slot, return false.
		if (!(outputTankComponent.getFluid().isEmpty() || outputTankComponent.getFluid().isFluidEqual(recipe.getOutputFluid()))
				|| outputTankComponent.getFluidAmount() + recipe.getOutputFluid().getAmount() > outputTankComponent.getCapacity()) {
			return ProcessingCheckState.fluidOutputFull();
		}

		// Drain the input fluid.
		inputTankComponent.drain(recipe.getInputFluid().getAmount(), FluidAction.EXECUTE);

		// Fill the output fluid.
		outputTankComponent.fill(recipe.getOutputFluid(), FluidAction.EXECUTE);

		// Use the heat.
		heatStorage.getStorage().heat(TileEntityEvaporator.DEFAULT_EVAPORATION_HEAT, false);

		markTileEntityForSynchronization();
		return ProcessingCheckState.ok();
	}

	public boolean isValidInput(FluidStack stack) {
		return getRecipe(stack).isPresent();
	}

	protected Optional<CondensationRecipe> getRecipe(FluidStack stack) {
		return StaticPowerRecipeRegistry.getRecipe(CondensationRecipe.RECIPE_TYPE, new RecipeMatchParameters(stack));
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerCondenser(windowId, inventory, this);
	}
}
