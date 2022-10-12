package theking530.staticpower.events;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
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
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import theking530.staticcore.initialization.StaticCoreRegistry;
import theking530.staticcore.item.ICustomModelSupplier;
import theking530.staticcore.productivity.ProductMetricTileRendererRegistry;
import theking530.staticcore.productivity.ProductMetricTileRendererRegistry.RegisterEvent;
import theking530.staticcore.productivity.product.fluid.FluidStackProductMetricRenderer;
import theking530.staticcore.productivity.product.item.ItemStackProductMetricRenderer;
import theking530.staticpower.StaticPower;
import theking530.staticpower.blocks.interfaces.IRenderLayerProvider;
import theking530.staticpower.client.StaticPowerAdditionalModels;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.client.rendering.items.FluidCapsuleItemModel.CapsuleColorProvider;
import theking530.staticpower.client.rendering.renderers.RadiusPreviewRenderer;
import theking530.staticpower.client.rendering.renderers.WireRenderer;
import theking530.staticpower.init.ModEntities;
import theking530.staticpower.init.ModItems;
import theking530.staticpower.init.ModKeyBindings;
import theking530.staticpower.init.ModProducts;
import theking530.staticpower.teams.productivity.GuiProductionMenu;
import theking530.staticpower.teams.research.ActiveResearchHUD;
import theking530.staticpower.teams.research.GuiResearchMenu;

