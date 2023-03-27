package theking530.staticcore.productivity;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;

import theking530.staticcore.StaticCore;
import theking530.staticcore.StaticCoreRegistries;
import theking530.staticcore.productivity.cacheentry.ProductionEntry;
import theking530.staticcore.productivity.cacheentry.ProductionEntry.ProductionEntryState;
import theking530.staticcore.productivity.cacheentry.ProductivityRate;
import theking530.staticcore.productivity.metrics.MetricPeriod;
import theking530.staticcore.productivity.metrics.MetricType;
import theking530.staticcore.productivity.metrics.ProductionMetric;
import theking530.staticcore.productivity.metrics.ProductionMetrics;
import theking530.staticcore.productivity.metrics.ProductivityTimeline;
import theking530.staticcore.productivity.metrics.ProductivityTimeline.ProductivityTimelineEntry;
import theking530.staticcore.productivity.product.ProductType;

public class ProductionCache<T> {
	private final List<Map<Integer, ProductionEntry<T>>> productivityBuckets;
	private final Map<Integer, Integer> productivityBucketMap;
	private final ProductType<T> productType;
	private final String productTablePrefix;
	private ProductionMetrics clientMetrics;
	private long lastClientSyncTime;

	private Connection database;
	private int bucketRoundRobinIndex;
	private boolean isClientSide;

	public ProductionCache(ProductType<T> productType, boolean isClientSide) {
		this.isClientSide = isClientSide;
		this.productType = productType;
		this.productTablePrefix = StaticCoreRegistries.ProductRegistry().getKey(productType).toString().replace(":", "_");
		bucketRoundRobinIndex = 0;
		productivityBucketMap = new HashMap<>();
		productivityBuckets = new LinkedList<Map<Integer, ProductionEntry<T>>>();
		for (int i = 0; i < 20; i++) {
			productivityBuckets.add(new HashMap<Integer, ProductionEntry<T>>());
		}
		clientMetrics = ProductionMetrics.EMPTY;
	}

	public void tick(long gameTime) {
		for (Map<Integer, ProductionEntry<T>> bucket : productivityBuckets) {
			for (ProductionEntry<T> entry : bucket.values()) {
				entry.tick(gameTime);
			}
		}
	}

	public ProductionEntry<T> addOrUpdateProductionRate(ProductionTrackingToken<T> token, T product, int productHash, double rate) {
		return addOrUpdateProductionRate(token, product, productHash, rate, rate);
	}

	public ProductionEntry<T> addOrUpdateProductionRate(ProductionTrackingToken<T> token, T product, int productHash, double currentRate, double idealRate) {
		ProductionEntry<T> entry = getOrCreateProductionEntry(product, productHash);
		entry.updateProductionRate(token, currentRate, idealRate);
		return entry;
	}

	public ProductionEntry<T> addOrUpdateConsumptionRate(ProductionTrackingToken<T> token, T product, int productHash, double rate) {
		return addOrUpdateConsumptionRate(token, product, productHash, rate, rate);
	}

