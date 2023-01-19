package theking530.staticcore.productivity.metrics;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;
import theking530.staticcore.network.NetworkMessage;
import theking530.staticcore.productivity.product.ProductType;
import theking530.staticpower.StaticPower;
import theking530.staticpower.StaticPowerRegistries;
import theking530.staticpower.network.StaticPowerMessageHandler;
import theking530.staticpower.teams.Team;
import theking530.staticpower.teams.TeamManager;

public class PacketRequestProductionMetrics extends NetworkMessage {
	private ProductType<?> productType;

	public PacketRequestProductionMetrics() {

	}

	public PacketRequestProductionMetrics(ProductType<?> productType) {
		this.productType = productType;
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeUtf(StaticPowerRegistries.ProductRegistry().getKey(productType).toString());
	}

	@Override
	public void decode(FriendlyByteBuf buffer) {
		productType = StaticPowerRegistries.ProductRegistry().getValue(new ResourceLocation(buffer.readUtf()));
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer serverPlayer = ctx.get().getSender();
			Team team = TeamManager.get(serverPlayer.level).getTeamForPlayer(serverPlayer);
			if (team == null) {
				StaticPower.LOGGER
						.error(String.format("Recieved request for production metrics for player: %1$s that does not belong to any team!", serverPlayer.getName().getString()));
				return;
			}

			PacketRecieveProductionMetrics response = new PacketRecieveProductionMetrics(productType,
					team.getProductionManager().getCache(productType).getProductionMetrics(MetricPeriod.MINUTE));
			StaticPowerMessageHandler.sendMessageToPlayer(StaticPowerMessageHandler.MAIN_PACKET_CHANNEL, (ServerPlayer) ctx.get().getSender(), response);
		});
	}
}