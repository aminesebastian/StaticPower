package theking530.staticpower.tileentities.cables.network.modules.factories;

import theking530.staticpower.tileentities.cables.network.modules.AbstractCableNetworkModule;
import theking530.staticpower.tileentities.cables.network.modules.FluidNetworkModule;

public class FluidNetworkModuleFactory implements ICableNetworkModuleFactory {

	@Override
	public AbstractCableNetworkModule create() {
		return new FluidNetworkModule();
	}
}
