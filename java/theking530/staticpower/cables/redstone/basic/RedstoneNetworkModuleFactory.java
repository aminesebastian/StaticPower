package theking530.staticpower.cables.redstone.basic;

import net.minecraft.resources.ResourceLocation;
import theking530.staticpower.cables.network.modules.CableNetworkModule;
import theking530.staticpower.cables.network.modules.ICableNetworkModuleFactory;

public class RedstoneNetworkModuleFactory implements ICableNetworkModuleFactory {

	@Override
	public CableNetworkModule create(ResourceLocation moduleName) {
		return new RedstoneNetworkModule(moduleName);
	}
}
