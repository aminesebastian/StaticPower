package theking530.staticpower;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import theking530.staticcore.StaticCoreConfig;
import theking530.staticcore.initialization.StaticCoreRegistrationHelpers;
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
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModEntities;
import theking530.staticpower.init.ModFeatures;
import theking530.staticpower.init.ModFluids;
import theking530.staticpower.init.ModItemAttributes;
import theking530.staticpower.init.ModItems;
import theking530.staticpower.init.ModLootSerializers;
import theking530.staticpower.init.ModMaterials;
import theking530.staticpower.init.ModNetworkMessages;
import theking530.staticpower.init.ModProducts;
import theking530.staticpower.init.ModRecipeSerializers;
import theking530.staticpower.init.ModRecipeTypes;
import theking530.staticpower.init.ModUpgradeTypes;
import theking530.staticpower.init.cables.ModCableCapabilities;
import theking530.staticpower.init.cables.ModCableDestinations;
import theking530.staticpower.init.cables.ModCableModules;
import theking530.staticpower.integration.ModIntegrations;

@Mod(StaticPower.MOD_ID)
@Mod.EventBusSubscriber(modid = StaticPower.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class StaticPower {
	public static final Logger LOGGER = LogManager.getLogger("Static Power");
	public static final String MOD_ID = "staticpower";

	private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, StaticPower.MOD_ID);
	private static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, StaticPower.MOD_ID);

	public StaticPower() {
		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

		// Enable forge's milk.
		ForgeMod.enableMilkFluid();

		StaticPowerConfig.preInitialize();

		StaticCoreConfig.registerTier(StaticPower.MOD_ID, StaticPowerTiers.WOOD, StaticPowerTierWood::new);
		StaticCoreConfig.registerTier(StaticPower.MOD_ID, StaticPowerTiers.BASIC, StaticPowerTierBasic::new);
		StaticCoreConfig.registerTier(StaticPower.MOD_ID, StaticPowerTiers.ADVANCED, StaticPowerTierAdvanced::new);
		StaticCoreConfig.registerTier(StaticPower.MOD_ID, StaticPowerTiers.STATIC, StaticPowerTierStatic::new);
		StaticCoreConfig.registerTier(StaticPower.MOD_ID, StaticPowerTiers.ENERGIZED, StaticPowerTierEnergized::new);
		StaticCoreConfig.registerTier(StaticPower.MOD_ID, StaticPowerTiers.LUMUM, StaticPowerTierLumum::new);
		StaticCoreConfig.registerTier(StaticPower.MOD_ID, StaticPowerTiers.CREATIVE, StaticPowerTierCreative::new);

		StaticCoreConfig.registerTier(StaticPower.MOD_ID, StaticPowerTiers.ALUMINUM, StaticPowerTierAluminum::new);
		StaticCoreConfig.registerTier(StaticPower.MOD_ID, StaticPowerTiers.ZINC, StaticPowerTierZinc::new);
		StaticCoreConfig.registerTier(StaticPower.MOD_ID, StaticPowerTiers.BRONZE, StaticPowerTierBronze::new);
		StaticCoreConfig.registerTier(StaticPower.MOD_ID, StaticPowerTiers.COPPER, StaticPowerTierCopper::new);
		StaticCoreConfig.registerTier(StaticPower.MOD_ID, StaticPowerTiers.GOLD, StaticPowerTierGold::new);
		StaticCoreConfig.registerTier(StaticPower.MOD_ID, StaticPowerTiers.IRON, StaticPowerTierIron::new);
		StaticCoreConfig.registerTier(StaticPower.MOD_ID, StaticPowerTiers.SILVER, StaticPowerTierSilver::new);
		StaticCoreConfig.registerTier(StaticPower.MOD_ID, StaticPowerTiers.TIN, StaticPowerTierTin::new);
		StaticCoreConfig.registerTier(StaticPower.MOD_ID, StaticPowerTiers.TUNGSTEN, StaticPowerTierTungsten::new);

		StaticCoreConfig.registerTier(StaticPower.MOD_ID, StaticPowerTiers.DIAMOND, StaticPowerTierDiamond::new);
		StaticCoreConfig.registerTier(StaticPower.MOD_ID, StaticPowerTiers.RUBY, StaticPowerTierRuby::new);
		StaticCoreConfig.registerTier(StaticPower.MOD_ID, StaticPowerTiers.SAPPHIRE, StaticPowerTierSapphire::new);
		StaticCoreConfig.registerTier(StaticPower.MOD_ID, StaticPowerTiers.EMERALD, StaticPowerTierEmerald::new);

		ModIntegrations.preInit(eventBus);

		ModItemAttributes.init(eventBus);
		ModUpgradeTypes.init(eventBus);
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

		StaticCoreRegistrationHelpers.registerBlockEntityTypes(eventBus, BLOCK_ENTITY_TYPES);
		StaticCoreRegistrationHelpers.registerContainerTypes(eventBus, MENU_TYPES);

		ModProducts.init(eventBus);
		ModIntegrations.postInit(eventBus);
	}
}
