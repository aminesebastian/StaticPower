package theking530.staticpower.tileentities.cables.network.pathfinding;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.util.math.BlockPos;
import theking530.staticpower.tileentities.cables.network.CableNetwork;

public class PathCache {
	/** Map of destinations to map of source blocks and paths. */
	private HashMap<BlockPos, HashMap<BlockPos, List<Path>>> Cache;
	private CableNetwork OwningNetwork;

	public PathCache(CableNetwork owningNetwork) {
		Cache = new HashMap<BlockPos, HashMap<BlockPos, List<Path>>>();
		OwningNetwork = owningNetwork;
	}

	/**
	 * Checks to see if we have a path between a particular source and destination.
	 * 
	 * @param source
	 * @param destination
	 * @return
	 */
	public boolean hasPath(BlockPos source, BlockPos destination) {
		return Cache.get(destination).get(source) != null;
	}

	/**
	 * Gets the paths between the provided source and destination if one exists.
	 * 
	 * @param source
	 * @param destination
	 * @return
	 */
	public @Nullable List<Path> getPaths(BlockPos source, BlockPos destination) {
		if (Cache.containsKey(destination) && Cache.get(destination).containsKey(source)) {
			return Cache.get(destination).get(source);
		} else {
			return cacheNewPath(source, destination);
		}
	}

	/**
	 * Attempts to calculate the paths between the provided source and destination,
	 * and returns the calculated paths if successful.
	 * 
	 * @param source
	 * @param destination
	 * @return
	 */
	public List<Path> cacheNewPath(BlockPos source, BlockPos destination) {
		// Perform the path finding.
		NetworkPathFinder pathFinder = new NetworkPathFinder(OwningNetwork.getGraph(), OwningNetwork.getWorld(), source, destination);
		List<Path> paths = pathFinder.executeAlgorithm();

		// Cache each provided path.
		paths.forEach((path) -> {
			if (!Cache.containsKey(path.getDestinationLocation())) {
				Cache.put(path.getDestinationLocation(), new HashMap<BlockPos, List<Path>>());
				Cache.get(path.getDestinationLocation()).put(source, new LinkedList<Path>());
			}
			Cache.get(path.getDestinationLocation()).get(source).add(path);
		});

		// If we have a destination for this, sort it by length.
		if (Cache.containsKey(destination)) {
			Cache.get(destination).get(source).sort(Comparator.comparingInt(Path::getLength));
		}

		// Check if we now have the path.
		return getPaths(source, destination);
	}

	/**
	 * Clears all cached paths.
	 */
	public void invalidateCache() {
		Cache.clear();
	}
}
