package theking530.staticpower.events;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.DrawSelectionEvent;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogColors;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogDensity;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent.Stage;
import net.minecraftforge.client.event.ScreenEvent.BackgroundDrawnEvent;
import net.minecraftforge.client.event.ScreenEvent.DrawScreenEvent;
import net.minecraftforge.client.event.ScreenEvent.InitScreenEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.utilities.Color;
import theking530.staticpower.StaticPower;
import theking530.staticpower.client.gui.StaticPowerExtensionGui;
import theking530.staticpower.client.gui.StaticPowerHUDElement;
import theking530.staticpower.client.rendering.CustomRenderer;
import theking530.staticpower.fluid.AbstractStaticPowerFluid;
import theking530.staticpower.items.tools.AbstractMultiHarvestTool;
import theking530.staticpower.utilities.PlayerUtilities;
import theking530.staticpower.utilities.RaytracingUtilities;

/**
 * Any client only event handling is performed here.
 * 
 * @author
 *
 */
@SuppressWarnings("resource")
@Mod.EventBusSubscriber(modid = StaticPower.MOD_ID, bus = EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class StaticPowerForgeBusClient {
	protected static Field currentBlockDamageMP;
	public static final Logger LOGGER = LogManager.getLogger(StaticPowerForgeBusClient.class);
	private static final CustomRenderer CUSTOM_RENDERER = new CustomRenderer();
	private static final List<StaticPowerExtensionGui> UI_EXTENSIONS = new ArrayList<>();
	private static final List<StaticPowerExtensionGui> BOUND_UI_EXTENSIONS = new ArrayList<>();
	private static final List<StaticPowerHUDElement> HUD_ELEMENTS = new ArrayList<>();

	public static void addHUDElement(StaticPowerHUDElement element) {
		HUD_ELEMENTS.add(element);
	}

	@SubscribeEvent
	public static void render(RenderLevelStageEvent event) {
		CUSTOM_RENDERER.render(event);
		renderMultiHarvesteBlockBreakEffect(event);
	}

	@SubscribeEvent(priority = EventPriority.NORMAL)
	public static void onInitScreenEvent(InitScreenEvent event) {
		BOUND_UI_EXTENSIONS.clear();
		for (StaticPowerExtensionGui gui : UI_EXTENSIONS) {
			boolean shouldAttach = gui.shouldAttach(event);
			if (shouldAttach) {
				event.addListener(gui);
				BOUND_UI_EXTENSIONS.add(gui);
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.NORMAL)
	public static void onDrawScreen(DrawScreenEvent.Post event) {
		for (StaticPowerExtensionGui gui : UI_EXTENSIONS) {
			gui.tick();
			gui.render(event.getPoseStack(), event.getMouseX(), event.getMouseY(), event.getPartialTicks());
		}
	}

	@SubscribeEvent(priority = EventPriority.NORMAL)
	public static void onDrawBehindScreen(BackgroundDrawnEvent event) {
		for (StaticPowerExtensionGui gui : UI_EXTENSIONS) {
			gui.renderBackground(event.getPoseStack());
		}
	}

	@SubscribeEvent(priority = EventPriority.NORMAL)
	public static void onDrawHUD(RenderGameOverlayEvent.Post event) {
		if (event.getType() == ElementType.ALL) {
			for (StaticPowerHUDElement gui : HUD_ELEMENTS) {
				gui.setCurrentWindow(event.getWindow());
				gui.tick();
				gui.renderBackground(event.getMatrixStack());
				gui.render(event.getMatrixStack(), 0, 0, event.getPartialTicks());
			}

			// Draw the fluid overlay.
			Fluid fluid = PlayerUtilities.getFluidAtEyeLevel();
			if (fluid instanceof AbstractStaticPowerFluid) {
				AbstractStaticPowerFluid abstractFluid = (AbstractStaticPowerFluid) fluid;
				GuiDrawUtilities.drawScreenOverlay(fluid.getAttributes().getOverlayTexture(), abstractFluid.getOverlayColor(), 0.75f, 512, 512);
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
	public static void onEvent(FogDensity event) {

	}

	@SubscribeEvent
	public static void onRenderFog(EntityViewRenderEvent.RenderFogEvent event) {
		Fluid fluid = PlayerUtilities.getFluidAtEyeLevel();
		if (fluid instanceof AbstractStaticPowerFluid) {
			RenderSystem.setShaderFogStart(-8.0f);
			RenderSystem.setShaderFogEnd(event.getFarPlaneDistance() * 0.05f);
		}
	}

	@SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
	public static void onEvent(FogColors event) {
		Fluid fluid = PlayerUtilities.getFluidAtEyeLevel();

		if (fluid instanceof AbstractStaticPowerFluid) {
			AbstractStaticPowerFluid abstractFluid = (AbstractStaticPowerFluid) fluid;
			Color color = abstractFluid.getFogColor();
			event.setRed(color.getRed());
			event.setGreen(color.getGreen());
			event.setBlue(color.getBlue());
		}
	}

	/**
	 * Renders the outline on the extra blocks
	 *
	 * @param event the highlight event
	 */
	@SubscribeEvent
	public static void renderBlockHighlights(DrawSelectionEvent.HighlightBlock event) {
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

	private static void renderMultiHarvesteBlockBreakEffect(RenderLevelStageEvent event) {
		if (event.getStage() == Stage.AFTER_PARTICLES) {
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
					drawBlockDamageTexture(event.getLevelRenderer(), event.getPoseStack(), Minecraft.getInstance().gameRenderer.getMainCamera(), player.getCommandSenderWorld(),
							extraBlocks);
				}
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
	@SuppressWarnings("deprecation")
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
			matrixStackIn.translate((double) pos.getX() - renderInfo.getPosition().x, (double) pos.getY() - renderInfo.getPosition().y,
					(double) pos.getZ() - renderInfo.getPosition().z);
			VertexConsumer matrixBuilder = new SheetedDecalTextureGenerator(vertexBuilder, matrixStackIn.last().pose(), matrixStackIn.last().normal());

			// Render the damage.
			dispatcher.renderBreakingTexture(world.getBlockState(pos), pos, world, matrixStackIn, matrixBuilder);
			matrixStackIn.popPose();
		}
	}

	// Gets the damage of the current block being targeted by the player.
	protected static float getCurrentFocusedBlockDamage() {
		try {
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
