package theking530.staticcore.utilities.math;

import net.minecraft.network.FriendlyByteBuf;

@SuppressWarnings({ "unchecked", "rawtypes" })
public abstract class AbstractVectorHP<T extends AbstractVectorHP> implements Cloneable {
	protected final static char[] PREFIXES = { 'X', 'Y', 'Z', 'W' };
	protected final static char[] COLOR_PREFIXES = { 'R', 'G', 'B', 'A' };
	protected final double[] values;

	public AbstractVectorHP(int size) {
		values = new double[size];
		for (int i = 0; i < size; i++) {
			values[i] = 0.0f;
		}
	}

	public AbstractVectorHP(double... initialValues) {
		values = new double[initialValues.length];
		for (int i = 0; i < initialValues.length; i++) {
			values[i] = initialValues[i];
		}
	}

	public abstract T copy();

	public double getScalar(int index) {
		return values[index];
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
		double length = getLength();
		for (int i = 0; i < values.length; i++) {
			values[i] /= length;
		}
		return (T) this;
	}

	public double getLength() {
		double sum = 0;
		for (Double val : values) {
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
			output = (int) (31 * output + Double.doubleToLongBits(values[i]));
		}
		return output;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AbstractVectorHP) {
			AbstractVectorHP other = (AbstractVectorHP) obj;
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
		for (double val : values) {
			buff.writeDouble(val);
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
