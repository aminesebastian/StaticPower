package theking530.staticpower.cables.redstone.basic;

import theking530.staticcore.cablenetwork.modules.CableNetworkModule;
import theking530.staticcore.cablenetwork.modules.CableNetworkModuleType;

public class RedstoneNetworkModuleFactory extends CableNetworkModuleType {

	@Override
	public CableNetworkModule create() {
		return new RedstoneNetworkModule(this);
	}
}
