package theking530.api.heat;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;
import theking530.staticpower.client.utilities.GuiTextUtilities;

public class HeatTooltipUtilities {

	public static MutableComponent getHeatConductivityTooltip(double heatDissipation) {
		return new TextComponent(ChatFormatting.GRAY + "Heat Conductivity: ").append(GuiTextUtilities.formatConductivityToString(heatDissipation)).withStyle(ChatFormatting.BLUE);
	}

	public static MutableComponent getHeatGenerationTooltip(double heatGeneration) {
		return new TextComponent(ChatFormatting.GRAY + "Heat Generation: ").append(GuiTextUtilities.formatHeatRateToString(heatGeneration)).withStyle(ChatFormatting.GOLD);
	}

	public static MutableComponent getHeatCapacityTooltip(double capacity) {
		return new TextComponent(ChatFormatting.GRAY + "Heat Capacity: ").append(GuiTextUtilities.formatHeatToString(capacity)).withStyle(ChatFormatting.GREEN);
	}

	public static MutableComponent getOverheatingTooltip(double temperature) {
		return new TextComponent(ChatFormatting.GRAY + "Overheats At: ").append(GuiTextUtilities.formatHeatToString(temperature)).withStyle(ChatFormatting.RED);
	}
}
