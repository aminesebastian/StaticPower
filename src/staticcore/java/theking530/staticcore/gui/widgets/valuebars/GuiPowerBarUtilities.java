package theking530.staticcore.gui.widgets.valuebars;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import theking530.api.energy.StaticPowerVoltage;
import theking530.api.energy.StaticVoltageRange;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.gui.StaticCoreGuiTextures;
import theking530.staticcore.gui.text.PowerTooltips;
import theking530.staticcore.utilities.SDColor;

public class GuiPowerBarUtilities {
	public static List<Component> getTooltip(double storedPower, double capacity, double maximumInptuPower, StaticPowerVoltage minimumInputVoltage,
			StaticPowerVoltage maximumInputVoltage) {
		List<Component> tooltip = new ArrayList<Component>();

		PowerTooltips.addStoredPowerTooltip(tooltip, storedPower);
		PowerTooltips.addMaximumInputPowerTooltip(tooltip, maximumInptuPower);
		PowerTooltips.addVoltageInputTooltip(tooltip, new StaticVoltageRange(minimumInputVoltage, maximumInputVoltage));

		return tooltip;
	}

	public static void drawPowerBar(PoseStack matrixStack, float xpos, float ypos, float width, float height, double currentEnergy, double maxEnergy) {
		float percentFilled = (float) (currentEnergy / maxEnergy);
		float filledHeight = percentFilled * height;

		matrixStack.pushPose();
		matrixStack.translate(xpos, ypos, 0);
		GuiDrawUtilities.drawSlot(matrixStack, width, height, 0, 0, 0);
		float glowState = getPowerBarGlow();
		GuiDrawUtilities.drawTexture(matrixStack, StaticCoreGuiTextures.POWER_BAR_BG, width, height, 0, 0, 1, 1, SDColor.WHITE);
		GuiDrawUtilities.drawTexture(matrixStack, StaticCoreGuiTextures.POWER_BAR_FG, width, filledHeight, 0, height - filledHeight, 0, 0, 1 - percentFilled, 1, 1,
				new SDColor(glowState, glowState, glowState, 1.0f));
		matrixStack.popPose();
	}

	private static float getPowerBarGlow() {
		@SuppressWarnings("resource")
		double sin = (Math.sin(Minecraft.getInstance().level.getGameTime() / 25.0f));
		sin = (sin + 6) / 7;
		return (float) sin;
	}
}
