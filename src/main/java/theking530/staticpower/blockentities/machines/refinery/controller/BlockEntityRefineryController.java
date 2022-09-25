package theking530.staticpower.blockentities.machines.refinery.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import theking530.api.heat.IHeatStorage.HeatTransferAction;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticcore.utilities.SDMath;
import theking530.staticcore.utilities.Vector4D;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blockentities.BlockEntityMachine;
import theking530.staticpower.blockentities.components.control.RecipeProcessingComponent;
import theking530.staticpower.blockentities.components.control.AbstractProcesingComponent.ProcessingCheckState;
import theking530.staticpower.blockentities.components.control.RecipeProcessingComponent.RecipeProcessingPhase;
import theking530.staticpower.blockentities.components.control.sideconfiguration.DefaultSideConfiguration;
import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticpower.blockentities.components.fluids.FluidTankComponent;
import theking530.staticpower.blockentities.components.heat.HeatStorageComponent;
import theking530.staticpower.blockentities.components.heat.HeatStorageComponent.HeatManipulationAction;
import theking530.staticpower.blockentities.components.items.InventoryComponent;
import theking530.staticpower.blockentities.components.items.UpgradeInventoryComponent;
import theking530.staticpower.blockentities.components.loopingsound.LoopingSoundComponent;
import theking530.staticpower.blockentities.machines.refinery.IRefineryBlockEntity;
import theking530.staticpower.blockentities.machines.refinery.boiler.BlockRefineryBoiler;
import theking530.staticpower.blockentities.machines.refinery.heatvent.BlockEntityRefineryHeatVent;
import theking530.staticpower.blockentities.machines.refinery.tower.BlockRefineryTower;
import theking530.staticpower.blockentities.machines.refinery.tower.BlockRefineryTower.TowerPiece;
import theking530.staticpower.blockentities.utilities.MultiBlockCache;
import theking530.staticpower.blockentities.utilities.MultiBlockWrapper;
import theking530.staticpower.blocks.tileentity.StaticPowerMachineBlock;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.wrappers.refinery.RefineryRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModFluids;
import theking530.staticpower.init.ModTags;
import theking530.staticpower.utilities.InventoryUtilities;

public class BlockEntityRefineryController extends BlockEntityMachine {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityRefineryController> TYPE = new BlockEntityTypeAllocator<BlockEntityRefineryController>(
			(type, pos, state) -> new BlockEntityRefineryController(pos, state), ModBlocks.RefineryController);
	public static final int MAX_EFFICIENCY_TOWER_HEIGHT = 4;

	public final InventoryComponent catalystInventory;
	public final InventoryComponent internalInventory;
	public final LoopingSoundComponent generatingSoundComponent;
	public final UpgradeInventoryComponent upgradesInventory;
	public final RecipeProcessingComponent<RefineryRecipe> processingComponent;
	public final HeatStorageComponent heatStorage;
	public final FluidTankComponent[] fluidTanks;
	private float currentProcessingProductivity;

	private final MultiBlockCache<IRefineryBlockEntity> multiBlockCache;
	private boolean refreshMultiBlock;

	public BlockEntityRefineryController(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);
		multiBlockCache = new MultiBlockCache<>(this::isValidForMultiBlock, IRefineryBlockEntity.class);

		// Get the tier object.
		StaticPowerTier tier = getTierObject();
		registerComponent(generatingSoundComponent = new LoopingSoundComponent("GeneratingSoundComponent", 20));

