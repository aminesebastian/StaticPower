package theking530.staticcore.gui.widgets.valuebars;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.text.ITextComponent;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticpower.client.gui.GuiTextures;
import theking530.staticpower.client.utilities.GuiTextUtilities;

public class GuiHeatBarUtilities {
	public static List<ITextComponent> getTooltip(float currentHeat, float maxHeat, float energyTransferPerTick) {
		List<ITextComponent> tooltip = new ArrayList<ITextComponent>();

		// Show the total amount of energy remaining / total energy capacity.
		tooltip.add(GuiTextUtilities.formatHeatToString(currentHeat, maxHeat));

		return tooltip;
	}

	public static void drawHeatBar(float xpos, float ypos, float width, float height, float zLevel, float currentHeat, float maxHeat) {
		float u1 = currentHeat / maxHeat;
		float k1 = u1 * height;

		GuiDrawUtilities.drawSlot(xpos, ypos - height, width, height);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuffer();
		Minecraft.getInstance().getTextureManager().bindTexture(GuiTextures.HEAT_BAR_BG);
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		vertexbuffer.pos(xpos + width, ypos, zLevel).tex(1, 1).endVertex();
		vertexbuffer.pos(xpos + width, ypos - height, zLevel).tex(1.0f, 0.0f).endVertex();
		vertexbuffer.pos(xpos, ypos - height, zLevel).tex(0.0f, 0.0f).endVertex();
		vertexbuffer.pos(xpos, ypos, zLevel).tex(0, 1).endVertex();
		tessellator.draw();

		Minecraft.getInstance().getTextureManager().bindTexture(GuiTextures.HEAT_BAR_FG);
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		vertexbuffer.pos(xpos + width, ypos, zLevel).tex(1, u1).endVertex();
		vertexbuffer.pos(xpos + width, ypos - k1, zLevel).tex(1, 0).endVertex();
		vertexbuffer.pos(xpos, ypos - k1, zLevel).tex(0, 0).endVertex();
		vertexbuffer.pos(xpos, ypos, zLevel).tex(0, u1).endVertex();
		tessellator.draw();
	}
}
