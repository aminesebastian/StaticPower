package theking530.staticpower.client.utilities;

import java.text.NumberFormat;

import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import theking530.staticpower.utilities.MetricConverter;

/**
 * Utility class to format various values into display in the UI including
 * energy and fluid.
 * 
 * @author Amine Sebastian
 *
 */
public class GuiTextUtilities {
	/** Translation text component for Static Volts (SV). */
	public static final TranslationTextComponent ENERGY_UNIT_TRANSLATION = new TranslationTextComponent("gui.staticpower.energy_unit");
	/** Translation text component for Static Volts Per Tick (SV/t). */
	public static final TranslationTextComponent ENERGY_RATE_TRANSLATION = new TranslationTextComponent("gui.staticpower.energy_unit_per_tick");

	/** Translation text component for millibuckets (mB). */
	public static final TranslationTextComponent FLUID_UNIT_TRANSLATION = new TranslationTextComponent("gui.staticpower.fluid_unit");
	/** Translation text component for millibuckets Per Tick (mB/t). */
	public static final TranslationTextComponent FLUID_RATE_TRANSLATION = new TranslationTextComponent("gui.staticpower.fluid_unit_per_tick");

	/** Translation text component for heat (H). */
	public static final TranslationTextComponent HEAT_UNIT_TRANSLATION = new TranslationTextComponent("gui.staticpower.heat_unit");
	/** Translation text component for Heat Per Tick (H/t). */
	public static final TranslationTextComponent HEAT_RATE_TRANSLATION = new TranslationTextComponent("gui.staticpower.heat_unit_per_tick");
	/** Translation text component for Conductivity. */
	public static final TranslationTextComponent HEAT_CONDUCTIVITY_TRANSLATION = new TranslationTextComponent("gui.staticpower.heat_conductivity_unit");

	/** Single instance of number formatter with two decimal places.. */
	private static final NumberFormat NUMBER_FORMATTER_TWO_DECIMAL;
	/** Single instance of number formatter with a single decimal place. */
	private static final NumberFormat NUMBER_FORMATTER_ONE_DECIMAL;
	/** Single instance of number formatter with no decimal place. */
	private static final NumberFormat NUMBER_FORMATTER_NO_DECIMAL;
	
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

	/**
	 * Formats the provided energy into a string for display in the UI. Example,
	 * energy 50000 turns into 50kSV. Uses localization.
	 * 
	 * @param energy The amount of energy to format.
	 * @return The formatted string.
	 */
	public static IFormattableTextComponent formatEnergyToString(int energy, boolean includeUnits, boolean includeMetricUnit) {
		// Allocate the text component.
		IFormattableTextComponent output;

		// If the value is equal to the integer max, make it infinite.
		if (energy == Integer.MAX_VALUE) {
			output = new StringTextComponent("∞");
		} else {
			// Perform the metric conversion.
			MetricConverter metricEnergy = new MetricConverter(energy);
			output = new StringTextComponent(NUMBER_FORMATTER_TWO_DECIMAL.format(metricEnergy.getValue()));

			// Include the metric unit if requested.
			if (includeMetricUnit) {
				output.appendString(metricEnergy.getSuffix());
			}
		}

		if (includeUnits) {
			output.append(ENERGY_UNIT_TRANSLATION);
		}
		return output;
	}

	public static IFormattableTextComponent formatEnergyToString(int energy, boolean includeUnits) {
		return formatEnergyToString(energy, includeUnits, true);
	}

	public static IFormattableTextComponent formatEnergyToString(int energy) {
		return formatEnergyToString(energy, true, true);
	}

	/**
	 * Formats the provided energy and capacity into a string for display in the UI.
	 * Example, energy 50000 and storage 100000 turns into 50,000/100,000 SV. Uses
	 * localization.
	 * 
	 * @param energy   The amount of energy to format as the numerator.
	 * @param capacity The maximum amount of energy to use as the denominator.
	 * @return The formatted string.
	 */
	public static IFormattableTextComponent formatEnergyToString(int energy, int capacity) {
		return formatEnergyToString(energy, false, true).appendString("/").append(formatEnergyToString(capacity));

	}

	/**
	 * Formats the provided energyRate into a string for display in the UI. Example,
	 * energyRate 1000 turns into 1kSV/t. Uses localization.
	 * 
	 * @param energyRate The energy rate to format.
	 * @return The formatted string.
	 */
	public static IFormattableTextComponent formatEnergyRateToString(double energyRate) {
		// Allocate the text component.
		IFormattableTextComponent output;
		
		// If the value is equal to the integer max, make it infinite.
		if ((int)energyRate == Integer.MAX_VALUE) {
			output = new StringTextComponent("∞");
		} else {
			MetricConverter metricRate = new MetricConverter(energyRate);
			output = new StringTextComponent(NUMBER_FORMATTER_TWO_DECIMAL.format(metricRate.getValue())).appendString(metricRate.getSuffix());
		}
		return output.append(ENERGY_RATE_TRANSLATION);
	}

	public static IFormattableTextComponent formatHeatToString(double currentHeat, double capacity) {
		return formatHeatToString(currentHeat, false, true).appendString("/").append(formatHeatToString(capacity));

	}

