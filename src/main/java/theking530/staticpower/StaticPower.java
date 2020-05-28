package theking530.staticpower;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.item.ItemGroup;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import theking530.staticpower.initialization.ModBlocks;
import theking530.staticpower.initialization.ModContainerTypes;
import theking530.staticpower.initialization.ModItems;
import theking530.staticpower.initialization.ModTileEntityTypes;
import theking530.staticpower.initialization.ModUpgrades;
import theking530.staticpower.utilities.Reference;

@Mod(Reference.MOD_ID)
@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class StaticPower {
	public static final Logger LOGGER = LogManager.getLogger(Reference.MOD_ID);
	public static final ItemGroup CREATIVE_TAB = new StaticPowerItemGroup();

	public StaticPower() {
		ModBlocks.init();
		ModItems.init();
		ModUpgrades.init();
		ModTileEntityTypes.init();
		ModContainerTypes.init();
	}

	@SubscribeEvent
	public static void setup(final FMLCommonSetupEvent event) {
		LOGGER.info("Performing Static Power Common Setup");
		Registry.onCommonSetupEvent(event);
	}

	@SubscribeEvent
	public static void clientSetup(final FMLClientSetupEvent event) {
		LOGGER.info("Performing Static Power Client Side Setup");
		Registry.onClientSetupEvent(event);
	}

	@SubscribeEvent
	public static void onLootTableLoad(LootTableLoadEvent evt) {

	}
}