@SuppressWarnings("resource")
@Mod.EventBusSubscriber(modid = StaticPower.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class StaticPowerModEventsClient {
	public static final Logger LOGGER = LogManager.getLogger(StaticPowerModEventsClient.class);

	@SubscribeEvent
	public static void registerKeyBindings(RegisterKeyMappingsEvent event) {
		ModKeyBindings.registerBindings(event);
		LOGGER.info("Static Power registered key bindings!");
	}

	@SuppressWarnings("removal")
	@SubscribeEvent
	public static void clientSetupEvent(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			// If the block does not request the standard solid render type, set the new
			// render type for the block.
			LOGGER.info("Initializing Block Render Layers!");
			for (Block block : ForgeRegistries.BLOCKS) {
				// Check and update the render type as needed.
				if (block instanceof IRenderLayerProvider) {
					IRenderLayerProvider renderLayerProvider = (IRenderLayerProvider) block;
					if (renderLayerProvider.getRenderType() != RenderType.solid()) {
						ItemBlockRenderTypes.setRenderLayer(block, renderLayerProvider.getRenderType());
					}
				}
			}

			LOGGER.info("Initializing Fluid Render Layers!");
			for (Fluid fluid : ForgeRegistries.FLUIDS) {
				// Check and update the render type as needed.
				if (fluid instanceof IRenderLayerProvider) {
					IRenderLayerProvider renderLayerProvider = (IRenderLayerProvider) fluid;
					if (renderLayerProvider.getRenderType() != RenderType.solid()) {
						ItemBlockRenderTypes.setRenderLayer(fluid, renderLayerProvider.getRenderType());
					}
				}
			}

			// Register the guis.
			LOGGER.info("Registering Screen Factories!");
			StaticCoreRegistry.registerScreenFactories(event);

			// TODO: Build a system to handle this non-manually.
			ModKeyBindings.addCallback(ModKeyBindings.OPEN_RESEARCH, (binding) -> {
				if (binding.wasJustPressed()) {
					if (Minecraft.getInstance().screen == null) {
						Minecraft.getInstance().setScreen(new GuiResearchMenu());
					} else if (Minecraft.getInstance().screen instanceof GuiResearchMenu) {
						Minecraft.getInstance().screen.onClose();
					}
				}
			});
			ModKeyBindings.addCallback(ModKeyBindings.OPEN_PRODUCTION, (binding) -> {
				if (binding.wasJustPressed()) {
					if (Minecraft.getInstance().screen == null) {
						Minecraft.getInstance().setScreen(new GuiProductionMenu());
					} else if (Minecraft.getInstance().screen instanceof GuiProductionMenu) {
						Minecraft.getInstance().screen.onClose();
					}
				}
			});

			StaticPowerForgeBusClient.addHUDElement(new ActiveResearchHUD());

			// Register the custom renderers.
			StaticPowerForgeBusClient.addCustomRenderer(new WireRenderer());
			StaticPowerForgeBusClient.addCustomRenderer(new RadiusPreviewRenderer());

			// Fire event to register metric renderers.
			ModLoader.get().postEvent(new ProductMetricTileRendererRegistry.RegisterEvent());
			RegisterEvent.finalizeRegistration();

			// Log the completion.
			LOGGER.info("Static Power Client Setup Completed!");
		});
	}

	@SubscribeEvent
	public static void registerProductMetricRenderers(ProductMetricTileRendererRegistry.RegisterEvent event) {
		event.registerProductMetricRenderer(ModProducts.Item, () -> new ItemStackProductMetricRenderer());
		event.registerProductMetricRenderer(ModProducts.Fluid, () -> new FluidStackProductMetricRenderer());
	}

	@SubscribeEvent
	public static void modelRegistryEvent(ModelEvent.RegisterAdditional event) {
		// Register any additional models we want.
		LOGGER.info("Registering Additional Models!");
		StaticPowerAdditionalModels.registerModels(event);
		LOGGER.info(String.format("Registered: %1$d Additional Models!", StaticPowerAdditionalModels.MODELS.size()));
	}

	@SubscribeEvent
	public static void modelBakeEvent(ModelEvent.BakingCompleted event) {
		// Loop through all the blocks, and check to see if they are a model supplier.
		for (Block block : ForgeRegistries.BLOCKS) {
			if (block instanceof ICustomModelSupplier) {

				// Get the supplier.
				ICustomModelSupplier supplier = ((ICustomModelSupplier) block);

				// Loop through all the blockstates and override their models if they have an
				// override.
				for (BlockState blockState : block.getStateDefinition().getPossibleStates()) {
					if (supplier.hasModelOverride(blockState)) {
						// Get the existing model.
						ModelResourceLocation variantMRL = BlockModelShaper.stateToModelLocation(blockState);
						BakedModel existingModel = event.getModels().get(variantMRL);
						BakedModel override = supplier.getModelOverride(blockState, existingModel, event);
						if (override != null) {
							event.getModels().put(variantMRL, override);
						} else {
							LOGGER.error(String.format("Encountered null model override for block: %1$s.", block.getName().getString()));
						}
					}
				}
			}
		}
		for (Item item : ForgeRegistries.ITEMS) {
			if (item instanceof ICustomModelSupplier) {

				// Get the supplier.
				ICustomModelSupplier supplier = ((ICustomModelSupplier) item);

				if (supplier.hasModelOverride(null)) {
					// Get the existing model.
					ModelResourceLocation modelLocation = new ModelResourceLocation(ForgeRegistries.ITEMS.getKey(item), "inventory");
					BakedModel existingModel = supplier.getBaseModelOverride(event);
					if (existingModel == null) {
						existingModel = event.getModelManager().getModel(modelLocation);
					}

					if (existingModel != null) {
						BakedModel override = supplier.getModelOverride(null, existingModel, event);
						event.getModels().put(modelLocation, override);
					}
				}
			}
		}
		LOGGER.info("Static Power Model Overrides Completed!");
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
		LOGGER.info("Static Power Item Color Overrides Completed!");
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
					LOGGER.error(String.format("Failed to register texture: %1$s to the texture atlas.", sprite));
				}
			}
			LOGGER.info(String.format("Registered %1$s Static Power sprites.", spriteCount));
		}
		LOGGER.info("Static Power Model Texture Stitch Event Completed!");
	}

	@SubscribeEvent
	public static void registerEntityRenders(EntityRenderersEvent.RegisterRenderers event) throws Exception {
		LOGGER.info("Registering Entity Renderers!");
		ModEntities.registerEntityRenders(event);

		// Register the tile entity special renderers.
		LOGGER.info("Registering Tile Entity Special Renderers!");
		StaticCoreRegistry.registerBlockEntityRenderers(event);
	}
}
