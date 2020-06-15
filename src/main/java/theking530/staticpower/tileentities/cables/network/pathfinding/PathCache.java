package theking530.staticpower.tileentities.cables.network.pathfinding;

import java.util.HashMap;

import javax.annotation.Nullable;

import net.minecraft.util.math.BlockPos;
import theking530.staticpower.tileentities.cables.network.CableNetwork;

public class PathCache {
	/** Map of destinations to map of source blocks and paths. */
	private HashMap<BlockPos, HashMap<BlockPos, Path>> Cache;
	private CableNetwork OwningNetwork;

	public PathCache(CableNetwork owningNetwork) {
		Cache = new HashMap<BlockPos, HashMap<BlockPos, Path>>();
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
		return getPath(source, destination) != null;
	}

	/**
	 * Gets the path between the provided source and destination if one exists.
	 * 
	 * @param source
	 * @param destination
	 * @return
	 */
	public @Nullable Path getPath(BlockPos source, BlockPos destination) {
		if (Cache.containsKey(destination) && Cache.get(destination).containsKey(source)) {
			return Cache.get(destination).get(source);
		}
		return null;
	}

	/**
	 * Attempts to calculate a path between the provided source and destination, and
	 * returns true if successful.
	 * 
	 * @param source
	 * @param destination
	 * @return
	 */
	public boolean calculatePath(BlockPos source, BlockPos destination) {
		// C'mon, should've checked first.
		if (hasPath(source, destination)) {
			return true;
		}

		// Perform the path finding.
		NetworkPathFinder pathFinder = new NetworkPathFinder(OwningNetwork.getGraph(), source);
		HashMap<BlockPos, Path> paths = pathFinder.executeAlgorithm();

		// Cache each provided path.
		paths.forEach((dest, path) -> {
			if (!Cache.containsKey(dest)) {
				Cache.put(dest, new HashMap<BlockPos, Path>());
			}
			Cache.get(dest).put(source, path);
		});

		// Check if we now have the path.
		return hasPath(source, destination);
	}

	/**
	 * Clears all cached paths.
	 */
	public void invalidateCache() {
		Cache.clear();
	}
}
