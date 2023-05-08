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
import theking530.staticcore.blockentity.components.control.processing.ConcretizedProductContainer;
import theking530.staticcore.blockentity.components.control.processing.ProcessingCheckState;
import theking530.staticcore.blockentity.components.control.processing.ProcessingContainer;
import theking530.staticcore.blockentity.components.control.processing.recipe.IRecipeProcessor;
import theking530.staticcore.blockentity.components.control.processing.recipe.RecipeProcessingComponent;
import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationPreset;
import theking530.staticcore.blockentity.components.control.sideconfiguration.presets.AllSidesNever;
import theking530.staticcore.blockentity.components.fluids.FluidTankComponent;
import theking530.staticcore.blockentity.components.heat.HeatStorageComponent;
import theking530.staticcore.blockentity.components.heat.HeatStorageComponent.HeatManipulationAction;
import theking530.staticcore.blockentity.components.items.InventoryComponent;
import theking530.staticcore.blockentity.components.items.UpgradeInventoryComponent;
import theking530.staticcore.blockentity.components.loopingsound.LoopingSoundComponent;
import theking530.staticcore.blockentity.multiblock.MultiBlockCache;
import theking530.staticcore.blockentity.multiblock.MultiBlockEntry;
import theking530.staticcore.blockentity.multiblock.MultiBlockFormationStatus;
import theking530.staticcore.crafting.RecipeMatchParameters;
import theking530.staticcore.data.StaticCoreTier;
import theking530.staticcore.gui.text.GuiTextUtilities;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticcore.utilities.math.SDMath;
import theking530.staticcore.utilities.math.Vector4D;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blockentities.BlockEntityMachine;
import theking530.staticpower.blockentities.machines.refinery.boiler.BlockRefineryBoiler;
import theking530.staticpower.blockentities.machines.refinery.heatvent.BlockEntityRefineryHeatVent;
import theking530.staticpower.blockentities.machines.refinery.tower.BlockRefineryTower;
import theking530.staticpower.blocks.StaticPowerBlockProperties;
import theking530.staticpower.blocks.StaticPowerBlockProperties.TowerPiece;
import theking530.staticpower.blocks.tileentity.StaticPowerMachineBlock;
import theking530.staticpower.data.crafting.wrappers.refinery.RefineryRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModFluids;
import theking530.staticpower.init.ModRecipeTypes;
import theking530.staticpower.init.tags.ModBlockTags;

public class BlockEntityRefineryController extends BlockEntityMachine implements IRecipeProcessor<RefineryRecipe> {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityRefineryController> TYPE = new BlockEntityTypeAllocator<BlockEntityRefineryController>(
			"refinery_controller", (type, pos, state) -> new BlockEntityRefineryController(pos, state),
			ModBlocks.RefineryController);
	public static final int MAX_EFFICIENCY_TOWER_HEIGHT = 4;

	public static final MultiBlockFormationStatus MULTIPLE_CONTROLLERS = MultiBlockFormationStatus
			.failed("gui.staticpower.refinery_status_multiple_controllers");
	public static final MultiBlockFormationStatus MISSING_BOILER = MultiBlockFormationStatus
			.failed("gui.staticpower.refinery_missing_boiler");

	public final InventoryComponent catalystInventory;
	public final LoopingSoundComponent generatingSoundComponent;
	public final UpgradeInventoryComponent upgradesInventory;
	public final RecipeProcessingComponent<RefineryRecipe> processingComponent;
	public final HeatStorageComponent heatStorage;
	public final FluidTankComponent[] fluidTanks;
	private float currentProcessingProductivity;

	private final MultiBlockCache<BlockEntityRefineryController> multiBlockCache;

	public BlockEntityRefineryController(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);
		multiBlockCache = new MultiBlockCache<>(this, this::isValidForMultiBlock, this::isWellFormed);

		// Get the tier object.
		StaticCoreTier tier = getTierObject();
		registerComponent(generatingSoundComponent = new LoopingSoundComponent("GeneratingSoundComponent", 20));

