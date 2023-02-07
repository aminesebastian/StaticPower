package theking530.staticpower.blockentities.machines.fluid_pump;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.api.energy.CapabilityStaticPower;
import theking530.staticcore.cablenetwork.Cable;
import theking530.staticcore.cablenetwork.manager.CableNetworkAccessor;
import theking530.staticcore.cablenetwork.manager.CableNetworkManager;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.blockentities.BlockEntityMachine;
import theking530.staticpower.blockentities.components.control.sideconfiguration.DefaultSideConfiguration;
import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationUtilities;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticpower.blockentities.components.fluids.FluidInputServoComponent;
import theking530.staticpower.blockentities.components.fluids.FluidTankComponent;
import theking530.staticpower.blockentities.components.items.BatteryInventoryComponent;
import theking530.staticpower.blockentities.components.items.InventoryComponent;
import theking530.staticpower.cables.fluid.FluidNetworkModule;
import theking530.staticpower.client.rendering.blockentity.BlockEntityRenderFluidPump;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.cables.ModCableModules;

public class BlockEntityFluidPump extends BlockEntityMachine {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityFluidPump> TYPE = new BlockEntityTypeAllocator<BlockEntityFluidPump>("fluid_pump",
			(type, pos, state) -> new BlockEntityFluidPump(type, pos, state), ModBlocks.FluidPump);

	public static final DefaultSideConfiguration SIDE_CONFIGURATION = new DefaultSideConfiguration();
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setTileEntitySpecialRenderer(BlockEntityRenderFluidPump::new);
		}

		SIDE_CONFIGURATION.setSide(BlockSide.TOP, true, MachineSideMode.Input);
		SIDE_CONFIGURATION.setSide(BlockSide.BOTTOM, true, MachineSideMode.Input);
		SIDE_CONFIGURATION.setSide(BlockSide.FRONT, true, MachineSideMode.Input);
		SIDE_CONFIGURATION.setSide(BlockSide.BACK, true, MachineSideMode.Output);
		SIDE_CONFIGURATION.setSide(BlockSide.LEFT, true, MachineSideMode.Input);
		SIDE_CONFIGURATION.setSide(BlockSide.RIGHT, true, MachineSideMode.Input);
	}

	public final InventoryComponent batteryInventory;
	public final FluidTankComponent fluidTankComponent;
	private final FluidPumpRenderingState renderingState;

	public BlockEntityFluidPump(BlockEntityTypeAllocator<BlockEntityFluidPump> allocator, BlockPos pos, BlockState state) {
		super(allocator, pos, state);
		renderingState = new FluidPumpRenderingState();
		enableFaceInteraction();

		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", powerStorage));

		registerComponent(fluidTankComponent = new FluidTankComponent("FluidTank", 1000) {
			@Override
			public int fill(FluidStack resource, FluidAction action) {
				FluidStack resourceCopy = resource.copy();
				int pumped = performPumping(resourceCopy, action);
				resourceCopy.shrink(pumped);
				return pumped + super.fill(resourceCopy, action);
			}
		}.setCapabilityExposedModes(MachineSideMode.Input, MachineSideMode.Output));
		fluidTankComponent.setCanFill(true);
		fluidTankComponent.setAutoSyncPacketsEnabled(true);

		registerComponent(new FluidInputServoComponent("FluidInputServoComponent", 100, fluidTankComponent, MachineSideMode.Input));
	}

	@Override
	public void process() {
		if (!getLevel().isClientSide()) {
			int pumped = performPumping(this.fluidTankComponent.getFluid(), FluidAction.EXECUTE);
			this.fluidTankComponent.drain(pumped, FluidAction.EXECUTE);
		}
	}

	public void onNeighborReplaced(BlockState state, Direction direction, BlockState facingState, BlockPos FacingPos) {
		super.onNeighborReplaced(state, direction, facingState, FacingPos);
		requestModelDataUpdate();
	}

	protected int performPumping(FluidStack fluid, FluidAction action) {
		if (getLevel().isClientSide() || fluid.isEmpty()) {
			return 0;
		}

		if (powerStorage.drainPower(1, true).getPower() != 1) {
			return 0;
		}

		if (!redstoneControlComponent.passesRedstoneCheck()) {
			return 0;
		}

		FluidStack resourceCopy = fluid.copy();
		for (Direction dir : Direction.values()) {
			MachineSideMode sideMode = ioSideConfiguration.getWorldSpaceDirectionConfiguration(dir);
			if (!sideMode.isOutputMode()) {
				continue;

			}

			BlockPos targetPos = getBlockPos().relative(dir);
			CableNetworkManager manager = CableNetworkAccessor.get(getLevel());
			Cable cable = manager.getCable(targetPos);
			if (cable == null || cable.getNetwork() == null) {
				continue;
			}

			FluidNetworkModule module = cable.getNetwork().getModule(ModCableModules.Fluid.get());
			if (module == null) {
				continue;
			}

			int supplied = module.fill(targetPos, resourceCopy, 32.0f, action);
			resourceCopy.shrink(supplied);

			if (resourceCopy.isEmpty()) {
				break;
			}
		}

		int pumped = fluid.getAmount() - resourceCopy.getAmount();
		if (pumped > 0) {
			powerStorage.drainPower(1, false);
		}
		return pumped;
	}

	protected boolean isValidSideConfiguration(BlockSide side, MachineSideMode mode) {
		if (side != BlockSide.BACK && side != BlockSide.FRONT) {
			return mode == MachineSideMode.Input || mode == MachineSideMode.Disabled;
		}
		return mode == MachineSideMode.Output || mode == MachineSideMode.Input;
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

	protected DefaultSideConfiguration getDefaultSideConfiguration() {
		return SIDE_CONFIGURATION;
	}

	@Nonnull
	@Override
	protected void getAdditionalModelData(Builder builder) {
		super.getAdditionalModelData(builder);
		for (Direction dir : Direction.values()) {
			renderingState.setPowerConnectedStatus(dir, false);

			BlockSide side = SideConfigurationUtilities.getBlockSide(dir, getFacingDirection());
			if (side == BlockSide.FRONT || side == BlockSide.BACK) {
				continue;
			}

			BlockPos toCheck = getBlockPos().relative(dir);
			BlockEntity entity = getLevel().getBlockEntity(toCheck);
			if (entity != null) {
				renderingState.setPowerConnectedStatus(dir, entity.getCapability(CapabilityStaticPower.STATIC_VOLT_CAPABILITY, dir).isPresent());
			}
		}
		builder.with(PUMP_RENDERING_STATE, renderingState.copy());
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerFluidPump(windowId, inventory, this);
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
		private Map<Direction, Boolean> powerConnections;

		public FluidPumpRenderingState() {
			powerConnections = new HashMap<>();
			for (Direction dir : Direction.values()) {
				powerConnections.put(dir, false);
			}
		}

		public void setPowerConnectedStatus(Direction side, boolean connected) {
			powerConnections.put(side, connected);
		}

		public boolean hasPowerConnectedStatus(Direction side) {
			return powerConnections.get(side);
		}

		public FluidPumpRenderingState copy() {
			FluidPumpRenderingState output = new FluidPumpRenderingState();
			for (Direction dir : Direction.values()) {
				output.setPowerConnectedStatus(dir, powerConnections.get(dir));
			}
			return output;
		}
	}
}
