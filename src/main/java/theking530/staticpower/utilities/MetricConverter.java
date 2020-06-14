package theking530.staticpower.utilities;

public class MetricConverter {
	private static final String[] SUFFIXES = { "", "k", "M", "G", "T", "P", "E", "Z", "Y" };
	private int Value;
	private String Suffix;

	public MetricConverter(int value) {
		// Keep dividing the Value by 1000 until we hit a current value of < 1000.
		// For each iteration of the loop, increment the suffix index.
		int suffixIndex = 0;
		this.Value = value;
		while (Value / 1000 > 0) {
			Value /= 1000;
			suffixIndex++;
		}
		if (suffixIndex < SUFFIXES.length - 1) {
			Suffix = SUFFIXES[suffixIndex];
		} else {
			Suffix = "TOO_BIG_C'MON";
		}
	}

	public int getValue() {
		return Value;
	}

	public String getSuffix() {
		return Suffix;
	}
}
