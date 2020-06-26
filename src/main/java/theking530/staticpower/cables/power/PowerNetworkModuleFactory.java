package theking530.staticpower.cables.power;

import theking530.staticpower.cables.network.AbstractCableNetworkModule;
import theking530.staticpower.cables.network.ICableNetworkModuleFactory;

public class PowerNetworkModuleFactory implements ICableNetworkModuleFactory {

	@Override
	public AbstractCableNetworkModule create() {
		return new PowerNetworkModule();
	}
}