	public static IFormattableTextComponent formatHeatToString(double heat) {
		return formatHeatToString(heat, true, true);
	}

	public static IFormattableTextComponent formatHeatToString(double heat, boolean includeUnits, boolean includeMetricUnit) {
		// Allocate the text component.
		IFormattableTextComponent output;

		// If the value is equal to the integer max, make it infinite.
		if ((int)heat == Integer.MAX_VALUE) {
			output = new StringTextComponent("∞");
		} else {
			MetricConverter metricEnergy = new MetricConverter(heat);
			output = new StringTextComponent(NUMBER_FORMATTER_TWO_DECIMAL.format(metricEnergy.getValue()));

			if (includeMetricUnit) {
				output.appendString(metricEnergy.getSuffix());
			}
		}

		if (includeUnits) {
			output.appendString(" ").append(HEAT_UNIT_TRANSLATION);
		}
		return output;
	}

	public static IFormattableTextComponent formatHeatRateToString(double heatTransferRate) {
		// Allocate the text component.
		IFormattableTextComponent output;

		// If the value is equal to the integer max, make it infinite.
		if ((int)heatTransferRate == Integer.MAX_VALUE) {
			output = new StringTextComponent("∞");
		} else {
			MetricConverter metricRate = new MetricConverter(heatTransferRate);
			output = new StringTextComponent(NUMBER_FORMATTER_TWO_DECIMAL.format(metricRate.getValue())).appendString(" ").appendString(metricRate.getSuffix());
		}

		return output.append(HEAT_RATE_TRANSLATION);
	}

	public static IFormattableTextComponent formatConductivityToString(double conductivity) {
		// Allocate the text component.
		IFormattableTextComponent output;

		// If the value is equal to the integer max, make it infinite.
		if ((int)conductivity == Integer.MAX_VALUE) {
			output = new StringTextComponent("∞");
		} else {
			MetricConverter metricRate = new MetricConverter(conductivity);
			output = new StringTextComponent(NUMBER_FORMATTER_TWO_DECIMAL.format(metricRate.getValue())).appendString(metricRate.getSuffix());
		}

		return output.append(HEAT_CONDUCTIVITY_TRANSLATION);
	}

	public static IFormattableTextComponent formatFluidToString(double currentFluid, double capacity) {
		return formatFluidToString(currentFluid, false, true).appendString("/").append(formatFluidToString(capacity));

	}

	public static IFormattableTextComponent formatFluidToString(double fluidAmount) {
		return formatFluidToString(fluidAmount, true, true);
	}

	public static IFormattableTextComponent formatFluidToString(double fluid, boolean includeUnits, boolean includeMetricUnit) {
		// Allocate the text component.
		IFormattableTextComponent output;

		// If the value is equal to the integer max, make it infinite.
		if ((int)fluid == Integer.MAX_VALUE) {
			output = new StringTextComponent("∞");
		} else {
			MetricConverter metricFluid = new MetricConverter(fluid, -1);
			output = new StringTextComponent(NUMBER_FORMATTER_ONE_DECIMAL.format(metricFluid.getValue()));

			if (includeMetricUnit) {
				output.appendString(metricFluid.getSuffix());
			}
		}

		// Append the units if requested.
		if (includeUnits) {
			output.append(FLUID_UNIT_TRANSLATION);
		}

		return output;
	}

	public static IFormattableTextComponent formatUnitRateToString(double rate, String unlocalizedUnit) {
		IFormattableTextComponent output = formatUnitRateToString(rate);
		output.append(new TranslationTextComponent(unlocalizedUnit));
		return output;
	}

	public static IFormattableTextComponent formatUnitRateToString(double rate) {
		MetricConverter metricPerUnit = new MetricConverter(rate);
		IFormattableTextComponent output = new StringTextComponent(NUMBER_FORMATTER_ONE_DECIMAL.format(metricPerUnit.getValue()));
		output.appendString(metricPerUnit.getSuffix());
		return output;
	}

	public static IFormattableTextComponent formatFluidRateToString(double fluidRate) {
		return formatFluidRateToString(fluidRate, true);
	}

	public static IFormattableTextComponent formatFluidRateToString(double fluidRate, boolean includeSpace) {
		// Allocate the text component.
		IFormattableTextComponent output;

		// If the value is equal to the integer max, make it infinite.
		if ((int)fluidRate == Integer.MAX_VALUE) {
			output = new StringTextComponent("∞");
		} else {
			MetricConverter metricRate = new MetricConverter(fluidRate, -1);
			output = new StringTextComponent(NUMBER_FORMATTER_ONE_DECIMAL.format(metricRate.getValue())).appendString(includeSpace ? " " : "").appendString(metricRate.getSuffix());
		}

		return output.append(FLUID_RATE_TRANSLATION);
	}

	public static IFormattableTextComponent formatNumberAsString(double number) {
		return new StringTextComponent(NUMBER_FORMATTER_TWO_DECIMAL.format(number));
	}

	public static IFormattableTextComponent formatNumberAsString(int number) {
		return new StringTextComponent(NUMBER_FORMATTER_TWO_DECIMAL.format(number));
	}
}
