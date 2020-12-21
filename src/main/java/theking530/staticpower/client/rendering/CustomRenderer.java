package theking530.staticpower.client.rendering;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import theking530.staticcore.utilities.Color;

@OnlyIn(Dist.CLIENT)
public class CustomRenderer {
	private BlockModel model = new BlockModel();
	private static HashMap<TileEntity, HashMap<String, DrawCubeRequest>> CubeRenderRequests = new HashMap<TileEntity, HashMap<String, DrawCubeRequest>>();

	public void render(RenderWorldLastEvent event) {
		// Get the current matrix stack.
		MatrixStack matrixStack = event.getMatrixStack();

		// Start our own stack entry and project us into world space.
		matrixStack.push();
		Vector3d projectedView = Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();
		matrixStack.translate(-projectedView.x, -projectedView.y, -projectedView.z);

		// Transform so we don't z-fight.
		matrixStack.scale(1.0f, 1.001f, 1.0f);
		matrixStack.translate(0.0f, -0.0042f, 0.0f);

		// Prepare a list of entries to remove if the te has been removed from the
		// world.
		List<TileEntity> teEntriesToRemove = new LinkedList<TileEntity>();

		// Draw the requested cubes.
		for (TileEntity te : CubeRenderRequests.keySet()) {
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
		for (TileEntity te : teEntriesToRemove) {
			CubeRenderRequests.remove(te);
		}

		// Pop our stack entry.
		matrixStack.pop();
	}

	public static void addCubeRenderer(TileEntity tileEntity, String key, Vector3f position, Vector3f scale, Color color) {
		if (!CubeRenderRequests.containsKey(tileEntity)) {
			CubeRenderRequests.put(tileEntity, new HashMap<String, DrawCubeRequest>());
		}
		CubeRenderRequests.get(tileEntity).put(key, new DrawCubeRequest(position, scale, color));
	}

	public static void addCubeRenderer(TileEntity tileEntity, String key, BlockPos position, Vector3f scale, Color color) {
		addCubeRenderer(tileEntity, key, new Vector3f(position.getX(), position.getY(), position.getZ()), scale, color);
	}

	public static void removeAllRequestsForTileEntity(TileEntity tileEntity) {
		CubeRenderRequests.remove(tileEntity);
	}

	public static void removeCubeRenderer(TileEntity tileEntity, String key) {
		while (CubeRenderRequests.containsKey(tileEntity)) {
			CubeRenderRequests.get(tileEntity).remove(key);
			if (CubeRenderRequests.get(tileEntity).isEmpty()) {
				CubeRenderRequests.remove(tileEntity);
			}
		}
	}

	@SuppressWarnings("unused")
	private void drawLine(Vector3f start, Vector3f end, Matrix4f matrix) {
		IRenderTypeBuffer.Impl buffer = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
		IVertexBuilder builder = buffer.getBuffer(RenderType.LINES);
		builder.pos(matrix, start.getX(), start.getY(), start.getZ()).color(1f, 0, 0, 1f).endVertex();
		builder.pos(matrix, end.getX(), end.getY(), end.getZ()).color(1f, 0, 0, 1f).endVertex();
		RenderSystem.disableDepthTest();
		buffer.finish(RenderType.LINES);
	}
}
