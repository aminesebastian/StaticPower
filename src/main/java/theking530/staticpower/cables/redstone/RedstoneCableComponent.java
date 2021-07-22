package theking530.staticpower.cables.redstone;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ObserverBlock;
import net.minecraft.block.RepeaterBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.CableUtilities;
import theking530.staticpower.cables.network.CableNetworkModuleTypes;
import theking530.staticpower.cables.network.ServerCable;
import theking530.staticpower.cables.network.ServerCable.CableConnectionState;

public class RedstoneCableComponent extends AbstractCableProviderComponent {
	public static final String SELECTOR_KEY = "selector";
	private final String selector;

	public RedstoneCableComponent(String name, String selector) {
		super(name, CableNetworkModuleTypes.REDSTONE_NETWORK_MODULE);
		this.selector = selector;
	}

	@Override
	public void preProcessUpdate() {
		super.preProcessUpdate();
	}

	@Override
	protected boolean canAttachAttachment(ItemStack attachment) {
		return false;
	}

	/**
	 * Gets the heat network module for the network this cable belongs to. We have
	 * to wrap it in an optional because while we can guarantee once this component
	 * is validated that the network is valid, since this component exposes external
	 * methods, other tile entities that are made valid before us may call some of
	 * our methods.
	 * 
	 * @return
	 */
	public Optional<RedstoneNetworkModule> getRedstoneNetworkModule() {
		return getNetworkModule(CableNetworkModuleTypes.REDSTONE_NETWORK_MODULE);
	}

	@Override
	protected void initializeCableProperties(ServerCable cable) {
		cable.setProperty(SELECTOR_KEY, selector);
	}

	@Override
	protected CableConnectionState cacheConnectionState(Direction side, @Nullable TileEntity te, BlockPos blockPosition) {
		AbstractCableProviderComponent otherProvider = CableUtilities.getCableWrapperComponent(getWorld(), blockPosition);
		if (otherProvider != null && otherProvider.areCableCompatible(this, side)) {
			if (!otherProvider.isSideDisabled(side.getOpposite())) {
				return CableConnectionState.CABLE;
			}
		} else if (otherProvider == null) {
			if (canConnectTo(getWorld(), getPos(), side.getOpposite())) {
				return CableConnectionState.TILE_ENTITY;
			}
		}
		return CableConnectionState.NONE;
	}

	@SuppressWarnings("deprecation")
	public static boolean canConnectTo(World world, BlockPos blockPos, @Nullable Direction side) {
		BlockState blockState = world.getBlockState(blockPos.offset(side.getOpposite()));
		if (blockState.isIn(Blocks.REDSTONE_WIRE)) {
			return true;
		} else if (blockState.isIn(Blocks.REPEATER)) {
			Direction direction = blockState.get(RepeaterBlock.HORIZONTAL_FACING);
			return direction == side || direction.getOpposite() == side;
		} else if (blockState.isIn(Blocks.OBSERVER)) {
			return side == blockState.get(ObserverBlock.FACING);
		} else {
			return side != null && !blockState.isAir() && !(blockState.getBlock() instanceof BlockRedstoneCable);
		}
	}

	@Override
	public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		AtomicInteger output = new AtomicInteger(0);
		getRedstoneNetworkModule().ifPresent((module) -> {
			output.set(module.getCurrentSignalStrength(selector));
		});
		return output.get();
	}

	@Override
	public int getStrongPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		AtomicInteger output = new AtomicInteger(0);
		getRedstoneNetworkModule().ifPresent((module) -> {
			output.set(module.getCurrentSignalStrength(selector));
		});
		return output.get();
	}

	@Override
	public <T> LazyOptional<T> provideCapability(Capability<T> cap, Direction side) {
		return LazyOptional.empty();
	}

}
