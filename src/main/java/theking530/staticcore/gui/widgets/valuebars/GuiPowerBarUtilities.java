package theking530.staticcore.gui.widgets.valuebars;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.client.gui.GuiTextures;
import theking530.staticpower.client.utilities.GuiTextUtilities;

public class GuiPowerBarUtilities {
	public static List<ITextComponent> getTooltip(long currentEnergy, long maxEnergy, double energyPerTick) {
		List<ITextComponent> tooltip = new ArrayList<ITextComponent>();

		// Add the input rate to the tooltip.
		tooltip.add(new TranslationTextComponent("gui.staticpower.max_input").appendString(": ").append(GuiTextUtilities.formatEnergyRateToString(energyPerTick)));

		// Show the total amount of energy remaining / total energy capacity.
		tooltip.add(GuiTextUtilities.formatEnergyToString(currentEnergy, maxEnergy));

		return tooltip;
	}

	public static void drawPowerBar(@Nullable MatrixStack matrixStack, float xpos, float ypos, float width, float height, float zLevel, long currentEnergy, long maxEnergy) {
		float u1 = (float) currentEnergy / (float) maxEnergy;
		float k1 = u1 * height;

		GuiDrawUtilities.drawSlot(matrixStack, xpos, ypos - height, width, height, 0);

		// Get the origin.
		Vector2D origin = GuiDrawUtilities.translatePositionByMatrix(matrixStack, xpos, ypos);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuffer();
		Minecraft.getInstance().getTextureManager().bindTexture(GuiTextures.POWER_BAR_BG);
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		vertexbuffer.pos(origin.getX() + width, origin.getY(), zLevel).tex(1, 0).endVertex();
		vertexbuffer.pos(origin.getX() + width, origin.getY() - height, zLevel).tex(1.0f, 1.0f).endVertex();
		vertexbuffer.pos(origin.getX(), origin.getY() - height, zLevel).tex(0.0f, 1.0f).endVertex();
		vertexbuffer.pos(origin.getX(), origin.getY(), zLevel).tex(0, 0).endVertex();
		tessellator.draw();

		float glowState = getPowerBarGlow();
		Minecraft.getInstance().getTextureManager().bindTexture(GuiTextures.POWER_BAR_FG);
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX);
		vertexbuffer.pos(origin.getX() + width, origin.getY(), zLevel).color(glowState, glowState, glowState, 1.0f).tex(1, 0).endVertex();
		vertexbuffer.pos(origin.getX() + width, origin.getY() - k1, zLevel).color(glowState, glowState, glowState, 1.0f).tex(1, u1).endVertex();
		vertexbuffer.pos(origin.getX(), origin.getY() - k1, zLevel).color(glowState, glowState, glowState, 1.0f).tex(0, u1).endVertex();
		vertexbuffer.pos(origin.getX(), origin.getY(), zLevel).color(glowState, glowState, glowState, 1.0f).tex(0, 0).endVertex();
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
