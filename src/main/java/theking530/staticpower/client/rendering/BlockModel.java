package theking530.staticpower.client.rendering;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

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
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.utilities.SDColor;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticpower.client.StaticPowerSprites;

@OnlyIn(Dist.CLIENT)
public class BlockModel {
	@SuppressWarnings("unused")
	private static final SDColor DEFAULT_COLOR = new SDColor(1.0f, 1.0f, 1.0f);
	private static final Vector3D ONE_VECTOR = new Vector3D(1.0f, 1.0f, 1.0f);
	private static final Set<Direction> ALL_SIDES = new HashSet<>(Arrays.asList(Direction.values()));

	public static void drawCube(PoseStack matrixStack, MultiBufferSource buffer, Vector3f position, Vector3f scale, SDColor tint) {
		// Bind a blank texture.
		TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(StaticPowerSprites.BLANK_TEXTURE);
		drawCube(matrixStack, buffer, position, scale, tint, sprite, ONE_VECTOR);
	}

	public static void drawCube(PoseStack matrixStack, MultiBufferSource buffer, Vector3f position, Vector3f scale, SDColor tint, TextureAtlasSprite sprite) {
		drawCube(matrixStack, buffer, position, scale, tint, sprite, ONE_VECTOR);
	}

	public static void drawCube(PoseStack matrixStack, MultiBufferSource buffer, Vector3f position, Vector3f scale, SDColor tint, TextureAtlasSprite sprite, Vector3D uv) {
		drawCube(matrixStack, buffer, position, scale, tint, sprite, uv, null);
	}

	public static void drawCube(PoseStack matrixStack, MultiBufferSource buffer, Vector3f position, Vector3f scale, SDColor tint, TextureAtlasSprite sprite, Vector3D uv,
			BlockPos lightingPos) {
		VertexConsumer builder = buffer.getBuffer(RenderType.translucentMovingBlock());
		drawCube(matrixStack, builder, ALL_SIDES, lightingPos, position, scale, sprite, tint, uv);
	}

	public static void drawCubeInWorld(PoseStack matrixStack, Vector3f position, Vector3f scale, SDColor tint) {
		// Bind a blank texture.
		TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(StaticPowerSprites.BLANK_TEXTURE);
		drawCubeInWorld(matrixStack, position, scale, tint, sprite, ONE_VECTOR);
	}

	public static void drawCubeInWorld(PoseStack matrixStack, Vector3f position, Vector3f scale, SDColor tint, TextureAtlasSprite sprite) {
		drawCubeInWorld(matrixStack, position, scale, tint, sprite, ONE_VECTOR);
	}

	public static void drawCubeInWorld(PoseStack matrixStack, Vector3f position, Vector3f scale, SDColor tint, TextureAtlasSprite sprite, Vector3D uv) {
		drawCubeInWorld(matrixStack, position, scale, tint, sprite, uv, null);
	}

	public static void drawCubeInWorld(PoseStack matrixStack, Vector3f position, Vector3f scale, SDColor tint, TextureAtlasSprite sprite, Vector3D uv, BlockPos lightingPos) {
		MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
		VertexConsumer builder = buffer.getBuffer(RenderType.translucentMovingBlock());
		drawCube(matrixStack, builder, ALL_SIDES, lightingPos, position, scale, sprite, tint, uv);
		buffer.endBatch(RenderType.translucentMovingBlock());
	}

	public static void drawCubeInGui(PoseStack matrixStack, Vector3f position, Vector3f scale, SDColor tint) {
		// Bind a blank texture.
		TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(StaticPowerSprites.BLANK_TEXTURE);
		drawCubeInGui(matrixStack, position, scale, tint, sprite, ONE_VECTOR);
	}

	public static void drawCubeInGui(PoseStack matrixStack, Vector3f position, Vector3f scale, SDColor tint, TextureAtlasSprite sprite, Vector3D uv) {
		BlockModel.drawCubeInGui(matrixStack, position, scale, tint, sprite, uv, null);
	}

	public static void drawCubeInGui(PoseStack matrixStack, Vector3f position, Vector3f scale, SDColor tint, TextureAtlasSprite sprite, Vector3D uv, BlockPos lightingPos) {
		RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
		RenderSystem.enableBlend();
		Tesselator tesselator = Tesselator.getInstance();
		BufferBuilder builder = tesselator.getBuilder();
		builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
		drawCube(matrixStack, builder, ALL_SIDES, lightingPos, position, scale, sprite, tint, uv);
		tesselator.end();
		RenderSystem.disableBlend();
	}

