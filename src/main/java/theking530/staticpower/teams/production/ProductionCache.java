package theking530.staticpower.teams.production;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import theking530.staticpower.StaticPower;
import theking530.staticpower.teams.production.ProductionEntry.ProductionEntryState;
import theking530.staticpower.teams.production.metrics.MetricPeriod;
import theking530.staticpower.teams.production.metrics.MetricType;
import theking530.staticpower.teams.production.metrics.SerializedMetricPeriod;
import theking530.staticpower.teams.production.metrics.SertializedBiDirectionalMetrics;

public abstract class ProductionCache<T> {
	private final List<Map<Integer, ProductionEntry<T>>> productivityBuckets;
	private final Map<Integer, Integer> productivityBucketMap;
	private final Connection database;
	private int bucketRoundRobinIndex;

	public ProductionCache(Connection database) {
		this.database = database;

		bucketRoundRobinIndex = 0;
		productivityBucketMap = new HashMap<>();
		productivityBuckets = new LinkedList<Map<Integer, ProductionEntry<T>>>();
		for (int i = 0; i < 20; i++) {
			productivityBuckets.add(new HashMap<Integer, ProductionEntry<T>>());
		}
	}

	public ProductionEntry<T> addOrUpdateProductionRate(ProductionTrackingToken<T> token, T product, int productHash, double rate) {
		ProductionEntry<T> entry = getOrCreateProductionEntry(product, productHash);
		entry.updateProductionRate(token, rate);
		return entry;
	}

	public ProductionEntry<T> addOrUpdateConsumptionRate(ProductionTrackingToken<T> token, T product, int productHash, double rate) {
		ProductionEntry<T> entry = getOrCreateProductionEntry(product, productHash);
		entry.updateConsumptionRate(token, rate);
		return entry;
	}

	public ProductionEntry<T> addProduced(T product, int productHash, double amount) {
		ProductionEntry<T> entry = getOrCreateProductionEntry(product, productHash);
		entry.produced(amount);
		return entry;
	}

	public ProductionEntry<T> addConsumed(T product, int productHash, double amount) {
		ProductionEntry<T> entry = getOrCreateProductionEntry(product, productHash);
		entry.consumed(amount);
		return entry;
	}

	public void initializeDatabase() {
		createProductLookupTable();
	}

	public void insertProductivityPerSecond(Connection database, int bucketIndex, long gameTime) {
		List<ProductionEntry<T>> toBeInserted = new LinkedList<>();

		try {
			Collection<ProductionEntry<T>> bucket = productivityBuckets.get(bucketIndex).values();
			Statement stmt = database.createStatement();

			for (ProductionEntry<T> entry : bucket) {
				ProductionEntryState metric = entry.getValuesForDatabaseInsert();
				if (metric.isEmpty()) {
					continue;
				}

				//@formatter:off
				String upsert = String.format("REPLACE INTO %1$s_productivity_%2$s(product_hash, consumed, produced, consumption_rate, production_rate, game_tick) \n"
						+ "  VALUES('%3$d', '%4$f', '%5$f', '%6$f', '%7$f', '%8$d');",
						getProductType(), MetricPeriod.SECOND.getTableKey(), 
						entry.getProductHashCode(), 
						metric.consumed(),
						metric.produced(),
						metric.consumptionRate(),
						metric.productionRate(),
						gameTime);
				//@formatter:on
				stmt.addBatch(upsert);
				toBeInserted.add(entry);
			}

			if (!toBeInserted.isEmpty()) {
				StaticPower.LOGGER.trace(String.format("Inserting %1$d entries for bucket: %2$d for product: %3$s.", toBeInserted.size(), bucketIndex, getProductType()));
				stmt.executeBatch();

				// Only if we made it this far should we clear the entires.
				for (ProductionEntry<T> entry : toBeInserted) {
					entry.clearCurrentSecondMetrics();
				}
			}

		} catch (SQLException e) {
			StaticPower.LOGGER.error(String.format("An error occured when inserting the per second productivity for product: %1$s.", getProductType()), e);
		}
	}

