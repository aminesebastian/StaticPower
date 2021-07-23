package theking530.staticpower.cables.item;

import net.minecraft.util.ResourceLocation;
import theking530.staticpower.cables.network.AbstractCableNetworkModule;
import theking530.staticpower.cables.network.ICableNetworkModuleFactory;

public class ItemNetworkModuleFactory implements ICableNetworkModuleFactory {

	@Override
	public AbstractCableNetworkModule create(ResourceLocation moduleName) {
		return new ItemNetworkModule();
	}
}
