package theking530.staticpower.events;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import theking530.staticcore.initialization.StaticCoreRegistry;
import theking530.staticpower.StaticPowerRegistry;
import theking530.staticpower.blocks.interfaces.IBlockRenderLayerProvider;
import theking530.staticpower.blocks.interfaces.ICustomModelSupplier;
import theking530.staticpower.client.StaticPowerAdditionalModels;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.client.rendering.CustomRenderer;
import theking530.staticpower.client.rendering.items.FluidCapsuleItemModel.CapsuleColorProvider;
import theking530.staticpower.init.ModItems;

@SuppressWarnings("deprecation")
@OnlyIn(Dist.CLIENT)
public class StaticPowerClientEventHandler {
	public static final Logger LOGGER = LogManager.getLogger(StaticPowerClientEventHandler.class);
	private static final CustomRenderer CUSTOM_RENDERER = new CustomRenderer();

	/**
	 * This event is raised by the client setup event.
	 * 
	 * @param event The client setup event.
	 */
	public static void onClientSetupEvent(FMLClientSetupEvent event) {
		// If the block does not request the standard solid render type, set the new
		// render type for the block.
		for (Block block : StaticPowerRegistry.BLOCKS) {

			// Check and update the render type as needed.
			if (block instanceof IBlockRenderLayerProvider) {
				IBlockRenderLayerProvider renderLayerProvider = (IBlockRenderLayerProvider) block;
				if (renderLayerProvider.getRenderType() != RenderType.getSolid()) {
					RenderTypeLookup.setRenderLayer(block, renderLayerProvider.getRenderType());
				}
			}
		}

		// Register any additional models we want.
		StaticPowerAdditionalModels.regsiterModels();

		// Initialize the guis.
		initializeGui();

		// Initialize the tile entity special renderers.
		StaticCoreRegistry.registerTileEntitySpecialRenderers();

		// Log the completion.
		LOGGER.info("Static Power Client Setup Completed!");
	}

	public static void onModelBakeEvent(ModelBakeEvent event) {
		// Loop through all the blocks, and check to see if they are a model supplier.
		for (Block block : StaticPowerRegistry.BLOCKS) {
			if (block instanceof ICustomModelSupplier) {

				// Get the supplier.
				ICustomModelSupplier supplier = ((ICustomModelSupplier) block);

				// Loop through all the blockstates and override their models if they have an
				// override.
				for (BlockState blockState : block.getStateContainer().getValidStates()) {
					if (supplier.hasModelOverride(blockState)) {
						// Get the existing model.
						ModelResourceLocation variantMRL = BlockModelShapes.getModelLocation(blockState);
						IBakedModel existingModel = event.getModelRegistry().get(variantMRL);
						IBakedModel override = supplier.getModelOverride(blockState, existingModel, event);
						if (override != null) {
							event.getModelRegistry().put(variantMRL, override);
						} else {
							LOGGER.error(String.format("Encountered null model override for block: %1$s.", block.getTranslatedName().getString()));
						}
					}
				}
			}
		}
		for (Item item : StaticPowerRegistry.ITEMS) {
			if (item instanceof ICustomModelSupplier) {

				// Get the supplier.
				ICustomModelSupplier supplier = ((ICustomModelSupplier) item);

				if (supplier.hasModelOverride(null)) {
					// Get the existing model.
					ModelResourceLocation modelLocation = new ModelResourceLocation(item.getRegistryName(), "inventory");
					IBakedModel existingModel = event.getModelManager().getModel(modelLocation);

					if (existingModel != null) {
						IBakedModel override = supplier.getModelOverride(null, existingModel, event);
						event.getModelRegistry().put(modelLocation, override);
					}
				}
			}
		}
	}

	public static void onItemColorBakeEvent(ColorHandlerEvent.Item event) {
		event.getItemColors().register(new CapsuleColorProvider(), ModItems.IronFluidCapsule);
		event.getItemColors().register(new CapsuleColorProvider(), ModItems.BasicFluidCapsule);
		event.getItemColors().register(new CapsuleColorProvider(), ModItems.AdvancedFluidCapsule);
		event.getItemColors().register(new CapsuleColorProvider(), ModItems.StaticFluidCapsule);
		event.getItemColors().register(new CapsuleColorProvider(), ModItems.EnergizedFluidCapsule);
		event.getItemColors().register(new CapsuleColorProvider(), ModItems.LumumFluidCapsule);
		event.getItemColors().register(new CapsuleColorProvider(), ModItems.CreativeFluidCapsule);
	}

	public static void onTextureStitchEvent(TextureStitchEvent.Pre event) {
		if (event.getMap().getTextureLocation() == AtlasTexture.LOCATION_BLOCKS_TEXTURE) {
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
	}

	public static void render(RenderWorldLastEvent event) {
		CUSTOM_RENDERER.render(event);
	}

	private static void initializeGui() {
		StaticCoreRegistry.registerScreenFactories();
	}
}
