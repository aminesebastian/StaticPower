package theking530.staticpower.tileentities.powered.refinery.controller;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import theking530.staticcore.initialization.tileentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.wrappers.refinery.RefineryRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModFluids;
import theking530.staticpower.init.ModTags;
import theking530.staticpower.tileentities.TileEntityMachine;
import theking530.staticpower.tileentities.components.control.AbstractProcesingComponent.ProcessingCheckState;
import theking530.staticpower.tileentities.components.control.RecipeProcessingComponent;
import theking530.staticpower.tileentities.components.control.RecipeProcessingComponent.RecipeProcessingLocation;
import theking530.staticpower.tileentities.components.control.sideconfiguration.DefaultSideConfiguration;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticpower.tileentities.components.fluids.FluidTankComponent;
import theking530.staticpower.tileentities.components.heat.HeatStorageComponent;
import theking530.staticpower.tileentities.components.heat.HeatStorageComponent.HeatManipulationAction;
import theking530.staticpower.tileentities.components.items.InventoryComponent;
import theking530.staticpower.tileentities.components.items.UpgradeInventoryComponent;
import theking530.staticpower.tileentities.powered.refinery.IRefineryBlockEntity;
import theking530.staticpower.utilities.InventoryUtilities;

public class TileEntityRefineryController extends TileEntityMachine {
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityRefineryController> TYPE = new BlockEntityTypeAllocator<TileEntityRefineryController>(
			(type, pos, state) -> new TileEntityRefineryController(pos, state), ModBlocks.RefineryController);

	public final InventoryComponent catalystInventory;
	public final InventoryComponent internalInventory;

	public final FluidTankComponent[] fluidTanks;

	public final UpgradeInventoryComponent upgradesInventory;
	public final RecipeProcessingComponent<RefineryRecipe> processingComponent;

	public final HeatStorageComponent heatStorage;

	private final List<IRefineryBlockEntity> multiBlockEntities;
	private boolean refreshMultiBlock;

