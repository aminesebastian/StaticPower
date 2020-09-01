package theking530.staticpower.events;

import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import theking530.staticpower.StaticPower;
import theking530.staticpower.StaticPowerRegistry;
import theking530.staticpower.data.StaticPowerDataRegistry;

@Mod.EventBusSubscriber(modid = StaticPower.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class StaticPowerModEventRegistry {
	@SubscribeEvent
	public static void registriesCreated(RegistryEvent.NewRegistry event) {
		StaticPowerDataRegistry.onResourcesReloaded();
	}

	@SubscribeEvent
	public static void commonSetupEvent(FMLCommonSetupEvent event) {
		StaticPowerCommonEventHandler.onCommonSetupEvent(event);
		StaticPower.LOGGER.info("Static Power Common Setup Completed!");
	}

	@SubscribeEvent
	public static void enqueueIMC(InterModEnqueueEvent event) {
		StaticPowerCommonEventHandler.enqueueIMC(event);
		StaticPower.LOGGER.info("Static Power IMC Messages Enqueued!");
	}

	@SubscribeEvent
	public static void clientSetupEvent(FMLClientSetupEvent event) {
		StaticPowerClientEventHandler.onClientSetupEvent(event);
		StaticPower.LOGGER.info("Static Power Client Setup Completed!");
	}

	@SubscribeEvent
	public static void modelBakeEvent(ModelBakeEvent event) {
		StaticPowerClientEventHandler.onModelBakeEvent(event);
		StaticPower.LOGGER.info("Static Power Model Overrides Completed!");
	}

	@SubscribeEvent
	public static void onItemColorBakeEvent(ColorHandlerEvent.Item event) {
		StaticPowerClientEventHandler.onItemColorBakeEvent(event);
		StaticPower.LOGGER.info("Static Power Item Color Overrides Completed!");
	}

	@SubscribeEvent
	public static void textureStitchEvent(TextureStitchEvent.Pre event) {
		StaticPowerClientEventHandler.onTextureStitchEvent(event);
		StaticPower.LOGGER.info("Static Power Model Texture Stitch Event Completed!");
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
		StaticPower.LOGGER.info("Static Power Reipce Serializers registered!");
	}
}
