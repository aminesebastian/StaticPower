package theking530.staticcore.productivity.metrics;

import net.minecraft.nbt.CompoundTag;
import theking530.staticcore.productivity.cacheentry.ProductivityRate;

public class ProductionMetric {
	private final int productHash;
	private final String serializedProduct;
	private final ProductivityRate consumed;
	private final ProductivityRate produced;

	public ProductionMetric(int productHash, String serializedProduct, ProductivityRate consumed, ProductivityRate produced) {
		this.productHash = productHash;
		this.consumed = consumed;
		this.produced = produced;
		this.serializedProduct = serializedProduct;
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
		return new ProductionMetric(tag.getInt("h"), tag.getString("s"), ProductivityRate.deserialize(tag.getCompound("c")), ProductivityRate.deserialize(tag.getCompound("p")));
	}

	public CompoundTag serialize() {
		CompoundTag tag = new CompoundTag();
		tag.putInt("i", productHash);
		tag.put("c", consumed.serialize());
		tag.put("p", produced.serialize());
		tag.putString("s", serializedProduct);
		return tag;
	}

	public String toString() {
		return "SerializedProductionMetric [serializedProduct=" + serializedProduct + ", consumption()=" + consumed + ", production()=" + produced + "]";
	}
}
