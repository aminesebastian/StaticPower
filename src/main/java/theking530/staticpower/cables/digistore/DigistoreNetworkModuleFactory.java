package theking530.staticpower.cables.digistore;

import theking530.staticpower.cables.network.AbstractCableNetworkModule;
import theking530.staticpower.cables.network.ICableNetworkModuleFactory;

public class DigistoreNetworkModuleFactory implements ICableNetworkModuleFactory {

	@Override
	public AbstractCableNetworkModule create() {
		return new DigistoreNetworkModule();
	}
}
