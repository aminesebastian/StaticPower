package theking530.staticpower.client.rendering.blocks;

import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import theking530.common.utilities.Color;

public class ColoredQuad extends BakedQuad {
	private final int[] vertexData;
	private final Color color;

	// Wrapper constructor
	public ColoredQuad(BakedQuad original, Color color) {
		super(original.getVertexData(), original.getTintIndex(), original.getFace(), original.func_187508_a(), original.shouldApplyDiffuseLighting());
		this.vertexData = original.getVertexData();
		this.color = color;
		recolor();
	}

	private void recolor() {
		// First getting the format of the quad.
		VertexFormat format = DefaultVertexFormats.BLOCK;

		// Getting the size of each vertex.
		int size = format.getIntegerSize();

		// Getting the color offset. Dividing by 4 because the offset is specified in
		// bytes.
		int offset = 12 / 4;

		// The color you want to recolor the quad to. Note that the format for the color
		// is ABGR!
		int newColor = color.encodeInInteger();

		// Iterating over the vertices(4 vertices per quad)
		for (int i = 0; i < 4; i++) {
			// Modifying the element of the vertex at [i] at [offset] with the new color.
			this.vertexData[offset + size * i] = newColor;
		}
	}

	// The vertex data getter.
	@Override
	public int[] getVertexData() {
		return this.vertexData;
	}
}