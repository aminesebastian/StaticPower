package theking530.common.gui;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import theking530.common.utilities.Color;

public class GuiDrawUtilities {
	private static final float genericBackgroundPixel = 1.0f / 9.0f;
	private static final Color defaultBackgroundColor = new Color(198, 198, 198, 255).fromEightBitToFloat();
	private static final Color defaultBackgroundRimTint = new Color(255, 255, 255, 255).fromEightBitToFloat();
	private static final Color defaultSlotDarkEdgeColor = new Color(55, 55, 55, 255).fromEightBitToFloat();
	private static final Color defaultSlotLightEdgeColor = new Color(255, 255, 255, 255).fromEightBitToFloat();
	private static final Color defaultSlotCornerColor = new Color(139, 139, 139, 255).fromEightBitToFloat();

	public static void drawGenericBackground(int width, int height, int guiLeft, int guiTop, Color mainBackgroundColor, Color rimTint) {
		float zLevel = 0.0f;

		// MainBG
		drawColoredRectangle(guiLeft + 3, guiTop + 3, width - 4, height - 4, zLevel, mainBackgroundColor);

		Minecraft.getInstance().getTextureManager().bindTexture(GuiTextures.GENERIC_GUI);

		// Corners
		drawTexturedGenericRect(guiLeft, guiTop, 4, 4, 0.0f, 0.0f, zLevel, 4 * genericBackgroundPixel, 4 * genericBackgroundPixel);
		drawTexturedGenericRect(guiLeft + width - 5, guiTop, 5, 4, zLevel, 4 * genericBackgroundPixel, 0.0f, 9 * genericBackgroundPixel, 4 * genericBackgroundPixel);
		drawTexturedGenericRect(guiLeft, guiTop + height - 5, 4, 5, 0.0f, zLevel, 4 * genericBackgroundPixel, 4 * genericBackgroundPixel, 9 * genericBackgroundPixel);
		drawTexturedGenericRect(guiLeft + width - 4, guiTop + height - 4, 4, 4, zLevel, 5 * genericBackgroundPixel, 5 * genericBackgroundPixel, 9 * genericBackgroundPixel, 9 * genericBackgroundPixel);

		// Sides
		drawTexturedGenericRect(guiLeft + 4, guiTop, width - 7, 3, zLevel, 3 * genericBackgroundPixel, 0.0f, 4 * genericBackgroundPixel, 3 * genericBackgroundPixel);
		drawTexturedGenericRect(guiLeft, guiTop + 4, 3, height - 8, 0.0f, zLevel, 3 * genericBackgroundPixel, 3 * genericBackgroundPixel, 4 * genericBackgroundPixel);
		drawTexturedGenericRect(guiLeft + 4, guiTop + height - 3, width - 8, 3, zLevel, 4 * genericBackgroundPixel, 6 * genericBackgroundPixel, 5 * genericBackgroundPixel, 9 * genericBackgroundPixel);
		drawTexturedGenericRect(guiLeft + width - 3, guiTop + 4, 3, height - 8, zLevel, 6 * genericBackgroundPixel, 3 * genericBackgroundPixel, 9 * genericBackgroundPixel, 4 * genericBackgroundPixel);
	}

	public static void drawGenericBackground(int width, int height, int guiLeft, int guiTop) {
		drawGenericBackground(width, height, guiLeft, guiTop, defaultBackgroundColor, defaultBackgroundRimTint);
	}

