package theking530.staticcore;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@Mod(StaticCore.MOD_ID)
@Mod.EventBusSubscriber(modid = StaticCore.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class StaticCore {
	public static final Logger LOGGER = LogManager.getLogger("Static Core");
	public static final String MOD_ID = "staticcore";

}
