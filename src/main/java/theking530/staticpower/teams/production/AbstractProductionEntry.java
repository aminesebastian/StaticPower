package theking530.staticpower.teams.production;

import theking530.staticpower.teams.production.metrics.Metric;
import theking530.staticpower.teams.production.metrics.MetricPeriod;

public abstract class AbstractProductionEntry<T> {
	protected T product;
	protected int currentSecondInput;
	protected int currentSecondOutput;

	public AbstractProductionEntry(T product) {
		this();
		this.product = product;
	}

	private AbstractProductionEntry() {
		this.currentSecondInput = 0;
		this.currentSecondOutput = 0;
	}

	public Metric captureCurrentSecondMetric() {
		return new Metric(currentSecondInput, currentSecondOutput, MetricPeriod.SECOND);
	}

	public void clearCurrentSecondMetrics() {
		currentSecondInput = 0;
		currentSecondOutput = 0;
	}

	public T getProduct() {
		return product;
	}

	public void inserted(int amount) {
		currentSecondInput += Math.max(0, amount);
	}

	public void extracted(int amount) {
		currentSecondOutput += Math.max(0, amount);
	}

	public abstract int getProductHashCode();

	public abstract String getSerializedProduct();

	@Override
	public String toString() {
		return "ProductionEntry [product=" + product + ", inserted=" + currentSecondInput + ", extracted=" + currentSecondOutput + "]";
	}
}
