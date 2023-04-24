package theking530.staticpower.blockentities.machines.pump;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.model.data.ModelData.Builder;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.api.energy.CapabilityStaticPower;
import theking530.api.energy.IStaticPowerStorage;
import theking530.api.fluid.CapabilityStaticFluid;
import theking530.api.fluid.IStaticPowerFluidHandler;
import theking530.staticcore.blockentity.components.control.processing.ProcessingCheckState;
import theking530.staticcore.blockentity.components.control.processing.machine.MachineProcessingComponent;
import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationPreset;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationUtilities;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticcore.blockentity.components.energy.IPowerStorageComponentFilter;
import theking530.staticcore.blockentity.components.fluids.FluidInputServoComponent;
import theking530.staticcore.blockentity.components.fluids.FluidTankComponent;
import theking530.staticcore.blockentity.components.fluids.IFluidTankComponentFilter;
import theking530.staticcore.blockentity.components.items.BatteryInventoryComponent;
import theking530.staticcore.blockentity.components.items.InputServoComponent;
import theking530.staticcore.blockentity.components.items.InventoryComponent;
import theking530.staticcore.blockentity.components.items.ItemStackHandlerFilter;
import theking530.staticcore.blockentity.components.loopingsound.LoopingSoundComponent;
import theking530.staticcore.data.StaticCoreTier;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticcore.world.WorldUtilities;
import theking530.staticpower.StaticPower;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blockentities.BlockEntityMachine;
import theking530.staticpower.client.rendering.blockentity.BlockEntityRenderPump;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.tags.ModItemTags;

public class BlockEntityPump extends BlockEntityMachine {
	private enum FluidPumpResult {
		NONE, PASSIVE_SUPPLY, ACTIVE_SUPPLY
	}

