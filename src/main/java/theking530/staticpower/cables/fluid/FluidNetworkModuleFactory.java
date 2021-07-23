package theking530.staticpower.cables.fluid;

import net.minecraft.util.ResourceLocation;
import theking530.staticpower.cables.network.AbstractCableNetworkModule;
import theking530.staticpower.cables.network.ICableNetworkModuleFactory;

public class FluidNetworkModuleFactory implements ICableNetworkModuleFactory {

	@Override
	public AbstractCableNetworkModule create(ResourceLocation moduleName) {
		return new FluidNetworkModule();
	}
}
