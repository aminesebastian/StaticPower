package theking530.staticcore.productivity.metrics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;
import theking530.staticcore.StaticCore;
import theking530.staticcore.StaticCoreRegistries;
import theking530.staticcore.network.NetworkMessage;
import theking530.staticcore.network.StaticCoreMessageHandler;
import theking530.staticcore.productivity.ServerProductionCache;
import theking530.staticcore.productivity.product.ProductType;
import theking530.staticcore.teams.ITeam;
import theking530.staticcore.teams.TeamManager;

public class PacketRequestProductionTimeline extends NetworkMessage {
	public record TimelineRequest(ProductType<?> productType, int product, MetricType type) {

		public void encode(FriendlyByteBuf buffer) {
			buffer.writeUtf(StaticCoreRegistries.ProductRegistry().getKey(productType).toString());
			buffer.writeInt(product);
			buffer.writeByte(type.ordinal());

		}

		public static TimelineRequest decode(FriendlyByteBuf buffer) {
			return new TimelineRequest(
					StaticCoreRegistries.ProductRegistry().getValue(new ResourceLocation(buffer.readUtf())),
					buffer.readInt(), MetricType.values()[buffer.readByte()]);
		}
	}

	private long requestedAtTime;
	private Collection<TimelineRequest> requests;
	private MetricPeriod period;

	public PacketRequestProductionTimeline() {

	}

	public PacketRequestProductionTimeline(long requestedAtTime, Collection<TimelineRequest> requests,
			MetricPeriod period) {
		this.requestedAtTime = requestedAtTime;
		this.requests = requests;
		this.period = period;
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeLong(requestedAtTime);
		buffer.writeByte(period.ordinal());

		buffer.writeByte(requests.size());
		for (TimelineRequest request : requests) {
			request.encode(buffer);
		}
	}

	@Override
	public void decode(FriendlyByteBuf buffer) {
		requestedAtTime = buffer.readLong();
		period = MetricPeriod.values()[buffer.readByte()];

		int count = buffer.readByte();
		requests = new ArrayList<>(count);
		for (int i = 0; i < count; i++) {
			requests.add(TimelineRequest.decode(buffer));
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
			for (TimelineRequest request : requests) {
				ServerProductionCache<?> cache = (ServerProductionCache<?>) team.getProductionManager()
						.getProductTypeCache(request.productType);
				ProductivityTimeline timeline = cache.getProductivityTimeline(request.type(), period, request.product(),
						requestedAtTime);
				if (!timeline.entries().isEmpty()) {
					timelines.add(timeline);
				}
			}

			PacketRecieveProductionTimeline response = new PacketRecieveProductionTimeline(requestedAtTime, period,
					timelines);
			StaticCoreMessageHandler.sendMessageToPlayer(StaticCoreMessageHandler.MAIN_PACKET_CHANNEL,
					(ServerPlayer) ctx.get().getSender(), response);
		});
	}
}
