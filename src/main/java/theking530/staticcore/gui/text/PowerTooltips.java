package theking530.staticcore.gui.text;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import theking530.api.energy.StaticVoltageRange;

public class PowerTooltips {

	public static void addOutputVoltageTooltip(List<Component> tooltip, double outputVoltage) {
		TooltipUtilities.addSingleLineBullet(tooltip, "gui.staticpower.output_voltage", ChatFormatting.BLUE, PowerTextFormatting.formatVoltageToString(outputVoltage));
	}

	public static void addMaximumOutputPowerTooltip(List<Component> tooltip, double maximumPower) {
		TooltipUtilities.addSingleLineBullet(tooltip, "gui.staticpower.max_output_power", ChatFormatting.RED, PowerTextFormatting.formatPowerRateToString(maximumPower));
	}

	public static void addPowerCapacityTooltip(List<Component> tooltip, double capacity) {
		TooltipUtilities.addSingleLineBullet(tooltip, "gui.staticpower.capacity", ChatFormatting.LIGHT_PURPLE, PowerTextFormatting.formatPowerToString(capacity));
	}

	public static void addStoredPowerTooltip(List<Component> tooltip, double storedPower) {
		TooltipUtilities.addSingleLineBullet(tooltip, "gui.staticpower.stored_power", ChatFormatting.GREEN, PowerTextFormatting.formatPowerToString(storedPower));
	}

	public static void addVoltageInputTooltip(List<Component> tooltip, StaticVoltageRange range) {
		TooltipUtilities.addSingleLineBullet(tooltip, "gui.staticpower.input_voltage", ChatFormatting.GREEN, PowerTextFormatting.formatVoltageRangeToString(range));
	}

	public static void addMaximumInputPowerTooltip(List<Component> tooltip, double maximumPower) {
		TooltipUtilities.addSingleLineBullet(tooltip, "gui.staticpower.max_input_power", ChatFormatting.AQUA, PowerTextFormatting.formatPowerRateToString(maximumPower));
	}
}
