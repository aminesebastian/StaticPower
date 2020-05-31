package theking530.api.utilities;

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

	public int getDimensions() {
		return values.size();
	}
}
