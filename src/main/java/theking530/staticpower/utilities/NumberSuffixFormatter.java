package theking530.staticpower.utilities;

public class NumberSuffixFormatter {
	public int Value;
	public String Suffix;

	public NumberSuffixFormatter(int InitialValue) {
		// Keep dividing the Value by 1000 until we hit a current value of < 1000.
		// For each iteration of the loop, increment the suffix index.
		Value = InitialValue;
		int suffixIndex = 0;
		while (Value / 1000 > 0) {
			Value /= 1000;
			suffixIndex++;
		}

		// Update the suffix (maxing out at Yotta).
		switch (suffixIndex) {
		case 0:
			Suffix = "";
			break;
		case 1:
			Suffix = "k";
			break;
		case 2:
			Suffix = "M";
			break;
		case 3:
			Suffix = "G";
			break;
		case 4:
			Suffix = "T";
			break;
		case 5:
			Suffix = "P";
			break;
		case 6:
			Suffix = "E";
			break;
		case 7:
			Suffix = "Z";
			break;
		case 8:
			Suffix = "Y";
			break;
		default:
			Suffix = "Y";
			break;
		}
	}
}
