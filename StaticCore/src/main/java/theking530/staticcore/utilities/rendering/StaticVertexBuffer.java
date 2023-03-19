package theking530.staticcore.utilities.rendering;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;

public class StaticVertexBuffer {

	private final Tesselator tessellator;
	private final BufferBuilder vertexbuffer;

	protected StaticVertexBuffer() {
		tessellator = Tesselator.getInstance();
		vertexbuffer = tessellator.getBuilder();
	}

	public static StaticVertexBuffer create(VertexFormat.Mode mode, VertexFormat format) {
		StaticVertexBuffer output = new StaticVertexBuffer();
		output.tessellator.getBuilder().begin(mode, format);
		return output;
	}

	public static StaticVertexBuffer quads(VertexFormat format) {
		return create(Mode.QUADS, format);
	}

	public void pos(float x, float y, float z, float u, float v) {
		vertexbuffer.vertex(x, y, z).uv(u, v).endVertex();
	}

	public void pos(double x, double y, double z, double u, double v) {
		vertexbuffer.vertex(x, y, z).uv((float) u, (float) v).endVertex();
	}

	public void pos(PoseStack pose, float x, float y, float z, float u, float v) {
		vertexbuffer.vertex(pose.last().pose(), x, y, z).uv(u, v).endVertex();
	}

	public void pos(PoseStack pose, double x, double y, double z, double u, double v) {
		vertexbuffer.vertex(pose.last().pose(), (float) x, (float) y, (float) z).uv((float) u, (float) v).endVertex();
	}

	public void end() {
		tessellator.end();
	}
}
