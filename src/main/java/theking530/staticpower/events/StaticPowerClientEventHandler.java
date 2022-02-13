package theking530.staticpower.events;

import java.lang.reflect.Field;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.DrawSelectionEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import theking530.staticcore.initialization.StaticCoreRegistry;
import theking530.staticcore.item.ICustomModelSupplier;
import theking530.staticpower.StaticPowerRegistry;
import theking530.staticpower.blocks.interfaces.IRenderLayerProvider;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.client.rendering.CustomRenderer;
import theking530.staticpower.client.rendering.items.FluidCapsuleItemModel.CapsuleColorProvider;
import theking530.staticpower.init.ModItems;
import theking530.staticpower.init.ModKeyBindings;
import theking530.staticpower.items.tools.AbstractMultiHarvestTool;
import theking530.staticpower.utilities.RaytracingUtilities;

@SuppressWarnings("deprecation")
@OnlyIn(Dist.CLIENT)
public class StaticPowerClientEventHandler {
	public static final Logger LOGGER = LogManager.getLogger(StaticPowerClientEventHandler.class);
	private static final CustomRenderer CUSTOM_RENDERER = new CustomRenderer();

	protected static Field currentBlockDamageMP;

	/**
	 * This event is raised by the client setup event (duh).
	 * 
	 * @param event The client setup event.
	 */
	public static void onClientSetupEvent(FMLClientSetupEvent event) {
		// If the block does not request the standard solid render type, set the new
		// render type for the block.
		LOGGER.info("Initializing Block Render Layers!");
		for (Block block : StaticPowerRegistry.BLOCKS) {
			// Check and update the render type as needed.
			if (block instanceof IRenderLayerProvider) {
				IRenderLayerProvider renderLayerProvider = (IRenderLayerProvider) block;
				if (renderLayerProvider.getRenderType() != RenderType.solid()) {
					ItemBlockRenderTypes.setRenderLayer(block, renderLayerProvider.getRenderType());
				}
			}
		}

		LOGGER.info("Initializing Fluid Render Layers!");
		for (Fluid fluid : StaticPowerRegistry.FLUIDS) {
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

		LOGGER.info("Performing Key Bindings!");
		ModKeyBindings.registerBindings(event);

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
				for (BlockState blockState : block.getStateDefinition().getPossibleStates()) {
					if (supplier.hasModelOverride(blockState)) {
						// Get the existing model.
						ModelResourceLocation variantMRL = BlockModelShaper.stateToModelLocation(blockState);
						BakedModel existingModel = event.getModelRegistry().get(variantMRL);
						BakedModel override = supplier.getModelOverride(blockState, existingModel, event);
						if (override != null) {
							event.getModelRegistry().put(variantMRL, override);
						} else {
							LOGGER.error(String.format("Encountered null model override for block: %1$s.", block.getName().getString()));
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
					BakedModel existingModel = event.getModelManager().getModel(modelLocation);

					if (existingModel != null) {
						BakedModel override = supplier.getModelOverride(null, existingModel, event);
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
		if (event.getAtlas().location() == TextureAtlas.LOCATION_BLOCKS) {
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

	public static void onWorldRender(RenderLevelLastEvent event) {
		CUSTOM_RENDERER.render(event);
		renderMultiHarvesteBlockBreakEffect(event);
	}

	/**
	 * Renders the outline on the extra blocks
	 *
	 * @param event the highlight event
	 */
	@OnlyIn(Dist.CLIENT)
	public static void renderMultiHarvestBoundingBoxes(DrawSelectionEvent.HighlightBlock event) {
		// If the player is null, do nothing.
		if (Minecraft.getInstance().player == null) {
			return;
		}

		// Get the held item.
		ItemStack tool = Minecraft.getInstance().player.getMainHandItem();

		// Check to see if the tool is not empty and is an instance of the multi harvest
		// tool.
		if (!tool.isEmpty() && tool.getItem() instanceof AbstractMultiHarvestTool) {
			// If so, get the current rendering info.
			Camera renderInfo = Minecraft.getInstance().gameRenderer.getMainCamera();

			// Get the tool's item and get the blocks that are currently being targeted for
			// harvesting.
			AbstractMultiHarvestTool toolItem = (AbstractMultiHarvestTool) tool.getItem();
			List<BlockPos> extraBlocks = toolItem.getMineableExtraBlocks(tool, event.getTarget().getBlockPos(), Minecraft.getInstance().player);

			// Get the world renderer state.
			LevelRenderer worldRender = event.getLevelRenderer();
			PoseStack matrix = event.getPoseStack();
			VertexConsumer vertexBuilder = worldRender.renderBuffers.bufferSource().getBuffer(RenderType.lines());
			Entity viewEntity = renderInfo.getEntity();

			// Get the current projected view.
			Vec3 vector3d = renderInfo.getPosition();

			// Push a new transform matrix.
			matrix.pushPose();

			// For all of the harvestable blocks, render the bounding box.
			for (BlockPos pos : extraBlocks) {
				if (Minecraft.getInstance().player.getCommandSenderWorld().getWorldBorder().isWithinBounds(pos)) {
					worldRender.renderHitOutline(matrix, vertexBuilder, viewEntity, vector3d.x(), vector3d.y(), vector3d.z(), pos,
							Minecraft.getInstance().player.getCommandSenderWorld().getBlockState(pos));
				}
			}

			// Pop the added transform matrix.
			matrix.popPose();
		}
	}

	private static void renderMultiHarvesteBlockBreakEffect(RenderLevelLastEvent event) {
		// Get the controller. If it is null, return early.
		MultiPlayerGameMode controller = Minecraft.getInstance().gameMode;
		if (controller == null) {
			return;
		}

		// Get the local player. If null, return early.
		Player player = Minecraft.getInstance().player;
		if (player == null) {
			return;
		}

		// Get the player's held item.
		ItemStack heldItem = player.getMainHandItem();

		// If we're holding a multi harvest tool, render the extra block damage.
		if (!heldItem.isEmpty() && heldItem.getItem() instanceof AbstractMultiHarvestTool) {
			// Raytrace from the player's perspective to see which block they are looking
			// at.
			BlockHitResult raytraceResult = RaytracingUtilities.findPlayerRayTrace(player.getCommandSenderWorld(), player, ClipContext.Fluid.ANY);
			if (raytraceResult.getType() != HitResult.Type.BLOCK) {
				return;
			}

			// Get all the extra blocks that we can mine based on where we are looking.
			List<BlockPos> extraBlocks = ((AbstractMultiHarvestTool) heldItem.getItem()).getMineableExtraBlocks(heldItem, raytraceResult.getBlockPos(), player);

			// If we're currently mining, draw the block damage texture.
			if (controller.isDestroying()) {
				drawBlockDamageTexture(event.getLevelRenderer(), event.getPoseStack(), Minecraft.getInstance().gameRenderer.getMainCamera(), player.getCommandSenderWorld(), extraBlocks);
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
	private static void drawBlockDamageTexture(LevelRenderer worldRender, PoseStack matrixStackIn, Camera renderInfo, Level world, Iterable<BlockPos> extraBlocks) {
		// Get the current break progress.
		int progress = (int) (getCurrentFocusedBlockDamage() * 10.0F) - 1;

		// If it's less than 0, do nothing.
		if (progress < 0) {
			return;
		}

		// Clamp the progress to a min of 10 (Thank you @Tinkers' Construct)!
		progress = Math.min(progress, 10);

		// Get the block render dispatcher and use it to render the damage effect.
		BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
		VertexConsumer vertexBuilder = worldRender.renderBuffers.crumblingBufferSource().getBuffer(ModelBakery.DESTROY_TYPES.get(progress));

		// Render the effect for each of the blocks.
		for (BlockPos pos : extraBlocks) {
			matrixStackIn.pushPose();
			// Make sure we transform into projection space.
			matrixStackIn.translate((double) pos.getX() - renderInfo.getPosition().x, (double) pos.getY() - renderInfo.getPosition().y, (double) pos.getZ() - renderInfo.getPosition().z);
			VertexConsumer matrixBuilder = new SheetedDecalTextureGenerator(vertexBuilder, matrixStackIn.last().pose(), matrixStackIn.last().normal());

			// Render the damage.
			dispatcher.renderBreakingTexture(world.getBlockState(pos), pos, world, matrixStackIn, matrixBuilder);
			matrixStackIn.popPose();
		}
	}

	// Gets the damage of the current block being targeted by the player.
	protected static float getCurrentFocusedBlockDamage() {
		try {
			// Cache the reflection handle to the private field (considered going ASM
			// here...but this is client side only so, would rather lose a few frames vs
			// introducing incomparability).
			if (currentBlockDamageMP == null) {
				currentBlockDamageMP = ObfuscationReflectionHelper.findField(MultiPlayerGameMode.class, "destroyProgress");
			}
			return currentBlockDamageMP.getFloat(Minecraft.getInstance().gameMode);
		} catch (Exception e) {
			LOGGER.error("An error occured when attempting to the break progress for the targeted block!", e);
		}
		return 0.0f;
	}
}
