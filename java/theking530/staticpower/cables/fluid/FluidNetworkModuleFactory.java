package theking530.staticpower.cables.fluid;

import net.minecraft.resources.ResourceLocation;
import theking530.staticpower.cables.network.modules.CableNetworkModule;
import theking530.staticpower.cables.network.modules.ICableNetworkModuleFactory;

public class FluidNetworkModuleFactory implements ICableNetworkModuleFactory {

	@Override
	public CableNetworkModule create(ResourceLocation moduleName) {
		return new FluidNetworkModule();
	}
}
