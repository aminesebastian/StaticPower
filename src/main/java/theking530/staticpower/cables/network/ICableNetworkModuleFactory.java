package theking530.staticpower.cables.network;

import net.minecraft.util.ResourceLocation;

public interface ICableNetworkModuleFactory {
	public AbstractCableNetworkModule create(ResourceLocation moduleName);
}
