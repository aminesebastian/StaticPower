package theking530.staticpower.cables.fluid;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import theking530.staticpower.client.utilities.GuiTextUtilities;

public class FluidCableTooltipUtilities {

	public static Component getFluidTrasnferRate(float rate) {
		return new TextComponent(ChatFormatting.GRAY.toString()).append(new TextComponent("Max Transfer: ")).append(ChatFormatting.BLUE.toString())
				.append(GuiTextUtilities.formatFluidRateToString(rate));
	}
}
