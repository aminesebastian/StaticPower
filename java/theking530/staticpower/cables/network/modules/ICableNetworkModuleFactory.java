package theking530.staticpower.cables.network.modules;

import net.minecraft.resources.ResourceLocation;

public interface ICableNetworkModuleFactory {
	public CableNetworkModule create(ResourceLocation moduleName);
}
