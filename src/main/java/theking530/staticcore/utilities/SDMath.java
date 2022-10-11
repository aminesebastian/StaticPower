package theking530.staticcore.utilities;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

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

	public static Vector4D getRandomVectorOffset() {
		float randomX = (RANDOM.nextFloat() - 0.5f) * 2;
		float randomY = (RANDOM.nextFloat() - 0.5f) * 2;
		float randomZ = (RANDOM.nextFloat() - 0.5f) * 2;
		float randomW = (RANDOM.nextFloat() - 0.5f) * 2;
		return new Vector4D(randomX, randomY, randomZ, randomW);
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
		return (b * alpha) + (a * (1.0f - alpha));
	}

	public static double lerp(double a, double b, float alpha) {
		return (b * alpha) + (a * (1.0f - alpha));
	}

	public static float lerp(float a, float b, float alpha) {
		return (b * alpha) + (a * (1.0f - alpha));
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

	public static Vector3f translateRelativeOffset(Direction dir, Vector3f vector) {
		if (dir == Direction.NORTH) {
			return new Vector3f(vector.z() + 1, vector.y(), -vector.x() + 1);
		} else if (dir == Direction.SOUTH) {
			return new Vector3f(-vector.z(), vector.y(), vector.x());
		} else if (dir == Direction.EAST) {
			return new Vector3f(vector.x(), vector.y(), -vector.z());
		} else if (dir == Direction.WEST) {
			return new Vector3f(-vector.x() + 1, vector.y(), vector.z() + 1);
		}
		return vector;
	}

	public static Vec3 getPointAlongQuadraticBezierCurve(float alpha, Vec3 start, Vec3 controlPoint, Vec3 end) {
		double x = (1 - alpha) * (1 - alpha) * start.x() + 2 * (1 - alpha) * alpha * controlPoint.x() + alpha * alpha * end.x();
		double y = (1 - alpha) * (1 - alpha) * start.y() + 2 * (1 - alpha) * alpha * controlPoint.y() + alpha * alpha * end.y();
		double z = (1 - alpha) * (1 - alpha) * start.z() + 2 * (1 - alpha) * alpha * controlPoint.z() + alpha * alpha * end.z();
		return new Vec3(x, y, z);

	}

	public static Vec3 getQuadrativeBezierDerivative(float alpha, Vec3 start, Vec3 controlPoint, Vec3 end) {
		Vec3 d1 = new Vec3(2 * (controlPoint.x() - start.x()), 2 * (controlPoint.y() - start.y()), 2 * (controlPoint.z() - start.z()));
		Vec3 d2 = new Vec3(2 * (end.x() - controlPoint.x()), 2 * (end.y() - controlPoint.y()), 2 * (controlPoint.z() - start.z()));

		double x = (1 - alpha) * d1.x() + alpha * d2.x();
		double y = (1 - alpha) * d1.y() + alpha * d2.y();
		double z = (1 - alpha) * d1.z() + alpha * d2.z();

		return new Vec3(x, y, z);
	}

	public static Vec3 getQuadraticBezierSecondDerivative(float alpha, Vec3 start, Vec3 controlPoint, Vec3 end) {
		double x = 2 * (end.x() - 2 * controlPoint.x() + start.x());
		double y = 2 * (end.y() - 2 * controlPoint.y() + start.y());
		double z = 2 * (end.z() - 2 * controlPoint.z() + start.z());
		return new Vec3(x, y, z);
	}

	public static Vec3 getQuadraticBezierNormal(float alpha, Vec3 start, Vec3 controlPoint, Vec3 end) {
		Vec3 deriv = getQuadrativeBezierDerivative(alpha, start, controlPoint, end).normalize();
		Vec3 secondDeriv = getQuadraticBezierSecondDerivative(alpha, start, controlPoint, end);
		Vec3 b = deriv.add(secondDeriv).normalize();
		Vec3 r = b.cross(deriv).normalize();
		Vec3 normal = r.cross(deriv).normalize();
		return new Vec3(normal.x, normal.y, normal.z);
	}
}
