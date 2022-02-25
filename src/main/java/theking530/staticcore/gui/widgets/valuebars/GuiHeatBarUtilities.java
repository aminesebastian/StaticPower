package theking530.staticcore.gui.widgets.valuebars;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import theking530.staticcore.gui.GuiDrawUtilities;
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

		GuiDrawUtilities.drawSlot(stack, width, height, xpos, ypos, 0);

		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuilder();
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, GuiTextures.HEAT_BAR_BG);
		vertexbuffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		vertexbuffer.vertex(stack.last().pose(), xpos + width, ypos + height, zLevel).uv(1, 0).endVertex();
		vertexbuffer.vertex(stack.last().pose(), xpos + width, ypos, zLevel).uv(1.0f, 1.0f).endVertex();
		vertexbuffer.vertex(stack.last().pose(), xpos, ypos, zLevel).uv(0.0f, 1.0f).endVertex();
		vertexbuffer.vertex(stack.last().pose(), xpos, ypos + height, zLevel).uv(0, 0).endVertex();
		tessellator.end();

		float glowState = (float) (Math.sin((float) Minecraft.getInstance().level.getGameTime() / 10.0f));
		glowState = Math.abs(glowState);
		glowState *= 2.0f;
		glowState += 8.0f;
		glowState /= 2.0f;

		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, GuiTextures.HEAT_BAR_FG);
		vertexbuffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		vertexbuffer.vertex(stack.last().pose(), xpos + width, ypos + height, zLevel).color(glowState, glowState, glowState, 1.0f).uv(1, 0).endVertex();
		vertexbuffer.vertex(stack.last().pose(), xpos + width, ypos + height - k1, zLevel).color(glowState, glowState, glowState, 1.0f).uv(1, u1).endVertex();
		vertexbuffer.vertex(stack.last().pose(), xpos, ypos + height - k1, zLevel).color(glowState, glowState, glowState, 1.0f).uv(0, u1).endVertex();
		vertexbuffer.vertex(stack.last().pose(), xpos, ypos + height, zLevel).color(glowState, glowState, glowState, 1.0f).uv(0, 0).endVertex();
		tessellator.end();
	}
}
