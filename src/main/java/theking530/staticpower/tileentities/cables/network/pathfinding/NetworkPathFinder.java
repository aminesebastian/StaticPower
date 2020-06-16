package theking530.staticpower.tileentities.cables.network.pathfinding;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import theking530.staticpower.tileentities.cables.network.CableNetworkGraph;
import theking530.staticpower.tileentities.cables.network.pathfinding.Path.PathEntry;

public class NetworkPathFinder {
	private final HashSet<BlockPos> AllPositions;
	private final HashSet<BlockPos> DestinationPositions;
	private final HashMap<BlockPos, Path> Paths;
	private final List<Path> OutputPaths;
	private final HashSet<BlockPos> ScannedPositions;
	private final BlockPos StartingCablePosition;

	public NetworkPathFinder(CableNetworkGraph graph, BlockPos startingCablePosition) {
		// Capture all the positions in the network graph.
		AllPositions = new HashSet<BlockPos>();
		graph.getCables().forEach(cable -> AllPositions.add(cable.getPos()));
		graph.getDestinations().forEach(destination -> AllPositions.add(destination.getPos()));

		// Capture all destination positions in the network graph.
		DestinationPositions = new HashSet<BlockPos>();
		graph.getDestinations().forEach(destination -> DestinationPositions.add(destination.getPos()));

		// Capture the starting cable position.
		StartingCablePosition = startingCablePosition;

		ScannedPositions = new HashSet<BlockPos>();
		Paths = new HashMap<BlockPos, Path>();
		OutputPaths = new LinkedList<Path>();
	}

	public List<Path> executeAlgorithm() {
		// Add the path for the initial starting cable.
		Paths.put(StartingCablePosition, new Path(StartingCablePosition, StartingCablePosition, new PathEntry(StartingCablePosition, Direction.DOWN)));

		// Execute the algorithm starting at the starting cable.
		algorithmWorker(StartingCablePosition);

		return OutputPaths;
	}

	private void algorithmWorker(BlockPos cable) {
		ScannedPositions.add(cable);

		// Iterate through all the adjacents.
		for (Direction dir : Direction.values()) {
			// Check for any adjacent cables. If no, skip this direction.
			if (!AllPositions.contains(cable.offset(dir))) {
				continue;
			}

			// Get the adjacent cable or destination.
			BlockPos adjacent = cable.offset(dir);

			// Skip already scanned positions.
			if (ScannedPositions.contains(adjacent)) {
				continue;
			}

			// Get the previous path to here.
			Path prevPath = Paths.get(cable);

			// RECURSE only if the adjacent position was a cable and not a destination. If
			// it is a destination, stop now.
			if (!DestinationPositions.contains(adjacent)) {
				Paths.put(adjacent, Path.fromPreviousPath(prevPath, dir, adjacent));
				algorithmWorker(adjacent);
			} else {
				// We have reached a destination. We should cache some info about this
				// destination.
				OutputPaths.add(Path.fromPreviousPath(prevPath, dir, adjacent));
			}
		}
	}
}
