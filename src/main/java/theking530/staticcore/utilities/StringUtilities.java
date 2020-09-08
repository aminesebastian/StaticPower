package theking530.staticcore.utilities;

public class StringUtilities {

	public static boolean isNumber(String input) {
		// If the input is null or empty, return false.
		if (input == null || input.isEmpty()) {
			return false;
		}

		// Try to parse, if it throws an exception, it is not an int.
		try {
			Integer.parseInt(input);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
