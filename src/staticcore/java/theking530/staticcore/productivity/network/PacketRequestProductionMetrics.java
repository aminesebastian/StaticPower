package theking530.staticcore.productivity.network;

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
import theking530.staticcore.productivity.metrics.MetricPeriod;
import theking530.staticcore.productivity.product.ProductType;
import theking530.staticcore.teams.ITeam;
import theking530.staticcore.teams.TeamManager;

public class PacketRequestProductionMetrics extends NetworkMessage {
	private MetricPeriod period;
	private ProductType<?> productType;

	public PacketRequestProductionMetrics() {

	}

	public PacketRequestProductionMetrics(MetricPeriod period, ProductType<?> productType) {
		this.productType = productType;
		this.period = period;
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeByte(period.ordinal());
		buffer.writeUtf(StaticCoreRegistries.ProductRegistry().getKey(productType).toString());
	}

	@Override
	public void decode(FriendlyByteBuf buffer) {
		period = MetricPeriod.values()[buffer.readByte()];
		productType = StaticCoreRegistries.ProductRegistry().getValue(new ResourceLocation(buffer.readUtf()));
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer serverPlayer = ctx.get().getSender();
			ITeam team = TeamManager.get(serverPlayer.level).getTeamForPlayer(serverPlayer);
			if (team == null) {
				StaticCore.LOGGER.error(String.format(
						"Recieved request for production metrics for player: %1$s that does not belong to any team!",
						serverPlayer.getName().getString()));
				return;
			}

			ServerProductionCache<?> cache = (ServerProductionCache<?>) team.getProductionManager()
					.getProductCache(productType);
			PacketRecieveProductionMetrics response = new PacketRecieveProductionMetrics(period, productType,
					cache.getProductionMetrics(period));
			StaticCoreMessageHandler.sendMessageToPlayer(StaticCoreMessageHandler.MAIN_PACKET_CHANNEL,
					(ServerPlayer) ctx.get().getSender(), response);
		});
	}
}
