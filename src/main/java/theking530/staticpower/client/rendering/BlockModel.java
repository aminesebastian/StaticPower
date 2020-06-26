package theking530.staticpower.client.rendering;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import theking530.api.utilities.Color;
import theking530.api.utilities.Vector2D;
import theking530.staticpower.client.StaticPowerSprites;

public class BlockModel {
	@SuppressWarnings("unused")
	private static final Color DEFAULT_COLOR = new Color(1.0f, 1.0f, 1.0f);
	private static final Vector2D IDENTITY_VECTOR = new Vector2D(1.0f, 1.0f);

	public void drawPreviewCube(Vector3f position, Vector3f scale, Color tint, MatrixStack matrixStack) {
		// Bind a blank texture.
		@SuppressWarnings("deprecation")
		TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasSpriteGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(StaticPowerSprites.BLANK_TEXTURE);
		drawPreviewCube(position, scale, tint, matrixStack, sprite, IDENTITY_VECTOR);
	}

	public void drawPreviewCube(Vector3f position, Vector3f scale, Color tint, MatrixStack matrixStack, TextureAtlasSprite sprite) {
		drawPreviewCube(position, scale, tint, matrixStack, sprite, IDENTITY_VECTOR);
	}

	public void drawPreviewCube(Vector3f position, Vector3f scale, Color tint, MatrixStack matrixStack, TextureAtlasSprite sprite, Vector2D uv) {
		// Get the buffer.
		IRenderTypeBuffer.Impl buffer = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();

		// Push a new matrix and set the translation ands scale.
		matrixStack.push();
		matrixStack.translate(position.getX(), position.getY(), position.getZ());
		matrixStack.scale(scale.getX(), scale.getY(), scale.getZ());

		// Get the vertex builder and set the color.
		IVertexBuilder builder = buffer.getBuffer(RenderType.getTranslucent());
		builder.color(tint.getRed(), tint.getBlue(), tint.getGreen(), tint.getAlpha());

		// Draw each block side.
		for (Direction dir : Direction.values()) {
			drawPreviewSide(dir, sprite, tint, matrixStack.getLast().getMatrix(), builder, uv);
		}

		// Pop the matrix and finish rendering.
		matrixStack.pop();
		buffer.finish(RenderType.getTranslucent());
	}

	@SuppressWarnings("deprecation")
	protected void drawPreviewSide(Direction side, Color tint, Matrix4f matrix, IVertexBuilder builder) {
		TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasSpriteGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(StaticPowerSprites.BLANK_TEXTURE);
		drawPreviewSide(side, sprite, tint, matrix, builder, IDENTITY_VECTOR);
	}

	protected void drawPreviewSide(Direction side, TextureAtlasSprite sprite, Color tint, Matrix4f matrix, IVertexBuilder builder) {
		drawPreviewSide(side, sprite, tint, matrix, builder, IDENTITY_VECTOR);
	}

