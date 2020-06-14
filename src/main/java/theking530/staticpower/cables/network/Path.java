package theking530.staticpower.cables.network;

import net.minecraft.util.math.BlockPos;

public class Path {
	private final BlockPos SourceLocation;
	private final BlockPos DestinationLocation;
	private final BlockPos[] Path;

	public Path(BlockPos source, BlockPos destination, BlockPos... path) {
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

	public BlockPos[] getPath() {
		return Path;
	}

	public int getLength() {
		return Path.length;
	}

	public static Path fromPreviousPath(Path previous, BlockPos newDestination) {
		// Create a new array equal to the size of the previous path PLUS 1.
		BlockPos[] newPathBlocks = new BlockPos[previous.getLength() + 1];

		// Add the previous path to the new path.
		for (int i = 0; i < previous.getLength(); i++) {
			newPathBlocks[i] = previous.getPath()[i];
		}

		// But include an additional position for the new destination.
		newPathBlocks[previous.getLength()] = newDestination;

		// Return the path.
		return new Path(previous.SourceLocation, newDestination, newPathBlocks);
	}
}
