package theking530.staticpower.events;

import java.lang.reflect.Field;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.blaze3d.vertex.MatrixApplyingVertexBuilder;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.DrawHighlightEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import theking530.staticcore.initialization.StaticCoreRegistry;
import theking530.staticcore.item.ICustomModelSupplier;
import theking530.staticpower.StaticPowerRegistry;
import theking530.staticpower.blocks.interfaces.IBlockRenderLayerProvider;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.client.rendering.CustomRenderer;
import theking530.staticpower.client.rendering.items.FluidCapsuleItemModel.CapsuleColorProvider;
import theking530.staticpower.entities.AbstractSpawnableEntityType;
import theking530.staticpower.init.ModItems;
import theking530.staticpower.items.tools.AbstractMultiHarvestTool;
import theking530.staticpower.utilities.RaytracingUtilities;

@SuppressWarnings("deprecation")
@OnlyIn(Dist.CLIENT)
public class StaticPowerClientEventHandler {
	public static final Logger LOGGER = LogManager.getLogger(StaticPowerClientEventHandler.class);
	private static final CustomRenderer CUSTOM_RENDERER = new CustomRenderer();

	protected static Field currentBlockDamageMP;

	/**
	 * This event is raised by the client setup event.
	 * 
	 * @param event The client setup event.
	 */
	public static void onClientSetupEvent(FMLClientSetupEvent event) {
		// If the block does not request the standard solid render type, set the new
		// render type for the block.
		LOGGER.info("Initializing Block Render Layers!");
		for (Block block : StaticPowerRegistry.BLOCKS) {
			// Check and update the render type as needed.
			if (block instanceof IBlockRenderLayerProvider) {
				IBlockRenderLayerProvider renderLayerProvider = (IBlockRenderLayerProvider) block;
				if (renderLayerProvider.getRenderType() != RenderType.getSolid()) {
					RenderTypeLookup.setRenderLayer(block, renderLayerProvider.getRenderType());
				}
			}
		}

		// Register the guis.
		LOGGER.info("Registering Screen Factories!");
		StaticCoreRegistry.registerScreenFactories();

		// Register the tile entity special renderers.
		LOGGER.info("Registering Tile Entity Special Renderers!");
		StaticCoreRegistry.registerTileEntitySpecialRenderers();

		// Regsiter entity renderers.
		LOGGER.info("Registering Entity Renderers!");
		for (AbstractSpawnableEntityType<?> type : StaticPowerRegistry.ENTITES) {
			type.registerRenderers(event);
		}

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

	public static void onWorldRender(RenderWorldLastEvent event) {
		CUSTOM_RENDERER.render(event);
		renderMultiHarvesteBlockBreakEffect(event);
	}

	/**
	 * Renders the outline on the extra blocks
	 *
	 * @param event the highlight event
	 */
	@OnlyIn(Dist.CLIENT)
	public static void renderMultiHarvestBoundingBoxes(DrawHighlightEvent.HighlightBlock event) {
		// If the player is null, do nothing.
		if (Minecraft.getInstance().player == null) {
			return;
		}

		// Get the held item.
		ItemStack tool = Minecraft.getInstance().player.getHeldItemMainhand();

		// Check to see if the tool is not empty and is an instance of the multi harvest
		// tool.
		if (!tool.isEmpty() && tool.getItem() instanceof AbstractMultiHarvestTool) {
			// If so, get the current rendering info.
			ActiveRenderInfo renderInfo = Minecraft.getInstance().gameRenderer.getActiveRenderInfo();

			// Get the tool's item and get the blocks that are currently being targeted for
			// harvesting.
			AbstractMultiHarvestTool toolItem = (AbstractMultiHarvestTool) tool.getItem();
			List<BlockPos> extraBlocks = toolItem.getMineableExtraBlocks(tool, event.getTarget().getPos(), Minecraft.getInstance().player);

			// Get the world renderer state.
			WorldRenderer worldRender = event.getContext();
			MatrixStack matrix = event.getMatrix();
			IVertexBuilder vertexBuilder = worldRender.renderTypeTextures.getBufferSource().getBuffer(RenderType.getLines());
			Entity viewEntity = renderInfo.getRenderViewEntity();

			// Get the current projected view.
			Vector3d vector3d = renderInfo.getProjectedView();

			// Push a new transform matrix.
			matrix.push();

			// For all of the harvestable blocks, render the bounding box.
			for (BlockPos pos : extraBlocks) {
				if (Minecraft.getInstance().player.getEntityWorld().getWorldBorder().contains(pos)) {
					worldRender.drawSelectionBox(matrix, vertexBuilder, viewEntity, vector3d.getX(), vector3d.getY(), vector3d.getZ(), pos,
							Minecraft.getInstance().player.getEntityWorld().getBlockState(pos));
				}
			}

			// Pop the added transform matrix.
			matrix.pop();
		}
	}

	private static void renderMultiHarvesteBlockBreakEffect(RenderWorldLastEvent event) {
		// Get the controller. If it is null, return early.
		PlayerController controller = Minecraft.getInstance().playerController;
		if (controller == null) {
			return;
		}

		// Get the local player. If null, return early.
		PlayerEntity player = Minecraft.getInstance().player;
		if (player == null) {
			return;
		}

		// Get the player's held item.
		ItemStack heldItem = player.getHeldItemMainhand();

		// If we're holding a multi harvest tool, render the extra block damage.
		if (!heldItem.isEmpty() && heldItem.getItem() instanceof AbstractMultiHarvestTool) {
			// Raytrace from the player's perspective to see which block they are looking
			// at.
			BlockRayTraceResult raytraceResult = RaytracingUtilities.findPlayerRayTrace(player.getEntityWorld(), player, RayTraceContext.FluidMode.ANY);
			if (raytraceResult.getType() != RayTraceResult.Type.BLOCK) {
				return;
			}

			// Get all the extra blocks that we can mine based on where we are looking.
			List<BlockPos> extraBlocks = ((AbstractMultiHarvestTool) heldItem.getItem()).getMineableExtraBlocks(heldItem, raytraceResult.getPos(), player);

			// If we're currently mining, draw the block damage texture.
			if (controller.getIsHittingBlock()) {
				drawBlockDamageTexture(event.getContext(), event.getMatrixStack(), Minecraft.getInstance().gameRenderer.getActiveRenderInfo(), player.getEntityWorld(), extraBlocks);
			}
		}
	}

	/**
	 * Draws the damaged texture on the given blocks
	 *
	 * @param worldRender   the current world renderer
	 * @param matrixStackIn the matrix stack
	 * @param renderInfo    the current render info from the client
	 * @param world         the active world
	 * @param extraBlocks   the list of blocks
	 */
	@OnlyIn(Dist.CLIENT)
	private static void drawBlockDamageTexture(WorldRenderer worldRender, MatrixStack matrixStackIn, ActiveRenderInfo renderInfo, World world, Iterable<BlockPos> extraBlocks) {
		// Get the current break progress.
		int progress = (int) (getCurrentFocusedBlockDamage() * 10.0F) - 1;

		// If it's less than 0, do nothing.
		if (progress < 0) {
			return;
		}

		// Clamp the progress to a min of 10 (Thank you @Tinkers' Construct)!
		progress = Math.min(progress, 10);

		// Get the block render dispatcher and use it to render the damage effect.
		BlockRendererDispatcher dispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
		IVertexBuilder vertexBuilder = worldRender.renderTypeTextures.getCrumblingBufferSource().getBuffer(ModelBakery.DESTROY_RENDER_TYPES.get(progress));

		// Render the effect for each of the blocks.
		for (BlockPos pos : extraBlocks) {
			matrixStackIn.push();
			// Make sure we transform into projection space.
			matrixStackIn.translate((double) pos.getX() - renderInfo.getProjectedView().x, (double) pos.getY() - renderInfo.getProjectedView().y,
					(double) pos.getZ() - renderInfo.getProjectedView().z);
			IVertexBuilder matrixBuilder = new MatrixApplyingVertexBuilder(vertexBuilder, matrixStackIn.getLast().getMatrix(), matrixStackIn.getLast().getNormal());

			// Render the damage.
			dispatcher.renderBlockDamage(world.getBlockState(pos), pos, world, matrixStackIn, matrixBuilder);
			matrixStackIn.pop();
		}
	}

	// Gets the damage of the current block being targeted by the player.
	protected static float getCurrentFocusedBlockDamage() {
		try {
			// Cache the reflection handle to the private field (considered going ASM
			// here...but this is client side only so, would rather lose a few frames vs
			// introducing incomparability).
			if (currentBlockDamageMP == null) {
				currentBlockDamageMP = ObfuscationReflectionHelper.findField(PlayerController.class, "field_78770_f");
			}
			return currentBlockDamageMP.getFloat(Minecraft.getInstance().playerController);
		} catch (Exception e) {
			LOGGER.error("An error occured when attempting to the break progress for the targeted block!", e);
		}
		return 0.0f;
	}
}
