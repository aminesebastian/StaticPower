package theking530.staticpower.tileentities.cables.network.pathfinding;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public class Path {
	private final BlockPos SourceLocation;
	private final BlockPos DestinationLocation;
	private final PathEntry[] Path;

	public Path(BlockPos source, BlockPos destination, PathEntry... path) {
		SourceLocation = source;
		DestinationLocation = destination;
		Path = path;
	}

	public BlockPos getSourceLocation() {
		return SourceLocation;
	}

	public BlockPos getDestinationLocation() {
		return DestinationLocation;
	}

	public PathEntry[] getPath() {
		return Path;
	}

	/**
	 * Returns the final change of direction that arrives at the destination. Useful
	 * to see which side of the destination the final cable in the path is.
	 * 
	 * @return
	 */
	public Direction getDestinationDirection() {
		return Path[Path.length - 1].getDirectionOfEntry();
	}

	public int getLength() {
		return Path.length;
	}

	public static Path fromPreviousPath(Path previous, Direction directionToNewDestination, BlockPos newDestination) {
		// Create a new array equal to the size of the previous path PLUS 1.
		PathEntry[] newPathBlocks = new PathEntry[previous.getLength() + 1];

		// Add the previous path to the new path.
		for (int i = 0; i < previous.getLength(); i++) {
			newPathBlocks[i] = previous.getPath()[i];
		}

		// But include an additional position for the new destination.
		newPathBlocks[previous.getLength()] = new PathEntry(newDestination, directionToNewDestination);

		// Return the path.
		return new Path(previous.SourceLocation, newDestination, newPathBlocks);
	}

	public static class PathEntry {
		private final BlockPos Position;
		private final Direction DirectionOfEntry;

		public PathEntry(BlockPos position, Direction directionOfApproach) {
			Position = position;
			DirectionOfEntry = directionOfApproach;
		}

		/**
		 * The position of this path entry.
		 * 
		 * @return
		 */
		public BlockPos getPosition() {
			return Position;
		}

		/**
		 * The direction to approach this entry to. Meaning, if you are traversing the
		 * network, and you are at the cable before this entry, this is the direction
		 * you must travel to arrive at this cable, NOT the direction from which a
		 * traverser will approach this cable.
		 * 
		 * @return
		 */
		public Direction getDirectionOfEntry() {
			return DirectionOfEntry;
		}
	}

}
