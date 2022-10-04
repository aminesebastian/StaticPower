package theking530.staticpower.teams.production.metrics;

public class ProductMetric<T> extends Metric {
	private final T product;

	public ProductMetric(T product, float input, float output, MetricPeriod period) {
		super(input, output, period);
		this.product = product;
	}

	public T getProduct() {
		return product;
	}
}
