package theking530.staticpower.cables.digistore;

import net.minecraft.resources.ResourceLocation;
import theking530.staticpower.cables.network.AbstractCableNetworkModule;
import theking530.staticpower.cables.network.ICableNetworkModuleFactory;

public class DigistoreNetworkModuleFactory implements ICableNetworkModuleFactory {

	@Override
	public AbstractCableNetworkModule create(ResourceLocation moduleName) {
		return new DigistoreNetworkModule();
	}
}
