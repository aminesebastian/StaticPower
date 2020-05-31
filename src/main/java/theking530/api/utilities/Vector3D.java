package theking530.api.utilities;

public class Vector3D extends Vector2D {

	public Vector3D(float x, float y, float z) {
		super(x, y);
		values.add(z);

	}

	public float getZ() {
		return values.get(2);
	}

	public void setZ(float z) {
		values.set(2, z);
	}
}
