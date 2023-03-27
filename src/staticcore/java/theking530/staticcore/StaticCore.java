package theking530.staticcore;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import theking530.staticcore.attributes.AttributeModifiers;
import theking530.staticcore.attributes.AttributeValues;
import theking530.staticcore.data.StaticCoreTiers;
import theking530.staticcore.data.tiers.advanced.StaticCoreTierAdvanced;
import theking530.staticcore.data.tiers.aluminum.StaticCoreTierAluminum;
import theking530.staticcore.data.tiers.basic.StaticCoreTierBasic;
import theking530.staticcore.data.tiers.bronze.StaticCoreTierBronze;
import theking530.staticcore.data.tiers.copper.StaticCoreTierCopper;
import theking530.staticcore.data.tiers.creative.StaticCoreTierCreative;
import theking530.staticcore.data.tiers.diamond.StaticCoreTierDiamond;
import theking530.staticcore.data.tiers.emerald.StaticCoreTierEmerald;
import theking530.staticcore.data.tiers.energized.StaticCoreTierEnergized;
import theking530.staticcore.data.tiers.gold.StaticCoreTierGold;
import theking530.staticcore.data.tiers.iron.StaticCoreTierIron;
import theking530.staticcore.data.tiers.lumum.StaticCoreTierLumum;
import theking530.staticcore.data.tiers.ruby.StaticCoreTierRuby;
import theking530.staticcore.data.tiers.sapphire.StaticCoreTierSapphire;
import theking530.staticcore.data.tiers.silver.StaticCoreTierSilver;
import theking530.staticcore.data.tiers.statictier.StaticCoreTierStatic;
import theking530.staticcore.data.tiers.tin.StaticCoreTierTin;
import theking530.staticcore.data.tiers.tungsten.StaticCoreTierTungsten;
import theking530.staticcore.data.tiers.wood.StaticCoreTierWood;
import theking530.staticcore.data.tiers.zinc.StaticCoreTierZinc;
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

		StaticCoreConfig.registerTier(StaticCore.MOD_ID, StaticCoreTiers.WOOD, StaticCoreTierWood::new);
		StaticCoreConfig.registerTier(StaticCore.MOD_ID, StaticCoreTiers.BASIC, StaticCoreTierBasic::new);
		StaticCoreConfig.registerTier(StaticCore.MOD_ID, StaticCoreTiers.ADVANCED, StaticCoreTierAdvanced::new);
		StaticCoreConfig.registerTier(StaticCore.MOD_ID, StaticCoreTiers.STATIC, StaticCoreTierStatic::new);
		StaticCoreConfig.registerTier(StaticCore.MOD_ID, StaticCoreTiers.ENERGIZED, StaticCoreTierEnergized::new);
		StaticCoreConfig.registerTier(StaticCore.MOD_ID, StaticCoreTiers.LUMUM, StaticCoreTierLumum::new);
		StaticCoreConfig.registerTier(StaticCore.MOD_ID, StaticCoreTiers.CREATIVE, StaticCoreTierCreative::new);

		StaticCoreConfig.registerTier(StaticCore.MOD_ID, StaticCoreTiers.ALUMINUM, StaticCoreTierAluminum::new);
		StaticCoreConfig.registerTier(StaticCore.MOD_ID, StaticCoreTiers.ZINC, StaticCoreTierZinc::new);
		StaticCoreConfig.registerTier(StaticCore.MOD_ID, StaticCoreTiers.BRONZE, StaticCoreTierBronze::new);
		StaticCoreConfig.registerTier(StaticCore.MOD_ID, StaticCoreTiers.COPPER, StaticCoreTierCopper::new);
		StaticCoreConfig.registerTier(StaticCore.MOD_ID, StaticCoreTiers.GOLD, StaticCoreTierGold::new);
		StaticCoreConfig.registerTier(StaticCore.MOD_ID, StaticCoreTiers.IRON, StaticCoreTierIron::new);
		StaticCoreConfig.registerTier(StaticCore.MOD_ID, StaticCoreTiers.SILVER, StaticCoreTierSilver::new);
		StaticCoreConfig.registerTier(StaticCore.MOD_ID, StaticCoreTiers.TIN, StaticCoreTierTin::new);
		StaticCoreConfig.registerTier(StaticCore.MOD_ID, StaticCoreTiers.TUNGSTEN, StaticCoreTierTungsten::new);

		StaticCoreConfig.registerTier(StaticCore.MOD_ID, StaticCoreTiers.DIAMOND, StaticCoreTierDiamond::new);
		StaticCoreConfig.registerTier(StaticCore.MOD_ID, StaticCoreTiers.RUBY, StaticCoreTierRuby::new);
		StaticCoreConfig.registerTier(StaticCore.MOD_ID, StaticCoreTiers.SAPPHIRE, StaticCoreTierSapphire::new);
		StaticCoreConfig.registerTier(StaticCore.MOD_ID, StaticCoreTiers.EMERALD, StaticCoreTierEmerald::new);

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
