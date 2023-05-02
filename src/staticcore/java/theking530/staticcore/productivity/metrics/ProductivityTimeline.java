package theking530.staticcore.productivity.metrics;

import java.util.TreeMap;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import theking530.staticcore.StaticCoreRegistries;
import theking530.staticcore.productivity.product.ProductType;

public class ProductivityTimeline {
	private long startTime;
	private long endTime;
	private int productHash;
	private ProductType<?> productType;
	private String serializedProduct;
	private MetricType metricType;
	private MetricPeriod period;

	private TreeMap<Long, ProductivityTimelineEntry> entries;

	public ProductivityTimeline(long startTime, long endTime, int productHash, ProductType<?> productType,
			String serializedProduct, MetricType metricType, MetricPeriod period,
			TreeMap<Long, ProductivityTimelineEntry> entries) {
		this.startTime = startTime;
		this.endTime = endTime;
		this.productHash = productHash;
		this.productType = productType;
		this.serializedProduct = serializedProduct;
		this.metricType = metricType;
		this.period = period;
		this.entries = entries;
	}

	public long getStartTime() {
		return startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public int getProductHash() {
		return productHash;
	}

	public ProductType<?> getProductType() {
		return productType;
	}

	public String getSerializedProduct() {
		return serializedProduct;
	}

	public MetricType getMetricType() {
		return metricType;
	}

	public MetricPeriod getPeriod() {
		return period;
	}

	public TreeMap<Long, ProductivityTimelineEntry> getEntries() {
		return entries;
	}

	public void append(ProductivityTimeline otherTimeline) {
		entries.putAll(otherTimeline.entries);
		
		// Keep the same start time but expand out the end time.
		endTime = otherTimeline.endTime;
	}

	public void encode(FriendlyByteBuf buffer) {
		buffer.writeLong(startTime);
		buffer.writeLong(endTime);
		buffer.writeInt(productHash);
		buffer.writeUtf(StaticCoreRegistries.ProductRegistry().getKey(productType).toString());
		buffer.writeUtf(serializedProduct);
		buffer.writeByte(metricType.ordinal());
		buffer.writeByte(period.ordinal());
		buffer.writeInt(entries.size());
		for (ProductivityTimelineEntry metric : entries.values()) {
			metric.encode(buffer);
		}
	}

	public static ProductivityTimeline decode(FriendlyByteBuf buffer) {
		long startTime = buffer.readLong();
		long endTime = buffer.readLong();
		int productHash = buffer.readInt();
		ProductType<?> productType = StaticCoreRegistries.ProductRegistry()
				.getValue(new ResourceLocation(buffer.readUtf()));
		String serializedProduct = buffer.readUtf();
		MetricType metricType = MetricType.values()[buffer.readByte()];
		MetricPeriod period = MetricPeriod.values()[buffer.readByte()];

		int count = buffer.readInt();
		TreeMap<Long, ProductivityTimelineEntry> entries = new TreeMap<>();
		for (int i = 0; i < count; i++) {
			ProductivityTimelineEntry entry = ProductivityTimelineEntry.decode(buffer);
			entries.put(entry.tick(), entry);
		}

		return new ProductivityTimeline(startTime, endTime, productHash, productType, serializedProduct, metricType,
				period, entries);
	}

	@Override
	public String toString() {
		return "ProductivityTimeline [startTime=" + startTime + ", endTime=" + endTime + ", productHash=" + productHash
				+ ", productType=" + productType + ", serializedProduct=" + serializedProduct + ", metricType="
				+ metricType + ", period=" + period + ", entries=" + entries + "]";
	}

	public record ProductivityTimelineEntry(double value, long tick) {
		public void encode(FriendlyByteBuf buffer) {
			buffer.writeDouble(value);
			buffer.writeLong(tick);
		}

		public static ProductivityTimelineEntry decode(FriendlyByteBuf buffer) {
			return new ProductivityTimelineEntry(buffer.readDouble(), buffer.readLong());
		}
	}
}
