package theking530.api.power;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import theking530.staticpower.client.utilities.GuiTextUtilities;

public class StaticVoltTooltipUtilities {

	public static ITextComponent getPowerPerTickTooltip(float powerPerTick) {
		return new StringTextComponent(TextFormatting.GRAY.toString() + "Max ").appendString(TextFormatting.BLUE.toString()).append(GuiTextUtilities.formatEnergyRateToString(powerPerTick)).mergeStyle(TextFormatting.AQUA);
	}
}
