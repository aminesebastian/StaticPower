package theking530.staticcore.productivity.metrics;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent.Context;
import theking530.staticcore.StaticCoreRegistries;
import theking530.staticcore.network.NetworkMessage;
import theking530.staticcore.productivity.client.GuiProductionMenu;
import theking530.staticcore.productivity.product.ProductType;

public class PacketRecieveProductionTimeline extends NetworkMessage {
	private long requestedAtTime;
	private ProductType<?> productType;
	private MetricPeriod period;
	private MetricType type;
	private List<ProductivityTimeline> timelines;

	public PacketRecieveProductionTimeline() {

	}

	public PacketRecieveProductionTimeline(long requestedAtTime, ProductType<?> productType, MetricPeriod period,
			MetricType type, List<ProductivityTimeline> timelines) {
		this.requestedAtTime = requestedAtTime;
		this.productType = productType;
		this.period = period;
		this.type = type;
		this.timelines = timelines;
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeLong(requestedAtTime);
		buffer.writeByte(timelines.size());
		for (ProductivityTimeline timeline : timelines) {
			timeline.encode(buffer);
		}

		buffer.writeUtf(StaticCoreRegistries.ProductRegistry().getKey(productType).toString());
		buffer.writeByte(period.ordinal());
		buffer.writeByte(type.ordinal());
	}

	@Override
	public void decode(FriendlyByteBuf buffer) {
		requestedAtTime = buffer.readLong();
		timelines = new LinkedList<ProductivityTimeline>();
		int count = buffer.readByte();
		for (int i = 0; i < count; i++) {
			timelines.add(ProductivityTimeline.decode(buffer));
		}

		productType = StaticCoreRegistries.ProductRegistry().getValue(new ResourceLocation(buffer.readUtf()));
		period = MetricPeriod.values()[buffer.readByte()];
		type = MetricType.values()[buffer.readByte()];
	}

	@SuppressWarnings("resource")
	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			GuiProductionMenu productionMenu = (GuiProductionMenu) Minecraft.getInstance().screen;
			if (productionMenu != null) {
				productionMenu.recieveTimelineData(requestedAtTime, productType, period, type, timelines);
			}
		});
	}
}
