package theking530.staticcore.cablenetwork.capabilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;
import theking530.staticcore.cablenetwork.ServerCable;

public abstract class ServerCableCapabilityType implements IForgeRegistryEntry<ServerCableCapabilityType> {
	private ResourceLocation registryName;

	public abstract ServerCableCapability create(ServerCable owningCable);

	public final ServerCableCapability create(ServerCable owningCable, CompoundTag tag) {
		ServerCableCapability module = create(owningCable);
		module.load(tag);
		return module;
	}

	@Override
	public ServerCableCapabilityType setRegistryName(ResourceLocation name) {
		registryName = name;
		return this;
	}

	@Override
	public ResourceLocation getRegistryName() {
		return registryName;
	}

	@Override
	public Class<ServerCableCapabilityType> getRegistryType() {
		return ServerCableCapabilityType.class;
	}
}
