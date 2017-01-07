package theking530.staticpower.client;

import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import theking530.staticpower.StaticPower;
import theking530.staticpower.handlers.ModEvents;

public class CommonProxy  {
	
	public static void preInit() {
	    ForgeChunkManager.setForcedChunkLoadingCallback(StaticPower.instance, null);
		ModEvents.init();
	}
	public void registerProxies() {
		
	}
}
