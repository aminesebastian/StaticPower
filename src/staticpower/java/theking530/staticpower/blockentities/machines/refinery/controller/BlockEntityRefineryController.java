package theking530.staticpower.blockentities.machines.refinery.controller;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import theking530.api.energy.CurrentType;
import theking530.api.energy.StaticPowerVoltage;
import theking530.api.heat.HeatUtilities;
import theking530.api.heat.IHeatStorage.HeatTransferAction;
import theking530.staticcore.blockentity.components.control.processing.ConcretizedProductContainer;
import theking530.staticcore.blockentity.components.control.processing.ProcessingCheckState;
import theking530.staticcore.blockentity.components.control.processing.ProcessingContainer;
import theking530.staticcore.blockentity.components.control.processing.recipe.IRecipeProcessor;
import theking530.staticcore.blockentity.components.control.processing.recipe.RecipeProcessingComponent;
import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationPreset;
import theking530.staticcore.blockentity.components.control.sideconfiguration.presets.AllSidesNever;
import theking530.staticcore.blockentity.components.energy.PowerStorageComponent;
import theking530.staticcore.blockentity.components.fluids.FluidTankComponent;
import theking530.staticcore.blockentity.components.heat.HeatStorageComponent;
import theking530.staticcore.blockentity.components.heat.HeatStorageComponent.HeatManipulationAction;
import theking530.staticcore.blockentity.components.items.InventoryComponent;
import theking530.staticcore.blockentity.components.items.UpgradeInventoryComponent;
import theking530.staticcore.blockentity.components.loopingsound.LoopingSoundComponent;
import theking530.staticcore.blockentity.components.multiblock.newstyle.MultiblockComponent;
import theking530.staticcore.blockentity.components.multiblock.newstyle.MultiblockState;
import theking530.staticcore.blockentity.components.multiblock.newstyle.MultiblockState.MultiblockStateEntry;
import theking530.staticcore.crafting.RecipeMatchParameters;
import theking530.staticcore.data.StaticCoreTier;
import theking530.staticcore.gui.text.GuiTextUtilities;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticcore.utilities.math.SDMath;
import theking530.staticcore.utilities.math.Vector4D;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blockentities.BlockEntityMachine;
import theking530.staticpower.blockentities.machines.refinery.BaseRefineryBlockEntity;
import theking530.staticpower.blockentities.machines.refinery.boiler.BlockRefineryBoiler;
import theking530.staticpower.blockentities.machines.refinery.condenser.BlockEntityRefineryCondenser;
import theking530.staticpower.blockentities.machines.refinery.heatvent.BlockEntityRefineryHeatVent;
import theking530.staticpower.blockentities.machines.refinery.tower.BlockRefineryTower;
import theking530.staticpower.blocks.StaticPowerBlockProperties;
import theking530.staticpower.blocks.StaticPowerBlockProperties.TowerPiece;
import theking530.staticpower.blocks.tileentity.StaticPowerMachineBlock;
import theking530.staticpower.data.crafting.wrappers.refinery.RefineryRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModFluids;
import theking530.staticpower.init.ModMultiblocks;
import theking530.staticpower.init.ModRecipeTypes;

public class BlockEntityRefineryController extends BlockEntityMachine implements IRecipeProcessor<RefineryRecipe> {

	public record RefineryTowerInfo(BlockPos base, BlockPos top, int height) {

		public static final RefineryTowerInfo EMPTY = new RefineryTowerInfo(null, null, 0);
		public boolean isValid() {
			return height > 0;
		}
	}

	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityRefineryController> TYPE = new BlockEntityTypeAllocator<BlockEntityRefineryController>(
			"refinery_controller", (type, pos, state) -> new BlockEntityRefineryController(pos, state),
			ModBlocks.RefineryController);
	public static final int MAX_EFFICIENCY_TOWER_HEIGHT = 4;

