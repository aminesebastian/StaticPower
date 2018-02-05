package theking530.staticpower.client.gui.widgets.valuebars;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import theking530.staticpower.assists.GuiTextures;
import theking530.staticpower.assists.utilities.GuiUtilities;

public class GuiPowerBar {
	
	private static float GLOW_SCALE_RATE;
	private static float WORLD_TIME;
	
	public GuiPowerBar() {
		GLOW_SCALE_RATE = 0.20f;
		WORLD_TIME = 0.0f;
	}
	
	public static List<String> drawText(int currentEnergy, int maxEnergy, int energyPerTick, int powerUse) {
    	String text = ("Input: " + energyPerTick + " RF/t");
    	if(powerUse > 0) {
    		text += "=" + "Usage: " + powerUse + " RF/t";
    	}
    	text += "=" + NumberFormat.getNumberInstance(Locale.US).format(currentEnergy)  + "/" + NumberFormat.getNumberInstance(Locale.US).format(maxEnergy) + " " + "RF";
    	String[] splitMsg = text.split("=");
		return Arrays.asList(splitMsg);
	}
	public static void drawPowerBar(int xpos, int ypos, int width, int height, float zLevel, int currentEnergy, int maxEnergy, float deltaTime) {
		float u1 = (float)currentEnergy/(float)maxEnergy;
		float k1 = u1 * height;
		float glowState = getGlow(deltaTime);
		
		Gui.drawRect(xpos, ypos, xpos + width, ypos-height, GuiUtilities.getColor(15, 15, 15));
		
		Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();
		Minecraft.getMinecraft().getTextureManager().bindTexture(GuiTextures.POWER_BAR);
		GlStateManager.color(glowState, glowState, glowState);
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		vertexbuffer.pos(xpos + width, ypos, zLevel).tex(1,0).endVertex();
		vertexbuffer.pos(xpos + width, ypos - k1, zLevel).tex(1,u1).endVertex();
		vertexbuffer.pos(xpos, ypos - k1, zLevel).tex(0,u1).endVertex();
		vertexbuffer.pos(xpos, ypos, zLevel).tex(0,0).endVertex();	
		GlStateManager.color(1.0f, 1.0f, 1.0f);
		tessellator.draw();


	}
	private static float getGlow(float deltaTime) {
		WORLD_TIME += deltaTime;
		float sin = (float)(Math.sin(WORLD_TIME*GLOW_SCALE_RATE));
		sin = (sin + 5)/5;

		return Math.min(sin, 1);
	}
}	
