package theking530.staticcore.utilities.math;

/**
 * Basic vector class.
 * 
 * @author Amine Sebastian
 *
 */
public class Vector4D extends AbstractVector<Vector4D> {
	/**
	 * Default UV vector 0.0f, 0.0f, 1.0f, 1.0f. DO NOT MODIFY.
	 */
	public static final Vector4D DEFAULT_UV = new Vector4D(0.0f, 0.0f, 1.0f, 1.0f);

	public Vector4D() {
		this(0, 0, 0, 0);
	}

	public Vector4D(float x, float y, float z) {
		this(x, y, z, 0.0f);
	}

	public Vector4D(float x, float y, float z, float w) {
		super(x, y, z, w);
	}

	public float getX() {
		return values[0];
	}

	public int getXi() {
		return Math.round(values[0]);
	}

	public void setX(float x) {
		values[0] = x;
	}

	public void addX(float x) {
		values[0] += x;
	}

	public float getY() {
		return values[1];
	}

	public int getYi() {
		return Math.round(values[1]);
	}

	public void setY(float y) {
		values[1] = y;
	}

	public void addY(float y) {
		values[1] += y;
	}

	public float getZ() {
		return values[2];
	}

	public void setZ(float z) {
		values[2] = z;
	}

	public int getZi() {
		return Math.round(values[2]);
	}

	public float getW() {
		return values[3];
	}

	public void setW(float w) {
		values[3] = w;
	}

	public int getWi() {
		return Math.round(values[3]);
	}

	public Vector4D add(float x, float y, float z, float w) {
		add(new Vector4D(x, y, z, w));
		return this;
	}

	public Vector4D substract(float x, float y, float z, float w) {
		subtract(new Vector4D(x, y, z, w));
		return this;
	}

	@Override
	public Vector4D copy() {
		return new Vector4D(values[0], values[1], values[2], values[3]);
	}
}
