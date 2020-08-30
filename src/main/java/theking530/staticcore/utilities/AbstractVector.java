package theking530.staticcore.utilities;

import java.util.ArrayList;
import java.util.List;

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

	public <T extends AbstractVector> T add(AbstractVector other) {
		for (int i = 0; i < Math.min(other.getDimensions(), getDimensions()); i++) {
			values.set(i, other.getScalar(i) + values.get(i));
		}
		return (T) this;
	}

	@Override
	public String toString() {
		return toStringInternal(false);
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
