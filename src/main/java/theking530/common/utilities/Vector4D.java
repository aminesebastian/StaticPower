package theking530.common.utilities;

/**
 * Basic vector class.
 * 
 * @author Amine Sebastian
 *
 */
public class Vector4D extends Vector3D {

	public Vector4D(float x, float y, float z) {
		this(x, y, z, 0.0f);
	}

	public Vector4D(float x, float y, float z, float w) {
		super(x, y, z);
		values.add(w);
	}

	public float getW() {
		return values.get(3);
	}

	public void setW(float w) {
		values.set(3, w);
	}
	@Override
	public Vector4D clone() {
		return new Vector4D(values.get(0), values.get(1), values.get(2), values.get(3));
	}
}
