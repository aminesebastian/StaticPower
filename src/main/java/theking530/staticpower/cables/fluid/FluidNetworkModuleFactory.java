package theking530.staticpower.cables.fluid;

import theking530.staticpower.cables.network.AbstractCableNetworkModule;
import theking530.staticpower.cables.network.ICableNetworkModuleFactory;

public class FluidNetworkModuleFactory implements ICableNetworkModuleFactory {

	@Override
	public AbstractCableNetworkModule create() {
		return new FluidNetworkModule();
	}
}
