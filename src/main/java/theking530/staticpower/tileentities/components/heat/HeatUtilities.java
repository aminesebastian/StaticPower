package theking530.staticpower.tileentities.components.heat;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import theking530.staticpower.client.utilities.GuiTextUtilities;

public class HeatUtilities {

	public static ITextComponent getHeatRateTooltip(float heatDissipation) {
		return new StringTextComponent(TextFormatting.GRAY.toString()).appendSibling(new StringTextComponent("Heat Dissipation: ")).appendText(TextFormatting.BLUE.toString())
				.appendSibling(GuiTextUtilities.formatHeatRateToString(heatDissipation));
	}

	public static ITextComponent getHeatGenerationTooltip(float heatGeneration) {
		return new StringTextComponent(TextFormatting.GRAY.toString()).appendSibling(new StringTextComponent("Heat Generation: ")).appendText(TextFormatting.GREEN.toString())
				.appendSibling(GuiTextUtilities.formatHeatRateToString(heatGeneration));
	}

	public static ITextComponent getHeatCapacityTooltip(float capacity) {
		return new StringTextComponent(TextFormatting.GRAY.toString()).appendSibling(new StringTextComponent("Heat Capacity: ")).appendText(TextFormatting.BLUE.toString())
				.appendSibling(GuiTextUtilities.formatHeatToString(capacity));
	}
}
