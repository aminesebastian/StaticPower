package theking530.staticcore.productivity.network;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent.Context;
import theking530.staticcore.StaticCoreRegistries;
import theking530.staticcore.network.NetworkMessage;
import theking530.staticcore.productivity.client.ClientProductionCache;
import theking530.staticcore.productivity.client.ClientProductionManager;
import theking530.staticcore.productivity.metrics.MetricPeriod;
import theking530.staticcore.productivity.metrics.ProductionMetrics;
import theking530.staticcore.productivity.product.ProductType;
import theking530.staticcore.teams.TeamManager;

public class PacketRecieveProductionMetrics extends NetworkMessage {
	private MetricPeriod period;
	private ProductionMetrics metrics;
	private ProductType<?> productType;

	public PacketRecieveProductionMetrics() {

	}

	public PacketRecieveProductionMetrics(MetricPeriod period, ProductType<?> productType, ProductionMetrics metrics) {
		this.period = period;
		this.metrics = metrics;
		this.productType = productType;
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeByte(period.ordinal());
		buffer.writeUtf(StaticCoreRegistries.ProductRegistry().getKey(productType).toString());
		metrics.encode(buffer);
	}

	@Override
	public void decode(FriendlyByteBuf buffer) {
		period = MetricPeriod.values()[buffer.readByte()];
		productType = StaticCoreRegistries.ProductRegistry().getValue(new ResourceLocation(buffer.readUtf()));
		metrics = ProductionMetrics.decode(buffer);
	}

	@SuppressWarnings("resource")
	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ClientProductionManager clientManager = (ClientProductionManager) TeamManager.getLocalTeam()
					.getProductionManager();
			ClientProductionCache<?> clientCache = (ClientProductionCache<?>) clientManager
					.getProductCache(productType);
			clientCache.recieveMetrics(period, metrics, Minecraft.getInstance().level.getGameTime());
		});
	}
}
