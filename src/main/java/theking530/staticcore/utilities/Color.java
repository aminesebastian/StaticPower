package theking530.staticcore.utilities;

import java.util.Arrays;
import java.util.List;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;

/**
 * Basic color class.
 * 
 * @author Amine Sebastian
 *
 */
public class Color extends Vector4D {

	public static final List<String> DYE_COLORS = Arrays.asList("white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray", "light_gray", "cyan", "purple", "blue", "brown", "green",
			"red", "black");

	public static final Color EIGHT_BIT_RED = new Color(255.0f, 0.0f, 0.0f, 255.0f);
	public static final Color EIGHT_BIT_WHITE = new Color(255.0f, 255.0f, 255.0f, 255.0f);
	public static final Color EIGHT_BIT_GREY = new Color(128.0f, 128.0f, 128.0f, 255.0f);
	public static final Color EIGHT_BIT_DARK_GREY = new Color(63.0f, 63.0f, 63.0f, 255.0f);
	public static final Color EIGHT_BIT_YELLOW = new Color(255.0f, 255.0f, 85.0f, 1.0f);
	public static final Color EIGHT_BIT_LIGHT_PURPLE = new Color(255.0f, 85.0f, 85.0f, 1.0f);
	public static final Color RED = new Color(1.0f, 0.0f, 0.0f, 1.0f);
	public static final Color GREEN = new Color(0.0f, 1.0f, 0.0f, 1.0f);
	public static final Color BLUE = new Color(0.0f, 0.0f, 1.0f, 1.0f);
	public static final Color WHITE = new Color(1.0f, 1.0f, 1.0f, 1.0f);
	public static final Color GREY = new Color(0.5f, 0.5f, 0.5f, 1.0f);
	public static final Color DARK_GREY = new Color(0.24705882352f, 0.24705882352f, 0.24705882352f, 1.0f);
	public static final Color BLACK = new Color(0.0f, 0.0f, 0.0f, 1.0f);

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

	public Color lighten(float r, float g, float b, float a) {
		add(new Vector4D(r, g, b, a));
		return this;
	}

	public Color darken(float r, float g, float b, float a) {
		subtract(new Vector4D(r, g, b, a));
		return this;
	}

	@Override
	public String toString() {
		return toStringInternal(true);
	}

	/**
	 * Converts color from range 0 - 255 to the normalized range 0.0f - 1.0f.
	 * 
	 * @return A new instance of the color normalized.
	 */
	public Color fromEightBitToFloat() {
		return new Color(getRed() / 255.0f, getGreen() / 255.0f, getBlue() / 255.0f, getAlpha() / 255.0f);

	}

	/**
	 * Converts color from range 0.0f - 1.0f to the eight bit range 0 - 255.
	 * 
	 * @return A new instance of the color in eight bit format.
	 */
	public Color fromFloatToEightBit() {
		return new Color(getRed() * 255.0f, getGreen() * 255.0f, getBlue() * 255.0f, getAlpha() * 255.0f);

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

	@Override
	public Color copy() {
		return new Color(values.get(0), values.get(1), values.get(2), values.get(3));
	}

	public void toBuffer(FriendlyByteBuf buff) {
		buff.writeFloat(getRed());
		buff.writeFloat(getGreen());
		buff.writeFloat(getBlue());
		buff.writeFloat(getAlpha());
	}

	public static Color fromJson(JsonObject object) {
		return new Color(object.get("r").getAsFloat(), object.get("g").getAsFloat(), object.get("b").getAsFloat(), object.get("a").getAsFloat());
	}

	public static Color fromBuffer(FriendlyByteBuf buff) {
		return new Color(buff.readFloat(), buff.readFloat(), buff.readFloat(), buff.readFloat());
	}

	public static Color lerp(Color first, Color second, float alpha) {
		alpha = SDMath.clamp(alpha, 0, 1);
		float inverseAlpha = 1.0f - alpha;
		return new Color(first.getRed() * inverseAlpha + second.getRed() * alpha, first.getGreen() * inverseAlpha + second.getGreen() * alpha,
				first.getBlue() * inverseAlpha + second.getBlue() * alpha);
	}
}
