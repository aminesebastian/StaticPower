package theking530.staticcore.utilities;

import net.minecraft.network.FriendlyByteBuf;

@SuppressWarnings({ "unchecked", "rawtypes" })
public abstract class AbstractVector<T extends AbstractVector> implements Cloneable {
	protected final static char[] PREFIXES = { 'X', 'Y', 'Z', 'W' };
	protected final static char[] COLOR_PREFIXES = { 'R', 'G', 'B', 'A' };
	protected final float[] values;

	public AbstractVector(int size) {
		values = new float[size];
		for (int i = 0; i < size; i++) {
			values[i] = 0.0f;
		}
	}

	public AbstractVector(float... initialValues) {
		values = new float[initialValues.length];
		for (int i = 0; i < initialValues.length; i++) {
			values[i] = initialValues[i];
		}
	}

	public abstract T copy();

	public float getScalar(int index) {
		return values[index];
	}

	public float setScalar(int index, float value) {
		return values[index] = value;
	}

	public int getDimensions() {
		return values.length;
	}

	public T multiply(float multiplier) {
		for (int i = 0; i < values.length; i++) {
			values[i] *= multiplier;
		}
		return (T) this;
	}

	public T divide(float divisor) {
		for (int i = 0; i < values.length; i++) {
			values[i] /= divisor;
		}
		return (T) this;
	}

	public T divide(T other) {
		for (int i = 0; i < Math.min(other.getDimensions(), getDimensions()); i++) {
			values[i] /= other.values[i];
		}
		return (T) this;
	}

	public T add(T other) {
		for (int i = 0; i < Math.min(other.getDimensions(), getDimensions()); i++) {
			values[i] += other.values[i];
		}
		return (T) this;
	}

	public T add(float... values) {
		for (int i = 0; i < Math.min(values.length, getDimensions()); i++) {
			this.values[i] += values[i];
		}
		return (T) this;
	}

	public T subtract(T other) {
		for (int i = 0; i < Math.min(other.getDimensions(), getDimensions()); i++) {
			values[i] -= other.values[i];
		}
		return (T) this;
	}

	public T normalize() {
		float length = getLength();
		for (int i = 0; i < values.length; i++) {
			values[i] /= length;
		}
		return (T) this;
	}

	public T negate() {
		for (int i = 0; i < values.length; i++) {
			values[i] /= -1;
		}
		return (T) this;
	}

	public float getLength() {
		float sum = 0;
		for (Float val : values) {
			sum += val * val;
		}
		return (float) Math.pow(sum, (1.0f / values.length));
	}

	public float dot(T other) {
		if (this.values.length != other.values.length) {
			throw new RuntimeException("Dot product can only be calculated between two vectors of the same degree!");
		} else {
			float output = 0;
			for (int i = 0; i < values.length; i++) {
				output += (values[i] * other.values[i]);
			}
			return output;
		}
	}

	@Override
	public String toString() {
		return toStringInternal(false);
	}

	@Override
	public int hashCode() {
		int output = 0;
		for (int i = 0; i < values.length; i++) {
			output = 31 * output + Float.floatToIntBits(values[i]);
		}
		return output;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AbstractVector) {
			AbstractVector other = (AbstractVector) obj;
			if (other.values.length != values.length) {
				return false;
			}

			for (int i = 0; i < other.values.length; i++) {
				if (other.values[i] != values[i]) {
					return false;
				}
			}

			return true;
		}
		return false;
	}

	public void toBuffer(FriendlyByteBuf buff) {
		for (float val : values) {
			buff.writeFloat(val);
		}
	}

	protected String toStringInternal(boolean useColor) {
		String output = "[";
		for (int i = 0; i < values.length; i++) {
			output += (useColor ? COLOR_PREFIXES[i] : PREFIXES[i]) + ":" + values[i];
			output += " ";
		}
		output = output.substring(0, output.length() - 1);
		output += "]";
		return output;
	}
}
