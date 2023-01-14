package theking530.staticcore.cablenetwork.capabilities;

import net.minecraft.nbt.CompoundTag;
import theking530.staticcore.cablenetwork.Cable;

public abstract class ServerCableCapabilityType {

	public abstract ServerCableCapability create(Cable owningCable);

	public final ServerCableCapability create(Cable owningCable, CompoundTag tag) {
		ServerCableCapability module = create(owningCable);
		module.load(tag);
		return module;
	}
}
