package theking530.staticcore.utilities;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class MetricConverter {
	private static final DecimalFormat FORMATTER = new DecimalFormat("#.##");
	private static final String[] SUFFIXES = { "y", "z", "a", "f", "p", "n", "u", "m", "", "K", "M", "G", "T", "P", "E", "Z", "Y" };
	private double Value;
	private String Suffix;

	public MetricConverter(double value, int initialOffset) {
		if (value == 0 || Double.isInfinite(value) || Double.isNaN(value)) {
			Value = value;
			Suffix = "";
			return;
		}

		// Make sure we round after a sufficient number of decimals to avoid funky
		// values (like returning 1000mw/t)
		BigDecimal roundOff = new BigDecimal(value).setScale(5, RoundingMode.HALF_UP);
		Value = Math.abs(roundOff.doubleValue());

		// Keep dividing the Value by 1000 until we hit a current value of < 1000.
		// For each iteration of the loop, increment the suffix index.
		int suffixIndex = 8 + initialOffset;

		if (Value > 1) {
			while (Value / 1000 >= 1) {
				Value /= 1000;
				suffixIndex++;
				if (suffixIndex > 16) {
					break;
				}
			}
		} else if (Value < 1) {
			while (Value < 1) {
				Value *= 1000;
				suffixIndex--;
				if (suffixIndex < 0) {
					break;
				}
			}
		}

		// Adjust the updated value if the original value was negative.
		if (value < 0) {
			Value *= -1;
		}

		// Cache the suffix.
		if (suffixIndex >= 0 && suffixIndex < SUFFIXES.length - 1) {
			Suffix = SUFFIXES[suffixIndex];
		} else {
			Suffix = "OUT_OF_RANGE_C'MON";
		}
	}

	public MetricConverter(double value) {
		this(value, 0);
	}

	public double getValue() {
		return Value;
	}

	public String getSuffix() {
		return Suffix;
	}

	public String getValueAsString(boolean includeSuffix) {
		if (includeSuffix) {
			return FORMATTER.format(Value) + Suffix;
		} else {
			return FORMATTER.format(Value);
		}
	}
}
