package theking530.staticpower.integration.TOP;

import net.minecraftforge.fml.InterModComms;
import theking530.staticpower.events.StaticPowerModEventRegistry;
import theking530.staticpower.integration.TOP.StaticPowerTOPHandler.TOPHandler;

public class PluginTOP {

	private static boolean registered;

	public static void sendIMC() {
		if (registered) {
			return;
		}
		registered = true;
		InterModComms.sendTo(StaticPowerModEventRegistry.TOP_MODID, "getTheOneProbe", TOPHandler::new);
	}
}