package theking530.staticpower.events;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import theking530.staticpower.StaticPower;
import theking530.staticpower.tileentities.cables.network.modules.factories.CableNetworkModuleRegistry;
import theking530.staticpower.tileentities.cables.network.modules.factories.CableNetworkModuleTypes;
import theking530.staticpower.tileentities.cables.network.modules.factories.FluidNetworkModuleFactory;
import theking530.staticpower.tileentities.cables.network.modules.factories.ItemNetworkModuleFactory;
import theking530.staticpower.tileentities.cables.network.modules.factories.PowerNetworkModuleFactory;

public class StaticPowerCommonEventHandler {

	/**
	 * This event is raised by the common setup event.
	 * 
	 * @param event The common setup event.
	 */
	public static void onCommonSetupEvent(FMLCommonSetupEvent event) {
		CableNetworkModuleRegistry.get().registerCableNetworkAttachmentFactory(CableNetworkModuleTypes.POWER_NETWORK_MODULE, new PowerNetworkModuleFactory());
		CableNetworkModuleRegistry.get().registerCableNetworkAttachmentFactory(CableNetworkModuleTypes.ITEM_NETWORK_MODULE, new ItemNetworkModuleFactory());
		CableNetworkModuleRegistry.get().registerCableNetworkAttachmentFactory(CableNetworkModuleTypes.FLUID_NETWORK_MODULE, new FluidNetworkModuleFactory());
		
		StaticPower.LOGGER.info("Static Power Common Setup Completed!");
	}
}
