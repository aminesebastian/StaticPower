package theking530.staticpower;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import theking530.staticcore.initialization.StaticCoreRegistry;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.data.tiers.StaticPowerTierAdvanced;
import theking530.staticpower.data.tiers.StaticPowerTierAluminum;
import theking530.staticpower.data.tiers.StaticPowerTierBasic;
import theking530.staticpower.data.tiers.StaticPowerTierBronze;
import theking530.staticpower.data.tiers.StaticPowerTierCopper;
import theking530.staticpower.data.tiers.StaticPowerTierCreative;
import theking530.staticpower.data.tiers.StaticPowerTierDiamond;
import theking530.staticpower.data.tiers.StaticPowerTierEmerald;
import theking530.staticpower.data.tiers.StaticPowerTierEnergized;
import theking530.staticpower.data.tiers.StaticPowerTierGold;
import theking530.staticpower.data.tiers.StaticPowerTierIron;
import theking530.staticpower.data.tiers.StaticPowerTierLumum;
import theking530.staticpower.data.tiers.StaticPowerTierRuby;
import theking530.staticpower.data.tiers.StaticPowerTierSapphire;
import theking530.staticpower.data.tiers.StaticPowerTierSilver;
import theking530.staticpower.data.tiers.StaticPowerTierStatic;
import theking530.staticpower.data.tiers.StaticPowerTierTin;
import theking530.staticpower.data.tiers.StaticPowerTierTungsten;
import theking530.staticpower.data.tiers.StaticPowerTierWood;
import theking530.staticpower.data.tiers.StaticPowerTierZinc;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModEntities;
import theking530.staticpower.init.ModFluids;
import theking530.staticpower.init.ModItems;
import theking530.staticpower.init.ModNetworkMessages;
import theking530.staticpower.init.ModRecipeSerializers;
import theking530.staticpower.world.trees.ModTrees;

@Mod(StaticPower.MOD_ID)
@Mod.EventBusSubscriber(modid = StaticPower.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class StaticPower {
	public static final Logger LOGGER = LogManager.getLogger("Static Power");
	public static final String MOD_ID = "staticpower";
	public static final CreativeModeTab CREATIVE_TAB = new StaticPowerItemGroup();

	public StaticPower() {
		try {
			IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
				
			// Enable forge's milk.
			ForgeMod.enableMilkFluid();
			
			StaticPowerConfig.preInitialize();

			StaticPowerConfig.registerTier(StaticPowerTiers.WOOD, StaticPowerTierWood::new);
			StaticPowerConfig.registerTier(StaticPowerTiers.BASIC, StaticPowerTierBasic::new);
			StaticPowerConfig.registerTier(StaticPowerTiers.ADVANCED, StaticPowerTierAdvanced::new);
			StaticPowerConfig.registerTier(StaticPowerTiers.STATIC, StaticPowerTierStatic::new);
			StaticPowerConfig.registerTier(StaticPowerTiers.ENERGIZED, StaticPowerTierEnergized::new);
			StaticPowerConfig.registerTier(StaticPowerTiers.LUMUM, StaticPowerTierLumum::new);
			StaticPowerConfig.registerTier(StaticPowerTiers.CREATIVE, StaticPowerTierCreative::new);

			StaticPowerConfig.registerTier(StaticPowerTiers.ALUMINUM, StaticPowerTierAluminum::new);
			StaticPowerConfig.registerTier(StaticPowerTiers.ZINC, StaticPowerTierZinc::new);
			StaticPowerConfig.registerTier(StaticPowerTiers.BRONZE, StaticPowerTierBronze::new);
			StaticPowerConfig.registerTier(StaticPowerTiers.COPPER, StaticPowerTierCopper::new);
			StaticPowerConfig.registerTier(StaticPowerTiers.GOLD, StaticPowerTierGold::new);
			StaticPowerConfig.registerTier(StaticPowerTiers.IRON, StaticPowerTierIron::new);
			StaticPowerConfig.registerTier(StaticPowerTiers.SILVER, StaticPowerTierSilver::new);
			StaticPowerConfig.registerTier(StaticPowerTiers.TIN, StaticPowerTierTin::new);
			StaticPowerConfig.registerTier(StaticPowerTiers.TUNGSTEN, StaticPowerTierTungsten::new);

			StaticPowerConfig.registerTier(StaticPowerTiers.DIAMOND, StaticPowerTierDiamond::new);
			StaticPowerConfig.registerTier(StaticPowerTiers.RUBY, StaticPowerTierRuby::new);
			StaticPowerConfig.registerTier(StaticPowerTiers.SAPPHIRE, StaticPowerTierSapphire::new);
			StaticPowerConfig.registerTier(StaticPowerTiers.EMERALD, StaticPowerTierEmerald::new);

			StaticCoreRegistry.preInitialize();
			ModRecipeSerializers.init();
			ModBlocks.init(eventBus);
			ModItems.init(eventBus);
			ModFluids.init(eventBus);
			ModNetworkMessages.init();
			ModTrees.init();
			ModEntities.init();
			StaticCoreRegistry.postInitialize();
//			
//			MinecraftForge.EVENT_BUS.register(this);
		} catch (Exception e) {
			LOGGER.error("An error occured during Static Power initialization.", e);
		}
	}
}
