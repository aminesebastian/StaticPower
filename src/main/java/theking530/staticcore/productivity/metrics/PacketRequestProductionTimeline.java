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

public class PacketRequestProductionTimeline extends NetworkMessage {
	private ProductType<?> productType;
	private int productHashCode;
	private MetricPeriod period;

	public PacketRequestProductionTimeline() {

	}

	public PacketRequestProductionTimeline(ProductType<?> productType, int productHashCode, MetricPeriod period) {
		this.productType = productType;
		this.productHashCode = productHashCode;
		this.period = period;
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeUtf(StaticPowerRegistries.ProductRegistry().getKey(productType).toString());
		buffer.writeInt(productHashCode);
		buffer.writeByte(period.ordinal());
	}

	@Override
	public void decode(FriendlyByteBuf buffer) {
		productType = StaticPowerRegistries.ProductRegistry().getValue(new ResourceLocation(buffer.readUtf()));
		productHashCode = buffer.readInt();
		period = MetricPeriod.values()[buffer.readByte()];
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer serverPlayer = ctx.get().getSender();
			Team team = TeamManager.get(serverPlayer.level).getTeamForPlayer(serverPlayer);
			if (team == null) {
				StaticPower.LOGGER
						.error(String.format("Recieved request for production timeline for player: %1$s that does not belong to any team!", serverPlayer.getName().getString()));
				return;
			}

			PacketRecieveProductionTimeline response = new PacketRecieveProductionTimeline(productType, productHashCode, period,
					team.getProductionManager().getCache(productType).getProductivityTimeline(period, productHashCode, serverPlayer.level.getGameTime()));
			StaticPowerMessageHandler.sendMessageToPlayer(StaticPowerMessageHandler.MAIN_PACKET_CHANNEL, (ServerPlayer) ctx.get().getSender(), response);
		});
	}
}
