package theking530.staticcore.gui.text;

import java.text.NumberFormat;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import theking530.api.energy.StaticPowerVoltage;
import theking530.api.energy.StaticVoltageRange;
import theking530.staticpower.utilities.MetricConverter;

public class PowerTextFormatting {
	/** Single instance of number formatter with two decimal places.. */
	private static final NumberFormat NUMBER_FORMATTER_TWO_DECIMAL;
	/** Single instance of number formatter with a single decimal place. */
	private static final NumberFormat NUMBER_FORMATTER_ONE_DECIMAL;
	/** Single instance of number formatter with no decimal place. */
	private static final NumberFormat NUMBER_FORMATTER_NO_DECIMAL;

	/** Translation text component for Static Volts (V). */
	public static final TranslatableComponent VOLTAGE_UNIT = new TranslatableComponent("gui.staticpower.volt_unit");

	/** Translation text component for Static Current (C). */
	public static final TranslatableComponent CURRENT_UNIT = new TranslatableComponent("gui.staticpower.current_unit");

	/** Translation text component for Static Power (P). */
	public static final TranslatableComponent POWER_UNIT = new TranslatableComponent("gui.staticpower.power_unit");
	/** Translation text component for Static Power Per Tick (P/t). */
	public static final TranslatableComponent POWER_RATE_UNIT = new TranslatableComponent("gui.staticpower.power_unit_per_tick");

	/** Translation text component for Static Resistance (Ω). */
	public static final TranslatableComponent RESISTANCE_UNIT = new TranslatableComponent("gui.staticpower.power_resistance_unit");

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
		if (Double.isInfinite(powerRate) || powerRate == Double.MAX_VALUE) {
			output = new TextComponent("∞");
		} else {
			MetricConverter metricRate = new MetricConverter(powerRate);
			output = new TextComponent(NUMBER_FORMATTER_ONE_DECIMAL.format(metricRate.getValue())).append(metricRate.getSuffix());
		}
		return output.append(POWER_RATE_UNIT);
	}

	public static MutableComponent formatPowerToString(double power, boolean includeUnits, boolean includeMetricUnit) {
		// Allocate the text component.
		MutableComponent output;

		// If the value is equal to the integer max, make it infinite.
		if (Double.isInfinite(power) || power == Double.MAX_VALUE) {
			output = new TextComponent("∞");
		} else {
			// Perform the metric conversion.
			MetricConverter metricEnergy = new MetricConverter(power);
			output = new TextComponent(NUMBER_FORMATTER_ONE_DECIMAL.format(metricEnergy.getValue()));

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

	public static MutableComponent formatVoltageToString(double voltage, boolean includeUnits, boolean includeMetricUnit) {
		return new TranslatableComponent(StaticPowerVoltage.getVoltageClass(voltage).getShortName());
//		// Allocate the text component.
//		MutableComponent output;
//
//		// If the value is equal to the integer max, make it infinite.
//		if (Double.isInfinite(voltage) || voltage == Double.MAX_VALUE || voltage == Double.MIN_VALUE) {
//			output = new TextComponent("∞");
//		} else {
//			// Perform the metric conversion.
//			MetricConverter metricEnergy = new MetricConverter(voltage);
//			output = new TextComponent(NUMBER_FORMATTER_ONE_DECIMAL.format(metricEnergy.getValue()));
//
//			// Include the metric unit if requested.
//			if (includeMetricUnit) {
//				output.append(metricEnergy.getSuffix());
//			}
//		}
//
//		if (includeUnits) {
//			output.append(VOLTAGE_UNIT);
//		}
//		return output;
	}

	public static MutableComponent formatVoltageToString(double voltage, boolean includeUnits) {
		return formatVoltageToString(voltage, includeUnits, true);
	}

	public static MutableComponent formatVoltageToString(double voltage) {
		return formatVoltageToString(voltage, true, true);
	}

	public static MutableComponent formatVoltageRangeToString(StaticVoltageRange range) {
		if (range.minimumVoltage() == range.maximumVoltage()) {
			return new TranslatableComponent(range.maximumVoltage().getShortName());
		} else if (range.minimumVoltage() == StaticPowerVoltage.LOW) {
			return new TextComponent("<").append(new TranslatableComponent(range.maximumVoltage().getShortName()));
		} else if (range.maximumVoltage() == StaticPowerVoltage.EXTREME) {
			return new TextComponent("<").append(new TranslatableComponent(range.maximumVoltage().getShortName()));
		} else {
			return new TranslatableComponent(range.minimumVoltage().getShortName()).append("⇔").append(new TranslatableComponent(range.maximumVoltage().getShortName()));
		}
	}

	public static MutableComponent formatResistanceToString(double resistance, boolean includeUnits, boolean includeMetricUnit) {
		// Allocate the text component.
		MutableComponent output;

		// If the value is equal to the integer max, make it infinite.
		if (Double.isInfinite(resistance) || resistance == Double.MAX_VALUE) {
			output = new TextComponent("∞");
		} else {
			// Perform the metric conversion.
			MetricConverter metricEnergy = new MetricConverter(resistance, 0);
			output = new TextComponent(NUMBER_FORMATTER_NO_DECIMAL.format(metricEnergy.getValue()));

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
}
