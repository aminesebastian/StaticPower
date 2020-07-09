package theking530.staticpower.events;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import theking530.staticpower.StaticPower;
import theking530.staticpower.cables.digistore.DigistoreNetworkModuleFactory;
import theking530.staticpower.cables.fluid.FluidNetworkModuleFactory;
import theking530.staticpower.cables.item.ItemNetworkModuleFactory;
import theking530.staticpower.cables.network.CableNetworkModuleRegistry;
import theking530.staticpower.cables.network.CableNetworkModuleTypes;
import theking530.staticpower.cables.power.PowerNetworkModuleFactory;
import theking530.staticpower.tileentities.nonpowered.digistorenetwork.CapabilityDigistoreInventory;

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
		CableNetworkModuleRegistry.get().registerCableNetworkAttachmentFactory(CableNetworkModuleTypes.DIGISTORE_NETWORK_MODULE, new DigistoreNetworkModuleFactory());
		CapabilityDigistoreInventory.register();
		StaticPower.LOGGER.info("Static Power Common Setup Completed!");
	}
}
