package theking530.staticcore.productivity;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import theking530.staticcore.productivity.entry.ProductionEntry;
import theking530.staticcore.productivity.entry.ProductionEntry.ProductionEntryState;
import theking530.staticcore.productivity.metrics.MetricPeriod;
import theking530.staticcore.productivity.metrics.MetricType;
import theking530.staticcore.productivity.metrics.SerializedMetricPeriod;
import theking530.staticcore.productivity.metrics.SertializedBiDirectionalMetrics;
import theking530.staticcore.productivity.product.ProductType;
import theking530.staticpower.StaticPower;

public class ProductionCache<T> {
	private final List<Map<Integer, ProductionEntry<T>>> productivityBuckets;
	private final Map<Integer, Integer> productivityBucketMap;
	private final ProductType<T> productType;
	private final String productTablePrefix;

	private Connection database;
	private int bucketRoundRobinIndex;

	public ProductionCache(ProductType<T> productType) {
		this.productType = productType;
		this.productTablePrefix = productType.getRegistryName().toString().replace(":", "_");
		bucketRoundRobinIndex = 0;
		productivityBucketMap = new HashMap<>();
		productivityBuckets = new LinkedList<Map<Integer, ProductionEntry<T>>>();
		for (int i = 0; i < 20; i++) {
			productivityBuckets.add(new HashMap<Integer, ProductionEntry<T>>());
		}
	}

	public void tick(long gameTime) {
		for (Map<Integer, ProductionEntry<T>> bucket : productivityBuckets) {
			for (ProductionEntry<T> entry : bucket.values()) {
				entry.tick(gameTime);
			}
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

	public void initializeDatabase(Connection database) {
		this.database = database;
		try {
			Statement stmt = database.createStatement();
			stmt.addBatch(createProductivityTable(MetricPeriod.SECOND));
			stmt.addBatch(createProductivityTable(MetricPeriod.MINUTE));
			stmt.addBatch(createProductivityTable(MetricPeriod.HOUR));
			stmt.addBatch(createProductivityTable(MetricPeriod.DAY));
			stmt.executeBatch();
		} catch (SQLException e) {
			StaticPower.LOGGER.error(String.format("An error occured when creating the productivity tracking tables!"), e);
		}
		createProductLookupTable();
	}

	public ProductType<T> getProductType() {
		return productType;
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
				String upsert = String.format("REPLACE INTO %1$s_productivity_%2$s(product_hash, consumed, produced, game_tick) \n"
						+ "  VALUES('%3$d', '%4$f', '%5$f',  '%6$d');",
						productTablePrefix, 
						MetricPeriod.SECOND.getTableKey(), 
						entry.getProductHashCode(), 
						metric.consumed(),
						metric.produced(),
						gameTime);
				//@formatter:on
				stmt.addBatch(upsert);
				toBeInserted.add(entry);
			}

			if (!toBeInserted.isEmpty()) {
				StaticPower.LOGGER.trace(String.format("Inserting %1$d entries for bucket: %2$d for product: %3$s.", toBeInserted.size(), bucketIndex, productTablePrefix));
				stmt.executeBatch();

				// Only if we made it this far should we clear the entires.
				for (ProductionEntry<T> entry : toBeInserted) {
					entry.clearCurrentSecondMetrics();
				}
			}

		} catch (SQLException e) {
			StaticPower.LOGGER.error(String.format("An error occured when inserting the per second productivity for product: %1$s.", productTablePrefix), e);
		}
	}

