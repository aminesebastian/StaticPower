package theking530.staticcore.gui.widgets.valuebars;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.utilities.Color;
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
		float percentFilled = (float) currentHeat / (float) maxHeat;
		float filledHeight = percentFilled * height;

		float sinInput = Minecraft.getInstance().getFrameTime() + Minecraft.getInstance().level.getGameTime();
		float glowState = (float) (Math.sin((float) sinInput / 20.0f));
		float inputMin = -1.0f;
		float inputMax = 1.0f;
		float glowMin = 0.75f;
		float glowMax = 1.0f;
		glowState = glowMin + ((glowMax - glowMin) / (inputMax - inputMin)) * (glowState - inputMin);

		stack.pushPose();
		stack.translate(xpos, ypos, 0);
		GuiDrawUtilities.drawSlot(stack, width, height, 0, 0, 0);
		GuiDrawUtilities.drawTexture(stack, GuiTextures.HEAT_BAR_BG, width, height, 0, 0, 1, 1, Color.WHITE);
		GuiDrawUtilities.drawTexture(stack, GuiTextures.HEAT_BAR_FG, width, filledHeight, 0, height - filledHeight, 0, 0, 1 - percentFilled, 1, 1, new Color(1, 1, 1, 1.0f));

		stack.pushPose();
		stack.translate(0, height - filledHeight, 0);
		GuiDrawUtilities.drawRectangle(stack, width + 2, 1, -1, 0, 0, Color.GREY);
		GuiDrawUtilities.drawRectangle(stack, 1, 5, -2, -2, 0, Color.GREY);
		GuiDrawUtilities.drawRectangle(stack, 1, 5, width + 1, -2, 0, Color.GREY);

		stack.translate(width + 2, -2.5f, 0);
//		GuiDrawUtilities.drawTexture(stack, GuiTextures.HEAT_INDICATOR_ARROW, 6, 6, 0, 0, 1, 1, Color.WHITE);
		stack.popPose();

		stack.popPose();
	}
}
