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
import theking530.staticcore.initialization.StaticCoreRegistrationHelper;
import theking530.staticpower.init.ModAttributes;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModEntities;
import theking530.staticpower.init.ModFeatures;
import theking530.staticpower.init.ModFluids;
import theking530.staticpower.init.ModItemAttributes;
import theking530.staticpower.init.ModItemSlots;
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
	public static final StaticCoreRegistrationHelper REGISTRATION = new StaticCoreRegistrationHelper( MOD_ID);

	private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES,
			StaticPower.MOD_ID);
	private static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, StaticPower.MOD_ID);

	public StaticPower() {
		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

		// Enable forge's milk.
		ForgeMod.enableMilkFluid();

		StaticPowerConfig.preInitialize();

		StaticCoreConfig.registerTier(StaticPower.MOD_ID, StaticCoreTiers.WOOD, StaticCoreTierWood::new);
		StaticCoreConfig.registerTier(StaticPower.MOD_ID, StaticCoreTiers.BASIC, StaticCoreTierBasic::new);
		StaticCoreConfig.registerTier(StaticPower.MOD_ID, StaticCoreTiers.ADVANCED, StaticCoreTierAdvanced::new);
		StaticCoreConfig.registerTier(StaticPower.MOD_ID, StaticCoreTiers.STATIC, StaticCoreTierStatic::new);
		StaticCoreConfig.registerTier(StaticPower.MOD_ID, StaticCoreTiers.ENERGIZED, StaticCoreTierEnergized::new);
		StaticCoreConfig.registerTier(StaticPower.MOD_ID, StaticCoreTiers.LUMUM, StaticCoreTierLumum::new);
		StaticCoreConfig.registerTier(StaticPower.MOD_ID, StaticCoreTiers.CREATIVE, StaticCoreTierCreative::new);

		StaticCoreConfig.registerTier(StaticPower.MOD_ID, StaticCoreTiers.ALUMINUM, StaticCoreTierAluminum::new);
		StaticCoreConfig.registerTier(StaticPower.MOD_ID, StaticCoreTiers.ZINC, StaticCoreTierZinc::new);
		StaticCoreConfig.registerTier(StaticPower.MOD_ID, StaticCoreTiers.BRONZE, StaticCoreTierBronze::new);
		StaticCoreConfig.registerTier(StaticPower.MOD_ID, StaticCoreTiers.COPPER, StaticCoreTierCopper::new);
		StaticCoreConfig.registerTier(StaticPower.MOD_ID, StaticCoreTiers.GOLD, StaticCoreTierGold::new);
		StaticCoreConfig.registerTier(StaticPower.MOD_ID, StaticCoreTiers.IRON, StaticCoreTierIron::new);
		StaticCoreConfig.registerTier(StaticPower.MOD_ID, StaticCoreTiers.SILVER, StaticCoreTierSilver::new);
		StaticCoreConfig.registerTier(StaticPower.MOD_ID, StaticCoreTiers.TIN, StaticCoreTierTin::new);
		StaticCoreConfig.registerTier(StaticPower.MOD_ID, StaticCoreTiers.TUNGSTEN, StaticCoreTierTungsten::new);

		StaticCoreConfig.registerTier(StaticPower.MOD_ID, StaticCoreTiers.DIAMOND, StaticCoreTierDiamond::new);
		StaticCoreConfig.registerTier(StaticPower.MOD_ID, StaticCoreTiers.RUBY, StaticCoreTierRuby::new);
		StaticCoreConfig.registerTier(StaticPower.MOD_ID, StaticCoreTiers.SAPPHIRE, StaticCoreTierSapphire::new);
		StaticCoreConfig.registerTier(StaticPower.MOD_ID, StaticCoreTiers.EMERALD, StaticCoreTierEmerald::new);

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
		ModItemSlots.init(eventBus);

		ModCableDestinations.init(eventBus);
		ModCableModules.init(eventBus);
		ModCableCapabilities.init(eventBus);
		ModLootSerializers.init(eventBus);
		ModFeatures.init(eventBus);
		ModIntegrations.init(eventBus);
		ModAttributes.init(eventBus);

		REGISTRATION.registerBlockEntityTypes(eventBus, BLOCK_ENTITY_TYPES);
		REGISTRATION.registerContainerTypes(eventBus, MENU_TYPES);

		ModProducts.init(eventBus);
		ModIntegrations.postInit(eventBus);
	}
}
