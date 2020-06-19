package theking530.staticpower.tileentities.cables;

import javax.annotation.Nullable;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import theking530.staticpower.tileentities.TileEntityBase;
import theking530.staticpower.tileentities.cables.ServerCable.CableConnectionState;

public class CableUtilities {

	public static CableConnectionState getConnectionState(IBlockReader world, BlockPos pos, Direction side) {
		AbstractCableProviderComponent cableComponent = getCableWrapperComponent(world, pos);
		if (cableComponent != null) {
			return cableComponent.getConnectionState(side);
		}
		return CableConnectionState.NONE;
	}

	public static boolean isSideConnectionDisabled(IBlockReader world, BlockPos pos, Direction side) {
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
	public static @Nullable AbstractCableProviderComponent getCableWrapperComponent(IBlockReader world, BlockPos pos) {
		if (world.getTileEntity(pos) instanceof TileEntityBase) {
			TileEntityBase tileEntityBase = (TileEntityBase) world.getTileEntity(pos);
			if (tileEntityBase.hasComponentOfType(AbstractCableProviderComponent.class)) {
				return tileEntityBase.getComponent(AbstractCableProviderComponent.class);
			}
		}
		return null;
	}

	public static boolean isCableStraightConnection(CableConnectionState[] connections) {
		int cableConnections = 0;

		// Get the number of sides that are connected to a cable. If a side is connected
		// to a tile entity, instantly return false;.
		for (Direction dir : Direction.values()) {
			if (connections[dir.ordinal()] == CableConnectionState.CABLE) {
				cableConnections++;
			} else if (connections[dir.ordinal()] == CableConnectionState.TILE_ENTITY) {
				return false;
			}
		}

		// If only two sides are connected, check to see which sides. If they are
		// opposites, we can use a straight cable model.
		if (cableConnections == 2) {
			for (int i = 0; i < 6; i += 2) {
				Direction dir = Direction.values()[i];
				if (connections[i] == CableConnectionState.CABLE && connections[dir.getOpposite().ordinal()] == CableConnectionState.CABLE) {
					return true;
				}
			}
		}

		return false;
	}

	public static @Nullable Direction getStraightConnectionSide(CableConnectionState[] connections) {
		if (isCableStraightConnection(connections)) {
			for (int i = 0; i < 6; i += 2) {
				Direction dir = Direction.values()[i];
				if (connections[dir.ordinal()] == CableConnectionState.CABLE) {
					return dir;
				}
			}
		}
		return null;
	}

	public static boolean isCableStraightConnection(IBlockReader world, BlockPos pos) {
		AbstractCableProviderComponent connectionComponent = getCableWrapperComponent(world, pos);
		if (connectionComponent != null) {
			return isCableStraightConnection(connectionComponent.getConnectionStates());
		}
		return false;
	}
}
