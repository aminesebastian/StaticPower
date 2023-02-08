package theking530.staticpower.blockentities.machines.pump;

import java.util.HashMap;
import java.util.Map;

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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.model.data.ModelData.Builder;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.api.energy.CapabilityStaticPower;
import theking530.api.energy.IStaticPowerStorage;
import theking530.api.fluid.CapabilityStaticFluid;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.blockentities.BlockEntityMachine;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationPreset;
import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationUtilities;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticpower.blockentities.components.energy.IPowerStorageComponentFilter;
import theking530.staticpower.blockentities.components.fluids.FluidInputServoComponent;
import theking530.staticpower.blockentities.components.fluids.FluidOutputServoComponent;
import theking530.staticpower.blockentities.components.fluids.FluidTankComponent;
import theking530.staticpower.blockentities.components.fluids.IFluidTankComponentFilter;
import theking530.staticpower.blockentities.components.items.BatteryInventoryComponent;
import theking530.staticpower.blockentities.components.items.InventoryComponent;
import theking530.staticpower.blockentities.components.loopingsound.LoopingSoundComponent;
import theking530.staticpower.client.rendering.blockentity.BlockEntityRenderFluidPump;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.init.ModBlocks;

public class BlockEntityPump extends BlockEntityMachine {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityPump> TYPE = new BlockEntityTypeAllocator<BlockEntityPump>("pump",
			(type, pos, state) -> new BlockEntityPump(type, pos, state), ModBlocks.BasicPump, ModBlocks.AdvancedPump, ModBlocks.StaticPump, ModBlocks.EnergizedPump,
			ModBlocks.LumumPump, ModBlocks.CreativePump);

	public static final SideConfigurationPreset SIDE_CONFIGURATION = new SideConfigurationPreset();
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setTileEntitySpecialRenderer(BlockEntityRenderFluidPump::new);
		}

		SIDE_CONFIGURATION.setSide(BlockSide.TOP, true, MachineSideMode.Input2);
		SIDE_CONFIGURATION.setSide(BlockSide.BOTTOM, true, MachineSideMode.Input2);
		SIDE_CONFIGURATION.setSide(BlockSide.FRONT, true, MachineSideMode.Input);
		SIDE_CONFIGURATION.setSide(BlockSide.BACK, true, MachineSideMode.Output);
		SIDE_CONFIGURATION.setSide(BlockSide.LEFT, true, MachineSideMode.Input2);
		SIDE_CONFIGURATION.setSide(BlockSide.RIGHT, true, MachineSideMode.Input2);
	}

	public final InventoryComponent batteryInventory;
	public final FluidTankComponent fluidTankComponent;
	private final LoopingSoundComponent soundComponent;
	private final FluidPumpRenderingState renderingState;
	private final FluidOutputServoComponent outputServo;

	public BlockEntityPump(BlockEntityTypeAllocator<BlockEntityPump> allocator, BlockPos pos, BlockState state) {
		super(allocator, pos, state);
		StaticPowerTier tierObject = getTierObject();
		renderingState = new FluidPumpRenderingState();

		enableFaceInteraction();
		registerComponent(soundComponent = new LoopingSoundComponent("SoundComponent", 20));
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", powerStorage));

		registerComponent(fluidTankComponent = new FluidTankComponent("FluidTank", tierObject.defaultTankCapacity.get()));
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

		registerComponent(new FluidInputServoComponent("FluidInputServoComponent", 100, fluidTankComponent, MachineSideMode.Input));
		registerComponent(outputServo = new FluidOutputServoComponent("FluidOutputServoComponent", 100, fluidTankComponent, MachineSideMode.Output));
	}

	@Override
	public void process() {
		if (!getLevel().isClientSide()) {
			if (isPerformingWork()) {
				soundComponent.startPlayingSound(SoundEvents.AMBIENT_UNDERWATER_LOOP, SoundSource.BLOCKS, 0.25f, 0.75f, getBlockPos(), 32);
				powerStorage.drainPower(1, false);
				outputServo.setOutputPressure(32);
			} else {
				soundComponent.stopPlayingSound();
				outputServo.setOutputPressure(16);
			}
		}
	}

	public boolean isPerformingWork() {
		return powerStorage.drainPower(1, true).getPower() >= 1 && hasConnectedPressureTarget();
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

	protected void onSidesConfigUpdate(BlockSide side, MachineSideMode newMode) {
		MachineSideMode frontMode = ioSideConfiguration.getBlockSideConfiguration(BlockSide.FRONT);
		MachineSideMode backMode = ioSideConfiguration.getBlockSideConfiguration(BlockSide.BACK);

		if (frontMode != backMode) {
			return;
		}

		if (side == BlockSide.FRONT) {
			if (frontMode.isInputMode()) {
				ioSideConfiguration.setBlockSpaceConfiguration(BlockSide.BACK, MachineSideMode.Output);
			} else if (frontMode.isOutputMode()) {
				ioSideConfiguration.setBlockSpaceConfiguration(BlockSide.BACK, MachineSideMode.Input);
			}
		} else if (side == BlockSide.BACK) {
			if (backMode.isInputMode()) {
				ioSideConfiguration.setBlockSpaceConfiguration(BlockSide.FRONT, MachineSideMode.Output);
			} else if (backMode.isOutputMode()) {
				ioSideConfiguration.setBlockSpaceConfiguration(BlockSide.FRONT, MachineSideMode.Input);
			}
		}
	}

	protected SideConfigurationPreset getDefaultSideConfiguration() {
		return SIDE_CONFIGURATION;
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
	public boolean shouldSerializeWhenBroken() {
		return false;
	}

	@Override
	public boolean shouldDeserializeWhenPlaced(CompoundTag nbt, Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		return false;
	}

	public static final ModelProperty<FluidPumpRenderingState> PUMP_RENDERING_STATE = new ModelProperty<FluidPumpRenderingState>();

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
