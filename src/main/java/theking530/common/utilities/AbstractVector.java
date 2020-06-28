package theking530.common.utilities;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractVector {
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

	public void multiply(float multiplier) {
		for (int i = 0; i < values.size(); i++) {
			values.set(i, values.get(i) * multiplier);
		}
	}

	public void add(AbstractVector other) {
		for (int i = 0; i < Math.min(other.getDimensions(), getDimensions()); i++) {
			values.set(i, other.getScalar(i) + values.get(i));
		}
	}
}