	@SuppressWarnings("resource")
	protected static void drawCube(PoseStack matrixStack, VertexConsumer builder, Set<Direction> sides, @Nullable BlockPos lightSamplePos, Vector3f position, Vector3f scale,
			TextureAtlasSprite sprite, SDColor tint, Vector3D uv) {
		// Get the vertex builder and set the color.
		builder.color(tint.getRed(), tint.getBlue(), tint.getGreen(), tint.getAlpha());

		int light = 15728880;
		if (lightSamplePos != null) {
			int i = LevelRenderer.getLightColor(Minecraft.getInstance().level, lightSamplePos);
			int j = LevelRenderer.getLightColor(Minecraft.getInstance().level, lightSamplePos.above());
			int k = i & 255;
			int l = j & 255;
			int i1 = i >> 16 & 255;
			int j1 = j >> 16 & 255;
			light = (k > l ? k : l) | (i1 > j1 ? i1 : j1) << 16;
		}

		// Push a new matrix and set the translation ands scale.
		matrixStack.pushPose();
		matrixStack.translate(position.x(), position.y(), position.z());
		matrixStack.scale(scale.x(), scale.y(), scale.z());

		float maxU = sprite.getU0() + (sprite.getU1() - sprite.getU0()) * uv.getX();
		float maxV = sprite.getV0() + (sprite.getV1() - sprite.getV0()) * uv.getY();
		float topBottomMaxV = sprite.getV0() + (sprite.getV1() - sprite.getV0()) * uv.getZ();

		if (sides.contains(Direction.DOWN)) {
			float shade = Minecraft.getInstance().level.getShade(Direction.DOWN, true);
			builder.vertex(matrixStack.last().pose(), 0.0f, 0.0f, 1.0f).color(shade * tint.getRed(), shade * tint.getGreen(), shade * tint.getBlue(), tint.getAlpha())
					.uv(maxU, sprite.getV0()).uv2(light).normal(0, -1, 0).endVertex();
			builder.vertex(matrixStack.last().pose(), 0.0f, 0.0f, 0.0f).color(shade * tint.getRed(), shade * tint.getGreen(), shade * tint.getBlue(), tint.getAlpha())
					.uv(maxU, topBottomMaxV).uv2(light).normal(0, -1, 0).endVertex();
			builder.vertex(matrixStack.last().pose(), 1.0f, 0.0f, 0.0f).color(shade * tint.getRed(), shade * tint.getGreen(), shade * tint.getBlue(), tint.getAlpha())
					.uv(sprite.getU0(), topBottomMaxV).uv2(light).normal(0, -1, 0).endVertex();
			builder.vertex(matrixStack.last().pose(), 1.0f, 0.0f, 1.0f).color(shade * tint.getRed(), shade * tint.getGreen(), shade * tint.getBlue(), tint.getAlpha())
					.uv(sprite.getU0(), sprite.getV0()).uv2(light).normal(0, -1, 0).endVertex();
		}
		if (sides.contains(Direction.UP)) {
			float shade = Minecraft.getInstance().level.getShade(Direction.UP, true);
			builder.vertex(matrixStack.last().pose(), 1.0f, 1.0f, 1.0f).color(shade * tint.getRed(), shade * tint.getGreen(), shade * tint.getBlue(), tint.getAlpha())
					.uv(maxU, sprite.getV0()).uv2(light).normal(0, 1, 0).endVertex();
			builder.vertex(matrixStack.last().pose(), 1.0f, 1.0f, 0.0f).color(shade * tint.getRed(), shade * tint.getGreen(), shade * tint.getBlue(), tint.getAlpha())
					.uv(maxU, topBottomMaxV).uv2(light).normal(0, 1, 0).endVertex();
			builder.vertex(matrixStack.last().pose(), 0.0f, 1.0f, 0.0f).color(shade * tint.getRed(), shade * tint.getGreen(), shade * tint.getBlue(), tint.getAlpha())
					.uv(sprite.getU0(), topBottomMaxV).uv2(light).normal(0, 1, 0).endVertex();
			builder.vertex(matrixStack.last().pose(), 0.0f, 1.0f, 1.0f).color(shade * tint.getRed(), shade * tint.getGreen(), shade * tint.getBlue(), tint.getAlpha())
					.uv(sprite.getU0(), sprite.getV0()).uv2(light).normal(0, 1, 0).endVertex();
		}
		if (sides.contains(Direction.NORTH)) {
			float shade = Minecraft.getInstance().level.getShade(Direction.NORTH, true);
			builder.vertex(matrixStack.last().pose(), 0.0f, 0.0f, 0.0f).color(shade * tint.getRed(), shade * tint.getGreen(), shade * tint.getBlue(), tint.getAlpha())
					.uv(maxU, sprite.getV0()).uv2(light).normal(0, 0, -1).endVertex();
			builder.vertex(matrixStack.last().pose(), 0.0f, 1.0f, 0.0f).color(shade * tint.getRed(), shade * tint.getGreen(), shade * tint.getBlue(), tint.getAlpha())
					.uv(maxU, maxV).uv2(light).normal(0, 0, -1).endVertex();
			builder.vertex(matrixStack.last().pose(), 1.0f, 1.0f, 0.0f).color(shade * tint.getRed(), shade * tint.getGreen(), shade * tint.getBlue(), tint.getAlpha())
					.uv(sprite.getU0(), maxV).uv2(light).normal(0, 0, -1).endVertex();
			builder.vertex(matrixStack.last().pose(), 1.0f, 0.0f, 0.0f).color(shade * tint.getRed(), shade * tint.getGreen(), shade * tint.getBlue(), tint.getAlpha())
					.uv(sprite.getU0(), sprite.getV0()).uv2(light).normal(0, 0, -1).endVertex();
		}
		if (sides.contains(Direction.SOUTH)) {
			float shade = Minecraft.getInstance().level.getShade(Direction.SOUTH, true);
			builder.vertex(matrixStack.last().pose(), 0.0f, 1.0f, 1.0f).color(shade * tint.getRed(), shade * tint.getGreen(), shade * tint.getBlue(), tint.getAlpha())
					.uv(maxU, sprite.getV0()).uv2(light).normal(0, 0, 1).endVertex();
			builder.vertex(matrixStack.last().pose(), 0.0f, 0.0f, 1.0f).color(shade * tint.getRed(), shade * tint.getGreen(), shade * tint.getBlue(), tint.getAlpha())
					.uv(maxU, maxV).uv2(light).normal(0, 0, 1).endVertex();
			builder.vertex(matrixStack.last().pose(), 1.0f, 0.0f, 1.0f).color(shade * tint.getRed(), shade * tint.getGreen(), shade * tint.getBlue(), tint.getAlpha())
					.uv(sprite.getU0(), maxV).uv2(light).normal(0, 0, 1).endVertex();
			builder.vertex(matrixStack.last().pose(), 1.0f, 1.0f, 1.0f).color(shade * tint.getRed(), shade * tint.getGreen(), shade * tint.getBlue(), tint.getAlpha())
					.uv(sprite.getU0(), sprite.getV0()).uv2(light).normal(0, 0, 1).endVertex();
		}
		if (sides.contains(Direction.WEST)) {
			float shade = Minecraft.getInstance().level.getShade(Direction.WEST, true);
			builder.vertex(matrixStack.last().pose(), 0.0f, 0.0f, 1.0f).color(shade * tint.getRed(), shade * tint.getGreen(), shade * tint.getBlue(), tint.getAlpha())
					.uv(maxU, sprite.getV0()).uv2(light).normal(-1, 0, 0).endVertex();
			builder.vertex(matrixStack.last().pose(), 0.0f, 1.0f, 1.0f).color(shade * tint.getRed(), shade * tint.getGreen(), shade * tint.getBlue(), tint.getAlpha())
					.uv(maxU, maxV).uv2(light).normal(-1, 0, 0).endVertex();
			builder.vertex(matrixStack.last().pose(), 0.0f, 1.0f, 0.0f).color(shade * tint.getRed(), shade * tint.getGreen(), shade * tint.getBlue(), tint.getAlpha())
					.uv(sprite.getU0(), maxV).uv2(light).normal(-1, 0, 0).endVertex();
			builder.vertex(matrixStack.last().pose(), 0.0f, 0.0f, 0.0f).color(shade * tint.getRed(), shade * tint.getGreen(), shade * tint.getBlue(), tint.getAlpha())
					.uv(sprite.getU0(), sprite.getV0()).uv2(light).normal(-1, 0, 0).endVertex();
		}
		if (sides.contains(Direction.EAST)) {
			float shade = Minecraft.getInstance().level.getShade(Direction.EAST, true);
			builder.vertex(matrixStack.last().pose(), 1.0f, 0.0f, 0.0f).color(shade * tint.getRed(), shade * tint.getGreen(), shade * tint.getBlue(), tint.getAlpha())
					.uv(maxU, sprite.getV0()).uv2(light).normal(1, 0, 0).endVertex();
			builder.vertex(matrixStack.last().pose(), 1.0f, 1.0f, 0.0f).color(shade * tint.getRed(), shade * tint.getGreen(), shade * tint.getBlue(), tint.getAlpha())
					.uv(maxU, maxV).uv2(light).normal(1, 0, 0).endVertex();
			builder.vertex(matrixStack.last().pose(), 1.0f, 1.0f, 1.0f).color(shade * tint.getRed(), shade * tint.getGreen(), shade * tint.getBlue(), tint.getAlpha())
					.uv(sprite.getU0(), maxV).uv2(light).normal(1, 0, 0).endVertex();
			builder.vertex(matrixStack.last().pose(), 1.0f, 0.0f, 1.0f).color(shade * tint.getRed(), shade * tint.getGreen(), shade * tint.getBlue(), tint.getAlpha())
					.uv(sprite.getU0(), sprite.getV0()).uv2(light).normal(1, 0, 0).endVertex();
		}

		// Pop the matrix and finish rendering.
		matrixStack.popPose();
	}
}