package theking530.api.utilities;

/**
 * Basic color class.
 * 
 * @author Amine Sebastian
 *
 */
public class Color extends Vector {

	public Color(float x, float y, float z) {
		super(x, y, z, 1.0f);
	}

	public Color(float x, float y, float z, float w) {
		super(x, y, z, w);
	}

	public float getRed() {
		return X;
	}

	public float getGreen() {
		return Y;
	}

	public float getBlue() {
		return Z;
	}

	public float getAlpha() {
		return W;
	}
}
