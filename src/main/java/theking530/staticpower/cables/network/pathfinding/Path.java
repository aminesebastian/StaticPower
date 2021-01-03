package theking530.staticpower.cables.network.pathfinding;

import java.util.Arrays;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;

public class Path {
	private final BlockPos sourceLocation;
	private final BlockPos destinationLocation;
	private final PathEntry[] path;
	private final ResourceLocation supportedNetworkType;

	public Path(BlockPos source, BlockPos destination, ResourceLocation supportedNetworkType, PathEntry... path) {
		sourceLocation = source;
		destinationLocation = destination;
		this.path = path;
		this.supportedNetworkType = supportedNetworkType;
	}

	public Path(CompoundNBT nbt) {
		// Get the serialized entries.
		ListNBT entries = nbt.getList("entries", Constants.NBT.TAG_COMPOUND);

		// Create the array to contain the entries.
		path = new PathEntry[entries.size()];

		// Create the entries.
		for (int i = 0; i < entries.size(); i++) {
			CompoundNBT entryTag = (CompoundNBT) entries.get(i);
			path[i] = PathEntry.createFromNbt(entryTag);
		}

		// Get the source and destination locations.
		sourceLocation = BlockPos.fromLong(nbt.getLong("source"));
		destinationLocation = BlockPos.fromLong(nbt.getLong("destination"));
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

	public int getLength() {
		return path.length;
	}

	@Override
	public String toString() {
		return "Path [path=" + Arrays.toString(path) + ", destinationLocation=" + destinationLocation + ", sourceLocation=" + sourceLocation + ", supportedNetworkType="
				+ supportedNetworkType + "]";
	}

	public CompoundNBT writeToNbt(CompoundNBT nbt) {
		// Serialize the source and destination locations.
		nbt.putLong("source", sourceLocation.toLong());
		nbt.putLong("destination", destinationLocation.toLong());

		// Serialize the parcels to the list.
		ListNBT pathNBTList = new ListNBT();
		for (PathEntry entry : path) {
			CompoundNBT entryTag = new CompoundNBT();
			entry.writeToNbt(entryTag);
			pathNBTList.add(entryTag);
		}
		nbt.put("entries", pathNBTList);
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

		public CompoundNBT writeToNbt(CompoundNBT nbt) {
			nbt.putLong("position", position.toLong());
			if (entryDirection != null) {
				nbt.putInt("direction", entryDirection.ordinal());
			}

			return nbt;
		}

		public static PathEntry createFromNbt(CompoundNBT nbt) {
			return new PathEntry(BlockPos.fromLong(nbt.getLong("position")), nbt.contains("direction") ? Direction.values()[nbt.getInt("direction")] : null);
		}

		@Override
		public String toString() {
			return "PathEntry [position=" + position + ", entryDirection=" + entryDirection + "]";
		}
	}
}