	public static void drawPlayerInventorySlots(int xPos, int yPos) {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				drawSlot(xPos + j * 18, yPos + 1 + i * 18, 16, 16);
			}
		}
		for (int i = 0; i < 9; i++) {
			drawSlot(xPos + i * 18, yPos + 59, 16, 16);
		}
	}

	public static void drawSlot(float xPos, float yPos, float width, float height, Color color) {
		float zLevel = 0.0f;

		if (color != null) {
			drawColoredRectangle(xPos - 2, yPos - 2, width + 4, height + 4, zLevel, color);
		}

		drawColoredRectangle(xPos - 1, yPos - 1, 1, 1 + height, zLevel, defaultSlotDarkEdgeColor);
		drawColoredRectangle(xPos, yPos - 1, width, 1, zLevel, defaultSlotDarkEdgeColor);
		drawColoredRectangle(xPos + width, yPos - 1, 1, 1, zLevel, defaultSlotCornerColor);

		drawColoredRectangle(xPos - 1, yPos + height, 1, 1, zLevel, defaultSlotCornerColor);
		drawColoredRectangle(xPos, yPos + height, width + 1, 1, zLevel, defaultSlotLightEdgeColor);
		drawColoredRectangle(xPos + width, yPos, 1, height, zLevel, defaultSlotLightEdgeColor);
		drawColoredRectangle(xPos, yPos, width, height, zLevel, defaultSlotCornerColor);
	}

	public static void drawSlot(float xPos, float yPos, float width, float height) {
		drawSlot(xPos, yPos, width, height, null);
	}

	public void drawVerticalBar(float xPos, float yPos, float width, float height, float fillAmount, Color color) {
		drawSlot(xPos, yPos, width, height);
		int filledHeight = (int) (fillAmount * height);
		float zLevel = 0.0f;
		drawColoredRectangle(xPos, yPos + (height - filledHeight), xPos + width, yPos + height, zLevel, color);

	}

	public static void drawTexturedGenericRect(float xCoord, float yCoord, float width, float height, float zLevel, float minU, float minV, float maxU, float maxV) {
		;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos(xCoord, yCoord + height, zLevel).tex(minU, maxV).endVertex();
		bufferbuilder.pos(xCoord + width, yCoord + height, zLevel).tex(maxU, maxV).endVertex();
		bufferbuilder.pos(xCoord + width, yCoord, zLevel).tex(maxU, minV).endVertex();
		bufferbuilder.pos(xCoord, yCoord, zLevel).tex(minU, minV).endVertex();
		tessellator.draw();
	}

	public static void drawColoredRectangle(float xCoord, float yCoord, float width, float height, float zLevel, Color color) {
		GlStateManager.disableTexture();
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

	public static void drawStringWithSize(String text, int xPos, int yPos, float scale, int color, boolean withShadow) {
//		int textX = (int) ((xPos - Minecraft.getInstance().fontRenderer.getStringWidth(text) * scale) / scale) - 1;
//		int textY = (int) ((yPos - 7 * scale) / scale) - 1;
//
//		GlStateManager.disableLighting();
//		GlStateManager.disableDepthTest();
//		GlStateManager.disableBlend();
//		GlStateManager.pushMatrix();
//		GlStateManager.scalef(scale, scale, scale);
//
//		if (withShadow) {
//			Minecraft.getInstance().fontRenderer.drawStringWithShadow(text, textX, textY, color);
//		} else {
//			Minecraft.getInstance().fontRenderer.drawString(text, textX, textY, color);
//		}
//
//		GlStateManager.popMatrix();
	}

	public static void drawTexturedModalRect(float x, float y, float width, float height, float minU, float minV, float maxU, float maxV) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos((double) (x + 0), (double) (y + height), 0.0).tex(minU, maxV).endVertex();
		bufferbuilder.pos((double) (x + width), (double) (y + height), 0.0).tex(maxU, maxV).endVertex();
		bufferbuilder.pos((double) (x + width), (double) (y + 0), 0.0).tex(maxU, minV).endVertex();
		bufferbuilder.pos((double) (x + 0), (double) (y + 0), 0.0).tex(minU, minV).endVertex();
		tessellator.draw();
	}

	public static void drawTexturedModalRect(float x, float y, float width, float height, float minU, float minV, float maxU, float maxV, float texetSize) {
		drawTexturedModalRect(x, y, width, height, minU * texetSize, minV * texetSize, maxU * texetSize, maxV * texetSize);
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

	public static void drawDefaultButton(boolean hovered, float x, float y, float width, float height, float zLevel) {
		float uPixel = 1.0f / 200.0f;
		float vPixel = 1.0f / 20.0f;

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder tes = tessellator.getBuffer();
		BufferBuilder vertexbuffer = tessellator.getBuffer();
		tes.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

		if (hovered) {
			Minecraft.getInstance().getTextureManager().bindTexture(GuiTextures.BUTTON_HOVER);
		} else {
			Minecraft.getInstance().getTextureManager().bindTexture(GuiTextures.BUTTON);
		}

		// Top
		vertexbuffer.pos(x + width, y + 2, 0).tex(0, vPixel * 2).endVertex();
		vertexbuffer.pos(x + width, y, 0).tex(0, 0).endVertex();
		vertexbuffer.pos(x, y, 0).tex(1, 0).endVertex();
		vertexbuffer.pos(x, y + 2, 0).tex(1, vPixel * 2).endVertex();

		// Bottom
		vertexbuffer.pos(x + width, y + (height), 0).tex(0, vPixel * 20).endVertex();
		vertexbuffer.pos(x + width, y + (height - 3), 0).tex(0, vPixel * 17).endVertex();
		vertexbuffer.pos(x, y + (height - 3), 0).tex(1, vPixel * 17).endVertex();
		vertexbuffer.pos(x, y + (height), 0).tex(1, vPixel * 20).endVertex();

		// Right
		vertexbuffer.pos(x + width, y + (height), 0).tex(0, vPixel * 20).endVertex();
		vertexbuffer.pos(x + width, y, 0).tex(0, 0).endVertex();
		vertexbuffer.pos(x - 2 + width, y, 0).tex(uPixel * 2, 0).endVertex();
		vertexbuffer.pos(x - 2 + width, y + (height), 0).tex(uPixel * 2, vPixel * 20).endVertex();

		// Left
		vertexbuffer.pos(x + 2, y + (height), 0).tex(uPixel * 198, 1).endVertex();
		vertexbuffer.pos(x + 2, y, 0).tex(uPixel * 198, 0).endVertex();
		vertexbuffer.pos(x, y, 0).tex(1, 0).endVertex();
		vertexbuffer.pos(x, y + (height), 0).tex(1, 1).endVertex();

		// Body
		vertexbuffer.pos(x + width - 2, y - 3 + (height), 0).tex(uPixel * 2, vPixel * 17).endVertex();
		vertexbuffer.pos(x + width - 2, y + 2, 0).tex(uPixel * 2, vPixel * 2).endVertex();
		vertexbuffer.pos(x + 2, y + 2, 0).tex(uPixel * 198, vPixel * 2).endVertex();
		vertexbuffer.pos(x + 2, y - 3 + (height), 0).tex(uPixel * 198, vPixel * 17).endVertex();

		tessellator.draw();
	}
}
