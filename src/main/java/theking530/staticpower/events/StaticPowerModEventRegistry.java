package theking530.staticpower.events;

import javax.annotation.Nonnull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;
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
import theking530.api.attributes.capability.CapabilityAttributable;
import theking530.api.digistore.CapabilityDigistoreInventory;
import theking530.api.heat.CapabilityHeatable;
import theking530.api.power.CapabilityStaticVolt;
import theking530.staticcore.initialization.StaticCoreRegistry;
import theking530.staticpower.StaticPower;
import theking530.staticpower.StaticPowerRegistry;
import theking530.staticpower.cables.digistore.DigistoreNetworkModuleFactory;
import theking530.staticpower.cables.fluid.FluidNetworkModuleFactory;
import theking530.staticpower.cables.heat.HeatNetworkModuleFactory;
import theking530.staticpower.cables.item.ItemNetworkModuleFactory;
import theking530.staticpower.cables.network.CableNetworkModuleRegistry;
import theking530.staticpower.cables.network.CableNetworkModuleTypes;
import theking530.staticpower.cables.power.PowerNetworkModuleFactory;
import theking530.staticpower.cables.redstone.basic.RedstoneNetworkModuleFactory;
import theking530.staticpower.cables.redstone.bundled.BundledRedstoneNetworkModuleFactory;
import theking530.staticpower.cables.scaffold.ScaffoldNetworkModuleFactory;
import theking530.staticpower.client.StaticPowerAdditionalModels;
import theking530.staticpower.data.loot.StaticPowerLootModifier;
import theking530.staticpower.entities.AbstractEntityType;
import theking530.staticpower.entities.player.datacapability.CapabilityStaticPowerPlayerData;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.teams.TeamManager;

