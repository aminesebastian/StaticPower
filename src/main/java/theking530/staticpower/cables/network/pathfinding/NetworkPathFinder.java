package theking530.staticpower.cables.network.pathfinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import theking530.staticpower.cables.network.CableNetworkGraph;
import theking530.staticpower.cables.network.Path;

public class NetworkPathFinder {
	private final HashSet<BlockPos> CablePositions;
	private final HashMap<BlockPos, Path> Paths;
	private final HashSet<BlockPos> ScannedPositions;
	private final BlockPos StartingCablePosition;

	public NetworkPathFinder(CableNetworkGraph graph, BlockPos startingCablePosition) {
		// Capture all the cable positions in the network graph.
		CablePositions = new HashSet<BlockPos>();
		graph.getCables().forEach(cable -> CablePositions.add(cable.getPos()));
		graph.getDestinations().forEach(destination -> CablePositions.add(destination.getPos()));
		
		// Capture the starting cable position.
		StartingCablePosition = startingCablePosition;

		ScannedPositions = new HashSet<BlockPos>();
		Paths = new HashMap<BlockPos, Path>();
	}

	public HashMap<BlockPos, Path> executeAlgorithm() {
		algorithmWorker(StartingCablePosition);
		return Paths;
	}

	private void algorithmWorker(BlockPos cable) {
		ScannedPositions.add(cable);

		// Iterate through all the adjacents.
		for (BlockPos adjacent : getAdjacents(cable)) {
			// Skip already scanned positions.
			if (ScannedPositions.contains(adjacent)) {
				continue;
			}

			// IF we have a path to this adjacent already, create a new path for it using
			// the previous. Otherwise, create a new path.
			if (Paths.containsKey(cable)) {
				Path prevPath = Paths.get(cable);
				Paths.put(adjacent, Path.fromPreviousPath(prevPath, adjacent));
			} else {
				Paths.put(adjacent, new Path(cable, adjacent, cable, adjacent));
			}

			// RECURSE.
			algorithmWorker(adjacent);
		}
	}

	public List<BlockPos> getAdjacents(BlockPos pos) {
		List<BlockPos> output = new ArrayList<BlockPos>();
		for (Direction dir : Direction.values()) {
			if (CablePositions.contains(pos.offset(dir))) {
				output.add(pos.offset(dir));
			}
		}
		return output;
	}
}
