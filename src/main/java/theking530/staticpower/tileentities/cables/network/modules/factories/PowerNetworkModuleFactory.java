package theking530.staticpower.tileentities.cables.network.modules.factories;

import theking530.staticpower.tileentities.cables.network.modules.AbstractCableNetworkModule;
import theking530.staticpower.tileentities.cables.network.modules.PowerNetworkModule;

public class PowerNetworkModuleFactory implements ICableNetworkModuleFactory {

	@Override
	public AbstractCableNetworkModule create() {
		return new PowerNetworkModule();
	}
}
