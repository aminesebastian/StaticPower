package theking530.staticcore.cablenetwork.capabilities;

import net.minecraft.nbt.CompoundTag;
import theking530.staticcore.cablenetwork.Cable;

public abstract class ServerCableCapabilityType<T extends ServerCableCapability> {

	public abstract T create(Cable owningCable);

	public final T create(Cable owningCable, CompoundTag tag) {
		T module = create(owningCable);
		module.load(tag);
		return module;
	}
}
