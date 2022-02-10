package theking530.staticcore.gui.widgets.valuebars;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.client.gui.GuiTextures;
import theking530.staticpower.client.utilities.GuiTextUtilities;

public class GuiPowerBarUtilities {
	public static List<Component> getTooltip(long currentEnergy, long maxEnergy, double energyPerTick) {
		List<Component> tooltip = new ArrayList<Component>();

		// Add the input rate to the tooltip.
		tooltip.add(new TranslatableComponent("gui.staticpower.max_input").append(": ")
				.append(GuiTextUtilities.formatEnergyRateToString(energyPerTick)));

		// Show the total amount of energy remaining / total energy capacity.
		tooltip.add(GuiTextUtilities.formatEnergyToString(currentEnergy, maxEnergy));

		return tooltip;
	}

	public static void drawPowerBar(@Nullable PoseStack matrixStack, float xpos, float ypos, float width, float height,
			float zLevel, long currentEnergy, long maxEnergy) {
		float u1 = (float) currentEnergy / (float) maxEnergy;
		float k1 = u1 * height;

		GuiDrawUtilities.drawSlot(matrixStack, xpos, ypos - height, width, height, 0);

		// Get the origin.
		Vector2D origin = GuiDrawUtilities.translatePositionByMatrix(matrixStack, xpos, ypos);
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, GuiTextures.POWER_BAR_BG);

		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuilder();
		vertexbuffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		vertexbuffer.vertex(origin.getX() + width, origin.getY(), zLevel).uv(1, 0).endVertex();
		vertexbuffer.vertex(origin.getX() + width, origin.getY() - height, zLevel).uv(1.0f, 1.0f).endVertex();
		vertexbuffer.vertex(origin.getX(), origin.getY() - height, zLevel).uv(0.0f, 1.0f).endVertex();
		vertexbuffer.vertex(origin.getX(), origin.getY(), zLevel).uv(0, 0).endVertex();
		tessellator.end();

		RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
		RenderSystem.setShaderTexture(0, GuiTextures.POWER_BAR_FG);
		float glowState = getPowerBarGlow();
		vertexbuffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
		vertexbuffer.vertex(origin.getX() + width, origin.getY(), zLevel).color(glowState, glowState, glowState, 1.0f)
				.uv(1, 0).endVertex();
		vertexbuffer.vertex(origin.getX() + width, origin.getY() - k1, zLevel)
				.color(glowState, glowState, glowState, 1.0f).uv(1, u1).endVertex();
		vertexbuffer.vertex(origin.getX(), origin.getY() - k1, zLevel).color(glowState, glowState, glowState, 1.0f)
				.uv(0, u1).endVertex();
		vertexbuffer.vertex(origin.getX(), origin.getY(), zLevel).color(glowState, glowState, glowState, 1.0f).uv(0, 0)
				.endVertex();
		tessellator.end();
	}

	private static float getPowerBarGlow() {
		float sin = (float) (Math.sin((float) Minecraft.getInstance().level.getGameTime() / 25.0f));
		sin = Math.abs(sin);
		sin *= 2.0f;
		sin += 8.0f;
		sin /= 10.0f;
		return sin;
	}
}
