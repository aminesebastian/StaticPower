package theking530.staticpower.events;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import theking530.staticpower.StaticPower;
import theking530.staticpower.tileentities.cables.CableType;
import theking530.staticpower.tileentities.network.factories.BasicPowerCableFactory;
import theking530.staticpower.tileentities.network.factories.CableFactories;

public class StaticPowerCommonEventHandler {

	/**
	 * This event is raised by the common setup event.
	 * 
	 * @param event The common setup event.
	 */
	public static void onCommonSetupEvent(FMLCommonSetupEvent event) {
		CableFactories.get().registerCableWrapperFactory(CableType.BASIC_POWER, new BasicPowerCableFactory());

		StaticPower.LOGGER.info("Static Power Common Setup Completed!");
	}
}
