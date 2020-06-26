package theking530.staticpower.cables.network.pathfinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import theking530.staticpower.cables.network.CableNetworkGraph;
import theking530.staticpower.cables.network.CableNetworkManager;
import theking530.staticpower.cables.network.pathfinding.Path.PathEntry;
import theking530.staticpower.utilities.WorldUtilities;

public class NetworkPathFinder {
	private final HashSet<BlockPos> GraphNodes;
	private final HashSet<BlockPos> TerminusNodes;
	private final BlockPos StartingCablePosition;
	private final BlockPos EndingCablePosition;
	private final HashSet<BlockPos> VisitedPositions;
	private final HashMap<BlockPos, PathEntry> Predecessors;
	private final ResourceLocation SupportedNetworkType;
	private final Queue<BlockPos> BFSQueue;

	public NetworkPathFinder(CableNetworkGraph graph, World world, BlockPos startingCablePosition, BlockPos targetPosition, ResourceLocation supportedNetworkType) {
		// Capture all the positions in the network graph.
		GraphNodes = new HashSet<BlockPos>();
		graph.getCables().values().forEach(cable -> {
			if (cable.supportsNetworkModule(supportedNetworkType)) {
				GraphNodes.add(cable.getPos());
			}
		});

		// Capture all the terminus nodes in the network graph (the final cables before
		// the target), if they support the network type.
		TerminusNodes = new HashSet<BlockPos>();
		for (Direction dir : Direction.values()) {
			if (CableNetworkManager.get(world).isTrackingCable(targetPosition.offset(dir))
					&& CableNetworkManager.get(world).getCable(targetPosition.offset(dir)).supportsNetworkModule(supportedNetworkType)) {
				TerminusNodes.add(targetPosition.offset(dir));
			}
		}

		// Capture the start position.
		StartingCablePosition = startingCablePosition;
		EndingCablePosition = targetPosition;
		SupportedNetworkType = supportedNetworkType;
		VisitedPositions = new HashSet<BlockPos>();
		Predecessors = new HashMap<BlockPos, PathEntry>();
		BFSQueue = new LinkedList<BlockPos>();
	}

	public List<Path> executeAlgorithm() {
		// If we have no terminus nodes, return an empty list.
		if (TerminusNodes.size() == 0) {
			return new ArrayList<Path>();
		}

		// Pre-populate the predecessors list.
		for (BlockPos node : GraphNodes) {
			Predecessors.put(node, null);
		}

		// Initialize the visited and BFS structures.
		VisitedPositions.add(StartingCablePosition);
		BFSQueue.add(StartingCablePosition);

		// Keep iterating until the BFS queue is empty.
		while (!BFSQueue.isEmpty()) {
			// Get the current position off the queue.
			BlockPos curr = BFSQueue.poll();

			// Scan all adjacent blocks.
			for (Direction dir : Direction.values()) {
				// Get the adjacent and check if we have visited it before. If we have, skip it.
				BlockPos adjacent = curr.offset(dir);
				if (!GraphNodes.contains(adjacent) || VisitedPositions.contains(adjacent)) {
					continue;
				}

				// Mark the adjacent as visited
				VisitedPositions.add(adjacent);

				// Cache the predecessor to this location.
				Predecessors.put(adjacent, new PathEntry(curr, dir));

				// If we got this far, we should recurse on this node.
				BFSQueue.add(adjacent);
			}
		}
		return generateOutputPaths();
	}

	protected List<Path> generateOutputPaths() {
		List<Path> output = new LinkedList<Path>();
		// Go through all predecessors.
		for (BlockPos terminus : TerminusNodes) {
			Path path = generatePathToOutput(terminus);
			if (path != null) {
				output.add(path);
			}
		}
		return output;
	}

	protected Path generatePathToOutput(BlockPos lastCable) {
		// If this potential last cable does not exist in the predecessors list, odds
		// are its just air.
		if (!Predecessors.containsKey(lastCable)) {
			return null;
		}

		// Get the predecessor to the last cable (if there is one, this path could just
		// be the single cable).
		PathEntry curr = Predecessors.get(lastCable);

		// Build the path entries.
		List<PathEntry> pathEntries = new ArrayList<PathEntry>();

		// Build the path.
		while (curr != null) {
			pathEntries.add(curr);
			curr = Predecessors.get(curr.getPosition());
		}
		// Add the starting cable - direction doesn't matter here.
		pathEntries.add(new PathEntry(StartingCablePosition, null));

		// The whole path is currently reversed, so unreverse it.
		Collections.reverse(pathEntries);

		// Add the last cable and the end position.
		pathEntries.add(new PathEntry(lastCable, WorldUtilities.getFacingFromPos(lastCable, EndingCablePosition)));
		pathEntries.add(new PathEntry(EndingCablePosition, WorldUtilities.getFacingFromPos(lastCable, EndingCablePosition)));

		// Cover the list to an array and create the final path.
		PathEntry[] entries = new PathEntry[pathEntries.size()];
		pathEntries.toArray(entries);
		return new Path(StartingCablePosition, EndingCablePosition, SupportedNetworkType, entries);
	}
}
