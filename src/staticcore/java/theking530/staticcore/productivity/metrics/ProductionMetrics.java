package theking530.staticcore.productivity.metrics;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.network.FriendlyByteBuf;

public class ProductionMetrics {
	public static final ProductionMetrics EMPTY = new ProductionMetrics(Collections.emptyMap());
	private final Map<Integer, ProductionMetric> metrics;

	public ProductionMetrics(Map<Integer, ProductionMetric> metrics) {
		this.metrics = metrics;
	}

	public Map<Integer, ProductionMetric> getMetrics() {
		return metrics;
	}

	public boolean isEmpty() {
		return metrics.size() == 0;
	}

	public void encode(FriendlyByteBuf buffer) {
		buffer.writeInt(metrics.size());
		for (ProductionMetric metric : metrics.values()) {
			buffer.writeNbt(metric.serialize());
		}
	}

	public static ProductionMetrics decode(FriendlyByteBuf buffer) {
		Map<Integer, ProductionMetric> metrics = new HashMap<>();
		int count = buffer.readInt();
		for (int i = 0; i < count; i++) {
			ProductionMetric metric = ProductionMetric.deserialize(buffer.readNbt());
			metrics.put(metric.getProductHash(), metric);
		}

		return new ProductionMetrics(metrics);
	}
}
