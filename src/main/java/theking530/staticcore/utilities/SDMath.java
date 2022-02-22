package theking530.staticcore.utilities;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;

import net.minecraft.core.Direction;

public class SDMath {
	public static final Matrix4f IDENTITY;
	static {
		IDENTITY = new Matrix4f();
		IDENTITY.setIdentity();
	}
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

	public static long clamp(long value, long min, long max) {
		return Math.max(Math.min(value, max), min);
	}

	public static float clamp(float value, float min, float max) {
		return Math.max(Math.min(value, max), min);
	}

	public static double clamp(double value, double min, double max) {
		return Math.max(Math.min(value, max), min);
	}

	public static float lerp(int a, int b, float alpha) {
		return (b * alpha) + (a * (1 - alpha));
	}

	public static double lerp(double a, double b, float alpha) {
		return (b * alpha) + (a * (1 - alpha));
	}

	public static float lerp(float a, float b, float alpha) {
		return (b * alpha) + (a * (1 - alpha));
	}

	public static int multiplyRespectingOverflow(int base, int multiplier) {
		try {
			int output = Math.multiplyExact(base, multiplier);
			return output;
		} catch (Exception e) {
			return Integer.MAX_VALUE;
		}
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
			offset = new Vector3f(vector.y(), -vector.z(), vector.y());
			break;
		case UP:
			offset = new Vector3f(vector.y(), vector.z(), vector.y());
			break;
		case EAST:
			offset = new Vector3f(-vector.z(), vector.y(), -vector.x());
			break;
		case WEST:
			offset = new Vector3f(vector.z(), vector.y(), vector.x());
			break;
		case NORTH:
			offset = new Vector3f(-vector.x(), vector.y(), vector.z());
			break;
		case SOUTH:
			offset = new Vector3f(vector.x(), vector.y(), -vector.z());
			break;
		}
		return offset;
	}

	public static float getAngleBetweenVectors(AbstractVector start, AbstractVector finish) {
		AbstractVector normStart = start.copy();
		AbstractVector normEnd = finish.copy();
		float dot = normStart.dot(normEnd);
		float magnitudes = start.getLength() * finish.getLength();
		return (float) Math.acos(dot / magnitudes);
	}
}
