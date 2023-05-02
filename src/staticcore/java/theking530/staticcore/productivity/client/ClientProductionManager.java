package theking530.staticcore.productivity.client;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import theking530.staticcore.StaticCoreRegistries;
import theking530.staticcore.network.StaticCoreMessageHandler;
import theking530.staticcore.productivity.IProductionManager;
import theking530.staticcore.productivity.metrics.MetricPeriod;
import theking530.staticcore.productivity.network.PacketRequestProductionMetrics;
import theking530.staticcore.productivity.network.PacketRequestProductionTimeline;
import theking530.staticcore.productivity.network.ProductivityTimelineRequest;
import theking530.staticcore.productivity.product.ProductType;

public class ClientProductionManager implements IProductionManager<ClientProductionCache<?>> {
	private final Map<ProductType<?>, ClientProductionCache<?>> cache;

	public ClientProductionManager() {
		cache = new HashMap<>();
		Collection<ProductType<?>> registeredProducts = StaticCoreRegistries.ProductRegistry().getValues();
		for (ProductType<?> productType : registeredProducts) {
			cache.put(productType, productType.createClientCache());
		}
	}

	@Override
	public void tick(long gameTime) {
		for (ClientProductionCache<?> prodCache : cache.values()) {
			prodCache.tick(gameTime);
		}
	}

	@Override
	public boolean isClientSide() {
		return true;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> ClientProductionCache<T> getProductCache(ProductType<T> productType) {
		if (cache.containsKey(productType)) {
			return (ClientProductionCache<T>) cache.get(productType);
		}
		return null;
	}

	public void requestMetricUpdate(MetricPeriod period, ProductType<?> productType) {
		StaticCoreMessageHandler.sendToServer(StaticCoreMessageHandler.MAIN_PACKET_CHANNEL,
				new PacketRequestProductionMetrics(period, productType));
	}

	public void requestTimelineUpdate(MetricPeriod period, List<ProductivityTimelineRequest> requests, long startTime,
			long endTime) {
		if (period == MetricPeriod.SECOND) {
			throw new RuntimeException("We can't get a timeline for a single second!");
		}

		StaticCoreMessageHandler.sendToServer(StaticCoreMessageHandler.MAIN_PACKET_CHANNEL,
				new PacketRequestProductionTimeline(startTime, endTime, requests,
						MetricPeriod.values()[period.ordinal() - 1]));
	}
}
