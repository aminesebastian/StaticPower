package theking530.staticcore.gui.widgets.valuebars;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.text.ITextComponent;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.client.gui.GuiTextures;
import theking530.staticpower.client.utilities.GuiTextUtilities;

public class GuiHeatBarUtilities {
	public static List<ITextComponent> getTooltip(double currentHeat, double maxHeat, double heatPerTick) {
		List<ITextComponent> tooltip = new ArrayList<ITextComponent>();

		// Show the total amount of energy remaining / total energy capacity.
		tooltip.add(GuiTextUtilities.formatHeatToString(currentHeat, maxHeat));

		return tooltip;
	}

	public static void drawHeatBar(MatrixStack stack, float xpos, float ypos, float width, float height, float zLevel, double currentHeat, double maxHeat) {
		float u1 = (float) (currentHeat / maxHeat);
		float k1 = u1 * height;

		GuiDrawUtilities.drawSlot(stack, xpos, ypos - height, width, height, 0);

		// Get the origin.
		Vector2D origin = GuiDrawUtilities.translatePositionByMatrix(stack, xpos, ypos);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuffer();
		Minecraft.getInstance().getTextureManager().bindTexture(GuiTextures.HEAT_BAR_BG);
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		vertexbuffer.pos(origin.getX() + width, origin.getY(), zLevel).tex(1, 0).endVertex();
		vertexbuffer.pos(origin.getX() + width, origin.getY() - height, zLevel).tex(1.0f, 1.0f).endVertex();
		vertexbuffer.pos(origin.getX(), origin.getY() - height, zLevel).tex(0.0f, 1.0f).endVertex();
		vertexbuffer.pos(origin.getX(), origin.getY(), zLevel).tex(0, 0).endVertex();
		tessellator.draw();

		float glowState = (float) (Math.sin((float) Minecraft.getInstance().world.getGameTime() / 10.0f));
		glowState = Math.abs(glowState);
		glowState *= 2.0f;
		glowState += 8.0f;
		glowState /= 2.0f;
		
		Minecraft.getInstance().getTextureManager().bindTexture(GuiTextures.HEAT_BAR_FG);
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		vertexbuffer.pos(origin.getX() + width, origin.getY(), zLevel).color(glowState, glowState, glowState, 1.0f).tex(1, 0).endVertex();
		vertexbuffer.pos(origin.getX() + width, origin.getY() - k1, zLevel).color(glowState, glowState, glowState, 1.0f).tex(1, u1).endVertex();
		vertexbuffer.pos(origin.getX(), origin.getY() - k1, zLevel).color(glowState, glowState, glowState, 1.0f).tex(0, u1).endVertex();
		vertexbuffer.pos(origin.getX(), origin.getY(), zLevel).color(glowState, glowState, glowState, 1.0f).tex(0, 0).endVertex();
		tessellator.draw();
	}
}
