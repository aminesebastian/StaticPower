package theking530.staticpower.tileentities.cables;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import theking530.staticpower.tileentities.TileEntityBase;
import theking530.staticpower.tileentities.network.TileEntityNetwork;

public abstract class AbstractCableTileEntity extends TileEntityBase {
	/**
	 * The container to keep track of the network. This only exists on the server.
	 */
	protected TileEntityNetwork<TileEntity> network;

	public AbstractCableTileEntity(TileEntityType<?> teType) {
		super(teType);
	}

	@Override
	public void onInitializedInWorld(World world, BlockPos pos) {
		super.onInitializedInWorld(world, pos);
		if (!world.isRemote()) {
			makeNetwork();
		}
	}

	@Override
	public void onBlockBroken(BlockState state, BlockState newState, boolean isMoving) {
		super.onBlockBroken(state, newState, isMoving);
	}

	protected void makeNetwork() {
		if (!world.isRemote()) {
			network = new TileEntityNetwork<TileEntity>(getWorld(), this::isValidDestinationForNetwork, this::isValidCableForNetwork);
		}
	}

	public abstract boolean isValidDestinationForNetwork(TileEntity tileEntity, Direction dir);

	public abstract boolean isValidCableForNetwork(BlockPos position, Direction dir);
}
