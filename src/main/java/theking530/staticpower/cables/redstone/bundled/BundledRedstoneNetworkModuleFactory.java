package theking530.staticpower.cables.redstone.bundled;

import net.minecraft.resources.ResourceLocation;
import theking530.staticpower.cables.network.AbstractCableNetworkModule;
import theking530.staticpower.cables.network.ICableNetworkModuleFactory;

public class BundledRedstoneNetworkModuleFactory implements ICableNetworkModuleFactory {

	@Override
	public AbstractCableNetworkModule create(ResourceLocation moduleName) {
		return new BundledRedstoneNetworkModule();
	}
}
