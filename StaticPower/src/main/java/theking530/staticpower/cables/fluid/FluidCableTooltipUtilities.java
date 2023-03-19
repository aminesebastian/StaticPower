package theking530.staticpower.cables.fluid;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import theking530.staticcore.gui.GuiTextUtilities;

public class FluidCableTooltipUtilities {

	public static Component getFluidTrasnferRate(float rate) {
		return Component.literal(ChatFormatting.GRAY.toString()).append(Component.literal("Max Transfer: ")).append(ChatFormatting.BLUE.toString())
				.append(GuiTextUtilities.formatFluidRateToString(rate));
	}
}
