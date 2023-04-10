package theking530.staticcore.productivity.metrics;

import theking530.staticcore.productivity.cacheentry.ProductivityRate;

public class ClientProductionMetric extends ProductionMetric {
	private final ProductivityRate smoothedConsumed;
	private final ProductivityRate smoothedProduced;

	public ClientProductionMetric(int productHash, String serializedProduct, ProductivityRate consumed,
			ProductivityRate produced, ProductivityRate smoothedConsumed, ProductivityRate smoothedProduced) {
		super(productHash, serializedProduct, consumed, produced);
		this.smoothedConsumed = smoothedConsumed;
		this.smoothedProduced = smoothedProduced;
	}

	public ClientProductionMetric(ProductionMetric metric) {
		this(metric.getProductHash(), metric.getSerializedProduct(), metric.getConsumed(), metric.getProduced(),
				metric.getConsumed(), metric.getProduced());
	}

	public ProductivityRate getSmoothedConsumed() {
		return smoothedConsumed;
	}

	public ProductivityRate getSmoothedProduced() {
		return smoothedProduced;
	}

	public ProductivityRate getSmoothedMetricValue(MetricType type) {
		if (type == MetricType.PRODUCTION) {
			return smoothedProduced;
		}
		return smoothedConsumed;
	}
}
