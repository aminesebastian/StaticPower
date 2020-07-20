package theking530.staticpower.utilities;

import java.text.DecimalFormat;

public class MetricConverter {
	private static final DecimalFormat FORMATTER = new DecimalFormat("#.##");
	private static final String[] SUFFIXES = { "", "k", "M", "G", "T", "P", "E", "Z", "Y" };
	private float Value;
	private String Suffix;

	public MetricConverter(int value) {
		// Keep dividing the Value by 1000 until we hit a current value of < 1000.
		// For each iteration of the loop, increment the suffix index.
		int suffixIndex = 0;
		this.Value = value;
		while (Value / 1000 >= 1) {
			Value /= 1000;
			suffixIndex++;
		}

		// Cache the suffix.
		if (suffixIndex < SUFFIXES.length - 1) {
			Suffix = SUFFIXES[suffixIndex];
		} else {
			Suffix = "TOO_BIG_C'MON";
		}
	}

	public float getValue() {
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
