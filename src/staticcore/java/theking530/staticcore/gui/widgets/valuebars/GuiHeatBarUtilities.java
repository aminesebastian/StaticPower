package theking530.staticcore.gui.widgets.valuebars;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import theking530.api.heat.IHeatStorage;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.utilities.SDColor;
import theking530.staticpower.client.gui.GuiTextures;
import theking530.staticpower.client.utilities.GuiTextUtilities;

public class GuiHeatBarUtilities {
	public static List<Component> getTooltip(int currentTemp, int minimumTemp, int overheatTemp, int maximumTemp, double heatPerTick) {
		List<Component> tooltip = new ArrayList<Component>();

		// Show the total amount of energy remaining / total energy capacity.
		tooltip.add(GuiTextUtilities.formatHeatToString(currentTemp, maximumTemp));
		tooltip.add(Component.literal("Overheat: ").withStyle(ChatFormatting.RED).append(GuiTextUtilities.formatHeatToString(overheatTemp)));

		return tooltip;
	}

	public static void drawHeatBar(PoseStack stack, float xpos, float ypos, float width, float height, float zLevel, int currentTemp, int maximumTemp) {
		drawHeatBar(stack, xpos, ypos, width, height, zLevel, currentTemp, IHeatStorage.MINIMUM_TEMPERATURE, maximumTemp, maximumTemp);
	}

	public static void drawHeatBar(PoseStack stack, float xpos, float ypos, float width, float height, float zLevel, int currentTemp, int overheatTemp, int maximumTemp) {
		drawHeatBar(stack, xpos, ypos, width, height, zLevel, currentTemp, IHeatStorage.MINIMUM_TEMPERATURE, overheatTemp, maximumTemp);
	}

	public static void drawHeatBar(PoseStack stack, float xpos, float ypos, float width, float height, float zLevel, int currentTemp, int minimumTemp, int overheatTemp,
			int maximumTemp) {
		float totalHeight = maximumTemp - IHeatStorage.MINIMUM_TEMPERATURE;
		float percentFilled = (currentTemp - IHeatStorage.MINIMUM_TEMPERATURE) / totalHeight;
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
		GuiDrawUtilities.drawTexture(stack, GuiTextures.HEAT_BAR_BG, width, height, 0, 0, 1, 1, SDColor.WHITE);
		GuiDrawUtilities.drawTexture(stack, GuiTextures.HEAT_BAR_FG, width, filledHeight, 0, height - filledHeight, 0, 0, 1 - percentFilled, 1, 1, new SDColor(1, 1, 1, 1.0f));
		GuiDrawUtilities.drawTexture(stack, GuiTextures.HEAT_BAR_INDICATOR, width + 4.5f, 10, -2.25f, height - filledHeight - 5, zLevel, 0, 0, 1, 1, SDColor.GREY);

		if (overheatTemp != maximumTemp) {
			float overheatHeight = ((overheatTemp - IHeatStorage.MINIMUM_TEMPERATURE) / totalHeight) * height;
			GuiDrawUtilities.drawTexture(stack, GuiTextures.HEAT_BAR_INDICATOR, width + 4.5f, 10, -2.25f, height - overheatHeight - 5, zLevel, 0, 0, 1, 1, SDColor.RED);
		}

		if (minimumTemp != IHeatStorage.MINIMUM_TEMPERATURE) {
			float minimumHeight = ((minimumTemp - IHeatStorage.MINIMUM_TEMPERATURE) / totalHeight) * height;
			GuiDrawUtilities.drawTexture(stack, GuiTextures.HEAT_BAR_INDICATOR, width + 4.5f, 10, -2.25f, height - minimumHeight - 5, zLevel, 0, 0, 1, 1, SDColor.GREEN);
		}

		stack.popPose();
	}

	public static void drawHeatLevelIndicator(PoseStack stack, float width, SDColor color) {
		GuiDrawUtilities.drawRectangle(stack, width + 2, 1, -1, 0, 0, color);
		GuiDrawUtilities.drawRectangle(stack, 1, 5, -2, -2, 0, color);
		GuiDrawUtilities.drawRectangle(stack, 1, 5, width + 1, -2, 0, color);
	}
}
