package theking530.staticpower.client.rendering;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import com.mojang.math.Matrix4f;
import net.minecraft.world.phys.Vec3;
import com.mojang.math.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.model.animation.Animation;
import theking530.staticcore.utilities.Color;

@OnlyIn(Dist.CLIENT)
public class CustomRenderer {
	private BlockModel model = new BlockModel();
	private static HashMap<BlockEntity, HashMap<String, DrawCubeRequest>> CubeRenderRequests = new HashMap<BlockEntity, HashMap<String, DrawCubeRequest>>();
	private static float lastRenderTime = 0.0f;
	private static float deltaTime = 0.0f;

	public void render(RenderWorldLastEvent event) {
		// Get the current matrix stack.
		PoseStack matrixStack = event.getMatrixStack();

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
		}

		// Purge the old requests.
		for (BlockEntity te : teEntriesToRemove) {
			CubeRenderRequests.remove(te);
		}

		// Pop our stack entry.
		matrixStack.popPose();

		// Update the delta time.
		float currentTime = Animation.getWorldTime(Minecraft.getInstance().level, event.getPartialTicks());
		deltaTime = currentTime - lastRenderTime;
		lastRenderTime = currentTime;
	}

	public static float getDeltaTime() {
		return deltaTime;
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
