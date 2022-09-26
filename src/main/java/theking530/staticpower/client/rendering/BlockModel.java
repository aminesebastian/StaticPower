package theking530.staticpower.client.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.inventory.InventoryMenu;
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
		TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(StaticPowerSprites.BLANK_TEXTURE);
		drawPreviewCube(position, scale, tint, matrixStack, sprite, IDENTITY_VECTOR);
	}

	public void drawPreviewCube(Vector3f position, Vector3f scale, Color tint, PoseStack matrixStack, TextureAtlasSprite sprite) {
		drawPreviewCube(position, scale, tint, matrixStack, sprite, IDENTITY_VECTOR);
	}

	public void drawPreviewCube(Vector3f position, Vector3f scale, Color tint, PoseStack matrixStack, TextureAtlasSprite sprite, Vector3D uv) {
		// Draw each block side.
		for (Direction dir : Direction.values()) {
			drawPreviewSide(matrixStack, dir, position, scale, sprite, tint, uv);
		}
	}

	public void drawPreviewSide(PoseStack matrixStack, Direction side, Vector3f position, Vector3f scale, Color tint) {
		TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(StaticPowerSprites.BLANK_TEXTURE);
		drawPreviewSide(matrixStack, side, position, scale, sprite, tint, IDENTITY_VECTOR);
	}

	public void drawPreviewSide(PoseStack matrixStack, Direction side, Vector3f position, Vector3f scale, TextureAtlasSprite sprite, Color tint, Vector3D uv) {
		MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
		VertexConsumer builder = buffer.getBuffer(RenderType.translucentMovingBlock());
		drawPreviewSide(matrixStack, builder, side, position, scale, sprite, tint, uv);
		buffer.endBatch(RenderType.translucentMovingBlock());
	}

	public void drawPreviewCubeGui(Vector3f position, Vector3f scale, Color tint, PoseStack matrixStack) {
		// Bind a blank texture.
		TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(StaticPowerSprites.BLANK_TEXTURE);
		drawPreviewCubeGui(position, scale, tint, matrixStack, sprite, IDENTITY_VECTOR);
	}

	public void drawPreviewCubeGui(Vector3f position, Vector3f scale, Color tint, PoseStack matrixStack, TextureAtlasSprite sprite, Vector3D uv) {
		// Draw each block side.
		for (Direction dir : Direction.values()) {
			drawPreviewSideGui(matrixStack, dir, position, scale, sprite, tint, uv);
		}
	}

	public void drawPreviewSideGui(PoseStack matrixStack, Direction side, Vector3f position, Vector3f scale, TextureAtlasSprite sprite, Color tint, Vector3D uv) {
		RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
		RenderSystem.enableBlend();
		Tesselator tesselator = Tesselator.getInstance();
		BufferBuilder builder = tesselator.getBuilder();
		builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
		drawPreviewSide(matrixStack, builder, side, position, scale, sprite, tint, uv);
		tesselator.end();
		RenderSystem.disableBlend();
	}

	protected void drawPreviewSide(PoseStack matrixStack, VertexConsumer builder, Direction side, Vector3f position, Vector3f scale, TextureAtlasSprite sprite, Color tint,
			Vector3D uv) {
		// Get the vertex builder and set the color.
		builder.color(tint.getRed(), tint.getBlue(), tint.getGreen(), tint.getAlpha());

		// Push a new matrix and set the translation ands scale.
		matrixStack.pushPose();
		matrixStack.translate(position.x(), position.y(), position.z());
		matrixStack.scale(scale.x(), scale.y(), scale.z());

		float maxU = sprite.getU0() + (sprite.getU1() - sprite.getU0()) * uv.getX();
		float maxV = sprite.getV0() + (sprite.getV1() - sprite.getV0()) * uv.getY();
		float topBottomMaxV = sprite.getV0() + (sprite.getV1() - sprite.getV0()) * uv.getZ();

		if (side == Direction.DOWN) {
			builder.vertex(matrixStack.last().pose(), 0.0f, 0.0f, 1.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).uv(maxU, sprite.getV0())
					.uv2(15728880).normal(0, -1, 0).endVertex();
			builder.vertex(matrixStack.last().pose(), 0.0f, 0.0f, 0.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).uv(maxU, topBottomMaxV).uv2(15728880)
					.normal(0, -1, 0).endVertex();
			builder.vertex(matrixStack.last().pose(), 1.0f, 0.0f, 0.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).uv(sprite.getU0(), topBottomMaxV)
					.uv2(15728880).normal(0, -1, 0).endVertex();
			builder.vertex(matrixStack.last().pose(), 1.0f, 0.0f, 1.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).uv(sprite.getU0(), sprite.getV0())
					.uv2(15728880).normal(0, -1, 0).endVertex();
		} else if (side == Direction.UP) {
			builder.vertex(matrixStack.last().pose(), 1.0f, 1.0f, 1.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).uv(maxU, sprite.getV0())
					.uv2(15728880).normal(0, 1, 0).endVertex();
			builder.vertex(matrixStack.last().pose(), 1.0f, 1.0f, 0.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).uv(maxU, topBottomMaxV).uv2(15728880)
					.normal(0, 1, 0).endVertex();
			builder.vertex(matrixStack.last().pose(), 0.0f, 1.0f, 0.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).uv(sprite.getU0(), topBottomMaxV)
					.uv2(15728880).normal(0, 1, 0).endVertex();
			builder.vertex(matrixStack.last().pose(), 0.0f, 1.0f, 1.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).uv(sprite.getU0(), sprite.getV0())
					.uv2(15728880).normal(0, 1, 0).endVertex();
		} else if (side == Direction.NORTH) {
			builder.vertex(matrixStack.last().pose(), 0.0f, 0.0f, 0.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).uv(maxU, sprite.getV0())
					.uv2(15728880).normal(0, 0, -1).endVertex();
			builder.vertex(matrixStack.last().pose(), 0.0f, 1.0f, 0.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).uv(maxU, maxV).uv2(15728880)
					.normal(0, 0, -1).endVertex();
			builder.vertex(matrixStack.last().pose(), 1.0f, 1.0f, 0.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).uv(sprite.getU0(), maxV)
					.uv2(15728880).normal(0, 0, -1).endVertex();
			builder.vertex(matrixStack.last().pose(), 1.0f, 0.0f, 0.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).uv(sprite.getU0(), sprite.getV0())
					.uv2(15728880).normal(0, 0, -1).endVertex();
		} else if (side == Direction.SOUTH) {
			builder.vertex(matrixStack.last().pose(), 0.0f, 1.0f, 1.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).uv(maxU, sprite.getV0())
					.uv2(15728880).normal(0, 0, 1).endVertex();
			builder.vertex(matrixStack.last().pose(), 0.0f, 0.0f, 1.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).uv(maxU, maxV).uv2(15728880)
					.normal(0, 0, 1).endVertex();
			builder.vertex(matrixStack.last().pose(), 1.0f, 0.0f, 1.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).uv(sprite.getU0(), maxV)
					.uv2(15728880).normal(0, 0, 1).endVertex();
			builder.vertex(matrixStack.last().pose(), 1.0f, 1.0f, 1.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).uv(sprite.getU0(), sprite.getV0())
					.uv2(15728880).normal(0, 0, 1).endVertex();
		} else if (side == Direction.WEST) {
			builder.vertex(matrixStack.last().pose(), 0.0f, 0.0f, 1.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).uv(maxU, sprite.getV0())
					.uv2(15728880).normal(-1, 0, 0).endVertex();
			builder.vertex(matrixStack.last().pose(), 0.0f, 1.0f, 1.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).uv(maxU, maxV).uv2(15728880)
					.normal(-1, 0, 0).endVertex();
			builder.vertex(matrixStack.last().pose(), 0.0f, 1.0f, 0.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).uv(sprite.getU0(), maxV)
					.uv2(15728880).normal(-1, 0, 0).endVertex();
			builder.vertex(matrixStack.last().pose(), 0.0f, 0.0f, 0.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).uv(sprite.getU0(), sprite.getV0())
					.uv2(15728880).normal(-1, 0, 0).endVertex();
		} else if (side == Direction.EAST) {
			builder.vertex(matrixStack.last().pose(), 1.0f, 0.0f, 0.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).uv(maxU, sprite.getV0())
					.uv2(15728880).normal(1, 0, 0).endVertex();
			builder.vertex(matrixStack.last().pose(), 1.0f, 1.0f, 0.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).uv(maxU, maxV).uv2(15728880)
					.normal(1, 0, 0).endVertex();
			builder.vertex(matrixStack.last().pose(), 1.0f, 1.0f, 1.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).uv(sprite.getU0(), maxV)
					.uv2(15728880).normal(1, 0, 0).endVertex();
			builder.vertex(matrixStack.last().pose(), 1.0f, 0.0f, 1.0f).color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha()).uv(sprite.getU0(), sprite.getV0())
					.uv2(15728880).normal(1, 0, 0).endVertex();
		}

		// Pop the matrix and finish rendering.
		matrixStack.popPose();
	}
}