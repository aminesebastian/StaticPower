package theking530.staticpower.cables.item;

import net.minecraft.resources.ResourceLocation;
import theking530.staticcore.cablenetwork.modules.CableNetworkModule;
import theking530.staticcore.cablenetwork.modules.ICableNetworkModuleFactory;

public class ItemNetworkModuleFactory implements ICableNetworkModuleFactory {

	@Override
	public CableNetworkModule create(ResourceLocation moduleName) {
		return new ItemNetworkModule();
	}
}
