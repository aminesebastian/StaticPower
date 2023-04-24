package theking530.staticcore.productivity.client;

import theking530.staticcore.productivity.IProductionCache;
import theking530.staticcore.productivity.metrics.MetricPeriod;
import theking530.staticcore.productivity.metrics.ProductionMetric;
import theking530.staticcore.productivity.metrics.ProductionMetrics;
import theking530.staticcore.productivity.product.ProductType;

public class ClientProductionCache<T> implements IProductionCache<T> {
	public static final double SMOOTHING_FACTOR = 10;

	private final ProductType<T> productType;
	private ProductionMetrics nextMetrics;
	private ProductionMetrics clientMetrics;
	private long lastClientSyncTime;

	public ClientProductionCache(ProductType<T> productType) {
		this.productType = productType;
		nextMetrics = ProductionMetrics.EMPTY;
		clientMetrics = ProductionMetrics.EMPTY;
	}

	@Override
	public void tick(long gameTime) {
		for (ProductionMetric lastProduced : clientMetrics.getMetrics().values()) {
			if (nextMetrics.getMetrics().containsKey(lastProduced.getProductHash())) {
				lastProduced.interpolateTowards(nextMetrics.getMetrics().get(lastProduced.getProductHash()),
						SMOOTHING_FACTOR);
			}
		}
	}

	@Override
	public ProductType<T> getProductType() {
		return productType;
	}

	public ProductionMetrics getProductionMetrics(MetricPeriod period) {
		return clientMetrics;
	}

	public void setClientSyncedMetrics(ProductionMetrics recievedMetrics, long syncTime) {
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