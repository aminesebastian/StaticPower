package theking530.staticpower.teams.production.metrics;

import java.util.List;
import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;
import theking530.staticcore.network.NetworkMessage;
import theking530.staticpower.StaticPower;
import theking530.staticpower.network.StaticPowerMessageHandler;
import theking530.staticpower.teams.Team;
import theking530.staticpower.teams.TeamManager;

public class PacketGetProductionMetrics extends NetworkMessage {

	public PacketGetProductionMetrics() {

	}

	public PacketGetProductionMetrics(List<SerializedMetricPeriod> metrics) {
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
	}

	@Override
	public void decode(FriendlyByteBuf buffer) {
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer serverPlayer = ctx.get().getSender();
			Team team = TeamManager.get().getTeamForPlayer(serverPlayer);
			if (team == null) {
				StaticPower.LOGGER
						.error(String.format("Recieved request for production metrics for player: %1$s that does not belong to any team!", serverPlayer.getName().getString()));
				return;
			}

			PacketRecieveProductionMetrics response = new PacketRecieveProductionMetrics(
					team.getProductionManager().itemProductvitiyCache.getAverageProductionRate(MetricPeriod.MINUTE));
			StaticPowerMessageHandler.sendMessageToPlayer(StaticPowerMessageHandler.MAIN_PACKET_CHANNEL, (ServerPlayer) ctx.get().getSender(), response);
		});
	}
}
