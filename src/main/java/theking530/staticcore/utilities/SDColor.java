package theking530.staticcore.utilities;

import java.util.Arrays;
import java.util.List;

import com.google.gson.JsonObject;
import com.mojang.math.Vector3f;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.FriendlyByteBuf;

/**
 * Basic color class.
 * 
 * @author Amine Sebastian
 *
 */
public class SDColor extends AbstractVector<SDColor> {
	public static final Codec<SDColor> CODEC = RecordCodecBuilder.create(instance -> instance
			.group(Codec.FLOAT.fieldOf("r").forGetter(vector -> vector.getRed()), Codec.FLOAT.fieldOf("g").forGetter(vector -> vector.getBlue()),
					Codec.FLOAT.fieldOf("b").forGetter(vector -> vector.getGreen()), Codec.FLOAT.fieldOf("a").forGetter(vector -> vector.getAlpha()))
			.apply(instance, SDColor::new));

	public static final List<String> DYE_COLORS = Arrays.asList("white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray", "light_gray", "cyan", "purple",
			"blue", "brown", "green", "red", "black");

	public static final SDColor EIGHT_BIT_RED = new SDColor(255.0f, 0.0f, 0.0f, 255.0f);
	public static final SDColor EIGHT_BIT_WHITE = new SDColor(255.0f, 255.0f, 255.0f, 255.0f);
	public static final SDColor EIGHT_BIT_BLACK = new SDColor(0.0f, 0.0f, 0.0f, 255.0f);
	public static final SDColor EIGHT_BIT_GREY = new SDColor(128.0f, 128.0f, 128.0f, 255.0f);
	public static final SDColor EIGHT_BIT_DARK_GREY = new SDColor(63.0f, 63.0f, 63.0f, 255.0f);
	public static final SDColor EIGHT_BIT_YELLOW = new SDColor(255.0f, 255.0f, 85.0f, 1.0f);
	public static final SDColor EIGHT_BIT_LIGHT_PURPLE = new SDColor(255.0f, 85.0f, 85.0f, 1.0f);
	public static final SDColor RED = new SDColor(1.0f, 0.0f, 0.0f, 1.0f);
	public static final SDColor GREEN = new SDColor(0.0f, 1.0f, 0.0f, 1.0f);
	public static final SDColor BLUE = new SDColor(0.0f, 0.0f, 1.0f, 1.0f);
	public static final SDColor WHITE = new SDColor(1.0f, 1.0f, 1.0f, 1.0f);
	public static final SDColor GREY = new SDColor(0.5f, 0.5f, 0.5f, 1.0f);
	public static final SDColor DARK_GREY = new SDColor(0.24705882352f, 0.24705882352f, 0.24705882352f, 1.0f);
	public static final SDColor BLACK = new SDColor(0.0f, 0.0f, 0.0f, 1.0f);

	public SDColor(float x, float y, float z) {
		super(x, y, z, 1.0f);
	}

	public SDColor(float x, float y, float z, float w) {
		super(x, y, z, w);
	}

	public float getRed() {
		return values[0];
	}

	public SDColor setRed(float red) {
		values[0] = red;
		return this;
	}

	public float getGreen() {
		return values[1];
	}

	public SDColor setGreen(float green) {
		values[1] = green;
		return this;
	}

	public float getBlue() {
		return values[2];
	}

	public SDColor setBlue(float blue) {
		values[2] = blue;
		return this;
	}

	public float getAlpha() {
		return values[3];
	}

	public SDColor setAlpha(float alpha) {
		values[3] = alpha;
		return this;
	}

	public SDColor lighten(float r, float g, float b, float a) {
		add(new SDColor(r, g, b, a));
		return this;
	}

	public SDColor clampToSDRRange() {
		values[0] = SDMath.clamp(values[0], 0, 1);
		values[1] = SDMath.clamp(values[1], 0, 1);
		values[2] = SDMath.clamp(values[2], 0, 1);
		values[3] = SDMath.clamp(values[3], 0, 1);
		return this;
	}

	public SDColor darken(float r, float g, float b, float a) {
		subtract(new SDColor(r, g, b, a));
		setRed(Math.max(0, getRed()));
		setGreen(Math.max(0, getGreen()));
		setBlue(Math.max(0, getBlue()));
		setAlpha(Math.max(0, getAlpha()));
		return this;
	}

	public SDColor desaturate(float percentage) {
		float luminance = 0.3f * getRed() + 0.6f * getGreen() + 0.1f * getBlue();
		setRed(getRed() + percentage * (luminance - getRed()));
		setGreen(getGreen() + percentage * (luminance - getGreen()));
		setBlue(getBlue() + percentage * (luminance - getBlue()));
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
	public SDColor fromEightBitToFloat() {
		return new SDColor(getRed() / 255.0f, getGreen() / 255.0f, getBlue() / 255.0f, getAlpha() / 255.0f);

	}

	/**
	 * Converts color from range 0.0f - 1.0f to the eight bit range 0 - 255.
	 * 
	 * @return A new instance of the color in eight bit format.
	 */
	public SDColor fromFloatToEightBit() {
		return new SDColor(getRed() * 255.0f, getGreen() * 255.0f, getBlue() * 255.0f, getAlpha() * 255.0f);

	}

	public static SDColor fromEncodedInteger(int input) {
		return new SDColor((input >> 16 & 0xFF), (input >> 8 & 0xFF), (input & 0xFF), (input >> 24 & 0xFF));
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
	public SDColor copy() {
		return new SDColor(values[0], values[1], values[2], values[3]);
	}

	public Vector3f toVector3f() {
		return new Vector3f(getRed(), getGreen(), getBlue());
	}

	public void toBuffer(FriendlyByteBuf buff) {
		buff.writeFloat(getRed());
		buff.writeFloat(getGreen());
		buff.writeFloat(getBlue());
		buff.writeFloat(getAlpha());
	}

	public static SDColor fromJson(JsonObject object) {
		return new SDColor(object.get("r").getAsFloat(), object.get("g").getAsFloat(), object.get("b").getAsFloat(), object.get("a").getAsFloat());
	}

	public static SDColor fromBuffer(FriendlyByteBuf buff) {
		return new SDColor(buff.readFloat(), buff.readFloat(), buff.readFloat(), buff.readFloat());
	}

	public static SDColor lerp(SDColor first, SDColor second, float alpha) {
		alpha = SDMath.clamp(alpha, 0, 1);
		float inverseAlpha = 1.0f - alpha;
		return new SDColor(first.getRed() * inverseAlpha + second.getRed() * alpha, first.getGreen() * inverseAlpha + second.getGreen() * alpha,
				first.getBlue() * inverseAlpha + second.getBlue() * alpha);
	}
}
