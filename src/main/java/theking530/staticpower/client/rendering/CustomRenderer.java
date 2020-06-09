package theking530.staticpower.client.rendering;

import java.util.HashMap;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import theking530.api.utilities.Color;

public class CustomRenderer {
	private BlockModel model = new BlockModel();
	private static HashMap<Object, DrawCubeRequest> CubeRenderRequests = new HashMap<Object, DrawCubeRequest>();

	public void render(RenderWorldLastEvent event) {
		MatrixStack matrixStack = event.getMatrixStack();

		PlayerEntity player = Minecraft.getInstance().player;
		double x = player.lastTickPosX + (player.getPosX() - player.lastTickPosX) * event.getPartialTicks();
		double y = player.lastTickPosY + (player.getPosY() - player.lastTickPosY) * event.getPartialTicks();
		double z = player.lastTickPosZ + (player.getPosZ() - player.lastTickPosZ) * event.getPartialTicks();

		matrixStack.push();
		matrixStack.translate(-x, -y, -z);

		// Draw the requested cubes.
		for (DrawCubeRequest request : CubeRenderRequests.values()) {
			model.drawPreviewCube(request.Position, request.Scale, request.Color, matrixStack);
		}

		matrixStack.pop();
	}

	public static void addCubeRenderer(Object key, Vector3f position, Vector3f scale, Color color) {
		CubeRenderRequests.put(key, new DrawCubeRequest(position, scale, color));
	}

	public static void addCubeRenderer(Object key, BlockPos position, Vector3f scale, Color color) {
		CubeRenderRequests.put(key, new DrawCubeRequest(position, scale, color));
	}

	public static void removeCubeRenderer(Object key) {
		CubeRenderRequests.remove(key);
	}

	private void drawLine(Vector3f start, Vector3f end, Matrix4f matrix) {
		IRenderTypeBuffer.Impl buffer = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
		IVertexBuilder builder = buffer.getBuffer(RenderType.LINES);
		builder.pos(matrix, start.getX(), start.getY(), start.getZ()).color(1f, 0, 0, 1f).endVertex();
		builder.pos(matrix, end.getX(), end.getY(), end.getZ()).color(1f, 0, 0, 1f).endVertex();
		RenderSystem.disableDepthTest();
		buffer.finish(RenderType.LINES);
	}
}
