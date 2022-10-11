package theking530.staticpower.cables.item;

import theking530.staticcore.cablenetwork.modules.CableNetworkModule;
import theking530.staticcore.cablenetwork.modules.CableNetworkModuleType;

public class ItemNetworkModuleFactory extends CableNetworkModuleType {

	@Override
	public CableNetworkModule create() {
		return new ItemNetworkModule();
	}
}
