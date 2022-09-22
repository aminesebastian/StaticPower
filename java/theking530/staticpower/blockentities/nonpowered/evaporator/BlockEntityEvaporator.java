package theking530.staticpower.blockentities.nonpowered.evaporator;

import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.api.heat.IHeatStorage.HeatTransferAction;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blockentities.BlockEntityConfigurable;
import theking530.staticpower.blockentities.components.control.MachineProcessingComponent;
import theking530.staticpower.blockentities.components.control.AbstractProcesingComponent.ProcessingCheckState;
import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.blockentities.components.fluids.FluidInputServoComponent;
import theking530.staticpower.blockentities.components.fluids.FluidOutputServoComponent;
import theking530.staticpower.blockentities.components.fluids.FluidTankComponent;
import theking530.staticpower.blockentities.components.heat.HeatStorageComponent;
import theking530.staticpower.blockentities.components.items.UpgradeInventoryComponent;
import theking530.staticpower.client.rendering.blockentity.BlockEntityRenderEvaporator;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerRecipeRegistry;
import theking530.staticpower.data.crafting.wrappers.evaporation.EvaporatorRecipe;
import theking530.staticpower.init.ModBlocks;

public class BlockEntityEvaporator extends BlockEntityConfigurable {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityEvaporator> TYPE = new BlockEntityTypeAllocator<BlockEntityEvaporator>(
			(type, pos, state) -> new BlockEntityEvaporator(pos, state), ModBlocks.Evaporator);

	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setTileEntitySpecialRenderer(BlockEntityRenderEvaporator::new);
		}
	}

	public static final int DEFAULT_PROCESSING_TIME = 5;
	public static final int DEFAULT_TANK_SIZE = 5000;
	public static final int DEFAULT_EVAPORATION_HEAT = 10;

	public final UpgradeInventoryComponent upgradesInventory;
	public final MachineProcessingComponent processingComponent;
	public final FluidTankComponent inputTankComponent;
	public final FluidTankComponent outputTankComponent;
	public final HeatStorageComponent heatStorage;

	public BlockEntityEvaporator(BlockPos pos, BlockState state) {
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
		inputTankComponent.setAutoSyncPacketsEnabled(true);

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
			EvaporatorRecipe recipe = getRecipe(inputTankComponent.getFluid(), false).orElse(null);
			// Check if the output fluid matches the already exists fluid if one exists.
			if (!outputTankComponent.getFluid().isEmpty() && !outputTankComponent.getFluid().isFluidEqual(recipe.getOutputFluid())) {
				return ProcessingCheckState.outputFluidDoesNotMatch();
			}
			// Check the fluid capacity.
			if (outputTankComponent.getFluidAmount() + recipe.getOutputFluid().getAmount() > outputTankComponent.getCapacity()) {
				return ProcessingCheckState.fluidOutputFull();
			}
			// Check the heat level.
			if (heatStorage.getCurrentHeat() < recipe.getRequiredHeat()) {
				return ProcessingCheckState.error("Heat level is not high enough!");
			}

			// If all the checks pass, return ok.
			return ProcessingCheckState.ok();
		} else {
			return ProcessingCheckState.skip();
		}
	}

	protected void processingStarted() {
		EvaporatorRecipe recipe = getRecipe(inputTankComponent.getFluid(), false).orElse(null);
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
		EvaporatorRecipe recipe = getRecipe(inputTankComponent.getFluid(), false).orElse(null);

		// If we don;t have enough heat, return early.
		if (heatStorage.getCurrentHeat() < recipe.getRequiredHeat()) {
			return ProcessingCheckState.error("Not enough Heat!");
		}

		// If we can't store the filled output in the output slot, return false.
		if (!(outputTankComponent.getFluid().isEmpty() || outputTankComponent.getFluid().isFluidEqual(recipe.getOutputFluid()))
				|| outputTankComponent.getFluidAmount() + recipe.getOutputFluid().getAmount() > outputTankComponent.getCapacity()) {
			return ProcessingCheckState.error("Output tank full!");
		}

		// Drain the input fluid.
		inputTankComponent.drain(recipe.getInputFluid().getAmount(), FluidAction.EXECUTE);

		// Fill the output fluid.
		outputTankComponent.fill(recipe.getOutputFluid(), FluidAction.EXECUTE);

		// Use the heat.
		heatStorage.cool(DEFAULT_EVAPORATION_HEAT, HeatTransferAction.EXECUTE);
		return ProcessingCheckState.ok();
	}

	public boolean isValidInput(FluidStack stack, boolean ignoreAmount) {
		return getRecipe(stack, ignoreAmount).isPresent();
	}

	protected Optional<EvaporatorRecipe> getRecipe(FluidStack stack, boolean ignoreAmount) {
		RecipeMatchParameters matchParams = new RecipeMatchParameters(stack);
		if (ignoreAmount) {
			matchParams.ignoreFluidAmounts();
		}
		return StaticPowerRecipeRegistry.getRecipe(EvaporatorRecipe.RECIPE_TYPE, matchParams);
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerEvaporator(windowId, inventory, this);
	}
}
