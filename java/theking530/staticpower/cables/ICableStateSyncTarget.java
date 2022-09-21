package theking530.staticpower.cables;

import net.minecraft.nbt.CompoundTag;
import theking530.staticpower.cables.network.ServerCable;

public interface ICableStateSyncTarget {
	public void synchronizeServerToClient(ServerCable cable, CompoundTag tag);

	public void recieveCableSyncState(CompoundTag tag);
}
