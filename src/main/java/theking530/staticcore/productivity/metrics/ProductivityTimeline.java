package theking530.staticcore.productivity.metrics;

import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import theking530.staticcore.productivity.metrics.ProductivityTimeline.ProductivityTimelineEntry;
import theking530.staticcore.productivity.product.ProductType;
import theking530.staticpower.StaticPowerRegistries;

public record ProductivityTimeline(ProductType<?> productType, String serializedProduct, MetricPeriod period, ImmutableList<ProductivityTimelineEntry> entries) {

	public void encode(FriendlyByteBuf buffer) {
		buffer.writeUtf(StaticPowerRegistries.ProductRegistry().getKey(productType).toString());
		buffer.writeUtf(serializedProduct);
		buffer.writeByte(period.ordinal());
		buffer.writeInt(entries.size());
		for (ProductivityTimelineEntry metric : entries) {
			metric.encode(buffer);
		}
	}

	public static ProductivityTimeline decode(FriendlyByteBuf buffer) {
		ProductType<?> productType = StaticPowerRegistries.ProductRegistry().getValue(new ResourceLocation(buffer.readUtf()));
		String serializedProduct = buffer.readUtf();
		MetricPeriod period = MetricPeriod.values()[buffer.readByte()];

		int count = buffer.readInt();
		List<ProductivityTimelineEntry> entries = new LinkedList<>();
		for (int i = 0; i < count; i++) {
			entries.add(ProductivityTimelineEntry.decode(buffer));
		}

		return new ProductivityTimeline(productType, serializedProduct, period, ImmutableList.copyOf(entries));
	}
	public record ProductivityTimelineEntry(float produced, float consumed, long tick) {
		public void encode(FriendlyByteBuf buffer) {
			buffer.writeFloat(produced);
			buffer.writeFloat(consumed);
			buffer.writeLong(tick);
		}

		public static ProductivityTimelineEntry decode(FriendlyByteBuf buffer) {
			return new ProductivityTimelineEntry(buffer.readFloat(), buffer.readFloat(), buffer.readLong());
		}
	}
}
