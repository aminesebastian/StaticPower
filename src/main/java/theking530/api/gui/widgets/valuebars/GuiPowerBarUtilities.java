package theking530.api.gui.widgets.valuebars;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import theking530.api.gui.GuiDrawUtilities;
import theking530.api.gui.GuiTextures;

public class GuiPowerBarUtilities {

	public static List<String> getTooltip(int currentEnergy, int maxEnergy, int energyPerTick, int powerUse) {
		String text = ("Input: " + energyPerTick + " RF/t");
		if (powerUse > 0) {
			text += "=" + "Usage: " + powerUse + " RF/t";
		}
		text += "=" + NumberFormat.getNumberInstance(Locale.US).format(currentEnergy) + "/" + NumberFormat.getNumberInstance(Locale.US).format(maxEnergy) + " " + "RF";
		String[] splitMsg = text.split("=");
		return Arrays.asList(splitMsg);
	}

	public static void drawPowerBar(int xpos, int ypos, int width, int height, float zLevel, int currentEnergy, int maxEnergy) {
		float u1 = (float) currentEnergy / (float) maxEnergy;
		float k1 = u1 * height;

		GuiDrawUtilities.drawSlot(xpos, ypos - height, width, height);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuffer();
		Minecraft.getInstance().getTextureManager().bindTexture(GuiTextures.POWER_BAR_BG);
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		vertexbuffer.pos(xpos + width, ypos, zLevel).tex(1, 0).endVertex();
		vertexbuffer.pos(xpos + width, ypos - height, zLevel).tex(1.0f, 1.0f).endVertex();
		vertexbuffer.pos(xpos, ypos - height, zLevel).tex(0.0f, 1.0f).endVertex();
		vertexbuffer.pos(xpos, ypos, zLevel).tex(0, 0).endVertex();
		tessellator.draw();

		float glowState = getPowerBarGlow();
		Minecraft.getInstance().getTextureManager().bindTexture(GuiTextures.POWER_BAR_FG);
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX);
		vertexbuffer.pos(xpos + width, ypos, zLevel).color(glowState, glowState, glowState, 1.0f).tex(1, 0).endVertex();
		vertexbuffer.pos(xpos + width, ypos - k1, zLevel).color(glowState, glowState, glowState, 1.0f).tex(1, u1).endVertex();
		vertexbuffer.pos(xpos, ypos - k1, zLevel).color(glowState, glowState, glowState, 1.0f).tex(0, u1).endVertex();
		vertexbuffer.pos(xpos, ypos, zLevel).color(glowState, glowState, glowState, 1.0f).tex(0, 0).endVertex();
		tessellator.draw();
	}

	private static float getPowerBarGlow() {
		float sin = (float) (Math.sin((float) 1.0f / 1000.0f));

		sin = Math.abs(sin);
		sin += 3;
		sin /= 6.0f;
		return sin * 1.5f;
	}
}
