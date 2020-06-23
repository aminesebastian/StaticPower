package theking530.api.utilities;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class SDMath {
	private static final Random RANDOM = new Random();

	public static boolean diceRoll(double percentage) {
		return RANDOM.nextDouble() <= percentage;
	}

	/**
	 * Gets a random integer in the provided range (inclusive).
	 * 
	 * @param min
	 * @param max
	 * @return
	 */
	public static int getRandomIntInRange(int min, int max) {
		return ThreadLocalRandom.current().nextInt(min, max + 1);
	}

	public static int getSmallestFactor(int value) {
		return getSmallestFactor(value, 2);
	}

	public static int getSmallestFactor(int value, int minimumFactor) {
		// Cover the base case.
		if (value == 0) {
			return 0;
		}

		// Early out for a value of 1.
		int target = Math.abs(value);
		if (target == 1) {
			return 1;
		}

		// If the minimum factor is larger than the target, just return the target.
		if (minimumFactor > target) {
			return target;
		}
		
		// Iterate from 2 to the upperbound, and check if we ever hit a modulus with no
		// remained.
		for (int i = minimumFactor; i < value + 1; i++) {
			if (target % i == 0) {
				return i;
			}
		}

		// If nothing was found, return the absolute of the initial value;
		return target;
	}
}
