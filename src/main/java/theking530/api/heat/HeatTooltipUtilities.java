package theking530.api.heat;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import theking530.staticpower.client.utilities.GuiTextUtilities;

public class HeatTooltipUtilities {

	public static MutableComponent getHeatConductivityTooltip(float conductivity) {
		return new TextComponent(ChatFormatting.GRAY + "Heat Conductivity: ").append(GuiTextUtilities.formatConductivityToString(conductivity)).withStyle(ChatFormatting.AQUA);
	}

	public static MutableComponent getActiveTemperatureTooltip(int temperature) {
		return new TextComponent(ChatFormatting.GRAY + "Temperature: ").append(GuiTextUtilities.formatHeatRateToString(temperature)).withStyle(ChatFormatting.GOLD);
	}

	public static MutableComponent getHeatGenerationTooltip(int heatGeneration) {
		return new TextComponent(ChatFormatting.GRAY + "Heat Generation: ").append(GuiTextUtilities.formatHeatRateToString(heatGeneration)).withStyle(ChatFormatting.GREEN);
	}

	public static MutableComponent getOverheatingTooltip(int temperature) {
		return new TextComponent(ChatFormatting.GRAY + "Overheats At: ").append(GuiTextUtilities.formatHeatToString(temperature)).withStyle(ChatFormatting.YELLOW);
	}

	public static MutableComponent getMaximumHeatTooltip(int capacity) {
		return new TextComponent(ChatFormatting.GRAY + "Maximum Capacity: ").append(GuiTextUtilities.formatHeatToString(capacity)).withStyle(ChatFormatting.RED);
	}

	public static MutableComponent getFreezingTooltip(int temperature) {
		return new TextComponent(ChatFormatting.GRAY + "Freezes At: ").append(GuiTextUtilities.formatHeatToString(temperature)).withStyle(ChatFormatting.BLUE);
	}
}
