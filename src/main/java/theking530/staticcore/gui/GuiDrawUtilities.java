package theking530.staticcore.gui;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Vector4f;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticcore.utilities.Vector4D;
import theking530.staticpower.StaticPower;
import theking530.staticpower.client.gui.GuiTextures;

@OnlyIn(Dist.CLIENT)
public class GuiDrawUtilities {
	public static final Color DEFAULT_BACKGROUND_COLOR = new Color(198, 198, 198, 255).fromEightBitToFloat();
	public static final Color DEFAULT_BACKGROUND_EDGE_TINT = new Color(255, 255, 255, 255).fromEightBitToFloat();
	public static final Color DEFAULT_SLOT_DARK_EDGE_COLOR = new Color(55, 55, 55, 255).fromEightBitToFloat();
	public static final Color DEFAULT_SLOT_LIGHT_EDGE_COLOR = new Color(255, 255, 255, 255).fromEightBitToFloat();
	public static final Color DEFAULT_SLOT_CORNER_COLOR = new Color(139, 139, 139, 255).fromEightBitToFloat();

	private static final float BACKGROUND_PIXEL_SIZE = 1.0f / 9.0f;

	public static final PoseStack IDENTITY_STACK;
	static {
		IDENTITY_STACK = new PoseStack();
		IDENTITY_STACK.setIdentity();
	}

