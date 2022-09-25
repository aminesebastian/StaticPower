package theking530.staticpower.cables.digistore;

import theking530.staticcore.cablenetwork.modules.CableNetworkModule;
import theking530.staticcore.cablenetwork.modules.CableNetworkModuleType;

public class DigistoreNetworkModuleFactory extends CableNetworkModuleType {

	@Override
	public CableNetworkModule create() {
		return new DigistoreNetworkModule();
	}
}
