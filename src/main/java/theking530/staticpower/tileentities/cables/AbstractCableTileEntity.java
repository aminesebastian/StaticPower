package theking530.staticpower.tileentities.cables;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntityType;
import theking530.staticpower.tileentities.TileEntityBase;
import theking530.staticpower.tileentities.network.CableNetworkManager;

public abstract class AbstractCableTileEntity<T extends AbstractCableWrapper> extends TileEntityBase {
	private T Wrapper;

	public AbstractCableTileEntity(TileEntityType<?> teType) {
		super(teType);
	}

	/**
	 * Checks to make sure that this cable is being properly tracked, and if not,
	 * registers it for tracking.
	 */
	public void validateTrackedByNetwork() {
		// If we're on the server, check to see if the cable network manager for this
		// world is tracking a cable at this position. If it is not, add this cable for
		// tracking.
		if (!world.isRemote) {
			CableNetworkManager manager = CableNetworkManager.get(world);
			if (!manager.isTrackingCable(pos)) {
				manager.addCable(getWrapper());
			}
		}
	}

	@Override
	public void onBlockBroken(BlockState state, BlockState newState, boolean isMoving) {
		super.onBlockBroken(state, newState, isMoving);
	}

	public T getWrapper() {
		if (Wrapper == null) {
			Wrapper = createWrapper();
		}
		return Wrapper;
	}

	public abstract T createWrapper();
}
