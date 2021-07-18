package theking530.staticcore.gui;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.utilities.Color;
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

	public static void drawGenericBackground(int width, int height, int guiLeft, int guiTop, float zLevel, Color mainBackgroundColor, Color rimTint, boolean drawLeft, boolean drawRight,
			boolean drawTop, boolean drawBottom) {
		// MainBG
		drawColoredRectangle(guiLeft + 3, guiTop + 3, width - 4, height - 4, zLevel, mainBackgroundColor);

		Minecraft.getInstance().getTextureManager().bindTexture(GuiTextures.GENERIC_GUI);

		// Corners
		drawTexturedGenericRect(guiLeft, guiTop, 4, 4, 0.0f, 0.0f, zLevel, 4 * BACKGROUND_PIXEL_SIZE, 4 * BACKGROUND_PIXEL_SIZE);
		drawTexturedGenericRect(guiLeft + width - 5, guiTop, 5, 4, zLevel, 4 * BACKGROUND_PIXEL_SIZE, 0.0f, 9 * BACKGROUND_PIXEL_SIZE, 4 * BACKGROUND_PIXEL_SIZE);
		drawTexturedGenericRect(guiLeft, guiTop + height - 5, 4, 5, 0.0f, zLevel, 4 * BACKGROUND_PIXEL_SIZE, 4 * BACKGROUND_PIXEL_SIZE, 9 * BACKGROUND_PIXEL_SIZE);
		drawTexturedGenericRect(guiLeft + width - 4, guiTop + height - 4, 4, 4, zLevel, 5 * BACKGROUND_PIXEL_SIZE, 5 * BACKGROUND_PIXEL_SIZE, 9 * BACKGROUND_PIXEL_SIZE,
				9 * BACKGROUND_PIXEL_SIZE);

		// Sides
		if (drawTop) {
			drawTexturedGenericRect(guiLeft + 4, guiTop, width - 7, 3, zLevel, 3 * BACKGROUND_PIXEL_SIZE, 0.0f, 4 * BACKGROUND_PIXEL_SIZE, 3 * BACKGROUND_PIXEL_SIZE);
		}
		if (drawLeft) {
			drawTexturedGenericRect(guiLeft, guiTop + 4, 3, height - 8, 0.0f, zLevel, 3 * BACKGROUND_PIXEL_SIZE, 3 * BACKGROUND_PIXEL_SIZE, 4 * BACKGROUND_PIXEL_SIZE);
		}
		if (drawRight) {
			drawTexturedGenericRect(guiLeft + width - 3, guiTop + 4, 3, height - 8, zLevel, 6 * BACKGROUND_PIXEL_SIZE, 3 * BACKGROUND_PIXEL_SIZE, 9 * BACKGROUND_PIXEL_SIZE,
					4 * BACKGROUND_PIXEL_SIZE);
		}
		if (drawBottom) {
			drawTexturedGenericRect(guiLeft + 4, guiTop + height - 3, width - 8, 3, zLevel, 4 * BACKGROUND_PIXEL_SIZE, 6 * BACKGROUND_PIXEL_SIZE, 5 * BACKGROUND_PIXEL_SIZE,
					9 * BACKGROUND_PIXEL_SIZE);
		}
	}

	public static void drawGenericBackground(int width, int height, int guiLeft, int guiTop, float zLevel) {
		drawGenericBackground(width, height, guiLeft, guiTop, zLevel, DEFAULT_BACKGROUND_COLOR, DEFAULT_BACKGROUND_EDGE_TINT, true, true, true, true);
	}

	public static void drawGenericBackground(int width, int height, int guiLeft, int guiTop) {
		drawGenericBackground(width, height, guiLeft, guiTop, 0.0f, DEFAULT_BACKGROUND_COLOR, DEFAULT_BACKGROUND_EDGE_TINT, true, true, true, true);
	}

	public static void drawPlayerInventorySlots(MatrixStack matrixStack, int xPos, int yPos) {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				drawSlot(matrixStack, xPos + j * 18, yPos + 1 + i * 18, 16, 16, 0);
			}
		}
		for (int i = 0; i < 9; i++) {
			drawSlot(matrixStack, xPos + i * 18, yPos + 59, 16, 16, 0);
		}
	}

	public static void drawSlot(@Nullable MatrixStack matrixStack, float xPos, float yPos, float width, float height, float zLevel, Color color) {
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

	public static void drawSlot(@Nullable MatrixStack matrixStack, float xPos, float yPos, float width, float height, float zLevel) {
		drawSlot(matrixStack, xPos, yPos, width, height, zLevel, null);
	}

	public void drawVerticalBar(MatrixStack matrixStack, float xPos, float yPos, float width, float height, float fillAmount, Color color) {
		drawSlot(null, xPos, yPos, width, height, 0);
		int filledHeight = (int) (fillAmount * height);
		float zLevel = 0.0f;
		drawColoredRectangle(xPos, yPos + (height - filledHeight), xPos + width, yPos + height, zLevel, color);
	}

	public static void drawTexturedGenericRect(float xCoord, float yCoord, float width, float height, float zLevel, float minU, float minV, float maxU, float maxV) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos(xCoord, yCoord + height, zLevel).tex(minU, maxV).endVertex();
		bufferbuilder.pos(xCoord + width, yCoord + height, zLevel).tex(maxU, maxV).endVertex();
		bufferbuilder.pos(xCoord + width, yCoord, zLevel).tex(maxU, minV).endVertex();
		bufferbuilder.pos(xCoord, yCoord, zLevel).tex(minU, minV).endVertex();
		tessellator.draw();
	}

	public static void drawColoredRectangle(@Nullable MatrixStack matrixStack, float xCoord, float yCoord, float width, float height, float zLevel, Color color) {
		Vector2D origin = translatePositionByMatrix(matrixStack, xCoord, yCoord);
		drawColoredRectangle(origin.getX(), origin.getY(), width, height, zLevel, color);

	}

	public static void drawColoredRectangle(float xCoord, float yCoord, float width, float height, float zLevel, Color color) {
		GlStateManager.disableTexture();
		GlStateManager.enableBlend();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
		bufferbuilder.pos(xCoord, yCoord + height, zLevel).color(color.getX(), color.getY(), color.getZ(), color.getW()).endVertex();
		bufferbuilder.pos(xCoord + width, yCoord + height, zLevel).color(color.getX(), color.getY(), color.getZ(), color.getW()).endVertex();
		bufferbuilder.pos(xCoord + width, yCoord, zLevel).color(color.getX(), color.getY(), color.getZ(), color.getW()).endVertex();
		bufferbuilder.pos(xCoord, yCoord, zLevel).color(color.getX(), color.getY(), color.getZ(), color.getW()).endVertex();
		tessellator.draw();
		GlStateManager.enableTexture();
	}

	public static void drawStringWithSize(@Nonnull MatrixStack matrixStack, String text, float xPos, float yPos, float scale, Color color, boolean withShadow) {
		drawStringWithSize(matrixStack, text, xPos, yPos, scale, color.encodeInInteger(), withShadow);
	}

	public static void drawStringWithSizeCentered(@Nonnull MatrixStack matrixStack, String text, float xPos, float yPos, float scale, Color color, boolean withShadow) {
		float width = Minecraft.getInstance().fontRenderer.getStringWidth(text) * scale;
		drawStringWithSize(matrixStack, text, xPos + (width / 2), yPos, scale, color.encodeInInteger(), withShadow);
	}

	public static void drawStringWithSizeLeftAligned(@Nonnull MatrixStack matrixStack, String text, float xPos, float yPos, float scale, Color color, boolean withShadow) {
		float width = Minecraft.getInstance().fontRenderer.getStringWidth(text) * scale;
		drawStringWithSize(matrixStack, text, xPos + width, yPos, scale, color.encodeInInteger(), withShadow);
	}

	public static void drawStringWithSize(@Nonnull MatrixStack matrixStack, String text, float xPos, float yPos, float scale, TextFormatting color, boolean withShadow) {
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
	public static void drawStringWithSize(@Nonnull MatrixStack matrixStack, String text, float xPos, float yPos, float scale, int color, boolean withShadow) {
		// The matrix stack cannot be null.
		if (matrixStack == null) {
			StaticPower.LOGGER.error("A non-null matrix stack must be provided to this method!");
			return;
		}

		final float scaleFactor = scale;
		final float inverseScaleFactor = 1.0f / scaleFactor;
		final int offset = 0;

		matrixStack.push();
		matrixStack.scale(scaleFactor, scaleFactor, 1.0f);

		RenderSystem.disableBlend();
		final int X = (int) ((xPos + offset - Minecraft.getInstance().fontRenderer.getStringWidth(text) * scaleFactor) * inverseScaleFactor);
		final int Y = (int) ((yPos + offset - 7.0f * scaleFactor) * inverseScaleFactor);
		IRenderTypeBuffer.Impl buffer = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
		Minecraft.getInstance().fontRenderer.renderString(text, X, Y, color, withShadow, matrixStack.getLast().getMatrix(), buffer, true, 0, 15728880);
		buffer.finish();
		RenderSystem.enableBlend();
		matrixStack.pop();
	}

	public static void drawTexturedModalRect(ResourceLocation texture, float x, float y, float width, float height, float minU, float minV, float maxU, float maxV) {
		Minecraft.getInstance().getTextureManager().bindTexture(texture);
		GlStateManager.enableBlend();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos((double) (x + 0), (double) (y + height), 0.0).tex(minU, maxV).endVertex();
		bufferbuilder.pos((double) (x + width), (double) (y + height), 0.0).tex(maxU, maxV).endVertex();
		bufferbuilder.pos((double) (x + width), (double) (y + 0), 0.0).tex(maxU, minV).endVertex();
		bufferbuilder.pos((double) (x + 0), (double) (y + 0), 0.0).tex(minU, minV).endVertex();
		tessellator.draw();
	}

	public static void drawTexturedModalRect(ResourceLocation texture, float x, float y, float width, float height, float minU, float minV, float maxU, float maxV, Color color) {
		Minecraft.getInstance().getTextureManager().bindTexture(texture);
		GlStateManager.enableBlend();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX);
		bufferbuilder.pos((double) (x + 0), (double) (y + height), 0.0).color(color.getX(), color.getY(), color.getZ(), color.getW()).tex(minU, maxV).endVertex();
		bufferbuilder.pos((double) (x + width), (double) (y + height), 0.0).color(color.getX(), color.getY(), color.getZ(), color.getW()).tex(maxU, maxV).endVertex();
		bufferbuilder.pos((double) (x + width), (double) (y + 0), 0.0).color(color.getX(), color.getY(), color.getZ(), color.getW()).tex(maxU, minV).endVertex();
		bufferbuilder.pos((double) (x + 0), (double) (y + 0), 0.0).color(color.getX(), color.getY(), color.getZ(), color.getW()).tex(minU, minV).endVertex();
		tessellator.draw();
	}

	public static TextureAtlasSprite getStillFluidSprite(FluidStack fluidStack) {
		Fluid fluid = fluidStack.getFluid();
		FluidAttributes attributes = fluid.getAttributes();
		ResourceLocation fluidStill = attributes.getStillTexture(fluidStack);
		return Minecraft.getInstance().getAtlasSpriteGetter(PlayerContainer.LOCATION_BLOCKS_TEXTURE).apply(fluidStill);
	}

	public static Color getFluidColor(FluidStack fluid) {
		FluidAttributes attributes = fluid.getFluid().getAttributes();
		int encodedFluidColor = attributes.getColor(fluid);
		return Color.fromEncodedInteger(encodedFluidColor).fromEightBitToFloat();
	}

	public static Vector2D translatePositionByMatrix(@Nullable MatrixStack matrixStack, Vector2D position) {
		return translatePositionByMatrix(matrixStack, position.getX(), position.getY());
	}

	public static Vector2D translatePositionByMatrix(@Nullable MatrixStack matrixStack, float xPos, float yPos) {
		if (matrixStack != null) {
			Vector4f vector4f = new Vector4f(xPos, yPos, 0, 1);
			vector4f.transform(matrixStack.getLast().getMatrix());
			return new Vector2D(vector4f.getX(), vector4f.getY());
		} else {
			return new Vector2D(xPos, yPos);
		}
	}

	public static void drawDefaultButton(boolean hovered, float x, float y, float width, float height, float zLevel) {
		float uPixel = 1.0f / 200.0f;
		float vPixel = 1.0f / 20.0f;

		if (hovered) {
			Minecraft.getInstance().getTextureManager().bindTexture(GuiTextures.BUTTON_HOVER);
		} else {
			Minecraft.getInstance().getTextureManager().bindTexture(GuiTextures.BUTTON);
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
