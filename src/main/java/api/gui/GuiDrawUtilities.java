package api.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import theking530.staticpower.assists.GuiTextures;
import theking530.staticpower.assists.utilities.GuiUtilities;

public class GuiDrawUtilities {
	private static final float genericBackgroundPixel = 1.0f/9.0f;
	
	public static void drawGenericBackground(int width, int height, int guiLeft, int guiTop, float zLevel) {		
		//MainBG
		Gui.drawRect(guiLeft+3, guiTop+3, guiLeft+width-3, guiTop+height-3, GuiUtilities.getColor(198, 198, 198));
		GL11.glColor3f(1.0f, 1.0f, 1.0f);
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(GuiTextures.GENERIC_GUI);
		GL11.glEnable(GL11.GL_BLEND);
		//Corners
		drawTexturedGenericRect(guiLeft, guiTop, 4, 4, 0.0f, 0.0f, zLevel, 4*genericBackgroundPixel, 4*genericBackgroundPixel);
		drawTexturedGenericRect(guiLeft+width-5, guiTop, 5, 4, zLevel, 4*genericBackgroundPixel, 0.0f, 9*genericBackgroundPixel, 4*genericBackgroundPixel);
		drawTexturedGenericRect(guiLeft, guiTop+height-5, 4, 5, 0.0f, zLevel, 4*genericBackgroundPixel, 4*genericBackgroundPixel, 9*genericBackgroundPixel);	
		drawTexturedGenericRect(guiLeft+width-4, guiTop+height-4, 4, 4, zLevel, 5*genericBackgroundPixel, 5*genericBackgroundPixel, 9*genericBackgroundPixel, 9*genericBackgroundPixel);

		//Sides
		drawTexturedGenericRect(guiLeft+4, guiTop, width-7, 3, zLevel, 3*genericBackgroundPixel, 0.0f, 4*genericBackgroundPixel, 3*genericBackgroundPixel);
		drawTexturedGenericRect(guiLeft, guiTop+4, 3, height-8, 0.0f, zLevel, 3*genericBackgroundPixel, 3*genericBackgroundPixel, 4*genericBackgroundPixel);
		drawTexturedGenericRect(guiLeft+4, guiTop+height-3, width-8, 3, zLevel, 4*genericBackgroundPixel, 6*genericBackgroundPixel, 5*genericBackgroundPixel, 9*genericBackgroundPixel);
		drawTexturedGenericRect(guiLeft+width-3, guiTop+4, 3, height-8, zLevel, 6*genericBackgroundPixel, 3*genericBackgroundPixel, 9*genericBackgroundPixel, 4*genericBackgroundPixel);
		GL11.glDisable(GL11.GL_BLEND);
	}
	public static void drawPlayerInventorySlots(int xPos, int yPos) {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				drawSlot(xPos + j * 18, yPos + 1 + i * 18, 16, 16);
			}
		}
		for (int i = 0; i < 9; i++) {
			drawSlot(xPos + i * 18, yPos+59, 16, 16);
		}
	}
	public static void drawSlot(int xPos, int yPos, int width, int height) {
		Gui.drawRect(xPos-1, yPos-1, xPos, yPos+height, GuiUtilities.getColor(55, 55, 55));
		Gui.drawRect(xPos, yPos-1, xPos+width, yPos, GuiUtilities.getColor(55, 55, 55));
		Gui.drawRect(xPos+width, yPos-1, xPos+width+1, yPos, GuiUtilities.getColor(139, 139, 139));
		
		Gui.drawRect(xPos-1, yPos+height, xPos, yPos+height+1, GuiUtilities.getColor(139, 139, 139));
		Gui.drawRect(xPos, yPos+height, xPos+width+1, yPos+height+1, GuiUtilities.getColor(255, 255, 255));
		Gui.drawRect(xPos+width, yPos, xPos+width+1, yPos+height+1, GuiUtilities.getColor(255, 255, 255));
		
		Gui.drawRect(xPos, yPos, xPos+width, yPos+height, GuiUtilities.getColor(139, 139, 139));
	}
    public static void drawTexturedGenericRect(int xCoord, int yCoord, int width, int height, float zLevel, double minU, double minV, double maxU, double maxV) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(xCoord, yCoord+height, zLevel).tex(minU, maxV).endVertex();
        bufferbuilder.pos(xCoord+width, yCoord+height, zLevel).tex(maxU, maxV).endVertex();
        bufferbuilder.pos(xCoord+width, yCoord, zLevel).tex(maxU, minV).endVertex();
        bufferbuilder.pos(xCoord, yCoord, zLevel).tex(minU, minV).endVertex();
        tessellator.draw();
    }
}
