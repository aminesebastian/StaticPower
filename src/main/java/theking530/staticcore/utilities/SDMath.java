package theking530.staticcore.utilities;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import net.minecraft.client.renderer.Vector3f;
import net.minecraft.util.Direction;

public class SDMath {
	private static final Random RANDOM = new Random();

	public static boolean diceRoll(double percentage) {
		if (percentage >= 1.0d) {
			return true;
		}
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

	public static int clamp(int value, int min, int max) {
		return Math.max(Math.min(value, max), min);
	}

	public static float clamp(float value, float min, float max) {
		return Math.max(Math.min(value, max), min);
	}

	public static double clamp(double value, double min, double max) {
		return Math.max(Math.min(value, max), min);
	}

	/**
	 * Transforms the provided vector to point in the direction of the provided
	 * vector.
	 * 
	 * @param dir
	 * @param vector
	 * @return
	 */
	public static Vector3f transformVectorByDirection(Direction dir, Vector3f vector) {
		Vector3f offset = null;
		switch (dir) {
		case DOWN:
			offset = new Vector3f(vector.getY(), -vector.getZ(), vector.getY());
			break;
		case UP:
			offset = new Vector3f(vector.getY(), vector.getZ(), vector.getY());
			break;
		case EAST:
			offset = new Vector3f(-vector.getZ(), vector.getY(), vector.getX());
			break;
		case WEST:
			offset = new Vector3f(vector.getZ(), vector.getY(), vector.getX());
			break;
		case NORTH:
			offset = new Vector3f(vector.getX(), vector.getY(), vector.getZ());
			break;
		case SOUTH:
			offset = new Vector3f(vector.getX(), vector.getY(), -vector.getZ());
			break;
		}
		return offset;
	}
}
