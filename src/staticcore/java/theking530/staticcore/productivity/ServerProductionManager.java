package theking530.staticcore.productivity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import theking530.staticcore.StaticCoreRegistries;
import theking530.staticcore.productivity.metrics.MetricPeriod;
import theking530.staticcore.productivity.product.ProductType;
import theking530.staticcore.teams.ServerTeam;
import theking530.staticcore.utilities.SDTime;

public class ServerProductionManager implements IProductionManager<ServerProductionCache<?>> {
	private final ServerTeam team;
	private final Map<ProductType<?>, ServerProductionCache<?>> cache;

	public ServerProductionManager(ServerTeam team) {
		this.team = team;
		cache = new HashMap<>();

		Collection<ProductType<?>> registeredProducts = StaticCoreRegistries.ProductRegistry().getValues();
		for (ProductType<?> productType : registeredProducts) {
			cache.put(productType, productType.createServerCache());
		}
		initializeDatabase();
	}

	public void tick(long gameTime) {
		int currentTickIndex = (int) (gameTime % 20);
		for (ServerProductionCache<?> prodCache : cache.values()) {
			prodCache.tick(gameTime);
			prodCache.insertProductivityPerSecond(team.getDatabaseConnection(), currentTickIndex, gameTime);

			if (gameTime % SDTime.TICKS_PER_SECOND == 0) {
				prodCache.clearOldEntries(team.getDatabaseConnection(), MetricPeriod.SECOND, gameTime);
			}

			if (gameTime % SDTime.TICKS_PER_MINUTE == 0) {
				prodCache.clearOldEntries(team.getDatabaseConnection(), MetricPeriod.MINUTE, gameTime);
				prodCache.updateAggregateData(team.getDatabaseConnection(), MetricPeriod.SECOND, MetricPeriod.MINUTE,
						gameTime);
			}

			if (gameTime % SDTime.TICKS_PER_HOUR == 0) {
				prodCache.clearOldEntries(team.getDatabaseConnection(), MetricPeriod.HOUR, gameTime);
				prodCache.updateAggregateData(team.getDatabaseConnection(), MetricPeriod.MINUTE, MetricPeriod.HOUR,
						gameTime);
			}

			if (gameTime % SDTime.TICKS_PER_DAY == 0) {
				prodCache.clearOldEntries(team.getDatabaseConnection(), MetricPeriod.DAY, gameTime);
				prodCache.updateAggregateData(team.getDatabaseConnection(), MetricPeriod.HOUR, MetricPeriod.DAY,
						gameTime);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> ServerProductionCache<T> getProductTypeCache(ProductType<T> productType) {
		if (cache.containsKey(productType)) {
			return (ServerProductionCache<T>) cache.get(productType);
		}
		return null;
	}

	protected void initializeDatabase() {
		for (ServerProductionCache<?> prodCache : cache.values()) {
			prodCache.initializeDatabase(team.getDatabaseConnection());
		}
	}

	@Override
	public boolean isClientSide() {
		return false;
	}
}
