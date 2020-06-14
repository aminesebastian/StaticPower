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
	/** Translation text component for Forge Energy (FE). */
	public static final TranslationTextComponent ENERGY_UNIT_TRANSLATION = new TranslationTextComponent("gui.staticpower.energy_unit");
	/** Translation text component for Forge Energy Per Tick (FE/t). */
	public static final TranslationTextComponent ENERGY_RATE_TRANSLATION = new TranslationTextComponent("gui.staticpower.energy_unit_per_tick");
	/** Single instance of number formatter. */
	private static final NumberFormat NUMBER_FORMATTER;

	/**
	 * Static initializer for number formatter.
	 */
	static {
		NUMBER_FORMATTER = NumberFormat.getInstance();
		NUMBER_FORMATTER.setGroupingUsed(true);
	}

	/**
	 * Formats the provided energy into a string for display in the UI. Example,
	 * energy 50000 turns into 50,000 FE. Uses localization.
	 * 
	 * @param energy The amount of energy to format.
	 * @return The formatted string.
	 */
	public static ITextComponent formatEnergyToString(int energy) {
		MetricConverter metricEnergy = new MetricConverter(energy);
		return new StringTextComponent(NUMBER_FORMATTER.format(metricEnergy.getValue())).appendText(metricEnergy.getSuffix()).appendSibling(ENERGY_UNIT_TRANSLATION);
	}

	/**
	 * Formats the provided energy and capacity into a string for display in the UI.
	 * Example, energy 50000 and storage 100000 turns into 50,000/100,000 FE. Uses
	 * localization.
	 * 
	 * @param energy   The amount of energy to format as the numerator.
	 * @param capacity The maximum amount of energy to use as the denominator.
	 * @return The formatted string.
	 */
	public static ITextComponent formatEnergyToString(int energy, int capacity) {
		MetricConverter metricCapacity = new MetricConverter(capacity);
		MetricConverter metricEnergy = new MetricConverter(energy);

		// If the suffixes are different, put them both in the string, otherwise, just
		// use the capacity suffix.
		if (!metricCapacity.getSuffix().equals(metricEnergy.getSuffix())) {
			return formatEnergyToString(energy).appendText("/").appendSibling(formatEnergyToString(capacity));
		} else {
			return formatEnergyToString(energy).appendText("/").appendSibling(formatEnergyToString(capacity));
		}

	}

	/**
	 * Formats the provided energyRate into a string for display in the UI. Example,
	 * energyRate 1000 turns into 1kFE/t. Uses localization.
	 * 
	 * @param energyRate The energy rate to format.
	 * @return The formatted string.
	 */
	public static ITextComponent formatEnergyRateToString(int energyRate) {
		MetricConverter metricRate = new MetricConverter(energyRate);
		return new StringTextComponent(NUMBER_FORMATTER.format(metricRate.getValue())).appendText(metricRate.getSuffix()).appendSibling(ENERGY_RATE_TRANSLATION);
	}
}
