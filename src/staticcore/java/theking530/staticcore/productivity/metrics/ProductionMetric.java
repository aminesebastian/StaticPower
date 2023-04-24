package theking530.staticcore.productivity.metrics;

import net.minecraft.nbt.CompoundTag;
import theking530.staticcore.productivity.cacheentry.ProductivityRate;

public class ProductionMetric {
	private final int productHash;
	private final String serializedProduct;
	protected final ProductivityRate consumed;
	protected final ProductivityRate produced;

	private final ProductivityRate smoothedConsumed;
	private final ProductivityRate smoothedProduced;

	public ProductionMetric(int productHash, String serializedProduct, ProductivityRate consumed,
			ProductivityRate produced) {
		this.productHash = productHash;
		this.serializedProduct = serializedProduct;
		this.consumed = consumed;
		this.produced = produced;
		this.smoothedConsumed = consumed;
		this.smoothedProduced = produced;
	}

	public String getSerializedProduct() {
		return serializedProduct;
	}

	public ProductivityRate getConsumed() {
		return consumed;
	}

	public ProductivityRate getProduced() {
		return produced;
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

	public void interpolateTowards(ProductionMetric nextValue, double smoothingFactor) {
		getProduced().interpolateTowards(nextValue.getProduced().getCurrentValue(),
				nextValue.getProduced().getIdealValue(), smoothingFactor);

		getConsumed().interpolateTowards(nextValue.getConsumed().getCurrentValue(),
				nextValue.getConsumed().getIdealValue(), smoothingFactor);
	}

	public int getProductHash() {
		return productHash;
	}

	public ProductivityRate getMetricValue(MetricType type) {
		if (type == MetricType.PRODUCTION) {
			return produced;
		}
		return consumed;
	}

	public static ProductionMetric deserialize(CompoundTag tag) {
		return new ProductionMetric(tag.getInt("h"), tag.getString("s"),
				ProductivityRate.deserialize(tag.getCompound("c")), ProductivityRate.deserialize(tag.getCompound("p")));
	}

	public CompoundTag serialize() {
		CompoundTag tag = new CompoundTag();
		tag.putInt("h", productHash);
		tag.putString("s", serializedProduct);
		tag.put("c", consumed.serialize());
		tag.put("p", produced.serialize());
		return tag;
	}

	public String toString() {
		return "SerializedProductionMetric [serializedProduct=" + serializedProduct + ", consumption()=" + consumed
				+ ", production()=" + produced + "]";
	}
}
