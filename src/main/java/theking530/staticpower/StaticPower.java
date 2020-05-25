package theking530.staticpower;

import java.util.logging.LogManager;
import java.util.logging.Logger;

import net.minecraft.item.ItemGroup;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import theking530.staticpower.initialization.ModItems;
import theking530.staticpower.utilities.Reference;

@Mod(Reference.MOD_ID)
public class StaticPower {
	public static final Logger LOGGER = LogManager.getLogManager().getLogger(Reference.MOD_ID);
	public static final Registry REGISTRY  = new Registry();
	public static final ItemGroup CREATIVE_TAB = new StaticPowerItemGroup();
	
	public StaticPower() {
		ModItems.init(REGISTRY);
	}
	@SubscribeEvent
	public void setup(FMLCommonSetupEvent  Event) {
		LOGGER.info("Performing Status Power Common Setup");
	}
	@SubscribeEvent
	public void clientSetup(FMLClientSetupEvent  Event) {
		LOGGER.info("Performing Status Power Client Side Setup");
	}
}