	public final InventoryComponent catalystInventory;
	public final LoopingSoundComponent generatingSoundComponent;
	public final UpgradeInventoryComponent upgradesInventory;
	public final RecipeProcessingComponent<RefineryRecipe> processingComponent;
	public final HeatStorageComponent heatStorage;
	public final FluidTankComponent[] fluidTanks;
	public final MultiblockComponent<BaseRefineryBlockEntity> multiblockComponent;
	private float currentProcessingProductivity;

	public BlockEntityRefineryController(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);

		// Get the tier object.
		StaticCoreTier tier = getTierObject();

		registerComponent(generatingSoundComponent = new LoopingSoundComponent("GeneratingSoundComponent", 20));
		registerComponent(multiblockComponent = new MultiblockComponent<BaseRefineryBlockEntity>("MultiblockComponent",
				ModMultiblocks.REFINERY.get()));
		multiblockComponent.setStateChangedCallback((multiblockState) -> {
			this.multiblockStateChanged(multiblockState);
		});

		// Setup the inventories.
		registerComponent(catalystInventory = new InventoryComponent("CatalystInventory", 1, MachineSideMode.Input)
				.setShiftClickEnabled(true).setExposedAsCapability(false));
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));

		// Create the energy storage and and set the energy storage upgrade inventory.
		powerStorage.setExposeAsCapability(false);
		powerStorage.setUpgradeInventory(upgradesInventory);
		powerStorage.setSideConfiguration(null);

		// Setup the processing component.
		registerComponent(processingComponent = new RecipeProcessingComponent<RefineryRecipe>("ProcessingComponent", 1,
				ModRecipeTypes.REFINERY_RECIPE_TYPE.get()));

		// Initialize the processing component to work with the redstone control
		// component, upgrade component and energy component.
		processingComponent.setShouldControlOnBlockState(true);
		processingComponent.setUpgradeInventory(upgradesInventory);
		processingComponent.setRedstoneControlComponent(redstoneControlComponent);
		processingComponent.setPowerComponent(powerStorage);
		processingComponent.setModulateProcessingTimeByPowerSatisfaction(false);

		// Setup the input fluid tanks.
		fluidTanks = new FluidTankComponent[5];

		registerComponent(
				fluidTanks[0] = new FluidTankComponent("FluidTank0", tier.defaultTankCapacity.get(), (fluid) -> {
					FluidStack otherFluid = getInputTank(1).isEmpty() ? ModFluids.WILDCARD : getInputTank(1).getFluid();
					RecipeMatchParameters params = new RecipeMatchParameters().setFluids(fluid, otherFluid)
							.ignoreItems().ignoreFluidAmounts();
					return processingComponent.getRecipe(params).isPresent();
				}).setCapabilityExposedModes(MachineSideMode.Input2).setUpgradeInventory(upgradesInventory));

		registerComponent(
				fluidTanks[1] = new FluidTankComponent("FluidTank1", tier.defaultTankCapacity.get(), (fluid) -> {
					FluidStack otherFluid = getInputTank(0).isEmpty() ? ModFluids.WILDCARD : getInputTank(0).getFluid();
					RecipeMatchParameters params = new RecipeMatchParameters().setFluids(otherFluid, fluid)
							.ignoreItems().ignoreFluidAmounts();
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
		registerComponent(heatStorage = new HeatStorageComponent("HeatStorageComponent", tier)
				.setCapabiltiyFilter((amount, direction, action) -> action == HeatManipulationAction.COOL)
				.setExposedAsCapability(false).setMeltdownRecoveryTicks(100).setTransferHeatWithEnvironment(false));
		heatStorage.setUpgradeInventory(upgradesInventory);
	}

	@Override
	protected PowerStorageComponent createPowerStorageComponent() {
		return new PowerStorageComponent("PowerStorage", 10000, StaticPowerVoltage.LOW, StaticPowerVoltage.HIGH, 5000,
				new CurrentType[] { CurrentType.DIRECT }, StaticPowerVoltage.LOW, 1000, CurrentType.DIRECT, true,
				false);
	}

	@Override
	public void process() {
		if (!getLevel().isClientSide()) {
			if (redstoneControlComponent.passesRedstoneCheck() && !getBoilers().isEmpty()) {

				if (!heatStorage.isRecoveringFromMeltdown()) {
					double maxPowerOutput = powerStorage.drainPower(powerStorage.getMaximumPowerOutput(), true)
							.getPower();
					float maxHeatFlux = HeatUtilities.calculateHeatFluxFromPower(maxPowerOutput);
					float usedHeatFlux = heatStorage.heat(maxHeatFlux, HeatTransferAction.EXECUTE);
					powerStorage.drainPower(HeatUtilities.calculatePowerFromHeatFlux(usedHeatFlux), false);
				}

				Optional<RefineryRecipe> recipe = processingComponent.getProcessingOrPendingRecipe();
				if (recipe.isPresent() && getProductivity() > 0) {
					if (processingComponent.hasProcessingStarted() && !processingComponent.isProcessingPaused()) {
						heatStorage.cool(recipe.get().getProcessingSection().getHeat(), HeatTransferAction.EXECUTE);
					}
				}
			}

			// Handle sounds.
			if (processingComponent.isBlockStateOn()) {
				generatingSoundComponent.startPlayingSound(SoundEvents.MINECART_RIDING, SoundSource.BLOCKS, 0.25f, 0.5f,
						getBlockPos(), 64);
			} else {
				generatingSoundComponent.stopPlayingSound();
			}

			for (BlockPos boilerPos : getBoilers().keySet()) {
				HeatUtilities.transferHeat(level, heatStorage, boilerPos, HeatTransferAction.EXECUTE);
			}

			updateMultiblockBlockStates(processingComponent.isBlockStateOn());

			supplyCondensers();
		} else {
			if (processingComponent.isBlockStateOn() && SDMath.diceRoll(0.5f)) {
				renderParticleEffects();
			}
		}
	}

	private void updateMultiblockBlockStates(boolean isOn) {
		for (MultiblockStateEntry wrapper : multiblockComponent.getState().getBlocks()) {
			BlockState multiBlockState = wrapper.blockState();
			if (multiBlockState.hasProperty(StaticPowerMachineBlock.IS_ON)) {
				boolean onState = multiBlockState.getValue(StaticPowerMachineBlock.IS_ON);
				if (onState != isOn) {
					getLevel().setBlock(wrapper.pos(), multiBlockState.setValue(StaticPowerMachineBlock.IS_ON, isOn),
							2);
				}
			}
		}
	}

	private void renderParticleEffects() {
		Vector4D randomVector = SDMath.getRandomVectorOffset();
		for (MultiblockStateEntry wrapper : multiblockComponent.getState().getBlocks()) {
			BlockState multiBlockState = wrapper.blockState();

			if (multiBlockState.getBlock() instanceof BlockRefineryTower
					&& multiBlockState.hasProperty(StaticPowerBlockProperties.TOWER_POSITION)) {
				TowerPiece position = multiBlockState.getValue(StaticPowerBlockProperties.TOWER_POSITION);
				if (position == TowerPiece.TOP || position == TowerPiece.FULL) {
					getLevel().addParticle(ParticleTypes.LARGE_SMOKE,
							wrapper.pos().getX() + 0.5f + (randomVector.getX() * 0.15f), wrapper.pos().getY() + 1f,
							wrapper.pos().getZ() + 0.5f + (randomVector.getZ() * 0.15f), 0.0f, 0.005f, 0.0f);
					if (SDMath.diceRoll(0.5f)) {
						getLevel().addParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE,
								wrapper.pos().getX() + 0.5f + (randomVector.getX() * 0.15f), wrapper.pos().getY() + 1f,
								wrapper.pos().getZ() + 0.5f + (randomVector.getZ() * 0.15f), 0.0f, 0.05f, 0.0f);
					}
				}
			} else if (getLevel().getBlockEntity(wrapper.pos()) instanceof BlockEntityRefineryHeatVent) {
				if (SDMath.diceRoll(0.1f)) {
					randomVector.setX(randomVector.getX() * 0.55f);
					randomVector.setZ(randomVector.getZ() * 0.55f);
					getLevel().addParticle(ParticleTypes.SMOKE, wrapper.pos().getX() + 0.5f + randomVector.getX(),
							wrapper.pos().getY() + 0.5f + randomVector.getY() / 2,
							wrapper.pos().getZ() + 0.5f + randomVector.getZ(), 0.0f, 0.0f, 0.0f);
				}
			}
		}
	}

	public int getHeatUsage() {
		double heatUse = StaticPowerConfig.SERVER.refineryHeatUse.get();
		Optional<RefineryRecipe> recipe = processingComponent.getProcessingOrPendingRecipe();
		if (recipe.isPresent()) {
			heatUse = recipe.get().getProcessingSection().getHeat();
		}
		return (int) (heatUse * processingComponent.getCalculatedHeatGenerationMultiplier());
	}

	public float getMinimumHeat() {
		Optional<RefineryRecipe> recipe = processingComponent.getProcessingOrPendingRecipe();
		if (recipe.isPresent()) {
			return recipe.get().getProcessingSection().getHeat();
		}
		return StaticPowerConfig.SERVER.refineryMinimumHeat.get();
	}

	public float getMaxHeatGeneration() {
		return HeatUtilities.calculateHeatFluxFromPower((float) powerStorage.getMaximumPowerOutput());
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

		for (MultiblockStateEntry multiBlockEntry : multiblockComponent.getState().getBlocks()) {
			if (multiBlockEntry.blockState().getBlock() instanceof BlockRefineryBoiler) {
				RefineryTowerInfo towerInfo = getTowerInfoForPos(multiBlockEntry.pos().above());
				output.put(multiBlockEntry.pos(), towerInfo.height());
			}
		}

		return output;
	}

	public RefineryTowerInfo getTowerInfoForPos(BlockPos towerPos) {
		BlockState attachedToState = getLevel().getBlockState(towerPos);
		if (attachedToState.getBlock() != ModBlocks.RefineryTower.get()) {
			return RefineryTowerInfo.EMPTY;
		}

		BlockPos towerTop = null;
		for (int i = 0; i < 4; i++) {
			BlockPos queryPos = towerPos.relative(Direction.UP, i);
			BlockState queryState = getLevel().getBlockState(queryPos);
			if (queryState.getBlock() != ModBlocks.RefineryTower.get()) {
				break;
			}

			TowerPiece position = queryState.getValue(StaticPowerBlockProperties.TOWER_POSITION);
			if (position == TowerPiece.TOP) {
				towerTop = queryPos;
				break;
			}
		}

		if (towerTop == null) {
			return RefineryTowerInfo.EMPTY;
		}

		BlockPos towerBottom = null;
		for (int i = 0; i < 4; i++) {
			BlockPos queryPos = towerPos.relative(Direction.DOWN, i);
			BlockState queryState = getLevel().getBlockState(queryPos);
			if (queryState.getBlock() != ModBlocks.RefineryTower.get()) {
				break;
			}

			TowerPiece position = queryState.getValue(StaticPowerBlockProperties.TOWER_POSITION);
			if (position == TowerPiece.BOTTOM) {
				towerBottom = queryPos;
				break;
			}
		}

		if (towerBottom == null) {
			return RefineryTowerInfo.EMPTY;
		}

		int towerHeight = towerTop.getY() - towerBottom.getY() + 1;
		return new RefineryTowerInfo(towerBottom, towerTop, towerHeight);
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

	protected ProcessingCheckState checkMultiBlockReady() {
		if (!this.multiblockComponent.isWellFormed()) {
			return ProcessingCheckState.error("wtf");
		}
		if (getBoilers().size() == 0) {
			return ProcessingCheckState.error("Missing Boilers!");
		}
		if (getProductivity() <= 0.0f) {
			return ProcessingCheckState.error("Missing Refinery Towers!");
		}
		return ProcessingCheckState.ok();
	}

	protected ProcessingCheckState checkHeatStorageReady(RefineryRecipe recipe) {
		if (heatStorage.isRecoveringFromMeltdown()) {
			return ProcessingCheckState.error("Machine recovering from overheat! (" + GuiTextUtilities
					.formatTicksToTimeUnit(this.heatStorage.getMeltdownRecoveryTicksRemaining()).getString() + ")");
		}
		if (heatStorage.isOverheated()) {
			return ProcessingCheckState.error("Machine overheating!");
		}

		if (!heatStorage.isAboveMinimumHeat()
				|| heatStorage.getTemperature() < recipe.getProcessingSection().getMinimumHeat()) {
			return ProcessingCheckState.heatStorageTooCold(recipe.getProcessingSection().getMinimumHeat());
		}

		return ProcessingCheckState.ok();
	}

	protected ProcessingCheckState fillOutputTanksWithOutput(ConcretizedProductContainer outputContainer,
			FluidAction action) {
		if (outputContainer.getFluids().size() > 0) {
			FluidStack output0 = outputContainer.getFluid(0).copy();
			output0.setAmount((int) (output0.getAmount() * currentProcessingProductivity));

			if (getOutputTank(0).fill(output0, action) != output0.getAmount()) {
				return ProcessingCheckState.fluidOutputFull();
			}
		}

		if (outputContainer.getFluids().size() > 1) {
			FluidStack output1 = outputContainer.getFluid(1).copy();
			output1.setAmount((int) (output1.getAmount() * currentProcessingProductivity));
			if (getOutputTank(1).fill(output1, action) != output1.getAmount()) {
				return ProcessingCheckState.fluidOutputFull();
			}
		}

		if (outputContainer.getFluids().size() > 2) {
			FluidStack output2 = outputContainer.getFluid(2).copy();
			output2.setAmount((int) (output2.getAmount() * currentProcessingProductivity));
			if (getOutputTank(2).fill(output2, action) != output2.getAmount()) {
				return ProcessingCheckState.fluidOutputFull();
			}
		}

		return ProcessingCheckState.ok();
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerRefineryController(windowId, inventory, this);
	}

	@Override
	protected SideConfigurationPreset getDefaultSideConfiguration() {
		return AllSidesNever.INSTANCE;
	}

	@Override
	public void setRemoved() {
		updateMultiblockBlockStates(false);
		super.setRemoved();
	}

	@Override
	public RecipeMatchParameters getRecipeMatchParameters(RecipeProcessingComponent<RefineryRecipe> component) {
		return new RecipeMatchParameters().setItems(catalystInventory.getStackInSlot(0))
				.setFluids(getInputTank(0).getFluid(), getInputTank(1).getFluid());
	}

	@Override
	public void captureOutputs(RecipeProcessingComponent<RefineryRecipe> component, RefineryRecipe recipe,
			ConcretizedProductContainer outputContainer) {

		if (!recipe.getFluidOutput1().isEmpty()) {
			outputContainer.addFluid(recipe.getFluidOutput1());
		}
		if (!recipe.getFluidOutput2().isEmpty()) {
			outputContainer.addFluid(recipe.getFluidOutput2());
		}
		if (!recipe.getFluidOutput3().isEmpty()) {
			outputContainer.addFluid(recipe.getFluidOutput3());
		}
	}

	@Override
	public ProcessingCheckState canStartProcessingRecipe(RecipeProcessingComponent<RefineryRecipe> component,
			RefineryRecipe recipe, ConcretizedProductContainer outputContainer) {
		ProcessingCheckState multiBlockCheck = checkMultiBlockReady();
		if (!multiBlockCheck.isOk()) {
			return multiBlockCheck;
		}

		ProcessingCheckState tankCheck = fillOutputTanksWithOutput(outputContainer, FluidAction.SIMULATE);
		if (!tankCheck.isOk()) {
			return tankCheck;
		}

		ProcessingCheckState heatCheck = checkHeatStorageReady(recipe);
		if (!heatCheck.isOk()) {
			return heatCheck;
		}

		return ProcessingCheckState.ok();
	}

	@Override
	public void prepareComponentForProcessing(RecipeProcessingComponent<RefineryRecipe> component,
			RefineryRecipe recipe, ConcretizedProductContainer outputContainer) {
		heatStorage.setMinimumHeatThreshold(recipe.getProcessingSection().getMinimumHeat());
		currentProcessingProductivity = getProductivity();
	}

	@Override
	public void captureInputs(RecipeProcessingComponent<RefineryRecipe> component, RefineryRecipe recipe,
			ProcessingContainer processingContainer, ConcretizedProductContainer inputContainer) {
		if (recipe.hasCatalyst()) {
			inputContainer.addItem(catalystInventory.extractItem(0, recipe.getCatalyst().getCount(), false));
		} else {
			inputContainer.addFluid(FluidStack.EMPTY);
		}
		if (recipe.hasPrimaryFluidInput()) {
			inputContainer.addFluid(getInputTank(0).drain(
					(int) (recipe.getPrimaryFluidInput().getAmount() * currentProcessingProductivity),
					FluidAction.EXECUTE));
		} else {
			inputContainer.addFluid(FluidStack.EMPTY);
		}
		if (recipe.hasSecondaryFluidInput()) {
			inputContainer.addFluid(getInputTank(1).drain(
					(int) (recipe.getSecondaryFluidInput().getAmount() * currentProcessingProductivity),
					FluidAction.EXECUTE));
		} else {
			inputContainer.addFluid(FluidStack.EMPTY);
		}
	}

	public ProcessingCheckState canContinueProcessing(RecipeProcessingComponent<RefineryRecipe> component,
			ProcessingContainer processingContainer) {
		Optional<RefineryRecipe> recipe = component.getProcessingRecipe();

		ProcessingCheckState heatCheck = checkHeatStorageReady(recipe.get());
		if (!heatCheck.isOk()) {
			return heatCheck;
		}
		return ProcessingCheckState.ok();
	}

	@Override
	public void onProcessingCompleted(RecipeProcessingComponent<RefineryRecipe> component,
			ProcessingContainer processingContainer) {
		fillOutputTanksWithOutput(processingContainer.getOutputs(), FluidAction.EXECUTE);
		heatStorage.cool(getHeatUsage(), HeatTransferAction.EXECUTE);
	}

	private void multiblockStateChanged(MultiblockState newState) {
		if (newState.isWellFormed()) {
			heatStorage.setMass(getBoilers().size() * 1 + 10);
		}
	}

	private void supplyCondensers() {
		Map<Integer, List<BlockEntityRefineryCondenser>> map = new HashMap<>();

		for (MultiblockStateEntry entry : multiblockComponent.getState().getBlocks()) {
			if (entry.blockState().getBlock() != ModBlocks.RefineryCondenser.get()) {
				continue;
			}

			BlockEntityRefineryCondenser condenser = (BlockEntityRefineryCondenser) getLevel()
					.getBlockEntity(entry.pos());
			if (!condenser.hasRecipeTankIndex()) {
				continue;
			}

			int tankIndex = condenser.getRecipeTankIndex();
			if (!map.containsKey(tankIndex)) {
				map.put(tankIndex, new LinkedList<>());
			}
			map.get(tankIndex).add(condenser);
		}

		for (int i = 0; i < 3; i++) {
			FluidTankComponent outputTank = getOutputTank(i);
			if (outputTank.isEmpty()) {
				continue;
			}

			List<BlockEntityRefineryCondenser> condensers = map.getOrDefault(i, null);
			if (condensers == null || condensers.isEmpty()) {
				continue;
			}

			for (BlockEntityRefineryCondenser condenser : condensers) {
				FluidTankComponent condenserTank = condenser.tank;
				FluidStack simulatedDrain = outputTank.drain(1000, FluidAction.SIMULATE);
				int supplied = condenserTank.fill(simulatedDrain, FluidAction.EXECUTE);
				outputTank.drain(supplied, FluidAction.EXECUTE);
			}
		}
	}
}