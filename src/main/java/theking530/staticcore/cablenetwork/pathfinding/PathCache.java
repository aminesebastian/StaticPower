package theking530.staticcore.cablenetwork.pathfinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.core.BlockPos;
import theking530.staticcore.cablenetwork.CableNetwork;
import theking530.staticcore.cablenetwork.modules.CableNetworkModuleType;
import theking530.staticpower.StaticPower;

public class PathCache {
	public static final Logger LOGGER = LogManager.getLogger(PathCache.class);
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
	public boolean hasPath(BlockPos source, BlockPos destination, CableNetworkModuleType moduleType) {
		return Cache.get(destination) != null && Cache.get(destination).get(source) != null
				&& Cache.get(destination).get(source).stream().anyMatch(path -> path.getSupportedNetworkType().equals(moduleType));
	}

	/**
	 * Gets the paths between the provided source and destination if one exists.
	 * 
	 * @param source
	 * @param destination
	 * @return
	 */
	public @Nullable List<Path> getPaths(BlockPos cablePosition, BlockPos destination, CableNetworkModuleType moduleType) {
		if (cablePosition == null) {
			LOGGER.error("Attemtping to find a path with a null source position.");
			return Collections.emptyList();
		}

		if (destination == null) {
			LOGGER.error("Attemtping to find a path with a null destination position.");
			return Collections.emptyList();
		}

		return cacheNewPath(cablePosition, destination, moduleType);
		// TEMPORARY
//		if (hasPath(cablePosition, destination, moduleType)) {
//			return Cache.get(destination).get(cablePosition).stream().filter(path -> path.getSupportedNetworkType().equals(moduleType)).collect(Collectors.toList());
//		} else {
//			return cacheNewPath(cablePosition, destination, moduleType);
//		}
	}

	/**
	 * Attempts to calculate the paths between the provided source and destination,
	 * and returns the calculated paths if successful.
	 * 
	 * @param source
	 * @param destination
	 * @return
	 */
	private List<Path> cacheNewPath(BlockPos source, BlockPos destination, CableNetworkModuleType moduleType) {
		// Perform the path finding.
		NetworkPathFinder pathFinder = new NetworkPathFinder(OwningNetwork.getGraph(), OwningNetwork.getWorld(), source, destination, moduleType, (cable, dist) -> dist);
		List<Path> paths = pathFinder.executeAlgorithm();

		// If we found no paths, return early.
		if (paths.size() == 0) {
			return Collections.emptyList();
		}

		// Cache each provided path.
		if (!Cache.containsKey(destination)) {
			Cache.put(destination, new HashMap<BlockPos, List<Path>>());
		}
		if (!Cache.get(destination).containsKey(source)) {
			Cache.get(destination).put(source, new ArrayList<Path>());
		}
		Cache.get(destination).get(source).addAll(paths);

		// If we have a destination for this, sort it by length.
		if (Cache.containsKey(destination)) {
			Cache.get(destination).get(source).sort(Comparator.comparingDouble(Path::getLength));
		}

		// Check if we now have the path.
		return paths;
	}

	/**
	 * Clears all cached paths.
	 */
	public void invalidateCache() {
		Cache.clear();
		StaticPower.LOGGER.debug(String.format("Path cache invalidated for network with origin cable position: %1$s!", OwningNetwork.getOrigin().toString()));
	}
}
