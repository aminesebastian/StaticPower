package theking530.staticpower.tileentities.components.heat;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import theking530.staticpower.client.utilities.GuiTextUtilities;

public class HeatUtilities {

	public static ITextComponent getHeatTooltip(float heatDissipation) {
		return new StringTextComponent(TextFormatting.GRAY.toString()).appendSibling(new StringTextComponent("Heat Dissipation: "))
				.appendText(TextFormatting.BLUE.toString()).appendSibling(GuiTextUtilities.formatHeatRateToString(heatDissipation));
	}
}
