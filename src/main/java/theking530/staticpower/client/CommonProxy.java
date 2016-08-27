package theking530.staticpower.client;

import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import theking530.staticpower.StaticPower;

public class CommonProxy  {
	
	public static void preInit() {
	    ForgeChunkManager.setForcedChunkLoadingCallback(StaticPower.instance, null);
	}
	public void registerProxies() {
		
	}
}
