package theking530.staticpower.client.utilities;

import java.text.NumberFormat;

import net.minecraft.util.text.ITextComponent;
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
	/** Translation text component for Conductivity (σ). */
	public static final TranslationTextComponent HEAT_CONDUCTIVITY_TRANSLATION = new TranslationTextComponent("gui.staticpower.heat_conductivity");

	/** Single instance of number formatter. */
	private static final NumberFormat NUMBER_FORMATTER;

	/**
	 * Static initializer for number formatter.
	 */
	static {
		NUMBER_FORMATTER = NumberFormat.getInstance();
		NUMBER_FORMATTER.setGroupingUsed(true);
		NUMBER_FORMATTER.setMaximumFractionDigits(2);
	}

	/**
	 * Formats the provided energy into a string for display in the UI. Example,
	 * energy 50000 turns into 50kSV. Uses localization.
	 * 
	 * @param energy The amount of energy to format.
	 * @return The formatted string.
	 */
	public static ITextComponent formatEnergyToString(int energy, boolean includeUnits, boolean includeMetricUnit) {
		MetricConverter metricEnergy = new MetricConverter(energy);
		ITextComponent output = new StringTextComponent(NUMBER_FORMATTER.format(metricEnergy.getValue()));

		if (includeMetricUnit) {
			output.appendText(metricEnergy.getSuffix());
		}

		if (includeUnits) {
			output.appendSibling(ENERGY_UNIT_TRANSLATION);
		}
		return output;
	}

	public static ITextComponent formatEnergyToString(int energy, boolean includeUnits) {
		return formatEnergyToString(energy, includeUnits, true);
	}

	public static ITextComponent formatEnergyToString(int energy) {
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
	public static ITextComponent formatEnergyToString(int energy, int capacity) {
		return formatEnergyToString(energy, false, true).appendText("/").appendSibling(formatEnergyToString(capacity));

	}

	/**
	 * Formats the provided energyRate into a string for display in the UI. Example,
	 * energyRate 1000 turns into 1kSV/t. Uses localization.
	 * 
	 * @param energyRate The energy rate to format.
	 * @return The formatted string.
	 */
	public static ITextComponent formatEnergyRateToString(float energyRate) {
		MetricConverter metricRate = new MetricConverter(energyRate);
		return new StringTextComponent(NUMBER_FORMATTER.format(metricRate.getValue())).appendText(metricRate.getSuffix()).appendSibling(ENERGY_RATE_TRANSLATION);
	}

	public static ITextComponent formatHeatToString(float currentHeat, float capacity) {
		return formatHeatToString(currentHeat, false, true).appendText("/").appendSibling(formatHeatToString(capacity));

	}

	public static ITextComponent formatHeatToString(float heat) {
		return formatHeatToString(heat, true, true);
	}

	public static ITextComponent formatHeatToString(float heat, boolean includeUnits, boolean includeMetricUnit) {
		MetricConverter metricEnergy = new MetricConverter(heat);
		ITextComponent output = new StringTextComponent(NUMBER_FORMATTER.format(metricEnergy.getValue()));

		if (includeMetricUnit) {
			output.appendText(metricEnergy.getSuffix());
		}

		if (includeUnits) {
			output.appendText(" ").appendSibling(HEAT_UNIT_TRANSLATION);
		}
		return output;
	}

	public static ITextComponent formatHeatRateToString(float heatTransferRate) {
		MetricConverter metricRate = new MetricConverter(heatTransferRate);
		return new StringTextComponent(NUMBER_FORMATTER.format(metricRate.getValue())).appendText(" ").appendText(metricRate.getSuffix()).appendSibling(HEAT_RATE_TRANSLATION);
	}

	public static ITextComponent formatConductivityToString(float conductivity) {
		MetricConverter metricRate = new MetricConverter(conductivity);
		return new StringTextComponent(NUMBER_FORMATTER.format(metricRate.getValue())).appendText(metricRate.getSuffix()).appendSibling(HEAT_CONDUCTIVITY_TRANSLATION);
	}

	public static ITextComponent formatFluidToString(float currentFluid, float capacity) {
		return formatHeatToString(currentFluid, false, true).appendText("/").appendSibling(formatHeatToString(capacity));

	}

	public static ITextComponent formatFluidToString(float fluidAmount) {
		return formatHeatToString(fluidAmount, true, true);
	}

	public static ITextComponent formatFluidToString(float fluid, boolean includeUnits, boolean includeMetricUnit) {
		MetricConverter metricEnergy = new MetricConverter(fluid);
		ITextComponent output = new StringTextComponent(NUMBER_FORMATTER.format(metricEnergy.getValue()));

		if (includeMetricUnit) {
			output.appendText(metricEnergy.getSuffix());
		}

		if (includeUnits) {
			output.appendText(" ").appendSibling(FLUID_UNIT_TRANSLATION);
		}
		return output;
	}

	public static ITextComponent formatFluidRateToString(float fluidRate) {
		MetricConverter metricRate = new MetricConverter(fluidRate);
		return new StringTextComponent(NUMBER_FORMATTER.format(metricRate.getValue())).appendText(" ").appendText(metricRate.getSuffix()).appendSibling(FLUID_RATE_TRANSLATION);
	}

	public static ITextComponent formatNumberAsString(float number) {
		return new StringTextComponent(NUMBER_FORMATTER.format(number));
	}

	public static ITextComponent formatNumberAsString(int number) {
		return new StringTextComponent(NUMBER_FORMATTER.format(number));
	}
}
