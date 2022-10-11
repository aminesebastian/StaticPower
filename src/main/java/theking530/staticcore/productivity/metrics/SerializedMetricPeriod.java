package theking530.staticcore.productivity.metrics;

import net.minecraft.nbt.CompoundTag;

public class SerializedMetricPeriod extends Metric {
	private final String serializedProduct;

	public SerializedMetricPeriod(String serializedProduct, double consumption, double production, MetricPeriod period) {
		super(consumption, production, period);
		this.serializedProduct = serializedProduct;
	}

	public String getSerializedProduct() {
		return serializedProduct;
	}

	public static SerializedMetricPeriod deserialize(CompoundTag tag) {
		return new SerializedMetricPeriod(tag.getString("s"), tag.getDouble("c"), tag.getDouble("p"), MetricPeriod.values()[tag.getByte("t")]);
	}

	@Override
	public CompoundTag serialize() {
		CompoundTag tag = super.serialize();
		tag.putString("s", serializedProduct);
		return tag;
	}

	@Override
	public String toString() {
		return "SerializedMetricPeriod [serializedProduct=" + serializedProduct + ", consumption()=" + getConsumption() + ", production()=" + getProduction() + ", period()="
				+ getPeriod() + "]";
	}
}
