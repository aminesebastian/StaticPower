package theking530.staticpower.tileentities.network;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiPredicate;

import net.minecraft.block.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileEntityNetwork<T extends TileEntity> {
	private BiPredicate<TileEntity, Direction> ValidTileEntityPredicate;
	private BiPredicate<BlockPos, Direction> ValidExtenderPredicate;
	private HashMap<BlockPos, T> NetworkMap;

	private World world;
	private boolean dirty;

	/**
	 * Creates a tile entity network.
	 * 
	 * @param world                    The world the network operates in.
	 * @param validTileEntityPredicate This predicate returns whether or not a tile
	 *                                 entity should be added to the network. Note:
	 *                                 the second parameter is the Direction that
	 *                                 the provided tile entity was approached FROM.
	 *                                 Meaning it is the value that should be
	 *                                 provided when attempting to get a capability.
	 * 
	 * @param validExtenderPredicate   This method is called to determine if a
	 *                                 position should be added to the search list.
	 *                                 For example, in the digistore network, this
	 *                                 returns true if the block is a DigistoreWire
	 *                                 OR if it is a digistore tile entity. Another
	 *                                 example would be for PowerCables, any
	 *                                 powercable is considered an extender. These
	 *                                 locations are not cached, so any positions
	 *                                 that are not required to be queryable later
	 *                                 should be included here instead.
	 */
	public TileEntityNetwork(World world, BiPredicate<TileEntity, Direction> validTileEntityPredicate, BiPredicate<BlockPos, Direction> validExtenderPredicate) {
		this.world = world;
		ValidTileEntityPredicate = validTileEntityPredicate;
		ValidExtenderPredicate = validExtenderPredicate;
		NetworkMap = new HashMap<BlockPos, T>();
	}

	/**
	 * Gets a map of all the block positions to tile entities in the network.
	 * 
	 * @return
	 */
	public HashMap<BlockPos, T> getAllNetworkTiles() {
		return NetworkMap;
	}

	/**
	 * Gets all the {@link TileEntity}s in the network of the provided class.
	 * 
	 * @param <K>      The tileentity class to look for.
	 * @param tileType The tileentity class to look for.
	 * @return A list of all the tileentities that inherit from the provided type.
	 */
	@SuppressWarnings("unchecked")
	public <K extends T> List<K> getAllNetworkTiles(Class<K> tileType) {
		List<K> output = new LinkedList<K>();
		for (T tileEntity : NetworkMap.values()) {
			if (tileType.isInstance(tileEntity)) {
				output.add((K) tileEntity);
			}
		}
		return output;
	}

	public boolean containsPosition(BlockPos pos) {
		return NetworkMap.containsKey(pos);
	}

	/**
	 * Updates the network.
	 * 
	 * @param startingTileEntity The tile entity to start from.
	 * @param forceUpdate        If true, the update will occur regardless of
	 *                           whether or not the network is marked dirty. If
	 *                           false, this method will do nothing.
	 */
	public void updateNetwork(T startingTileEntity, boolean forceUpdate) {
		if (!dirty && !forceUpdate) {
			return;
		}
		// Clear the network map and add the starting tile entity.
		NetworkMap.clear();
		NetworkMap.put(startingTileEntity.getPos(), startingTileEntity);

		// Create the visited hash set and add the starting position as already visited.
		HashSet<BlockPos> visited = new HashSet<BlockPos>();
		visited.add(startingTileEntity.getPos());

		// Kick off the recursion.
		_updateNetworkWorker(new HashSet<BlockPos>(), startingTileEntity.getPos());
	}

	/**
	 * Indicates that a block has changed and that the network is in a dirty state.
	 */
	public void markDirty() {
		dirty = true;
	}

	/**
	 * Recursive graph update worker.
	 * 
	 * @param visited
	 * @param currentPosition
	 */
	@SuppressWarnings("unchecked")
	protected void _updateNetworkWorker(HashSet<BlockPos> visited, BlockPos currentPosition) {
		for (Direction facing : Direction.values()) {
			// Get the next position to test. If we've visited it before, skip it.
			BlockPos testPos = currentPosition.offset(facing);
			if (visited.contains(testPos)) {
				continue;
			}

			// Add the block to the visited list.
			visited.add(testPos);

			// Skip air blocks. This also ensures we skip any blocks that may have been
			// removed but whose tile entity may linger for a moment. (A check for isRemoved
			// on the tile entity does not catch this edge case...).
			if (world.getBlockState(testPos).getBlock() == Blocks.AIR) {
				continue;
			}

			// Get the tileentiy at the block position and check if it is in our set of
			// valid tile entities. If it is, add it to the list.
			TileEntity te = world.getTileEntity(testPos);
			if (te != null && !te.isRemoved() && ValidTileEntityPredicate.test(te, facing.getOpposite())) {
				NetworkMap.put(testPos, (T) te);
			}

			// Now check if the provided position is valid to extend our network. If it is,
			// recurse, if not, this was a terminus.
			if (ValidExtenderPredicate.test(testPos, facing.getOpposite())) {
				_updateNetworkWorker(visited, testPos);
			}
		}
	}
}
