package theking530.staticpower.cables.power.wire;

import net.minecraft.resources.ResourceLocation;
import theking530.staticpower.cables.network.modules.CableNetworkModule;
import theking530.staticpower.cables.network.modules.ICableNetworkModuleFactory;

public class PowerWireNetworkModuleFactory implements ICableNetworkModuleFactory {

	@Override
	public CableNetworkModule create(ResourceLocation moduleName) {
		return new PowerWireNetworkModule();
	}
}