	protected void drawPreviewSide(Direction side, TextureAtlasSprite sprite, Color tint, Matrix4f matrix, IVertexBuilder builder, Vector2D uv) {
		float maxU = sprite.getMinU() + (sprite.getMaxU() - sprite.getMinU()) * uv.getX();
		float maxV = sprite.getMinV() + (sprite.getMaxV() - sprite.getMinV()) * uv.getY();

		if (side == Direction.DOWN) {
			builder.pos(matrix, 0.0f, 0.0f, 1.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).tex(maxU, sprite.getMinV()).lightmap(15728880).normal(0, -1, 0).endVertex();
			builder.pos(matrix, 0.0f, 0.0f, 0.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).tex(maxU, maxV).lightmap(15728880).normal(0, -1, 0).endVertex();
			builder.pos(matrix, 1.0f, 0.0f, 0.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).tex(sprite.getMinU(), maxV).lightmap(15728880).normal(0, -1, 0).endVertex();
			builder.pos(matrix, 1.0f, 0.0f, 1.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).tex(sprite.getMinU(), sprite.getMinV()).lightmap(15728880).normal(0, -1, 0).endVertex();
		} else if (side == Direction.UP) {
			builder.pos(matrix, 1.0f, 1.0f, 1.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).tex(maxU, sprite.getMinV()).lightmap(15728880).normal(0, 1, 0).endVertex();
			builder.pos(matrix, 1.0f, 1.0f, 0.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).tex(maxU, maxV).lightmap(15728880).normal(0, 1, 0).endVertex();
			builder.pos(matrix, 0.0f, 1.0f, 0.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).tex(sprite.getMinU(), maxV).lightmap(15728880).normal(0, 1, 0).endVertex();
			builder.pos(matrix, 0.0f, 1.0f, 1.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).tex(sprite.getMinU(), sprite.getMinV()).lightmap(15728880).normal(0, 1, 0).endVertex();
		} else if (side == Direction.NORTH) {
			builder.pos(matrix, 0.0f, 0.0f, 0.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).tex(maxU, sprite.getMinV()).lightmap(15728880).normal(0, 0, -1).endVertex();
			builder.pos(matrix, 0.0f, 1.0f, 0.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).tex(maxU, maxV).lightmap(15728880).normal(0, 0, -1).endVertex();
			builder.pos(matrix, 1.0f, 1.0f, 0.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).tex(sprite.getMinU(), maxV).lightmap(15728880).normal(0, 0, -1).endVertex();
			builder.pos(matrix, 1.0f, 0.0f, 0.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).tex(sprite.getMinU(), sprite.getMinV()).lightmap(15728880).normal(0, 0, -1).endVertex();
		} else if (side == Direction.SOUTH) {
			builder.pos(matrix, 0.0f, 1.0f, 1.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).tex(maxU, sprite.getMinV()).lightmap(15728880).normal(0, 0, 1).endVertex();
			builder.pos(matrix, 0.0f, 0.0f, 1.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).tex(maxU, maxV).lightmap(15728880).normal(0, 0, 1).endVertex();
			builder.pos(matrix, 1.0f, 0.0f, 1.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).tex(sprite.getMinU(), maxV).lightmap(15728880).normal(0, 0, 1).endVertex();
			builder.pos(matrix, 1.0f, 1.0f, 1.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).tex(sprite.getMinU(), sprite.getMinV()).lightmap(15728880).normal(0, 0, 1).endVertex();
		} else if (side == Direction.WEST) {
			builder.pos(matrix, 0.0f, 0.0f, 1.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).tex(maxU, sprite.getMinV()).lightmap(15728880).normal(-1, 0, 0).endVertex();
			builder.pos(matrix, 0.0f, 1.0f, 1.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).tex(maxU, maxV).lightmap(15728880).normal(-1, 0, 0).endVertex();
			builder.pos(matrix, 0.0f, 1.0f, 0.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).tex(sprite.getMinU(), maxV).lightmap(15728880).normal(-1, 0, 0).endVertex();
			builder.pos(matrix, 0.0f, 0.0f, 0.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).tex(sprite.getMinU(), sprite.getMinV()).lightmap(15728880).normal(-1, 0, 0).endVertex();
		} else if (side == Direction.EAST) {
			builder.pos(matrix, 1.0f, 0.0f, 0.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).tex(maxU, sprite.getMinV()).lightmap(15728880).normal(1, 0, 0).endVertex();
			builder.pos(matrix, 1.0f, 1.0f, 0.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).tex(maxU, maxV).lightmap(15728880).normal(1, 0, 0).endVertex();
			builder.pos(matrix, 1.0f, 1.0f, 1.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).tex(sprite.getMinU(), maxV).lightmap(15728880).normal(1, 0, 0).endVertex();
			builder.pos(matrix, 1.0f, 0.0f, 1.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).tex(sprite.getMinU(), sprite.getMinV()).lightmap(15728880).normal(1, 0, 0).endVertex();
		}
	}
}