	public ProductionEntry<T> addOrUpdateConsumptionRate(ProductionTrackingToken<T> token, T product, int productHash, double currentRate, double idealRate) {
		ProductionEntry<T> entry = getOrCreateProductionEntry(product, productHash);
		entry.updateConsumptionRate(token, currentRate, idealRate);
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

	public ProductType<T> getProductType() {
		return productType;
	}

	public ProductivityTimeline getProductivityTimeline(MetricPeriod period, int productHash, long currentGameTick) {
		String serializedProduct = getSerializedProductFromHash(productHash);
		try {
			Statement stmt = database.createStatement();
			long threshold = currentGameTick - period.getMaxRecordsAgeTicks();
			//@formatter:off
			String query = String.format("SELECT consumed, produced, game_tick \n"
					+ "FROM %1$s_productivity_%2$s \n"
					+ "WHERE game_tick > %3$d AND product_hash = %4$d\n"
					+ "ORDER BY game_tick DESC",
					productTablePrefix, 
					period.getTableKey(),
					threshold,
					productHash);		
			//@formatter:on		

			List<ProductivityTimelineEntry> entries = new LinkedList<>();
			ResultSet sqlData = stmt.executeQuery(query);
			while (sqlData.next()) {
				ProductivityTimelineEntry entry = new ProductivityTimelineEntry(sqlData.getFloat(1), sqlData.getFloat(2), sqlData.getLong(3));
				entries.add(entry);
			}
			return new ProductivityTimeline(productType, serializedProduct, period, ImmutableList.copyOf(entries));
		} catch (Exception e) {
			StaticCore.LOGGER.error(String.format("An error occured when getting the production timeline for product hash: %1$d on table: %2$s.", productHash, productTablePrefix),
					e);
		}
		return new ProductivityTimeline(productType, serializedProduct, period, ImmutableList.of());
	}

	public String getSerializedProductFromHash(int productHash) {
		try {
			Statement stmt = database.createStatement();
		//@formatter:off
		String query = String.format("SELECT serialized_product "
				+ "FROM %1$s_product "
				+ "WHERE product_hash = %2$d ", productTablePrefix, productHash);	
		//@formatter:on		
			ResultSet sqlData = stmt.executeQuery(query);
			return sqlData.getString(1);
		} catch (Exception e) {
			StaticCore.LOGGER.error(
					String.format("An error occured when attempting to get the serialized product for product hash: %1$d on table: %2$s.", productHash, productTablePrefix), e);
		}
		return "";
	}

	public ProductionMetrics getProductionMetrics(MetricPeriod period) {
		if (this.isClientSide) {
			return clientMetrics;
		} else {
			List<ProductionMetric> inputs = getAverageProductionRate(period, MetricType.CONSUMPTION);
			List<ProductionMetric> outputs = getAverageProductionRate(period, MetricType.PRODUCTION);
			return new ProductionMetrics(inputs, outputs);
		}
	}

	public void setClientSyncedMetrics(ProductionMetrics metrics, long syncTime) {
		clientMetrics = metrics;
		lastClientSyncTime = syncTime;
	}

	public boolean haveClientValuesUpdatedSince(long lastCheckTime) {
		return lastClientSyncTime > lastCheckTime;
	}

	public void initializeDatabase(Connection database) {
		if (isClientSide) {
			throw new RuntimeException("The database can only be initialized on the server!");
		}

		this.database = database;
		try {
			Statement stmt = database.createStatement();
			stmt.addBatch(createProductivityTable(MetricPeriod.SECOND));
			stmt.addBatch(createProductivityTable(MetricPeriod.MINUTE));
			stmt.addBatch(createProductivityTable(MetricPeriod.HOUR));
			stmt.addBatch(createProductivityTable(MetricPeriod.DAY));
			stmt.executeBatch();
		} catch (SQLException e) {
			StaticCore.LOGGER.error(String.format("An error occured when creating the productivity tracking tables!"), e);
		}
		createProductLookupTable();
	}

	public void insertProductivityPerSecond(Connection database, int bucketIndex, long gameTime) {
		if (isClientSide) {
			throw new RuntimeException("Productivity data can only be inserted on the server!");
		}

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
						 productType.getProductHashCode(entry.getProduct()), 
						metric.consumed(),
						metric.produced(),
						gameTime);
				//@formatter:on
				stmt.addBatch(upsert);
				toBeInserted.add(entry);
			}

