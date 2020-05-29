package theking530.staticpower.initialization;

import theking530.staticpower.network.NetworkMessage;
import theking530.staticpower.network.StaticPowerMessageHandler;

public class ModNetworkMessages {
	public static void init() {
		StaticPowerMessageHandler.registerMessage(NetworkMessage.class);
	}
}
