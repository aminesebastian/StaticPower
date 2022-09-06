package theking530.staticpower.client.utilities;

import java.text.NumberFormat;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
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
	public static final TranslatableComponent ENERGY_UNIT_TRANSLATION = new TranslatableComponent("gui.staticpower.energy_unit");
	/** Translation text component for Static Volts Per Tick (SV/t). */
	public static final TranslatableComponent ENERGY_RATE_TRANSLATION = new TranslatableComponent("gui.staticpower.energy_unit_per_tick");

	/** Translation text component for millibuckets (mB). */
	public static final TranslatableComponent FLUID_UNIT_TRANSLATION = new TranslatableComponent("gui.staticpower.fluid_unit");
	/** Translation text component for millibuckets Per Tick (mB/t). */
	public static final TranslatableComponent FLUID_RATE_TRANSLATION = new TranslatableComponent("gui.staticpower.fluid_unit_per_tick");

	/** Translation text component for heat (H). */
	public static final TranslatableComponent HEAT_UNIT_TRANSLATION = new TranslatableComponent("gui.staticpower.heat_unit");
	/** Translation text component for Heat Per Tick (H/t). */
	public static final TranslatableComponent HEAT_RATE_TRANSLATION = new TranslatableComponent("gui.staticpower.heat_unit_per_tick");
	/** Translation text component for Conductivity. */
	public static final TranslatableComponent HEAT_CONDUCTIVITY_TRANSLATION = new TranslatableComponent("gui.staticpower.heat_conductivity_unit");

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

	public static MutableComponent formatTicksToTimeUnit(int ticks) {
		return new TranslatableComponent(NUMBER_FORMATTER_ONE_DECIMAL.format(ticks / 20.0)).append(new TranslatableComponent("gui.staticpower.seconds.short"));
	}

	/**
	 * Formats the provided energy into a string for display in the UI. Example,
	 * energy 50000 turns into 50kSV. Uses localization.
	 * 
	 * @param energy The amount of energy to format.
	 * @return The formatted string.
	 */
	@Deprecated
	public static MutableComponent formatEnergyToString(long energy, boolean includeUnits, boolean includeMetricUnit) {
		// Allocate the text component.
		MutableComponent output;

		// If the value is equal to the integer max, make it infinite.
		if (energy == Integer.MAX_VALUE) {
			output = new TextComponent("∞");
		} else {
			// Perform the metric conversion.
			MetricConverter metricEnergy = new MetricConverter(energy, -1);
			output = new TextComponent(NUMBER_FORMATTER_TWO_DECIMAL.format(metricEnergy.getValue()));

			// Include the metric unit if requested.
			if (includeMetricUnit) {
				output.append(metricEnergy.getSuffix());
			}
		}

		if (includeUnits) {
			output.append(ENERGY_UNIT_TRANSLATION);
		}
		return output;
	}

	@Deprecated
	public static MutableComponent formatEnergyToString(long energy, boolean includeUnits) {
		return formatEnergyToString(energy, includeUnits, true);
	}

	@Deprecated
	public static MutableComponent formatEnergyToString(long energy) {
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
	@Deprecated
	public static MutableComponent formatEnergyToString(long energy, long capacity) {
		return formatEnergyToString(energy, false, true).append("/").append(formatEnergyToString(capacity));

	}

	/**
	 * Formats the provided energyRate into a string for display in the UI. Example,
	 * energyRate 1000 turns into 1kSV/t. Uses localization.
	 * 
	 * @param energyRate The energy rate to format.
	 * @return The formatted string.
	 */
	public static MutableComponent formatEnergyRateToString(double energyRate) {
		// Allocate the text component.
		MutableComponent output;

		// If the value is equal to the integer max, make it infinite.
		if ((int) energyRate == Integer.MAX_VALUE) {
			output = new TextComponent("∞");
		} else {
			MetricConverter metricRate = new MetricConverter(energyRate, -1);
			output = new TextComponent(NUMBER_FORMATTER_TWO_DECIMAL.format(metricRate.getValue())).append(metricRate.getSuffix());
		}
		return output.append(ENERGY_RATE_TRANSLATION);
	}

	public static MutableComponent formatHeatToString(int currentHeat, int capacity) {
		return formatHeatToString(currentHeat, false, true).append("/").append(formatHeatToString(capacity));

	}

	public static MutableComponent formatHeatToString(int heat) {
		return formatHeatToString(heat, true, true);
	}

	public static MutableComponent formatHeatToString(int heat, boolean includeUnits, boolean includeMetricUnit) {
		// Allocate the text component.
		MutableComponent output;

		// If the value is equal to the integer max, make it infinite.
		if ((int) heat == Integer.MAX_VALUE) {
			output = new TextComponent("∞");
		} else {
			MetricConverter metricEnergy = new MetricConverter(heat, -1);
			output = new TextComponent(NUMBER_FORMATTER_ONE_DECIMAL.format(metricEnergy.getValue()));

			if (includeMetricUnit) {
				output.append(metricEnergy.getSuffix());
			}
		}

		if (includeUnits) {
			output.append(" ").append(HEAT_UNIT_TRANSLATION);
		}
		return output;
	}

	public static MutableComponent formatHeatRateToString(double heatTransferRate) {
		// Allocate the text component.
		MutableComponent output;

		// If the value is equal to the integer max, make it infinite.
		if ((int) heatTransferRate == Integer.MAX_VALUE) {
			output = new TextComponent("∞");
		} else {
			MetricConverter metricRate = new MetricConverter(heatTransferRate, -1);
			output = new TextComponent(NUMBER_FORMATTER_ONE_DECIMAL.format(metricRate.getValue())).append(" ").append(metricRate.getSuffix());
		}

		return output.append(HEAT_RATE_TRANSLATION);
	}

	public static MutableComponent formatConductivityToString(double conductivity) {
		// Allocate the text component.
		MutableComponent output;

		// If the value is equal to the integer max, make it infinite.
		if ((int) conductivity == Integer.MAX_VALUE) {
			output = new TextComponent("∞");
		} else {
			MetricConverter metricRate = new MetricConverter(conductivity);
			output = new TextComponent(NUMBER_FORMATTER_TWO_DECIMAL.format(metricRate.getValue())).append(metricRate.getSuffix());
		}

		return output.append(HEAT_CONDUCTIVITY_TRANSLATION);
	}

	public static MutableComponent formatFluidToString(double currentFluid, double capacity) {
		return formatFluidToString(currentFluid, false, true).append("/").append(formatFluidToString(capacity));

	}

	public static MutableComponent formatFluidToString(double fluidAmount) {
		return formatFluidToString(fluidAmount, true, true);
	}

	public static MutableComponent formatFluidToString(double fluid, boolean includeUnits, boolean includeMetricUnit) {
		// Allocate the text component.
		MutableComponent output;

		// If the value is equal to the integer max, make it infinite.
		if ((int) fluid == Integer.MAX_VALUE) {
			output = new TextComponent("∞");
		} else {
			MetricConverter metricFluid = new MetricConverter(fluid, -1);
			output = new TextComponent(NUMBER_FORMATTER_ONE_DECIMAL.format(metricFluid.getValue()));

			if (includeMetricUnit) {
				output.append(metricFluid.getSuffix());
			}
		}

		// Append the units if requested.
		if (includeUnits) {
			output.append(FLUID_UNIT_TRANSLATION);
		}

		return output;
	}

	public static MutableComponent formatUnitRateToString(double rate, String unlocalizedUnit) {
		MutableComponent output = formatUnitRateToString(rate);
		output.append(new TranslatableComponent(unlocalizedUnit));
		return output;
	}

	public static MutableComponent formatUnitRateToString(double rate) {
		MetricConverter metricPerUnit = new MetricConverter(rate);
		MutableComponent output = new TextComponent(NUMBER_FORMATTER_ONE_DECIMAL.format(metricPerUnit.getValue()));
		output.append(metricPerUnit.getSuffix());
		return output;
	}

	public static MutableComponent formatFluidRateToString(double fluidRate) {
		return formatFluidRateToString(fluidRate, true);
	}

	public static MutableComponent formatFluidRateToString(double fluidRate, boolean includeSpace) {
		// Allocate the text component.
		MutableComponent output;

		// If the value is equal to the integer max, make it infinite.
		if ((int) fluidRate == Integer.MAX_VALUE) {
			output = new TextComponent("∞");
		} else {
			MetricConverter metricRate = new MetricConverter(fluidRate, -1);
			output = new TextComponent(NUMBER_FORMATTER_ONE_DECIMAL.format(metricRate.getValue())).append(includeSpace ? " " : "").append(metricRate.getSuffix());
		}

		return output.append(FLUID_RATE_TRANSLATION);
	}

	public static MutableComponent formatNumberAsString(double number) {
		return new TextComponent(NUMBER_FORMATTER_TWO_DECIMAL.format(number));
	}

	public static MutableComponent formatNumberAsStringNoDecimal(double number) {
		return new TextComponent(NUMBER_FORMATTER_NO_DECIMAL.format(Double.isNaN(number) ? 0 : number));
	}

	public static MutableComponent formatNumberAsStringOneDecimal(double number) {
		return new TextComponent(NUMBER_FORMATTER_ONE_DECIMAL.format(number));
	}

	public static MutableComponent formatNumberAsString(int number) {
		return new TextComponent(NUMBER_FORMATTER_TWO_DECIMAL.format(number));
	}

	public static MutableComponent formatNumberAsPercentStringNoDecimal(double number) {
		return new TextComponent(NUMBER_FORMATTER_NO_DECIMAL.format(Double.isNaN(number) ? 0 : number * 100)).append("%");
	}

	public static MutableComponent formatNumberAsPercentStringOneDecimal(double number) {
		return new TextComponent(NUMBER_FORMATTER_ONE_DECIMAL.format(Double.isNaN(number) ? 0 : number * 100)).append("%");
	}

	public static MutableComponent createTooltipBulletpoint(String localizationKey, ChatFormatting color) {
		return new TextComponent(color.toString() + "• " + new TranslatableComponent(localizationKey).getString() + " ");
	}
}
