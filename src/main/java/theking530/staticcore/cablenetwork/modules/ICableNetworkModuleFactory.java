package theking530.staticcore.cablenetwork.modules;

import net.minecraft.resources.ResourceLocation;

public interface ICableNetworkModuleFactory {
	public CableNetworkModule create(ResourceLocation moduleName);
}
