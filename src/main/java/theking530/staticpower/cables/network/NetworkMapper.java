package theking530.staticpower.cables.network;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.block.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import theking530.staticpower.cables.AbstractCableWrapper;

public class NetworkMapper {
	private final Set<AbstractCableWrapper> DiscoveredCables;
	private final Set<AbstractCableWrapper> NewlyAddedCables;
	private final Set<AbstractCableWrapper> RemovedCables;
	private final Set<AbstractCableWrapper> InitialCables;
	private final Set<TileEntity> Destinations;

	public NetworkMapper(Set<AbstractCableWrapper> startingCables) {
		InitialCables = startingCables;
		DiscoveredCables = new HashSet<AbstractCableWrapper>();
		NewlyAddedCables = new HashSet<AbstractCableWrapper>();
		RemovedCables = new HashSet<AbstractCableWrapper>();
		Destinations = new HashSet<TileEntity>();

		RemovedCables.addAll(startingCables);
	}

	public void scanFromLocation(World world, BlockPos startingPos) {
		// Create the visited hash set and add the starting position as already visited.
		HashSet<BlockPos> visited = new HashSet<BlockPos>();
		visited.add(startingPos);

		// Check the starting position.
		scanLocation(world, null, startingPos);

		// Kick off the recursion.
		_updateNetworkWorker(world, visited, startingPos);
	}

	public Set<AbstractCableWrapper> getDiscoveredCables() {
		return DiscoveredCables;
	}

	public Set<AbstractCableWrapper> getNewlyAddedCables() {
		return NewlyAddedCables;
	}

	public Set<AbstractCableWrapper> getRemovedPipes() {
		return RemovedCables;
	}

	public Set<TileEntity> getDestinations() {
		return Destinations;
	}

	protected void _updateNetworkWorker(World world, HashSet<BlockPos> visited, BlockPos currentPosition) {
		for (Direction facing : Direction.values()) {
			// If we're testing on a position that contains a cable and the cable is
			// disabled on the side we're testing, skip it. Do NOT mark that side as visited
			// though, as another cable may get to it that is enabled on that side.
			AbstractCableWrapper cable = CableNetworkManager.get(world).getCable(currentPosition);
			if (cable != null && cable.isDisabledOnSide(facing)) {
				continue;
			}

			// Get the next position to test. If we've visited it before, skip it.
			BlockPos testPos = currentPosition.offset(facing);
			if (visited.contains(testPos)) {
				continue;
			}

			// Add the block to the visited list.
			visited.add(testPos);

			// Attempt to cache this location if needed. If true, we found a cable and we
			// continue mapping, otherwise, we stop here.
			if (scanLocation(world, facing, testPos)) {
				_updateNetworkWorker(world, visited, testPos);
			}
		}
	}

	/**
	 * Checks if the provided location should be cached into this mapper.
	 * 
	 * @param world
	 * @param facing   The direction we are going out TO the block. This needs to be
	 *                 reversed when checking properties from that block. If this is
	 *                 the first scan, this parameter will be null.
	 * @param location
	 * @return True if we should continue recursing, false otherwise.
	 */
	protected boolean scanLocation(World world, @Nullable Direction facing, BlockPos location) {
		// Skip air blocks. This also ensures we skip any blocks that may have been
		// removed but whose tile entity may linger for a moment. (A check for isRemoved
		// on the tile entity does not catch this edge case...).
		if (world.getBlockState(location).getBlock() == Blocks.AIR) {
			return false;
		}

		// Check the starting position.
		AbstractCableWrapper cable = CableNetworkManager.get(world).getCable(location);
		if (cable != null) {
			// If the cable is disabled on the side opposite from the one we approached
			// from, do not discover it and do not continue searching in that direction.
			if (facing != null && cable.isDisabledOnSide(facing.getOpposite())) {
				return false;
			}

			// Add the cable as a discovered cable.
			DiscoveredCables.add(cable);

			// If this cable wasn't in the initial set, add it to the new cables.
			if (!InitialCables.contains(cable)) {
				NewlyAddedCables.add(cable);
			}

			// Remove the cable if it was in the removed cable set.
			RemovedCables.remove(cable);
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
