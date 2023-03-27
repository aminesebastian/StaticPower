package theking530.staticcore.cablenetwork;

import net.minecraft.nbt.CompoundTag;

public interface ICableStateSyncTarget {
	public void recieveCableSyncState(CompoundTag tag);
}
