package theking530.staticpower.cables;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

/**
 * 
 * 
 * The reason we have a {{@link #linkId} here instead of just using the
 * {{@link #linkToPosition} as a unique identifier is if we replace the link
 * connection at a point with a new one within one tick, when we sync to the
 * client, they'll have no way of knowing that this is a new connection to the
 * same position.
 * 
 * @author amine
 *
 */
public record SparseCableLink(long linkId, BlockPos linkToPosition, CompoundTag data, SparseCableConnectionType connectionType) {
	public enum SparseCableConnectionType {
		STARTING, ENDING
	}

	public CompoundTag serialize() {
		CompoundTag output = new CompoundTag();
		output.putLong("i", linkId);
		output.putLong("p", linkToPosition.asLong());
		output.putByte("t", (byte) connectionType.ordinal());
		output.put("d", data);
		return output;
	}

	public static SparseCableLink fromTag(CompoundTag tag) {
		return new SparseCableLink(tag.getLong("i"), BlockPos.of(tag.getLong("p")), tag.getCompound("d"), SparseCableConnectionType.values()[tag.getByte("t")]);
	}
}
