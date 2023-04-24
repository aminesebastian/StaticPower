package theking530.staticcore.productivity.metrics;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent.Context;
import theking530.staticcore.StaticCoreRegistries;
import theking530.staticcore.network.NetworkMessage;
import theking530.staticcore.productivity.client.ClientProductionCache;
import theking530.staticcore.productivity.client.ClientProductionManager;
import theking530.staticcore.productivity.product.ProductType;
import theking530.staticcore.teams.TeamManager;

public class PacketRecieveProductionMetrics extends NetworkMessage {
	private ProductionMetrics metrics;
	private ProductType<?> productType;

	public PacketRecieveProductionMetrics() {

	}

	public PacketRecieveProductionMetrics(ProductType<?> productType, ProductionMetrics metrics) {
		this.metrics = metrics;
		this.productType = productType;
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeUtf(StaticCoreRegistries.ProductRegistry().getKey(productType).toString());
		metrics.encode(buffer);
	}

	@Override
	public void decode(FriendlyByteBuf buffer) {
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
					.getProductTypeCache(productType);
			clientCache.setClientSyncedMetrics(metrics, Minecraft.getInstance().level.getGameTime());
		});
	}
}
