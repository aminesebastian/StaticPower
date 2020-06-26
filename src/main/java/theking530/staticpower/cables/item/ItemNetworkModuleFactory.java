package theking530.staticpower.cables.item;

import theking530.staticpower.cables.network.AbstractCableNetworkModule;
import theking530.staticpower.cables.network.ICableNetworkModuleFactory;

public class ItemNetworkModuleFactory implements ICableNetworkModuleFactory {

	@Override
	public AbstractCableNetworkModule create() {
		return new ItemNetworkModule();
	}
}
