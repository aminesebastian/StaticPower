package theking530.staticcore.cablenetwork.modules;

import net.minecraft.nbt.CompoundTag;

public abstract class CableNetworkModuleType {

	public abstract CableNetworkModule create();

	public final CableNetworkModule create(CompoundTag tag) {
		CableNetworkModule module = create();
		module.readFromNbt(tag);
		return module;
	}
}
