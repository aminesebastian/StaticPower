package theking530.staticpower.events;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.block.ComposterBlock;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import theking530.api.digistore.CapabilityDigistoreInventory;
import theking530.api.heat.CapabilityHeatable;
import theking530.api.power.CapabilityStaticVolt;
import theking530.api.smithingattributes.attributes.AttributeModifierRegistry;
import theking530.api.smithingattributes.attributes.AttributeRegistry;
import theking530.api.smithingattributes.attributes.FortuneAttributeDefenition;
import theking530.api.smithingattributes.attributes.modifiers.FloatAttributeModifier;
import theking530.api.smithingattributes.capability.CapabilitySmithable;
import theking530.staticpower.StaticPower;
import theking530.staticpower.StaticPowerRegistry;
import theking530.staticpower.cables.digistore.DigistoreNetworkModuleFactory;
import theking530.staticpower.cables.fluid.FluidNetworkModuleFactory;
import theking530.staticpower.cables.heat.HeatNetworkModuleFactory;
import theking530.staticpower.cables.item.ItemNetworkModuleFactory;
import theking530.staticpower.cables.network.CableNetworkModuleRegistry;
import theking530.staticpower.cables.network.CableNetworkModuleTypes;
import theking530.staticpower.cables.power.PowerNetworkModuleFactory;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.integration.TOP.PluginTOP;

@SuppressWarnings("deprecation")
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

		// Register capabilities.
		CapabilityDigistoreInventory.register();
		CapabilityStaticVolt.register();
		CapabilityHeatable.register();
		CapabilitySmithable.register();

		// Register smithing attributes.
		AttributeRegistry.registerAttributeType(new ResourceLocation("staticpower", "fortune"), (id) -> new FortuneAttributeDefenition(id));
		
		// Register smithing attribute modifiers.
		AttributeModifierRegistry.registerAttributeType("float", (name, type) -> new FloatAttributeModifier(name, type));
		
		// Register composter recipes.
		DeferredWorkQueue.runLater(() -> {
			ComposterBlock.CHANCES.put(ModBlocks.RubberTreeLeaves.asItem(), 0.6f);
		});

		LOGGER.info("Static Power Common Setup Completed!");
	}

	@SubscribeEvent
	public static void enqueueIMC(InterModEnqueueEvent event) {
		// Only register if the one probe is loaded.
		ModList modList = ModList.get();
		if (modList.isLoaded(TOP_MODID)) {
			PluginTOP.sendIMC();
		}
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
	public static void registerTileEntityTypes(RegistryEvent.Register<TileEntityType<?>> event) {
		StaticPowerRegistry.onRegisterTileEntityTypes(event);
	}

	@SubscribeEvent
	public static void registerContainerTypes(RegistryEvent.Register<ContainerType<?>> event) {
		StaticPowerRegistry.onRegisterContainerTypes(event);
	}

	@SubscribeEvent
	public static void registerRecipeSerializers(RegistryEvent.Register<IRecipeSerializer<?>> event) {
		StaticPowerRegistry.onRegisterRecipeSerializers(event);
		LOGGER.info("Static Power Reipce Serializers registered!");
	}
}
