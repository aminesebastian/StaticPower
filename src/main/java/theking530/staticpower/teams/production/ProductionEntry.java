package theking530.staticpower.teams.production;

public abstract class ProductionEntry<T> {
	public record Metric(float input, float output, int period) {
	}

	public enum MetricPeriod {
		SECOND("second"), MINUTE("minute"), HOUR("hour"), DAY("day");

		private final String tableKey;

		MetricPeriod(String tableKey) {
			this.tableKey = tableKey;
		}

		public String getTableKey() {
			return tableKey;
		}

	}

	protected T product;
	protected int currentSecondInput;
	protected int currentSecondOutput;

	public ProductionEntry(T product) {
		this();
		this.product = product;
	}

	protected ProductionEntry() {
		this.currentSecondInput = 0;
		this.currentSecondOutput = 0;
	}

	public void reset() {
		currentSecondInput = 0;
		currentSecondOutput = 0;
	}

	public T getProduct() {
		return product;
	}

	public int getCurrentSecondInput() {
		return currentSecondInput;
	}

	public int getCurrentSecondOutput() {
		return currentSecondOutput;
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