			if (!toBeInserted.isEmpty()) {
				StaticCore.LOGGER.trace(String.format("Inserting %1$d entries for bucket: %2$d for product: %3$s.", toBeInserted.size(), bucketIndex, productTablePrefix));
				stmt.executeBatch();

				// Only if we made it this far should we clear the entires.
				for (ProductionEntry<T> entry : toBeInserted) {
					entry.clearCurrentSecondMetrics();
				}
			}

		} catch (Exception e) {
			StaticCore.LOGGER.error(String.format("An error occured when inserting the per second productivity for product: %1$s.", productTablePrefix), e);
		}
	}

	public void updateAggregateData(Connection database, MetricPeriod fromPeriod, MetricPeriod toPeriod, long gameTime) {
		if (isClientSide) {
			throw new RuntimeException("Aggregate data can only be updated on the server!");
		}

		StaticCore.LOGGER.trace(String.format("Updating aggregate data for product type: %1$s from period: %2$s to period: %3$s.", productTablePrefix, fromPeriod.getTableKey(),
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
		} catch (Exception e) {
			StaticCore.LOGGER.error(String.format("An error occured when aggregating the productivity for product: %1$s from period: %2$s to period: %3$s.", productTablePrefix,
					fromPeriod.getTableKey(), toPeriod.getTableKey()), e);
		}
	}

	public void clearOldEntries(Connection database, MetricPeriod period, long gameTime) {
		if (period.getMaxRecordsAgeTicks() == -1) {
			return;
		}

		StaticCore.LOGGER.trace(String.format("Deleting old data for product: %1$s over period: %2$s.", productTablePrefix, period.getTableKey()));
		try {
			//@formatter:off
			String upsert = String.format("DELETE FROM %1$s_productivity_%2$s \n"
					+ " WHERE game_tick < %3$d;",
					productTablePrefix, period.getTableKey(),  gameTime - period.getMaxRecordsAgeTicks());
			//@formatter:on

			Statement stmt = database.createStatement();
			stmt.execute(upsert);
		} catch (Exception e) {
			StaticCore.LOGGER
					.error(String.format("An error occured when clearing old data for product: %1$s over period: %2$s.", productTablePrefix, period.getMaxRecordsAgeTicks()), e);
		}
	}

	private List<ProductionMetric> getAverageProductionRate(MetricPeriod period, MetricType direction) {
		List<ProductionMetric> metrics = new LinkedList<ProductionMetric>();

		// Pull the production rates from memory into the metrics result.
		for (Map<Integer, ProductionEntry<T>> bucket : productivityBuckets) {
			for (ProductionEntry<T> entry : bucket.values()) {
				ProductivityRate consumption = new ProductivityRate(entry.getConsumptionRate().getCurrentValue() * period.getPeriodLengthInSeconds(),
						entry.getConsumptionRate().getIdealValue() * period.getPeriodLengthInSeconds());
				ProductivityRate production = new ProductivityRate(entry.getProductionRate().getCurrentValue() * period.getPeriodLengthInSeconds(),
						entry.getProductionRate().getIdealValue() * period.getPeriodLengthInSeconds());
				metrics.add(
						new ProductionMetric(productType.getProductHashCode(entry.getProduct()), productType.getSerializedProduct(entry.getProduct()), consumption, production));
			}
		}

		// Sort such that the highest rates go to the top.
		if (direction == MetricType.PRODUCTION) {
			metrics.sort((m1, m2) -> Double.compare(m2.getProduced().getCurrentValue(), m1.getProduced().getCurrentValue()));
		} else {
			metrics.sort((m1, m2) -> Double.compare(m2.getConsumed().getCurrentValue(), m1.getConsumed().getCurrentValue()));
		}

		return metrics;
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
		int productHashCode = productType.getProductHashCode(entry.getProduct());

		if (containsProduct(productHashCode)) {
			throw new RuntimeException("Attempted to insert a product entry for a product we're already tracking!");
		}
		productivityBucketMap.put(productHashCode, bucketRoundRobinIndex);
		Map<Integer, ProductionEntry<T>> bucket = productivityBuckets.get(bucketRoundRobinIndex);
		bucket.put(productHashCode, entry);
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
		} catch (Exception e) {
			StaticCore.LOGGER.error(String.format("An error occured when creating the lookup table for product type: $1$s!", productTablePrefix), e);
		}
	}

	protected void insertNewProduct(ProductionEntry<T> entry) {
		//@formatter:off
			String insert = String.format("REPLACE INTO %1$s_product(product_hash, serialized_product) \n"
					+ "  VALUES('%2$d', '%3$s');",
					productTablePrefix,productType.getProductHashCode(entry.getProduct()), productType.getSerializedProduct(entry.getProduct()));
			//@formatter:on
		try {
			Statement stmt = getDatabase().createStatement();
			stmt.execute(insert);
		} catch (Exception e) {
			StaticCore.LOGGER.error(String.format("An error occured when inserting a new product entry for product: $1$s!", productType.getSerializedProduct(entry.getProduct())),
					e);
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
