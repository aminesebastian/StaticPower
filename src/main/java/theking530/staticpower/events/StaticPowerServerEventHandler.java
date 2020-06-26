package theking530.staticpower.events;

import net.minecraftforge.event.TickEvent;
import theking530.staticpower.cables.network.CableNetworkManager;

public class StaticPowerServerEventHandler {
	public static void onWorldTickEvent(TickEvent.WorldTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			CableNetworkManager.get(event.world).tick();
		}
	}
}
