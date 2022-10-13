package theking530.staticcore.productivity.metrics;

import net.minecraft.nbt.CompoundTag;

public class ProductionMetric {
	private final int productHash;
	private final String serializedProduct;
	private final double consumed;
	private final double produced;

	public ProductionMetric(int productHash, String serializedProduct, double consumed, double produced) {
		this.productHash = productHash;
		this.consumed = consumed;
		this.produced = produced;
		this.serializedProduct = serializedProduct;
	}

	public String getSerializedProduct() {
		return serializedProduct;
	}

	public double getConsumed() {
		return consumed;
	}

	public double getProduced() {
		return produced;
	}

	public int getProductHash() {
		return productHash;
	}

	public double getMetric(MetricType type) {
		if (type == MetricType.PRODUCTION) {
			return produced;
		}
		return consumed;
	}

	public static ProductionMetric deserialize(CompoundTag tag) {
		return new ProductionMetric(tag.getInt("h"), tag.getString("s"), tag.getDouble("c"), tag.getDouble("p"));
	}

	public CompoundTag serialize() {
		CompoundTag tag = new CompoundTag();
		tag.putInt("i", productHash);
		tag.putDouble("c", consumed);
		tag.putDouble("p", produced);
		tag.putString("s", serializedProduct);
		return tag;
	}

	public String toString() {
		return "SerializedProductionMetric [serializedProduct=" + serializedProduct + ", consumption()=" + consumed + ", production()=" + produced + "]";
	}
}
