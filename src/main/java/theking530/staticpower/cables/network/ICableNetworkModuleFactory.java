package theking530.staticpower.cables.network;

import net.minecraft.resources.ResourceLocation;

public interface ICableNetworkModuleFactory {
	public AbstractCableNetworkModule create(ResourceLocation moduleName);
}