	private record PumpTubePathResult(boolean isComplete, List<BlockPos> missingTubes) {

	}

	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityPump> TYPE = new BlockEntityTypeAllocator<BlockEntityPump>(
			"pump", (type, pos, state) -> new BlockEntityPump(type, pos, state), ModBlocks.BasicPump,
			ModBlocks.AdvancedPump, ModBlocks.StaticPump, ModBlocks.EnergizedPump, ModBlocks.LumumPump,
			ModBlocks.CreativePump);
	public static final ModelProperty<FluidPumpRenderingState> PUMP_RENDERING_STATE = new ModelProperty<FluidPumpRenderingState>();
	public static final int MAX_SEARCH_DEPTH = 512;
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setTileEntitySpecialRenderer(BlockEntityRenderPump::new);
		}
	}

	public final InventoryComponent tubeInventory;
	public final InventoryComponent batteryInventory;
	public final FluidTankComponent fluidTankComponent;
	public final MachineProcessingComponent processingComponent;
	private final LoopingSoundComponent soundComponent;
	private final FluidPumpRenderingState renderingState;
	private PumpTubeCache pumpTubeCache;

	public BlockEntityPump(BlockEntityTypeAllocator<BlockEntityPump> allocator, BlockPos pos, BlockState state) {
		super(allocator, pos, state);
		renderingState = new FluidPumpRenderingState();
		pumpTubeCache = null;

		// Get the tier.
		StaticCoreTier tierObject = getTierObject();
		int pumpRate = tierObject.pumpRate.get();

		registerComponent(tubeInventory = new InventoryComponent("TubeInventory", 1, MachineSideMode.Input)
				.setShiftClickEnabled(true).setFilter(new ItemStackHandlerFilter() {
					public boolean canInsertItem(int slot, ItemStack stack) {
						return ModItemTags.matches(ModItemTags.PUMP_TUBE, stack.getItem());
					}
				}));
		registerComponent(soundComponent = new LoopingSoundComponent("SoundComponent", 20));
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", powerStorage));
		registerComponent(processingComponent = new MachineProcessingComponent("ProcessingComponent", pumpRate)
				.setRedstoneControlComponent(redstoneControlComponent).setPowerComponent(powerStorage)
				.setModulateProcessingTimeByPowerSatisfaction(false));

		registerComponent(fluidTankComponent = new FluidTankComponent("FluidTank",
				StaticPowerConfig.SERVER.pumpTankCapacity.get()));
		fluidTankComponent.setCapabilityExposedModes(MachineSideMode.Input, MachineSideMode.Output);
		fluidTankComponent.setFluidTankFilter(new IFluidTankComponentFilter() {
			@Override
			public boolean shouldExposeTankOnSide(Direction side) {
				BlockSide blockSide = SideConfigurationUtilities.getBlockSide(side, getFacingDirection());
				return blockSide == BlockSide.FRONT && blockSide == BlockSide.BACK;
			}
		});

		powerStorage.setSideConfiguration(ioSideConfiguration);
		powerStorage.setPowerStorageFilter(new IPowerStorageComponentFilter() {
			@Override
			public boolean shouldExposePowerOnSide(Direction side) {
				BlockSide blockSide = SideConfigurationUtilities.getBlockSide(side, getFacingDirection());
				return blockSide != BlockSide.FRONT && blockSide != BlockSide.BACK;
			}
		});

		registerComponent(new InputServoComponent("InputServoComponent", tubeInventory, 0));
		registerComponent(new FluidInputServoComponent("FluidInputServoComponent", 1000, fluidTankComponent,
				MachineSideMode.Input));
	}

	@Override
	public void process() {
		if (!getLevel().isClientSide() && redstoneControlComponent.passesRedstoneCheck()) {
			distributeFluid();
		}

		// Don't process if there aren't any pump tubes.
		List<Direction> sidesToPumpFrom = getSidesWithPump();
		if (sidesToPumpFrom.isEmpty()) {
			processingComponent.pauseProcessing();
		} else {
			processingComponent.resumeProcessing();
		}
	}

	public void onNeighborReplaced(BlockState state, Direction direction, BlockState facingState, BlockPos FacingPos) {
		super.onNeighborReplaced(state, direction, facingState, FacingPos);
		requestModelDataUpdate();
	}

	private void distributeFluid() {
		FluidPumpResult pushType = FluidPumpResult.NONE;

		for (Direction dir : Direction.values()) {
			// If we can't output from the provided side, skip it.
			if (!ioSideConfiguration.getWorldSpaceDirectionConfiguration(dir).isOutputMode()) {
				continue;
			}
			BlockSide side = SideConfigurationUtilities.getBlockSide(dir, getFacingDirection());
			if (side == BlockSide.FRONT || side == BlockSide.BACK) {
				FluidPumpResult pushResult = pushFluidToSide(dir);
				if (pushResult.ordinal() > pushType.ordinal()) {
					pushType = pushResult;
				}
			}
		}

		if (pushType == FluidPumpResult.ACTIVE_SUPPLY) {
			soundComponent.startPlayingSound(SoundEvents.AMBIENT_UNDERWATER_LOOP, SoundSource.BLOCKS, 0.5f, 0.9f,
					getBlockPos(), 32);
			powerStorage.drainPower(1, false);
		} else {
			soundComponent.stopPlayingSound();
		}
	}

	private FluidPumpResult pushFluidToSide(Direction side) {
		BlockPos targetPos = getBlockPos().relative(side);

		// Check for the tile entity on the provided side. If null, return early.
		BlockEntity te = getLevel().getBlockEntity(targetPos);
		if (te == null) {
			return FluidPumpResult.NONE;
		}

		// First try to push to a static fluid capability.
		IStaticPowerFluidHandler staticFluidHandler = te
				.getCapability(CapabilityStaticFluid.STATIC_FLUID_CAPABILITY, side.getOpposite()).orElse(null);
		if (staticFluidHandler != null) {
			float pressure = this.powerStorage.drainPower(1, true).getPower() == 1
					? IStaticPowerFluidHandler.MAX_PRESSURE
					: IStaticPowerFluidHandler.PASSIVE_FLOW_PRESSURE;
			FluidStack simulatedDrain = fluidTankComponent.drain(fluidTankComponent.getCapacity(),
					FluidAction.SIMULATE);
			int filled = staticFluidHandler.fill(simulatedDrain, pressure, FluidAction.EXECUTE);
			fluidTankComponent.drain(filled, FluidAction.EXECUTE);
			return FluidPumpResult.ACTIVE_SUPPLY;
		}

		// If the above failed, try to push to a regular fluid handler.
		IFluidHandler fluidHandler = te.getCapability(ForgeCapabilities.FLUID_HANDLER, side.getOpposite()).orElse(null);
		if (fluidHandler != null) {
			FluidUtil.tryFluidTransfer(fluidHandler, fluidTankComponent, fluidTankComponent.getCapacity(), true);
			return FluidPumpResult.PASSIVE_SUPPLY;
		}

		return FluidPumpResult.NONE;
	}

	protected boolean isValidSideConfiguration(BlockSide side, MachineSideMode mode) {
		if (side != BlockSide.BACK && side != BlockSide.FRONT) {
			return mode == MachineSideMode.Input || mode == MachineSideMode.Disabled;
		}
		return mode == MachineSideMode.Output || mode == MachineSideMode.Input;
	}

	protected boolean hasConnectedPressureTarget() {
		for (Direction dir : Direction.values()) {
			BlockPos targetPos = getBlockPos().relative(dir);
			BlockEntity te = getLevel().getBlockEntity(targetPos);
			if (te == null) {
				continue;
			}
			if (te.getCapability(CapabilityStaticFluid.STATIC_FLUID_CAPABILITY, dir.getOpposite()).isPresent()) {
				return true;
			}
		}
		return false;
	}

	protected ProcessingCheckState canPerformInWorldPumping() {
		if (powerStorage.getStoredPower() < StaticPowerConfig.SERVER.pumpPowerUsage.get()) {
			return ProcessingCheckState.notEnoughPower(StaticPowerConfig.SERVER.pumpPowerUsage.get());
		}

		if ((fluidTankComponent.getFluidAmount() + 1000) > fluidTankComponent.getCapacity()) {
			return ProcessingCheckState.fluidOutputFull();
		}

		List<Direction> sidesToPumpFrom = getSidesWithPump();
		if (sidesToPumpFrom.isEmpty()) {
			return ProcessingCheckState.cancel();
		}

		// The pumpTubeCache not being null implies the algorithm has run at least once.
		if (pumpTubeCache != null) {
			BlockPos fluidPos = getNextPumpablePosition();
			if (fluidPos != null) {
				PumpTubePathResult pathResult = doesPumpTubePathExistToPosition(fluidPos,
						pumpTubeCache.getFacingDirection());
				if (!pathResult.isComplete() && !hasTubesForPlacement()) {
					return ProcessingCheckState.error("gui.staticpower.alert.missing_pump_tubes");
				}
			}
		}

		return ProcessingCheckState.ok();
	}

	protected ProcessingCheckState performInWorldPumping() {
		List<Direction> sidesToPumpFrom = getSidesWithPump();

		// If we don't have a pump cache, try to make one.
		if (pumpTubeCache == null || pumpTubeCache.isEmpty()) {
			Direction sideWithPumpTube = sidesToPumpFrom
					.get(getLevel().getRandom().nextIntBetweenInclusive(0, sidesToPumpFrom.size() - 1));
			BlockPos pumpTubePos = getBlockPos().relative(sideWithPumpTube);

			// First check and see if any of the pump tubes along the chain have valid
			// fluids. If they do, just use those.
			do {
				pumpTubeCache = getFluidSourcesSurroundingPosition(pumpTubePos, sideWithPumpTube);
				if (!pumpTubeCache.isEmpty()) {
					break;
				}
				pumpTubePos = pumpTubePos.relative(sideWithPumpTube);
			} while (doesPositionContainValidPumpTube(pumpTubePos.relative(sideWithPumpTube), sideWithPumpTube));

			// If we still don't have a valid fluid cache, check the next position along the
			// chain.
			// Regardless of whether or not the location has a valid fluid cache, we still
			// want to add a new tube there,
			// so the next iteration can start from there.
			if (pumpTubeCache.isEmpty()) {
				BlockPos nextPumpTubeLocation = pumpTubePos.relative(sideWithPumpTube);
				pumpTubeCache = getFluidSourcesSurroundingPosition(nextPumpTubeLocation, sideWithPumpTube);
				if (pumpTubeCache.isEmpty() && hasTubesForPlacement()) {
					placePumpTube(nextPumpTubeLocation, sideWithPumpTube);
				}
			}
		}

		// If we were unable to build a cache, cancel processing.
		if (pumpTubeCache.isEmpty() && !hasTubesForPlacement()) {
			return ProcessingCheckState.error("gui.staticpower.alert.missing_pump_tubes");
		}

		BlockPos fluidPos = getNextPumpablePosition();
		PumpTubePathResult pathResult = doesPumpTubePathExistToPosition(fluidPos, pumpTubeCache.getFacingDirection());
		if (!pathResult.isComplete() && hasTubesForPlacement()) {
			placePumpTube(pathResult.missingTubes().get(0), pumpTubeCache.getFacingDirection());
			return ProcessingCheckState.cancel();
		}

		// Try to pump at the provided location. Always succeed regardless.
		if (pumpFluidAtPosition(fluidPos)) {
			pumpTubeCache.poll();
		}

		return ProcessingCheckState.ok();
	}

	protected boolean hasTubesForPlacement() {
		return ModItemTags.matches(ModItemTags.PUMP_TUBE, tubeInventory.getStackInSlot(0).getItem());
	}

	protected BlockPos getNextPumpablePosition() {
		while (!pumpTubeCache.isEmpty()) {
			BlockPos toPump = pumpTubeCache.peek();
			if (getLevel().getFluidState(toPump).isSource()) {
				return toPump;
			} else {
				pumpTubeCache.poll();
			}
		}
		return null;
	}

	protected PumpTubePathResult doesPumpTubePathExistToPosition(BlockPos fluidPos, Direction expectedDirection) {
		BlockPos queryPos = getBlockPos().relative(expectedDirection);
		BlockPos targetPos = WorldUtilities.projectPositionOntoLine(fluidPos, getBlockPos(),
				expectedDirection.getAxis());

		List<BlockPos> missingTubes = new ArrayList<>();
		while (!queryPos.equals(targetPos)) {
			if (!doesPositionContainValidPumpTube(queryPos, expectedDirection)) {
				missingTubes.add(queryPos);
			}
			queryPos = queryPos.relative(expectedDirection);
		}

		if (!doesPositionContainValidPumpTube(queryPos, expectedDirection)) {
			missingTubes.add(queryPos);
		}

		return new PumpTubePathResult(missingTubes.isEmpty(), missingTubes);
	}

	protected boolean doesPositionContainValidPumpTube(BlockPos pos, Direction expectedDirection) {
		BlockState testState = getLevel().getBlockState(pos);
		if (testState.getBlock() != ModBlocks.PumpTube.get()) {
			return false;
		}
		if (testState.getValue(BlockPumpTube.FACING).getAxis() != expectedDirection.getAxis()) {
			return false;
		}
		return true;
	}

	protected boolean pumpFluidAtPosition(BlockPos pumpPos) {
		FluidState fluidState = getLevel().getFluidState(pumpPos);
		FluidStack pumpedStack = new FluidStack(fluidState.getType(), 1000);
		int simulatedFill = this.fluidTankComponent.fill(pumpedStack, FluidAction.SIMULATE);
		if (simulatedFill != 1000) {
			return false;
		}

		if (fluidState.getType().getPickupSound().isPresent()) {
			getLevel().playSound(null, getBlockPos(), fluidState.getType().getPickupSound().get(), SoundSource.BLOCKS,
					0.5f, 1.0f);
		} else {
			getLevel().playSound(null, getBlockPos(), SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 0.5f, 1.0f);
		}
		powerStorage.drainPower(StaticPowerConfig.SERVER.pumpPowerUsage.get(), false);
		fluidTankComponent.fill(pumpedStack, FluidAction.EXECUTE);
		getLevel().setBlockAndUpdate(pumpPos, Blocks.AIR.defaultBlockState());
		return true;
	}

	protected PumpTubeCache getFluidSourcesSurroundingPosition(BlockPos queryPosition, Direction facingDirection) {
		Queue<BlockPos> bfsQueue = new LinkedList<>();
		PumpTubeCache output = new PumpTubeCache(queryPosition, facingDirection);

		for (Direction side : Direction.values()) {
			if (side == facingDirection || side == facingDirection.getOpposite()) {
				continue;
			}
			BlockPos pos = queryPosition.relative(side);
			FluidState fluidState = getLevel().getFluidState(pos);
			if (!fluidState.isEmpty()) {
				bfsQueue.add(pos);
			}
		}

		int searched = 0;
		Set<BlockPos> visited = new HashSet<BlockPos>();
		while (!bfsQueue.isEmpty()) {
			searched++;
			BlockPos queryPos = bfsQueue.poll();
			FluidState fluidState = getLevel().getFluidState(queryPos);
			if (fluidState.isSource()) {
				output.add(queryPos);
			}

			for (Direction side : Direction.values()) {
				if (side == facingDirection || side == facingDirection.getOpposite()) {
					continue;
				}

				BlockPos adjacentPos = queryPos.relative(side);
				if (!getLevel().getFluidState(adjacentPos).isEmpty() && !visited.contains(adjacentPos)) {
					bfsQueue.add(queryPos.relative(side));
					visited.add(adjacentPos);
				}
			}

			if (searched >= MAX_SEARCH_DEPTH) {
				break;
			}
		}

		if (!output.isEmpty()) {
			StaticPower.LOGGER
					.info(String.format("Cached new pump tube for pump at location: %1$s. New pump queue size: %2$d.",
							getBlockPos(), output.size()));
		} else {
			StaticPower.LOGGER.info(String.format("No fluids were found at location: %1$s.", getBlockPos()));
		}

		return output;
	}

	protected void placePumpTube(BlockPos tubePos, Direction facingDirection) {
		getLevel().setBlock(tubePos,
				ModBlocks.PumpTube.get().defaultBlockState().setValue(BlockPumpTube.FACING, facingDirection), 2);
		tubeInventory.getStackInSlot(0).shrink(1);
		getLevel().playSound(null, tubePos, SoundEvents.COPPER_PLACE, SoundSource.BLOCKS, 1.0f, 1.0f);
	}

	protected List<Direction> getSidesWithPump() {
		List<Direction> output = new ArrayList<Direction>();
		for (Direction dir : Direction.values()) {
			BlockPos posToCheck = getBlockPos().relative(dir);
			if (doesPositionContainValidPumpTube(posToCheck, dir)) {
				output.add(dir);
			}
		}
		return output;
	}

	@Override
	protected SideConfigurationPreset getDefaultSideConfiguration() {
		return PumpSideConfiguration.INSTANCE;
	}

	@Override
	protected void getAdditionalModelData(Builder builder) {
		super.getAdditionalModelData(builder);
		for (Direction dir : Direction.values()) {
			renderingState.setConnected(dir, false);

			BlockSide side = SideConfigurationUtilities.getBlockSide(dir, getFacingDirection());
			if (side == BlockSide.FRONT || side == BlockSide.BACK) {
				continue;
			}

			MachineSideMode sideMode = ioSideConfiguration.getWorldSpaceDirectionConfiguration(dir);
			if (!sideMode.isInputMode()) {
				continue;
			}

			BlockPos toCheck = getBlockPos().relative(dir);
			BlockState otherBlockState = getLevel().getBlockState(toCheck);
			if (otherBlockState.getBlock() == ModBlocks.PumpTube.get()) {
				renderingState.setConnected(dir, true);
				continue;
			}

			BlockEntity entity = getLevel().getBlockEntity(toCheck);
			if (entity != null) {
				IStaticPowerStorage powerStorage = entity
						.getCapability(CapabilityStaticPower.STATIC_VOLT_CAPABILITY, dir).orElse(null);
				if (powerStorage != null) {
					renderingState.setConnected(dir, powerStorage.canOutputExternalPower());
				} else {
					renderingState.setConnected(dir,
							entity.getCapability(ForgeCapabilities.ITEM_HANDLER, dir).isPresent());
				}
			}
		}
		builder.with(PUMP_RENDERING_STATE, renderingState.copy());
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerPump(windowId, inventory, this);
	}

	@Override
	public boolean shouldSerializeWhenBroken(Player player) {
		return false;
	}

	@Override
	public boolean shouldDeserializeWhenPlaced(CompoundTag nbt, Level world, BlockPos pos, BlockState state,
			LivingEntity placer, ItemStack stack) {
		return false;
	}

	public static class FluidPumpRenderingState {
		private Map<Direction, Boolean> connectionStates;

		public FluidPumpRenderingState() {
			connectionStates = new HashMap<>();
			for (Direction dir : Direction.values()) {
				connectionStates.put(dir, false);
			}
		}

		public void setConnected(Direction side, boolean connected) {
			connectionStates.put(side, connected);
		}

		public boolean hasConnection(Direction side) {
			return connectionStates.get(side);
		}

		public FluidPumpRenderingState copy() {
			FluidPumpRenderingState output = new FluidPumpRenderingState();
			for (Direction dir : Direction.values()) {
				output.setConnected(dir, connectionStates.get(dir));
			}
			return output;
		}
	}
}
