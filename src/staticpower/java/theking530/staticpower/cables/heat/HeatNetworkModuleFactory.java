package theking530.staticpower.cables.heat;

import theking530.staticcore.cablenetwork.modules.CableNetworkModule;
import theking530.staticcore.cablenetwork.modules.CableNetworkModuleType;

public class HeatNetworkModuleFactory extends CableNetworkModuleType {

	@Override
	public CableNetworkModule create() {
		return new HeatNetworkModule();
	}
}
