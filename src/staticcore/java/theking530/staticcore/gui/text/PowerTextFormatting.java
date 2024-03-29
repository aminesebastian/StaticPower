package theking530.staticcore.gui.text;

import java.text.NumberFormat;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import theking530.api.energy.StaticPowerVoltage;
import theking530.api.energy.StaticVoltageRange;
import theking530.staticcore.utilities.MetricConverter;

public class PowerTextFormatting {
	/** Single instance of number formatter with two decimal places.. */
	private static final NumberFormat NUMBER_FORMATTER_TWO_DECIMAL;
	/** Single instance of number formatter with a single decimal place. */
	private static final NumberFormat NUMBER_FORMATTER_ONE_DECIMAL;
	/** Single instance of number formatter with no decimal place. */
	private static final NumberFormat NUMBER_FORMATTER_NO_DECIMAL;

	/** Translation text component for Static Volts (V). */
	public static final MutableComponent VOLTAGE_UNIT = Component.translatable("gui.staticcore.volt_unit");

	/** Translation text component for Static Current (A). */
	public static final MutableComponent CURRENT_UNIT = Component.translatable("gui.staticcore.current_unit");

	/** Translation text component for Static Power (P). */
	public static final MutableComponent POWER_UNIT = Component.translatable("gui.staticcore.power_unit");
	/** Translation text component for Static Power Per Tick (P/t). */
	public static final MutableComponent POWER_RATE_UNIT = Component.translatable("gui.staticcore.power_unit_per_tick");

	/** Translation text component for Static Resistance (Ω). */
	public static final MutableComponent RESISTANCE_UNIT = Component.translatable("gui.staticcore.resistance_unit");

	/**
	 * Static initializer for number formatter.
	 */
	static {
		NUMBER_FORMATTER_TWO_DECIMAL = NumberFormat.getInstance();
		NUMBER_FORMATTER_TWO_DECIMAL.setGroupingUsed(true);
		NUMBER_FORMATTER_TWO_DECIMAL.setMaximumFractionDigits(2);

		NUMBER_FORMATTER_ONE_DECIMAL = NumberFormat.getInstance();
		NUMBER_FORMATTER_ONE_DECIMAL.setGroupingUsed(true);
		NUMBER_FORMATTER_ONE_DECIMAL.setMaximumFractionDigits(1);

		NUMBER_FORMATTER_NO_DECIMAL = NumberFormat.getInstance();
		NUMBER_FORMATTER_NO_DECIMAL.setGroupingUsed(true);
		NUMBER_FORMATTER_NO_DECIMAL.setMaximumFractionDigits(0);
	}

	public static MutableComponent formatPowerRateToString(double powerRate) {
		// Allocate the text component.
		MutableComponent output;

		// If the value is equal to the integer max, make it infinite.
		if (Double.isInfinite(powerRate) || Double.isNaN(powerRate)) {
			output = Component.literal("∞");
		} else if (Double.isNaN(powerRate)) {
			output = Component.literal("NaN");
		} else {
			MetricConverter metricRate = new MetricConverter(powerRate);
			output = Component.literal(NUMBER_FORMATTER_ONE_DECIMAL.format(metricRate.getValue())).append(metricRate.getSuffix());
		}
		return output.append(POWER_RATE_UNIT);
	}

	public static MutableComponent formatPowerToString(double power, boolean includeUnits, boolean includeMetricUnit) {
		// Allocate the text component.
		MutableComponent output;

		// If the value is equal to the integer max, make it infinite.
		if (Double.isInfinite(power)) {
			output = Component.literal("∞");
		} else {
			// Perform the metric conversion.
			MetricConverter metricEnergy = new MetricConverter(power);
			output = Component.literal(NUMBER_FORMATTER_ONE_DECIMAL.format(metricEnergy.getValue()));

			// Include the metric unit if requested.
			if (includeMetricUnit) {
				output.append(metricEnergy.getSuffix());
			}
		}

		if (includeUnits) {
			output.append(POWER_UNIT);
		}
		return output;
	}

