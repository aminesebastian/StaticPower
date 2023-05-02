package theking530.staticcore.productivity.network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;
import theking530.staticcore.StaticCore;
import theking530.staticcore.network.NetworkMessage;
import theking530.staticcore.network.StaticCoreMessageHandler;
import theking530.staticcore.productivity.ServerProductionCache;
import theking530.staticcore.productivity.metrics.MetricPeriod;
import theking530.staticcore.productivity.metrics.ProductivityTimeline;
import theking530.staticcore.teams.ITeam;
import theking530.staticcore.teams.TeamManager;

public class PacketRequestProductionTimeline extends NetworkMessage {

	private long startTime;
	private long endTime;
	private Collection<ProductivityTimelineRequest> requests;
	private MetricPeriod period;

	public PacketRequestProductionTimeline() {

	}

	public PacketRequestProductionTimeline(long startTime, long endTime,
			Collection<ProductivityTimelineRequest> requests, MetricPeriod period) {
		this.startTime = startTime;
		this.endTime = endTime;
		this.requests = requests;
		this.period = period;
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeLong(startTime);
		buffer.writeLong(endTime);
		buffer.writeByte(period.ordinal());

		buffer.writeByte(requests.size());
		for (ProductivityTimelineRequest request : requests) {
			request.encode(buffer);
		}
	}

	@Override
	public void decode(FriendlyByteBuf buffer) {
		startTime = buffer.readLong();
		endTime = buffer.readLong();
		period = MetricPeriod.values()[buffer.readByte()];

		int count = buffer.readByte();
		requests = new ArrayList<>(count);
		for (int i = 0; i < count; i++) {
			requests.add(ProductivityTimelineRequest.decode(buffer));
		}
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer serverPlayer = ctx.get().getSender();
			ITeam team = TeamManager.get(serverPlayer.level).getTeamForPlayer(serverPlayer);
			if (team == null) {
				StaticCore.LOGGER.error(String.format(
						"Recieved request for production timeline for player: %1$s that does not belong to any team!",
						serverPlayer.getName().getString()));
				return;
			}

			List<ProductivityTimeline> timelines = new LinkedList<ProductivityTimeline>();
			for (ProductivityTimelineRequest request : requests) {
				ServerProductionCache<?> cache = (ServerProductionCache<?>) team.getProductionManager()
						.getProductCache(request.productType());
				ProductivityTimeline timeline = cache.getProductivityTimeline(request.type(), period, request.product(),
						startTime, endTime);
				if (!timeline.getEntries().isEmpty()) {
					timelines.add(timeline);
				}
			}

			PacketRecieveProductionTimeline response = new PacketRecieveProductionTimeline(startTime, endTime, period,
					timelines);
			StaticCoreMessageHandler.sendMessageToPlayer(StaticCoreMessageHandler.MAIN_PACKET_CHANNEL,
					(ServerPlayer) ctx.get().getSender(), response);
		});
	}
}
