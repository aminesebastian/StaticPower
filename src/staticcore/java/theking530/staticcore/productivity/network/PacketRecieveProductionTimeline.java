package theking530.staticcore.productivity.network;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;
import theking530.staticcore.network.NetworkMessage;
import theking530.staticcore.productivity.client.ClientProductionCache;
import theking530.staticcore.productivity.client.ClientProductionManager;
import theking530.staticcore.productivity.metrics.MetricPeriod;
import theking530.staticcore.productivity.metrics.ProductivityTimeline;
import theking530.staticcore.teams.TeamManager;

public class PacketRecieveProductionTimeline extends NetworkMessage {
	private long startTime;
	private long endTime;
	private MetricPeriod period;
	private List<ProductivityTimeline> timelines;

	public PacketRecieveProductionTimeline() {

	}

	public PacketRecieveProductionTimeline(long startTime, long endTime, MetricPeriod period,
			List<ProductivityTimeline> timelines) {
		this.startTime = startTime;
		this.endTime = endTime;
		this.period = period;
		this.timelines = timelines;
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeLong(startTime);
		buffer.writeLong(endTime);
		buffer.writeByte(period.ordinal());
		buffer.writeByte(timelines.size());

		for (ProductivityTimeline timeline : timelines) {
			timeline.encode(buffer);
		}
	}

	@Override
	public void decode(FriendlyByteBuf buffer) {
		startTime = buffer.readLong();
		endTime = buffer.readLong();
		period = MetricPeriod.values()[buffer.readByte()];

		int count = buffer.readByte();

		timelines = new LinkedList<ProductivityTimeline>();
		for (int i = 0; i < count; i++) {
			timelines.add(ProductivityTimeline.decode(buffer));
		}
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ClientProductionManager clientManager = (ClientProductionManager) TeamManager.getLocalTeam()
					.getProductionManager();

			for (ProductivityTimeline timeline : timelines) {
				ClientProductionCache<?> clientCache = (ClientProductionCache<?>) clientManager
						.getProductCache(timeline.getProductType());
				clientCache.recieveProductivityTimelines(period, timeline, startTime, endTime);
			}
		});
	}
}
