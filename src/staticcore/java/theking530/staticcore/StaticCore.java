package theking530.staticcore;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import theking530.staticcore.attributes.AttributeModifiers;
import theking530.staticcore.attributes.AttributeValues;
import theking530.staticcore.init.StaticCoreItems;
import theking530.staticcore.init.StaticCorePackets;
import theking530.staticcore.init.StaticCoreProductTypes;
import theking530.staticcore.init.StaticCoreRecipeSerializers;
import theking530.staticcore.init.StaticCoreRecipeTypes;
import theking530.staticcore.init.StaticCoreUpgradeTypes;

@Mod(StaticCore.MOD_ID)
@Mod.EventBusSubscriber(modid = StaticCore.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class StaticCore {
	public static final Logger LOGGER = LogManager.getLogger("Static Core");
	public static final String MOD_ID = "staticcore";

	public StaticCore() {
		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
		StaticCoreConfig.preInitialize();
		
		AttributeValues.init(eventBus);
		AttributeModifiers.init(eventBus);

		StaticCoreItems.init(eventBus);
		StaticCoreRecipeTypes.init(eventBus);
		StaticCoreRecipeSerializers.init(eventBus);
		StaticCoreProductTypes.init(eventBus);
		StaticCoreUpgradeTypes.init(eventBus);
		StaticCorePackets.init();
	}
}
