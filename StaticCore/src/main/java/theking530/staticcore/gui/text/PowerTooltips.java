package theking530.staticcore.gui.text;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import theking530.api.energy.StaticPowerVoltage;
import theking530.api.energy.StaticVoltageRange;

public class PowerTooltips {

	public static void addTransformerRatioTooltip(List<Component> tooltip, int ratio) {
		TooltipUtilities.addSingleLineBullet(tooltip, "gui.staticpower.transfomer_ratio", ChatFormatting.GREEN, PowerTextFormatting.formatTransformerRatioToString(ratio));
	}

	public static void addOutputVoltageTooltip(List<Component> tooltip, StaticPowerVoltage outputVoltage) {
		TooltipUtilities.addSingleLineBullet(tooltip, "gui.staticpower.output_voltage", ChatFormatting.BLUE, PowerTextFormatting.formatVoltageToString(outputVoltage));
	}

	public static void addMaxVoltageTooltip(List<Component> tooltip, StaticPowerVoltage maxVoltage) {
		TooltipUtilities.addSingleLineBullet(tooltip, "gui.staticpower.max_voltage", ChatFormatting.DARK_PURPLE, PowerTextFormatting.formatVoltageToString(maxVoltage));
	}

	public static void addMaximumOutputPowerTooltip(List<Component> tooltip, double maximumPower) {
		TooltipUtilities.addSingleLineBullet(tooltip, "gui.staticpower.max_output_power", ChatFormatting.RED, PowerTextFormatting.formatPowerRateToString(maximumPower));
	}

	public static void addMaximumPowerTransferTooltip(List<Component> tooltip, double maximumPowerTransfer) {
		TooltipUtilities.addSingleLineBullet(tooltip, "gui.staticpower.max_power_transfer", ChatFormatting.GREEN,
				PowerTextFormatting.formatPowerRateToString(maximumPowerTransfer));
	}

	public static void addResistanceTooltip(List<Component> tooltip, double resistance) {
		TooltipUtilities.addSingleLineBullet(tooltip, "gui.staticpower.resistance", ChatFormatting.YELLOW, PowerTextFormatting.formatResistanceToString(resistance));
	}

	public static void addPowerCapacityTooltip(List<Component> tooltip, double capacity) {
		TooltipUtilities.addSingleLineBullet(tooltip, "gui.staticpower.capacity", ChatFormatting.LIGHT_PURPLE, PowerTextFormatting.formatPowerToString(capacity));
	}

	public static void addStoredPowerTooltip(List<Component> tooltip, double storedPower) {
		TooltipUtilities.addSingleLineBullet(tooltip, "gui.staticpower.stored_power", ChatFormatting.GREEN, PowerTextFormatting.formatPowerToString(storedPower));
	}

	public static void addVoltageInputTooltip(List<Component> tooltip, StaticVoltageRange range) {
		TooltipUtilities.addSingleLineBullet(tooltip, "gui.staticpower.input_voltage", ChatFormatting.AQUA, PowerTextFormatting.formatVoltageRangeToString(range));
	}

	public static void addMaximumInputPowerTooltip(List<Component> tooltip, double maximumPower) {
		TooltipUtilities.addSingleLineBullet(tooltip, "gui.staticpower.max_input", ChatFormatting.YELLOW, PowerTextFormatting.formatPowerRateToString(maximumPower));
	}

	public static void addMaximumCurrentTooltip(List<Component> tooltip, double current) {
		TooltipUtilities.addSingleLineBullet(tooltip, "gui.staticpower.max_current", ChatFormatting.GOLD, PowerTextFormatting.formatCurrentToString(current));
	}
	
	public static void addInsulatedTooltip(List<Component> tooltip) {
		TooltipUtilities.addSingleLineBullet(tooltip, "gui.staticpower.insulated", ChatFormatting.GREEN, Component.translatable("gui.staticpower.insulated_tooltip"));
	}
}
