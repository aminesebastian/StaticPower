package theking530.api.heat;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import theking530.staticpower.client.utilities.GuiTextUtilities;

public class HeatTooltipUtilities {

	public static ITextComponent getHeatRateTooltip(float heatDissipation) {
		return new StringTextComponent(TextFormatting.GRAY.toString())
				.append(new StringTextComponent("Heat Dissipation: ")).appendString(TextFormatting.BLUE.toString())
				.append(GuiTextUtilities.formatHeatRateToString(heatDissipation));
	}

	public static ITextComponent getHeatGenerationTooltip(float heatGeneration) {
		return new StringTextComponent(TextFormatting.GRAY.toString())
				.append(new StringTextComponent("Heat Generation: ")).appendString(TextFormatting.GREEN.toString())
				.append(GuiTextUtilities.formatHeatRateToString(heatGeneration));
	}

	public static ITextComponent getHeatCapacityTooltip(float capacity) {
		return new StringTextComponent(TextFormatting.GRAY.toString())
				.append(new StringTextComponent("Heat Capacity: ")).appendString(TextFormatting.BLUE.toString())
				.append(GuiTextUtilities.formatHeatToString(capacity));
	}
}
