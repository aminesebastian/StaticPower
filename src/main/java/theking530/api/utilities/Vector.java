package theking530.api.utilities;

/**
 * Basic vector class.
 * 
 * @author Amine Sebastian
 *
 */
public class Vector {

	protected float X;
	protected float Y;
	protected float Z;
	protected float W;

	public Vector(float x, float y) {
		X = x;
		Y = y;
		Z = 0.0f;
		W = 0.0f;
		;
	}

	public Vector(float x, float y, float z) {
		X = x;
		Y = y;
		Z = z;
		W = 0.0f;
	}

	public Vector(float x, float y, float z, float w) {
		X = x;
		Y = y;
		Z = z;
		W = w;
	}

	public float getX() {
		return X;
	}

	public void setX(float x) {
		X = x;
	}

	public float getY() {
		return Y;
	}

	public void setY(float y) {
		Y = y;
	}

	public float getZ() {
		return Z;
	}

	public void setZ(float z) {
		Z = z;
	}

	public float getW() {
		return W;
	}

	public void setW(float w) {
		W = w;
	}

	public Vector addVectors(Vector vec) {
		return new Vector(vec.getX() + X, vec.getY() + Y, vec.getZ() + Z, vec.getW() + W);
	}

	public Vector subtractVectors(Vector vec) {
		return new Vector(vec.getX() - X, vec.getY() - Y, vec.getZ() - Z, vec.getW() - W);
	}

	public float Length() {
		return (float) Math.sqrt((X * X + Y * Y + Z * Z + W * W));
	}

	public float SquaredLength() {
		return (X * X + Y * Y + Z * Z + W * W);
	}

	public Vector normalize() {
		float length = Length();
		return new Vector(X / length, Y / length, Z / length, W / length);
	}
}
