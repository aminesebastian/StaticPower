package theking530.staticpower.blockentities.machines.fluid_pump;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.cablenetwork.Cable;
import theking530.staticcore.cablenetwork.manager.CableNetworkAccessor;
import theking530.staticcore.cablenetwork.manager.CableNetworkManager;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.blockentities.BlockEntityBase;
import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticpower.blockentities.components.fluids.FluidTankComponent;
import theking530.staticpower.cables.fluid.FluidNetworkModule;
import theking530.staticpower.client.rendering.blockentity.BlockEntityRenderFluidPump;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.cables.ModCableModules;

public class BlockEntityFluidPump extends BlockEntityBase {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityFluidPump> TYPE = new BlockEntityTypeAllocator<BlockEntityFluidPump>("fluid_pump",
			(type, pos, state) -> new BlockEntityFluidPump(type, pos, state), ModBlocks.FluidPump);

	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setTileEntitySpecialRenderer(BlockEntityRenderFluidPump::new);
		}
	}

	public final FluidTankComponent fluidTankComponent;
	public final SideConfigurationComponent ioSideConfiguration;

	public BlockEntityFluidPump(BlockEntityTypeAllocator<BlockEntityFluidPump> allocator, BlockPos pos, BlockState state) {
		super(allocator, pos, state);

		// Add the tank component.
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

		// Add the side configuration component.
		registerComponent(ioSideConfiguration = new SideConfigurationComponent("SideConfiguration", this::onSidesConfigUpdate, this::isValidSideConfiguration,
				SideConfigurationComponent.FRONT_BACK_INPUT_OUTPUT));

	}

	@Override
	public void process() {
		if (!getLevel().isClientSide()) {
			int pumped = performPumping(this.fluidTankComponent.getFluid(), FluidAction.EXECUTE);
			this.fluidTankComponent.drain(pumped, FluidAction.EXECUTE);
		}
	}

	protected int performPumping(FluidStack fluid, FluidAction action) {
		if (getLevel().isClientSide() || fluid.isEmpty()) {
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

		return fluid.getAmount() - resourceCopy.getAmount();
	}

	protected boolean isValidSideConfiguration(BlockSide side, MachineSideMode mode) {
		if (side != BlockSide.BACK && side != BlockSide.FRONT) {
			return mode == MachineSideMode.Never;
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
}
