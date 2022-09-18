package theking530.staticpower.cables.scaffold;

import net.minecraft.resources.ResourceLocation;
import theking530.staticpower.cables.network.modules.CableNetworkModule;
import theking530.staticpower.cables.network.modules.ICableNetworkModuleFactory;

public class ScaffoldNetworkModuleFactory implements ICableNetworkModuleFactory {

	@Override
	public CableNetworkModule create(ResourceLocation moduleName) {
		return new ScaffoldNetworkModule();
	}
}
