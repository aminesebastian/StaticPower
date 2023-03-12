package theking530.staticpower;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import theking530.staticcore.initialization.StaticCoreRegistry;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.data.tiers.advanced.StaticPowerTierAdvanced;
import theking530.staticpower.data.tiers.aluminum.StaticPowerTierAluminum;
import theking530.staticpower.data.tiers.basic.StaticPowerTierBasic;
import theking530.staticpower.data.tiers.bronze.StaticPowerTierBronze;
import theking530.staticpower.data.tiers.copper.StaticPowerTierCopper;
import theking530.staticpower.data.tiers.creative.StaticPowerTierCreative;
import theking530.staticpower.data.tiers.diamond.StaticPowerTierDiamond;
import theking530.staticpower.data.tiers.emerald.StaticPowerTierEmerald;
import theking530.staticpower.data.tiers.energized.StaticPowerTierEnergized;
import theking530.staticpower.data.tiers.gold.StaticPowerTierGold;
import theking530.staticpower.data.tiers.iron.StaticPowerTierIron;
import theking530.staticpower.data.tiers.lumum.StaticPowerTierLumum;
import theking530.staticpower.data.tiers.ruby.StaticPowerTierRuby;
import theking530.staticpower.data.tiers.sapphire.StaticPowerTierSapphire;
import theking530.staticpower.data.tiers.silver.StaticPowerTierSilver;
import theking530.staticpower.data.tiers.statictier.StaticPowerTierStatic;
import theking530.staticpower.data.tiers.tin.StaticPowerTierTin;
import theking530.staticpower.data.tiers.tungsten.StaticPowerTierTungsten;
import theking530.staticpower.data.tiers.wood.StaticPowerTierWood;
import theking530.staticpower.data.tiers.zinc.StaticPowerTierZinc;
import theking530.staticpower.init.ModItemAttributes;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModEntities;
import theking530.staticpower.init.ModFeatures;
import theking530.staticpower.init.ModFluids;
import theking530.staticpower.init.ModItems;
import theking530.staticpower.init.ModLootSerializers;
import theking530.staticpower.init.ModMaterials;
import theking530.staticpower.init.ModNetworkMessages;
import theking530.staticpower.init.ModProducts;
import theking530.staticpower.init.ModRecipeSerializers;
import theking530.staticpower.init.ModRecipeTypes;
import theking530.staticpower.init.cables.ModCableCapabilities;
import theking530.staticpower.init.cables.ModCableDestinations;
import theking530.staticpower.init.cables.ModCableModules;
import theking530.staticpower.integration.ModIntegrations;

@Mod(StaticPower.MOD_ID)
@Mod.EventBusSubscriber(modid = StaticPower.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class StaticPower {
	public static final Logger LOGGER = LogManager.getLogger("Static Power");
	public static final String MOD_ID = "staticpower";

	public StaticPower() {
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

		StaticCoreRegistry.preInitialize(eventBus);
		ModIntegrations.preInit(eventBus);

		ModItemAttributes.init(eventBus);

		ModMaterials.init();
		ModRecipeTypes.init(eventBus);
		ModRecipeSerializers.init(eventBus);
		ModBlocks.init(eventBus);
		ModItems.init(eventBus);
		ModFluids.init(eventBus);
		ModNetworkMessages.init();
		ModEntities.init(eventBus);

		ModCableDestinations.init(eventBus);
		ModCableModules.init(eventBus);
		ModCableCapabilities.init(eventBus);
		ModLootSerializers.init(eventBus);
		ModFeatures.init(eventBus);
		ModIntegrations.init(eventBus);

		StaticCoreRegistry.postInitialize();
		StaticCoreRegistry.registerBlockEntityTypes(eventBus);
		StaticCoreRegistry.registerContainerTypes(eventBus);

		ModProducts.init(eventBus);
		ModIntegrations.postInit(eventBus);
	}
}
