package theking530.staticcore.utilities;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.FriendlyByteBuf;

@SuppressWarnings("unchecked")
public abstract class AbstractVector implements Cloneable {
	protected final static char[] PREFIXES = { 'X', 'Y', 'Z', 'W' };
	protected final static char[] COLOR_PREFIXES = { 'R', 'G', 'B', 'A' };
	protected final List<Float> values;

	public AbstractVector(int size) {
		values = new ArrayList<Float>();
		for (int i = 0; i < size; i++) {
			values.add(0.0f);
		}
	}

	public AbstractVector(float... initialValues) {
		values = new ArrayList<Float>();
		for (int i = 0; i < initialValues.length; i++) {
			values.add(initialValues[i]);
		}
	}

	public abstract <T extends AbstractVector> T copy();

	public float getScalar(int index) {
		return values.get(index);
	}

	public int getDimensions() {
		return values.size();
	}

	public <T extends AbstractVector> T multiply(float multiplier) {
		for (int i = 0; i < values.size(); i++) {
			values.set(i, values.get(i) * multiplier);
		}
		return (T) this;
	}

	public <T extends AbstractVector> T divide(float divisor) {
		for (int i = 0; i < values.size(); i++) {
			values.set(i, values.get(i) / divisor);
		}
		return (T) this;
	}

	public <T extends AbstractVector> T divide(AbstractVector other) {
		for (int i = 0; i < Math.min(other.getDimensions(), getDimensions()); i++) {
			values.set(i, values.get(i) / other.values.get(i));
		}
		return (T) this;
	}

	public <T extends AbstractVector> T add(AbstractVector other) {
		for (int i = 0; i < Math.min(other.getDimensions(), getDimensions()); i++) {
			values.set(i, values.get(i) + other.values.get(i));
		}
		return (T) this;
	}

	public <T extends AbstractVector> T subtract(AbstractVector other) {
		for (int i = 0; i < Math.min(other.getDimensions(), getDimensions()); i++) {
			values.set(i, values.get(i) - other.values.get(i));
		}
		return (T) this;
	}

	public <T extends AbstractVector> T normalize() {
		float length = getLength();
		for (int i = 0; i < values.size(); i++) {
			values.set(i, values.get(i) / length);
		}
		return (T) this;
	}

	public float getLength() {
		float sum = 0;
		for (Float val : values) {
			sum += val * val;
		}
		return (float) Math.pow(sum, (1.0f / values.size()));
	}

	public float dot(AbstractVector other) {
		if (this.values.size() != other.values.size()) {
			throw new RuntimeException("Dot product can only be calculated between two vectors of the same degree!");
		} else {
			float output = 0;
			for (int i = 0; i < values.size(); i++) {
				output += (values.get(i) * other.values.get(i));
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
		for (int i = 0; i < values.size(); i++) {
			output = 31 * output + Float.floatToIntBits(values.get(i));
		}
		return output;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AbstractVector) {
			AbstractVector other = (AbstractVector) obj;
			if (other.values.size() != values.size()) {
				return false;
			}

			for (int i = 0; i < other.values.size(); i++) {
				if (other.values.get(i) != values.get(i)) {
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
		for (int i = 0; i < values.size(); i++) {
			output += (useColor ? COLOR_PREFIXES[i] : PREFIXES[i]) + ":" + values.get(i);
			output += " ";
		}
		output = output.substring(0, output.length() - 1);
		output += "]";
		return output;
	}
}
