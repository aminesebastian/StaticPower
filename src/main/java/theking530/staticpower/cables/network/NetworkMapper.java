package theking530.staticpower.cables.network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.block.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class NetworkMapper {
	private final Collection<ServerCable> InitialCables;
	private final Set<ServerCable> DiscoveredCables;
	private final Set<ServerCable> NewlyAddedCables;
	private final Set<ServerCable> RemovedCables;
	private final List<DestinationWrapper> Destinations;

	public NetworkMapper(Collection<ServerCable> startingCables) {
		InitialCables = startingCables;
		DiscoveredCables = new HashSet<ServerCable>();
		NewlyAddedCables = new HashSet<ServerCable>();
		RemovedCables = new HashSet<ServerCable>();
		Destinations = new ArrayList<DestinationWrapper>();

		RemovedCables.addAll(startingCables);
	}

	public void scanFromLocation(World world, BlockPos startingPos) {
		// Create the visited hash set and add the starting position as already visited.
		HashSet<BlockPos> visited = new HashSet<BlockPos>();
		visited.add(startingPos);

		// Check the starting position.
		scanLocation(world, CableNetworkManager.get(world).getCable(startingPos), null, startingPos);

		// Kick off the recursion.
		_updateNetworkWorker(world, visited, startingPos);
	}

	public Set<ServerCable> getDiscoveredCables() {
		return DiscoveredCables;
	}

	public Set<ServerCable> getNewlyAddedCables() {
		return NewlyAddedCables;
	}

	public Set<ServerCable> getRemovedCables() {
		return RemovedCables;
	}

	public List<DestinationWrapper> getDestinations() {
		return Destinations;
	}

	protected void _updateNetworkWorker(World world, HashSet<BlockPos> visited, BlockPos currentPosition) {
		// If we're testing on a position that contains a cable and the cable is
		// disabled on the side we're testing, skip it. Do NOT mark that side as visited
		// though, as another cable may get to it that is enabled on that side.
		ServerCable cable = CableNetworkManager.get(world).getCable(currentPosition);
		
		for (Direction facing : Direction.values()) {			
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
			if (scanLocation(world, cable, facing, testPos)) {
				_updateNetworkWorker(world, visited, testPos);
			}
		}
	}

	/**
	 * Checks if the provided location should be cached into this mapper.
	 * 
	 * @param world
	 * @param scanningCable The cable that initiated this scan.
	 * @param facing        The direction we are going out TO the block. This needs
	 *                      to be reversed when checking properties from that block.
	 *                      If this is the first scan, this parameter will be null.
	 * @param location
	 * @return True if we should continue recursing, false otherwise.
	 */
	protected boolean scanLocation(World world, ServerCable scanningCable, @Nullable Direction facing, BlockPos location) {
		// Skip air blocks. This also ensures we skip any blocks that may have been
		// removed but whose tile entity may linger for a moment. (A check for isRemoved
		// on the tile entity does not catch this edge case...).
		if (world.getBlockState(location).getBlock() == Blocks.AIR) {
			return false;
		}

		// Check the starting position.
		ServerCable cable = CableNetworkManager.get(world).getCable(location);
		if (cable != null && scanningCable.shouldConnectTo(cable)) {
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
			// Get the tileentiy at the block position.
			TileEntity te = world.getTileEntity(location);

			// Make sure it is valid.
			if (te != null && !te.isRemoved()) {

				// Cache a destination wrapper for it.
				DestinationWrapper wrapper = new DestinationWrapper(te, scanningCable.getPos(), facing.getOpposite());
				Destinations.add(wrapper);
			}
			return false;
		}
	}
}
