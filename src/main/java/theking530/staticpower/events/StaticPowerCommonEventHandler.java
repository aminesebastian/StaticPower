package theking530.staticpower.events;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import theking530.staticpower.StaticPower;
import theking530.staticpower.cables.network.factories.cables.BasicPowerCableFactory;
import theking530.staticpower.cables.network.factories.cables.CableTypes;
import theking530.staticpower.cables.network.factories.cables.CableWrapperRegistry;
import theking530.staticpower.cables.network.factories.modules.CableNetworkModuleRegistry;
import theking530.staticpower.cables.network.factories.modules.CableNetworkModuleTypes;
import theking530.staticpower.cables.network.factories.modules.PowerNetworkModuleFactory;

public class StaticPowerCommonEventHandler {

	/**
	 * This event is raised by the common setup event.
	 * 
	 * @param event The common setup event.
	 */
	public static void onCommonSetupEvent(FMLCommonSetupEvent event) {
		CableWrapperRegistry.get().registerCableWrapperFactory(CableTypes.BASIC_POWER, new BasicPowerCableFactory());
		CableWrapperRegistry.get().registerCableWrapperFactory(CableTypes.BASIC_ITEM, new BasicPowerCableFactory());
		CableNetworkModuleRegistry.get().registerCableNetworkAttachmentFactory(CableNetworkModuleTypes.POWER_NETWORK_ATTACHMENT, new PowerNetworkModuleFactory());

		StaticPower.LOGGER.info("Static Power Common Setup Completed!");
	}
}
