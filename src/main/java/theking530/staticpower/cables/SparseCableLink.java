package theking530.staticpower.cables;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

public record SparseCableLink(BlockPos linkToPosition, SparseCableConnectionType connectionType) {
	public enum SparseCableConnectionType {
		STARTING, ENDING
	}

	public CompoundTag serialize() {
		CompoundTag output = new CompoundTag();
		output.putLong("p", linkToPosition.asLong());
		output.putByte("t", (byte) connectionType.ordinal());
		return output;
	}

	public static SparseCableLink fromTag(CompoundTag tag) {
		return new SparseCableLink(BlockPos.of(tag.getLong("p")), SparseCableConnectionType.values()[tag.getByte("t")]);
	}
}
