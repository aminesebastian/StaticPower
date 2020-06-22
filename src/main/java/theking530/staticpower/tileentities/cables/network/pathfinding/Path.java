package theking530.staticpower.tileentities.cables.network.pathfinding;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;

public class Path {
	private final BlockPos SourceLocation;
	private final BlockPos DestinationLocation;
	private final PathEntry[] Path;
	private final ResourceLocation SupportedNetworkType;

	public Path(BlockPos source, BlockPos destination, ResourceLocation supportedNetworkType, PathEntry... path) {
		SourceLocation = source;
		DestinationLocation = destination;
		Path = path;
		SupportedNetworkType = supportedNetworkType;
	}

	public Path(CompoundNBT nbt) {
		// Get the serialized entries.
		ListNBT entries = nbt.getList("entries", Constants.NBT.TAG_COMPOUND);

		// Create the array to contain the entries.
		Path = new PathEntry[entries.size()];

		// Create the entries.
		for (int i = 0; i < entries.size(); i++) {
			CompoundNBT entryTag = (CompoundNBT) entries.get(i);
			Path[i] = PathEntry.createFromNbt(entryTag);
		}

		// Get the source and destination locations.
		SourceLocation = BlockPos.fromLong(nbt.getLong("source"));
		DestinationLocation = BlockPos.fromLong(nbt.getLong("destination"));
		SupportedNetworkType = new ResourceLocation(nbt.getString("supported_network_module"));
	}

	public BlockPos getSourceLocation() {
		return SourceLocation;
	}

	public BlockPos getDestinationLocation() {
		return DestinationLocation;
	}

	public BlockPos getFinalCablePosition() {
		return Path[Path.length - 2].getPosition();
	}

	public Direction getFinalCableExitDirection() {
		return Path[Path.length - 2].getDirectionOfEntry();
	}

	public PathEntry[] getEntries() {
		return Path;
	}

	public ResourceLocation getSupportedNetworkType() {
		return SupportedNetworkType;
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
		 * traverser will approach this cable/destination.
		 * 
		 * @return
		 */
		public Direction getDirectionOfEntry() {
			return DirectionOfEntry;
		}

		public CompoundNBT writeToNbt(CompoundNBT nbt) {
			nbt.putLong("position", Position.toLong());
			if (DirectionOfEntry != null) {
				nbt.putInt("direction", DirectionOfEntry.ordinal());
			}

			return nbt;
		}

		public static PathEntry createFromNbt(CompoundNBT nbt) {
			return new PathEntry(BlockPos.fromLong(nbt.getLong("position")), nbt.contains("direction") ? Direction.values()[nbt.getInt("direction")] : null);
		}
	}

	public CompoundNBT writeToNbt(CompoundNBT nbt) {
		// Serialize the source and destination locations.
		nbt.putLong("source", SourceLocation.toLong());
		nbt.putLong("destination", DestinationLocation.toLong());

		// Serialize the parcels to the list.
		ListNBT pathNBTList = new ListNBT();
		for (PathEntry entry : Path) {
			CompoundNBT entryTag = new CompoundNBT();
			entry.writeToNbt(entryTag);
			pathNBTList.add(entryTag);
		}
		nbt.put("entries", pathNBTList);
		nbt.putString("supported_network_module", SupportedNetworkType.toString());
		return nbt;
	}
}
