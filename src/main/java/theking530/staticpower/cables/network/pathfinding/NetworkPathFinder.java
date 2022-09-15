package theking530.staticpower.cables.network.pathfinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.function.BiFunction;
import java.util.function.Function;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import theking530.staticpower.cables.SparseCableLink;
import theking530.staticpower.cables.network.CableNetworkGraph;
import theking530.staticpower.cables.network.CableNetworkManager;
import theking530.staticpower.cables.network.ServerCable;
import theking530.staticpower.cables.network.pathfinding.Path.PathEntry;
import theking530.staticpower.utilities.WorldUtilities;

public class NetworkPathFinder {
	private final HashSet<BlockPos> existingCables;
	private final HashSet<BlockPos> destinationAdjacentCables;
	private final BlockPos startingCablePosition;
	private final BlockPos endingPosition;
	private final HashSet<BlockPos> visitedPositions;
	private final HashMap<BlockPos, PathEntry> predecessors;
	private final ResourceLocation supportedNetworkType;
	private final Queue<BlockPos> bfsQueue;
	private final Level world;
	private final BiFunction<ServerCable, Double, Float> lengthProvider;

	public NetworkPathFinder(CableNetworkGraph graph, Level world, BlockPos startingCablePosition, BlockPos targetPosition, ResourceLocation supportedNetworkType,
			BiFunction<ServerCable, Double, Float> lengthProvider) {
		// Capture all the positions in the network graph.
		existingCables = new HashSet<BlockPos>();
		graph.getCables().values().forEach(cable -> {
			if (cable.supportsNetworkModule(supportedNetworkType)) {
				existingCables.add(cable.getPos());
			}
		});

		// Capture all the terminus nodes in the network graph (the final cables before
		// the target), if they support the network type.
		destinationAdjacentCables = new HashSet<BlockPos>();
		for (Direction dir : Direction.values()) {
			ServerCable cable = CableNetworkManager.get(world).getCable(targetPosition.relative(dir));
			if (cable != null && cable.supportsNetworkModule(supportedNetworkType) && !cable.isDisabledOnSide(dir.getOpposite())) {
				destinationAdjacentCables.add(targetPosition.relative(dir));
			}
		}

		// Capture the start position.
		this.startingCablePosition = startingCablePosition;
		this.endingPosition = targetPosition;
		this.supportedNetworkType = supportedNetworkType;
		this.lengthProvider = lengthProvider;
		this.world = world;

		visitedPositions = new HashSet<BlockPos>();
		predecessors = new HashMap<BlockPos, PathEntry>();
		bfsQueue = new LinkedList<BlockPos>();
	}

	public List<Path> executeAlgorithm() {
		// If we have no terminus nodes, return an empty list.
		if (destinationAdjacentCables.size() == 0) {
			return Collections.emptyList();
		}

		// Pre-populate the predecessors list.
		for (BlockPos node : existingCables) {
			predecessors.put(node, null);
		}

		// Initialize the visited and BFS structures with the starting position.
		visitedPositions.add(startingCablePosition);
		bfsQueue.add(startingCablePosition);

		// Keep iterating until the BFS queue is empty.
		while (!bfsQueue.isEmpty()) {
			// Get the current position off the queue.
			BlockPos curr = bfsQueue.poll();
			ServerCable cable = CableNetworkManager.get(world).getCable(curr);
			if (cable == null) {
				throw new RuntimeException(String.format("Attempted to use a null cable in a network path find. Error occured at location: %1$s.", curr));
			}

			// The following block of code builds the list of positions to scan around the
			// current cable.
			Map<BlockPos, Direction> positionsToScan = new HashMap<>();
			if (cable.isSparse()) {
				for (SparseCableLink link : cable.getSparseLinks()) {
					if (!existingCables.contains(link.linkToPosition()) || visitedPositions.contains(link.linkToPosition())) {
						continue;
					}
					positionsToScan.put(link.linkToPosition(), Direction.UP);
				}
			} else {
				// Scan all adjacent blocks.
				for (Direction dir : Direction.values()) {
					// Skip checking that block at this point in time because the cable is disabled
					// on that side. We may hit the block on this side again, but it will be later
					// when the path is longer.
					if (cable.isDisabledOnSide(dir)) {
						continue;
					}

					// Get the adjacent and check if we have visited it before. If we have, skip it.
					// Also, skip it if it's not part of our graph nodes list.
					// Finally, skip it if the filter fails.
					BlockPos adjacent = curr.relative(dir);
					if (!existingCables.contains(adjacent) || visitedPositions.contains(adjacent)) {
						continue;
					}
					positionsToScan.put(adjacent, dir);
				}
			}

			// Scan all positions captured in the previous step.
			for (BlockPos adjacent : positionsToScan.keySet()) {
				// Mark the adjacent as visited
				visitedPositions.add(adjacent);

				// Cache the predecessor to this location.
				predecessors.put(adjacent, new PathEntry(curr, positionsToScan.get(adjacent)));

				// Now we add the position to the BFS queue and continue.
				bfsQueue.add(adjacent);
			}
		}

		return generateOutputPaths();
	}

	protected List<Path> generateOutputPaths() {
		List<Path> output = new LinkedList<Path>();
		// Go through all predecessors.
		for (BlockPos terminus : destinationAdjacentCables) {
			// This will generate the shortest path to the terminus.
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
		if (!predecessors.containsKey(lastCable)) {
			return null;
		}

		// Get the predecessor to the last cable (if there is one, this path could just
		// be the single cable).
		PathEntry curr = predecessors.get(lastCable);

		// Build the path entries.
		List<PathEntry> pathEntries = new ArrayList<PathEntry>();

		// Build the path.
		while (curr != null) {
			pathEntries.add(curr);
			curr = predecessors.get(curr.getPosition());
		}
		// Add the starting cable - direction doesn't matter here.
		pathEntries.add(new PathEntry(startingCablePosition, null));

		// The whole path is currently reversed, so unreverse it.
		Collections.reverse(pathEntries);

		// Add the last cable and the end position.
		pathEntries.add(new PathEntry(lastCable, WorldUtilities.getFacingFromPos(lastCable, endingPosition)));
		pathEntries.add(new PathEntry(endingPosition, WorldUtilities.getFacingFromPos(lastCable, endingPosition)));

		float length = 1;
		if (pathEntries.size() > 1) {
			for (int i = 1; i < pathEntries.size(); i++) {
				PathEntry previous = pathEntries.get(i - 1);
				PathEntry current = pathEntries.get(i);
				float distance = current.getPosition().distManhattan(previous.getPosition());
				length += distance;
			}
		}
//		for (PathEntry entry : pathEntries) {
//			ServerCable cable = CableNetworkManager.get(world).getCable(entry.getPosition());
//			if (cable != null) {
//				length += lengthProvider.apply(cable);
//			}
//		}

		// Convert the list to an array and create the final path.
		PathEntry[] entries = new PathEntry[pathEntries.size()];
		pathEntries.toArray(entries);
		return new Path(startingCablePosition, endingPosition, supportedNetworkType, length, entries);
	}
}