@Mod.EventBusSubscriber(modid = StaticPower.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class StaticPowerModEventRegistry {
	public static final String TOP_MODID = "theoneprobe";
	public static final String JEI_MODID = "jei";

	public static final Logger LOGGER = LogManager.getLogger(StaticPowerModEventRegistry.class);

	@SubscribeEvent
	public static void commonSetupEvent(FMLCommonSetupEvent event) {
		// Register network modules.
		CableNetworkModuleRegistry.get().registerCableNetworkAttachmentFactory(CableNetworkModuleTypes.POWER_NETWORK_MODULE, new PowerNetworkModuleFactory());
		CableNetworkModuleRegistry.get().registerCableNetworkAttachmentFactory(CableNetworkModuleTypes.ITEM_NETWORK_MODULE, new ItemNetworkModuleFactory());
		CableNetworkModuleRegistry.get().registerCableNetworkAttachmentFactory(CableNetworkModuleTypes.FLUID_NETWORK_MODULE, new FluidNetworkModuleFactory());
		CableNetworkModuleRegistry.get().registerCableNetworkAttachmentFactory(CableNetworkModuleTypes.DIGISTORE_NETWORK_MODULE, new DigistoreNetworkModuleFactory());
		CableNetworkModuleRegistry.get().registerCableNetworkAttachmentFactory(CableNetworkModuleTypes.HEAT_NETWORK_MODULE, new HeatNetworkModuleFactory());
		CableNetworkModuleRegistry.get().registerCableNetworkAttachmentFactory(CableNetworkModuleTypes.SCAFFOLD_NETWORK_MODULE, new ScaffoldNetworkModuleFactory());

		CableNetworkModuleRegistry.get().registerCableNetworkAttachmentFactory(CableNetworkModuleTypes.BUNDLED_REDSTONE_NETWORK_MODULE, new BundledRedstoneNetworkModuleFactory());
		CableNetworkModuleRegistry.get().registerCableNetworkAttachmentFactory(CableNetworkModuleTypes.REDSTONE_NETWORK_MODULE, new RedstoneNetworkModuleFactory());

		CableNetworkModuleRegistry.get().registerCableNetworkAttachmentFactory(CableNetworkModuleTypes.REDSTONE_NETWORK_MODULE_DARK_RED, new RedstoneNetworkModuleFactory());
		CableNetworkModuleRegistry.get().registerCableNetworkAttachmentFactory(CableNetworkModuleTypes.REDSTONE_NETWORK_MODULE_RED, new RedstoneNetworkModuleFactory());
		CableNetworkModuleRegistry.get().registerCableNetworkAttachmentFactory(CableNetworkModuleTypes.REDSTONE_NETWORK_MODULE_GOLD, new RedstoneNetworkModuleFactory());
		CableNetworkModuleRegistry.get().registerCableNetworkAttachmentFactory(CableNetworkModuleTypes.REDSTONE_NETWORK_MODULE_YELLOW, new RedstoneNetworkModuleFactory());
		CableNetworkModuleRegistry.get().registerCableNetworkAttachmentFactory(CableNetworkModuleTypes.REDSTONE_NETWORK_MODULE_DARK_GREEN, new RedstoneNetworkModuleFactory());
		CableNetworkModuleRegistry.get().registerCableNetworkAttachmentFactory(CableNetworkModuleTypes.REDSTONE_NETWORK_MODULE_GREEN, new RedstoneNetworkModuleFactory());
		CableNetworkModuleRegistry.get().registerCableNetworkAttachmentFactory(CableNetworkModuleTypes.REDSTONE_NETWORK_MODULE_AQUA, new RedstoneNetworkModuleFactory());
		CableNetworkModuleRegistry.get().registerCableNetworkAttachmentFactory(CableNetworkModuleTypes.REDSTONE_NETWORK_MODULE_DARK_AQUA, new RedstoneNetworkModuleFactory());
		CableNetworkModuleRegistry.get().registerCableNetworkAttachmentFactory(CableNetworkModuleTypes.REDSTONE_NETWORK_MODULE_DARK_BLUE, new RedstoneNetworkModuleFactory());
		CableNetworkModuleRegistry.get().registerCableNetworkAttachmentFactory(CableNetworkModuleTypes.REDSTONE_NETWORK_MODULE_BLUE, new RedstoneNetworkModuleFactory());
		CableNetworkModuleRegistry.get().registerCableNetworkAttachmentFactory(CableNetworkModuleTypes.REDSTONE_NETWORK_MODULE_LIGHT_PURPLE, new RedstoneNetworkModuleFactory());
		CableNetworkModuleRegistry.get().registerCableNetworkAttachmentFactory(CableNetworkModuleTypes.REDSTONE_NETWORK_MODULE_DARK_PURPLE, new RedstoneNetworkModuleFactory());
		CableNetworkModuleRegistry.get().registerCableNetworkAttachmentFactory(CableNetworkModuleTypes.REDSTONE_NETWORK_MODULE_WHITE, new RedstoneNetworkModuleFactory());
		CableNetworkModuleRegistry.get().registerCableNetworkAttachmentFactory(CableNetworkModuleTypes.REDSTONE_NETWORK_MODULE_GRAY, new RedstoneNetworkModuleFactory());
		CableNetworkModuleRegistry.get().registerCableNetworkAttachmentFactory(CableNetworkModuleTypes.REDSTONE_NETWORK_MODULE_DARK_GRAY, new RedstoneNetworkModuleFactory());
		CableNetworkModuleRegistry.get().registerCableNetworkAttachmentFactory(CableNetworkModuleTypes.REDSTONE_NETWORK_MODULE_BLACK, new RedstoneNetworkModuleFactory());

		// Register composter recipes.
		event.enqueueWork(() -> {
			ComposterBlock.COMPOSTABLES.put(ModBlocks.RubberTreeLeaves.asItem(), 0.6f);
		});

		// Register data classes.
		StaticPowerRegistry.registerDataFactory("teams", () -> {
			return TeamManager.get();
		});

		LOGGER.info("Static Power Common Setup Completed!");
	}

	@SubscribeEvent
	public static void capabilityRegisterEvent(RegisterCapabilitiesEvent event) {
		// Register capabilities.
		CapabilityDigistoreInventory.register(event);
		CapabilityStaticVolt.register(event);
		CapabilityHeatable.register(event);
		CapabilityAttributable.register(event);
		CapabilityStaticPowerPlayerData.register(event);
	}

	@SubscribeEvent
	public static void enqueueIMC(InterModEnqueueEvent event) {
		LOGGER.info("Static Power IMC Messages Enqueued!");
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void clientSetupEvent(FMLClientSetupEvent event) {
		StaticPowerClientEventHandler.onClientSetupEvent(event);
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
		StaticPowerClientEventHandler.onModelBakeEvent(event);
		LOGGER.info("Static Power Model Overrides Completed!");
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void onItemColorBakeEvent(ColorHandlerEvent.Item event) {
		StaticPowerClientEventHandler.onItemColorBakeEvent(event);
		LOGGER.info("Static Power Item Color Overrides Completed!");
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void textureStitchEvent(TextureStitchEvent.Pre event) {
		StaticPowerClientEventHandler.onTextureStitchEvent(event);
		LOGGER.info("Static Power Model Texture Stitch Event Completed!");
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void registerEntityRenders(EntityRenderersEvent.RegisterRenderers event) throws Exception {
		// Regsiter entity renderers.
		LOGGER.info("Registering Entity Renderers!");
		for (AbstractEntityType<?> type : StaticPowerRegistry.ENTITIES) {
			type.registerRenderers(event);
		}

		// Register the tile entity special renderers.
		LOGGER.info("Registering Tile Entity Special Renderers!");
		StaticCoreRegistry.registerBlockEntityRenderers(event);
	}

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		StaticPowerRegistry.onRegisterItems(event);
	}

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event) {
		StaticPowerRegistry.onRegisterBlocks(event);
	}

	@SubscribeEvent
	public static void registerFluids(RegistryEvent.Register<Fluid> event) {
		StaticPowerRegistry.onRegisterFluids(event);
	}

	@SubscribeEvent
	public static void registerTileEntityTypes(RegistryEvent.Register<BlockEntityType<?>> event) {
		StaticPowerRegistry.onRegisterTileEntityTypes(event);
	}

	@SubscribeEvent
	public static void registerContainerTypes(RegistryEvent.Register<MenuType<?>> event) {
		StaticPowerRegistry.onRegisterContainerTypes(event);
	}

	@SubscribeEvent
	public static void registerEntities(final RegistryEvent.Register<EntityType<?>> event) {
		StaticPowerRegistry.onRegisterEntities(event);
	}

	@SubscribeEvent
	public static void onAttributeCreate(EntityAttributeCreationEvent event) {
		StaticPowerRegistry.onRegisterEntityAttributes(event);
	}

	@SubscribeEvent
	public static void registerRecipeSerializers(RegistryEvent.Register<RecipeSerializer<?>> event) {
		StaticPowerRegistry.onRegisterRecipeSerializers(event);
		LOGGER.info("Static Power Reipce Serializers registered!");
	}

	@SubscribeEvent
	public static void registerModifierSerializers(@Nonnull final RegistryEvent.Register<GlobalLootModifierSerializer<?>> event) {
		event.getRegistry().register(new StaticPowerLootModifier.Serializer().setRegistryName(new ResourceLocation(StaticPower.MOD_ID, "static_power_loot_modifier")));
	}
}
