package theking530.common.utilities;

/**
 * Basic color class.
 * 
 * @author Amine Sebastian
 *
 */
public class Color extends Vector4D {
	public static final Color WHITE = new Color(1.0f, 1.0f, 1.0f, 1.0f);

	public Color(float x, float y, float z) {
		super(x, y, z, 1.0f);
	}

	public Color(float x, float y, float z, float w) {
		super(x, y, z, w);
	}

	public float getRed() {
		return getX();
	}

	public float getGreen() {
		return getY();
	}

	public float getBlue() {
		return getZ();
	}

	public float getAlpha() {
		return getW();
	}

	/**
	 * Converts color from range 0 - 255 to the normalized range 0.0f - 1.0f.
	 * 
	 * @return A new instance of the color normalized.
	 */
	public Color fromEightBitToFloat() {
		return new Color(getRed() / 255.0f, getGreen() / 255.0f, getBlue() / 255.0f, getAlpha() / 255.0f);

	}

	public static Color fromEncodedInteger(int input) {
		return new Color((input >> 16 & 0xFF), (input >> 8 & 0xFF), (input & 0xFF), (input >> 24 & 0xFF));
	}

	/**
	 * Encodes the 4 8 bit values for color (RGBA) into a single 32bit integer. This
	 * requires that all values are less than or equal to 255. Float values are cast
	 * to integers.
	 * 
	 * @return
	 */
	public int encodeInInteger() {
		return (int) getAlpha() << 24 | (int) getRed() << 16 | (int) getGreen() << 8 | (int) getBlue();
	}
}
