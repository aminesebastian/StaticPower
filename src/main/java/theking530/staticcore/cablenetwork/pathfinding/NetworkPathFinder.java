package theking530.staticcore.cablenetwork.pathfinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.function.BiFunction;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import theking530.staticcore.cablenetwork.CableNetworkGraph;
import theking530.staticcore.cablenetwork.CableNetworkManager;
import theking530.staticcore.cablenetwork.ServerCable;
import theking530.staticcore.cablenetwork.modules.CableNetworkModuleType;
import theking530.staticcore.cablenetwork.pathfinding.Path.PathEntry;
import theking530.staticcore.cablenetwork.scanning.CableScanLocation;
import theking530.staticpower.utilities.WorldUtilities;

public class NetworkPathFinder {
	private final HashSet<BlockPos> existingCables;
	private final HashSet<BlockPos> destinationAdjacentCables;
	private final BlockPos startingCablePosition;
	private final BlockPos endingPosition;
	private final HashSet<BlockPos> visitedPositions;
	private final HashMap<BlockPos, PathEntry> predecessors;
	private final CableNetworkModuleType supportedNetworkType;
	private final Queue<BlockPos> bfsQueue;
	private final Level world;
	private final boolean destinationIsCable;
	private final BiFunction<ServerCable, Float, Float> lengthProvider;

	public NetworkPathFinder(CableNetworkGraph graph, Level world, BlockPos startingCablePosition, BlockPos endingPosition, CableNetworkModuleType supportedNetworkType,
			BiFunction<ServerCable, Float, Float> lengthProvider) {
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
		destinationIsCable = CableNetworkManager.get(world).isTrackingCable(endingPosition);
		if (!destinationIsCable) {
			for (Direction dir : Direction.values()) {
				ServerCable cable = CableNetworkManager.get(world).getCable(endingPosition.relative(dir));
				if (cable != null && cable.supportsNetworkModule(supportedNetworkType) && !cable.isDisabledOnSide(dir.getOpposite())) {
					destinationAdjacentCables.add(endingPosition.relative(dir));
				}
			}
		}

		// Capture the start position.
		this.startingCablePosition = startingCablePosition;
		this.endingPosition = endingPosition;
		this.supportedNetworkType = supportedNetworkType;
		this.lengthProvider = lengthProvider;
		this.world = world;

		visitedPositions = new HashSet<BlockPos>();
		predecessors = new HashMap<BlockPos, PathEntry>();
		bfsQueue = new LinkedList<BlockPos>();
	}

	public List<Path> executeDFSVersion() {
		List<Path> output = new LinkedList<Path>();

		// If we're already at the target location, create a 2 entry path (from the
		// starting outwards, and then from the starting to the end).
		if (isAtTargetLocation(startingCablePosition)) {
			List<PathEntry> rawPath = new ArrayList<>();
			rawPath.add(new PathEntry(startingCablePosition, null));
			rawPath.add(new PathEntry(endingPosition, WorldUtilities.getFacingFromPos(startingCablePosition, endingPosition)));
			output.add(new Path(startingCablePosition, endingPosition, supportedNetworkType, 1, rawPath));
		} else {
			List<PathEntry> initialPath = new ArrayList<>();
			Set<BlockPos> initialVisited = new HashSet<>();
			List<List<PathEntry>> rawPaths = new ArrayList<>();

			recurseDFS(rawPaths, new CableScanLocation(startingCablePosition, null, false), initialPath, initialVisited);

			System.out.println("Path count: " + rawPaths.size());

			for (List<PathEntry> entries : rawPaths) {
				float length = 0;
				for (PathEntry entry : entries) {
					length += entry.getDistance();
				}

				if (!destinationIsCable) {
					entries.add(new PathEntry(endingPosition, WorldUtilities.getFacingFromPos(entries.get(entries.size() - 1).getPosition(), endingPosition)));
				}

				output.add(new Path(startingCablePosition, endingPosition, supportedNetworkType, length, entries));
			}

			// Sort shortest to longest.
			output.sort((Path lhs, Path rhs) -> lhs.getLength() < rhs.getLength() ? -1 : lhs.getLength() == rhs.getLength() ? 0 : 1);

		}

		return output;
	}

