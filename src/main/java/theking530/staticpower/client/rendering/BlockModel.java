package theking530.staticpower.client.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticpower.client.StaticPowerSprites;

@OnlyIn(Dist.CLIENT)
public class BlockModel {
	@SuppressWarnings("unused")
	private static final Color DEFAULT_COLOR = new Color(1.0f, 1.0f, 1.0f);
	private static final Vector3D IDENTITY_VECTOR = new Vector3D(1.0f, 1.0f, 1.0f);

	public void drawPreviewCube(Vector3f position, Vector3f scale, Color tint, PoseStack matrixStack) {
		// Bind a blank texture.
		@SuppressWarnings("deprecation")
		TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(StaticPowerSprites.BLANK_TEXTURE);
		drawPreviewCube(position, scale, tint, matrixStack, sprite, IDENTITY_VECTOR);
	}

	public void drawPreviewCube(Vector3f position, Vector3f scale, Color tint, PoseStack matrixStack, TextureAtlasSprite sprite) {
		drawPreviewCube(position, scale, tint, matrixStack, sprite, IDENTITY_VECTOR);
	}

	public void drawPreviewCube(Vector3f position, Vector3f scale, Color tint, PoseStack matrixStack, TextureAtlasSprite sprite, Vector3D uv) {
		// Get the buffer.
		MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();

		// Push a new matrix and set the translation ands scale.
		matrixStack.pushPose();
		matrixStack.translate(position.x(), position.y(), position.z());
		matrixStack.scale(scale.x(), scale.y(), scale.z());

		// Get the vertex builder and set the color.
		VertexConsumer builder = buffer.getBuffer(RenderType.translucent());
		builder.color(tint.getRed(), tint.getBlue(), tint.getGreen(), tint.getAlpha());

		// Draw each block side.
		for (Direction dir : Direction.values()) {
			drawPreviewSide(dir, sprite, tint, matrixStack.last().pose(), builder, uv);
		}

		// Pop the matrix and finish rendering.
		matrixStack.popPose();
		buffer.endBatch(RenderType.translucent());
	}

	@SuppressWarnings("deprecation")
	protected void drawPreviewSide(Direction side, Color tint, Matrix4f matrix, VertexConsumer builder) {
		TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(StaticPowerSprites.BLANK_TEXTURE);
		drawPreviewSide(side, sprite, tint, matrix, builder, IDENTITY_VECTOR);
	}

	protected void drawPreviewSide(Direction side, TextureAtlasSprite sprite, Color tint, Matrix4f matrix, VertexConsumer builder) {
		drawPreviewSide(side, sprite, tint, matrix, builder, IDENTITY_VECTOR);
	}

