package theking530.api.heat;

import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import theking530.staticpower.client.utilities.GuiTextUtilities;

public class HeatTooltipUtilities {

	public static IFormattableTextComponent getHeatConductivityTooltip(double heatDissipation) {
		return new StringTextComponent(TextFormatting.GRAY + "Heat Conductivity: ").append(GuiTextUtilities.formatConductivityToString(heatDissipation)).mergeStyle(TextFormatting.BLUE);
	}

	public static IFormattableTextComponent getHeatGenerationTooltip(double heatGeneration) {
		return new StringTextComponent(TextFormatting.GRAY + "Heat Generation: ").append(GuiTextUtilities.formatHeatRateToString(heatGeneration)).mergeStyle(TextFormatting.GOLD);
	}

	public static IFormattableTextComponent getHeatCapacityTooltip(double capacity) {
		return new StringTextComponent(TextFormatting.GRAY + "Heat Capacity: ").append(GuiTextUtilities.formatHeatToString(capacity)).mergeStyle(TextFormatting.GREEN);
	}

	public static IFormattableTextComponent getOverheatingTooltip(double temperature) {
		return new StringTextComponent(TextFormatting.GRAY + "Overheats At: ").append(GuiTextUtilities.formatHeatToString(temperature)).mergeStyle(TextFormatting.RED);
	}
}
