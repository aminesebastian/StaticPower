package theking530.staticpower.cables.redstone;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ObserverBlock;
import net.minecraft.block.RepeaterBlock;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.CableUtilities;
import theking530.staticpower.cables.network.CableNetworkManager;
import theking530.staticpower.cables.network.CableNetworkModuleTypes;
import theking530.staticpower.cables.network.ServerCable;
import theking530.staticpower.cables.network.ServerCable.CableConnectionState;
import theking530.staticpower.cables.redstone.network.PacketUpdateRedstoneCableConfiguration;
import theking530.staticpower.client.StaticPowerAdditionalModels;
import theking530.staticpower.network.StaticPowerMessageHandler;

public class RedstoneCableComponent extends AbstractCableProviderComponent {
	public static final String CONFIGURATION_KEY = "redstone_configuration";

	/**
	 * The side configuration for this cable.
	 */
	private RedstoneCableConfiguration configuration;
	/**
	 * The name of the network module this cable is a part of.
	 */
	private ResourceLocation moduleName;
	/**
	 * This value ONLY represents the client side power level for this cable.
	 */
	private int clientSidePowerLevel;

	public RedstoneCableComponent(String name, String selector) {
		super(name, getModuleType(selector));
		this.moduleName = this.getSupportedNetworkModuleTypes().stream().findFirst().get();

		// Initialize the side configuration.
		configuration = new RedstoneCableConfiguration();
		for (Direction dir : Direction.values()) {
			configuration.getSideConfig(dir).setSelector(selector);
		}
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

	@Override
	protected void initializeCableProperties(ServerCable cable) {
		cable.setProperty(CONFIGURATION_KEY, configuration.serializeNBT());
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
		// Check to make sure the side is not disabled and configured to be an output.
		if (!isSideDisabled(side.getOpposite()) && configuration.getSideConfig(side.getOpposite()).isOutputSide()) {
			AtomicInteger output = new AtomicInteger(0);
			getRedstoneNetworkModule().ifPresent((module) -> {
				output.set(module.getCurrentSignalStrength(configuration.getSideConfig(side.getOpposite()).getSelector()));
			});
			return output.get();
		}
		return clientSidePowerLevel;
	}

	@Override
	public int getStrongPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		// Check to make sure the side is not disabled and configured to be an output.
		if (!isSideDisabled(side.getOpposite()) && configuration.getSideConfig(side.getOpposite()).isOutputSide()) {
			AtomicInteger output = new AtomicInteger(0);
			getRedstoneNetworkModule().ifPresent((module) -> {
				output.set(module.getCurrentSignalStrength(configuration.getSideConfig(side.getOpposite()).getSelector()));
			});
			return output.get();
		}
		return clientSidePowerLevel;
	}

	/**
	 * Gets the redstone network module for the network this cable belongs to. We
	 * have to wrap it in an optional because while we can guarantee once this
	 * component is validated that the network is valid, since this component
	 * exposes external methods, other tile entities that are made valid before us
	 * may call some of our methods.
	 * 
	 * @return
	 */
	public Optional<RedstoneNetworkModule> getRedstoneNetworkModule() {
		return getNetworkModule(moduleName);
	}

	public RedstoneCableConfiguration getConfiguration() {
		return configuration;
	}

	public void updateConfiguration(RedstoneCableConfiguration configuration) {
		this.configuration = configuration;
		if (getWorld().isRemote) {
			// Send a packet to the server with the updated values.
			PacketUpdateRedstoneCableConfiguration msg = new PacketUpdateRedstoneCableConfiguration(getPos(), configuration);
			StaticPowerMessageHandler.MAIN_PACKET_CHANNEL.sendToServer(msg);
		} else {
			if (CableNetworkManager.get(getWorld()).isTrackingCable(getPos())) {
				CableNetworkManager.get(getWorld()).getCable(getPos()).setProperty(CONFIGURATION_KEY, configuration.serializeNBT());
				getWorld().notifyNeighborsOfStateChange(this.getPos(), getWorld().getBlockState(getPos()).getBlock());
			}
		}
	}

	@Override
	public CompoundNBT serializeUpdateNbt(CompoundNBT nbt, boolean fromUpdate) {
		super.serializeUpdateNbt(nbt, fromUpdate);

		// Serialize the side configs.
		nbt.put("config", configuration.serializeNBT());

		// Use this as a way to sync the client side value of the power level.
		if (getWorld() != null && !getWorld().isRemote) {
			nbt.putByte("power", (byte) clientSidePowerLevel);
		}

		return nbt;
	}

	@Override
	public void deserializeUpdateNbt(CompoundNBT nbt, boolean fromUpdate) {
		super.deserializeUpdateNbt(nbt, fromUpdate);

		// Deserialize the side config.
		RedstoneCableConfiguration sideConfig = new RedstoneCableConfiguration();
		sideConfig.deserializeNBT(nbt.getCompound("config"));
		configuration = sideConfig;

		// Use this as a way to sync the client side value of the power level.
		if (getWorld() != null && getWorld().isRemote) {
			clientSidePowerLevel = (int) nbt.getByte("power");
		}
	}

	@Override
	protected ResourceLocation getAttachmentModelForSide(Direction side) {
		if (getConnectionState(side) == CableConnectionState.TILE_ENTITY) {
			return configuration.getSideConfig(side).isInputSide() ? StaticPowerAdditionalModels.CABLE_REDSTONE_BASIC_ATTACHMENT_INPUT
					: StaticPowerAdditionalModels.CABLE_REDSTONE_BASIC_ATTACHMENT_OUTPUT;
		} else {
			return super.getAttachmentModelForSide(side);
		}
	}

	@Override
	protected boolean getInitialSideDisabledState(Direction side) {
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

}
