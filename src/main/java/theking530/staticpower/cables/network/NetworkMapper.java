package theking530.staticpower.cables.network;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import theking530.staticpower.cables.SparseCableLink;

public class NetworkMapper {
	private final Collection<ServerCable> InitialCables;
	private final Set<ServerCable> DiscoveredCables;
	private final Set<ServerCable> NewlyAddedCables;
	private final Set<ServerCable> RemovedCables;
	private HashMap<BlockPos, DestinationWrapper> Destinations;
	private BlockPos startingPosition;

	public NetworkMapper(Collection<ServerCable> startingCables) {
		InitialCables = startingCables;
		DiscoveredCables = new LinkedHashSet<ServerCable>();
		NewlyAddedCables = new LinkedHashSet<ServerCable>();
		RemovedCables = new LinkedHashSet<ServerCable>();
		Destinations = new HashMap<BlockPos, DestinationWrapper>();

		RemovedCables.addAll(startingCables);
	}

	public void scanFromLocation(Level world, BlockPos startingPos) {
		// Create the visited hash set and add the starting position as already visited.
		HashSet<BlockPos> visited = new HashSet<BlockPos>();
		visited.add(startingPos);

		// Capture the starting position.
		startingPosition = startingPos;

		// Check the starting position.
		ServerCable startingCable = CableNetworkManager.get(world).getCable(startingPos);
		scanLocation(world, startingCable, null, startingPos);
//		if (startingCable.isSparse()) {
//			for (Direction dir : Direction.values()) {
//				if (!startingCable.isLinkedTo(startingPos.relative(dir))) {
//					scanLocationForDestinations(world, startingCable, dir, startingPos.relative(dir));
//				}
//			}
//		}

		// Kick off the recursion.
		recurseAroundCable(world, visited, startingPos);
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

	public HashMap<BlockPos, DestinationWrapper> getDestinations() {
		return Destinations;
	}

	public BlockPos getStartingPosition() {
		return startingPosition;
	}

	protected void recurseAroundCable(Level world, HashSet<BlockPos> visited, BlockPos currentPosition) {
		if (!CableNetworkManager.get(world).isTrackingCable(currentPosition)) {
			return;
		}

		ServerCable cable = CableNetworkManager.get(world).getCable(currentPosition);
		for (CableScanLocation scanLoc : cable.getScanLocations()) {
			BlockPos linkPosition = scanLoc.getLocation();
			if (cable != null && !scanLoc.isSparseLink() && cable.isDisabledOnSide(scanLoc.getSide())) {
				continue;
			}

			if (visited.contains(linkPosition)) {
				continue;
			}

			// Attempt to cache this location if needed. If true, we found a cable and we
			// continue mapping, otherwise, we stop here and just check for destinations.
			if (scanLocation(world, cable, scanLoc.getSide(), linkPosition)) {
				visited.add(linkPosition);
				recurseAroundCable(world, visited, linkPosition);
			} else {
				scanLocationForDestinations(world, cable, scanLoc.getSide(), linkPosition);
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
	protected boolean scanLocation(Level world, ServerCable scanningCable, @Nullable Direction facing, BlockPos location) {
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
		}
		return false;
	}

	protected void scanLocationForDestinations(Level world, ServerCable scanningCable, Direction facing, BlockPos location) {
		// Make sure it is valid.
		if (!Destinations.containsKey(location)) {
			// Cache a destination wrapper for it.
			DestinationWrapper wrapper = new DestinationWrapper(world, location, world.getBlockEntity(location), scanningCable.getPos(), facing.getOpposite());
			if (!wrapper.hasSupportedDestinationTypes()) {
				Destinations.put(location, wrapper);
			}
		} else {
			Destinations.get(location).addConnectedCable(scanningCable.getPos(), facing.getOpposite());
		}
	}
}
