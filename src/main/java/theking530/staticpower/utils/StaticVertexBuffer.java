package theking530.staticpower.utils;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;

public class StaticVertexBuffer {

	public static final Tessellator tessellator = Tessellator.getInstance();
	public static final VertexBuffer vertexbuffer = tessellator.getBuffer();
    
	public static void pos(float x, float y, float z, float u, float v) {
		vertexbuffer.pos(x, y, z).tex(u, v).endVertex();
	}
	public static void pos(double x, double y, double z, double u, double v) {
		vertexbuffer.pos(x, y, z).tex(u, v).endVertex();
	}
}
