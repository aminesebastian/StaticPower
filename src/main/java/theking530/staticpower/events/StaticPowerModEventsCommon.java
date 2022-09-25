package theking530.staticpower.events;

import javax.annotation.Nonnull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.RegistryEvent;
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
import theking530.staticcore.cablenetwork.destinations.CableDestination;
import theking530.staticcore.cablenetwork.modules.CableNetworkModuleType;
import theking530.staticcore.data.StaticPowerGameDataManager;
import theking530.staticcore.initialization.StaticCoreRegistry;
import theking530.staticpower.StaticPower;
import theking530.staticpower.StaticPowerRegistries;
import theking530.staticpower.blockentities.machines.cropfarmer.BlockEntityBasicFarmer;
import theking530.staticpower.blockentities.machines.cropfarmer.harvesters.CactusCropHarvester;
import theking530.staticpower.blockentities.machines.cropfarmer.harvesters.GenericCropHarvester;
import theking530.staticpower.blockentities.machines.cropfarmer.harvesters.NetherWartCropHarvester;
import theking530.staticpower.blockentities.machines.cropfarmer.harvesters.StemCropHarvester;
import theking530.staticpower.blockentities.machines.cropfarmer.harvesters.SugarCaneCropHarvester;
import theking530.staticpower.client.StaticPowerAdditionalModels;
import theking530.staticpower.data.loot.StaticPowerLootModifier;
import theking530.staticpower.entities.player.datacapability.CapabilityStaticPowerPlayerData;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModEntities;
import theking530.staticpower.init.ModRecipeSerializers;
import theking530.staticpower.teams.TeamManager;
import theking530.staticpower.world.ModConfiguredFeatures;

@Mod.EventBusSubscriber(modid = StaticPower.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class StaticPowerModEventsCommon {
	public static final String TOP_MODID = "theoneprobe";
	public static final String JEI_MODID = "jei";

	public static final Logger LOGGER = LogManager.getLogger(StaticPowerModEventsCommon.class);

	@SubscribeEvent
	public static void commonSetupEvent(FMLCommonSetupEvent event) {

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

		LOGGER.info("Static Power Common Setup Completed!");
	}

	@SubscribeEvent
	public static void registerCustomRegistries(@Nonnull NewRegistryEvent event) {
		event.create(new RegistryBuilder<CableDestination>().setName(StaticPowerRegistries.CABLE_DESTINATION_REGISTRY).setType(CableDestination.class).setIDRange(0,
				Integer.MAX_VALUE - 1));
		event.create(new RegistryBuilder<CableNetworkModuleType>().setName(StaticPowerRegistries.CABLE_MODULE_REGISTRY).setType(CableNetworkModuleType.class).setIDRange(0,
				Integer.MAX_VALUE - 1));
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
	@OnlyIn(Dist.CLIENT)
	public static void clientSetupEvent(FMLClientSetupEvent event) {
		StaticPowerForgeEventsProxy.onClientSetupEvent(event);
		LOGGER.info("Static Power Client Setup Completed!");
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void modelRegistryEvent(ModelRegistryEvent event) {
		// Register any additional models we want.
		LOGGER.info("Registering Additional Models!");
		StaticPowerAdditionalModels.registerModels();
		LOGGER.info(String.format("Registered: %1$d Additional Models!", StaticPowerAdditionalModels.MODELS.size()));
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void modelBakeEvent(ModelBakeEvent event) {
		StaticPowerForgeEventsProxy.onModelBakeEvent(event);
		LOGGER.info("Static Power Model Overrides Completed!");
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void onItemColorBakeEvent(ColorHandlerEvent.Item event) {
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
	public static void registerTileEntityTypes(RegistryEvent.Register<BlockEntityType<?>> event) {
		StaticCoreRegistry.registerBlockEntityTypes(event);
	}

	@SubscribeEvent
	public static void registerContainerTypes(RegistryEvent.Register<MenuType<?>> event) {
		StaticCoreRegistry.registerContainerTypes(event);
	}

	@SubscribeEvent
	public static void registerEntities(final RegistryEvent.Register<EntityType<?>> event) {
		ModEntities.onRegisterEntities(event);
	}

	@SubscribeEvent
	public static void registerFeatures(final RegistryEvent.Register<Feature<?>> event) {
		ModConfiguredFeatures.registerFeatures(event);
	}

	@SubscribeEvent
	public static void onAttributeCreate(EntityAttributeCreationEvent event) {
		ModEntities.onRegisterEntityAttributes(event);
	}

	@SubscribeEvent
	public static void registerRecipeSerializers(RegistryEvent.Register<RecipeSerializer<?>> event) {
		ModRecipeSerializers.onRegisterRecipeSerializers(event);
		LOGGER.info("Static Power Reipce Serializers registered!");
	}

	@SubscribeEvent
	public static void registerModifierSerializers(@Nonnull final RegistryEvent.Register<GlobalLootModifierSerializer<?>> event) {
		event.getRegistry().register(new StaticPowerLootModifier.Serializer().setRegistryName(new ResourceLocation(StaticPower.MOD_ID, "static_power_loot_modifier")));
	}
}
