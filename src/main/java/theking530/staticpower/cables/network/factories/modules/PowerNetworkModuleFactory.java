package theking530.staticpower.cables.network.factories.modules;

import theking530.staticpower.cables.network.modules.AbstractCableNetworkModule;
import theking530.staticpower.cables.network.modules.PowerNetworkModule;

public class PowerNetworkModuleFactory implements ICableNetworkModuleFactory {

	@Override
	public AbstractCableNetworkModule create() {
		return new PowerNetworkModule();
	}
}
