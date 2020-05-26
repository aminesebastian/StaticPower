package theking530.staticpower.client.utilities;

import java.text.NumberFormat;

import theking530.staticpower.utilities.NumberSuffixFormatter;

/**
 * Utility class to format various values into display in the UI including
 * energy and fluid.
 * 
 * @author Amine Sebastian
 *
 */
public class GuiFormattingUtilities {
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
	public static String formatEnergyToString(int energy) {
		NumberSuffixFormatter formatterValue = new NumberSuffixFormatter(energy);
		return String.format("%1$s%2$s%3$s", NUMBER_FORMATTER.format(formatterValue.Value), formatterValue.Suffix, "FE");
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
	public static String formatEnergyToString(int energy, int capacity) {
		// Format both the energy and the capacity.
		NumberSuffixFormatter energyFormatted = new NumberSuffixFormatter(energy);
		NumberSuffixFormatter capacityFormatted = new NumberSuffixFormatter(capacity);

		// If the suffixes are different, put them both in the string, otherwise, just
		// use the capacity suffix.
		if (energyFormatted.Suffix.equals(capacityFormatted.Suffix)) {
			return String.format("%1$s%2$s%3$s/%4$s%5$s%6$s", NUMBER_FORMATTER.format(energyFormatted.Value), energyFormatted.Suffix, "FE", NUMBER_FORMATTER.format(capacityFormatted.Value), capacityFormatted.Suffix, "FE");
		} else {
			return String.format("%1$s/%2$s%3$s%4$s", NUMBER_FORMATTER.format(energyFormatted.Value), NUMBER_FORMATTER.format(capacityFormatted.Value), capacityFormatted.Suffix, "FE");
		}

	}

	/**
	 * Formats the provided energyRate into a string for display in the UI. Example,
	 * energyRate 1000 turns into 1,000 FE/t. Uses localization.
	 * 
	 * @param energyRate The energy rate to format.
	 * @return The formatted string.
	 */
	public static String formatEnergyRateToString(int energyRate) {
		NumberSuffixFormatter formatterValue = new NumberSuffixFormatter(energyRate);
		return String.format("%1$s%2$s%3$s", NUMBER_FORMATTER.format(formatterValue.Value), formatterValue.Suffix, "FE/t");
	}
}
