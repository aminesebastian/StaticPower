package theking530.staticpower.tileentities.cables;

import javax.annotation.Nullable;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import theking530.staticpower.tileentities.TileEntityBase;
import theking530.staticpower.tileentities.cables.AbstractCableWrapper.CableConnectionState;
import theking530.staticpower.tileentities.components.AbstractCableProviderComponent;

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
}
