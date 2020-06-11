package theking530.staticpower.tileentities.network.factories.modules;

import theking530.staticpower.tileentities.network.modules.AbstractCableNetworkModule;
import theking530.staticpower.tileentities.network.modules.PowerNetworkModule;

public class PowerNetworkModuleFactory implements ICableNetworkModuleFactory {

	@Override
	public AbstractCableNetworkModule create() {
		return new PowerNetworkModule();
	}
}
