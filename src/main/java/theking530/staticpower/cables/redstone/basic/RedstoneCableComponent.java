package theking530.staticpower.cables.redstone.basic;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ObserverBlock;
import net.minecraft.world.level.block.RepeaterBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.cablenetwork.CableNetworkManager;
import theking530.staticcore.cablenetwork.CableUtilities;
import theking530.staticcore.cablenetwork.ServerCable;
import theking530.staticcore.cablenetwork.data.CableSideConnectionState;
import theking530.staticcore.cablenetwork.data.CableSideConnectionState.CableConnectionType;
import theking530.staticcore.cablenetwork.destinations.CableDestination;
import theking530.staticcore.cablenetwork.modules.CableNetworkModuleType;
import theking530.staticcore.utilities.MinecraftColor;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.redstone.RedstoneCableConfiguration;
import theking530.staticpower.cables.redstone.network.PacketUpdateRedstoneCableConfiguration;
import theking530.staticpower.client.StaticPowerAdditionalModels;
import theking530.staticpower.init.cables.ModCableDestinations;
import theking530.staticpower.init.cables.ModCableModules;
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
	private CableNetworkModuleType moduleType;
	/**
	 * This value ONLY represents the client side power level for this cable.
	 */
	private int clientSidePowerLevel;

	public RedstoneCableComponent(String name, @Nullable MinecraftColor color) {
		super(name, getModuleType(color));
		this.moduleType = this.getSupportedNetworkModuleTypes().stream().findFirst().get();

		// Initialize the side configuration.
		configuration = new RedstoneCableConfiguration();
		for (Direction dir : Direction.values()) {
			configuration.getSideConfig(dir).setSelector(color.getName());
		}
	}

	protected CableConnectionType getUncachedConnectionState(Direction side, @Nullable BlockEntity te, BlockPos blockPosition, boolean firstWorldLoaded) {
		AbstractCableProviderComponent otherProvider = CableUtilities.getCableWrapperComponent(getLevel(), blockPosition);
		if (otherProvider != null) {
			if (otherProvider.areCableCompatible(this, side)) {
				if (!otherProvider.isSideDisabled(side.getOpposite())) {
					return CableConnectionType.CABLE;
				}
			} else {
				return getSupportedNetworkModuleTypes().contains(ModCableModules.NakedRedstone.get())
						|| otherProvider.getSupportedNetworkModuleTypes().contains(ModCableModules.BundledRedstone.get())
						|| otherProvider.getSupportedNetworkModuleTypes().contains(ModCableModules.NakedRedstone.get()) ? CableConnectionType.TILE_ENTITY
								: CableConnectionType.NONE;
			}
		} else if (!firstWorldLoaded && otherProvider == null) {
			if (canConnectTo(getLevel(), getPos(), side.getOpposite())) {
				return CableConnectionType.TILE_ENTITY;
			}
		}
		return CableConnectionType.NONE;
	}

	@Override
	protected void getSupportedDestinationTypes(Set<CableDestination> types) {
		types.add(ModCableDestinations.Redstone.get());
	}

	@Override
	protected void initializeCableProperties(ServerCable cable, BlockPlaceContext context, BlockState state, LivingEntity placer, ItemStack stack) {
		super.initializeCableProperties(cable, context, state, placer, stack);
		cable.getDataTag().put(CONFIGURATION_KEY, configuration.serializeNBT());
	}

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
		return getNetworkModule(moduleType);
	}

	public RedstoneCableConfiguration getConfiguration() {
		return configuration;
	}

	public void updateConfiguration(RedstoneCableConfiguration configuration) {
		this.configuration = configuration;
		if (isClientSide()) {
			// Send a packet to the server with the updated values.
			PacketUpdateRedstoneCableConfiguration msg = new PacketUpdateRedstoneCableConfiguration(getPos(), configuration);
			StaticPowerMessageHandler.MAIN_PACKET_CHANNEL.sendToServer(msg);
			getTileEntity().addRenderingUpdateRequest();
		} else {
			if (CableNetworkManager.get(getLevel()).isTrackingCable(getPos())) {
				CableNetworkManager.get(getLevel()).getCable(getPos()).getDataTag().put(CONFIGURATION_KEY, configuration.serializeNBT());
				getLevel().updateNeighborsAt(this.getPos(), getLevel().getBlockState(getPos()).getBlock());
			}
		}
	}

	@Override
	public CompoundTag serializeUpdateNbt(CompoundTag nbt, boolean fromUpdate) {
		super.serializeUpdateNbt(nbt, fromUpdate);

		// Serialize the side configs.
		nbt.put("config", configuration.serializeNBT());

		// Use this as a way to sync the client side value of the power level.
		if (getLevel() != null && !getLevel().isClientSide) {
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
		if (getLevel() != null && getLevel().isClientSide) {
			clientSidePowerLevel = (int) nbt.getByte("power");
		}
	}

	@Override
	protected ResourceLocation getAttachmentModel(Direction side, CableSideConnectionState connectionState) {
		if (connectionState.getConnectionType() == CableConnectionType.TILE_ENTITY) {
			if (configuration.getSideConfig(side).isInputSide()) {
				return StaticPowerAdditionalModels.CABLE_REDSTONE_BASIC_ATTACHMENT_INPUT;
			} else if (configuration.getSideConfig(side).isOutputSide()) {
				return StaticPowerAdditionalModels.CABLE_REDSTONE_BASIC_ATTACHMENT_OUTPUT;
			} else {
				return StaticPowerAdditionalModels.CABLE_REDSTONE_BASIC_ATTACHMENT_INPUT_OUTPUT;
			}
		} else {
			return super.getAttachmentModel(side, connectionState);
		}
	}

	protected static CableNetworkModuleType getModuleType(@Nullable MinecraftColor color) {
		if (color == null) {
			return ModCableModules.NakedRedstone.get();
		}
		return ModCableModules.RedstoneModules.get(color).get();
	}
}
