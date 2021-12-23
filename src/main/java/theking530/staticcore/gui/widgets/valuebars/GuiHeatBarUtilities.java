package theking530.staticcore.gui.widgets.valuebars;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.network.chat.Component;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.client.gui.GuiTextures;
import theking530.staticpower.client.utilities.GuiTextUtilities;

public class GuiHeatBarUtilities {
	public static List<Component> getTooltip(double currentHeat, double maxHeat, double heatPerTick) {
		List<Component> tooltip = new ArrayList<Component>();

		// Show the total amount of energy remaining / total energy capacity.
		tooltip.add(GuiTextUtilities.formatHeatToString(currentHeat, maxHeat));

		return tooltip;
	}

	public static void drawHeatBar(PoseStack stack, float xpos, float ypos, float width, float height, float zLevel, double currentHeat, double maxHeat) {
		float u1 = (float) (currentHeat / maxHeat);
		float k1 = u1 * height;

		GuiDrawUtilities.drawSlot(stack, xpos, ypos - height, width, height, 0);

		// Get the origin.
		Vector2D origin = GuiDrawUtilities.translatePositionByMatrix(stack, xpos, ypos);

		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuilder();
		Minecraft.getInstance().getTextureManager().bindForSetup(GuiTextures.HEAT_BAR_BG);
		vertexbuffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		vertexbuffer.vertex(origin.getX() + width, origin.getY(), zLevel).uv(1, 0).endVertex();
		vertexbuffer.vertex(origin.getX() + width, origin.getY() - height, zLevel).uv(1.0f, 1.0f).endVertex();
		vertexbuffer.vertex(origin.getX(), origin.getY() - height, zLevel).uv(0.0f, 1.0f).endVertex();
		vertexbuffer.vertex(origin.getX(), origin.getY(), zLevel).uv(0, 0).endVertex();
		tessellator.end();

		float glowState = (float) (Math.sin((float) Minecraft.getInstance().level.getGameTime() / 10.0f));
		glowState = Math.abs(glowState);
		glowState *= 2.0f;
		glowState += 8.0f;
		glowState /= 2.0f;
		
		Minecraft.getInstance().getTextureManager().bindForSetup(GuiTextures.HEAT_BAR_FG);
		vertexbuffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		vertexbuffer.vertex(origin.getX() + width, origin.getY(), zLevel).color(glowState, glowState, glowState, 1.0f).uv(1, 0).endVertex();
		vertexbuffer.vertex(origin.getX() + width, origin.getY() - k1, zLevel).color(glowState, glowState, glowState, 1.0f).uv(1, u1).endVertex();
		vertexbuffer.vertex(origin.getX(), origin.getY() - k1, zLevel).color(glowState, glowState, glowState, 1.0f).uv(0, u1).endVertex();
		vertexbuffer.vertex(origin.getX(), origin.getY(), zLevel).color(glowState, glowState, glowState, 1.0f).uv(0, 0).endVertex();
		tessellator.end();
	}
}
