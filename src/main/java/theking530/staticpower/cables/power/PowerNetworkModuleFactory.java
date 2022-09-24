package theking530.staticpower.cables.power;

import net.minecraft.resources.ResourceLocation;
import theking530.staticcore.cablenetwork.modules.CableNetworkModule;
import theking530.staticcore.cablenetwork.modules.ICableNetworkModuleFactory;

public class PowerNetworkModuleFactory implements ICableNetworkModuleFactory {

	@Override
	public CableNetworkModule create(ResourceLocation moduleName) {
		return new PowerNetworkModule();
	}
}
