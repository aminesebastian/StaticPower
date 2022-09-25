package theking530.staticcore.cablenetwork.modules;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

public abstract class CableNetworkModuleType implements IForgeRegistryEntry<CableNetworkModuleType> {
	private ResourceLocation registryName;

	public abstract CableNetworkModule create();

	public final CableNetworkModule create(CompoundTag tag) {
		CableNetworkModule module = create();
		module.readFromNbt(tag);
		return module;
	}

	@Override
	public CableNetworkModuleType setRegistryName(ResourceLocation name) {
		registryName = name;
		return this;
	}

	@Override
	public ResourceLocation getRegistryName() {
		return registryName;
	}

	@Override
	public Class<CableNetworkModuleType> getRegistryType() {
		return CableNetworkModuleType.class;
	}

	@Override
	public String toString() {
		return this.getRegistryName().toString();
	}
}