	protected void drawPreviewSide(Direction side, TextureAtlasSprite sprite, Color tint, Matrix4f matrix, VertexConsumer builder, Vector3D uv) {
		float maxU = sprite.getU0() + (sprite.getU1() - sprite.getU0()) * uv.getX();
		float maxV = sprite.getV0() + (sprite.getV1() - sprite.getV0()) * uv.getY();
		float topBottomMaxV = sprite.getV0() + (sprite.getV1() - sprite.getV0()) * uv.getZ();

		if (side == Direction.DOWN) {
			builder.vertex(matrix, 0.0f, 0.0f, 1.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).uv(maxU, sprite.getV0()).uv2(15728880).normal(0, -1, 0)
					.endVertex();
			builder.vertex(matrix, 0.0f, 0.0f, 0.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).uv(maxU, topBottomMaxV).uv2(15728880).normal(0, -1, 0)
					.endVertex();
			builder.vertex(matrix, 1.0f, 0.0f, 0.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).uv(sprite.getU0(), topBottomMaxV).uv2(15728880)
					.normal(0, -1, 0).endVertex();
			builder.vertex(matrix, 1.0f, 0.0f, 1.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).uv(sprite.getU0(), sprite.getV0()).uv2(15728880)
					.normal(0, -1, 0).endVertex();
		} else if (side == Direction.UP) {
			builder.vertex(matrix, 1.0f, 1.0f, 1.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).uv(maxU, sprite.getV0()).uv2(15728880).normal(0, 1, 0)
					.endVertex();
			builder.vertex(matrix, 1.0f, 1.0f, 0.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).uv(maxU, topBottomMaxV).uv2(15728880).normal(0, 1, 0)
					.endVertex();
			builder.vertex(matrix, 0.0f, 1.0f, 0.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).uv(sprite.getU0(), topBottomMaxV).uv2(15728880)
					.normal(0, 1, 0).endVertex();
			builder.vertex(matrix, 0.0f, 1.0f, 1.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).uv(sprite.getU0(), sprite.getV0()).uv2(15728880)
					.normal(0, 1, 0).endVertex();
		} else if (side == Direction.NORTH) {
			builder.vertex(matrix, 0.0f, 0.0f, 0.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).uv(maxU, sprite.getV0()).uv2(15728880).normal(0, 0, -1)
					.endVertex();
			builder.vertex(matrix, 0.0f, 1.0f, 0.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).uv(maxU, maxV).uv2(15728880).normal(0, 0, -1).endVertex();
			builder.vertex(matrix, 1.0f, 1.0f, 0.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).uv(sprite.getU0(), maxV).uv2(15728880).normal(0, 0, -1)
					.endVertex();
			builder.vertex(matrix, 1.0f, 0.0f, 0.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).uv(sprite.getU0(), sprite.getV0()).uv2(15728880)
					.normal(0, 0, -1).endVertex();
		} else if (side == Direction.SOUTH) {
			builder.vertex(matrix, 0.0f, 1.0f, 1.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).uv(maxU, sprite.getV0()).uv2(15728880).normal(0, 0, 1)
					.endVertex();
			builder.vertex(matrix, 0.0f, 0.0f, 1.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).uv(maxU, maxV).uv2(15728880).normal(0, 0, 1).endVertex();
			builder.vertex(matrix, 1.0f, 0.0f, 1.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).uv(sprite.getU0(), maxV).uv2(15728880).normal(0, 0, 1)
					.endVertex();
			builder.vertex(matrix, 1.0f, 1.0f, 1.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).uv(sprite.getU0(), sprite.getV0()).uv2(15728880)
					.normal(0, 0, 1).endVertex();
		} else if (side == Direction.WEST) {
			builder.vertex(matrix, 0.0f, 0.0f, 1.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).uv(maxU, sprite.getV0()).uv2(15728880).normal(-1, 0, 0)
					.endVertex();
			builder.vertex(matrix, 0.0f, 1.0f, 1.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).uv(maxU, maxV).uv2(15728880).normal(-1, 0, 0).endVertex();
			builder.vertex(matrix, 0.0f, 1.0f, 0.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).uv(sprite.getU0(), maxV).uv2(15728880).normal(-1, 0, 0)
					.endVertex();
			builder.vertex(matrix, 0.0f, 0.0f, 0.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).uv(sprite.getU0(), sprite.getV0()).uv2(15728880)
					.normal(-1, 0, 0).endVertex();
		} else if (side == Direction.EAST) {
			builder.vertex(matrix, 1.0f, 0.0f, 0.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).uv(maxU, sprite.getV0()).uv2(15728880).normal(1, 0, 0)
					.endVertex();
			builder.vertex(matrix, 1.0f, 1.0f, 0.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).uv(maxU, maxV).uv2(15728880).normal(1, 0, 0).endVertex();
			builder.vertex(matrix, 1.0f, 1.0f, 1.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).uv(sprite.getU0(), maxV).uv2(15728880).normal(1, 0, 0)
					.endVertex();
			builder.vertex(matrix, 1.0f, 0.0f, 1.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).uv(sprite.getU0(), sprite.getV0()).uv2(15728880)
					.normal(1, 0, 0).endVertex();
		}
	}
}