package theking530.staticcore.cablenetwork.capabilities;

import net.minecraft.nbt.CompoundTag;
import theking530.staticcore.cablenetwork.ServerCable;

public abstract class ServerCableCapabilityType {

	public abstract ServerCableCapability create(ServerCable owningCable);

	public final ServerCableCapability create(ServerCable owningCable, CompoundTag tag) {
		ServerCableCapability module = create(owningCable);
		module.load(tag);
		return module;
	}
}
