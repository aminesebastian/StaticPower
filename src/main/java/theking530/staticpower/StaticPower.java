package theking530.staticpower;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.config.ModConfig;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModContainerTypes;
import theking530.staticpower.init.ModFluids;
import theking530.staticpower.init.ModItems;
import theking530.staticpower.init.ModNetworkMessages;
import theking530.staticpower.init.ModOres;
import theking530.staticpower.init.ModRecipeSerializers;
import theking530.staticpower.init.ModTileEntityTypes;
import theking530.staticpower.init.ModUpgrades;

@Mod(StaticPower.MOD_ID)
@Mod.EventBusSubscriber(modid = StaticPower.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class StaticPower {
	public static final String MOD_ID = "staticpower";
	public static final Logger LOGGER = LogManager.getLogger(StaticPower.MOD_ID);
	public static final ItemGroup CREATIVE_TAB = new StaticPowerItemGroup();

	public StaticPower() {
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, StaticPowerConfig.CLIENT_SPEC);
		ModRecipeSerializers.init();
		ModBlocks.init();
		ModItems.init();
		ModUpgrades.init();
		ModFluids.init();
		ModNetworkMessages.init();
		ModTileEntityTypes.init();
		ModContainerTypes.init();
		ModOres.init();
	}
}
