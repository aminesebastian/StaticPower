package theking530.staticpower.blockentities.machines.pump;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import javax.annotation.Nonnull;

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
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.StaticPower;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blockentities.BlockEntityMachine;
import theking530.staticpower.blockentities.components.control.processing.MachineProcessingComponent;
import theking530.staticpower.blockentities.components.control.processing.ProcessingCheckState;
import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationPreset;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationUtilities;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticpower.blockentities.components.energy.IPowerStorageComponentFilter;
import theking530.staticpower.blockentities.components.fluids.FluidInputServoComponent;
import theking530.staticpower.blockentities.components.fluids.FluidTankComponent;
import theking530.staticpower.blockentities.components.fluids.IFluidTankComponentFilter;
import theking530.staticpower.blockentities.components.items.BatteryInventoryComponent;
import theking530.staticpower.blockentities.components.items.InventoryComponent;
import theking530.staticpower.blockentities.components.loopingsound.LoopingSoundComponent;
import theking530.staticpower.client.rendering.blockentity.BlockEntityRenderPump;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.init.ModBlocks;

public class BlockEntityPump extends BlockEntityMachine {
	private enum FluidPumpResult {
		NONE, PASSIVE_SUPPLY, ACTIVE_SUPPLY
	}

	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityPump> TYPE = new BlockEntityTypeAllocator<BlockEntityPump>("pump",
			(type, pos, state) -> new BlockEntityPump(type, pos, state), ModBlocks.BasicPump, ModBlocks.AdvancedPump, ModBlocks.StaticPump, ModBlocks.EnergizedPump,
			ModBlocks.LumumPump, ModBlocks.CreativePump);
	public static final ModelProperty<FluidPumpRenderingState> PUMP_RENDERING_STATE = new ModelProperty<FluidPumpRenderingState>();
	public static final int MAX_SEARCH_DEPTH = 1000;
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setTileEntitySpecialRenderer(BlockEntityRenderPump::new);
		}
	}

	public final InventoryComponent batteryInventory;
	public final FluidTankComponent fluidTankComponent;
	public final MachineProcessingComponent processingComponent;
	private final LoopingSoundComponent soundComponent;
	private final FluidPumpRenderingState renderingState;
	private final Queue<BlockPos> positionsWithSourceFluids;
	private BlockPos lastQuerriedPumpTubePos;

	public BlockEntityPump(BlockEntityTypeAllocator<BlockEntityPump> allocator, BlockPos pos, BlockState state) {
		super(allocator, pos, state);
		renderingState = new FluidPumpRenderingState();
		positionsWithSourceFluids = new LinkedList<>();

		// Get the tier.
		StaticPowerTier tierObject = getTierObject();
		int pumpRate = tierObject.pumpRate.get();

		registerComponent(soundComponent = new LoopingSoundComponent("SoundComponent", 20));
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", powerStorage));
		registerComponent(processingComponent = new MachineProcessingComponent("ProcessingComponent", pumpRate, this::canProcess, this::canProcess, this::performPumping, true)
				.setRedstoneControlComponent(redstoneControlComponent).setPowerComponent(powerStorage).setShouldModuleProcessingTimeByPowerSatisfaction(false));

		registerComponent(fluidTankComponent = new FluidTankComponent("FluidTank", StaticPowerConfig.SERVER.pumpTankCapacity.get()));
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

		registerComponent(new FluidInputServoComponent("FluidInputServoComponent", 1000, fluidTankComponent, MachineSideMode.Input));
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
			soundComponent.startPlayingSound(SoundEvents.AMBIENT_UNDERWATER_LOOP, SoundSource.BLOCKS, 0.5f, 0.9f, getBlockPos(), 32);
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
		IStaticPowerFluidHandler staticFluidHandler = te.getCapability(CapabilityStaticFluid.STATIC_FLUID_CAPABILITY, side.getOpposite()).orElse(null);
		if (staticFluidHandler != null) {
			float pressure = this.powerStorage.drainPower(1, true).getPower() == 1 ? IStaticPowerFluidHandler.MAX_PRESSURE : IStaticPowerFluidHandler.PASSIVE_FLOW_PRESSURE;
			FluidStack simulatedDrain = fluidTankComponent.drain(fluidTankComponent.getCapacity(), FluidAction.SIMULATE);
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

	public void onNeighborReplaced(BlockState state, Direction direction, BlockState facingState, BlockPos FacingPos) {
		super.onNeighborReplaced(state, direction, facingState, FacingPos);
		requestModelDataUpdate();
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

	public ProcessingCheckState canProcess() {
		if (this.powerStorage.getStoredPower() < StaticPowerConfig.SERVER.pumpPowerUsage.get()) {
			return ProcessingCheckState.notEnoughPower(StaticPowerConfig.SERVER.pumpPowerUsage.get());
		}
		if ((fluidTankComponent.getFluidAmount() + 1000) > fluidTankComponent.getCapacity()) {
			return ProcessingCheckState.fluidOutputFull();
		}

		return ProcessingCheckState.ok();
	}

	protected ProcessingCheckState performPumping() {
		List<Direction> sidesToPumpFrom = getSidesWithPump();
		if (sidesToPumpFrom.isEmpty()) {
			return ProcessingCheckState.cancel();
		}
		
		//TODO: Ensure pump tube chain is unbroken up to lastQuerriedPumpTubePos.

		while (!positionsWithSourceFluids.isEmpty()) {
			BlockPos toPump = positionsWithSourceFluids.peek();
			FluidState fluidState = getLevel().getFluidState(toPump);
			if (fluidState.isSource()) {
				if (pumpPos(toPump)) {
					positionsWithSourceFluids.poll();
					return ProcessingCheckState.ok();
				}
			} else {
				positionsWithSourceFluids.poll();
			}
		}

		// Try to place a new pump tube along a random direction.
		if (positionsWithSourceFluids.isEmpty()) {
			Direction sideWithPumpTube = sidesToPumpFrom.get(getLevel().getRandom().nextIntBetweenInclusive(0, sidesToPumpFrom.size() - 1));
			BlockPos pumpTubePos = getBlockPos().relative(sideWithPumpTube);
			while (doesPositionContainValidPumpTube(pumpTubePos.relative(sideWithPumpTube), sideWithPumpTube)) {
				pumpTubePos = pumpTubePos.relative(sideWithPumpTube);
			}
			pushNewPumpTube(pumpTubePos.relative(sideWithPumpTube), sideWithPumpTube);
		}

		// IF after pushing a new pump tube, we have valid fluid positions, we
		// succeeded.
		if (!positionsWithSourceFluids.isEmpty()) {
			return ProcessingCheckState.ok();
		}

		return ProcessingCheckState.error("No pumpable fluids found in range of pump tubes!");
	}

	public boolean doesPositionContainValidPumpTube(BlockPos pos, Direction expectedDirection) {
		BlockState testState = getLevel().getBlockState(pos);
		if (testState.getBlock() != ModBlocks.PumpTube.get()) {
			return false;
		}
		if (testState.getValue(BlockPumpTube.FACING).getAxis() != expectedDirection.getAxis()) {
			return false;
		}
		return true;
	}

	protected boolean pumpPos(BlockPos pumpPos) {
		FluidState fluidState = getLevel().getFluidState(pumpPos);
		if (!fluidState.getType().isSource(fluidState)) {
			return false;
		}

		FluidStack pumpedStack = new FluidStack(fluidState.getType(), 1000);
		int simulatedPump = fluidTankComponent.fill(pumpedStack, FluidAction.SIMULATE);
		if (simulatedPump == 1000) {
			if (fluidState.getType().getPickupSound().isPresent()) {
				getLevel().playSound(null, getBlockPos(), fluidState.getType().getPickupSound().get(), SoundSource.BLOCKS, 0.5f, 1.0f);
			} else {
				getLevel().playSound(null, getBlockPos(), SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 0.5f, 1.0f);
			}
			powerStorage.drainPower(StaticPowerConfig.SERVER.pumpPowerUsage.get(), false);
			fluidTankComponent.fill(pumpedStack, FluidAction.EXECUTE);
			getLevel().setBlockAndUpdate(pumpPos, Blocks.AIR.defaultBlockState());
			return true;
		}
		return false;
	}

	protected void pushNewPumpTube(BlockPos tubePos, Direction facingDirection) {
		Queue<BlockPos> bfsQueue = new LinkedList<>();

		for (Direction side : Direction.values()) {
			if (side == facingDirection || side == facingDirection.getOpposite()) {
				continue;
			}
			BlockPos pos = tubePos.relative(side);
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
				positionsWithSourceFluids.add(queryPos);
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

		StaticPower.LOGGER.info(String.format("Pushed new pump tube for pump at location: %1$s. New pump queue size: %2$d.", getBlockPos(), positionsWithSourceFluids.size()));
		getLevel().setBlock(tubePos,
				ModBlocks.PumpTube.get().defaultBlockState().setValue(BlockPumpTube.AUTOMATICALLY_PLACED, true).setValue(BlockPumpTube.FACING, facingDirection), 2);
		lastQuerriedPumpTubePos = tubePos;
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

	@Nonnull
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
				IStaticPowerStorage powerStorage = entity.getCapability(CapabilityStaticPower.STATIC_VOLT_CAPABILITY, dir).orElse(null);
				if (powerStorage != null) {
					renderingState.setConnected(dir, powerStorage.canOutputExternalPower());
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
	public boolean shouldDeserializeWhenPlaced(CompoundTag nbt, Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
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
