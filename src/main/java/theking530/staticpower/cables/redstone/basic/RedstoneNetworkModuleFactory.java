package theking530.staticpower.cables.redstone.basic;

import net.minecraft.util.ResourceLocation;
import theking530.staticpower.cables.network.AbstractCableNetworkModule;
import theking530.staticpower.cables.network.ICableNetworkModuleFactory;

public class RedstoneNetworkModuleFactory implements ICableNetworkModuleFactory {

	@Override
	public AbstractCableNetworkModule create(ResourceLocation moduleName) {
		return new RedstoneNetworkModule(moduleName);
	}
}