	public static void drawTexturedBox(PoseStack pose, ResourceLocation texture, float width, float height, Vector4D borderDefenitions, float x, float y, float z, Color tint) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, texture);
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShaderColor(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha());
		RenderSystem.enableBlend();

		// Body
		drawTexture(pose, texture, width - 4, height - 4, x + 3, y + 3, z, 5 * BACKGROUND_PIXEL_SIZE, 5 * BACKGROUND_PIXEL_SIZE, 5 * BACKGROUND_PIXEL_SIZE, 5 * BACKGROUND_PIXEL_SIZE);

		// Corners
		drawTexture(pose, texture, 4, 4, x, y, z, 0.0f, 0.0f, 4 * BACKGROUND_PIXEL_SIZE, 4 * BACKGROUND_PIXEL_SIZE);
		drawTexture(pose, texture, 5, 4, x + width - 5, y, z, 4 * BACKGROUND_PIXEL_SIZE, 0.0f, 9 * BACKGROUND_PIXEL_SIZE, 4 * BACKGROUND_PIXEL_SIZE);
		drawTexture(pose, texture, 4, 5, x, y + height - 5, z, 0.0f, 4 * BACKGROUND_PIXEL_SIZE, 4 * BACKGROUND_PIXEL_SIZE, 9 * BACKGROUND_PIXEL_SIZE);
		drawTexture(pose, texture, 4, 4, x + width - 4, y + height - 4, 0.0f, 5 * BACKGROUND_PIXEL_SIZE, 5 * BACKGROUND_PIXEL_SIZE, 9 * BACKGROUND_PIXEL_SIZE, 9 * BACKGROUND_PIXEL_SIZE);

		// Sides
		drawTexture(pose, texture, width - 7, 3, x + 4, y, z, 3 * BACKGROUND_PIXEL_SIZE, 0.0f, 4 * BACKGROUND_PIXEL_SIZE, 3 * BACKGROUND_PIXEL_SIZE);
		drawTexture(pose, texture, 3, height - 8, x, y + 4, 3, z, 3 * BACKGROUND_PIXEL_SIZE, 3 * BACKGROUND_PIXEL_SIZE, 4 * BACKGROUND_PIXEL_SIZE);
		drawTexture(pose, texture, 3, height - 8, x + width - 3, y + 4, z, 6 * BACKGROUND_PIXEL_SIZE, 3 * BACKGROUND_PIXEL_SIZE, 9 * BACKGROUND_PIXEL_SIZE, 4 * BACKGROUND_PIXEL_SIZE);
		drawTexture(pose, texture, width - 8, 3, x + 4, y + height - 3, z, 4 * BACKGROUND_PIXEL_SIZE, 6 * BACKGROUND_PIXEL_SIZE, 5 * BACKGROUND_PIXEL_SIZE, 9 * BACKGROUND_PIXEL_SIZE);

		RenderSystem.setShaderColor(1, 1, 1, 1);
	}

	public static void drawTexturedBox(PoseStack pose, ResourceLocation texture, float width, float height, Vector4D borderDefenitions, float x, float y, float z) {
		drawTexturedBox(pose, texture, width, height, borderDefenitions, x, y, z, Color.WHITE);
	}

	public static void drawTexturedBox(PoseStack pose, ResourceLocation texture, float width, float height, Vector4D borderDefenitions, float x, float y) {
		drawTexturedBox(pose, texture, width, height, borderDefenitions, x, y, 0.0f, Color.WHITE);
	}

	public static void drawTexturedBox(PoseStack pose, ResourceLocation texture, float width, float height, Vector4D borderDefenitions) {
		drawTexturedBox(pose, texture, width, height, borderDefenitions, 0, 0, 0.0f, Color.WHITE);
	}

	public static void drawGenericBackground(PoseStack pose, float width, float height, float x, float y, float z, Color tint) {
		ResourceLocation texture = GuiTextures.GENERIC_GUI;
		RenderSystem.setShaderColor(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha());

		// Body
		drawTexture(pose, texture, width - 4, height - 4, x + 3, y + 3, z, 5 * BACKGROUND_PIXEL_SIZE, 5 * BACKGROUND_PIXEL_SIZE, 5 * BACKGROUND_PIXEL_SIZE, 5 * BACKGROUND_PIXEL_SIZE);

		// Corners
		drawTexture(pose, texture, 4, 4, x, y, z, 0.0f, 0.0f, 4 * BACKGROUND_PIXEL_SIZE, 4 * BACKGROUND_PIXEL_SIZE);
		drawTexture(pose, texture, 5, 4, x + width - 5, y, z, 4 * BACKGROUND_PIXEL_SIZE, 0.0f, 9 * BACKGROUND_PIXEL_SIZE, 4 * BACKGROUND_PIXEL_SIZE);
		drawTexture(pose, texture, 4, 5, x, y + height - 5, z, 0.0f, 4 * BACKGROUND_PIXEL_SIZE, 4 * BACKGROUND_PIXEL_SIZE, 9 * BACKGROUND_PIXEL_SIZE);
		drawTexture(pose, texture, 4, 4, x + width - 4, y + height - 4, 0.0f, 5 * BACKGROUND_PIXEL_SIZE, 5 * BACKGROUND_PIXEL_SIZE, 9 * BACKGROUND_PIXEL_SIZE, 9 * BACKGROUND_PIXEL_SIZE);

		// Sides
		drawTexture(pose, texture, width - 7, 3, x + 4, y, z, 3 * BACKGROUND_PIXEL_SIZE, 0.0f, 4 * BACKGROUND_PIXEL_SIZE, 3 * BACKGROUND_PIXEL_SIZE);
		drawTexture(pose, texture, 3, height - 8, x, y + 4, z, 0.0f, 3 * BACKGROUND_PIXEL_SIZE, 3 * BACKGROUND_PIXEL_SIZE, 4 * BACKGROUND_PIXEL_SIZE);
		drawTexture(pose, texture, 3, height - 8, x + width - 3, y + 4, z, 6 * BACKGROUND_PIXEL_SIZE, 3 * BACKGROUND_PIXEL_SIZE, 9 * BACKGROUND_PIXEL_SIZE, 4 * BACKGROUND_PIXEL_SIZE);
		drawTexture(pose, texture, width - 8, 3, x + 4, y + height - 3, z, 4 * BACKGROUND_PIXEL_SIZE, 6 * BACKGROUND_PIXEL_SIZE, 5 * BACKGROUND_PIXEL_SIZE, 9 * BACKGROUND_PIXEL_SIZE);

		RenderSystem.setShaderColor(1, 1, 1, 1);

	}

	public static void drawGenericBackground(PoseStack pose, float width, float height, float x, float y, float z) {
		drawGenericBackground(pose, width, height, x, y, z, Color.WHITE);
	}

	public static void drawGenericBackground(PoseStack pose, float width, float height, float x, float y) {
		drawGenericBackground(pose, width, height, x, y, 0.0f, Color.WHITE);
	}

	public static void drawGenericBackground(PoseStack pose, float width, float height) {
		drawGenericBackground(pose, width, height, 0, 0, 0.0f, Color.WHITE);
	}

	public static void drawPlayerInventorySlots(PoseStack matrixStack, int xPos, int yPos) {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				drawSlot(matrixStack, 16, 16, xPos + j * 18, yPos + 1 + i * 18, 0);
			}
		}
		for (int i = 0; i < 9; i++) {
			drawSlot(matrixStack, 16, 16, xPos + i * 18, yPos + 59, 0);
		}
	}

	public static void drawSlotWithBorder(PoseStack pose, float width, float height, float x, float y, float z, Color borderColor) {
		if (borderColor != null) {
			drawRectangle(pose, width + 4, height + 4, x - 2, y - 2, z, borderColor);
		}

		drawRectangle(pose, 1, 1 + height, x - 1, y - 1, z, DEFAULT_SLOT_DARK_EDGE_COLOR);
		drawRectangle(pose, width, 1, x, y - 1, z, DEFAULT_SLOT_DARK_EDGE_COLOR);
		drawRectangle(pose, 1, 1, x + width, y - 1, z, DEFAULT_SLOT_CORNER_COLOR);

		drawRectangle(pose, 1, 1, x - 1, y + height, z, DEFAULT_SLOT_CORNER_COLOR);
		drawRectangle(pose, width + 1, 1, x, y + height, z, DEFAULT_SLOT_LIGHT_EDGE_COLOR);
		drawRectangle(pose, 1, height, x + width, y, z, DEFAULT_SLOT_LIGHT_EDGE_COLOR);

		drawRectangle(pose, width, height, x, y, z, DEFAULT_SLOT_CORNER_COLOR);

		if (borderColor != null) {
			Color lighterFill = borderColor.copy();
			lighterFill.setW(0.25f);
			drawRectangle(pose, width, height, x, y, z, lighterFill);
		}
	}

	public static void drawSlot(PoseStack pose, float width, float height, float x, float y, float z) {
		drawSlotWithBorder(pose, width, height, x, y, z, null);
	}

	public static void drawSlot(PoseStack pose, float width, float height) {
		drawSlotWithBorder(pose, width, height, 0, 0, 0, null);
	}

	public static void drawRectangle(PoseStack pose, float width, float height, Color color) {
		drawRectangle(pose, width, height, 0, 0, 0, color);
	}

	public static void drawRectangle(PoseStack pose, float width, float height, float x, float y, float z, Color color) {
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		RenderSystem.enableBlend();
		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuilder();
		bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
		bufferbuilder.vertex(pose.last().pose(), x, y + height, z).color(color.getX(), color.getY(), color.getZ(), color.getW()).endVertex();
		bufferbuilder.vertex(pose.last().pose(), x + width, y + height, z).color(color.getX(), color.getY(), color.getZ(), color.getW()).endVertex();
		bufferbuilder.vertex(pose.last().pose(), x + width, y, z).color(color.getX(), color.getY(), color.getZ(), color.getW()).endVertex();
		bufferbuilder.vertex(pose.last().pose(), x, y, z).color(color.getX(), color.getY(), color.getZ(), color.getW()).endVertex();
		tessellator.end();
	}

	public static void drawTexture(PoseStack pose, ResourceLocation texture, float width, float height, float minU, float minV, float maxU, float maxV, Color color) {
		drawTexture(pose, texture, width, height, 0, 0, 0, minU, minV, maxU, maxV);
	}

	public static void drawTexture(PoseStack pose, ResourceLocation texture, float width, float height, float x, float y, float z, float minU, float minV, float maxU, float maxV) {
		drawTexture(pose, texture, width, height, x, y, z, minU, minV, maxU, maxV, Color.WHITE);
	}

	public static void drawTexture(PoseStack pose, ResourceLocation texture, float width, float height, float x, float y, float z, float minU, float minV, float maxU, float maxV, Color color) {
		RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
		RenderSystem.setShaderTexture(0, texture);
		RenderSystem.enableBlend();
		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuilder();
		bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
		bufferbuilder.vertex(pose.last().pose(), x, (y + height), z).color(color.getX(), color.getY(), color.getZ(), color.getW()).uv(minU, maxV).endVertex();
		bufferbuilder.vertex(pose.last().pose(), (x + width), (y + height), z).color(color.getX(), color.getY(), color.getZ(), color.getW()).uv(maxU, maxV).endVertex();
		bufferbuilder.vertex(pose.last().pose(), (x + width), y, z).color(color.getX(), color.getY(), color.getZ(), color.getW()).uv(maxU, minV).endVertex();
		bufferbuilder.vertex(pose.last().pose(), x, y, z).color(color.getX(), color.getY(), color.getZ(), color.getW()).uv(minU, minV).endVertex();
		tessellator.end();
	}

	public static void drawSprite(PoseStack pose, ResourceLocation spriteLocation, float width, float height, float minU, float minV, float maxU, float maxV, Color color) {
		drawSprite(pose, spriteLocation, 0, 0, width, height, 0, minU, minV, maxU, maxV);
	}

	public static void drawSprite(PoseStack pose, ResourceLocation spriteLocation, float width, float height, float x, float y, float z, float minU, float minV, float maxU, float maxV) {
		drawSprite(pose, spriteLocation, width, height, x, y, z, minU, minV, maxU, maxV, Color.WHITE);
	}

	public static void drawSprite(PoseStack pose, ResourceLocation spriteLocation, float width, float height, float x, float y, float z, Color color) {
		drawSprite(pose, spriteLocation, width, height, x, y, z, 0, 0, 1, 1, color);
	}

	public static void drawSprite(PoseStack pose, ResourceLocation spriteLocation, float width, float height, float x, float y, float z) {
		drawSprite(pose, spriteLocation, width, height, x, y, z, 0, 0, 1, 1, Color.WHITE);
	}

	public static void drawSprite(PoseStack pose, ResourceLocation spriteLocation, float width, float height, Color color) {
		drawSprite(pose, spriteLocation, width, height, 0, 0, 0, 0, 0, 1, 1, color);
	}

	public static void drawSprite(PoseStack pose, ResourceLocation spriteLocation, float width, float height) {
		drawSprite(pose, spriteLocation, width, height, 0, 0, 0, 0, 0, 1, 1, Color.WHITE);
	}

	public static void drawSprite(PoseStack pose, ResourceLocation spriteLocation, float width, float height, float x, float y, float z, float minU, float minV, float maxU, float maxV, Color color) {
		RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
		RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
		RenderSystem.enableBlend();
		TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(spriteLocation);

		float uDiff = sprite.getU1() - sprite.getU0();
		float vDiff = sprite.getV1() - sprite.getV0();

		float minSpriteU = sprite.getU0() + (minU * uDiff);
		float maxSpriteU = sprite.getU1() - ((1.0f - maxU) * vDiff);
		float minSpriteV = sprite.getV0() + (minV * vDiff);
		float maxSpriteV = sprite.getV1() - ((1.0f - maxV) * vDiff);

		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuilder();
		bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
		bufferbuilder.vertex(pose.last().pose(), (x + 0), (y + height), z).color(color.getX(), color.getY(), color.getZ(), color.getW()).uv(minSpriteU, maxSpriteV).endVertex();
		bufferbuilder.vertex(pose.last().pose(), (x + width), (y + height), z).color(color.getX(), color.getY(), color.getZ(), color.getW()).uv(maxSpriteU, maxSpriteV).endVertex();
		bufferbuilder.vertex(pose.last().pose(), (x + width), (y + 0), z).color(color.getX(), color.getY(), color.getZ(), color.getW()).uv(maxSpriteU, minSpriteV).endVertex();
		bufferbuilder.vertex(pose.last().pose(), (x + 0), (y + 0), z).color(color.getX(), color.getY(), color.getZ(), color.getW()).uv(minSpriteU, minSpriteV).endVertex();
		tessellator.end();
	}

	public static void drawString(PoseStack matrixStack, String text, float xPos, float yPos, float zPos, float scale, Color color, boolean withShadow) {
		drawString(matrixStack, text, xPos, yPos, zPos, scale, color.encodeInInteger(), withShadow);
	}

	public static void drawStringCentered(PoseStack matrixStack, String text, float xPos, float yPos, float zPos, float scale, Color color, boolean withShadow) {
		@SuppressWarnings("resource")
		float width = Minecraft.getInstance().font.width(text) * scale;
		drawString(matrixStack, text, xPos + (width / 2), yPos, zPos, scale, color.encodeInInteger(), withShadow);
	}

	public static void drawStringLeftAligned(PoseStack matrixStack, String text, float xPos, float yPos, float zPos, float scale, Color color, boolean withShadow) {
		@SuppressWarnings("resource")
		float width = Minecraft.getInstance().font.width(text) * scale;
		drawString(matrixStack, text, xPos + width, yPos, zPos, scale, color.encodeInInteger(), withShadow);
	}

	public static List<String> wrapString(String string, float maxWidth) {
		// Draw the description.
		List<String> lines = new ArrayList<String>();
		StringBuilder currentLine = new StringBuilder();
		int currentLineWidth = 0;

		for (String word : string.split(" ")) {
			@SuppressWarnings("resource")
			int wordWidth = Minecraft.getInstance().font.width(word);
			if (wordWidth == 0 || currentLineWidth + wordWidth < maxWidth) {
				currentLine.append(word + " ");
				currentLineWidth += wordWidth + 1;
			} else {
				lines.add(currentLine.toString().trim());
				currentLine = new StringBuilder();
				currentLine.append(word + " ");
				currentLineWidth = wordWidth + 1;
			}
		}

		if (currentLine.toString().stripTrailing().length() > 0) {
			lines.add(currentLine.toString().stripTrailing());
			currentLineWidth = 0;
		}

		return lines;
	}

	public static void drawStringWithSize(PoseStack matrixStack, String text, float xPos, float yPos, float zPos, float scale, ChatFormatting color, boolean withShadow) {
		drawString(matrixStack, text, xPos, yPos, zPos, scale, color.getColor(), withShadow);
	}

	/**
	 * Renders a string with the provided scale.
	 * 
	 * @param text
	 * @param xPos
	 * @param yPos
	 * @param zPos       TODO
	 * @param scale
	 * @param color
	 * @param withShadow
	 */
	@SuppressWarnings("resource")
	public static void drawString(PoseStack matrixStack, String text, float xPos, float yPos, float zPos, float scale, int color, boolean withShadow) {
		// The matrix stack cannot be null.
		if (matrixStack == null) {
			StaticPower.LOGGER.error("A non-null matrix stack must be provided to this method!");
			return;
		}

		final float scaleFactor = scale;
		final float inverseScaleFactor = 1.0f / scaleFactor;
		matrixStack.pushPose();
		matrixStack.scale(scaleFactor, scaleFactor, 1.0f);
		matrixStack.translate(0, 0, zPos);

		RenderSystem.disableBlend();
		final int X = (int) ((xPos - Minecraft.getInstance().font.width(text) * scaleFactor) * inverseScaleFactor);
		final int Y = (int) ((yPos - 7.0f * scaleFactor) * inverseScaleFactor);
		MultiBufferSource.BufferSource buffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
		Minecraft.getInstance().font.drawInBatch(text, X, Y, color, withShadow, matrixStack.last().pose(), buffer, true, 0, 15728880);
		buffer.endBatch();
		RenderSystem.enableBlend();
		matrixStack.popPose();
	}

	public static void drawLine(PoseStack pose, Vector3D start, Vector3D end, Color startcolor, Color endColor, float thickness) {
		RenderSystem.setShader(GameRenderer::getRendertypeLinesShader);
		RenderSystem.enableBlend();
		RenderSystem.disableCull();
		RenderSystem.lineWidth(thickness);

		Vector2D normal = end.copy().subtract(start).normalize();
		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuilder();
		bufferbuilder.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR_NORMAL);
		bufferbuilder.vertex(pose.last().pose(), start.getX(), start.getY(), start.getZ()).color(startcolor.getRed(), startcolor.getGreen(), startcolor.getBlue(), startcolor.getAlpha())
				.normal(normal.getX(), normal.getY(), 0).endVertex();
		bufferbuilder.vertex(pose.last().pose(), end.getX(), end.getY(), end.getZ()).color(endColor.getRed(), endColor.getGreen(), endColor.getBlue(), endColor.getAlpha())
				.normal(normal.getX(), normal.getY(), 0).endVertex();
		tessellator.end();
		RenderSystem.enableCull();
	}

	public static void drawLine(PoseStack pose, Vector3D start, Vector3D end, Color color, float thickness) {
		drawLine(pose, start, end, color, color, thickness);
	}

	public static void drawLine(PoseStack pose, Vector3D start, Vector3D end, Color color) {
		drawLine(pose, start, end, color, color, 1.0f);
	}

	public static void drawItem(@Nullable PoseStack pose, ItemStack item, float alpha) {
		drawItem(pose, item, 0, 0, 0, 16, 16, alpha);
	}

	public static void drawItem(@Nullable PoseStack pose, ItemStack item, float x, float y, float z) {
		drawItem(pose, item, x, y, z, 16, 16, 1);

	}

	public static void drawItem(@Nullable PoseStack pose, ItemStack item, float x, float y, float z, float alpha) {
		drawItem(pose, item, x, y, z, 16, 16, alpha);
	}

	public static void drawItem(@Nullable PoseStack pose, ItemStack item, float x, float y, float z, float width, float height) {
		drawItem(pose, item, x, y, z, width, height, 1);
	}

	@SuppressWarnings("resource")
	public static void drawItem(@Nullable PoseStack pose, ItemStack item, float x, float y, float z, float width, float height, float alpha) {
		BakedModel model = Minecraft.getInstance().getItemRenderer().getModel(item, (Level) null, Minecraft.getInstance().player, 0);
		Minecraft.getInstance().getTextureManager().getTexture(InventoryMenu.BLOCK_ATLAS).setFilter(false, false);
		RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

		PoseStack posestack = RenderSystem.getModelViewStack();
		posestack.pushPose();
		posestack.translate(8.0D, 8.0D, 0.0D);

		if (pose != null) {
			Vector3D offset = GuiDrawUtilities.translatePositionByMatrix(pose, x, y, z);
			posestack.translate(offset.getX(), offset.getY(), offset.getZ());
		} else {
			posestack.translate(x, y, z);
		}

		posestack.scale(1.0F, -1.0F, 1.0F);
		posestack.scale(width, height, 16.0F);
		RenderSystem.applyModelViewMatrix();

		PoseStack posestack1 = new PoseStack();
		MultiBufferSource.BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
		boolean flag = !model.usesBlockLight();
		if (flag) {
			Lighting.setupForFlatItems();
		}

		Minecraft.getInstance().getItemRenderer().render(item, ItemTransforms.TransformType.GUI, false, posestack1, multibuffersource$buffersource, 15728880, OverlayTexture.NO_OVERLAY, model);

		
		multibuffersource$buffersource.endBatch();
		RenderSystem.enableDepthTest();
		if (flag) {
			Lighting.setupFor3DItems();
		}

		posestack.popPose();

		RenderSystem.applyModelViewMatrix();

		RenderSystem.disableDepthTest();
		RenderSystem.enableBlend();
		GuiDrawUtilities.drawRectangle(pose, width, height, x, y, 0,
				new Color(DEFAULT_SLOT_CORNER_COLOR.getRed(), DEFAULT_SLOT_CORNER_COLOR.getGreen(), DEFAULT_SLOT_CORNER_COLOR.getBlue(), 1.0f - alpha));
		RenderSystem.disableBlend();
		RenderSystem.enableDepthTest();
	}

	public static float getSinFunction(float period, float amplitude) {
		@SuppressWarnings("resource")
		double sin = (Math.sin(Minecraft.getInstance().level.getGameTime() / period));
		sin = (sin + amplitude) / (amplitude + 1);
		return (float) sin;
	}

	public static TextureAtlasSprite getStillFluidSprite(FluidStack fluidStack) {
		Fluid fluid = fluidStack.getFluid();
		FluidAttributes attributes = fluid.getAttributes();
		ResourceLocation fluidStill = attributes.getStillTexture(fluidStack);
		return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(fluidStill);
	}

	public static Color getFluidColor(FluidStack fluid) {
		FluidAttributes attributes = fluid.getFluid().getAttributes();
		int encodedFluidColor = attributes.getColor(fluid);
		return Color.fromEncodedInteger(encodedFluidColor).fromEightBitToFloat();
	}

	public static Vector2D translatePositionByMatrix(PoseStack matrixStack, Vector2D position) {
		return translatePositionByMatrix(matrixStack, position.getX(), position.getY());
	}

	public static Vector2D translatePositionByMatrix(PoseStack matrixStack, float xPos, float yPos) {
		if (matrixStack != null) {
			Vector4f vector4f = new Vector4f(xPos, yPos, 0, 1);
			vector4f.transform(matrixStack.last().pose());
			return new Vector2D(vector4f.x(), vector4f.y());
		} else {
			return new Vector2D(xPos, yPos);
		}
	}

	public static Vector3D translatePositionByMatrix(PoseStack matrixStack, float xPos, float yPos, float zPos) {
		if (matrixStack != null) {
			Vector4f vector4f = new Vector4f(xPos, yPos, zPos, 1);
			vector4f.transform(matrixStack.last().pose());
			return new Vector3D(vector4f.x(), vector4f.y(), vector4f.z());
		} else {
			return new Vector3D(xPos, yPos, zPos);
		}
	}
}
