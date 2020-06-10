package theking530.staticpower.events;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import theking530.staticpower.StaticPower;
import theking530.staticpower.StaticPowerRegistry;
import theking530.staticpower.blocks.IBlockRenderLayerProvider;
import theking530.staticpower.blocks.ICustomModelSupplier;
import theking530.staticpower.client.rendering.CustomRenderer;
import theking530.staticpower.client.rendering.StaticPowerRendererTextures;
import theking530.staticpower.client.rendering.tileentity.TileEntityRenderDigistore;
import theking530.staticpower.initialization.ModTileEntityTypes;

@SuppressWarnings("deprecation")
public class StaticPowerClientEventHandler {
	private static final CustomRenderer TEST_RENDERER = new CustomRenderer();

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

			// Check to see if we should register any additional models.
			if (block instanceof ICustomModelSupplier) {
				// Get the supplier.
				ICustomModelSupplier supplier = ((ICustomModelSupplier) block);
				supplier.registerAdditionalModels();
			}
		}

		// Initialize the guis.
		initializeGui();

		// Temp TESR
		ClientRegistry.bindTileEntityRenderer(ModTileEntityTypes.DIGISTORE, TileEntityRenderDigistore::new);

		// Log the completion.
		StaticPower.LOGGER.info("Static Power Client Setup Completed!");
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
							StaticPower.LOGGER.error(String.format("Encountered null model override for block: %1$s.", block.getNameTextComponent().getFormattedText()));
						}
					}
				}
			}
		}
	}
	public static void onTextureStitchEvent(TextureStitchEvent.Pre event) {
		int spriteCount = 0;
		if (event.getMap().getTextureLocation() == AtlasTexture.LOCATION_BLOCKS_TEXTURE) {
			for (ResourceLocation sprite : StaticPowerRendererTextures.SPRITES) {
				event.addSprite(sprite);
				spriteCount++;
			}
		}
		StaticPower.LOGGER.info(String.format("Registered %1$s Static Power sprites.", spriteCount));
	}

	public static void render(RenderWorldLastEvent event) {
		TEST_RENDERER.render(event);
	}

	@SuppressWarnings({ "unchecked" })
	@OnlyIn(Dist.CLIENT)
	private static void initializeGui() {
		DeferredWorkQueue.runLater(() -> {
			StaticPowerRegistry.SCREEN_FACTORIES.forEach((containerType, screenFactory) -> {
				ScreenManager.registerFactory(containerType, screenFactory);
			});
			StaticPower.LOGGER.info("Registered all Static Power container types.");
		});
	}
}
