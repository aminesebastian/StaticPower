package theking530.staticcore.gui.widgets.valuebars;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import theking530.api.energy.StaticPowerVoltage;
import theking530.api.energy.StaticVoltageRange;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.text.PowerTextFormatting;
import theking530.staticcore.utilities.Color;
import theking530.staticpower.client.gui.GuiTextures;

public class GuiPowerBarUtilities {
	public static List<Component> getTooltip(double storedPower, double capacity, double maximumInptuPower, StaticPowerVoltage minimumInputVoltage,
			StaticPowerVoltage maximumInputVoltage) {
		List<Component> tooltip = new ArrayList<Component>();

		// Show the total amount of energy remaining / total energy capacity.
		tooltip.add(new TranslatableComponent("gui.staticpower.stored_power").withStyle(ChatFormatting.GREEN).append(": ")
				.append(ChatFormatting.WHITE + PowerTextFormatting.formatPowerToString(storedPower, capacity).getString()));

		// Add the input voltage to the tooltip.
		tooltip.add(new TranslatableComponent("gui.staticpower.input_voltage").withStyle(ChatFormatting.AQUA).append(": ")
				.append(ChatFormatting.WHITE + PowerTextFormatting.formatVoltageRangeToString(new StaticVoltageRange(minimumInputVoltage, maximumInputVoltage)).getString()));

		tooltip.add(new TranslatableComponent("gui.staticpower.max_input").withStyle(ChatFormatting.YELLOW).append(": ")
				.append(ChatFormatting.WHITE + PowerTextFormatting.formatPowerRateToString(maximumInptuPower).getString()));
		return tooltip;
	}

	public static void drawPowerBar(PoseStack matrixStack, float xpos, float ypos, float width, float height, double currentEnergy, double maxEnergy) {
		float percentFilled = (float) (currentEnergy / maxEnergy);
		float filledHeight = percentFilled * height;

		matrixStack.pushPose();
		matrixStack.translate(xpos, ypos, 0);
		GuiDrawUtilities.drawSlot(matrixStack, width, height, 0, 0, 0);
		float glowState = getPowerBarGlow();
		GuiDrawUtilities.drawTexture(matrixStack, GuiTextures.POWER_BAR_BG, width, height, 0, 0, 1, 1, Color.WHITE);
		GuiDrawUtilities.drawTexture(matrixStack, GuiTextures.POWER_BAR_FG, width, filledHeight, 0, height - filledHeight, 0, 0, 1 - percentFilled, 1, 1,
				new Color(glowState, glowState, glowState, 1.0f));
		matrixStack.popPose();
	}

	private static float getPowerBarGlow() {
		@SuppressWarnings("resource")
		double sin = (Math.sin(Minecraft.getInstance().level.getGameTime() / 25.0f));
		sin = (sin + 6) / 7;
		return (float) sin;
	}
}
