package theking530.staticpower.teams.production;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import theking530.staticcore.utilities.SDTime;
import theking530.staticpower.StaticPower;
import theking530.staticpower.init.ModProducts;
import theking530.staticpower.teams.Team;
import theking530.staticpower.teams.production.metrics.MetricPeriod;
import theking530.staticpower.teams.production.metrics.SertializedBiDirectionalMetrics;
import theking530.staticpower.teams.production.product.ProductType;

public class ProductionManager {
	private final Team team;
	public final ItemProductivityCache itemProductvitiyCache;
	public SertializedBiDirectionalMetrics tempClientMetrics = SertializedBiDirectionalMetrics.EMPTY;

	public Map<ProductType<?, ?>, ProductionCache<?>> cache;

	public ProductionManager(Team team) {
		this.team = team;
		this.itemProductvitiyCache = new ItemProductivityCache(team.getDatabaseConnection());
		cache = new HashMap<>();
		cache.put(ModProducts.ITEM, itemProductvitiyCache);

		initializeDatabase();
	}

	public void tick(long gameTime) {
		int currentTickIndex = (int) (gameTime % 20);
		itemProductvitiyCache.insertProductivityPerSecond(team.getDatabaseConnection(), currentTickIndex, gameTime);

		if (gameTime % SDTime.TICKS_PER_SECOND == 0) {
			itemProductvitiyCache.clearOldEntries(team.getDatabaseConnection(), MetricPeriod.SECOND, gameTime);
		}

		if (gameTime % SDTime.TICKS_PER_MINUTE == 0) {
			itemProductvitiyCache.clearOldEntries(team.getDatabaseConnection(), MetricPeriod.MINUTE, gameTime);
			itemProductvitiyCache.updateAggregateData(team.getDatabaseConnection(), MetricPeriod.SECOND, MetricPeriod.MINUTE, gameTime);
		}

		if (gameTime % SDTime.TICKS_PER_HOUR == 0) {
			itemProductvitiyCache.clearOldEntries(team.getDatabaseConnection(), MetricPeriod.HOUR, gameTime);
			itemProductvitiyCache.updateAggregateData(team.getDatabaseConnection(), MetricPeriod.MINUTE, MetricPeriod.HOUR, gameTime);
		}

		if (gameTime % SDTime.TICKS_PER_DAY == 0) {
			itemProductvitiyCache.clearOldEntries(team.getDatabaseConnection(), MetricPeriod.DAY, gameTime);
			itemProductvitiyCache.updateAggregateData(team.getDatabaseConnection(), MetricPeriod.HOUR, MetricPeriod.DAY, gameTime);
		}
	}

	@SuppressWarnings("unchecked")
	public <T, K extends ProductionEntry<T>> ProductionCache<T> getCache(ProductType<T, K> productType) {
		if (cache.containsKey(productType)) {
			return (ProductionCache<T>) cache.get(productType);
		}
		return null;
	}

	protected void initializeDatabase() {
		Connection db = team.getDatabaseConnection();

		try {
			Statement stmt = db.createStatement();
			stmt.addBatch(createProductivityTable("item", MetricPeriod.SECOND));
			stmt.addBatch(createProductivityTable("item", MetricPeriod.MINUTE));
			stmt.addBatch(createProductivityTable("item", MetricPeriod.HOUR));
			stmt.addBatch(createProductivityTable("item", MetricPeriod.DAY));
			stmt.executeBatch();
		} catch (SQLException e) {
			StaticPower.LOGGER.error(String.format("An error occured when creating the productivity tracking tables!"), e);
		}

		itemProductvitiyCache.initializeDatabase();
	}

	private String createProductivityTable(String productType, MetricPeriod period) {
		//@formatter:off
		return String.format("CREATE TABLE IF NOT EXISTS %1$s_productivity_%2$s (\n" 
				+ "	product_hash int NOT NULL,\n" 
				+ "	consumed integer NOT NULL,\n"
				+ "	produced integer NOT NULL,\n" 
				+ "	consumption_rate real NOT NULL,\n"
				+ "	production_rate real NOT NULL,\n" 
				+ "	game_tick bigint NOT NULL\n"
				+ ");", productType, period.getTableKey());
		//@formatter:on
	}
}
