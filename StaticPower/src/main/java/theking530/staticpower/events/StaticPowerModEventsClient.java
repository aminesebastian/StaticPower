package theking530.staticpower.events;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import theking530.staticcore.block.IRenderLayerProvider;
import theking530.staticcore.client.ICustomModelProvider;
import theking530.staticcore.events.StaticCoreForgeBusClient;
import theking530.staticcore.initialization.StaticCoreRegistrationHelpers;
import theking530.staticpower.StaticPower;
import theking530.staticpower.client.StaticPowerAdditionalModels;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.client.rendering.items.FluidCapsuleItemModel.CapsuleColorProvider;
import theking530.staticpower.client.rendering.renderers.ElectricalOverlayRenderer;
import theking530.staticpower.client.rendering.renderers.RadiusPreviewRenderer;
import theking530.staticpower.client.rendering.renderers.WireRenderer;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModEntities;
import theking530.staticpower.init.ModFluids;
import theking530.staticpower.init.ModItems;
import theking530.staticpower.init.ModKeyBindings;

@Mod.EventBusSubscriber(modid = StaticPower.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class StaticPowerModEventsClient {

	@SubscribeEvent
	public static void registerKeyBindings(RegisterKeyMappingsEvent event) {
		ModKeyBindings.registerBindings(event);
		StaticPower.LOGGER.info("Static Power registered key bindings!");
	}

	@SuppressWarnings("removal")
	@SubscribeEvent
	public static void clientSetupEvent(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			// If the block does not request the standard solid render type, set the new
			// render type for the block.
			StaticPower.LOGGER.info("Initializing Block Render Layers!");
			for (RegistryObject<Block> block : ModBlocks.BLOCKS.getEntries()) {
				// Check and update the render type as needed.
				if (block.get() instanceof IRenderLayerProvider) {
					IRenderLayerProvider renderLayerProvider = (IRenderLayerProvider) block.get();
					if (renderLayerProvider.getRenderType() != RenderType.solid()) {
						ItemBlockRenderTypes.setRenderLayer(block.get(), renderLayerProvider.getRenderType());
					}
				}
			}

			StaticPower.LOGGER.info("Initializing Fluid Render Layers!");
			for (RegistryObject<Fluid> fluid : ModFluids.FLUIDS.getEntries()) {
				// Check and update the render type as needed.
				if (fluid.get() instanceof IRenderLayerProvider) {
					IRenderLayerProvider renderLayerProvider = (IRenderLayerProvider) fluid.get();
					if (renderLayerProvider.getRenderType() != RenderType.solid()) {
						ItemBlockRenderTypes.setRenderLayer(fluid.get(), renderLayerProvider.getRenderType());
					}
				}
			}

			StaticCoreRegistrationHelpers.registerScreenFactories(event);

			// Register the custom renderers.
			StaticCoreForgeBusClient.addCustomRenderer(new WireRenderer());
			StaticCoreForgeBusClient.addCustomRenderer(new RadiusPreviewRenderer());
			StaticCoreForgeBusClient.addCustomRenderer(new ElectricalOverlayRenderer());

			// Log the completion.
			StaticPower.LOGGER.info("Static Power Client Setup Completed!");
		});
	}

	@SubscribeEvent
	public static void modelRegistryEvent(ModelEvent.RegisterAdditional event) {
		// Register any additional models we want.
		StaticPower.LOGGER.info("Registering Additional Models!");
		StaticPowerAdditionalModels.registerModels(event);
		StaticPower.LOGGER.info(String.format("Registered: %1$d Additional Models!", StaticPowerAdditionalModels.MODELS.size()));
	}

	@SubscribeEvent
	public static void modelBakeEvent(ModelEvent.BakingCompleted event) {
		// Loop through all the blocks, and check to see if they are a model supplier.
		for (RegistryObject<Block> block : ModBlocks.BLOCKS.getEntries()) {
			if (block.get() instanceof ICustomModelProvider) {

				// Get the supplier.
				ICustomModelProvider supplier = ((ICustomModelProvider) block.get());

				// Loop through all the blockstates and override their models if they have an
				// override.
				for (BlockState blockState : block.get().getStateDefinition().getPossibleStates()) {
					if (supplier.hasModelOverride(blockState)) {
						// Get the existing model.
						ModelResourceLocation variantMRL = BlockModelShaper.stateToModelLocation(blockState);
						BakedModel existingModel = event.getModels().get(variantMRL);
						BakedModel override = supplier.getBlockModeOverride(blockState, existingModel, event);
						if (override != null) {
							event.getModels().put(variantMRL, override);
						} else {
							StaticPower.LOGGER.error(String.format("Encountered null model override for block: %1$s.", block.get().getName().getString()));
						}
					}
				}
			}
		}
		for (RegistryObject<Item> item : ModItems.ITEMS.getEntries()) {
			if (item.get() instanceof ICustomModelProvider) {

				// Get the supplier.
				ICustomModelProvider supplier = ((ICustomModelProvider) item.get());

				if (supplier.hasModelOverride(null)) {
					// Get the existing model.
					ModelResourceLocation modelLocation = new ModelResourceLocation(ForgeRegistries.ITEMS.getKey(item.get()), "inventory");
					BakedModel existingModel = supplier.getItemModelOverride(event);
					if (existingModel == null) {
						existingModel = event.getModelManager().getModel(modelLocation);
					}

					if (existingModel != null) {
						BakedModel override = supplier.getBlockModeOverride(null, existingModel, event);
						event.getModels().put(modelLocation, override);
					}
				}
			}
		}

		StaticPower.LOGGER.info("Static Power Model Overrides Completed!");
	}

	@SubscribeEvent
	public static void onItemColorBakeEvent(RegisterColorHandlersEvent.Item event) {
		event.register(new CapsuleColorProvider(), ModItems.IronFluidCapsule.get());
		event.register(new CapsuleColorProvider(), ModItems.BasicFluidCapsule.get());
		event.register(new CapsuleColorProvider(), ModItems.AdvancedFluidCapsule.get());
		event.register(new CapsuleColorProvider(), ModItems.StaticFluidCapsule.get());
		event.register(new CapsuleColorProvider(), ModItems.EnergizedFluidCapsule.get());
		event.register(new CapsuleColorProvider(), ModItems.LumumFluidCapsule.get());
		event.register(new CapsuleColorProvider(), ModItems.CreativeFluidCapsule.get());
		StaticPower.LOGGER.info("Static Power Item Color Overrides Completed!");
	}

	@SubscribeEvent
	public static void textureStitchEvent(TextureStitchEvent.Pre event) {
		if (event.getAtlas().location() == InventoryMenu.BLOCK_ATLAS) {
			int spriteCount = 0;
			for (ResourceLocation sprite : StaticPowerSprites.SPRITES) {
				try {
					event.addSprite(sprite);
					spriteCount++;
				} catch (Exception e) {
					StaticPower.LOGGER.error(String.format("Failed to register texture: %1$s to the texture atlas.", sprite));
				}
			}
			StaticPower.LOGGER.info(String.format("Registered %1$s Static Power sprites.", spriteCount));
		}
		StaticPower.LOGGER.info("Static Power Model Texture Stitch Event Completed!");
	}

	@SubscribeEvent
	public static void registerEntityRenders(EntityRenderersEvent.RegisterRenderers event) throws Exception {
		StaticPower.LOGGER.info("Registering Entity Renderers!");
		ModEntities.registerEntityRenders(event);

		// Register the tile entity special renderers.
		StaticPower.LOGGER.info("Registering Tile Entity Special Renderers!");
		StaticCoreRegistrationHelpers.registerBlockEntityRenderers(StaticPower.MOD_ID, event);
	}

}
