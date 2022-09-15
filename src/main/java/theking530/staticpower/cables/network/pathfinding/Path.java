package theking530.staticpower.cables.network.pathfinding;

import java.util.Arrays;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

public class Path {
	private final BlockPos sourceLocation;
	private final BlockPos destinationLocation;
	private final PathEntry[] path;
	private final float length;
	private final ResourceLocation supportedNetworkType;

	public Path(BlockPos source, BlockPos destination, ResourceLocation supportedNetworkType, float length, PathEntry... path) {
		sourceLocation = source;
		destinationLocation = destination;
		this.path = path;
		this.length = length;
		this.supportedNetworkType = supportedNetworkType;
	}

	public Path(CompoundTag nbt) {
		// Get the serialized entries.
		ListTag entries = nbt.getList("entries", Tag.TAG_COMPOUND);

		// Create the array to contain the entries.
		path = new PathEntry[entries.size()];

		// Create the entries.
		for (int i = 0; i < entries.size(); i++) {
			CompoundTag entryTag = (CompoundTag) entries.get(i);
			path[i] = PathEntry.createFromNbt(entryTag);
		}
		length = nbt.getFloat("length");

		// Get the source and destination locations.
		sourceLocation = BlockPos.of(nbt.getLong("source"));
		destinationLocation = BlockPos.of(nbt.getLong("destination"));
		supportedNetworkType = new ResourceLocation(nbt.getString("supported_network_module"));
	}

	public BlockPos getSourceLocation() {
		return sourceLocation;
	}

	public BlockPos getSourceCableLocation() {
		return path[1].getPosition();
	}

	public BlockPos getDestinationLocation() {
		return destinationLocation;
	}

	public BlockPos getFinalCablePosition() {
		return path[path.length - 2].getPosition();
	}

	public Direction getFinalCableExitDirection() {
		return path[path.length - 2].getDirectionOfEntry();
	}

	public PathEntry[] getEntries() {
		return path;
	}

	public ResourceLocation getSupportedNetworkType() {
		return supportedNetworkType;
	}

	/**
	 * Returns the final change of direction that arrives at the destination. Useful
	 * to see which side of the destination the final cable in the path is.
	 * 
	 * @return
	 */
	public Direction getDestinationDirection() {
		return path[path.length - 1].getDirectionOfEntry();
	}

	public float getLength() {
		return length;
	}

	public int getPathEntryCount() {
		return path.length;
	}

	@Override
	public String toString() {
		return "Path [path=" + Arrays.toString(path) + ", destinationLocation=" + destinationLocation + ", sourceLocation=" + sourceLocation + ", supportedNetworkType="
				+ supportedNetworkType + "]";
	}

	public CompoundTag writeToNbt(CompoundTag nbt) {
		// Serialize the source and destination locations.
		nbt.putLong("source", sourceLocation.asLong());
		nbt.putLong("destination", destinationLocation.asLong());

		// Serialize the parcels to the list.
		ListTag pathNBTList = new ListTag();
		for (PathEntry entry : path) {
			CompoundTag entryTag = new CompoundTag();
			entry.writeToNbt(entryTag);
			pathNBTList.add(entryTag);
		}
		nbt.put("entries", pathNBTList);
		nbt.putFloat("length", length);
		nbt.putString("supported_network_module", supportedNetworkType.toString());
		return nbt;
	}

	public static class PathEntry {
		private final BlockPos position;
		private final Direction entryDirection;

		public PathEntry(BlockPos position, Direction directionOfApproach) {
			this.position = position;
			entryDirection = directionOfApproach;
		}

		/**
		 * The position of this path entry.
		 * 
		 * @return
		 */
		public BlockPos getPosition() {
			return position;
		}

		/**
		 * The direction to approach this entry to. Meaning, if you are traversing the
		 * network, and you are at the cable before this entry, this is the direction
		 * you must travel to arrive at this cable, NOT the direction from which a
		 * traverser will approach this cable/destination.
		 * 
		 * @return
		 */
		public Direction getDirectionOfEntry() {
			return entryDirection;
		}

		public CompoundTag writeToNbt(CompoundTag nbt) {
			nbt.putLong("position", position.asLong());
			if (entryDirection != null) {
				nbt.putInt("direction", entryDirection.ordinal());
			}

			return nbt;
		}

		public static PathEntry createFromNbt(CompoundTag nbt) {
			return new PathEntry(BlockPos.of(nbt.getLong("position")), nbt.contains("direction") ? Direction.values()[nbt.getInt("direction")] : null);
		}

		@Override
		public String toString() {
			return "PathEntry [position=" + position + ", entryDirection=" + entryDirection + "]";
		}
	}
}
