package theking530.api.heat;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import theking530.staticpower.client.utilities.GuiTextUtilities;

public class HeatTooltipUtilities {

	public static ITextComponent getHeatRateTooltip(float heatDissipation) {
		return new StringTextComponent(TextFormatting.GRAY + "Heat Dissipation: ").append(GuiTextUtilities.formatHeatRateToString(heatDissipation)).mergeStyle(TextFormatting.BLUE);
	}

	public static ITextComponent getHeatGenerationTooltip(float heatGeneration) {
		return new StringTextComponent(TextFormatting.GRAY + "Heat Generation: ").append(GuiTextUtilities.formatHeatRateToString(heatGeneration)).mergeStyle(TextFormatting.GOLD);
	}

	public static ITextComponent getHeatCapacityTooltip(float capacity) {
		return new StringTextComponent(TextFormatting.GRAY + "Heat Capacity: ").append(GuiTextUtilities.formatHeatToString(capacity)).mergeStyle(TextFormatting.GREEN);
	}
}
