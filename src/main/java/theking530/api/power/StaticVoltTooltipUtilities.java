package theking530.api.power;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;
import theking530.staticpower.client.utilities.GuiTextUtilities;

public class StaticVoltTooltipUtilities {

	public static Component getPowerPerTickTooltip(float powerPerTick) {
		return new TextComponent(ChatFormatting.GRAY.toString() + "Max ").append(ChatFormatting.BLUE.toString()).append(GuiTextUtilities.formatEnergyRateToString(powerPerTick)).withStyle(ChatFormatting.AQUA);
	}
}
