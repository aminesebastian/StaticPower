package theking530.staticpower.cables.scaffold;

import net.minecraft.util.ResourceLocation;
import theking530.staticpower.cables.network.AbstractCableNetworkModule;
import theking530.staticpower.cables.network.ICableNetworkModuleFactory;

public class ScaffoldNetworkModuleFactory implements ICableNetworkModuleFactory {

	@Override
	public AbstractCableNetworkModule create(ResourceLocation moduleName) {
		return new ScaffoldNetworkModule();
	}
}
