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

	public static String prettyFormatCamelCase(String camelCase) {
		// Regular Expression
		String regex = "([a-z]|[A-Z])([A-Z])";

		// Replacement string
		String replacement = "$1 $2";

		// Replace the given regex
		// with replacement string
		// and convert it to lower case.
		camelCase = camelCase.replace("_", "");
		camelCase = camelCase.replaceAll(regex, replacement);

		// Return string with first character capitalized.
		return camelCase.substring(0, 1).toUpperCase() + camelCase.substring(1);
	}
}