	public void updateAggregateData(Connection database, MetricPeriod fromPeriod, MetricPeriod toPeriod, long gameTime) {
		StaticPower.LOGGER.trace(String.format("Updating aggregate data for product type: %1$s from period: %2$s to period: %3$s.", getProductType(), fromPeriod.getTableKey(),
				toPeriod.getTableKey()));
		try {
			//@formatter:off
			String upsert = String.format("REPLACE INTO %1$s_productivity_%3$s \n"
					+ " SELECT product_hash, "
					+ " 	SUM(consumed) as consumed, "
					+ " 	SUM(produced) as produced, "
					+ " 	AVG(consumption_rate) as consumption_rate, "
					+ " 	AVG(production_rate) as production_rate, "
					+ "		%4$d \n"
					+ " FROM item_productivity_%2$s \n"
					+ " WHERE game_tick > %5$d \n"
					+ " GROUP BY product_hash;",
					getProductType(), fromPeriod.getTableKey(), toPeriod.getTableKey(), gameTime, gameTime - toPeriod.getMetricPeriodInTicks());
			//@formatter:on

			Statement stmt = database.createStatement();
			stmt.execute(upsert);
		} catch (SQLException e) {
			StaticPower.LOGGER.error(String.format("An error occured when aggregating the productivity for product: %1$s from period: %2$s to period: %3$s.", getProductType(),
					fromPeriod.getTableKey(), toPeriod.getTableKey()), e);
		}
	}

	public SertializedBiDirectionalMetrics getSerializedProductionMetrics(long gameTime, int limit, MetricPeriod period) {
		List<SerializedMetricPeriod> inputs = getAverageProductionRate(gameTime, limit, period, MetricType.CONSUMPTION);
		List<SerializedMetricPeriod> outputs = getAverageProductionRate(gameTime, limit, period, MetricType.PRODUCTION);
		return new SertializedBiDirectionalMetrics(inputs, outputs);
	}

	private List<SerializedMetricPeriod> getAverageProductionRate(long gameTime, int limit, MetricPeriod period, MetricType direction) {
		List<SerializedMetricPeriod> metrics = new LinkedList<SerializedMetricPeriod>();

		try {
		//@formatter:off
		String query = String.format("SELECT   \r\n"
				+ "	 product.serialized_product,   \r\n"
				+ "	 AVG(consumption_rate) as consumption_rate,  \r\n"
				+ "	 AVG(production_rate) as production_rate  \r\n"
				+ "FROM   \r\n"
				+ "	 item_product as product  \r\n"
				+ "		LEFT JOIN  \r\n"
				+ "	 item_productivity_second as metric  \r\n"
				+ "		ON  \r\n"
				+ "	 product.product_hash = metric.product_hash \r\n"
				+ "WHERE	\r\n"
				+ "	metric.game_tick >= %1$d OR metric.game_tick IS NULL\r\n"
				+ "GROUP BY  \r\n"
				+ "	product.product_hash \r\n"
				+ "ORDER BY %2$s_rate DESC \r\n"
				+ "LIMIT %3$d;", gameTime - period.getMetricPeriodInTicks(), direction.getQueryField(), limit);
		//@formatter:on
			Statement stmt = database.createStatement();
			ResultSet result = stmt.executeQuery(query);
			while (result.next()) {
				String product = result.getString("serialized_product");
				double consumption = result.getDouble("consumption_rate") * period.getPeriodLengthInSeconds();
				double production = result.getDouble("production_rate") * period.getPeriodLengthInSeconds();
				if (direction == MetricType.CONSUMPTION) {
					if (consumption > 0) {
						metrics.add(new SerializedMetricPeriod(product, consumption, production, period));
					}
				} else {
					if (production > 0) {
						metrics.add(new SerializedMetricPeriod(product, consumption, production, period));
					}
				}
			}
		} catch (SQLException e) {
			StaticPower.LOGGER.error(String.format("An error occured when pulling the average production rage for product type: %1$s.", getProductType()), e);
		}

		return metrics;
	}