	public void updateAggregateData(Connection database, MetricPeriod fromPeriod, MetricPeriod toPeriod, long gameTime) {
		StaticPower.LOGGER.trace(String.format("Updating aggregate data for product type: %1$s from period: %2$s to period: %3$s.", productTablePrefix, fromPeriod.getTableKey(),
				toPeriod.getTableKey()));
		try {
			//@formatter:off
			String upsert = String.format("REPLACE INTO %1$s_productivity_%3$s \n"
					+ " SELECT product_hash, "
					+ " 	SUM(consumed) as consumed, "
					+ " 	SUM(produced) as produced, "
					+ "		%4$d \n"
					+ " FROM %1$s_productivity_%2$s \n"
					+ " WHERE game_tick > %5$d \n"
					+ " GROUP BY product_hash;",
					productTablePrefix, fromPeriod.getTableKey(), toPeriod.getTableKey(), gameTime, gameTime - toPeriod.getMetricPeriodInTicks());
			//@formatter:on

			Statement stmt = database.createStatement();
			stmt.execute(upsert);
		} catch (SQLException e) {
			StaticPower.LOGGER.error(String.format("An error occured when aggregating the productivity for product: %1$s from period: %2$s to period: %3$s.", productTablePrefix,
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

		// Pull the production rates from memory into the metrics result.
		for (Map<Integer, ProductionEntry<T>> bucket : productivityBuckets) {
			for (ProductionEntry<T> entry : bucket.values()) {
				double consumption = entry.getConsumptionRate() * period.getPeriodLengthInSeconds();
				double production = entry.getProductionRate() * period.getPeriodLengthInSeconds();
				metrics.add(new SerializedMetricPeriod(entry.getSerializedProduct(), consumption, production, period));
			}
		}

		// Sort such that the highest rates go to the top.
		if (direction == MetricType.PRODUCTION) {
			metrics.sort((m1, m2) -> Double.compare(m2.getProduction(), m1.getProduction()));
		} else {
			metrics.sort((m1, m2) -> Double.compare(m2.getConsumption(), m1.getConsumption()));
		}

		return metrics;
	}

	public void clearOldEntries(Connection database, MetricPeriod period, long gameTime) {
		if (period.getMaxRecordsAgeTicks() == -1) {
			return;
		}

		StaticPower.LOGGER.trace(String.format("Deleting old data for product: %1$s over period: %2$s.", productTablePrefix, period.getTableKey()));
		try {
			//@formatter:off
			String upsert = String.format("DELETE FROM %1$s_productivity_%2$s \n"
					+ " WHERE game_tick < %3$d;",
					productTablePrefix, period.getTableKey(),  gameTime - period.getMaxRecordsAgeTicks());
			//@formatter:on

			Statement stmt = database.createStatement();
			stmt.execute(upsert);
		} catch (SQLException e) {
			StaticPower.LOGGER
					.error(String.format("An error occured when clearing old data for product: %1$s over period: %2$s.", productTablePrefix, period.getMaxRecordsAgeTicks()), e);
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
			entry = productType.createProductionEntry(product);
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
				+ ")", productTablePrefix);
		//@formatter:on

		try {
			Statement stmt = getDatabase().createStatement();
			stmt.execute(tableQuery);
		} catch (SQLException e) {
			StaticPower.LOGGER.error(String.format("An error occured when creating the lookup table for product type: $1$s!", productTablePrefix), e);
		}
	}

	protected void insertNewProduct(ProductionEntry<T> entry) {
		//@formatter:off
			String insert = String.format("REPLACE INTO %1$s_product(product_hash, serialized_product) \n"
					+ "  VALUES('%2$d', '%3$s');",
					productTablePrefix, entry.getProductHashCode(), entry.getSerializedProduct());
			//@formatter:on
		try {
			Statement stmt = getDatabase().createStatement();
			stmt.execute(insert);
		} catch (SQLException e) {
			StaticPower.LOGGER.error(String.format("An error occured when inserting a new product entry for product: $1$s!", entry.getSerializedProduct()), e);
		}
	}

	protected Connection getDatabase() {
		return database;
	}

	private String createProductivityTable(MetricPeriod period) {
		//@formatter:off
		return String.format("CREATE TABLE IF NOT EXISTS %1$s_productivity_%2$s (\n" 
				+ "	product_hash int NOT NULL,\n" 
				+ "	consumed integer NOT NULL,\n"
				+ "	produced integer NOT NULL,\n" 
				+ "	game_tick bigint NOT NULL\n"
				+ ");", productTablePrefix, period.getTableKey());
		//@formatter:on
	}
}
