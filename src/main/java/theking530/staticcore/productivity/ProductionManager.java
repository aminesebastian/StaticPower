package theking530.staticcore.productivity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import theking530.staticcore.productivity.entry.ProductionEntry;
import theking530.staticcore.productivity.metrics.MetricPeriod;
import theking530.staticcore.productivity.metrics.SertializedBiDirectionalMetrics;
import theking530.staticcore.productivity.product.ProductType;
import theking530.staticcore.utilities.SDTime;
import theking530.staticpower.StaticPowerRegistries;
import theking530.staticpower.teams.Team;

public class ProductionManager {
	private final Team team;
	private final Map<ProductType<?>, SertializedBiDirectionalMetrics> clientProductionMetrics;
	private final Map<ProductType<?>, ProductionCache<?>> cache;

	public ProductionManager(Team team) {
		this.team = team;
		cache = new HashMap<>();
		clientProductionMetrics = new HashMap<>();

		Collection<ProductType<?>> registeredProducts = StaticPowerRegistries.ProductRegistry().getValues();
		for (ProductType<?> productType : registeredProducts) {
			cache.put(productType, productType.createNewCacheInstance());
			clientProductionMetrics.put(productType, SertializedBiDirectionalMetrics.EMPTY);
		}

		initializeDatabase();
	}

	public void tick(long gameTime) {
		int currentTickIndex = (int) (gameTime % 20);
		for (ProductionCache<?> prodCache : cache.values()) {
			prodCache.tick(gameTime);
			prodCache.insertProductivityPerSecond(team.getDatabaseConnection(), currentTickIndex, gameTime);

			if (gameTime % SDTime.TICKS_PER_SECOND == 0) {
				prodCache.clearOldEntries(team.getDatabaseConnection(), MetricPeriod.SECOND, gameTime);
			}

			if (gameTime % SDTime.TICKS_PER_MINUTE == 0) {
				prodCache.clearOldEntries(team.getDatabaseConnection(), MetricPeriod.MINUTE, gameTime);
				prodCache.updateAggregateData(team.getDatabaseConnection(), MetricPeriod.SECOND, MetricPeriod.MINUTE, gameTime);
			}

			if (gameTime % SDTime.TICKS_PER_HOUR == 0) {
				prodCache.clearOldEntries(team.getDatabaseConnection(), MetricPeriod.HOUR, gameTime);
				prodCache.updateAggregateData(team.getDatabaseConnection(), MetricPeriod.MINUTE, MetricPeriod.HOUR, gameTime);
			}

			if (gameTime % SDTime.TICKS_PER_DAY == 0) {
				prodCache.clearOldEntries(team.getDatabaseConnection(), MetricPeriod.DAY, gameTime);
				prodCache.updateAggregateData(team.getDatabaseConnection(), MetricPeriod.HOUR, MetricPeriod.DAY, gameTime);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public <T, K extends ProductionEntry<T>> ProductionCache<T> getCache(ProductType<T> productType) {
		if (cache.containsKey(productType)) {
			return (ProductionCache<T>) cache.get(productType);
		}
		return null;
	}

	public <T> SertializedBiDirectionalMetrics getClientSyncedMetrics(ProductType<T> productType) {
		return clientProductionMetrics.get(productType);
	}

	public <T> void setClientSyncedMetrics(ProductType<T> productType, SertializedBiDirectionalMetrics metrics) {
		clientProductionMetrics.put(productType, metrics);
	}

	protected void initializeDatabase() {
		for (ProductionCache<?> prodCache : cache.values()) {
			prodCache.initializeDatabase(team.getDatabaseConnection());
		}
	}

}