	public void clearOldEntries(Connection database, MetricPeriod period, long gameTime) {
		if (period.getMaxRecordsAgeTicks() == -1) {
			return;
		}

		StaticPower.LOGGER.trace(String.format("Deleting old data for product: %1$s over period: %2$s.", getProductType(), period.getTableKey()));
		try {
			//@formatter:off
			String upsert = String.format("DELETE FROM %1$s_productivity_%2$s \n"
					+ " WHERE game_tick < %3$d;",
					getProductType(), period.getTableKey(),  gameTime - period.getMaxRecordsAgeTicks());
			//@formatter:on

			Statement stmt = database.createStatement();
			stmt.execute(upsert);
		} catch (SQLException e) {
			StaticPower.LOGGER
					.error(String.format("An error occured when clearing old data for product: %1$s over period: %2$s.", getProductType(), period.getMaxRecordsAgeTicks()), e);
		}
	}

	protected boolean containsProduct(int productHash) {
		return productivityBucketMap.containsKey(productHash);
	}

	protected ProductionEntry<T> getOrCreateProductionEntry(T product, int productHash) {
		ProductionEntry<T> entry;
		if (containsProduct(productHash)) {
			Map<Integer, ProductionEntry<T>> bucket = productivityBuckets.get(productivityBucketMap.get(productHash));
			entry = bucket.get(productHash);
		} else {
			entry = createNewEntry(product);
			insertNew(entry);
		}
		return entry;
	}

	protected void insertNew(ProductionEntry<T> entry) {
		if (containsProduct(entry.getProductHashCode())) {
			throw new RuntimeException("Attempted to insert a product entry for a product we're already tracking!");
		}
		productivityBucketMap.put(entry.getProductHashCode(), bucketRoundRobinIndex);
		Map<Integer, ProductionEntry<T>> bucket = productivityBuckets.get(bucketRoundRobinIndex);
		bucket.put(entry.getProductHashCode(), entry);
		bucketRoundRobinIndex = (bucketRoundRobinIndex + 1) % productivityBuckets.size();
		insertNewProduct(entry);
	}

	protected void createProductLookupTable() {
		//@formatter:off
		String tableQuery =  String.format("CREATE TABLE IF NOT EXISTS %1$s_product (\n" 
				+ "	product_hash int NOT NULL,\n" 
				+ "	serialized_product text NOT NULL,\n"
				+ " UNIQUE(product_hash,serialized_product)\n"
				+ ")", getProductType());
		//@formatter:on

		try {
			Statement stmt = getDatabase().createStatement();
			stmt.execute(tableQuery);
		} catch (SQLException e) {
			StaticPower.LOGGER.error(String.format("An error occured when creating the lookup table for product type: $1$s!", getProductType()), e);
		}
	}

	protected void insertNewProduct(ProductionEntry<T> entry) {
		//@formatter:off
			String insert = String.format("REPLACE INTO %1$s_product(product_hash, serialized_product) \n"
					+ "  VALUES('%2$d', '%3$s');",
					getProductType(), entry.getProductHashCode(), entry.getSerializedProduct());
			//@formatter:on
		try {
			Statement stmt = getDatabase().createStatement();
			stmt.execute(insert);
		} catch (SQLException e) {
			StaticPower.LOGGER.error(String.format("An error occured when inserting a new product entry for product: $1$s!", entry.getSerializedProduct()), e);
		}
	}

	protected abstract ProductionEntry<T> createNewEntry(T product);

	protected abstract String getProductType();

	protected Connection getDatabase() {
		return database;
	}
}
