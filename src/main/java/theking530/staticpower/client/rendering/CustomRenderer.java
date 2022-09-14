package theking530.staticpower.client.rendering;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent.Stage;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.Vector3D;

@OnlyIn(Dist.CLIENT)
public class CustomRenderer {
	private BlockModel model = new BlockModel();
	private static HashMap<BlockEntity, HashMap<String, DrawCubeRequest>> CubeRenderRequests = new HashMap<BlockEntity, HashMap<String, DrawCubeRequest>>();

	public void render(RenderLevelStageEvent event) {
		if (event.getStage() == Stage.AFTER_TRANSLUCENT_BLOCKS) {
			// Get the current matrix stack.
			PoseStack matrixStack = event.getPoseStack();

			// Start our own stack entry and project us into world space.
			matrixStack.pushPose();
			Vec3 projectedView = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
			matrixStack.translate(-projectedView.x, -projectedView.y, -projectedView.z);

			// Transform so we don't z-fight.
			matrixStack.scale(1.0f, 1.001f, 1.0f);
			matrixStack.translate(0.0f, -0.0042f, 0.0f);

			// Prepare a list of entries to remove if the te has been removed from the
			// world.
			List<BlockEntity> teEntriesToRemove = new LinkedList<BlockEntity>();

			// Draw the requested cubes.
			for (BlockEntity te : CubeRenderRequests.keySet()) {
				// If the tile entity is not valid, add it to the list of entries to remove.
				if (te == null || te.isRemoved()) {
					teEntriesToRemove.add(te);
					continue;
				}

				// Draw the request.
				for (DrawCubeRequest request : CubeRenderRequests.get(te).values()) {
					model.drawPreviewCube(request.Position, request.Scale, request.Color, matrixStack);
				}

				RenderSystem.setShader(GameRenderer::getRendertypeLinesShader);
				RenderSystem.enableBlend();
				RenderSystem.disableCull();
				RenderSystem.lineWidth(10);
				Vector3D start = new Vector3D(te.getBlockPos().getX() + 0.5f, te.getBlockPos().getY() + 1f, te.getBlockPos().getZ() + 0.5f);
				Vector3D end = new Vector3D(0, te.getBlockPos().getY() + 1f, te.getBlockPos().getZ() + 0.5f);
				Color startcolor = Color.BLUE;
				Color endColor = Color.RED;
				Vector3D normal = end.copy().subtract(start).normalize();
				
				for (int i = 0; i < 1000; i++) {
					Tesselator tessellator = Tesselator.getInstance();
					BufferBuilder bufferbuilder = tessellator.getBuilder();
					bufferbuilder.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR_NORMAL);
					bufferbuilder.vertex(matrixStack.last().pose(), start.getX(), start.getY(), start.getZ())
							.color(startcolor.getRed(), startcolor.getGreen(), startcolor.getBlue(), startcolor.getAlpha()).normal(normal.getX(), normal.getY(), 0).endVertex();
					bufferbuilder.vertex(matrixStack.last().pose(), end.getX(), end.getY(), end.getZ())
							.color(endColor.getRed(), endColor.getGreen(), endColor.getBlue(), endColor.getAlpha()).normal(normal.getX(), normal.getY(), 0).endVertex();
					tessellator.end();
				}
				RenderSystem.enableCull();

			}

			// Purge the old requests.
			for (BlockEntity te : teEntriesToRemove) {
				CubeRenderRequests.remove(te);
			}

			// Pop our stack entry.
			matrixStack.popPose();
		}
	}

	public static float getDeltaTime() {
		return Minecraft.getInstance().getDeltaFrameTime();
	}

	public static void addCubeRenderer(BlockEntity tileEntity, String key, Vector3f position, Vector3f scale, Color color) {
		if (!CubeRenderRequests.containsKey(tileEntity)) {
			CubeRenderRequests.put(tileEntity, new HashMap<String, DrawCubeRequest>());
		}
		CubeRenderRequests.get(tileEntity).put(key, new DrawCubeRequest(position, scale, color));
	}

	public static void addCubeRenderer(BlockEntity tileEntity, String key, BlockPos position, Vector3f scale, Color color) {
		addCubeRenderer(tileEntity, key, new Vector3f(position.getX(), position.getY(), position.getZ()), scale, color);
	}

	public static void removeAllRequestsForTileEntity(BlockEntity tileEntity) {
		CubeRenderRequests.remove(tileEntity);
	}

	public static void removeCubeRenderer(BlockEntity tileEntity, String key) {
		while (CubeRenderRequests.containsKey(tileEntity)) {
			CubeRenderRequests.get(tileEntity).remove(key);
			if (CubeRenderRequests.get(tileEntity).isEmpty()) {
				CubeRenderRequests.remove(tileEntity);
			}
		}
	}

	@SuppressWarnings("unused")
	private void drawLine(Vector3f start, Vector3f end, Matrix4f matrix) {
		MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
		VertexConsumer builder = buffer.getBuffer(RenderType.LINES);
		builder.vertex(matrix, start.x(), start.y(), start.z()).color(1f, 0, 0, 1f).endVertex();
		builder.vertex(matrix, end.x(), end.y(), end.z()).color(1f, 0, 0, 1f).endVertex();
		RenderSystem.disableDepthTest();
		buffer.endBatch(RenderType.LINES);
	}
}
