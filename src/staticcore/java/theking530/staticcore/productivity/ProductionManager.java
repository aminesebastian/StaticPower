package theking530.staticcore.productivity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import theking530.staticcore.StaticCoreRegistries;
import theking530.staticcore.productivity.cacheentry.ProductionEntry;
import theking530.staticcore.productivity.metrics.MetricPeriod;
import theking530.staticcore.productivity.product.ProductType;
import theking530.staticcore.teams.Team;
import theking530.staticcore.utilities.SDTime;

public class ProductionManager {
	private final Team team;
	private final Map<ProductType<?>, ProductionCache<?>> cache;
	private final boolean isClientSide;

	public ProductionManager(Team team, boolean isClientSide) {
		this.team = team;
		this.isClientSide = isClientSide;
		cache = new HashMap<>();

		Collection<ProductType<?>> registeredProducts = StaticCoreRegistries.ProductRegistry().getValues();
		for (ProductType<?> productType : registeredProducts) {
			cache.put(productType, productType.createNewCacheInstance(isClientSide));
		}
		if (!isClientSide) {
			initializeDatabase();
		}
	}

	public void tick(long gameTime) {
		if (!isClientSide) {
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
	}

	@SuppressWarnings("unchecked")
	public <T, K extends ProductionEntry<T>> ProductionCache<T> getCache(ProductType<T> productType) {
		if (cache.containsKey(productType)) {
			return (ProductionCache<T>) cache.get(productType);
		}
		return null;
	}

	protected void initializeDatabase() {
		for (ProductionCache<?> prodCache : cache.values()) {
			prodCache.initializeDatabase(team.getDatabaseConnection());
		}
	}

}
