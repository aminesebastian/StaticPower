package theking530.api.heat;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import theking530.staticcore.gui.GuiTextUtilities;

public class HeatTooltipUtilities {

	public static MutableComponent getHeatConductivityTooltip(float conductivity) {
		return Component.literal(ChatFormatting.GRAY + "Heat Conductivity: ").append(GuiTextUtilities.formatConductivityToString(conductivity)).withStyle(ChatFormatting.AQUA);
	}

	public static MutableComponent getActiveTemperatureTooltip(int temperature) {
		return Component.literal(ChatFormatting.GRAY + "Temperature: ").append(GuiTextUtilities.formatHeatRateToString(temperature)).withStyle(ChatFormatting.GOLD);
	}

	public static MutableComponent getHeatGenerationTooltip(int heatGeneration) {
		return Component.literal(ChatFormatting.GRAY + "Heat Generation: ").append(GuiTextUtilities.formatHeatRateToString(heatGeneration)).withStyle(ChatFormatting.GREEN);
	}

	public static MutableComponent getOverheatingTooltip(int temperature) {
		return Component.literal(ChatFormatting.GRAY + "Overheats At: ").append(GuiTextUtilities.formatHeatToString(temperature)).withStyle(ChatFormatting.YELLOW);
	}	

	public static MutableComponent getMaximumHeatTooltip(int capacity) {
		return Component.literal(ChatFormatting.GRAY + "Maximum Capacity: ").append(GuiTextUtilities.formatHeatToString(capacity)).withStyle(ChatFormatting.RED);
	}

	public static MutableComponent getFreezingTooltip(int temperature) {
		return Component.literal(ChatFormatting.GRAY + "Freezes At: ").append(GuiTextUtilities.formatHeatToString(temperature)).withStyle(ChatFormatting.BLUE);
	}
}