		// Setup the inventories.
		registerComponent(catalystInventory = new InventoryComponent("CatalystInventory", 1, MachineSideMode.Input).setShiftClickEnabled(true).setExposedAsCapability(false));
		registerComponent(internalInventory = new InventoryComponent("InternalInventory", 1));
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));

		// Create the energy storage and and set the energy storage upgrade inventory.
		powerStorage.setExposeAsCapability(false);
		powerStorage.setUpgradeInventory(upgradesInventory);

		// Setup the processing component.
		registerComponent(processingComponent = new RecipeProcessingComponent<RefineryRecipe>("ProcessingComponent", 1, RecipeProcessingComponent.MOVE_TIME, RefineryRecipe.RECIPE_TYPE,
				this::getMatchParameters, this::canProcessRecipe, this::moveInputs, this::processingCompleted));

		// Initialize the processing component to work with the redstone control
		// component, upgrade component and energy component.
		processingComponent.setShouldControlBlockState(true);
		processingComponent.setUpgradeInventory(upgradesInventory);
		processingComponent.setRedstoneControlComponent(redstoneControlComponent);

		// Setup the input fluid tanks.
		fluidTanks = new FluidTankComponent[5];

		registerComponent(fluidTanks[0] = new FluidTankComponent("FluidTank0", tier.defaultTankCapacity.get(), (fluid) -> {
			FluidStack otherFluid = getInputTank(1).isEmpty() ? ModFluids.WILDCARD : getInputTank(1).getFluid();
			RecipeMatchParameters params = new RecipeMatchParameters().setFluids(fluid, otherFluid).ignoreItems().ignoreFluidAmounts();
			return processingComponent.getRecipeMatchingParameters(params).isPresent();
		}).setCapabilityExposedModes(MachineSideMode.Input2).setUpgradeInventory(upgradesInventory));

		registerComponent(fluidTanks[1] = new FluidTankComponent("FluidTank1", tier.defaultTankCapacity.get(), (fluid) -> {
			FluidStack otherFluid = getInputTank(0).isEmpty() ? ModFluids.WILDCARD : getInputTank(0).getFluid();
			RecipeMatchParameters params = new RecipeMatchParameters().setFluids(otherFluid, fluid).ignoreItems().ignoreFluidAmounts();
			return processingComponent.getRecipeMatchingParameters(params).isPresent();
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
		registerComponent(
				heatStorage = new HeatStorageComponent("HeatStorageComponent", 0, tier.defaultMachineOverheatTemperature.get(), tier.defaultMachineMaximumTemperature.get(), 1)
						.setCapabiltiyFilter((amount, direction, action) -> action == HeatManipulationAction.COOL).setExposedAsCapability(false)
						.setEnableAutomaticHeatTransfer(false).setMeltdownRecoveryTicks(100));
		heatStorage.setUpgradeInventory(upgradesInventory);
	}

	@Override
	public void process() {
		if (refreshMultiBlock) {
			multiBlockCache.refresh(getLevel(), getBlockPos());
			for (MultiBlockWrapper<IRefineryBlockEntity> wrapper : multiBlockCache) {
				if (wrapper.hasBlockEntity()) {
					wrapper.entity.setController(this);
				}
			}
			refreshMultiBlock = false;
		}
		refreshMultiBlock = true;

		if (!getLevel().isClientSide()) {
			if (redstoneControlComponent.passesRedstoneCheck() && getProductivity() > 0) {
				double powerCost = StaticPowerConfig.SERVER.refineryPowerUsage.get();
				boolean shouldHeat = processingComponent.isPerformingWork() || !heatStorage.isRecoveringFromMeltdown();
				if (powerStorage.canSupplyPower(powerCost) && shouldHeat) {
					powerStorage.drainPower(powerCost, false);
					heatStorage.heat(getHeatGeneration(), HeatTransferAction.EXECUTE);
				}
			}

			// Handle sounds.
			if (processingComponent.getIsOnBlockState()) {
				generatingSoundComponent.startPlayingSound(SoundEvents.MINECART_RIDING.getRegistryName(), SoundSource.BLOCKS, 0.25f, 0.5f, getBlockPos(), 64);
			} else {
				generatingSoundComponent.stopPlayingSound();
			}

			updateMultiblockBlockStates(processingComponent.getIsOnBlockState());
		} else {
			if (processingComponent.getIsOnBlockState() && SDMath.diceRoll(0.5f)) {
				renderParticleEffects();
			}
		}
	}

	private void updateMultiblockBlockStates(boolean isOn) {
		for (MultiBlockWrapper<IRefineryBlockEntity> wrapper : multiBlockCache) {
			BlockState multiBlockState = wrapper.getBlockState(getLevel());
			if (multiBlockState.hasProperty(StaticPowerMachineBlock.IS_ON)) {
				boolean onState = multiBlockState.getValue(StaticPowerMachineBlock.IS_ON);
				if (onState != isOn) {
					getLevel().setBlock(wrapper.position, multiBlockState.setValue(StaticPowerMachineBlock.IS_ON, isOn), 2);
				}
			}
		}
	}

	private void renderParticleEffects() {
		Vector4D randomVector = SDMath.getRandomVectorOffset();
		for (MultiBlockWrapper<IRefineryBlockEntity> wrapper : multiBlockCache) {
			BlockState multiBlockState = wrapper.getBlockState(getLevel());

			if (multiBlockState.getBlock() instanceof BlockRefineryTower && multiBlockState.hasProperty(BlockRefineryTower.TOWER_POSITION)) {
				TowerPiece position = multiBlockState.getValue(BlockRefineryTower.TOWER_POSITION);
				if (position == TowerPiece.TOP || position == TowerPiece.FULL) {
					getLevel().addParticle(ParticleTypes.LARGE_SMOKE, wrapper.position.getX() + 0.5f + (randomVector.getX() * 0.15f), wrapper.position.getY() + 1f,
							wrapper.position.getZ() + 0.5f + (randomVector.getZ() * 0.15f), 0.0f, 0.005f, 0.0f);
					if (SDMath.diceRoll(0.5f)) {
						getLevel().addParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, wrapper.position.getX() + 0.5f + (randomVector.getX() * 0.15f), wrapper.position.getY() + 1f,
								wrapper.position.getZ() + 0.5f + (randomVector.getZ() * 0.15f), 0.0f, 0.05f, 0.0f);
					}
				}
			} else if (wrapper.entity instanceof BlockEntityRefineryHeatVent) {
				if (SDMath.diceRoll(0.1f)) {
					randomVector.setX(randomVector.getX() * 0.55f);
					randomVector.setZ(randomVector.getZ() * 0.55f);
					getLevel().addParticle(ParticleTypes.SMOKE, wrapper.position.getX() + 0.5f + randomVector.getX(), wrapper.position.getY() + 0.5f + randomVector.getY() / 2,
							wrapper.position.getZ() + 0.5f + randomVector.getZ(), 0.0f, 0.0f, 0.0f);
				}
			}
		}
	}

	private boolean isValidForMultiBlock(BlockPos pos, BlockState state, BlockEntity be) {
		return state.is(ModTags.REFINERY_BLOCK);
	}

	public void requestMultiBlockRefresh() {
		this.refreshMultiBlock = true;
	}

	public int getHeatUsage() {
		int heatUse = StaticPowerConfig.SERVER.refineryHeatUse.get();
		Optional<RefineryRecipe> recipe = processingComponent.getCurrentOrPendingRecipe();
		if (recipe.isPresent()) {
			heatUse = recipe.get().getProcessingSection().getHeatUse();
		}
		return (int) (heatUse * processingComponent.getCalculatedHeatGenerationMultiplier());
	}

	public int getMinimumHeat() {
		Optional<RefineryRecipe> recipe = processingComponent.getCurrentOrPendingRecipe();
		if (recipe.isPresent()) {
			return recipe.get().getProcessingSection().getHeatUse();
		}
		return StaticPowerConfig.SERVER.refineryMinimumHeat.get();
	}

	public int getHeatGeneration() {
		return StaticPowerConfig.SERVER.refineryPerBoilerHeatGeneration.get() * getBoilers().size();
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

	public Map<BlockPos, Integer> getBoilers() {
		HashMap<BlockPos, Integer> output = new HashMap<>();

		for (MultiBlockWrapper<IRefineryBlockEntity> wrapper : multiBlockCache) {
			if (wrapper.getBlockState(getLevel()).getBlock() instanceof BlockRefineryBoiler) {
				int count = 0;
				for (int i = 1; i < 6; i++) {
					BlockPos toCheck = wrapper.position.above(i);
					if (getLevel().getBlockState(toCheck).getBlock() instanceof BlockRefineryTower) {
						count++;
					} else {
						break;
					}
				}
				output.put(wrapper.position, count);
			}
		}

		return output;
	}

	public float getProductivity() {
		float efficiencyPer = 1.0f / MAX_EFFICIENCY_TOWER_HEIGHT;
		Map<BlockPos, Integer> boilers = getBoilers();

		// Get the total efficiency and then divide by the number of boilers.
		float total = 0;
		for (BlockPos pos : boilers.keySet()) {
			total += Math.min(boilers.get(pos), MAX_EFFICIENCY_TOWER_HEIGHT) * efficiencyPer;
		}
		return total;
	}

	protected RecipeMatchParameters getMatchParameters(RecipeProcessingPhase location) {
		if (location == RecipeProcessingPhase.PROCESSING) {
			return new RecipeMatchParameters().setItems(internalInventory.getStackInSlot(0)).setFluids(getInputTank(0).getFluid(), getInputTank(1).getFluid());
		} else {
			return new RecipeMatchParameters().setItems(catalystInventory.getStackInSlot(0)).setFluids(getInputTank(0).getFluid(), getInputTank(1).getFluid());
		}
	}

	protected void moveInputs(RefineryRecipe recipe) {
		transferItemInternally(recipe.getCatalyst().getCount(), catalystInventory, 0, internalInventory, 0);
		currentProcessingProductivity = getProductivity();
	}

	protected ProcessingCheckState canProcessRecipe(RefineryRecipe recipe, RecipeProcessingPhase location) {
		processingComponent.setProcessingPowerUsage(recipe.getPowerCost());
		processingComponent.setMaxProcessingTime(recipe.getProcessingTime());
		heatStorage.setMinimumHeatThreshold(recipe.getProcessingSection().getMinimumHeat());
		ProcessingCheckState multiBlockCheck = checkMultiBlockReady();
		if (!multiBlockCheck.isOk()) {
			return multiBlockCheck;
		}

		ProcessingCheckState tankCheck = fillOutputTanksWithOutput(recipe, FluidAction.SIMULATE);
		if (!tankCheck.isOk()) {
			return tankCheck;
		}

		ProcessingCheckState heatCheck = checkHeatStorageReady();
		if (!heatCheck.isOk()) {
			return heatCheck;
		}

		return ProcessingCheckState.ok();
	}

	protected ProcessingCheckState processingCompleted(RefineryRecipe recipe) {
		// Output the refined fluids.
		fillOutputTanksWithOutput(recipe, FluidAction.EXECUTE);

		// Drain the fluid.
		getInputTank(0).drain((int) (recipe.getPrimaryFluidInput().getAmount() * currentProcessingProductivity), FluidAction.EXECUTE);
		getInputTank(1).drain((int) (recipe.getSecondaryFluidInput().getAmount() * currentProcessingProductivity), FluidAction.EXECUTE);

		// Clear the internal inventory.
		InventoryUtilities.clearInventory(internalInventory);
		heatStorage.cool(getHeatUsage(), HeatTransferAction.EXECUTE);
		return ProcessingCheckState.ok();
	}

	protected ProcessingCheckState checkMultiBlockReady() {
		if (getBoilers().size() == 0) {
			return ProcessingCheckState.error("Missing Boilers!");
		}
		if (getProductivity() <= 0.0f) {
			return ProcessingCheckState.error("Missing Refinery Towers!");
		}
		return ProcessingCheckState.ok();
	}

	protected ProcessingCheckState checkHeatStorageReady() {
		if (heatStorage.isRecoveringFromMeltdown()) {
			return ProcessingCheckState
					.error("Machine recovering from overheat! (" + GuiTextUtilities.formatTicksToTimeUnit(this.heatStorage.getMeltdownRecoveryTicksRemaining()).getString() + ")");
		}
		if (heatStorage.isOverheated()) {
			return ProcessingCheckState.error("Machine overheating!");
		}
		if (!heatStorage.isAboveMinimumHeat()) {
			return ProcessingCheckState.error("Not enough heat!");
		}
		return ProcessingCheckState.ok();
	}

	protected ProcessingCheckState fillOutputTanksWithOutput(RefineryRecipe recipe, FluidAction action) {
		FluidStack output1 = recipe.getFluidOutput1().copy();
		if (!output1.isEmpty()) {
			output1.setAmount((int) (output1.getAmount() * currentProcessingProductivity));
		}

		FluidStack output2 = recipe.getFluidOutput2().copy();
		if (!output2.isEmpty()) {
			output2.setAmount((int) (output2.getAmount() * currentProcessingProductivity));
		}

		FluidStack output3 = recipe.getFluidOutput3().copy();
		if (!output3.isEmpty()) {
			output3.setAmount((int) (output3.getAmount() * currentProcessingProductivity));
		}

		if (getOutputTank(0).fill(output1, action) != output1.getAmount()) {
			return ProcessingCheckState.fluidOutputFull();
		}

		if (getOutputTank(1).fill(output2, action) != output2.getAmount()) {
			return ProcessingCheckState.fluidOutputFull();
		}

		if (getOutputTank(2).fill(output3, action) != output3.getAmount()) {
			return ProcessingCheckState.fluidOutputFull();
		}

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

	@Override
	public void setRemoved() {
		updateMultiblockBlockStates(false);
		super.setRemoved();
	}
}