		// Setup the inventories.
		registerComponent(catalystInventory = new InventoryComponent("CatalystInventory", 1, MachineSideMode.Input)
				.setShiftClickEnabled(true).setExposedAsCapability(false));
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));

		// Create the energy storage and and set the energy storage upgrade inventory.
		powerStorage.setExposeAsCapability(false);
		powerStorage.setUpgradeInventory(upgradesInventory);

		// Setup the processing component.
		registerComponent(processingComponent = new RecipeProcessingComponent<RefineryRecipe>("ProcessingComponent", 1,
				ModRecipeTypes.REFINERY_RECIPE_TYPE.get()));

		// Initialize the processing component to work with the redstone control
		// component, upgrade component and energy component.
		processingComponent.setShouldControlOnBlockState(true);
		processingComponent.setUpgradeInventory(upgradesInventory);
		processingComponent.setRedstoneControlComponent(redstoneControlComponent);
		processingComponent.setPowerComponent(powerStorage);

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
		registerComponent(heatStorage = new HeatStorageComponent("HeatStorageComponent", 0,
				tier.defaultMachineOverheatTemperature.get(), tier.defaultMachineMaximumTemperature.get(), 50)
				.setCapabiltiyFilter((amount, direction, action) -> action == HeatManipulationAction.COOL)
				.setExposedAsCapability(false).setMeltdownRecoveryTicks(100));
		heatStorage.setUpgradeInventory(upgradesInventory);
	}

	@Override
	public void process() {
		multiBlockCache.update();

		if (!getLevel().isClientSide()) {
			if (redstoneControlComponent.passesRedstoneCheck() && getProductivity() > 0
					&& processingComponent.getProcessingOrPendingRecipe().isPresent()) {
				double powerCost = StaticPowerConfig.SERVER.refineryPowerUsage.get();
				boolean shouldHeat = processingComponent.performedWorkLastTick()
						|| !heatStorage.isRecoveringFromMeltdown();
				if (powerStorage.canSupplyPower(powerCost) && shouldHeat) {
					powerStorage.drainPower(powerCost, false);
					heatStorage.heat(getHeatGeneration(), HeatTransferAction.EXECUTE);
				}
			}

			// Handle sounds.
			if (processingComponent.isBlockStateOn()) {
				generatingSoundComponent.startPlayingSound(SoundEvents.MINECART_RIDING, SoundSource.BLOCKS, 0.25f, 0.5f,
						getBlockPos(), 64);
			} else {
				generatingSoundComponent.stopPlayingSound();
			}

			updateMultiblockBlockStates(processingComponent.isBlockStateOn());
		} else {
			if (processingComponent.isBlockStateOn() && SDMath.diceRoll(0.5f)) {
				renderParticleEffects();
			}
		}
	}

	public MultiBlockFormationStatus getMultiBlockStatus() {
		return this.multiBlockCache.getStatus();
	}

	private void updateMultiblockBlockStates(boolean isOn) {
		for (MultiBlockEntry<BlockEntityRefineryController> wrapper : multiBlockCache) {
			BlockState multiBlockState = wrapper.getBlockState();
			if (multiBlockState.hasProperty(StaticPowerMachineBlock.IS_ON)) {
				boolean onState = multiBlockState.getValue(StaticPowerMachineBlock.IS_ON);
				if (onState != isOn) {
					getLevel().setBlock(wrapper.getPosition(),
							multiBlockState.setValue(StaticPowerMachineBlock.IS_ON, isOn), 2);
				}
			}
		}
	}

	private void renderParticleEffects() {
		Vector4D randomVector = SDMath.getRandomVectorOffset();
		for (MultiBlockEntry<BlockEntityRefineryController> wrapper : multiBlockCache) {
			BlockState multiBlockState = wrapper.getBlockState();

			if (multiBlockState.getBlock() instanceof BlockRefineryTower
					&& multiBlockState.hasProperty(StaticPowerBlockProperties.TOWER_POSITION)) {
				TowerPiece position = multiBlockState.getValue(StaticPowerBlockProperties.TOWER_POSITION);
				if (position == TowerPiece.TOP || position == TowerPiece.FULL) {
					getLevel().addParticle(ParticleTypes.LARGE_SMOKE,
							wrapper.getPosition().getX() + 0.5f + (randomVector.getX() * 0.15f),
							wrapper.getPosition().getY() + 1f,
							wrapper.getPosition().getZ() + 0.5f + (randomVector.getZ() * 0.15f), 0.0f, 0.005f, 0.0f);
					if (SDMath.diceRoll(0.5f)) {
						getLevel().addParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE,
								wrapper.getPosition().getX() + 0.5f + (randomVector.getX() * 0.15f),
								wrapper.getPosition().getY() + 1f,
								wrapper.getPosition().getZ() + 0.5f + (randomVector.getZ() * 0.15f), 0.0f, 0.05f, 0.0f);
					}
				}
			} else if (getLevel().getBlockEntity(wrapper.getPosition()) instanceof BlockEntityRefineryHeatVent) {
				if (SDMath.diceRoll(0.1f)) {
					randomVector.setX(randomVector.getX() * 0.55f);
					randomVector.setZ(randomVector.getZ() * 0.55f);
					getLevel().addParticle(ParticleTypes.SMOKE,
							wrapper.getPosition().getX() + 0.5f + randomVector.getX(),
							wrapper.getPosition().getY() + 0.5f + randomVector.getY() / 2,
							wrapper.getPosition().getZ() + 0.5f + randomVector.getZ(), 0.0f, 0.0f, 0.0f);
				}
			}
		}
	}

	private boolean isValidForMultiBlock(BlockPos pos, BlockState state, BlockEntity be) {
		return state.is(ModBlockTags.REFINERY_BLOCK);
	}

	private MultiBlockFormationStatus isWellFormed(Map<BlockPos, MultiBlockEntry<BlockEntityRefineryController>> map) {
		int boilerCount = 0;
		for (BlockPos pos : map.keySet()) {
			if (getLevel().getBlockEntity(pos) instanceof BlockEntityRefineryController) {
				return MULTIPLE_CONTROLLERS;
			}

			if (getLevel().getBlockState(pos).getBlock() instanceof BlockRefineryBoiler) {
				boilerCount++;
			}
		}

		if (boilerCount == 0) {
			return MISSING_BOILER;
		}

		return MultiBlockFormationStatus.OK;
	}

	public void onNeighborChanged(BlockState currentState, BlockPos neighborPos, boolean isMoving) {
		super.onNeighborChanged(currentState, neighborPos, isMoving);
		this.multiBlockCache.update();
	}

	public int getHeatUsage() {
		float heatUse = StaticPowerConfig.SERVER.refineryHeatUse.get();
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

	public float getHeatGeneration() {
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

		for (MultiBlockEntry<BlockEntityRefineryController> wrapper : multiBlockCache) {
			if (wrapper.getBlockState().getBlock() instanceof BlockRefineryBoiler) {
				int count = 0;
				for (int i = 1; i < 6; i++) {
					BlockPos toCheck = wrapper.getPosition().above(i);
					if (getLevel().getBlockState(toCheck).getBlock() instanceof BlockRefineryTower) {
						count++;
					} else {
						break;
					}
				}
				output.put(wrapper.getPosition(), count);
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
			return ProcessingCheckState.error("Machine recovering from overheat! (" + GuiTextUtilities
					.formatTicksToTimeUnit(this.heatStorage.getMeltdownRecoveryTicksRemaining()).getString() + ")");
		}
		if (heatStorage.isOverheated()) {
			return ProcessingCheckState.error("Machine overheating!");
		}
		if (!heatStorage.isAboveMinimumHeat()) {
			return ProcessingCheckState.error("Not enough heat!");
		}
		return ProcessingCheckState.ok();
	}

	protected ProcessingCheckState fillOutputTanksWithOutput(ConcretizedProductContainer outputContainer,
			FluidAction action) {
		FluidStack output1 = outputContainer.getFluid(0).copy();
		if (!output1.isEmpty()) {
			output1.setAmount((int) (output1.getAmount() * currentProcessingProductivity));
		}

		FluidStack output2 = outputContainer.getFluid(1).copy();
		if (!output2.isEmpty()) {
			output2.setAmount((int) (output2.getAmount() * currentProcessingProductivity));
		}

		FluidStack output3 = outputContainer.getFluid(2).copy();
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

		ProcessingCheckState heatCheck = checkHeatStorageReady();
		if (!heatCheck.isOk()) {
			return heatCheck;
		}

		return ProcessingCheckState.ok();
	}

	@Override
	public void prepareComponentForProcessing(RecipeProcessingComponent<RefineryRecipe> component,
			RefineryRecipe recipe, ConcretizedProductContainer outputContainer) {
		component.setBaseProcessingTime(recipe.getProcessingTime());
		component.setBasePowerUsage(recipe.getPowerCost());
		heatStorage.setMinimumHeatThreshold(recipe.getProcessingSection().getMinimumHeat());
		currentProcessingProductivity = getProductivity();
	}

	@Override
	public void captureInputs(RecipeProcessingComponent<RefineryRecipe> component, RefineryRecipe recipe,
			ProcessingContainer processingContainer, ConcretizedProductContainer inputContainer) {
		if (recipe.hasCatalyst()) {
			inputContainer.addItem(catalystInventory.extractItem(0, recipe.getCatalyst().getCount(), false));
		}
		if (recipe.hasPrimaryFluidInput()) {
			inputContainer.addFluid(getInputTank(0).drain(
					(int) (recipe.getPrimaryFluidInput().getAmount() * currentProcessingProductivity),
					FluidAction.EXECUTE));
		}
		if (recipe.hasSecondaryFluidInput()) {
			inputContainer.addFluid(getInputTank(1).drain(
					(int) (recipe.getSecondaryFluidInput().getAmount() * currentProcessingProductivity),
					FluidAction.EXECUTE));
		}
	}

	@Override
	public void onProcessingCompleted(RecipeProcessingComponent<RefineryRecipe> component,
			ProcessingContainer processingContainer) {
		fillOutputTanksWithOutput(processingContainer.getOutputs(), FluidAction.EXECUTE);
		heatStorage.cool(getHeatUsage(), HeatTransferAction.EXECUTE);
	}
}