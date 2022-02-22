package theking530.staticcore.gui.widgets.valuebars;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.utilities.Color;
import theking530.staticpower.client.gui.GuiTextures;
import theking530.staticpower.client.utilities.GuiTextUtilities;

public class GuiPowerBarUtilities {
	public static List<Component> getTooltip(long currentEnergy, long maxEnergy, double energyPerTick) {
		List<Component> tooltip = new ArrayList<Component>();

		// Add the input rate to the tooltip.
		tooltip.add(new TranslatableComponent("gui.staticpower.max_input").append(": ").append(GuiTextUtilities.formatEnergyRateToString(energyPerTick)));

		// Show the total amount of energy remaining / total energy capacity.
		tooltip.add(GuiTextUtilities.formatEnergyToString(currentEnergy, maxEnergy));

		return tooltip;
	}

	public static void drawPowerBar(PoseStack matrixStack, float xpos, float ypos, float width, float height, float zLevel, long currentEnergy, long maxEnergy) {
		float percentFilled = (float) currentEnergy / (float) maxEnergy;
		float filledHeight = percentFilled * height;

		GuiDrawUtilities.drawSlot(matrixStack, width, height, 0, 0, 0);
		float glowState = getPowerBarGlow();
		GuiDrawUtilities.drawTexture(matrixStack, GuiTextures.POWER_BAR_BG, width, height, 0, 0, 1, 1, Color.WHITE);
		GuiDrawUtilities.drawTexture(matrixStack, GuiTextures.POWER_BAR_FG, width, filledHeight, 0, height - filledHeight, 0, 0, 1 - percentFilled, 1, 1,
				new Color(glowState, glowState, glowState, 1.0f));
	}

	private static float getPowerBarGlow() {
		@SuppressWarnings("resource")
		double sin = (Math.sin(Minecraft.getInstance().level.getGameTime() / 25.0f));
		sin = (sin + 6) / 7;
		return (float) sin;
	}
}
