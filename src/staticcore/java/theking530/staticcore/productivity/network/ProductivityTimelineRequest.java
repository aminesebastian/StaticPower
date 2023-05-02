package theking530.staticcore.productivity.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import theking530.staticcore.StaticCoreRegistries;
import theking530.staticcore.productivity.metrics.MetricType;
import theking530.staticcore.productivity.product.ProductType;

public record ProductivityTimelineRequest(ProductType<?> productType, int product, MetricType type) {

	public void encode(FriendlyByteBuf buffer) {
		buffer.writeUtf(StaticCoreRegistries.ProductRegistry().getKey(productType).toString());
		buffer.writeInt(product);
		buffer.writeByte(type.ordinal());

	}

	public static ProductivityTimelineRequest decode(FriendlyByteBuf buffer) {
		return new ProductivityTimelineRequest(
				StaticCoreRegistries.ProductRegistry().getValue(new ResourceLocation(buffer.readUtf())),
				buffer.readInt(), MetricType.values()[buffer.readByte()]);
	}
}