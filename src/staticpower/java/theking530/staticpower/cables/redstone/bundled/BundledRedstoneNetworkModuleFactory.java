package theking530.staticpower.cables.redstone.bundled;

import theking530.staticcore.cablenetwork.modules.CableNetworkModule;
import theking530.staticcore.cablenetwork.modules.CableNetworkModuleType;

public class BundledRedstoneNetworkModuleFactory extends CableNetworkModuleType {

	@Override
	public CableNetworkModule create() {
		return new BundledRedstoneNetworkModule();
	}
}
