package theking530.staticcore.cablenetwork;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.cablenetwork.data.CableConnectionState.CableConnectionType;
import theking530.staticpower.blockentities.BlockEntityBase;
import theking530.staticpower.cables.AbstractCableBlock;
import theking530.staticpower.cables.AbstractCableProviderComponent;

public class CableUtilities {

	public static CableConnectionType getConnectionState(BlockGetter world, BlockPos pos, Direction side) {
		BlockState state = world.getBlockState(pos);
		if (!doesBlockStateHaveConnectionProperty(state)) {
			return CableConnectionType.NONE;
		}

		return world.getBlockState(pos).getValue(AbstractCableBlock.CONNECTION_TYPES.get(side));
	}

	public static boolean doesBlockStateHaveConnectionProperty(BlockState state) {
		return state.hasProperty(AbstractCableBlock.CONNECTION_TYPES.get(Direction.DOWN));
	}

	public static boolean isSideConnectionDisabled(BlockGetter world, BlockPos pos, Direction side) {
		AbstractCableProviderComponent cableComponent = getCableWrapperComponent(world, pos);
		if (cableComponent != null) {
			return cableComponent.isSideDisabled(side);
		}
		return true;
	}

	/**
	 * Get the cable wrapper at the provided location if one exists, otherwise
	 * returns null.
	 * 
	 * @param world The world to check for the cable wrapper component.
	 * @param pos   The location to check.
	 * @return The cable wrapper component if one is found, null otherwise.
	 */
	@SuppressWarnings("unchecked")
	public static @Nullable <T extends AbstractCableProviderComponent> T getCableWrapperComponent(BlockGetter world, BlockPos pos) {
		if (world.getBlockEntity(pos) instanceof BlockEntityBase) {
			BlockEntityBase tileEntityBase = (BlockEntityBase) world.getBlockEntity(pos);
			if (tileEntityBase.hasComponentOfType(AbstractCableProviderComponent.class)) {
				return (T) tileEntityBase.getComponent(AbstractCableProviderComponent.class);
			}
		}
		return null;
	}

	public static boolean isCableStraightConnection(BlockState blockState, CableRenderingState renderingState) {
		int cableConnections = 0;

		// Get the number of sides that are connected to a cable. If a side is connected
		// to a tile entity, instantly return false;.
		for (Direction dir : Direction.values()) {
			if (!renderingState.isDisabledOnSide(dir) && getConnectionTypeOnSide(blockState, dir) == CableConnectionType.CABLE) {
				cableConnections++;
			} else if (getConnectionTypeOnSide(blockState, dir) == CableConnectionType.DESTINATION) {
				return false;
			}

			if (renderingState.hasAttachment(dir)) {
				return false;
			}
		}

		// If only two sides are connected, check to see which sides. If they are
		// opposites, we can use a straight cable model.
		if (cableConnections == 2) {
			for (int i = 0; i < 6; i += 2) {
				Direction dir = Direction.values()[i];
				if (getConnectionTypeOnSide(blockState, dir) == CableConnectionType.CABLE && getConnectionTypeOnSide(blockState, dir.getOpposite()) == CableConnectionType.CABLE) {
					return true;
				}
			}
		}

		return false;
	}

	public static @Nullable Direction getStraightConnectionSide(BlockState blockState, CableRenderingState renderingState) {
		if (isCableStraightConnection(blockState, renderingState)) {
			for (int i = 0; i < 6; i += 2) {
				Direction dir = Direction.values()[i];
				if (!renderingState.isDisabledOnSide(dir) && getConnectionTypeOnSide(blockState, dir) == CableConnectionType.CABLE) {
					return dir;
				}
			}
		}
		return null;
	}

	public static boolean isCableStraightConnection(BlockGetter world, BlockPos pos) {
		AbstractCableProviderComponent connectionComponent = getCableWrapperComponent(world, pos);
		if (connectionComponent != null) {
			CableConnectionType[] connectionTypes = new CableConnectionType[6];
			for (Direction dir : Direction.values()) {
				connectionTypes[dir.ordinal()] = connectionComponent.getConnectionTypeOnSide(dir);
			}

			return isCableStraightConnection(world.getBlockState(pos), connectionComponent.getRenderingState());
		}
		return false;
	}

	public static CableConnectionType getConnectionTypeOnSide(BlockState state, Direction side) {
		return state.getValue(AbstractCableBlock.CONNECTION_TYPES.get(side));
	}
}