	public TileEntityRefineryController(BlockPos pos, BlockState state) {
		super(TYPE, pos, state, StaticPowerTiers.STATIC); // This will be an ADVANCED tier machine but we want to use STATIC tier values.
		multiBlockEntities = new LinkedList<>();

		// Get the tier object.
		StaticPowerTier tier = StaticPowerConfig.getTier(getTier());

		// Setup the inventories.
		registerComponent(catalystInventory = new InventoryComponent("CatalystInventory", 1, MachineSideMode.Input).setShiftClickEnabled(true).setExposedAsCapability(false));
		registerComponent(internalInventory = new InventoryComponent("InternalInventory", 1));
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));

		// Create the energy storage and and set the energy storage upgrade inventory.
		energyStorage.setExposedAsCapability(false);
		energyStorage.setUpgradeInventory(upgradesInventory);

		// Setup the processing component.
		registerComponent(processingComponent = new RecipeProcessingComponent<RefineryRecipe>("ProcessingComponent", RefineryRecipe.RECIPE_TYPE, 1, this::getMatchParameters,
				this::moveInputs, this::canProcessRecipe, this::processingCompleted));

		// Initialize the processing component to work with the redstone control
		// component, upgrade component and energy component.
		processingComponent.setShouldControlBlockState(true);
		processingComponent.setUpgradeInventory(upgradesInventory);
		processingComponent.setEnergyComponent(energyStorage);
		processingComponent.setRedstoneControlComponent(redstoneControlComponent);

		// Setup the input fluid tanks.
		fluidTanks = new FluidTankComponent[5];

		registerComponent(fluidTanks[0] = new FluidTankComponent("FluidTank0", tier.defaultTankCapacity.get(), (fluid) -> {
			FluidStack otherFluid = getInputTank(1).isEmpty() ? ModFluids.WILDCARD : getInputTank(1).getFluid();
			RecipeMatchParameters params = new RecipeMatchParameters().setFluids(fluid, otherFluid).ignoreItems();
			return processingComponent.getRecipe(params).isPresent();
		}).setCapabilityExposedModes(MachineSideMode.Input2).setUpgradeInventory(upgradesInventory));

		registerComponent(fluidTanks[1] = new FluidTankComponent("FluidTank1", tier.defaultTankCapacity.get(), (fluid) -> {
			FluidStack otherFluid = getInputTank(0).isEmpty() ? ModFluids.WILDCARD : getInputTank(0).getFluid();
			RecipeMatchParameters params = new RecipeMatchParameters().setFluids(otherFluid, fluid).ignoreItems();
			return processingComponent.getRecipe(params).isPresent();
		}).setCapabilityExposedModes(MachineSideMode.Input3).setUpgradeInventory(upgradesInventory));

		// Setup the output fluid tanks.
		registerComponent(fluidTanks[2] = new FluidTankComponent("FluidTankOutput0", tier.defaultTankCapacity.get()));
		fluidTanks[2].setCapabilityExposedModes(MachineSideMode.Output);
		fluidTanks[2].setUpgradeInventory(upgradesInventory);
		fluidTanks[2].setAutoSyncPacketsEnabled(true);

		registerComponent(fluidTanks[3] = new FluidTankComponent("FluidTankOutput1", tier.defaultTankCapacity.get()));
		fluidTanks[3].setCapabilityExposedModes(MachineSideMode.Output2);
		fluidTanks[3].setUpgradeInventory(upgradesInventory);
		fluidTanks[3].setAutoSyncPacketsEnabled(true);

		registerComponent(fluidTanks[4] = new FluidTankComponent("FluidTankOutput2", tier.defaultTankCapacity.get()));
		fluidTanks[4].setCapabilityExposedModes(MachineSideMode.Output3);
		fluidTanks[4].setUpgradeInventory(upgradesInventory);
		fluidTanks[4].setAutoSyncPacketsEnabled(true);

		// Add the heat storage and the upgrade inventory to the heat component.
		registerComponent(heatStorage = new HeatStorageComponent("HeatStorageComponent", 350.0f, 1)
				.setCapabiltiyFilter((amount, direction, action) -> action == HeatManipulationAction.COOL).setExposedAsCapability(false).setEnableAutomaticHeatTransfer(false));
		heatStorage.setUpgradeInventory(upgradesInventory);
	}

	@Override
	public void process() {
		if (refreshMultiBlock) {
			refreshMultiBlock();
			refreshMultiBlock = false;
		}
		refreshMultiBlock = true;

		if (!getLevel().isClientSide()) {
			if (processingComponent.isPerformingWork()) {
				heatStorage.getStorage().heat(getHeatGeneration(), false);
			}
		}
	}

	public void requestMultiBlockRefresh() {
		this.refreshMultiBlock = true;
	}

	public double getHeatGeneration() {
		return StaticPowerConfig.SERVER.refineryHeatGeneration.get() * processingComponent.getCalculatedPowerUsageMultipler();
	}

	private void refreshMultiBlock() {
		multiBlockEntities.clear();

		Set<BlockPos> visited = new HashSet<>();
		visited.add(this.getBlockPos());

		Queue<BlockPos> toCheck = new LinkedList<>();
		for (Direction dir : Direction.values()) {
			toCheck.add(getBlockPos().relative(dir));
		}

		while (!toCheck.isEmpty()) {
			BlockPos target = toCheck.remove();
			visited.add(target);

			BlockState state = getLevel().getBlockState(target);
			if (state.is(ModTags.REFINERY_BLOCK)) {
				BlockEntity be = getLevel().getBlockEntity(target);
				if (be != null && be instanceof IRefineryBlockEntity) {
					IRefineryBlockEntity refineryBe = (IRefineryBlockEntity) be;
					multiBlockEntities.add(refineryBe);
					refineryBe.setController(this);
				}

				for (Direction dir : Direction.values()) {
					BlockPos newTarget = target.relative(dir);
					if (!visited.contains(newTarget)) {
						toCheck.add(newTarget);
					}
				}
			}
		}
	}

	public FluidTankComponent getInputTank(int index) {
		if (index <= 1) {
			return getTank(index);
		}
		throw new RuntimeException(String.format("Index: %1$s is not a valid input tank index.", index));
	}

	public FluidTankComponent getOutputTank(int index) {
		if (index <= 2) {
			return getTank(index + 2);
		}
		throw new RuntimeException(String.format("Index: %1$s is not a valid output tank index.", index));
	}

	public FluidTankComponent getTank(int index) {
		return fluidTanks[index];
	}

	public int getTankCount() {
		return fluidTanks.length;
	}

	protected RecipeMatchParameters getMatchParameters(RecipeProcessingLocation location) {
		if (location == RecipeProcessingLocation.INTERNAL) {
			return new RecipeMatchParameters().setItems(internalInventory.getStackInSlot(0)).setFluids(getInputTank(0).getFluid(), getInputTank(1).getFluid());
		} else {
			return new RecipeMatchParameters().setItems(catalystInventory.getStackInSlot(0)).setFluids(getInputTank(0).getFluid(), getInputTank(1).getFluid());
		}
	}

	protected ProcessingCheckState moveInputs(RefineryRecipe recipe) {
		// If this recipe has a fluid output that we cannot put into the output tank,
		// continue waiting.
		if (getOutputTank(0).fill(recipe.getFluidOutput1(), FluidAction.SIMULATE) != recipe.getFluidOutput1().getAmount()) {
			return ProcessingCheckState.outputTankCannotTakeFluid();
		}
		if (getOutputTank(1).fill(recipe.getFluidOutput2(), FluidAction.SIMULATE) != recipe.getFluidOutput2().getAmount()) {
			return ProcessingCheckState.outputTankCannotTakeFluid();
		}
		if (getOutputTank(2).fill(recipe.getFluidOutput3(), FluidAction.SIMULATE) != recipe.getFluidOutput3().getAmount()) {
			return ProcessingCheckState.outputTankCannotTakeFluid();
		}

		transferItemInternally(recipe.getCatalyst().getCount(), catalystInventory, 0, internalInventory, 0);

		// Set the power usage.
		processingComponent.setProcessingPowerUsage(recipe.getPowerCost());
		processingComponent.setMaxProcessingTime(recipe.getProcessingTime());

		return ProcessingCheckState.ok();
	}

	protected ProcessingCheckState canProcessRecipe(RefineryRecipe recipe) {
		// If this recipe has a fluid output that we cannot put into the output tank,
		// continue waiting.
		if (getOutputTank(0).fill(recipe.getFluidOutput1(), FluidAction.SIMULATE) != recipe.getFluidOutput1().getAmount()) {
			return ProcessingCheckState.outputTankCannotTakeFluid();
		}
		if (getOutputTank(1).fill(recipe.getFluidOutput2(), FluidAction.SIMULATE) != recipe.getFluidOutput2().getAmount()) {
			return ProcessingCheckState.outputTankCannotTakeFluid();
		}
		if (getOutputTank(2).fill(recipe.getFluidOutput3(), FluidAction.SIMULATE) != recipe.getFluidOutput3().getAmount()) {
			return ProcessingCheckState.outputTankCannotTakeFluid();
		}
		if (!heatStorage.getStorage().canFullyAbsorbHeat(this.getHeatGeneration())) {
			return ProcessingCheckState.error("Not enough heat capacity (Requires " + GuiTextUtilities.formatHeatToString(getHeatGeneration()).getString() + ")");
		}
		return ProcessingCheckState.ok();
	}

	protected ProcessingCheckState processingCompleted(RefineryRecipe recipe) {
		// Output the mixed fluid.
		getOutputTank(0).fill(recipe.getFluidOutput1(), FluidAction.EXECUTE);
		getOutputTank(1).fill(recipe.getFluidOutput2(), FluidAction.EXECUTE);
		getOutputTank(2).fill(recipe.getFluidOutput3(), FluidAction.EXECUTE);

		// Drain the fluid.
		getInputTank(0).drain(recipe.getPrimaryFluidInput().getAmount(), FluidAction.EXECUTE);
		getInputTank(1).drain(recipe.getSecondaryFluidInput().getAmount(), FluidAction.EXECUTE);

		// Clear the internal inventory.
		InventoryUtilities.clearInventory(internalInventory);
		return ProcessingCheckState.ok();
	}

	@Override
	protected boolean isValidSideConfiguration(BlockSide side, MachineSideMode mode) {
		return mode == MachineSideMode.Never;
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerRefineryController(windowId, inventory, this);
	}

	@Override
	protected DefaultSideConfiguration getDefaultSideConfiguration() {
		return SideConfigurationComponent.ALL_SIDES_NEVER;
	}
}