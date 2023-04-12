package theking530.staticcore.productivity.client;

import java.util.Optional;

import theking530.staticcore.productivity.IProductionCache;
import theking530.staticcore.productivity.metrics.ClientProductionMetric;
import theking530.staticcore.productivity.metrics.ClientProductionMetrics;
import theking530.staticcore.productivity.metrics.MetricPeriod;
import theking530.staticcore.productivity.metrics.ServerProductionMetrics;
import theking530.staticcore.productivity.product.ProductType;

public class ClientProductionCache<T> implements IProductionCache<T> {
	public static final double SMOOTHING_FACTOR = 10;

	private final ProductType<T> productType;
	private ClientProductionMetrics nextMetrics;
	private ClientProductionMetrics clientMetrics;
	private long lastClientSyncTime;

	public ClientProductionCache(ProductType<T> productType) {
		this.productType = productType;
		nextMetrics = ClientProductionMetrics.EMPTY;
		clientMetrics = ClientProductionMetrics.EMPTY;
	}

	@Override
	public void tick(long gameTime) {
		for (ClientProductionMetric lastProduced : clientMetrics.getProduction()) {
			Optional<ClientProductionMetric> next = nextMetrics.getProduction().stream()
					.filter((x) -> x.getProductHash() == lastProduced.getProductHash()).findFirst();
			if (next.isEmpty()) {
				continue;
			}

			lastProduced.getProduced().interpolateTowards(next.get().getProduced().getCurrentValue(),
					next.get().getProduced().getIdealValue(), SMOOTHING_FACTOR);
		}

		for (ClientProductionMetric lastConsumed : clientMetrics.getConsumption()) {
			Optional<ClientProductionMetric> next = nextMetrics.getConsumption().stream()
					.filter((x) -> x.getProductHash() == lastConsumed.getProductHash()).findFirst();
			if (next.isEmpty()) {
				continue;
			}

			lastConsumed.getConsumed().interpolateTowards(next.get().getConsumed().getCurrentValue(),
					next.get().getConsumed().getIdealValue(), SMOOTHING_FACTOR);
		}
	}

	@Override
	public ProductType<T> getProductType() {
		return productType;
	}

	public ClientProductionMetrics getProductionMetrics(MetricPeriod period) {
		return clientMetrics;
	}

	public void setClientSyncedMetrics(ServerProductionMetrics metrics, long syncTime) {
		ClientProductionMetrics recievedMetrics = new ClientProductionMetrics(metrics.getConsumption(),
				metrics.getProduction());
		if (clientMetrics.isEmpty()) {
			clientMetrics = recievedMetrics;
		} else {
			clientMetrics = nextMetrics;
		}

		nextMetrics = recievedMetrics;
		lastClientSyncTime = syncTime;
	}

	@Override
	public boolean haveClientValuesUpdatedSince(long lastCheckTime) {
		return lastClientSyncTime > lastCheckTime;
	}
}