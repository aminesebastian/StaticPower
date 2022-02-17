package theking530.staticcore.gui;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector4f;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.utilities.Color;
import theking530.staticcore.utilities.SDMath;
import theking530.staticcore.utilities.Vector2D;
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

	public static void drawGenericBackground(int width, int height, int guiLeft, int guiTop, float zLevel, Color mainBackgroundColor, boolean drawLeft, boolean drawRight, boolean drawTop,
			boolean drawBottom) {
		// MainBG
		drawColoredRectangle(guiLeft + 3, guiTop + 3, width - 4, height - 4, zLevel, mainBackgroundColor);

		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, GuiTextures.GENERIC_GUI);

		// Corners
		drawTexturedGenericRect(guiLeft, guiTop, 4, 4, 0.0f, 0.0f, zLevel, 4 * BACKGROUND_PIXEL_SIZE, 4 * BACKGROUND_PIXEL_SIZE);
		drawTexturedGenericRect(guiLeft + width - 5, guiTop, 5, 4, zLevel, 4 * BACKGROUND_PIXEL_SIZE, 0.0f, 9 * BACKGROUND_PIXEL_SIZE, 4 * BACKGROUND_PIXEL_SIZE);
		drawTexturedGenericRect(guiLeft, guiTop + height - 5, 4, 5, 0.0f, zLevel, 4 * BACKGROUND_PIXEL_SIZE, 4 * BACKGROUND_PIXEL_SIZE, 9 * BACKGROUND_PIXEL_SIZE);
		drawTexturedGenericRect(guiLeft + width - 4, guiTop + height - 4, 4, 4, zLevel, 5 * BACKGROUND_PIXEL_SIZE, 5 * BACKGROUND_PIXEL_SIZE, 9 * BACKGROUND_PIXEL_SIZE, 9 * BACKGROUND_PIXEL_SIZE);

		// Sides
		if (drawTop) {
			drawTexturedGenericRect(guiLeft + 4, guiTop, width - 7, 3, zLevel, 3 * BACKGROUND_PIXEL_SIZE, 0.0f, 4 * BACKGROUND_PIXEL_SIZE, 3 * BACKGROUND_PIXEL_SIZE);
		}
		if (drawLeft) {
			drawTexturedGenericRect(guiLeft, guiTop + 4, 3, height - 8, 0.0f, zLevel, 3 * BACKGROUND_PIXEL_SIZE, 3 * BACKGROUND_PIXEL_SIZE, 4 * BACKGROUND_PIXEL_SIZE);
		}
		if (drawRight) {
			drawTexturedGenericRect(guiLeft + width - 3, guiTop + 4, 3, height - 8, zLevel, 6 * BACKGROUND_PIXEL_SIZE, 3 * BACKGROUND_PIXEL_SIZE, 9 * BACKGROUND_PIXEL_SIZE, 4 * BACKGROUND_PIXEL_SIZE);
		}
		if (drawBottom) {
			drawTexturedGenericRect(guiLeft + 4, guiTop + height - 3, width - 8, 3, zLevel, 4 * BACKGROUND_PIXEL_SIZE, 6 * BACKGROUND_PIXEL_SIZE, 5 * BACKGROUND_PIXEL_SIZE, 9 * BACKGROUND_PIXEL_SIZE);
		}
	}

	public static void drawGenericBackground(int width, int height, int guiLeft, int guiTop, float zLevel, Color tint) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, GuiTextures.GENERIC_GUI);
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShaderColor(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getAlpha());
		RenderSystem.enableBlend();

		// Body
		drawTexturedGenericRect(guiLeft + 3, guiTop + 3, width - 4, height - 4, zLevel, 5 * BACKGROUND_PIXEL_SIZE, 5 * BACKGROUND_PIXEL_SIZE, 5 * BACKGROUND_PIXEL_SIZE, 5 * BACKGROUND_PIXEL_SIZE);

		// Corners
		drawTexturedGenericRect(guiLeft, guiTop, 4, 4, 0.0f, 0.0f, zLevel, 4 * BACKGROUND_PIXEL_SIZE, 4 * BACKGROUND_PIXEL_SIZE);
		drawTexturedGenericRect(guiLeft + width - 5, guiTop, 5, 4, zLevel, 4 * BACKGROUND_PIXEL_SIZE, 0.0f, 9 * BACKGROUND_PIXEL_SIZE, 4 * BACKGROUND_PIXEL_SIZE);
		drawTexturedGenericRect(guiLeft, guiTop + height - 5, 4, 5, 0.0f, zLevel, 4 * BACKGROUND_PIXEL_SIZE, 4 * BACKGROUND_PIXEL_SIZE, 9 * BACKGROUND_PIXEL_SIZE);
		drawTexturedGenericRect(guiLeft + width - 4, guiTop + height - 4, 4, 4, zLevel, 5 * BACKGROUND_PIXEL_SIZE, 5 * BACKGROUND_PIXEL_SIZE, 9 * BACKGROUND_PIXEL_SIZE, 9 * BACKGROUND_PIXEL_SIZE);

		// Sides
		drawTexturedGenericRect(guiLeft + 4, guiTop, width - 7, 3, zLevel, 3 * BACKGROUND_PIXEL_SIZE, 0.0f, 4 * BACKGROUND_PIXEL_SIZE, 3 * BACKGROUND_PIXEL_SIZE);
		drawTexturedGenericRect(guiLeft, guiTop + 4, 3, height - 8, 0.0f, zLevel, 3 * BACKGROUND_PIXEL_SIZE, 3 * BACKGROUND_PIXEL_SIZE, 4 * BACKGROUND_PIXEL_SIZE);
		drawTexturedGenericRect(guiLeft + width - 3, guiTop + 4, 3, height - 8, zLevel, 6 * BACKGROUND_PIXEL_SIZE, 3 * BACKGROUND_PIXEL_SIZE, 9 * BACKGROUND_PIXEL_SIZE, 4 * BACKGROUND_PIXEL_SIZE);
		drawTexturedGenericRect(guiLeft + 4, guiTop + height - 3, width - 8, 3, zLevel, 4 * BACKGROUND_PIXEL_SIZE, 6 * BACKGROUND_PIXEL_SIZE, 5 * BACKGROUND_PIXEL_SIZE, 9 * BACKGROUND_PIXEL_SIZE);

		RenderSystem.setShaderColor(1, 1, 1, 1);
	}

	public static void drawGenericBackground(int width, int height, int guiLeft, int guiTop, float zLevel) {
		drawGenericBackground(width, height, guiLeft, guiTop, zLevel, DEFAULT_BACKGROUND_COLOR, true, true, true, true);
	}

	public static void drawGenericBackground(int width, int height, int guiLeft, int guiTop) {
		drawGenericBackground(width, height, guiLeft, guiTop, 0.0f, DEFAULT_BACKGROUND_COLOR, true, true, true, true);
	}

	public static void drawPlayerInventorySlots(PoseStack matrixStack, int xPos, int yPos) {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				drawSlot(matrixStack, xPos + j * 18, yPos + 1 + i * 18, 16, 16, 0);
			}
		}
		for (int i = 0; i < 9; i++) {
			drawSlot(matrixStack, xPos + i * 18, yPos + 59, 16, 16, 0);
		}
	}

	public static void drawSlot(@Nullable PoseStack matrixStack, float xPos, float yPos, float width, float height, float zLevel, Color color) {
		Vector2D origin = translatePositionByMatrix(matrixStack, xPos, yPos);

		if (color != null) {
			drawColoredRectangle(origin.getX() - 2, origin.getY() - 2, width + 4, height + 4, zLevel, color);
		}

		drawColoredRectangle(origin.getX() - 1, origin.getY() - 1, 1, 1 + height, zLevel, DEFAULT_SLOT_DARK_EDGE_COLOR);
		drawColoredRectangle(origin.getX(), origin.getY() - 1, width, 1, zLevel, DEFAULT_SLOT_DARK_EDGE_COLOR);
		drawColoredRectangle(origin.getX() + width, origin.getY() - 1, 1, 1, zLevel, DEFAULT_SLOT_CORNER_COLOR);

		drawColoredRectangle(origin.getX() - 1, origin.getY() + height, 1, 1, zLevel, DEFAULT_SLOT_CORNER_COLOR);
		drawColoredRectangle(origin.getX(), origin.getY() + height, width + 1, 1, zLevel, DEFAULT_SLOT_LIGHT_EDGE_COLOR);
		drawColoredRectangle(origin.getX() + width, origin.getY(), 1, height, zLevel, DEFAULT_SLOT_LIGHT_EDGE_COLOR);
		drawColoredRectangle(origin.getX(), origin.getY(), width, height, zLevel, DEFAULT_SLOT_CORNER_COLOR);
	}

	public static void drawSlot(@Nullable PoseStack matrixStack, float xPos, float yPos, float width, float height, float zLevel) {
		drawSlot(matrixStack, xPos, yPos, width, height, zLevel, null);
	}

	public void drawVerticalBar(PoseStack matrixStack, float xPos, float yPos, float width, float height, float fillAmount, Color color) {
		drawSlot(null, xPos, yPos, width, height, 0);
		int filledHeight = (int) (fillAmount * height);
		float zLevel = 0.0f;
		drawColoredRectangle(xPos, yPos + (height - filledHeight), xPos + width, yPos + height, zLevel, color);
	}

	public static void drawTexturedGenericRect(float xCoord, float yCoord, float width, float height, float zLevel, float minU, float minV, float maxU, float maxV) {
		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuilder();
		bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		bufferbuilder.vertex(xCoord, yCoord + height, zLevel).uv(minU, maxV).endVertex();
		bufferbuilder.vertex(xCoord + width, yCoord + height, zLevel).uv(maxU, maxV).endVertex();
		bufferbuilder.vertex(xCoord + width, yCoord, zLevel).uv(maxU, minV).endVertex();
		bufferbuilder.vertex(xCoord, yCoord, zLevel).uv(minU, minV).endVertex();
		tessellator.end();
	}

	public static void drawColoredRectangle(PoseStack matrixStack, float xCoord, float yCoord, float width, float height, float zLevel, Color color) {
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuilder();
		bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
		bufferbuilder.vertex(matrixStack.last().pose(), xCoord, yCoord + height, zLevel).color(color.getX(), color.getY(), color.getZ(), color.getW()).endVertex();
		bufferbuilder.vertex(matrixStack.last().pose(), xCoord + width, yCoord + height, zLevel).color(color.getX(), color.getY(), color.getZ(), color.getW()).endVertex();
		bufferbuilder.vertex(matrixStack.last().pose(), xCoord + width, yCoord, zLevel).color(color.getX(), color.getY(), color.getZ(), color.getW()).endVertex();
		bufferbuilder.vertex(matrixStack.last().pose(), xCoord, yCoord, zLevel).color(color.getX(), color.getY(), color.getZ(), color.getW()).endVertex();
		tessellator.end();
	}

	public static void drawColoredRectangle(float xCoord, float yCoord, float width, float height, float zLevel, Color color) {
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuilder();
		bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
		bufferbuilder.vertex(xCoord, yCoord + height, zLevel).color(color.getX(), color.getY(), color.getZ(), color.getW()).endVertex();
		bufferbuilder.vertex(xCoord + width, yCoord + height, zLevel).color(color.getX(), color.getY(), color.getZ(), color.getW()).endVertex();
		bufferbuilder.vertex(xCoord + width, yCoord, zLevel).color(color.getX(), color.getY(), color.getZ(), color.getW()).endVertex();
		bufferbuilder.vertex(xCoord, yCoord, zLevel).color(color.getX(), color.getY(), color.getZ(), color.getW()).endVertex();
		tessellator.end();
	}

	public static void drawStringWithSize(@Nonnull PoseStack matrixStack, String text, float xPos, float yPos, float scale, Color color, boolean withShadow) {
		drawStringWithSize(matrixStack, text, xPos, yPos, scale, color.encodeInInteger(), withShadow);
	}

	public static void drawStringWithSizeCentered(@Nonnull PoseStack matrixStack, String text, float xPos, float yPos, float scale, Color color, boolean withShadow) {
		float width = Minecraft.getInstance().font.width(text) * scale;
		drawStringWithSize(matrixStack, text, xPos + (width / 2), yPos, scale, color.encodeInInteger(), withShadow);
	}

	public static void drawStringWithSizeLeftAligned(@Nonnull PoseStack matrixStack, String text, float xPos, float yPos, float scale, Color color, boolean withShadow) {
		float width = Minecraft.getInstance().font.width(text) * scale;
		drawStringWithSize(matrixStack, text, xPos + width, yPos, scale, color.encodeInInteger(), withShadow);
	}

	public static void drawStringWithSize(@Nonnull PoseStack matrixStack, String text, float xPos, float yPos, float scale, ChatFormatting color, boolean withShadow) {
		drawStringWithSize(matrixStack, text, xPos, yPos, scale, color.getColor(), withShadow);
	}

	/**
	 * Renders a string with the provided scale.
	 * 
	 * @param text
	 * @param xPos
	 * @param yPos
	 * @param scale
	 * @param color
	 * @param withShadow
	 */
	public static void drawStringWithSize(@Nonnull PoseStack matrixStack, String text, float xPos, float yPos, float scale, int color, boolean withShadow) {
		// The matrix stack cannot be null.
		if (matrixStack == null) {
			StaticPower.LOGGER.error("A non-null matrix stack must be provided to this method!");
			return;
		}

		final float scaleFactor = scale;
		final float inverseScaleFactor = 1.0f / scaleFactor;
		final int offset = 0;

		matrixStack.pushPose();
		matrixStack.scale(scaleFactor, scaleFactor, 1.0f);

		RenderSystem.disableBlend();
		final int X = (int) ((xPos + offset - Minecraft.getInstance().font.width(text) * scaleFactor) * inverseScaleFactor);
		final int Y = (int) ((yPos + offset - 7.0f * scaleFactor) * inverseScaleFactor);
		MultiBufferSource.BufferSource buffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
		Minecraft.getInstance().font.drawInBatch(text, X, Y, color, withShadow, matrixStack.last().pose(), buffer, true, 0, 15728880);
		buffer.endBatch();
		RenderSystem.enableBlend();
		matrixStack.popPose();
	}

	public static void drawTexturedModalRect(ResourceLocation texture, @Nullable PoseStack pose, float x, float y, float z, float width, float height, float minU, float minV, float maxU, float maxV) {
		drawTexturedModalRect(texture, pose, x, y, z, width, height, minU, minV, maxU, maxV, Color.WHITE);
	}

	public static void drawTexturedModalRect(ResourceLocation texture, @Nullable PoseStack pose, float x, float y, float z, float width, float height, float minU, float minV, float maxU, float maxV,
			Color color) {
		RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
		RenderSystem.setShaderTexture(0, texture);
		RenderSystem.enableBlend();
		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuilder();
		Matrix4f matrix = pose == null ? SDMath.IDENTITY : pose.last().pose();
		bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
		bufferbuilder.vertex(matrix, x, (y + height), z).color(color.getX(), color.getY(), color.getZ(), color.getW()).uv(minU, maxV).endVertex();
		bufferbuilder.vertex(matrix, (x + width), (y + height), z).color(color.getX(), color.getY(), color.getZ(), color.getW()).uv(maxU, maxV).endVertex();
		bufferbuilder.vertex(matrix, (x + width), y, z).color(color.getX(), color.getY(), color.getZ(), color.getW()).uv(maxU, minV).endVertex();
		bufferbuilder.vertex(matrix, x, y, z).color(color.getX(), color.getY(), color.getZ(), color.getW()).uv(minU, minV).endVertex();
		tessellator.end();
	}

	public static void drawTexturedModalSprite(ResourceLocation spriteLocation, @Nullable PoseStack pose, float x, float y, float z, float width, float height, float minU, float minV, float maxU,
			float maxV, Color color) {
		RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
		RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
		RenderSystem.enableBlend();
		TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(spriteLocation);
		Matrix4f matrix = pose == null ? SDMath.IDENTITY : pose.last().pose();

		float uDiff = sprite.getU1() - sprite.getU0();
		float vDiff = sprite.getV1() - sprite.getV0();

		float minSpriteU = sprite.getU0() + (minU * uDiff);
		float maxSpriteU = sprite.getU1() - ((1.0f - maxU) * vDiff);
		float minSpriteV = sprite.getV0() + (minV * vDiff);
		float maxSpriteV = sprite.getV1() - ((1.0f - maxV) * vDiff);

		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuilder();
		bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
		bufferbuilder.vertex(matrix, (x + 0), (y + height), z).color(color.getX(), color.getY(), color.getZ(), color.getW()).uv(minSpriteU, maxSpriteV).endVertex();
		bufferbuilder.vertex(matrix, (x + width), (y + height), z).color(color.getX(), color.getY(), color.getZ(), color.getW()).uv(maxSpriteU, maxSpriteV).endVertex();
		bufferbuilder.vertex(matrix, (x + width), (y + 0), z).color(color.getX(), color.getY(), color.getZ(), color.getW()).uv(maxSpriteU, minSpriteV).endVertex();
		bufferbuilder.vertex(matrix, (x + 0), (y + 0), z).color(color.getX(), color.getY(), color.getZ(), color.getW()).uv(minSpriteU, minSpriteV).endVertex();
		tessellator.end();
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

	public static Vector2D translatePositionByMatrix(@Nullable PoseStack matrixStack, Vector2D position) {
		return translatePositionByMatrix(matrixStack, position.getX(), position.getY());
	}

	public static Vector2D translatePositionByMatrix(@Nullable PoseStack matrixStack, float xPos, float yPos) {
		if (matrixStack != null) {
			Vector4f vector4f = new Vector4f(xPos, yPos, 0, 1);
			vector4f.transform(matrixStack.last().pose());
			return new Vector2D(vector4f.x(), vector4f.y());
		} else {
			return new Vector2D(xPos, yPos);
		}
	}

	public static void drawDefaultButton(boolean hovered, float x, float y, float width, float height, float zLevel) {
		float uPixel = 1.0f / 200.0f;
		float vPixel = 1.0f / 20.0f;

		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		if (hovered) {
			RenderSystem.setShaderTexture(0, GuiTextures.BUTTON_HOVER);
		} else {
			RenderSystem.setShaderTexture(0, GuiTextures.BUTTON);
		}

		// Body
		drawTexturedGenericRect(x + 2, y + 2, width - 4, height - 5, zLevel, uPixel * 2, vPixel * 2, uPixel * 198, vPixel * 17);

		// Corners
		drawTexturedGenericRect(x, y, 2, 2, zLevel, 0.0f, 0.0f, 2 * uPixel, 2 * vPixel);
		drawTexturedGenericRect(x + width - 2, y, 2, 2, zLevel, 198 * uPixel, 0, 1, 2 * vPixel);
		drawTexturedGenericRect(x, y + height - 3, 2, 3, zLevel, 0.0f, 17 * vPixel, 2 * uPixel, 20 * vPixel);
		drawTexturedGenericRect(x + width - 2, y + height - 3, 2, 3, zLevel, 198 * uPixel, 17 * vPixel, 1, 20 * vPixel);

		// Sides
		drawTexturedGenericRect(x + 2, y, width - 4, 2, zLevel, 2 * uPixel, 0, 198 * uPixel, 2 * vPixel);
		drawTexturedGenericRect(x, y + 2, 2, height - 5, zLevel, 0.0f, 2 * vPixel, 2 * uPixel, 17 * vPixel);
		drawTexturedGenericRect(x + width - 2, y + 2, 2, height - 5, zLevel, 198 * uPixel, 2 * vPixel, 1, 17 * vPixel);
		drawTexturedGenericRect(x + 2, y + height - 3, width - 4, 3, zLevel, 2 * uPixel, 17 * vPixel, 198 * uPixel, 20 * vPixel);
	}
}
