package theking530.staticpower.cables.scaffold;

import theking530.staticpower.cables.network.AbstractCableNetworkModule;
import theking530.staticpower.cables.network.ICableNetworkModuleFactory;

public class ScaffoldNetworkModuleFactory implements ICableNetworkModuleFactory {

	@Override
	public AbstractCableNetworkModule create() {
		return new ScaffoldNetworkModule();
	}
}
