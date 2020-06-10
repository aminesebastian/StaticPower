package theking530.staticpower.tileentities.network;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import theking530.staticpower.tileentities.cables.AbstractCableWrapper;

public class NetworkMapper {
	private final Set<AbstractCableWrapper> DiscoveredPipes;
	private final Set<AbstractCableWrapper> NewlyAddedPipes;
	private final Set<AbstractCableWrapper> RemovedPipes;
	private final Set<AbstractCableWrapper> InitialPipes;
	private final Set<TileEntity> Destinations;

	public NetworkMapper(Set<AbstractCableWrapper> startingPipes) {
		InitialPipes = startingPipes;
		DiscoveredPipes = new HashSet<AbstractCableWrapper>();
		NewlyAddedPipes = new HashSet<AbstractCableWrapper>();
		RemovedPipes = new HashSet<AbstractCableWrapper>();
		Destinations = new HashSet<TileEntity>();

		RemovedPipes.addAll(startingPipes);
	}

	public void scanFromLocation(World world, BlockPos startingPos) {
		// Create the visited hash set and add the starting position as already visited.
		HashSet<BlockPos> visited = new HashSet<BlockPos>();
		visited.add(startingPos);

		// Check the starting position.
		scanLocation(world, startingPos);

		// Kick off the recursion.
		_updateNetworkWorker(world, visited, startingPos);
	}

	public Set<AbstractCableWrapper> getDiscoveredPipes() {
		return DiscoveredPipes;
	}

	public Set<AbstractCableWrapper> getNewlyAddedPipes() {
		return NewlyAddedPipes;
	}

	public Set<AbstractCableWrapper> getRemovedPipes() {
		return RemovedPipes;
	}

	public Set<TileEntity> getDestinations() {
		return Destinations;
	}

	protected void _updateNetworkWorker(World world, HashSet<BlockPos> visited, BlockPos currentPosition) {
		for (Direction facing : Direction.values()) {
			// Get the next position to test. If we've visited it before, skip it.
			BlockPos testPos = currentPosition.offset(facing);
			if (visited.contains(testPos)) {
				continue;
			}

			// Add the block to the visited list.
			visited.add(testPos);

			// Attempt to cache this location if needed. If true, we found a cable and we
			// continue mapping, otherwise, we stop here.
			if (scanLocation(world, testPos)) {
				_updateNetworkWorker(world, visited, testPos);
			}
		}
	}

	/**
	 * Checks if the provided location should be cached into this mapper.
	 * 
	 * @param world
	 * @param location
	 * @return True if we should continue recursing, false otherwise.
	 */
	protected boolean scanLocation(World world, BlockPos location) {
		// Skip air blocks. This also ensures we skip any blocks that may have been
		// removed but whose tile entity may linger for a moment. (A check for isRemoved
		// on the tile entity does not catch this edge case...).
		if (world.getBlockState(location).getBlock() == Blocks.AIR) {
			return false;
		}

		// Check the starting position.
		AbstractCableWrapper cable = CableNetworkManager.get(world).getCable(location);
		if (cable != null) {
			DiscoveredPipes.add(cable);
			if (!InitialPipes.contains(cable)) {
				NewlyAddedPipes.add(cable);
			}

			RemovedPipes.remove(cable);
			return true;
		} else {
			// Get the tileentiy at the block position and check if it is in our set of
			// valid tile entities. If it is, add it to the list.
			TileEntity te = world.getTileEntity(location);
			if (te != null && !te.isRemoved()) {
				Destinations.add(te);
			}
		}
		return false;
	}
}
