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
import theking530.staticcore.productivity.ProductionCache;
import theking530.staticcore.productivity.product.ProductType;
import theking530.staticcore.teams.Team;
import theking530.staticcore.teams.TeamManager;

public class PacketRequestProductionTimeline extends NetworkMessage {
	private ProductType<?> productType;
	private Collection<Integer> productHashCodes;
	private MetricPeriod period;
	private MetricType type;

	public PacketRequestProductionTimeline() {

	}

	public PacketRequestProductionTimeline(ProductType<?> productType, Collection<Integer> productHashCodes, MetricPeriod period, MetricType type) {
		this.productType = productType;
		this.productHashCodes = productHashCodes;
		this.period = period;
		this.type = type;
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeUtf(StaticCoreRegistries.ProductRegistry().getKey(productType).toString());
		buffer.writeByte(period.ordinal());
		buffer.writeByte(type.ordinal());
		buffer.writeByte(productHashCodes.size());
		for (int code : productHashCodes) {
			buffer.writeInt(code);
		}
	}

	@Override
	public void decode(FriendlyByteBuf buffer) {
		productType = StaticCoreRegistries.ProductRegistry().getValue(new ResourceLocation(buffer.readUtf()));
		period = MetricPeriod.values()[buffer.readByte()];
		type = MetricType.values()[buffer.readByte()];

		productHashCodes = new ArrayList<>();
		int count = buffer.readByte();
		for (int i = 0; i < count; i++) {
			productHashCodes.add(buffer.readInt());
		}
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer serverPlayer = ctx.get().getSender();
			Team team = TeamManager.get(serverPlayer.level).getTeamForPlayer(serverPlayer);
			if (team == null) {
				StaticCore.LOGGER
						.error(String.format("Recieved request for production timeline for player: %1$s that does not belong to any team!", serverPlayer.getName().getString()));
				return;
			}

			ProductionCache<?> cache = team.getProductionManager().getCache(productType);
			List<ProductivityTimeline> timelines = new LinkedList<ProductivityTimeline>();
			for (int hashCode : productHashCodes) {
				ProductivityTimeline timeline = cache.getProductivityTimeline(period, hashCode, serverPlayer.level.getGameTime());
				if (!timeline.entries().isEmpty()) {
					timelines.add(timeline);
				}
			}

			PacketRecieveProductionTimeline response = new PacketRecieveProductionTimeline(productType, period, type, timelines);
			StaticCoreMessageHandler.sendMessageToPlayer(StaticCoreMessageHandler.MAIN_PACKET_CHANNEL, (ServerPlayer) ctx.get().getSender(), response);
		});
	}
}
