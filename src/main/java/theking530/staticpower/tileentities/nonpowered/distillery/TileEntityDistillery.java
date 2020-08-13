package theking530.staticpower.tileentities.nonpowered.distillery;

import java.util.Optional;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerRecipeRegistry;
import theking530.staticpower.data.crafting.wrappers.distilation.DistillationRecipe;
import theking530.staticpower.init.ModTileEntityTypes;
import theking530.staticpower.tileentities.TileEntityConfigurable;
import theking530.staticpower.tileentities.components.control.MachineProcessingComponent;
import theking530.staticpower.tileentities.components.fluids.FluidInputServoComponent;
import theking530.staticpower.tileentities.components.fluids.FluidOutputServoComponent;
import theking530.staticpower.tileentities.components.fluids.FluidTankComponent;
import theking530.staticpower.tileentities.components.heat.HeatStorageComponent;
import theking530.staticpower.tileentities.components.items.InventoryComponent;
import theking530.staticpower.tileentities.utilities.MachineSideMode;

public class TileEntityDistillery extends TileEntityConfigurable {
	public static final int DEFAULT_PROCESSING_TIME = 5;
	public static final int DEFAULT_TANK_SIZE = 5000;
	public static final float DEFAULT_EVAPORATION_HEAT = 200.0f;

	public final InventoryComponent upgradesInventory;
	public final MachineProcessingComponent processingComponent;
	public final FluidTankComponent inputTankComponent;
	public final FluidTankComponent outputTankComponent;
	public final HeatStorageComponent heatStorage;

	public TileEntityDistillery() {
		super(ModTileEntityTypes.DISTILLERY);

		registerComponent(upgradesInventory = new InventoryComponent("UpgradeInventory", 3, MachineSideMode.Never));
		registerComponent(processingComponent = new MachineProcessingComponent("ProcessingComponent", DEFAULT_PROCESSING_TIME, this::canProcess, this::canProcess, this::processingCompleted, true)
				.setShouldControlBlockState(true));

		registerComponent(inputTankComponent = new FluidTankComponent("InputFluidTank", DEFAULT_TANK_SIZE).setCapabilityExposedModes(MachineSideMode.Input));
		inputTankComponent.setCanDrain(false);

		registerComponent(outputTankComponent = new FluidTankComponent("OutputFluidTank", DEFAULT_TANK_SIZE).setCapabilityExposedModes(MachineSideMode.Output));
		outputTankComponent.setCanFill(false);

		registerComponent(new FluidInputServoComponent("FluidInputServoComponent", 100, inputTankComponent, MachineSideMode.Input));
		registerComponent(new FluidOutputServoComponent("FluidOutputServoComponent", 100, outputTankComponent, MachineSideMode.Output));

		registerComponent(heatStorage = new HeatStorageComponent("HeatStorageComponent", 1000.0f, 1.0f));
	}

	protected boolean canProcess() {
		if (hasValidInput()) {
			DistillationRecipe recipe = getRecipe(inputTankComponent.getFluid()).orElse(null);
			return redstoneControlComponent.passesRedstoneCheck() && (outputTankComponent.getFluid().isEmpty() || outputTankComponent.getFluid().isFluidEqual(recipe.getOutputFluid()))
					&& outputTankComponent.getFluidAmount() + recipe.getOutputFluid().getAmount() <= outputTankComponent.getCapacity()
					&& heatStorage.getStorage().getCurrentHeat() >= recipe.getRequiredHeat();
		}
		return false;
	}

	/**
	 * Once the processing is completed, place the output in the output slot (if
	 * possible). If not, return false. This method will continue to be called until
	 * true is returned.
	 * 
	 * @return
	 */
	protected boolean processingCompleted() {
		if (!getWorld().isRemote) {
			// If we have an item in the internal inventory, but not a valid output, just
			// return true. It is possible that a recipe was modified and no longer is
			// valid.
			if (!hasValidInput()) {
				return true;
			}

			// Get recipe.
			DistillationRecipe recipe = getRecipe(inputTankComponent.getFluid()).orElse(null);

			// If we don;t have enough heat, return early.
			if (heatStorage.getStorage().getCurrentHeat() < recipe.getRequiredHeat()) {
				return false;
			}

			// If we can't store the filled output in the output slot, return false.
			if (!(outputTankComponent.getFluid().isEmpty() || outputTankComponent.getFluid().isFluidEqual(recipe.getOutputFluid()))
					|| outputTankComponent.getFluidAmount() + recipe.getOutputFluid().getAmount() > outputTankComponent.getCapacity()) {
				return false;
			}

			// Drain the input fluid.
			inputTankComponent.drain(recipe.getInputFluid().getAmount(), FluidAction.EXECUTE);

			// Fill the output fluid.
			outputTankComponent.fill(recipe.getOutputFluid(), FluidAction.EXECUTE);

			markTileEntityForSynchronization();
			return true;

		}
		return false;
	}

	@Override
	public void process() {
		// Use power if we are processing.
		if (processingComponent.isPerformingWork()) {
			// Get recipe.
			DistillationRecipe recipe = getRecipe(inputTankComponent.getFluid()).orElse(null);
			// Cool the heat storage.
			if (recipe != null) {
				heatStorage.getStorage().cool(recipe.getRequiredHeat(), false);
			}
		}
	}

	public boolean hasValidInput() {
		return isValidInput(inputTankComponent.getFluid());
	}

	public boolean isValidInput(FluidStack stack) {
		return getRecipe(stack).isPresent();
	}

	protected Optional<DistillationRecipe> getRecipe(FluidStack stack) {
		return StaticPowerRecipeRegistry.getRecipe(DistillationRecipe.RECIPE_TYPE, new RecipeMatchParameters(stack));
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerDistillery(windowId, inventory, this);
	}
}
