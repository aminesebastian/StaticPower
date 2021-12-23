package theking530.staticcore.utilities;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;

public class StaticVertexBuffer {

	public static final Tesselator tessellator = Tesselator.getInstance();
	public static final BufferBuilder vertexbuffer = tessellator.getBuilder();

	public static void pos(float x, float y, float z, float u, float v) {
		vertexbuffer.vertex(x, y, z).uv(u, v).endVertex();
	}

	public static void pos(double x, double y, double z, double u, double v) {
		vertexbuffer.vertex(x, y, z).uv((float) u, (float) v).endVertex();
	}
}
