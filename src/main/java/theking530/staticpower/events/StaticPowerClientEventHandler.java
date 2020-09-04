package theking530.staticpower.events;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.Screen;
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
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import theking530.api.heat.HeatTooltipUtilities;
import theking530.staticcore.initialization.StaticCoreRegistry;
import theking530.staticpower.StaticPower;
import theking530.staticpower.StaticPowerRegistry;
import theking530.staticpower.blocks.interfaces.IBlockRenderLayerProvider;
import theking530.staticpower.blocks.interfaces.ICustomModelSupplier;
import theking530.staticpower.client.StaticPowerAdditionalModels;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.client.rendering.CustomRenderer;
import theking530.staticpower.client.rendering.items.FluidCapsuleItemModel.CapsuleColorProvider;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerRecipeRegistry;
import theking530.staticpower.data.crafting.wrappers.thermalconductivity.ThermalConductivityRecipe;
import theking530.staticpower.init.ModItems;

@SuppressWarnings("deprecation")
public class StaticPowerClientEventHandler {
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
		event.getItemColors().register(new CapsuleColorProvider(), ModItems.IronCapsule);
		event.getItemColors().register(new CapsuleColorProvider(), ModItems.BasicCapsule);
		event.getItemColors().register(new CapsuleColorProvider(), ModItems.AdvancedCapsule);
		event.getItemColors().register(new CapsuleColorProvider(), ModItems.StaticCapsule);
		event.getItemColors().register(new CapsuleColorProvider(), ModItems.EnergizedCapsule);
		event.getItemColors().register(new CapsuleColorProvider(), ModItems.LumumCapsule);
		event.getItemColors().register(new CapsuleColorProvider(), ModItems.CreativeCapsule);
	}

	public static void onTextureStitchEvent(TextureStitchEvent.Pre event) {
		if (event.getMap().getTextureLocation() == AtlasTexture.LOCATION_BLOCKS_TEXTURE) {
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
	}

	public static void render(RenderWorldLastEvent event) {
		CUSTOM_RENDERER.render(event);
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void onAddItemTooltip(ItemTooltipEvent event) {
		if (Screen.hasShiftDown()) {
			RecipeMatchParameters matchParameters = new RecipeMatchParameters(event.getItemStack());

			FluidUtil.getFluidContained(event.getItemStack()).ifPresent(fluid -> {
				matchParameters.setFluids(fluid.copy());
			});

			StaticPowerRecipeRegistry.getRecipe(ThermalConductivityRecipe.RECIPE_TYPE, matchParameters).ifPresent(recipe -> {
				event.getToolTip().add(HeatTooltipUtilities.getHeatRateTooltip(recipe.getThermalConductivity()));
			});
		}
	}

	@OnlyIn(Dist.CLIENT)
	private static void initializeGui() {
		StaticCoreRegistry.registerScreenFactories();
	}
}
