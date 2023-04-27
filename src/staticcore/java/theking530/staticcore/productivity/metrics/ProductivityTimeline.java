package theking530.staticcore.productivity.metrics;

import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import theking530.staticcore.StaticCoreRegistries;
import theking530.staticcore.productivity.product.ProductType;

public record ProductivityTimeline(long requestAtTime, ProductType<?> productType, String serializedProduct,
		MetricType metricType, MetricPeriod period, ImmutableList<ProductivityTimelineEntry> entries) {

	public void encode(FriendlyByteBuf buffer) {
		buffer.writeLong(requestAtTime);
		buffer.writeUtf(StaticCoreRegistries.ProductRegistry().getKey(productType).toString());
		buffer.writeUtf(serializedProduct);
		buffer.writeByte(metricType.ordinal());
		buffer.writeByte(period.ordinal());
		buffer.writeInt(entries.size());
		for (ProductivityTimelineEntry metric : entries) {
			metric.encode(buffer);
		}
	}

	public static ProductivityTimeline decode(FriendlyByteBuf buffer) {
		long requestAtTime = buffer.readLong();
		ProductType<?> productType = StaticCoreRegistries.ProductRegistry()
				.getValue(new ResourceLocation(buffer.readUtf()));
		String serializedProduct = buffer.readUtf();
		MetricType metricType = MetricType.values()[buffer.readByte()];
		MetricPeriod period = MetricPeriod.values()[buffer.readByte()];

		int count = buffer.readInt();
		List<ProductivityTimelineEntry> entries = new LinkedList<>();
		for (int i = 0; i < count; i++) {
			entries.add(ProductivityTimelineEntry.decode(buffer));
		}

		return new ProductivityTimeline(requestAtTime, productType, serializedProduct, metricType, period,
				ImmutableList.copyOf(entries));
	}

	@Override
	public String toString() {
		return "ProductivityTimeline [requestAtTime=" + requestAtTime + ", productType=" + productType
				+ ", serializedProduct=" + serializedProduct + ", metricType=" + metricType + ", period=" + period
				+ ", entries=" + entries + "]";
	}

	public record ProductivityTimelineEntry(float value, long tick) {
		public void encode(FriendlyByteBuf buffer) {
			buffer.writeFloat(value);
			buffer.writeLong(tick);
		}

		public static ProductivityTimelineEntry decode(FriendlyByteBuf buffer) {
			return new ProductivityTimelineEntry(buffer.readFloat(), buffer.readLong());
		}
	}
}
