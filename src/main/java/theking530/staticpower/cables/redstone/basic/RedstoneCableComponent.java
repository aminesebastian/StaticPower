package theking530.staticpower.cables.redstone.basic;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ObserverBlock;
import net.minecraft.world.level.block.RepeaterBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.CableUtilities;
import theking530.staticpower.cables.network.CableNetworkManager;
import theking530.staticpower.cables.network.CableNetworkModuleTypes;
import theking530.staticpower.cables.network.ServerCable;
import theking530.staticpower.cables.network.ServerCable.CableConnectionState;
import theking530.staticpower.cables.redstone.RedstoneCableConfiguration;
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
	protected CableConnectionState getUncachedConnectionState(Direction side, @Nullable BlockEntity te, BlockPos blockPosition, boolean firstWorldLoaded) {
		AbstractCableProviderComponent otherProvider = CableUtilities.getCableWrapperComponent(getWorld(), blockPosition);
		if (otherProvider != null) {
			if (otherProvider.areCableCompatible(this, side)) {
				if (!otherProvider.isSideDisabled(side.getOpposite())) {
					return CableConnectionState.CABLE;
				}
			} else {
				return getSupportedNetworkModuleTypes().contains(CableNetworkModuleTypes.REDSTONE_NETWORK_MODULE) ||  otherProvider.getSupportedNetworkModuleTypes().contains(CableNetworkModuleTypes.BUNDLED_REDSTONE_NETWORK_MODULE) || otherProvider.getSupportedNetworkModuleTypes().contains(CableNetworkModuleTypes.REDSTONE_NETWORK_MODULE) ? CableConnectionState.TILE_ENTITY: CableConnectionState.NONE;
			}
		} else if (!firstWorldLoaded && otherProvider == null) {
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
	public static boolean canConnectTo(Level world, BlockPos blockPos, @Nullable Direction side) {
		BlockState blockState = world.getBlockState(blockPos.relative(side.getOpposite()));
		if (blockState.is(Blocks.REDSTONE_WIRE)) {
			return true;
		} else if (blockState.is(Blocks.REPEATER)) {
			Direction direction = blockState.getValue(RepeaterBlock.FACING);
			return direction == side || direction.getOpposite() == side;
		} else if (blockState.is(Blocks.OBSERVER)) {
			return side == blockState.getValue(ObserverBlock.FACING);
		} else {
			return side != null && !blockState.isAir() && !(blockState.getBlock() instanceof BlockRedstoneCable);
		}
	}

	@Override
	public int getWeakPower(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
		// Check to make sure the side is not disabled and configured to be an output.
		if (!isSideDisabled(side.getOpposite()) && !configuration.getSideConfig(side.getOpposite()).isInputSide()) {
			AtomicInteger output = new AtomicInteger(0);
			getRedstoneNetworkModule().ifPresent((module) -> {
				output.set(module.getNetworkSignalStrength(configuration.getSideConfig(side.getOpposite()).getSelector()));
			});
			return output.get();
		}
		return 0;
	}

	@Override
	public int getStrongPower(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
		// Check to make sure the side is not disabled and configured to be an output.
		if (!isSideDisabled(side.getOpposite()) && !configuration.getSideConfig(side.getOpposite()).isInputSide()) {
			AtomicInteger output = new AtomicInteger(0);
			getRedstoneNetworkModule().ifPresent((module) -> {
				output.set(module.getNetworkSignalStrength(configuration.getSideConfig(side.getOpposite()).getSelector()));
			});
			return output.get();
		}
		return 0;
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
		if (getWorld().isClientSide) {
			// Send a packet to the server with the updated values.
			PacketUpdateRedstoneCableConfiguration msg = new PacketUpdateRedstoneCableConfiguration(getPos(), configuration);
			StaticPowerMessageHandler.MAIN_PACKET_CHANNEL.sendToServer(msg);
		} else {
			if (CableNetworkManager.get(getWorld()).isTrackingCable(getPos())) {
				CableNetworkManager.get(getWorld()).getCable(getPos()).setProperty(CONFIGURATION_KEY, configuration.serializeNBT());
				getWorld().updateNeighborsAt(this.getPos(), getWorld().getBlockState(getPos()).getBlock());
			}
		}
	}

	@Override
	public CompoundTag serializeUpdateNbt(CompoundTag nbt, boolean fromUpdate) {
		super.serializeUpdateNbt(nbt, fromUpdate);

		// Serialize the side configs.
		nbt.put("config", configuration.serializeNBT());

		// Use this as a way to sync the client side value of the power level.
		if (getWorld() != null && !getWorld().isClientSide) {
			nbt.putByte("power", (byte) clientSidePowerLevel);
		}

		return nbt;
	}

	@Override
	public void deserializeUpdateNbt(CompoundTag nbt, boolean fromUpdate) {
		super.deserializeUpdateNbt(nbt, fromUpdate);

		// Deserialize the side config.
		RedstoneCableConfiguration sideConfig = new RedstoneCableConfiguration();
		sideConfig.deserializeNBT(nbt.getCompound("config"));
		configuration = sideConfig;

		// Use this as a way to sync the client side value of the power level.
		if (getWorld() != null && getWorld().isClientSide) {
			clientSidePowerLevel = (int) nbt.getByte("power");
		}
	}

	@Override
	protected ResourceLocation getAttachmentModelForSide(Direction side) {
		if (getConnectionState(side) == CableConnectionState.TILE_ENTITY) {
			if (configuration.getSideConfig(side).isInputSide()) {
				return StaticPowerAdditionalModels.CABLE_REDSTONE_BASIC_ATTACHMENT_INPUT;
			} else if (configuration.getSideConfig(side).isOutputSide()) {
				return StaticPowerAdditionalModels.CABLE_REDSTONE_BASIC_ATTACHMENT_OUTPUT;
			} else {
				return StaticPowerAdditionalModels.CABLE_REDSTONE_BASIC_ATTACHMENT_INPUT_OUTPUT;
			}
		} else {
			return super.getAttachmentModelForSide(side);
		}
	}

	@Override
	protected boolean getInitialSideDisabledState(Direction side) {

		return false;// getWorld().getBlockState(getPos().offset(side)).isIn(Blocks.GRASS_BLOCK);
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
