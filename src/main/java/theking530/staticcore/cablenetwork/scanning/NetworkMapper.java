package theking530.staticcore.cablenetwork.scanning;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import theking530.staticcore.cablenetwork.CableNetworkManager;
import theking530.staticcore.cablenetwork.ServerCable;
import theking530.staticcore.cablenetwork.data.DestinationWrapper;

public class NetworkMapper {
	private final Collection<ServerCable> initialCables;
	private final Set<ServerCable> discoveredCables;
	private final Set<ServerCable> newlyAddedCables;
	private final Set<ServerCable> removedCables;
	private HashMap<BlockPos, DestinationWrapper> destinations;
	private BlockPos startingPosition;

	public NetworkMapper(Collection<ServerCable> startingCables) {
		this.initialCables = startingCables;
		this.discoveredCables = new LinkedHashSet<ServerCable>();
		this.newlyAddedCables = new LinkedHashSet<ServerCable>();
		this.removedCables = new LinkedHashSet<ServerCable>();
		this.destinations = new HashMap<BlockPos, DestinationWrapper>();
		this.removedCables.addAll(startingCables);
	}

	public void scanFromLocation(Level world, BlockPos startingPos) {
		// Create the visited hash set and add the starting position as already visited.
		HashSet<BlockPos> visited = new HashSet<BlockPos>();
		visited.add(startingPos);

		// Capture the starting position.
		startingPosition = startingPos;

		// Check the starting position. We make the assumption that the starting cable
		// is a valid one.
		ServerCable startingCable = CableNetworkManager.get(world).getCable(startingPos);
		discoverCable(startingCable);

		// Kick off the recursion.
		recurseAroundCable(world, visited, startingPos);
	}

	public Set<ServerCable> getDiscoveredCables() {
		return discoveredCables;
	}

	public Set<ServerCable> getNewlyAddedCables() {
		return newlyAddedCables;
	}

	public Set<ServerCable> getRemovedCables() {
		return removedCables;
	}

	public HashMap<BlockPos, DestinationWrapper> getDestinations() {
		return destinations;
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
			if (visited.contains(linkPosition)) {
				continue;
			}

			// Attempt to cache this location if needed. If true, we found a cable and we
			// continue mapping, otherwise, we stop here and just check for destinations.
			if (scanForCompatibleCable(world, cable, scanLoc)) {
				visited.add(linkPosition);
				recurseAroundCable(world, visited, linkPosition);
			} else {
				scanForCompatibleDestination(world, cable, scanLoc);
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
	protected boolean scanForCompatibleCable(Level world, ServerCable scanningCable, CableScanLocation scanLocation) {
		// Skip air blocks. This also ensures we skip any blocks that may have been
		// removed but whose tile entity may linger for a moment. (A check for isRemoved
		// on the tile entity does not catch this edge case...).
		if (world.getBlockState(scanLocation.getLocation()).getBlock() == Blocks.AIR) {
			return false;
		}

		// Check the starting position.
		ServerCable otherCable = CableNetworkManager.get(world).getCable(scanLocation.getLocation());
		if (otherCable != null && scanningCable.shouldConnectToCable(otherCable)) {
			// If the cable is disabled on the side opposite from the one we approached
			// from, do not discover it and do not continue searching in that direction.
			// We do not do this check for sparse cables.
			if (!scanLocation.isSparseLink() && scanLocation.getSide() != null && otherCable.isDisabledOnSide(scanLocation.getSide().getOpposite())) {
				return false;
			}

			discoverCable(otherCable);
			return true;
		}
		return false;
	}

	protected void scanForCompatibleDestination(Level world, ServerCable scanningCable, CableScanLocation scanLocation) {
		BlockPos location = scanLocation.getLocation();
		Direction sideOfCable = scanLocation.getSide();

		if (!destinations.containsKey(location)) {
			// Cache a destination wrapper for it but only add it if we found a compatible
			// destination.
			// We only want the final destinations map to have destination with at least one
			// side having a compatible destination.
			DestinationWrapper wrapper = new DestinationWrapper(world, location, world.getBlockEntity(location), scanningCable.getPos(), sideOfCable);
			if (wrapper.hasSupportedDestinationTypes(sideOfCable.getOpposite(), scanningCable)) {
				destinations.put(location, wrapper);
			}
		} else {
			destinations.get(location).addConnectedCable(scanningCable.getPos(), sideOfCable);
		}
	}

	protected void discoverCable(ServerCable cable) {
		// Add the cable as a discovered cable.
		discoveredCables.add(cable);

		// If this cable wasn't in the initial set, add it to the new cables.
		if (!initialCables.contains(cable)) {
			newlyAddedCables.add(cable);
		}

		// Remove the cable if it was in the removed cable set.
		removedCables.remove(cable);
	}
}
