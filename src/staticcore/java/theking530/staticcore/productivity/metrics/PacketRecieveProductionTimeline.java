package theking530.staticcore.productivity.metrics;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;
import theking530.staticcore.network.NetworkMessage;
import theking530.staticcore.productivity.client.GuiProductionMenu;

public class PacketRecieveProductionTimeline extends NetworkMessage {
	private long requestedAtTime;
	private MetricPeriod period;
	private List<ProductivityTimeline> timelines;

	public PacketRecieveProductionTimeline() {

	}

	public PacketRecieveProductionTimeline(long requestedAtTime, MetricPeriod period,
			List<ProductivityTimeline> timelines) {
		this.requestedAtTime = requestedAtTime;
		this.period = period;
		this.timelines = timelines;
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeLong(requestedAtTime);
		buffer.writeByte(period.ordinal());
		buffer.writeByte(timelines.size());
		
		for (ProductivityTimeline timeline : timelines) {
			timeline.encode(buffer);
		}
	}

	@Override
	public void decode(FriendlyByteBuf buffer) {
		requestedAtTime = buffer.readLong();
		period = MetricPeriod.values()[buffer.readByte()];
		
		int count = buffer.readByte();
		
		timelines = new LinkedList<ProductivityTimeline>();
		for (int i = 0; i < count; i++) {
			timelines.add(ProductivityTimeline.decode(buffer));
		}
	}

	@SuppressWarnings("resource")
	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			GuiProductionMenu productionMenu = (GuiProductionMenu) Minecraft.getInstance().screen;
			if (productionMenu != null) {
				productionMenu.recieveTimelineData(requestedAtTime, period, timelines);
			}
		});
	}
}