	private void recurseDFS(List<List<PathEntry>> paths, CableScanLocation curr, List<PathEntry> currentPath, Set<BlockPos> visited) {
		visited.add(curr.getLocation());

		if (!CableNetworkManager.get(world).isTrackingCable(curr.getLocation())) {
			return;
		}

		currentPath.add(new PathEntry(curr.getLocation(), curr.getSide()));

		if (isAtTargetLocation(curr.getLocation())) {
			paths.add(currentPath);
			return;
		}

		ServerCable cable = CableNetworkManager.get(world).getCable(curr.getLocation());
		List<CableScanLocation> toScan = new LinkedList<>();

		for (CableScanLocation scanLoc : cable.getScanLocations()) {
			if (visited.contains(scanLoc.getLocation())) {
				continue;
			}
			toScan.add(scanLoc);
		}

		if (toScan.isEmpty()) {
			return;
		}

		if (toScan.size() == 1) {
			recurseDFS(paths, toScan.get(0), currentPath, visited);
		} else {
			List<List<PathEntry>> pathCopies = new LinkedList<>();
			List<Set<BlockPos>> visitedCopies = new LinkedList<>();
			for (int i = 0; i < toScan.size(); i++) {
				pathCopies.add(new LinkedList<>(currentPath));
				visitedCopies.add(new HashSet<>(visited));
			}

			for (int i = 0; i < toScan.size(); i++) {
				recurseDFS(paths, toScan.get(i), pathCopies.get(i), visitedCopies.get(i));
			}
		}
	}

	private boolean isAtTargetLocation(BlockPos pos) {
		if (destinationIsCable) {
			return pos.equals(endingPosition);
		}
		return destinationAdjacentCables.contains(pos);
	}

	public List<Path> executeAlgorithm() {
		// If we have no terminus nodes, return an empty list.
		if (!destinationIsCable && destinationAdjacentCables.size() == 0) {
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

			for (CableScanLocation scanLoc : cable.getScanLocations()) {
				// Get the adjacent and check if we have visited it before. If we have, skip it.
				// Also, skip it if it's not part of our graph nodes list.
				// Finally, skip it if the filter fails.
				if (!existingCables.contains(scanLoc.getLocation()) || visitedPositions.contains(scanLoc.getLocation())) {
					continue;
				}

				// Mark the adjacent as visited
				visitedPositions.add(scanLoc.getLocation());

				// Cache the predecessor to this location.
				predecessors.put(scanLoc.getLocation(), new PathEntry(curr, scanLoc.getSide()));

				// Now we add the position to the BFS queue and continue.
				bfsQueue.add(scanLoc.getLocation());
			}
		}

		return generateOutputPaths();
	}

	protected List<Path> generateOutputPaths() {
		List<Path> output = new LinkedList<Path>();
		if (destinationIsCable) {
			Path path = generatePathToOutput(endingPosition);
			if (path != null) {
				output.add(path);
			}
		} else {
			// Go through all predecessors.
			for (BlockPos terminus : destinationAdjacentCables) {
				// This will generate the shortest path to the terminus.
				Path path = generatePathToOutput(terminus);
				if (path != null) {
					output.add(path);
				}
			}
		}

		System.out.println("Path count: " + output.size());
		return output;
	}

	protected Path generatePathToOutput(BlockPos penultimatePosition) {
		// If this potential last cable does not exist in the predecessors list, odds
		// are its just air.
		if (!predecessors.containsKey(penultimatePosition)) {
			return null;
		}

		// Get the predecessor to the last cable (if there is one, this path could just
		// be the single cable).
		PathEntry curr = predecessors.get(penultimatePosition);

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
		// Take the distance, then multiply by 10, clamp to int, and then divide.
		// This should give us a distance with a single decimal.
		double distance = Math.sqrt(penultimatePosition.distSqr(endingPosition));
		distance *= 10;
		int roundedDistance = (int) distance;

		pathEntries.add(new PathEntry(penultimatePosition, WorldUtilities.getFacingFromPos(penultimatePosition, endingPosition)));
		pathEntries.add(new PathEntry(endingPosition, WorldUtilities.getFacingFromPos(penultimatePosition, endingPosition)));

		float length = 1;
		Set<BlockPos> deduplicationList = new HashSet<>();
		for (PathEntry entry : pathEntries) {
			if (deduplicationList.contains(entry.getPosition())) {
				continue;
			}

			deduplicationList.add(entry.getPosition());
			length += entry.getDistance();
		}

		return new Path(startingCablePosition, endingPosition, supportedNetworkType, length, pathEntries);
	}

	protected double calculateDistance(BlockPos starting, BlockPos ending) {
		double distance = Math.sqrt(starting.distSqr(ending));
		distance *= 10;
		return ((int) distance) / 10;

	}
}
