package theking530.staticpower.teams.production.metrics;

import net.minecraft.nbt.CompoundTag;

public class SerializedMetricPeriod extends Metric {
	private final String serializedProduct;

	public SerializedMetricPeriod(String serializedProduct, float input, float output, MetricPeriod period) {
		super(input, output, period);
		this.serializedProduct = serializedProduct;
	}

	public String getSerializedProduct() {
		return serializedProduct;
	}

	public static SerializedMetricPeriod deserialize(CompoundTag tag) {
		return new SerializedMetricPeriod(tag.getString("s"), tag.getFloat("i"), tag.getFloat("o"), MetricPeriod.values()[tag.getByte("p")]);
	}

	@Override
	public CompoundTag serialize() {
		CompoundTag tag = super.serialize();
		tag.putString("s", serializedProduct);
		return tag;
	}

	@Override
	public String toString() {
		return "SerializedMetricPeriod [serializedProduct=" + serializedProduct + ", input=" + getInput() + ", output=" + getOutput() + ", period=" + getPeriod() + "]";
	}
}
