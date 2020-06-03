package theking530.api.utilities;

import java.util.Random;

public class SDMath {
	private static final Random RANDOM = new Random();

	public static boolean diceRoll(double percentage) {
		return RANDOM.nextDouble() <= percentage;
	}
}