	public static MutableComponent formatPowerToString(double power, boolean includeUnits) {
		return formatPowerToString(power, includeUnits, true);
	}

	public static MutableComponent formatPowerToString(double power) {
		return formatPowerToString(power, true, true);
	}

	public static MutableComponent formatPowerToString(double power, double capacity) {
		return formatPowerToString(power, false, true).append("/").append(formatPowerToString(capacity));
	}

	public static MutableComponent formatVoltageToString(StaticPowerVoltage voltage) {
		return Component.translatable(voltage.getShortName());
	}

	public static MutableComponent formatTransformerRatioToString(int ratio) {
		return Component.literal(String.valueOf(ratio + 1)).append(":1");
	}

	public static MutableComponent formatVoltageRangeToString(StaticVoltageRange range) {
		if (range.minimumVoltage() == range.maximumVoltage()) {
			return Component.translatable(range.maximumVoltage().getShortName());
		} else if (range.minimumVoltage() == StaticPowerVoltage.LOW && range.maximumVoltage() == StaticPowerVoltage.BONKERS) {
			return Component.translatable("gui.staticcore.any_voltage");
		} else if (range.minimumVoltage() == StaticPowerVoltage.LOW) {
			return Component.literal("<").append(Component.translatable(range.maximumVoltage().getShortName()));
		} else if (range.maximumVoltage() == StaticPowerVoltage.BONKERS) {
			return Component.literal(">").append(Component.translatable(range.minimumVoltage().getShortName()));
		} else {
			return Component.translatable(range.minimumVoltage().getShortName()).append("⇔").append(Component.translatable(range.maximumVoltage().getShortName()));
		}
	}

	public static MutableComponent formatResistanceToString(double resistance, boolean includeUnits, boolean includeMetricUnit) {
		// Allocate the text component.
		MutableComponent output;

		// If the value is equal to the integer max, make it infinite.
		if (Double.isInfinite(resistance)) {
			output = Component.literal("∞");
		} else {
			// Perform the metric conversion.
			MetricConverter metricEnergy = new MetricConverter(resistance, 0);
			output = Component.literal(NUMBER_FORMATTER_NO_DECIMAL.format(metricEnergy.getValue()));

			// Include the metric unit if requested.
			if (includeMetricUnit) {
				output.append(metricEnergy.getSuffix());
			}
		}

		if (includeUnits) {
			output.append(RESISTANCE_UNIT);
		}
		return output;
	}

	public static MutableComponent formatResistanceToString(double resistance, boolean includeUnits) {
		return formatResistanceToString(resistance, includeUnits, true);
	}

	public static MutableComponent formatResistanceToString(double resistance) {
		return formatResistanceToString(resistance, true, true);
	}

	public static MutableComponent formatCurrentToString(double current, boolean includeUnits, boolean includeMetricUnit) {
		// Allocate the text component.
		MutableComponent output;

		// If the value is equal to the integer max, make it infinite.
		if (Double.isInfinite(current)) {
			output = Component.literal("∞");
		} else {
			// Perform the metric conversion.
			MetricConverter metricEnergy = new MetricConverter(current, 0);
			output = Component.literal(NUMBER_FORMATTER_ONE_DECIMAL.format(metricEnergy.getValue()));

			// Include the metric unit if requested.
			if (includeMetricUnit) {
				output.append(metricEnergy.getSuffix());
			}
		}

		if (includeUnits) {
			output.append(CURRENT_UNIT);
		}
		return output;
	}

	public static MutableComponent formatCurrentToString(double current, boolean includeUnits) {
		return formatCurrentToString(current, includeUnits, true);
	}

	public static MutableComponent formatCurrentToString(double current) {
		return formatCurrentToString(current, true, true);
	}

}
