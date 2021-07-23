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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.CableUtilities;
import theking530.staticpower.cables.attachments.basicredstoneio.BasicRedstoneIO;
import theking530.staticpower.cables.network.CableNetworkModuleTypes;
import theking530.staticpower.cables.network.ServerCable;
import theking530.staticpower.cables.network.ServerCable.CableConnectionState;

public class RedstoneCableComponent extends AbstractCableProviderComponent {
	public static final String SELECTOR_KEY = "selector";
	private final String selector;
	private ResourceLocation moduleName;

	public RedstoneCableComponent(String name, String selector) {
		super(name, getModuleType(selector));
		this.moduleName = getModuleType(selector);
		this.selector = selector;
		this.addValidAttachmentClass(BasicRedstoneIO.class);
	}

	@Override
	public void preProcessUpdate() {
		super.preProcessUpdate();
	}

	@Override
	protected boolean canAttachAttachment(ItemStack attachment) {
		return false;
	}

	protected static ResourceLocation getModuleType(String selector) {
		switch (selector) {
		case "black":
			return CableNetworkModuleTypes.REDSTONE_NETWORK_MODULE_BLACK;
		case "dark_blue":
			return CableNetworkModuleTypes.REDSTONE_NETWORK_MODULE_DARK_BLUE;
		case "dark_green":
			return CableNetworkModuleTypes.REDSTONE_NETWORK_MODULE_DARK_GREEN;
		case "dark_aqua":
			return CableNetworkModuleTypes.REDSTONE_NETWORK_MODULE_DARK_AQUA;
		case "dark_red":
			return CableNetworkModuleTypes.REDSTONE_NETWORK_MODULE_DARK_RED;
		case "dark_purple":
			return CableNetworkModuleTypes.REDSTONE_NETWORK_MODULE_DARK_PURPLE;
		case "gold":
			return CableNetworkModuleTypes.REDSTONE_NETWORK_MODULE_GOLD;
		case "gray":
			return CableNetworkModuleTypes.REDSTONE_NETWORK_MODULE_GRAY;
		case "dark_gray":
			return CableNetworkModuleTypes.REDSTONE_NETWORK_MODULE_DARK_GRAY;
		case "blue":
			return CableNetworkModuleTypes.REDSTONE_NETWORK_MODULE_BLUE;
		case "green":
			return CableNetworkModuleTypes.REDSTONE_NETWORK_MODULE_GREEN;
		case "aqua":
			return CableNetworkModuleTypes.REDSTONE_NETWORK_MODULE_AQUA;
		case "red":
			return CableNetworkModuleTypes.REDSTONE_NETWORK_MODULE_RED;
		case "light_purple":
			return CableNetworkModuleTypes.REDSTONE_NETWORK_MODULE_LIGHT_PURPLE;
		case "yellow":
			return CableNetworkModuleTypes.REDSTONE_NETWORK_MODULE_YELLOW;
		case "white":
			return CableNetworkModuleTypes.REDSTONE_NETWORK_MODULE_WHITE;
		default:
			return CableNetworkModuleTypes.REDSTONE_NETWORK_MODULE;
		}
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
		return getNetworkModule(moduleName);
	}

	public String getSelector() {
		return selector;
	}

	@Override
	protected void initializeCableProperties(ServerCable cable) {
		cable.setProperty(SELECTOR_KEY, selector);
	}

	@Override
	protected CableConnectionState cacheConnectionState(Direction side, @Nullable TileEntity te, BlockPos blockPosition) {
		AbstractCableProviderComponent otherProvider = CableUtilities.getCableWrapperComponent(getWorld(), blockPosition);
		if (otherProvider != null) {
			if (otherProvider.areCableCompatible(this, side)) {
				if (!otherProvider.isSideDisabled(side.getOpposite())) {
					return CableConnectionState.CABLE;
				}
			} else {
				return getSupportedNetworkModuleTypes().contains(CableNetworkModuleTypes.REDSTONE_NETWORK_MODULE)
						|| otherProvider.getSupportedNetworkModuleTypes().contains(CableNetworkModuleTypes.REDSTONE_NETWORK_MODULE) ? CableConnectionState.TILE_ENTITY
								: CableConnectionState.NONE;
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
	public boolean areCableCompatible(AbstractCableProviderComponent otherProvider, Direction side) {
		return super.areCableCompatible(otherProvider, side);
	}

	@Override
	public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		// Check to make sure the side is not disabled. If it is, return 0.
		if (isSideDisabled(side.getOpposite())) {
			return 0;
		}

		AtomicInteger output = new AtomicInteger(0);
		getRedstoneNetworkModule().ifPresent((module) -> {
			output.set(module.getCurrentSignalStrength(selector));
		});
		return output.get();
	}

	@Override
	public int getStrongPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		// Check to make sure the side is not disabled. If it is, return 0.
		if (isSideDisabled(side.getOpposite())) {
			return 0;
		}

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
