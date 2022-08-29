package theking530.api.heat;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import theking530.staticpower.client.utilities.GuiTextUtilities;

public class HeatTooltipUtilities {

	public static MutableComponent getHeatConductivityTooltip(float conductivity) {
		return new TextComponent(ChatFormatting.GRAY + "Heat Conductivity: ").append(GuiTextUtilities.formatConductivityToString(conductivity)).withStyle(ChatFormatting.BLUE);
	}

	public static MutableComponent getActiveTemperatureTooltip(int temperature) {
		return new TextComponent(ChatFormatting.GRAY + "Temperature: ").append(GuiTextUtilities.formatHeatRateToString(temperature)).withStyle(ChatFormatting.GOLD);
	}

	public static MutableComponent getHeatGenerationTooltip(int heatGeneration) {
		return new TextComponent(ChatFormatting.GRAY + "Heat Generation: ").append(GuiTextUtilities.formatHeatRateToString(heatGeneration)).withStyle(ChatFormatting.GOLD);
	}

	public static MutableComponent getHeatCapacityTooltip(double capacity) {
		return new TextComponent(ChatFormatting.GRAY + "Heat Capacity: ").append(GuiTextUtilities.formatHeatToString(capacity)).withStyle(ChatFormatting.GREEN);
	}

	public static MutableComponent getOverheatingTooltip(int temperature) {
		return new TextComponent(ChatFormatting.GRAY + "Overheats At: ").append(GuiTextUtilities.formatHeatToString(temperature)).withStyle(ChatFormatting.RED);
	}

	public static MutableComponent getFreezingTooltip(int temperature) {
		return new TextComponent(ChatFormatting.GRAY + "Freezes At: ").append(GuiTextUtilities.formatHeatToString(temperature)).withStyle(ChatFormatting.RED);
	}
}
