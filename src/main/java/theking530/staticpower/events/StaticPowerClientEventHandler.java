package theking530.staticpower.events;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.ScreenManager;
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
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import theking530.staticpower.StaticPower;
import theking530.staticpower.StaticPowerRegistry;
import theking530.staticpower.blocks.IBlockRenderLayerProvider;
import theking530.staticpower.blocks.ICustomModelSupplier;
import theking530.staticpower.client.StaticPowerAdditionalModels;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.client.rendering.CustomRenderer;
import theking530.staticpower.client.rendering.tileentity.TileEntityRenderAutoCraftingTable;
import theking530.staticpower.client.rendering.tileentity.TileEntityRenderAutoSolderingTable;
import theking530.staticpower.client.rendering.tileentity.TileEntityRenderBatteryBlock;
import theking530.staticpower.client.rendering.tileentity.TileEntityRenderChargingStation;
import theking530.staticpower.client.rendering.tileentity.TileEntityRenderDigistore;
import theking530.staticpower.client.rendering.tileentity.TileEntityRenderFarmer;
import theking530.staticpower.client.rendering.tileentity.TileEntityRenderFluidCable;
import theking530.staticpower.client.rendering.tileentity.TileEntityRenderFluidGenerator;
import theking530.staticpower.client.rendering.tileentity.TileEntityRenderFluidInfuser;
import theking530.staticpower.client.rendering.tileentity.TileEntityRenderItemCable;
import theking530.staticpower.client.rendering.tileentity.TileEntityRenderPump;
import theking530.staticpower.client.rendering.tileentity.TileEntityRenderSolderingTable;
import theking530.staticpower.client.rendering.tileentity.TileEntityRenderSqueezer;
import theking530.staticpower.client.rendering.tileentity.TileEntityRenderTank;
import theking530.staticpower.client.rendering.tileentity.TileEntityRenderTreeFarmer;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerRecipeRegistry;
import theking530.staticpower.data.crafting.wrappers.thermalconductivity.ThermalConductivityRecipe;
import theking530.staticpower.init.ModTileEntityTypes;
import theking530.staticpower.tileentities.components.heat.HeatUtilities;

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
		}

		// Register any additional models we want.
		StaticPowerAdditionalModels.regsiterModels();

		// Initialize the guis.
		initializeGui();

		// Register the tile entity renderers.
		ClientRegistry.bindTileEntityRenderer(ModTileEntityTypes.DIGISTORE, TileEntityRenderDigistore::new);
		ClientRegistry.bindTileEntityRenderer(ModTileEntityTypes.ITEM_CABLE_BASIC, TileEntityRenderItemCable::new);
		ClientRegistry.bindTileEntityRenderer(ModTileEntityTypes.ITEM_CABLE_ADVANCED, TileEntityRenderItemCable::new);
		ClientRegistry.bindTileEntityRenderer(ModTileEntityTypes.ITEM_CABLE_STATIC, TileEntityRenderItemCable::new);
		ClientRegistry.bindTileEntityRenderer(ModTileEntityTypes.ITEM_CABLE_ENERGIZED, TileEntityRenderItemCable::new);
		ClientRegistry.bindTileEntityRenderer(ModTileEntityTypes.ITEM_CABLE_LUMUM, TileEntityRenderItemCable::new);
		ClientRegistry.bindTileEntityRenderer(ModTileEntityTypes.ITEM_CABLE_CREATIVE, TileEntityRenderItemCable::new);

		ClientRegistry.bindTileEntityRenderer(ModTileEntityTypes.BATTERY_BASIC, TileEntityRenderBatteryBlock::new);
		ClientRegistry.bindTileEntityRenderer(ModTileEntityTypes.BATTERY_ADVANCED, TileEntityRenderBatteryBlock::new);
		ClientRegistry.bindTileEntityRenderer(ModTileEntityTypes.BATTERY_STATIC, TileEntityRenderBatteryBlock::new);
		ClientRegistry.bindTileEntityRenderer(ModTileEntityTypes.BATTERY_ENERGIZED, TileEntityRenderBatteryBlock::new);
		ClientRegistry.bindTileEntityRenderer(ModTileEntityTypes.BATTERY_LUMUM, TileEntityRenderBatteryBlock::new);
		ClientRegistry.bindTileEntityRenderer(ModTileEntityTypes.BATTERY_CREATIVE, TileEntityRenderBatteryBlock::new);

		ClientRegistry.bindTileEntityRenderer(ModTileEntityTypes.SQUEEZER, TileEntityRenderSqueezer::new);
		ClientRegistry.bindTileEntityRenderer(ModTileEntityTypes.BASIC_FARMER, TileEntityRenderFarmer::new);
		ClientRegistry.bindTileEntityRenderer(ModTileEntityTypes.TREE_FARM, TileEntityRenderTreeFarmer::new);
		ClientRegistry.bindTileEntityRenderer(ModTileEntityTypes.CHARGING_STATION, TileEntityRenderChargingStation::new);
		ClientRegistry.bindTileEntityRenderer(ModTileEntityTypes.FLUID_INFUSER, TileEntityRenderFluidInfuser::new);
		ClientRegistry.bindTileEntityRenderer(ModTileEntityTypes.FLUID_GENERATOR, TileEntityRenderFluidGenerator::new);

		ClientRegistry.bindTileEntityRenderer(ModTileEntityTypes.FLUID_CABLE, TileEntityRenderFluidCable::new);
		ClientRegistry.bindTileEntityRenderer(ModTileEntityTypes.INDUSTRIAL_FLUID_CABLE, TileEntityRenderFluidCable::new);
		ClientRegistry.bindTileEntityRenderer(ModTileEntityTypes.TANK, TileEntityRenderTank::new);
		ClientRegistry.bindTileEntityRenderer(ModTileEntityTypes.PUMP, TileEntityRenderPump::new);

		ClientRegistry.bindTileEntityRenderer(ModTileEntityTypes.AUTO_CRAFTING_TABLE, TileEntityRenderAutoCraftingTable::new);
		ClientRegistry.bindTileEntityRenderer(ModTileEntityTypes.AUTO_SOLDERING_TABLE, TileEntityRenderAutoSolderingTable::new);
		ClientRegistry.bindTileEntityRenderer(ModTileEntityTypes.SOLDERING_TABLE, TileEntityRenderSolderingTable::new);

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
		TEST_RENDERER.render(event);
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
				event.getToolTip().add(HeatUtilities.getHeatRateTooltip(recipe.getThermalConductivity()));
			});
		}
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
