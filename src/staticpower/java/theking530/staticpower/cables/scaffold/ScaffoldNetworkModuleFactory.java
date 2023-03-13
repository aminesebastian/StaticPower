package theking530.staticpower.cables.scaffold;

import theking530.staticcore.cablenetwork.modules.CableNetworkModule;
import theking530.staticcore.cablenetwork.modules.CableNetworkModuleType;

public class ScaffoldNetworkModuleFactory extends CableNetworkModuleType {

	@Override
	public CableNetworkModule create() {
		return new ScaffoldNetworkModule();
	}
}
