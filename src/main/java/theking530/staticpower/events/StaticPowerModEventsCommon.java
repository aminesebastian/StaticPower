package theking530.staticpower.events;

import javax.annotation.Nonnull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.world.level.block.ComposterBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;
import theking530.api.attributes.capability.CapabilityAttributable;
import theking530.api.digistore.CapabilityDigistoreInventory;
import theking530.api.energy.CapabilityStaticPower;
import theking530.api.heat.CapabilityHeatable;
import theking530.staticcore.cablenetwork.capabilities.ServerCableCapabilityType;
import theking530.staticcore.cablenetwork.destinations.CableDestination;
import theking530.staticcore.cablenetwork.modules.CableNetworkModuleType;
import theking530.staticcore.data.StaticPowerGameDataManager;
import theking530.staticcore.initialization.StaticCoreRegistry;
import theking530.staticcore.productivity.product.ProductType;
import theking530.staticpower.StaticPower;
import theking530.staticpower.StaticPowerRegistries;
import theking530.staticpower.blockentities.machines.cropfarmer.BlockEntityBasicFarmer;
import theking530.staticpower.blockentities.machines.cropfarmer.harvesters.CactusCropHarvester;
import theking530.staticpower.blockentities.machines.cropfarmer.harvesters.GenericCropHarvester;
import theking530.staticpower.blockentities.machines.cropfarmer.harvesters.NetherWartCropHarvester;
import theking530.staticpower.blockentities.machines.cropfarmer.harvesters.StemCropHarvester;
import theking530.staticpower.blockentities.machines.cropfarmer.harvesters.SugarCaneCropHarvester;
import theking530.staticpower.client.StaticPowerAdditionalModels;
import theking530.staticpower.entities.player.datacapability.CapabilityStaticPowerPlayerData;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModEntities;
import theking530.staticpower.init.ModKeyBindings;
import theking530.staticpower.teams.TeamManager;

@Mod.EventBusSubscriber(modid = StaticPower.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class StaticPowerModEventsCommon {
	public static final String TOP_MODID = "theoneprobe";
	public static final String JEI_MODID = "jei";

	public static final Logger LOGGER = LogManager.getLogger(StaticPowerModEventsCommon.class);

	@SubscribeEvent
	public static void commonSetupEvent(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			// Register farming harvesters.
			BlockEntityBasicFarmer.registerHarvester(new GenericCropHarvester());
			BlockEntityBasicFarmer.registerHarvester(new SugarCaneCropHarvester());
			BlockEntityBasicFarmer.registerHarvester(new CactusCropHarvester());
			BlockEntityBasicFarmer.registerHarvester(new NetherWartCropHarvester());
			BlockEntityBasicFarmer.registerHarvester(new StemCropHarvester());

			// Register composter recipes.
			event.enqueueWork(() -> {
				ComposterBlock.COMPOSTABLES.put(ModBlocks.RubberTreeLeaves.get().asItem(), 0.6f);
			});

			// Register data classes.
			StaticPowerGameDataManager.registerDataFactory(TeamManager.ID, () -> {
				return new TeamManager();
			});

			ModEntities.registerPlacements(event);

			LOGGER.info("Static Power Common Setup Completed!");
		});
	}

	@SubscribeEvent
	public static void registerCustomRegistries(@Nonnull NewRegistryEvent event) {
		event.create(new RegistryBuilder<CableDestination>().setName(StaticPowerRegistries.CABLE_DESTINATION_REGISTRY).setIDRange(0, Integer.MAX_VALUE - 1));
		event.create(new RegistryBuilder<CableNetworkModuleType>().setName(StaticPowerRegistries.CABLE_MODULE_REGISTRY).setIDRange(0, Integer.MAX_VALUE - 1));
		event.create(new RegistryBuilder<ServerCableCapabilityType>().setName(StaticPowerRegistries.CABLE_CAPABILITY_REGISTRY).setIDRange(0, Integer.MAX_VALUE - 1));
		event.create(new RegistryBuilder<ProductType<?>>().setName(StaticPowerRegistries.PRODUCT_REGISTRY).setIDRange(0, Integer.MAX_VALUE - 1));
	}

	@SubscribeEvent
	public static void capabilityRegisterEvent(RegisterCapabilitiesEvent event) {
		// Register capabilities.
		CapabilityDigistoreInventory.register(event);
		CapabilityHeatable.register(event);
		CapabilityAttributable.register(event);
		CapabilityStaticPowerPlayerData.register(event);
		CapabilityStaticPower.register(event);
	}

	@SubscribeEvent
	public static void enqueueIMC(InterModEnqueueEvent event) {
		LOGGER.info("Static Power IMC Messages Enqueued!");
	}

	@SubscribeEvent
	public static void registerKeyBindings(RegisterKeyMappingsEvent event) {
		ModKeyBindings.registerBindings(event);
		LOGGER.info("Static Power registered key bindings!");
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void clientSetupEvent(FMLClientSetupEvent event) {
		StaticPowerForgeEventsProxy.onClientSetupEvent(event);
		LOGGER.info("Static Power Client Setup Completed!");
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void modelRegistryEvent(ModelEvent.RegisterAdditional event) {
		// Register any additional models we want.
		LOGGER.info("Registering Additional Models!");
		StaticPowerAdditionalModels.registerModels(event);
		LOGGER.info(String.format("Registered: %1$d Additional Models!", StaticPowerAdditionalModels.MODELS.size()));
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void modelBakeEvent(ModelEvent.BakingCompleted event) {
		StaticPowerForgeEventsProxy.onModelBakeEvent(event);
		LOGGER.info("Static Power Model Overrides Completed!");
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void onItemColorBakeEvent(RegisterColorHandlersEvent.Item event) {
		StaticPowerForgeEventsProxy.onItemColorBakeEvent(event);
		LOGGER.info("Static Power Item Color Overrides Completed!");
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void textureStitchEvent(TextureStitchEvent.Pre event) {
		StaticPowerForgeEventsProxy.onTextureStitchEvent(event);
		LOGGER.info("Static Power Model Texture Stitch Event Completed!");
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void registerEntityRenders(EntityRenderersEvent.RegisterRenderers event) throws Exception {
		LOGGER.info("Registering Entity Renderers!");
		ModEntities.registerEntityRenders(event);

		// Register the tile entity special renderers.
		LOGGER.info("Registering Tile Entity Special Renderers!");
		StaticCoreRegistry.registerBlockEntityRenderers(event);
	}

	@SubscribeEvent
	public static void onAttributeCreate(EntityAttributeCreationEvent event) {
		ModEntities.registerEntityAttributes(event);
	}
}
