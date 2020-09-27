package theking530.staticcore.gui.widgets.valuebars;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticpower.client.gui.GuiTextures;
import theking530.staticpower.client.utilities.GuiTextUtilities;

public class GuiPowerBarUtilities {
	public static List<ITextComponent> getTooltip(int currentEnergy, int maxEnergy, int energyPerTick) {
		List<ITextComponent> tooltip = new ArrayList<ITextComponent>();

		// Add the input rate to the tooltip.
		tooltip.add(new TranslationTextComponent("gui.staticpower.max_input").appendString(": ").append(GuiTextUtilities.formatEnergyRateToString(energyPerTick)));

		// Show the total amount of energy remaining / total energy capacity.
		tooltip.add(GuiTextUtilities.formatEnergyToString(currentEnergy, maxEnergy));

		return tooltip;
	}

	public static void drawPowerBar(float xpos, float ypos, float width, float height, float zLevel, int currentEnergy, int maxEnergy) {
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
		float sin = (float) (Math.sin((float) Minecraft.getInstance().world.getGameTime() / 25.0f));
		sin = Math.abs(sin);
		sin *= 2.0f;
		sin += 8.0f;
		sin /= 10.0f;
		return sin;
	}
}
