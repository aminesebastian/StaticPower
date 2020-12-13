package theking530.api.heat;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import theking530.staticpower.client.utilities.GuiTextUtilities;

public class HeatTooltipUtilities {

	public static ITextComponent getHeatConductivityTooltip(double heatDissipation) {
		return new StringTextComponent(TextFormatting.GRAY + "Heat Conductivity: ").append(GuiTextUtilities.formatConductivityToString(heatDissipation)).mergeStyle(TextFormatting.BLUE);
	}

	public static ITextComponent getHeatGenerationTooltip(double heatGeneration) {
		return new StringTextComponent(TextFormatting.GRAY + "Heat Generation: ").append(GuiTextUtilities.formatHeatRateToString(heatGeneration)).mergeStyle(TextFormatting.GOLD);
	}

	public static ITextComponent getHeatCapacityTooltip(double capacity) {
		return new StringTextComponent(TextFormatting.GRAY + "Heat Capacity: ").append(GuiTextUtilities.formatHeatToString(capacity)).mergeStyle(TextFormatting.GREEN);
	}
}
